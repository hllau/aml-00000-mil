package com.cx.ng;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory;

import javax.naming.*;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicTomcatDataSourceFactory extends BasicDataSourceFactory implements ObjectFactory {

    private static final Pattern _propRefPattern = Pattern.compile("\\$\\{.*?\\}");

    //http://tomcat.apache.org/tomcat-6.0-doc/jndi-resources-howto.html#Adding_Custom_Resource_Factories
    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception {
        if (obj instanceof Reference) {
            Reference ref = (Reference) obj;
            System.out.println("Resolving context reference values dynamically");

            for(int i = 0; i < ref.size(); i++) {
                RefAddr addr = ref.get(i);
                String tag = addr.getType();
                String value = (String) addr.getContent();
//                System.out.println("found : "+value);
                String proccessedValue = replaceByPattern(_propRefPattern, value, a->resolve(a));
                ref.remove(i);
                ref.add(i, new StringRefAddr(tag, proccessedValue));

            }
        }
        // Return the customized instance
        return super.getObjectInstance(obj, name, nameCtx, environment);
    }

    private String resolve(String value) {
        String[] splitted = value.split("\\?\\?");
        String result = System.getenv(splitted[0]);
        if(result == null){
            System.out.println("missing env variable:" + splitted[0]);
            if(splitted.length>1){
                result = splitted[1];
                System.out.println("using default value:" + result);
            }else{
                throw new RuntimeException("unable to resolve variable:" + value);
            }
        }
        return result;
    }

    private String replaceByPattern(Pattern _propRefPattern, String original, Function<String, String> resolver){

        StringBuffer result= new StringBuffer();
        //match params in optional part
        Matcher paramList = _propRefPattern.matcher(original);

        int j=0;
        while (paramList.find(j)){

            String found = paramList.group();
            System.out.println("found : "+found);
            String param = found.substring(2, found.length()-1);
            String envValue = resolver.apply(param);
            if(envValue == null){
                throw new RuntimeException("unknown variable:"+found);
            }
            result.append(original.substring(j, paramList.start()));
            result.append(envValue);

            j= paramList.start()+found.length();
        }
        if (j < original.length()) {
            result.append(original.substring(j));
        }
        return result.toString();
    }
}
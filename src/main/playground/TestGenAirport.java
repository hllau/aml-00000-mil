import org.junit.Test;

import java.io.*;

/**
 * Created by cheng on 17/8/2016.
 */
public class TestGenAirport {
    @Test
    public void genScript() throws IOException {
String filePath="C:\\workspace\\project\\cx\\snm-aml_aml_b2c-pilot_asia-mile-service\\src\\main\\webapp\\am\\System\\application_files\\script\\reactjs\\airport_src.js";
String filePath2="C:\\workspace\\project\\cx\\snm-aml_aml_b2c-pilot_asia-mile-service\\src\\main\\webapp\\am\\System\\application_files\\script\\reactjs\\airport.js";

        try(BufferedReader reader = new BufferedReader(new FileReader(filePath));
            FileWriter writer = new FileWriter(filePath2);
        ){
            String line;
            writer.write("var airportOptionList = [");
            while ((line = reader.readLine()) != null){
                if(line.contains("\"")){
                    String word = line.trim().substring(1,line.trim().lastIndexOf('"'));

                    String id = word.substring(word.lastIndexOf('(')+1, word.lastIndexOf(')'));
                    writer.write(String.format("{value:\"%s\", label:\"%s\"},\n", id, word));
                }
            }
            writer.write("]");

        }

    }
}

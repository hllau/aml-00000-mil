function removeItem(arr, item){
    for(var i = arr.length; i--;) {
        if(arr[i] === item) {
            arr.splice(i, 1);
            break;
        }
    }
}
function arrayUnion(arr1, arr2, equalityFunc) {
    var equalityFunc = equalityFunc;
    var union = arr1.concat(arr2);
    for (var i = 0; i < union.length; i++) {
        for (var j = i+1; j < union.length; j++) {
            if (equalityFunc(union[i], union[j])) {
                union.splice(j, 1);
                j--;
            }
        }
    }

    return union;
}

/**
 * Number.prototype.format(n, x, s, c)
 *
 * @param integer n: length of decimal
 * @param integer x: length of whole part
 * @param mixed   s: sections delimiter
 * @param mixed   c: decimal delimiter
 */
function formateNum(value) {
    var temp = value.toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1,")
    return temp.substring(0, temp.length-3)
};


function humanFileSize(size) {
    var i = Math.floor( Math.log(size) / Math.log(1024) );
    return ( size / Math.pow(1024, i) ).toFixed(2) * 1 + ' ' + ['B', 'kB', 'MB', 'GB', 'TB'][i];
};

function byString(o, s) {
    s = s.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
    s = s.replace(/^\./, '');           // strip a leading dot
    var a = s.split('.');
    for (var i = 0, n = a.length; i < n; ++i) {
        var k = a[i];
        if (k in o) {
            o = o[k];
        } else {
            return;
        }
    }
    return o;
}
function printTemplate(context, tpl){
    for(p in context)
        eval ("var "+p+"=context."+p);
    var result = tpl.replace(/{[^}]*}/g, function(key) {
        var t = key.substring(1,key.length-1)
        try{
            var value = eval(t)
            //add custom formatter here
            if(key=="{contType}"){
                var selected = todoStore.contTypeList.find(function(t){return t.value == value});
                value = (selected == null?null:selected.label);
            }
            if(value instanceof moment){
                value = value.format("DD/MM/YYYY")
            }
            //default behaviour
            return value==null ? key : value
        }catch(ex){
            return key
        }
    });
    return result ;
}
function buildAjaxRequest2(url, callBack) {
    return {
        type: 'POST',
        url: url,
        success: callBack,
        error: function(xhr, status, err) {
            alert("failed"+err.toString());
            console.error(url, status, err.toString());
        }}
}
function buildAjaxRequest(url, data, callBack) {
    return {
        contentType : 'application/json',
        type: 'POST',
        url: url,
        data: JSON.stringify(data),
        success: callBack,
        error: function(xhr, status, err) {
            alert("failed"+err.toString());
            console.error(url, status, err.toString());
        }}
}
function calcOverdueDay(d){
    if(d==null) return null;
    var diff = d==null?null:(new moment(new Date()).diff(d, 'days'))
    if(diff<= 0){
        return null;
    }
    return diff;
}
function convertToMoment(o){
    var args = Array.prototype.slice.call(arguments, 1);
    args.forEach(function (path){
        if(o[path]){
            o[path] = new moment(o[path]);
        }
    })

}
function toDateString(d){
    if(d == null) return '';
    if(d instanceof moment)
        return d.format('DD/MM/YYYY');
    else
        return new moment(d).format('DD/MM/YYYY');
}
function sortByString(arr, field, desc){
    sortBy(arr, field, "string", desc)
}

function sortBy(arr, field, fieldType, desc){
    //fieldType options:string
    if(arr == null || arr.length == 0) return;

    if(fieldType == "string"){
        arr.sort(function(a,b){
            var x = a[field].toLowerCase();
            var y = b[field].toLowerCase();
            if(desc =="desc"){
                return y< x ? -1 : y > x ? 1 : 0;
            }else{
                return x < y ? -1 : x > y ? 1 : 0;
            }

        })
    }else {
        arr.sort(function(a,b){
            if(desc =="desc") {
                return b[field] - a[field];
            }else{
                return a[field] - b[field];

            }
        })
    }

}
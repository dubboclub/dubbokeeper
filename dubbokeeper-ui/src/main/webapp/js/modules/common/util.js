window.queryString2Object= function (queryString) {
    var obj={};
    var items=queryString.split("&");
    for(var i=0;i<items.length;i++){
        var item=items[i];
        var pair=item.split("=");
        obj[pair[0]]=pair[1];
    }
    return obj;
};
window.object2QueryString=function(obj){
    var queryStr="";
    for(var key in obj){
        queryStr+=key+"="+obj[key]+"&";
    }
    queryStr=queryStr.substring(0,queryStr.length-1);
    return encodeURIComponent(queryStr);
}
window.urlEncode=function(url){
    return encodeURI(url)
}
window.ajaxResultStatu={};
window.ajaxResultStatu.SUCCESS=0;
window.ajaxResultStatu.FAILED=1;
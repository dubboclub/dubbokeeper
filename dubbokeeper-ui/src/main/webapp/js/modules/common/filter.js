var queryFilter=angular.module("queryFilter",[]);
queryFilter.$queryFilter=function(){
    this.filterMethod=function(originData,query){
        var filterResult=[];
        for(var i=0;i<originData.length;i++){
            var matched=true;
            for(var key in query){
                if(query[key]===null||query[key]===undefined||query[key]==="*"||query[key]===""||query[key]==="all"){
                    continue;
                }
                var itemValue=originData[i][key]+"";
                var queryData = query[key]+"";
                if(!itemValue){
                    matched=false;
                    break;
                }
                queryData=queryData.toUpperCase();
                itemValue=itemValue.toUpperCase();
                if(itemValue.indexOf(queryData)<0){
                    matched=false;
                    break;
                }
            }
            if(matched){
                filterResult.push(originData[i]);
            }
        }
        return filterResult;
    };
    this.$get = function () {
        return this.filterMethod;
    };
}

queryFilter.provider("$queryFilter",queryFilter.$queryFilter);
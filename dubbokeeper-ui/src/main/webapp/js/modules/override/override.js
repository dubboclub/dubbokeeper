var override = angular.module('override',['ngAnimate','ngRoute','serviceProvider','queryFilter','breadCrumb','dialog','dubbokeeperFilters']);

override.config(function($routeProvider){
    $routeProvider.when("/override/:serviceKey/list",{
        templateUrl:"templates/override/provider-overrides.html",
        controller:"providerOverrides"
    }).when("/override/:serviceKey/add",{
        templateUrl:"templates/override/edit-override.html",
        controller:"editOverride"
    }).when("/override/edit/:id/:serviceKey",{
        templateUrl:"templates/override/edit-override.html",
        controller:"editOverride"
    }).otherwise("/");

});

override.controller('editOverride',function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu,$dialog){
    $menu.switchMenu('dynamicConfig');
    $breadcrumb.pushCrumb('为'+decodeURIComponent($routeParams.serviceKey)+"新建动态配置",'为'+decodeURIComponent($routeParams.serviceKey)+"新建动态配置","addOverride");
    $scope.serviceKey=decodeURIComponent($routeParams.serviceKey);
    $scope.item={};
    $scope.item.params=[];
    $scope.item.params.push({key:"",value:""});
    $scope.item.methodParams=[];
    $scope.item.methodParams.push({method:"",key:"",value:""});
    $scope.item.serviceKey=$scope.serviceKey;
    $scope.item.enable="false";
    $scope.item.mock={};
    $scope.item.mock.type="fail";
    $scope.item.methodMocks=[];
    $httpWrapper.post({
        url:"/override/provider/"+encodeURIComponent(encodeURIComponent($scope.serviceKey))+"/methods.htm",
        success:function(data){
            $scope.methods=data;
            if(!$scope.item.methodParams[0].method){
                $scope.item.methodParams[0].method=data[0];
            }
            if($routeParams.id){
                $scope.id=$routeParams.id;
                $httpWrapper.post({
                    url:"override/"+$scope.id+"/detail.htm",
                    success:function(data){
                        $scope.serviceKey=data.serviceKey;
                        var item={};
                        item.application=data.application;
                        item.serviceKey = data.serviceKey;
                        item.address=data.address;
                        item.enable=data.enable+"";
                        var paramsObj = queryString2Object(data.parameters);
                        var params=[];
                        var methodParams=[];
                        var mock={};
                        var methodMocks=[];
                        var hadMock =false;
                        for(var key in paramsObj){
                            if(key.indexOf(".")>0){
                                var splits = key.split(".");
                                if(splits.length==2){
                                    var method = splits[0];
                                    var keyName = splits[1];
                                    if("mock"==keyName){
                                        var mockVal = decodeURIComponent(paramsObj[key]);
                                        var itemMock={};
                                        if(mockVal.indexOf("fail")==0){
                                            itemMock.type="fail";
                                            itemMock.value= mockVal.substring(5);
                                        }else if(mockVal.index("force")==0){
                                            itemMock.type="force";
                                            itemMock.value= mockVal.substring(6);
                                        }
                                        itemMock.method=method;
                                        methodMocks.push(itemMock);
                                    }else{
                                        methodParams.push({method:method,key:keyName,value:decodeURIComponent(paramsObj[key])});
                                    }
                                }
                            }else{
                                if("mock"==key){
                                    var mockVal = decodeURIComponent(paramsObj[key]);
                                    if(mockVal.indexOf("fail")==0){
                                        mock.type="fail";
                                        mock.value=mockVal.substring(5);
                                    }else if(mockVal.index("force")==0){
                                        mock.type="force";
                                        mock.value=mockVal.substring(6);
                                    }
                                    hadMock=true;
                                }else{
                                    params.push({key:key,value:decodeURIComponent(paramsObj[key])})
                                }
                            }
                        }
                        if(methodParams.length>0){
                            item.methodParams=methodParams;
                        }else{
                            item.methodParams=$scope.item.methodParams;
                        }
                        if(methodMocks.length>0){
                            item.methodMocks = methodMocks;
                        }else{
                            item.methodMocks=$scope.item.methodMocks;
                        }
                        if(params.length>0){
                            item.params=params;
                        }else{
                            item.params=$scope.item.params;
                        }
                        if(hadMock){
                            item.mock=mock;
                        }else{
                            item.mock=$scope.item.mock;
                        }
                        $scope.item=item;

                    }
                });
            }
        }
    });



    $scope.addParam=function(){
        $scope.item.params.push({key:"",value:""});
    }
    $scope.addMethodParam=function(){
        $scope.item.methodParams.push({method:$scope.methods[0],key:"",value:""});
    }
    $scope.removeParam=function(index){
        var newParams=[];
        newParams=newParams.concat($scope.item.params.slice(0,index));
        newParams=newParams.concat($scope.item.params.slice(index+1));
        $scope.item.params=newParams;
    }
    $scope.removeMethodParam=function(index){
        var newParams=[];
        newParams=newParams.concat($scope.item.methodParams.slice(0,index));
        newParams=newParams.concat($scope.item.methodParams.slice(index+1));
        $scope.item.methodParams=newParams;
    }
    $scope.removeMethodMock=function(index){
        var newParams=[];
        newParams=newParams.concat($scope.item.methodMocks.slice(0,index));
        newParams=newParams.concat($scope.item.methodMocks.slice(index+1));
        $scope.item.methodMocks=newParams;
    }
    $scope.addMethodMock=function(){
        $scope.item.methodMocks.push({type:'fail',method:$scope.methods[0],value:"return empty"})
    }

    $scope.save=function(){
        var params = generateParams();
        if(params==""){
            $dialog.alert({content:"请输入配置的参数！"});
            return ;
        }
        var item={};
        item.id=$routeParams.id;
        item.parameters=params;
        item.application=$scope.item.application;
        item.enable=$scope.item.enable;
        item.address=$scope.item.address;
        $httpWrapper.post({
            url:"override/provider/"+encodeURIComponent(encodeURIComponent($scope.serviceKey))+"/saveOverride.htm",
            data:item,
            success:function(data){
                if(data.result==ajaxResultStatu.SUCCESS){
                    $dialog.info({
                        content:"配置保存成功！"
                    });
                }
            }
        });
    }
    var generateParams=function(){
        var item = $scope.item;
        //提取接口参数
        var params=item.params;
        var paramContent="";
        for(var i=0;i<params.length;i++){
            if(params[i].value&&params[i].value!=""&&params[i].key&&params[i].key!=""){
                paramContent+=params[i].key+"="+encodeURIComponent(params[i].value)+"&";
            }
        }

        var methodParams = item.methodParams;
        for(var i=0;i<methodParams.length;i++){
            var param = methodParams[i];
            if(param.value&&param.value!=""&&param.key&&param.key!=""){
                paramContent+=param.method+"."+param.key+"="+encodeURIComponent(param.value)+"&";
            }
        }

        var mock = item.mock;
        if(mock.value&&mock.value!=''){
            paramContent+="mock="+encodeURIComponent(mock.type+":"+mock.value)+"&";
        }
        var methodMocks = item.methodMocks;
        for(var i=0;i<methodMocks.length;i++){
            var methodMock = methodMocks[i];
            if(methodMock.value&&methodMock.value!=''){
                paramContent+=methodMock.method+".mock="+encodeURIComponent(methodMock.type+":"+methodMock.value)+"&";
            }
        }
        if(paramContent.length>0){
            paramContent=paramContent.substring(0,paramContent.length-1);
        }
        return paramContent;
    }
});


override.controller('providerOverrides',function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu,$dialog){
    $menu.switchMenu('dynamicConfig');
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.service=decodeURIComponent($routeParams.serviceKey);
    $scope.serviceKey=encodeURI($routeParams.serviceKey);
    $scope.enabledOptios=[{
        val:true,
        text:"已启用"
    },{
        val:false,
        text:"已禁用"
    }];
    $scope.loadBalanceStrategies=[{
        val:'random',
        text:"随机"
    },{
        val:'roundrobin',
        text:"轮询"
    },{
        val:'leastactive',
        text:"最少并发"
    }];
    var currentQuery=undefined;
    var queryConfig=function(){
        $httpWrapper.post({
            url:"override/provider/"+encodeURI($routeParams.serviceKey)+"/list.htm",
            success:function(data){
                $scope.details=data;
                if(!data||data.length<=0){
                    $scope.isEmpty=true;
                }
                $scope.originData=data;
            }
        });
    }
    var queryWeightConfig = function(){
        $httpWrapper.post({
            url:"override/provider/"+encodeURIComponent($routeParams.serviceKey)+"/weight-list.htm",
            success:function(data){
                $scope.details=data;
                if(!data||data.length<=0){
                    $scope.isEmpty=true;
                }
                $scope.originData=data;
            }
        });
    }
    var queryLoadBalance=function(){
        $httpWrapper.post({
            url:"override/provider/"+encodeURIComponent($routeParams.serviceKey)+"/loadbalance-list.htm",
            success:function(data){
                $scope.details=data;
                if(!data||data.length<=0){
                    $scope.isEmpty=true;
                }
                $scope.originData=data;
            }
        });
    }
    $breadcrumb.pushCrumb('服务'+decodeURIComponent($routeParams.serviceKey)+"动态配置",'服务'+decodeURIComponent($routeParams.serviceKey)+"动态配置","providerOverrides");
    $scope.switchTab=function(tabName){
        $scope.currentTab=tabName;
        $scope.details=[];
        switch (tabName){
            case 'dynamicConfig':{
                currentQuery=queryConfig;
                break;
            }
            case 'weightConfig':{
                currentQuery=queryWeightConfig;
                break;
            }
            case 'loadBalance':{
                currentQuery=queryLoadBalance
                break;
            }
        }
        currentQuery();
    }

    $scope.select=function(){
        if($scope.selectAll){
            for(var i=0;i<$scope.details.length;i++){
                $scope.details[i].checked=true;
            }
        }else{
            for(var i=0;i<$scope.details.length;i++){
                $scope.details[i].checked=false;
            }
        }
    }
    $scope.operation=function(type,item){
        $dialog.confirm({
            content:"确认进行该操作？",
            callback:function(){
                $httpWrapper.post({
                    url:"/override/"+item.id+"/"+type+".htm",
                    success:function(data){
                        if(data.result==ajaxResultStatu.SUCCESS){
                            $dialog.info({
                                content:"成功更新该配置！",
                                callback:function(){
                                    currentQuery();
                                }
                            });

                        }
                    }
                });
            }
        })
    }
    $scope.batchOperation=function(type){
        var details =  $scope.details;
        var selected=[];
        for(var i=0;i<details.length;i++){
            if(details[i].checked){
                selected.push(details[i].id);
            }
        }
        if(selected.length>0){
            $dialog.confirm({
                content:"确定进行此操作？",
                callback:function(){
                    $httpWrapper.post({
                        url:"/override/"+selected.join(",")+"/batch/"+type+".htm" ,
                        success:function(data){
                            if(data.result==ajaxResultStatu.SUCCESS){
                                $dialog.info({
                                    content:"成功更新选中配置！",
                                    callback:function(){
                                        currentQuery();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }else{
            $dialog.alert({
                content:"请选择操作项！"
            });
        }

    }
    $scope.switchTab('dynamicConfig');
    $scope.query={};
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    }
});


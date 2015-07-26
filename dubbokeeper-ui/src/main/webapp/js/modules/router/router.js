var router = angular.module('router',['ngAnimate','ngRoute','queryFilter','breadCrumb','dialog','dubbokeeperFilters']);

router.config(function($routeProvider){
    $routeProvider.when("/admin/router/:serviceKey/list",{
        templateUrl:"templates/router/provider-routes.html",
        controller:"providerRoutes"
    }).when("/admin/router/:serviceKey/condition/add",{
        templateUrl:"templates/router/condition-edit.html",
        controller:"conditionEdit"
    }).when("/admin/router/:serviceKey/script/add",{
        templateUrl:"templates/router/script-add.html",
        controller:"scriptAdd"
    }).when("/admin/router/edit/condition/:serviceKey/:id",{
        templateUrl:"templates/router/condition-edit.html",
        controller:"conditionEdit"
    }).otherwise("/statistics");
});


router.controller('conditionEdit',function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu,$dialog){
    $menu.switchMenu('admin/routeConfig');
    $breadcrumb.pushCrumb('对服务'+decodeURIComponent($routeParams.serviceKey)+"新增路由规则",'对服务'+decodeURIComponent($routeParams.serviceKey)+"新增路由规则","routeConditionAdd");
    $scope.service=decodeURIComponent($routeParams.serviceKey);
    $scope.serviceKey=encodeURI($routeParams.serviceKey);
    $scope.id=$routeParams.id;
    $scope.item={};
    $scope.whenList=[];
    $scope.thenList=[];
    $httpWrapper.post({
        url:"override/provider/"+encodeURIComponent($routeParams.serviceKey)+"/methods.htm",
        success:function(data){
            $scope.methods=data;
        }
    });
    if($scope.id){
        $httpWrapper.post({
            url:"route/get_"+$scope.id+".htm",
            success: function (data) {
                $scope.item=data;
                if(data.matchRule){
                    $scope.whenList = parseRule(data.matchRule);
                    for(var i=0;i<$scope.whenList.length;i++){
                        var when = $scope.whenList[i];
                        if(when.condition=="consumer.methods"){
                            $scope.item.method=when.value;
                            $scope.removeWhen(i);
                            break;
                        }
                    }
                }
                if(data.filterRule){
                    $scope.thenList = parseRule(data.filterRule);
                }
            }
        });
    }
    var parseRule=function(rules){
        rules = rules.trim();
        rules = rules.split("&");
        var ruleArray = [];
        for(var i=0;i<rules.length;i++){
            var rule = rules[i];
            var ruleObj={};
            var index=0;
            if((index=rule.indexOf("!="))>0){
                ruleObj.condition=rule.substring(0,index).trim();
                ruleObj.rule="!=";
                ruleObj.value=rule.substring(index+2);
            }else{
                index=rule.indexOf("=");
                ruleObj.condition=rule.substring(0,index).trim();
                ruleObj.rule="=";
                ruleObj.value=rule.substring(index+1);
            }
            ruleArray.push(ruleObj);
        }
        return ruleArray;
    }
    $scope.whenConditions=[{
        val:'consumer.host',
        text:"消费者IP地址"
    },{
        val:'consumer.application',
        text:"消费者应用名"
    },{
        val:'consumer.version',
        text:"消费者版本"
    },{
        val:'consumer.cluster',
        text:'消费者集群'
    }];
    $scope.rules=[{
        val:'=',
        text:"匹配"
    },{
        val:'!=',
        text:"不匹配"
    }];
    var ipPattern = "^(([1-9]{1}[0-9]{0,2}[\\.]{1}){3}(([1-9]{1}[0-9]{0,2})|[\\*]{1}){1}){1}([,]{1}(([1-9]{1}[0-9]{0,2}[\\.]{1}){3}(([1-9]{1}[0-9]{0,2})|[\\*]{1}){1}){1}){0,}$";
    var textPattern = "^[a-zA-Z0-9-_]{1,}$";
    var numPattern = "^[1-9]{1}[0-9]{0,}$";
    var whenConditions={
        "consumer.host":{
            title:'输的地址可以是ip段(192.168.0.*)也可以可是具体ip地址，多个通过逗号分隔开',
            pattern:ipPattern
        },
        "consumer.application":{
            title:"请输入消费端app名称",
            pattern:textPattern
        },
        "consumer.version":{
            title:"请输入消费端版本",
            pattern:textPattern
        },
        "consumer.cluster":{
            title:"请输入消费端的集群名",
            pattern:textPattern
        }
    };
    $scope.thenConditions=[{
        val:'provider.host',
        text:"提供者IP地址"
    },{
        val:'provider.cluster',
        text:"提供者集群"
    },{
        val:'provider.protocol',
        text:"提供者协议"
    },{
        val:'provider.version',
        text:"提供者版本号"
    },{
        val:'provider.port',
        text:"提供者端口"
    }];
    var thenConditions={
        "provider.host":{
            title:'输的地址可以是ip段(192.168.0.*)也可以可是具体ip地址，多个通过逗号分隔开',
            pattern:ipPattern
        },
        "provider.application":{
            title:"请输入提供者的app名称",
            pattern:textPattern
        },
        "provider.protocol":{
            title:"请输入提供者的协议",
            pattern:textPattern
        },
        "provider.port":{
            title:"提供者端口必须是整型",
            pattern:numPattern
        },
        "provider.version":{
            title:"请输入提供者的版本",
            pattern:textPattern
        }
    };
    $scope.save=function(){
        var filterRule ="";
        if($scope.whenList.length>0){
            for(var i=0;i<$scope.whenList.length;i++){
                var when = $scope.whenList[i];
                var condition = whenConditions[when.condition];
                var reg = new RegExp(condition.pattern);
                if(!reg.test(when.value)){
                    $dialog.alert({content:condition.title, size:"small"});
                    return ;
                }
                filterRule+=when.condition+when.rule+when.value+"&"
            }
        }
        if($scope.item.method){
            filterRule+="consumer.methods="+$scope.item.method;
            delete $scope.item.method;
        }else if(filterRule.length>0){
            filterRule=filterRule.substring(0,filterRule.length-1);
        }
        var matchRule="";
        if($scope.thenList.length>0){
            for(var i=0;i<$scope.thenList.length;i++){
                var then = $scope.thenList[i];
                var condition = thenConditions[then.condition];
                var reg = new RegExp(condition.pattern);
                if(!reg.test(when.value)){
                    $dialog.alert({content:condition.title, size:"small"});
                    return ;
                }
                matchRule+=then.condition+then.rule+then.value+"&";
            }
        }
        if(matchRule.length>0){
            matchRule=matchRule.substring(0,matchRule.length-1);
        }
        $scope.item.matchRule=matchRule;
        $scope.item.filterRule=filterRule;
        $scope.item.service=$scope.service;
        $scope.item.type="condition";
        $httpWrapper.post({
            url:"route/create.htm",
            data:$scope.item,
            success: function (data) {
                if(data.result==ajaxResultStatu.SUCCESS){
                    $dialog.info({
                        content:"成功编辑路由规则！",
                        size:"small"
                    });
                }
            }
        });
    }
    $scope.selectMethod=function(){
        if(!$scope.item.method){
            if($scope.selectedMethod&&$scope.selectedMethod!=""){
                $scope.item.method=$scope.selectedMethod;
            }
        }else{
            if($scope.selectedMethod&&$scope.selectedMethod!=""){
                $scope.item.method=$scope.item.method+","+$scope.selectedMethod;
            }
        }


    }
    $scope.switchTab=function(tabName){
        $scope.currentTab=tabName;
    }

    $scope.switchTab('when');
    $scope.addWhen= function () {
        $scope.whenList.push({condition:'consumer.host',rule:'=',value:''});
    };
    $scope.addThen= function () {
        $scope.thenList.push({condition:'provider.host',rule:'=',value:''});
    };

    $scope.removeWhen=function(index){
        var newArray=[];
        newArray=newArray.concat($scope.whenList.slice(0,index));
        newArray=newArray.concat($scope.whenList.slice(index+1));
        $scope.whenList=newArray;
    }
    $scope.removeThen=function(index){
        var newArray=[];
        newArray=newArray.concat($scope.thenList.slice(0,index));
        newArray=newArray.concat($scope.thenList.slice(index+1));
        $scope.thenList=newArray;
    }
});

router.controller('providerRoutes', function ($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu) {
    $menu.switchMenu('admin/routeConfig');
    $breadcrumb.pushCrumb('服务'+decodeURIComponent($routeParams.serviceKey)+"路由配置列表",'服务'+decodeURIComponent($routeParams.serviceKey)+"路由配置列表","providerRoutes");
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
    $scope.routerTypes=[{
        val:'condition',
        text:"条件规则"
    },{
        val:'script',
        text:"脚本规则"
    }];
    $httpWrapper.post({
        url:"route/provider/"+encodeURIComponent($routeParams.serviceKey)+"/list.htm",
        success:function(data){
            $scope.details=data;
            if(!data||data.length<=0){
                $scope.isEmpty=true;
            }
            $scope.originData=data;
        }
    });
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
    $scope.query={};
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    }
});
var router = angular.module('router',['ngAnimate','ngRoute','queryFilter','breadCrumb','dialog','dubbokeeperFilters']);

router.config(function($routeProvider){
    $routeProvider.when("/admin/router/:serviceKey/list",{
        templateUrl:"templates/router/provider-routes.html",
        controller:"providerRoutes"
    }).when("/admin/router/:serviceKey/condition/add",{
        templateUrl:"templates/router/condition-add.html",
        controller:"conditionAdd"
    }).when("/admin/router/:serviceKey/script/add",{
        templateUrl:"templates/router/script-add.html",
        controller:"scriptAdd"
    }).otherwise("/statistics");
});


router.controller('conditionAdd',function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu){
    $menu.switchMenu('admin/routeConfig');
    $breadcrumb.pushCrumb('对服务'+decodeURIComponent($routeParams.serviceKey)+"新增路由规则",'对服务'+decodeURIComponent($routeParams.serviceKey)+"新增路由规则","routeConditionAdd");
    $scope.service=decodeURIComponent($routeParams.serviceKey);
    $scope.serviceKey=encodeURI($routeParams.serviceKey);
    $scope.item={};
    $httpWrapper.post({
        url:"override/provider/"+encodeURIComponent($routeParams.serviceKey)+"/methods.htm",
        success:function(data){
            $scope.methods=data;
        }
    });
    $scope.whenConditions=[{
        val:'consumer-ip',
        text:"消费者IP地址"
    },{
        val:'consumer-app',
        text:"消费者应用名"
    },{
        val:'consumer-app',
        text:"消费者应用名"
    },{
        val:'consumer-cluster',
        text:'消费者集群'
    }];
    $scope.rules=[{
        val:'match',
        text:"匹配"
    },{
        val:'not-match',
        text:"不匹配"
    }];
    $scope.thenConditions=[{
        val:'provider-ip',
        text:"提供者IP地址"
    },{
        val:'provider-cluster',
        text:"提供者集群"
    },{
        val:'provider-protocol',
        text:"提供者协议"
    },{
        val:'provider-port',
        text:"提供者端口"
    }];
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
    $scope.whenList=[];
    $scope.thenList=[];
    $scope.addWhen= function () {
        $scope.whenList.push({condition:'consumer-ip',rule:'match',value:''});
    };
    $scope.addThen= function () {
        $scope.thenList.push({condition:'provider-ip',rule:'match',value:''});
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
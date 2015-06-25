var override = angular.module('override',['ngAnimate','ngRoute','serviceProvider','queryFilter','breadCrumb']);

override.config(function($routeProvider){
    $routeProvider.when("/override/:serviceKey/list",{
        templateUrl:"templates/override/provider-overrides.html",
        controller:"providerOverrides"
    }).otherwise("/");

});

override.controller('providerOverrides',function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu){
    $menu.switchMenu('dynamicConfig');
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.serviceKey=decodeURIComponent($routeParams.serviceKey);
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
    $breadcrumb.pushCrumb('服务'+decodeURIComponent($routeParams.serviceKey)+"动态配置",'服务'+decodeURIComponent($routeParams.serviceKey)+"动态配置","providerOverrides");
    $scope.switchTab=function(tabName){
        $scope.currentTab=tabName;
        $scope.details=[];
        switch (tabName){
            case 'dynamicConfig':{
                $httpWrapper.post({
                    url:"override/provider/"+encodeURIComponent($routeParams.serviceKey)+"/list.htm",
                    success:function(data){
                        $scope.details=data;
                        if(!data||data.length<=0){
                            $scope.isEmpty=true;
                        }
                        $scope.originData=data;
                    }
                });
                break;
            }
            case 'weightConfig':{
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
                break;
            }
            case 'loadBalance':{
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
                break;
            }
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



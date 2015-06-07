var serviceProvider=angular.module("serviceProvider",['ngAnimate','ngRoute','queryFilter']);

serviceProvider.config(function($routeProvider){
    $routeProvider.when("/:service/providers",{
        templateUrl:"templates/apps/service-providers.html",
        controller:"serviceProviders"
    }).otherwise("/");
});


serviceProvider.controller("serviceProviders",function($scope,$http,$routeParams,$queryFilter){
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.service=$routeParams.service;
    $scope.query={};
    $scope.dynamicOptios=[{
        val:true,
        text:"动态"
    },{
        val:false,
        text:"静态"
    }];
    $scope.enabledOptios=[{
        val:true,
        text:"已启用"
    },{
        val:false,
        text:"已禁用"
    }];
    $http.post("provider/"+$routeParams.service+"/providers.htm").success(function(data){
        $scope.details=data;
        if(!data||data.length<=0){
            $scope.isEmpty=true;
        }
        $scope.originData= $scope.details;
    });

    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    }
});
var serviceProvider=angular.module("serviceProvider",['ngAnimate','ngRoute','queryFilter','breadCrumb']);

serviceProvider.config(function($routeProvider){
    $routeProvider.when("/:service/providers",{
        templateUrl:"templates/apps/service-providers.html",
        controller:"serviceProviders"
    }).when("/edit/:service/:address/:id/provider",{
        templateUrl:"templates/apps/edit-provider.html",
        controller:"editProvider"
    }).otherwise("/");
});


serviceProvider.controller("editProvider",function($scope,$http,$routeParams,$breadcrumb){
    $scope.provider={};
    $scope.service=$routeParams.service;
    $breadcrumb.pushCrumb($routeParams.address,"编辑服务"+$routeParams.service+"提供者","editProvider");
    $scope.enabledOptios=[{
        val:true,
        text:"启用"
    },{
        val:false,
        text:"禁用"
    }];
    $http.post("provider/"+$routeParams.id+"/provider-detail.htm").success(function(data){
        $scope.provider=data;
        $scope.parameters=queryString2Object(data.parameters);
        $scope.parameters.enabled=data.enabled;
        $scope.parameters.weight=data.weight;
    });
    $scope.update=function(){
        $http.post("provider/edit-provider.htm","parameters="+object2QueryString($scope.parameters)+"&id="+$routeParams.id,{ headers: { 'Content-Type': 'application/x-www-form-urlencoded'}}).success(function(data){
           console.log(data);
        });
    }

});



serviceProvider.controller("serviceProviders",function($scope,$http,$routeParams,$queryFilter,$breadcrumb){
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
    $breadcrumb.pushCrumb($scope.service,"查看服务"+$scope.service+"提供者列表","serviceProviders");
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
    $scope.select=function(){
        var selectAll = $(".selector").get(0).checked;
        $("input[type='checkbox']").each(function(){
            if(selectAll){
                this.checked=true;
            }else{
                this.checked=false;
            }

        });
    }
});
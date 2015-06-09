var apps=angular.module("apps",['ngAnimate','ngRoute','serviceProvider','queryFilter']);
apps.config(function($routeProvider){
    $routeProvider.when("/",{
        templateUrl:"templates/apps/application-table.html",
        controller:"appTable"
    }).when("/:application/nodes",{
        templateUrl:"templates/apps/node-details.html",
        controller:"nodesDetail"
    }).when("/:application/consumes",{
        templateUrl:"templates/apps/consume-details.html",
        controller:"consumeDetail"
    }).when("/:application/consumers",{
        templateUrl:"templates/apps/consumer-details.html",
        controller:"consumerDetail"
    }).when("/:application/provides",{
        templateUrl:"templates/apps/provide-details.html",
        controller:"provideDetail"
    }).when("/:provider/:consumer/consumes",{
        templateUrl:"templates/apps/provide-details.html",
        controller:"consumeServiceDetails"
    }).when("/:service/:id/service-consumers",{
        templateUrl:"templates/apps/application-table.html",
        controller:"consumerAppTable"
    }).otherwise("/");
});



apps.controller("consumeServiceDetails",function($scope,$http,$routeParams,$queryFilter){
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.provider=$routeParams.provider;
    $scope.consumer=$routeParams.consumer;
    $scope.isForOneConsumer=true;

    $http.post("app/"+$routeParams.provider+"/"+$routeParams.consumer+"/consumes.htm").success(function(data){
        $scope.details=data;
        if(!data||data.length<=0){
            $scope.isEmpty=true;
        }
        $scope.originData=data;
    });
    $scope.currentParameters="";
    $scope.viewParameters=function(detail){
        $scope.currentParameters=detail.parameters;
        //$('.modal-dialog').modal('toggle');
    }

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
    $scope.query={};
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    }
});

apps.controller("consumerDetail",function($scope,$http,$routeParams,$queryFilter){
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.application=$routeParams.application;
    $http.post("app/"+$routeParams.application+"/consumers.htm").success(function(data){
        $scope.details=data;
        if(!data||data.length<=0){
            $scope.isEmpty=true;
        }
        $scope.originData=data;
    });
    $scope.query={};
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    }

});


apps.controller("nodesDetail",function($scope,$http,$routeParams,$queryFilter){
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.application=$routeParams.application;
    $http.post("app/"+$routeParams.application+"/nodes.htm").success(function(data){
        $scope.details=data;
        if(!data||data.length<=0){
            $scope.isEmpty=true;
        }
        $scope.originData=data;
    });
    $scope.typeOptions=[{
        val:'1',
        text:"提供者"
    },{
        val:'2',
        text:"消费者"
    }];
    $scope.query={};
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    }
});
apps.controller("consumeDetail",function($scope,$http,$routeParams,$queryFilter){
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.application=$routeParams.application;
    $http.post("app/"+$routeParams.application+"/consumes.htm").success(function(data){
        $scope.details=data;
        if(!data||data.length<=0){
            $scope.isEmpty=true;
        }
        $scope.originData=data;
    });
    $scope.query={};
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    }
});
apps.controller("provideDetail",function($scope,$http,$routeParams,$queryFilter){
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.application=$routeParams.application;

    $http.post("app/"+$routeParams.application+"/provides.htm").success(function(data){
        $scope.details=data;
        if(!data||data.length<=0){
            $scope.isEmpty=true;
        }
        $scope.originData=data;
    });
    $scope.currentParameters="";
    $scope.viewParameters=function(detail){
        $scope.currentParameters=detail.parameters;
    }

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
    $scope.query={};
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    }
});
apps.controller("consumerAppTable",function($scope,$http,$routeParams,$queryFilter){
    $scope.applications=[];
    $scope.isEmpty=false;
    $scope.isConsumer=true;
    $scope.service=$routeParams.service;
    $http.post("app/"+$routeParams.id+"/consumer-apps.htm").success(function(data){
        $scope.applications=data;
        if(!data||data.length<0){
            $scope.isEmpty=true;
        }
        $scope.originData=data;
    });
    $scope.typeOptions=[{
        val:1,
        text:"P"
    },{
        val:2,
        text:"C"
    },{
        val:3,
        text:"P.AND.C"
    }];
    $scope.query={};
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.applications=$queryFilter($scope.originData,$scope.query);
    }
});

apps.controller("appTable",function($scope,$http,$queryFilter){
    $scope.applications=[];
    $scope.isEmpty=false;
    $http.post("app/list.htm").success(function(data){
        $scope.applications=data;
        if(!data||data.length<0){
            $scope.isEmpty=true;
        }
        $scope.originData=data;
    });
    $scope.query={};
    $scope.typeOptions=[{
        val:1,
        text:"P"
    },{
        val:2,
        text:"C"
    },{
        val:3,
        text:"P.AND.C"
    }];
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.applications=$queryFilter($scope.originData,$scope.query);
    }
});


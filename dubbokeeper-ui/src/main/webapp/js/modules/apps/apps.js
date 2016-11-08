var apps=angular.module("apps",['ngAnimate','ngRoute','serviceProvider','queryFilter','breadCrumb']);
apps.config(function($routeProvider){
    $routeProvider.when("/admin/apps",{
        templateUrl:"templates/apps/application-table.html",
        controller:"appTable"
    }).when("/admin/services", {
        templateUrl:"templates/apps/provide-details.html",
        controller:"serviceTable"
    }).when("/admin/:application/nodes",{
        templateUrl:"templates/apps/node-details.html",
        controller:"nodesDetail"
    }).when("/admin/:application/consumes",{
        templateUrl:"templates/apps/consume-details.html",
        controller:"consumeDetail"
    }).when("/admin/:application/consumers",{
        templateUrl:"templates/apps/consumer-details.html",
        controller:"consumerDetail"
    }).when("/admin/:application/provides",{
        templateUrl:"templates/apps/provide-details.html",
        controller:"provideDetail"
    }).when("/admin/:provider/:consumer/consumes",{
        templateUrl:"templates/apps/provide-details.html",
        controller:"consumeServiceDetails"
    }).when("/admin/:service/:id/service-consumers",{
        templateUrl:"templates/apps/application-table.html",
        controller:"consumerAppTable"
    });
});


apps.controller("consumeServiceDetails",function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu){

    $menu.switchMenu("admin/apps");
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.provider=$routeParams.provider;
    $scope.consumer=$routeParams.consumer;
    $scope.isForOneConsumer=true;
    $breadcrumb.pushCrumb($scope.consumer,"查看应用"+$scope.consumer+"依赖"+$scope.provider+"应用的服务列表","consumeServiceDetails");
    $httpWrapper.post({
        url:"app/"+$routeParams.provider+"/"+$routeParams.consumer+"/consumes.htm",
        success:function(data){
            $scope.details=data;
            if(!data||data.length<=0){
                $scope.isEmpty=true;
            }
            $scope.originData=data;
        }
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

apps.controller("consumerDetail",function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu){
    $menu.switchMenu("admin/apps");
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.application=$routeParams.application;
    $breadcrumb.pushCrumb($scope.application,"查看依赖"+$scope.application+"应用列表","consumerDetail");
    $httpWrapper.post({
        url:"app/"+$routeParams.application+"/consumers.htm",
        success:function(data){
            $scope.details=data;
            if(!data||data.length<=0){
                $scope.isEmpty=true;
            }
            $scope.originData=data;
        }
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


apps.controller("nodesDetail",function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu){
    $menu.switchMenu("admin/apps");
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.application=$routeParams.application;
    $breadcrumb.pushCrumb($scope.application,"查看"+$scope.application+"应用节点列表","nodesDetail");
    $httpWrapper.post({
        url: "app/"+$routeParams.application+"/nodes.htm",
        success:function(data){
            $scope.details=data;
            if(!data||data.length<=0){
                $scope.isEmpty=true;
            }
            $scope.originData=data;
        }
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
apps.controller("consumeDetail",function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu){
    $menu.switchMenu("admin/apps");
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.application=$routeParams.application;
    $breadcrumb.pushCrumb($scope.application,"查看"+$scope.application+"消费服务列表","consumeDetail");
    $httpWrapper.post({
        url:"app/"+$routeParams.application+"/consumes.htm",
        success:function(data){
            $scope.details=data;
            if(!data||data.length<=0){
                $scope.isEmpty=true;
            }
            $scope.originData=data;
        }
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
apps.controller("provideDetail",function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu){
    $menu.switchMenu("admin/apps");
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.application=$routeParams.application;
    var requestUrl = "app/"+$routeParams.application+"/provides.htm";
    $breadcrumb.pushCrumb($scope.application,"查看"+$scope.application+"提供服务列表","provideDetail");
    $httpWrapper.post({
        url:requestUrl,
        success:function(data){
            $scope.details=data;
            if(!data||data.length<=0){
                $scope.isEmpty=true;
            }
            $scope.originData=data;
        }
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
apps.controller("consumerAppTable",function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu){
    $menu.switchMenu("admin/apps");
    $scope.applications=[];
    $scope.isEmpty=false;
    $scope.isConsumer=true;
    $scope.service=$routeParams.service;
    $breadcrumb.pushCrumb($scope.service,"查看依赖"+$scope.service+"服务的应用列表","consumerAppTable");
    $httpWrapper.post({
        url:"app/"+$routeParams.id+"/consumer-apps.htm",
        success:function(data){
            $scope.applications=data;
            if(!data||data.length<0){
                $scope.isEmpty=true;
            }
            $scope.originData=data;
        }
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

apps.controller("appTable",function($scope,$httpWrapper,$queryFilter,$breadcrumb,$menu){
    $menu.switchMenu("admin/apps");
    $scope.applications=[];
    $scope.isEmpty=false;
    $breadcrumb.pushCrumb("应用列表","查看应用列表","admin/apps");
    $httpWrapper.post({
        url:"app/list.htm",
        success:function(data){
            $scope.applications=data;
            if(!data||data.length<0){
                $scope.isEmpty=true;
            }
            $scope.originData=data;
        }
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

apps.controller("serviceTable",function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu){
    $menu.switchMenu("admin/services");
    $scope.details=[];
    $scope.isEmpty=false;
    $scope.isForAllService=true;
    $breadcrumb.pushCrumb("服务列表","查看服务列表","admin/services");
    $httpWrapper.post({
        url:"app/services.htm",
        success:function(data){
            $scope.details=data;
            if(!data||data.length<=0){
                $scope.isEmpty=true;
            }
            $scope.originData=data;
        }
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


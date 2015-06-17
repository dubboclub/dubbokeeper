var serviceProvider=angular.module("serviceProvider",['ngAnimate','ngRoute','queryFilter','breadCrumb']);

serviceProvider.config(function($routeProvider){
    $routeProvider.when("/:serviceKey/:service/providers",{
        templateUrl:"templates/apps/service-providers.html",
        controller:"serviceProviders"
    }).when("/edit/:service/:address/:id/provider",{
        templateUrl:"templates/apps/edit-provider.html",
        controller:"editProvider"
    }).when("/view/:service/:address/:id/detail",{
        templateUrl:"templates/apps/view-provider.html",
        controller:"viewProvider"
    }).when("/operation/:type/:service/:address/:id/provider",{
        templateUrl:"templates/apps/edit-provider.html",
        controller:"operateProvider"
    }).otherwise("/");
});
serviceProvider.controller("viewProvider",function($scope,$httpWrapper,$routeParams,$breadcrumb,$httpWrapper,$menu){
    $menu.switchMenu(menu.APPS);
    $scope.provider={};
    $scope.service=$routeParams.service;
    $breadcrumb.pushCrumb($routeParams.address,"查看服务"+$routeParams.service+"提供者明细","viewProvider");
    $httpWrapper.post({url:"provider/"+$routeParams.id+"/provider-detail.htm",success:function(data){
        $scope.provider=data;
        $scope.parameters=queryString2Object(data.parameters);
        $scope.parameters.enabled=data.enabled;
        $scope.parameters.weight=data.weight;
    }})
});
serviceProvider.controller("editProvider",function($scope,$http,$routeParams,$breadcrumb,$dialog,$httpWrapper,$menu){
    $menu.switchMenu(menu.APPS);
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
    $httpWrapper.post({
        url:"provider/"+$routeParams.id+"/provider-detail.htm",
        success: function (data) {
            $scope.provider=data;
            $scope.parameters=queryString2Object(data.parameters);
            $scope.parameters.enabled=data.enabled;
            $scope.parameters.weight=data.weight;
        }
    });
    $scope.update=function(){
        $dialog.confirm({content:"确认提交修改内容？",callback:function(){
            $httpWrapper.post({
                url:"provider/edit-provider.htm",
                data:"parameters="+object2QueryString($scope.parameters)+"&id="+$routeParams.id,
                config:{ headers: { 'Content-Type': 'application/x-www-form-urlencoded'}},
                success:function(data){
                    $dialog.info({content:"成功更新"+$scope.service+"服务信息！"});
                }
            });
        }})
    }

});



serviceProvider.controller("serviceProviders",function($scope,$http,$routeParams,$queryFilter,$breadcrumb,$dialog,$httpWrapper,$menu){
    $menu.switchMenu(menu.APPS);
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
    var refreshData = function(){
        $httpWrapper.post({
            url:"provider/"+$routeParams.service+"/providers.htm",
            data:"serviceKey="+$routeParams.serviceKey,
            config:{ headers: { 'Content-Type': 'application/x-www-form-urlencoded'}},
            success:function(data){
                $scope.details=data;
                if(!data||data.length<=0){
                    $scope.isEmpty=true;
                }
                $scope.originData= $scope.details;
            }
        });
    }
    refreshData();
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    }
    $scope.operation=function(type,provider){
        var submit = function(){
            $httpWrapper.post({
                url:"provider/"+provider.id+"/"+type+"/operate.htm",
                success:function(data){
                    $dialog.info({content:"成功更新"+$scope.service+"服务信息！",callback:refreshData});
                }
            });
        };
        $dialog.confirm({content:"确认进行此操作？",size:'small',callback: function () {
            submit();
        }});
    }
    $scope.batchOperation=function(type){
        var selected=[];
        $("tbody tr input[type='checkbox']").each(function(){
            if(this.checked){
                selected.push(this.value);
            }
        });
        if(selected.length<=0){
            $dialog.alert({content:"请选择操作数据！",size:'small'});
            return;
        }
        var submit=function(){
            $httpWrapper.post({
                url:"provider/"+type+"/batch-operate.htm",
                data:"ids="+selected.join(","),
                config:{ headers: { 'Content-Type': 'application/x-www-form-urlencoded'}},
                success:function(){
                    $dialog.info({content:"成功更新"+$scope.service+"服务信息！",callback:refreshData});
                    $("thead tr input[type='checkbox']")[0].checked=false;
                }
            });
        };
        $dialog.confirm({content:"确认进行此操作？",size:'small',callback: function () {
            submit();
        }});
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
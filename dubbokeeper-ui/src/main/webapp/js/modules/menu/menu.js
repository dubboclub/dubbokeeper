var menu=angular.module("menu",['ngCookies']);
menu.statistics = {};
menu.statistics.HOME="statistics/home";
menu.admin = {};
menu.admin.APPS="admin/apps";
menu.admin.DYNAMIC_CONFIG="admin/dynamicConfig";
menu.admin.ROUTER="admin/routeConfig";
menu.monitor = {};
menu.monitor.elapsed="monitor/elapsed";
menu.directive("menuTpl",function(){
    return {
        restrict:"E",
        templateUrl:"templates/menu/menu.html",
        controller:"menuController"
    };
});
menu.controller("menuController",function($scope,$cookieStore,$dkContext,$menu){
    $scope.currentMenu=menu.statistics.HOME;
    $scope.currentBar='dashboard';
    $scope.switchMenu=function(m){
        $scope.currentMenu=m;
    }
    $menu._init($scope,$dkContext);
});
menu.$menu= function () {
    var dubboKeeperMenu = function () {
        this.inited=false;
    }
    dubboKeeperMenu.prototype._init=function($scope,$dkContext){
        this.inited=true;
        this.scope = $scope;
        this.dkContext=$dkContext;
        if(this.lastMenu){
           this._switch(this.lastMenu);
        }
    }
    dubboKeeperMenu.prototype.refreshMenus=function(menus){
        var rendingMenus=[];
        if(menus){
            for(var i=0;i<menus.length;i++){
                if(!menus[i].disable){
                    rendingMenus.push(menus[i])
                }
            }
            if(rendingMenus.length==0){
                this.dkContext.changeProperty('hasMenu',false);
            }else{
                this.dkContext.changeProperty('hasMenu',true);
            }
        }
        this.scope.menus=rendingMenus;
    }
    dubboKeeperMenu.prototype.switchMenu=function(menu){
        if(this.inited){
            this._switch(menu);
        }else{
            this.lastMenu=menu;
        }
    }
    dubboKeeperMenu.prototype.switchBarOnly=function(barIdentify){
        this.barProvider.switchBar(barIdentify);
    }
    dubboKeeperMenu.prototype._switch=function(menu){
        if(this.scope.currentMenu==menu){
            return ;
        }
        this.scope.currentMenu=menu;
        var bar = this.dkContext.getBarByMenuIdentify(menu);
        if(bar){
            this.barProvider.switchBar(bar.barIdentify);
        }
    }
    var menuProvider = new dubboKeeperMenu();
    this.$get = ["$bars","$dkContext",function ($bar,$dkContext) {
        menuProvider.barProvider = $bar;
        menuProvider.dkContext=$dkContext;
        return menuProvider;
    }];
}
menu.provider("$menu",menu.$menu);
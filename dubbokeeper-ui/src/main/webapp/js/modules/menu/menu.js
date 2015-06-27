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
menu.controller("menuController",function($scope,$cookieStore,$menu){
    $scope.currentMenu=menu.statistics.HOME;
    $scope.switchMenu=function(m){
        $scope.currentMenu=m;
    }
    $menu._init($scope);
});
menu.$menu= function () {
    var dubboKeeperMenu = function () {
        this.inited=false;
    }
    dubboKeeperMenu.prototype._init=function($scope){
        this.inited=true;
        this.scope = $scope;
        if(this.lastMenu){
            this.scope.currentMenu=this.lastMenu;
        }
    }
    dubboKeeperMenu.prototype.switchMenu=function(menu){
        if(this.inited){
            this.scope.currentMenu=menu;
        }else{
            this.lastMenu=menu;
        }
    }
    this.$get = function () {
        return new dubboKeeperMenu();
    };
}
menu.provider("$menu",menu.$menu);
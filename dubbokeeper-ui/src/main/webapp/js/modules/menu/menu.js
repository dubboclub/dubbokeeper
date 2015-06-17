var menu=angular.module("menu",['ngCookies']);
menu.HOME="home";
menu.APPS="apps";
menu.DYNAMIC_CONFIG="dynamicConfig";
menu.ROUTER="routeConfig";
menu.directive("menuTpl",function(){
    return {
        restrict:"E",
        templateUrl:"templates/menu/menu.html",
        controller:"menuController"
    };
});
menu.controller("menuController",function($scope,$cookieStore,$menu){
    $scope.currentMenu=menu.HOME;
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
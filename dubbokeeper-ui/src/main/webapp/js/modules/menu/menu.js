var menu=angular.module("menu",['ngCookies']);
menu.directive("menuTpl",function(){
    return {
        restrict:"E",
        templateUrl:"templates/menu/menu.html",
        controller:"menuController"
    };
});
menu.COOKIE_KEY="DUBBO_KEEEPER_CURRENT_MENU";
menu.controller("menuController",function($scope,$cookieStore){
    var currentMenu=$cookieStore.get(menu.COOKIE_KEY);
    if(currentMenu){
        $scope.currentMenu=currentMenu;
    }else{
        $scope.currentMenu="home";
    }

    $scope.switchMenu=function(m){
        $scope.currentMenu=m;
        $cookieStore.put(menu.COOKIE_KEY,m);
    }
});
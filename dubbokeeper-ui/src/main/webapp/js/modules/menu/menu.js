var menu=angular.module("menu",[]);
menu.directive("menuTpl",function(){

    return {
        restrict:"E",
        templateUrl:"templates/menu/menu.html"
    };
});
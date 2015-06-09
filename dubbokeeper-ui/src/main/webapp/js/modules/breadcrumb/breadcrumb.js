var breadcrumb=angular.module("breadcrumb",[]);
breadcrumb.directive("breadcrumbTpl",function(){
    return {
        restrict:"E",
        templateUrl:"templates/breadcrumb/breadcrumb.html",
        controller:"breadcrumbCtrl"
    };
});
breadcrumb.controllerScope=undefined;
breadcrumb.controller("breadcrumbCtrl",function($scope){
    $scope.items=[];
    breadcrumb.controllerScope=$scope;
});


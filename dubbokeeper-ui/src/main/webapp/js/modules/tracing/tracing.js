var tracing = angular.module('tracing',['ngRoute']);
tracing.config(function($routeProvider){
    $routeProvider.when("/tracing",{
        templateUrl:"templates/tracing/index.html",
        controller:"tracing"
    });
});
tracing.controller('tracing',function($scope, $menu){
    $scope.message = 'to be continue';
    $menu.switchBarOnly("tracing");
});
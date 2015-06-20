var override = angular.module('override',['ngAnimate','ngRoute','serviceProvider','queryFilter','breadCrumb']);

override.config(function($routeProvider){
    $routeProvider.when("/override/:service/:providerAddress/:providerId",{
        templateUrl:"templates/override/provider-overrides.html",
        controller:"providerOverrides"
    }).otherwise("/");

});

override.controller('providerOverrides',function($scope,$httpWrapper,$routeParams,$queryFilter,$breadcrumb,$menu){



});



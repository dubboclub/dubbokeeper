var head=angular.module("head",[]);
head.directive("headTpl",function(){

     return {
        restrict:"E",
        templateUrl:"templates/head/head.html",
        controller:"headController"
    };
});
head.controller("headController",function($scope){
});
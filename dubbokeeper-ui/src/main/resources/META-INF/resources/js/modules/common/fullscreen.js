var fullScreenDirective = angular.module('fullScreen', []);
fullScreenDirective.directive("fullScreen",function(){
    return {
        restrict:"A",
        scope: {
            changeCallback:"=changeCallback",
            targetClass:"=targetClass"
        },
        link:function($scope,element){
            element.append("&nbsp;&nbsp;<i class=\"glyphicon glyphicon-resize-full\" title='全屏'></i>");
            var full = element.find(".glyphicon-resize-full");
            full.bind("click",function(){
               $(document).bind("fullscreenchange", function(e) {
                    if($(document).fullScreen()){
                        full.removeClass("glyphicon-resize-full");
                        full.addClass("glyphicon-resize-small");
                        full.attr("title","还原");
                        element.parents("."+$scope.targetClass).addClass("full-screen");
                        if($scope.changeCallback){
                            $scope.changeCallback(true);
                        }
                    }else{
                        element.parents("."+$scope.targetClass).removeClass("full-screen");
                        full.removeClass("glyphicon-resize-small");
                        full.addClass("glyphicon-resize-full");
                        full.attr("title","全屏");
                        if($scope.changeCallback){
                            $scope.changeCallback(false);
                        }
                    }
                });
                if(element.parents("."+$scope.targetClass).fullScreen()){

                    element.parents("."+$scope.targetClass).fullScreen(false);
                }else{

                    element.parents("."+$scope.targetClass).fullScreen(true);
                }
            });
           // $("#fullscreenButton").toggle($(document).fullScreen() != null))
        }
    }
});
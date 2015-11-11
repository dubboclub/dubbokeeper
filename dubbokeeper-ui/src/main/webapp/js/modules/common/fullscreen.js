var fullScreen = angular.module('fullScreen', []);
fullScreen.directive("fullScreen",function(){
    return {
        restrict:"A",
        scope: {
            changeCallback:"=changeCallback"
        },
        link:function($scope,element){
            var but = element.append("<button>全屏</button>")
            console.log(element);
            but.bind("click",function(){
               $(document).bind("fullscreenchange", function(e) {
                    if($(document).fullScreen()){
                        element.addClass("fullScreen");
                        $scope.changeCallback(true);
                    }else{
                          $scope.changeCallback(false);
                         element.removeClass("fullScreen");
                    }
                });
                if(element.fullScreen()){
                    element.fullScreen(false);
                }else{
                    element.fullScreen(true); 
                }
            });
           // $("#fullscreenButton").toggle($(document).fullScreen() != null))
        }
    }
});
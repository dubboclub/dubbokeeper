var head=angular.module("head",[]);
head.directive("headTpl",function(){
     return {
        restrict:"E",
        templateUrl:"templates/head/head.html",
        controller:"headController"
    };
});
head.controller("headController",function($scope,$menu,$dkContext,$breadcrumb,$bars,$theme){
    $scope.currentBar="dashboard";
    $scope.bars=$dkContext.getBars();
    $dkContext.changeProperty('needBreadCrumb',false);
    $scope.currentTheme = $theme.getCurrentTheme();
    $scope.switchTheme=function(type){
        $theme.setTheme(type);
        $scope.currentTheme = $theme.getCurrentTheme();
    }
    $scope.switchBar=function(barName){
        var menus=[];
        for(var i=0;i<$scope.bars.length;i++){
            if($scope.bars[i].barIdentify==barName){
                if($scope.bars[i].menus){
                    menus=$scope.bars[i].menus;
                    $dkContext.changeProperty('needBreadCrumb',true);
                    for(var j=0;j<menus.length;j++){
                        if(menus[j].isHome){
                            $dkContext.changeProperty("currentHome",menus[j]);
                            $breadcrumb.resetHome(menus[j]);
                            break;
                        }
                    }
                }else{
                    $dkContext.changeProperty('needBreadCrumb',false);
                }
                break;
            }
        }
        $scope.currentBar=barName;
        $menu.refreshMenus(menus);
    }
    $bars._init($scope);
});

head.$bars= function () {
    var dubboKeeperBar = function () {
        this.inited=false;
    }
    dubboKeeperBar.prototype._init=function($scope){
        this.scope = $scope;
        if(this.lastBar){
            if( this.scope.currentBar==this.lastBar){
                return ;
            }
            this.scope.currentBar=this.lastBar;
            this.scope.switchBar(this.lastBar);
        }
        this.inited=true;
    }
    dubboKeeperBar.prototype.switchBar=function(barName){
         if(this.inited){
             if( this.scope.currentBar==barName){
                 return ;
             }
             this.scope.currentBar = barName;
             this.scope.switchBar(barName);
         }else{
             this.lastBar=barName;
         }
    }
    var barProvider = new dubboKeeperBar();
    this.$get = function () {
        return barProvider;
    };
}
head.provider("$bars",head.$bars);
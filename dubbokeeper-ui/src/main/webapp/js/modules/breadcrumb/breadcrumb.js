var breadCrumb=angular.module("breadCrumb",['ngCookies']);
breadCrumb.directive("breadcrumbTpl",function(){
    return {
        restrict:"E",
        templateUrl:"templates/breadcrumb/breadcrumb.html",
        controller:"breadcrumbCtrl"
    };
});
breadCrumb.controller("breadcrumbCtrl",function($scope,$breadcrumb,$location, $cookieStore){
    $scope.items=[];

    var crumbs=$cookieStore.get(breadCrumb.COOKIE_KEY);
    if(crumbs){
        $scope.items=crumbs;
    }
    $breadcrumb.init($scope,$location,$cookieStore);
});
breadCrumb.COOKIE_KEY="DUBBO_KEEPER_BREAD_CRUMB";
breadCrumb.$breadcrumb=function(){

    var BreadCrumb=function(){
    }
    BreadCrumb.prototype.pushCrumb=function(crumbName,tip,key){
        var index=this._findCrumbIndex(key);
        if(index<0){
            this.scope.items.push({name:crumbName,url:this.location.path(),tip:tip,key:key});
        }else if(index<this.scope.items.length){
            this.scope.items=this.scope.items.slice(0,index+1);
        }
        this.cookiesStore.put(breadCrumb.COOKIE_KEY, this.scope.items);
    }
    BreadCrumb.prototype._findCrumbIndex=function(key){
        for(var i=0;i<this.scope.items.length;i++){
            if(this.scope.items[i].key == key){
                return i;
            }
        }
        return -1;
    }
    BreadCrumb.prototype.init= function ($scope,$location,$cookieStore) {
        this.scope=$scope;
        this.location=$location;
        this.cookiesStore=$cookieStore;
    }
    var bc = new BreadCrumb();

    this.$get = function () {
        return bc;
    };
}
breadCrumb.provider("$breadcrumb",breadCrumb.$breadcrumb);
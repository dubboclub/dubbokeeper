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
    $scope.home={name:"Home",url:"/",tip:"首页",key:"statisticsIndex"};
    var crumbs=$cookieStore.get(breadCrumb.COOKIE_KEY);
    if(crumbs){
        $scope.items=crumbs;
        $cookieStore.remove(breadCrumb.COOKIE_KEY);
    }else{
        $scope.items.push($scope.home);
    }
    $breadcrumb.init($scope,$location,$cookieStore);
});
breadCrumb.COOKIE_KEY="DUBBO_KEEPER_BREAD_CRUMB";
breadCrumb.$breadcrumb=function(){
    var BreadCrumb=function(){
        this.inited=false;
        this.waitingPushCrumbs = [];
    }
    BreadCrumb.prototype.pushCrumb=function(crumbName,tip,key){
        var crumb = {name:crumbName,url:this.location.path(),tip:tip,key:key};
        if(this.inited){
            this._pushCrumb(crumb);
        }else{
            this.waitingPushCrumbs.push(crumb);
        }
    }
    BreadCrumb.prototype._pushCrumb=function(crumb){
        var index=this._findCrumbIndex(crumb.key);
        if(index<0){
            this.scope.items.push(crumb);
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
    BreadCrumb.prototype.init= function ($scope) {
        this.inited=true;
        this.scope=$scope;
        for(var i=0;i<this.waitingPushCrumbs.length;i++){
            this._pushCrumb(this.waitingPushCrumbs[i]);
        }
    }
    var bc = new BreadCrumb();

    this.$get = ["$location","$cookieStore",function ($location,$cookieStore) {
        bc.location=$location;
        bc.cookiesStore=$cookieStore;
        return bc;
    }];
}
breadCrumb.provider("$breadcrumb",breadCrumb.$breadcrumb);
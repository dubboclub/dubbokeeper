var breadCrumb=angular.module("breadCrumb",['ngCookies']);
breadCrumb.directive("breadcrumbTpl",function(){
    return {
        restrict:"E",
        templateUrl:"templates/breadcrumb/breadcrumb.html",
        controller:"breadcrumbCtrl"
    };
});
breadCrumb.controller("breadcrumbCtrl",function($scope,$breadcrumb,$location, $cookieStore,$dkContext){
    $scope.items=[];
    var currentHome = $dkContext.getProperty("currentHome");
    $scope.home={name:"Home",url:currentHome.href,tip:currentHome.showName,key:currentHome.identify};
    var crumbs=$cookieStore.get(breadCrumb.COOKIE_KEY);
    if(crumbs){
        $scope.items=crumbs;
        $cookieStore.remove(breadCrumb.COOKIE_KEY);
    }
    $breadcrumb.init($scope);
});
breadCrumb.COOKIE_KEY="DUBBO_KEEPER_BREAD_CRUMB";
breadCrumb.$breadcrumb=function(){
    var BreadCrumb=function(){
        this.inited=false;
        this.waitingPushCrumbs = [];
    }
    BreadCrumb.prototype.pushCrumb=function(crumbName,tip,key){
        var crumb = {name:crumbName,url:encodeURI(this.location.path()),tip:tip,key:key};
        if(this.inited){
            this._pushCrumb(crumb);
        }else{
            this.waitingPushCrumbs.push(crumb);
        }
    }
    BreadCrumb.prototype.resetHome=function(home){
        if(this.inited){
            this.scope.home={name:"Home",url:home.href,tip:home.showName,key:home.identify};
            this.scope.items=[];
        }else{
            this.home=home;
        }
    }
    BreadCrumb.prototype._pushCrumb=function(crumb){
        if(this.scope.home.key==crumb.key){
            this.scope.items=[];
            this.cookiesStore.put(breadCrumb.COOKIE_KEY, this.scope.items);
            return ;
        }
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
        this.scope=$scope;
        for(var i=0;i<this.waitingPushCrumbs.length;i++){
            this._pushCrumb(this.waitingPushCrumbs[i]);
        }
        if(this.home){
            this.scope.home={name:"Home",url:this.home.href,tip:this.home.showName,key:this.home.identify};
        }
        this.inited=true;
    }
    var bc = new BreadCrumb();

    this.$get = ["$location","$cookieStore","$dkContext",function ($location,$cookieStore,$dkContext) {
        bc.location=$location;
        bc.cookiesStore=$cookieStore;
        bc.dkContext=$dkContext;
        return bc;
    }];
}
breadCrumb.provider("$breadcrumb",breadCrumb.$breadcrumb);
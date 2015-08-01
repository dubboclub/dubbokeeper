var theme=angular.module("theme",['ngCookies']);
theme.directive("themeTpl",function(){
    return {
        restrict:"E",
        templateUrl:"templates/theme/theme.html",
        controller:"themeController"
    };
});

theme.controller("themeController",function($scope,$theme,$cookieStore){
    $scope.currentTheme='default';
    if($cookieStore.get(theme.cookieName)){
        $scope.currentTheme=$cookieStore.get(theme.cookieName);
    }
    $theme._init($scope);
});

theme.cookieName = "dubbokeeper.theme";
theme.$theme= function () {
    var dubboKeeperTheme = function () {
        this.inited=false;
    }
    dubboKeeperTheme.prototype._init=function($scope){
        this.scope = $scope;
        this.inited=true;
        this.setTheme($scope.currentTheme);
    }
    dubboKeeperTheme.prototype.getCurrentTheme=function(){
        while(!this.inited){

        }
        return this.scope.currentTheme;
    }
    dubboKeeperTheme.prototype.setTheme=function(themeName){
        var themeUrl = "";
        if('default'==themeName){
            themeUrl="";
        }else{
            themeUrl="css/libs/themes/"+themeName+"/bootstrap.css"
        }
        this.cookiesStore.put(theme.cookieName, themeName);
        window.document.getElementById('dubboKeeperTheme').setAttribute('href',themeUrl);
        this.scope.currentTheme = themeName;
    }
    var themeProvider = new dubboKeeperTheme();
    this.$get = ["$cookieStore",function ($cookieStore) {
        themeProvider.cookiesStore = $cookieStore;
        return themeProvider;
    }];
}
theme.provider("$theme",theme.$theme);
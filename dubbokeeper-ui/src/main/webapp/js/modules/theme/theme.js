var theme=angular.module("theme",['ngCookies']);
theme.cookieName = "dubbokeeper.theme";
theme.$theme= function () {
    var dubboKeeperTheme = function () {
        this.currentTheme='default';
    }
    dubboKeeperTheme.prototype._init=function(cookieStore){
        if(cookieStore.get(theme.cookieName)){
            this.currentTheme=cookieStore.get(theme.cookieName);
        }
        this.cookiesStore=cookieStore;
        this.setTheme(this.currentTheme);
    }
    dubboKeeperTheme.prototype.getCurrentTheme=function(){
        return this.currentTheme;
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
        this.currentTheme = themeName;
    }
    var themeProvider = new dubboKeeperTheme();
    this.$get = ["$cookieStore",function ($cookieStore) {
        themeProvider._init($cookieStore);
        return themeProvider;
    }];
}
theme.provider("$theme",theme.$theme);
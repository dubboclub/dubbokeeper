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
    dubboKeeperTheme.prototype.changeListener=function(callback){
        if(!callback||typeof callback!='function'){
            return ;
        }
        if(this.listeners){
            this.listeners.push(callback);
        }else{
            this.listeners=[];
            this.listeners.push(callback);
        }
    }
    dubboKeeperTheme.prototype._notify=function(){
        if(this.listeners){
            for(var i=0;i<this.listeners.length;i++){
                this.listeners[i]();
            }
        }
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
        this._notify();
    }
    var themeProvider = new dubboKeeperTheme();
    this.$get = ["$cookieStore",function ($cookieStore) {
        themeProvider._init($cookieStore);
        return themeProvider;
    }];
}
theme.provider("$theme",theme.$theme);
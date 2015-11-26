angular.module('dubbokeeperFilters', []).filter('encodeUrl', function() {
    return function(input) {
        return encodeURIComponent(encodeURIComponent(input));
    };
}).filter('formatInterfaceName',function(){
    return function(input){
        var items = input.split(".");
        for(var i=0;i<items.length-1;i++){
            items[i]=items[i].substring(0,1);
        }
        return items.join(".");
    }
});
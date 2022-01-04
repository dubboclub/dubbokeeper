var aboutus = angular.module('aboutus',['ngRoute']);
aboutus.config(function($routeProvider){
    $routeProvider.when("/aboutus",{
        templateUrl:"templates/aboutus/index.html",
        controller:"aboutus"
    });
});
aboutus.controller('aboutus',function($scope,$httpWrapper,$menu){
    $menu.switchBarOnly("aboutus");
    $httpWrapper.get({
        url:"templates/aboutus/aboutus.md",
        success:function(mdText){
            var converter = new showdown.Converter();
            var html = converter.makeHtml(Mustache.render(mdText, {}));
            $(".aboutus").html(html);
        }
    });
});
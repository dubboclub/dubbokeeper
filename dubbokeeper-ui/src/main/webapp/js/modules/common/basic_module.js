angular.module('dubbokeeperFilters', []).filter('encodeUrl', function() {
    return function(input) {
        return encodeURIComponent(encodeURIComponent(input));
    };
});
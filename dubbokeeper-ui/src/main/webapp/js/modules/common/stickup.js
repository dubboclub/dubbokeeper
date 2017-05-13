var stickup = angular.module('stickup', []);
stickup.directive('stickup', function() {
  return function(scope, element, attrs) {
     element.css({position:"fixed",width:"60%"});
  }
});
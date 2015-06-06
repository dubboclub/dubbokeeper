var apps=angular.module("apps",[]);
apps.directive("appList",["$http",function($http){
    return {
        restrict:"E",
        templateUrl:"templates/apps/application-table.html",
        controller:function($scope){
            $scope.applications=[];
            $http.post("app/list.htm").success(function(data){
                $scope.applications=data;
            });
        },
        controllerAs:"appList"
    };
}]);
var indexApp=angular.module("indexApp",[]);
indexApp.controller("indexCtrl",function($scope,$http){

});

indexApp.directive("appList",["$http",function($http){
    return {
        restrict:"E",
        templateUrl:"templates/index/application-table.html",
        controller:function($scope){
            $scope.applications=[];
            $('#appTable').bootstrapTable({
                url: 'app/list.htm',
                height:"650"
            });
            /*$http.post("app/list.htm").success(function(data){
                $scope.applications=data;
            });*/
        },
        controllerAs:"appList"
    };
}]);
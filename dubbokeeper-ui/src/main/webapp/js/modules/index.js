var indexApp=angular.module("indexApp",[]);
indexApp.controller("indexCtrl",function($scope){

});
indexApp.directive("applicationDetails",["$http",function($http){
   return {
       restrict:"E",
       templateUrl:"templates/index/application-table.html",
       controller:function(){
           this.applications=[];
           var me=this;
           $http.post("app/list.htm",function(data){
                me.applications=data;
           });
       },
       controllerAs:"indexApps"
   };
}]);
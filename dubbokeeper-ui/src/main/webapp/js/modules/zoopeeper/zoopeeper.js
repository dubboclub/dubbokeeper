var zoopeeper = angular.module('zoopeeper',['ngRoute','AxelSoft','stickup']);

zoopeeper.config(function($routeProvider){
    $routeProvider.when("/zoopeeper",{
        templateUrl:"templates/zoopeeper/zoopeeper.html",
        controller:"zooPeeperController"
    });
});
zoopeeper.controller("zooPeeperController",function($scope,$httpWrapper,$breadcrumb,$menu,$dialog){
    $menu.switchBarOnly("zoopeeper");
    $scope.zkList=[];
    $httpWrapper.post({
        url:"peeper/listZookeepers.htm",
        success:function(data){
            for(var i=0;i<data.length;i++){
                var option = {};
                option.text=data[i];
                option.val=data[i];
                $scope.zkList.push(option);
            }
        }
    });
    $scope.changeZkHost=function(){
         $scope.loadChildren();
    }
    $scope.structure = { nodeList:[]};
    $scope.loadChildren=function(parentNode){
        if(!$scope.currentZK||$scope.currentZK==""){
            return;
        }
        var parentPath="/";
        if(parentNode){
            if(parentNode.parent!='/'){
                parentPath=parentNode.parent+"/"+urlEncode(parentNode.name);
            }else{
                parentPath=parentNode.parent+urlEncode(parentNode.name);
            }
        }
        parentNode=parentNode||$scope.structure;
        $httpWrapper.post({
            url:"peeper/"+$scope.currentZK+"/loadChildren.htm",
            data:"parent="+parentPath,
            config:{ headers: { 'Content-Type': 'application/x-www-form-urlencoded'}},
            success:function(data){
                if(data.state=='SUCCESS'){
                    parentNode.nodeList=[];
                    parentNode.childNodes=[];
                    for(var i=0;i<data.nodeList.length;i++){
                        if(data.nodeList[i].nodeList){
                            data.nodeList[i].childNodes=[];
                            parentNode.nodeList.push(data.nodeList[i]);
                        }else{
                            parentNode.childNodes.push(data.nodeList[i]);
                        }
                    }
                }else if(data.state=='FAILED'){
                    $dialog.alert({
                        content:"后端出现异常，请查看后端日志!",
                        size:"small"
                    });
                }else{
                    $dialog.alert({
                        content:"连接远程Zookeeper出现问题，请确认服务是否可用！",
                        size:"small"
                    });
                }
            }
        });
        
    }
    $scope.options = {
        foldersProperty:"nodeList",
        filesProperty:"childNodes",
        onNodeSelect: function (node, breadcrums) {
            $scope.loadChildren(node);
            $scope.currentSelectedNode=node;
        }
    };
});
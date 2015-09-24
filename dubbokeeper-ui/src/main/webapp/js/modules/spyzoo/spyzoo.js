var spyzoo = angular.module('spyzoo',['ngRoute','AxelSoft','stickup']);

spyzoo.config(function($routeProvider){
    $routeProvider.when("/spyZoo",{
        templateUrl:"templates/spyzoo/spyzoo.html",
        controller:"spyZooController"
    });
});
spyzoo.controller("spyZooController",function($scope,$httpWrapper,$breadcrumb,$menu,$dialog){
    $menu.switchBarOnly("spyZoo");
    $scope.zkList=[];
    $httpWrapper.post({
        url:"spy/listZookeepers.htm",
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
            url:"spy/"+$scope.currentZK+"/loadChildren.htm",
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
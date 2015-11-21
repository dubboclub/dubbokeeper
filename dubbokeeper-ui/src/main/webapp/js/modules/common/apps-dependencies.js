var appDependencies=angular.module('appDependencies', ['dialog']);
appDependencies.directive("appDependencies",function(){
    return {
        restrict:"E",
        scope: {
            graph:"=graph"
        },
        templateUrl:"templates/statistics/app-dependencies.html",
        controller:function($scope,$dialog){
            var playground = $(".dependencies-window").find(".playground");
            var styles=[{color:'#6FB1FC',faveShape:'triangle'},{color:'#86B342',faveShape:'octagon'},{color:'#F5A45D',faveShape:'rectangle'}];
            $scope.saveImg=function(){
                if($scope.cy){
                    $dialog.info({
                        content:"<img style='width:100%;' src=\""+$scope.cy.png()+"\">",
                        title:"保存图片"
                    })
                }else{
                    $dialog.alert({
                        content:"还未初始化依赖关系图！"
                    })
                }
            }
            $scope.reset=function(){
                if($scope.initEdges&&$scope.initNodes){
                    $scope.cy.destroy();
                    playground.cytoscape(generateOptions($scope.initNodes,$scope.initEdges,clickNode));
                }else{
                    $dialog.alert({
                        content:"还未初始化依赖关系图！"
                    })
                }
            }
            var clickNode = function(evt){
                var node = evt.cyTarget
                $scope.selected=true;

                var neighborhood= node.neighborhood();
                $scope.cy.destroy();
                var nodes = [];
                var edges = [];
                for(var i=0;i<neighborhood.length;i++){
                    var el = neighborhood[i];
                    if(el.isEdge()){
                        var edge=el.data();
                        edge.id=undefined;
                        edges.push({data:edge});
                    }else{
                        nodes.push({data:el.data()});
                    }
                }
                nodes.push({data:node.data()});
                playground.cytoscape(generateOptions(nodes,edges,clickNode));
            }
            var generateOptions = function(nodes,edges,clickEvent){
                return  {
                    layout: {
                        name: 'cose',
                        padding: 10
                    },

                    style: cytoscape.stylesheet()
                        .selector('node')
                        .css({
                            'shape': 'data(faveShape)',
                            'width': 'mapData(weight, 40, 80, 20, 60)',
                            'content': 'data(name)',
                            'text-valign': 'center',
                            'text-outline-width': 2,
                            'text-outline-color': 'data(faveColor)',
                            'background-color': 'data(faveColor)',
                            'color': '#fff'
                        })
                        .selector(':selected')
                        .css({
                            'border-width': 3,
                            'border-color': '#333'
                        })
                        .selector('edge')
                        .css({
                            'opacity': 0.666,
                            'width': 'mapData(strength, 70, 100, 1, 1)',
                            'target-arrow-shape': 'triangle',
                            'source-arrow-shape': 'circle',
                            'line-color': 'data(faveColor)',
                            'source-arrow-color': 'data(faveColor)',
                            'target-arrow-color': 'data(faveColor)'
                        })
                        .selector('edge.questionable')
                        .css({
                            'line-style': 'dotted',
                            'target-arrow-shape': 'diamond'
                        })
                        .selector('.faded')
                        .css({
                            'opacity': 0.25,
                            'text-opacity': 0
                        }),

                    elements: {
                        nodes: nodes,
                        edges:edges
                    },

                    ready: function(){
                        $scope.cy = this;
                        this.on("click",'node',function(evt){

                            clickEvent(evt);
                        })

                    }
                };
            }
            $scope.$watch("graph",function(){

                if($scope.cy){
                    $scope.cy.destroy();
                    $scope.cy=undefined;
                }
                if($scope.graph&&$scope.graph.nodes){
                    var nodes = [];
                    for(var i=0;i<$scope.graph.nodes.length;i++){
                        var node = { data: { id: $scope.graph.nodes[i].name, name:  $scope.graph.nodes[i].name, weight: 65, faveColor:styles[$scope.graph.nodes[i].category].color, faveShape: styles[$scope.graph.nodes[i].category].faveShape } }
                        nodes.push(node);
                    }
                    var edges=[];
                    for(var i=0;i<$scope.graph.links.length;i++){
                        var edge = { data: { source: $scope.graph.links[i].source, target: $scope.graph.links[i].target, faveColor: '#6FB1FC', strength: 90 } };
                        edges.push(edge);
                    }
                    $scope.initNodes=nodes;
                    $scope.initEdges=edges;


                    playground.cytoscape(generateOptions(nodes,edges,clickNode));
                }
            });


        }
    }
});
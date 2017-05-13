var appDependencies=angular.module('appDependencies', ['dialog','theme']);
appDependencies.directive("appDependencies",function(){
    return {
        restrict:"E",
        scope: {
            graph:"=graph"
        },
        templateUrl:"templates/statistics/app-dependencies.html",
        controller:function($scope,$dialog,$theme){
            var $window = $(".dependencies-window");
            var playground = $window.find(".playground");
            $scope.styles=[{color:$window.find(".provider").css("backgroundColor"),faveShape:'ellipse'},{color:$window.find(".consumer").css("backgroundColor"),faveShape:'ellipse'},{color:$window.find(".p-and-c").css("backgroundColor"),faveShape:'ellipse'}];
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
                    initRending();
                }else{
                    $dialog.alert({
                        content:"还未初始化依赖关系图！"
                    })
                }
            }
            var clickEdge = function(evt){
                var edge =evt.cyTarget;
                location.hash="#/admin/"+edge.data().target+"/"+edge.data().source+"/consumes";
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
                playground.cytoscape(generateOptions(nodes,edges,clickNode,clickEdge));
            }
            var generateOptions = function(nodes,edges,clickEvent,clickEdge){
                return  {
                    layout: {
                        name: 'cose',
                        padding: 10
                    },

                    style: cytoscape.stylesheet()
                        .selector('node')
                        .css({
                            'shape': 'data(faveShape)',
                            'width': '60',
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
                            'width': '1',
                            'target-arrow-shape': 'triangle',
                            'source-arrow-shape': 'circle',
                            'line-stype':'dotted',
                            'line-color': 'data(faveColor)',
                            'source-arrow-color': 'data(faveColor)',
                            'target-arrow-color': 'data(faveColor)'
                        }),

                    elements: {
                        nodes: nodes,
                        edges:edges
                    },

                    ready: function(){
                        $scope.cy = this;
                        this.on("mouseover",'node,edge',function(evt){
                            if(evt.cyTarget.isEdge()){
                                evt.cyTarget.css({
                                    width:"5",
                                    'line-color':'#ff892a',
                                    'label': evt.cyTarget.data().source+"依赖"+ evt.cyTarget.data().target,
                                    'source-arrow-color': '#ff892a',
                                    'color':$window.find(".label-info").css("backgroundColor"),
                                    'font-weight':'bolder',
                                    'font-size':'18',
                                    'target-arrow-color': '#ff892a'
                                });
                            }else{
                                evt.cyTarget.css({
                                    width:"70",
                                    'background-color':'#ff892a'
                                });
                            }

                        });
                        this.on("mouseout",'node,edge',function(evt){
                            if(evt.cyTarget.isEdge()){
                                evt.cyTarget.css({
                                    width:"1",
                                    'line-color':evt.cyTarget.data().faveColor,
                                    'label':'',
                                    'source-arrow-color': evt.cyTarget.data().faveColor,
                                    'target-arrow-color': evt.cyTarget.data().faveColor
                                });
                            }else{
                                evt.cyTarget.css({
                                    width:"60",
                                    'background-color':evt.cyTarget.data().faveColor
                                });
                            }

                        });
                        this.on("click",'node',function(evt){

                            clickEvent(evt);
                        })
                        this.on("click",'edge',function(evt){

                            clickEdge(evt);
                        })
                    }
                };
            }
            $scope.$watch("graph",function(){

                initRending();
            });

            var initRending = function(){
                if($scope.cy){
                    $scope.cy.destroy();
                    $scope.cy=undefined;
                }
                if($scope.graph&&$scope.graph.nodes){
                    var nodes = [];
                    for(var i=0;i<$scope.graph.nodes.length;i++){
                        var node = { data: { id: $scope.graph.nodes[i].name, name:  $scope.graph.nodes[i].name, weight: 65, faveColor:$scope.styles[$scope.graph.nodes[i].category].color, faveShape: $scope.styles[$scope.graph.nodes[i].category].faveShape } }
                        nodes.push(node);
                    }
                    var edges=[];
                    for(var i=0;i<$scope.graph.links.length;i++){
                        var edge = { data: { source: $scope.graph.links[i].source, target: $scope.graph.links[i].target, faveColor: $window.find(".label-info").css("backgroundColor"), strength: 90 } };
                        edges.push(edge);
                    }
                    $scope.initNodes=nodes;
                    $scope.initEdges=edges;


                    playground.cytoscape(generateOptions(nodes,edges,clickNode,clickEdge));
                }
                $theme.changeListener(function(){
                    setTimeout(function(){
                        $scope.styles=[{color:$window.find(".provider").css("backgroundColor"),faveShape:'ellipse'},{color:$window.find(".consumer").css("backgroundColor"),faveShape:'ellipse'},{color:$window.find(".p-and-c").css("backgroundColor"),faveShape:'ellipse'}];
                        initRending();
                    },100);
                })
            }
        }
    }
});
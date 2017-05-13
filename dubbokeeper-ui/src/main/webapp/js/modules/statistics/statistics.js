var statistics = angular.module("statistics",['ngRoute']);

statistics.config(function($routeProvider){
    $routeProvider.when("/statistics",{
        templateUrl:"templates/statistics/statistics.html",
        controller:"statisticsIndex"
    });
});
statistics._generatePieOption=function(data,title,name){
    if(typeof data != 'object'){
        throw new Error("饼状图数据必须是键值对形式");
    }
    var keys = [];
    var dataset=[];
    for(var key in data){
        keys.push(key);
        var item={};
        item.name=key;
        item.value=data[key];
        dataset.push(item);
    }
    var option = {
        title : {
            text: title,
            x:'center'
        },
        tooltip : {
            trigger: 'item',
            formatter: "{a} <br/>{b} : {c} ({d}%)"
        },
        legend: {
            orient : 'vertical',
            x : 'left',
            data:keys
        },
        toolbox: {
            show : true,
            feature : {
                magicType : {
                    show: true,
                    type: ['pie', 'funnel'],
                    option: {
                        funnel: {
                            x: '25%',
                            width: '50%',
                            funnelAlign: 'left',
                            max: 1548
                        }
                    }
                },
                restore : {show: true},
                saveAsImage : {show: true}
            }
        },
        calculable : true,
        series : [
            {
                name:name,
                type:'pie',
                radius : '55%',
                center: ['50%', '60%'],
                data:dataset
            }
        ]
    };
    return option;
}
statistics.controller("statisticsIndex",function($scope,$httpWrapper,$breadcrumb,$menu){
    $breadcrumb.pushCrumb("Home","首页","statisticsIndex");
    $menu.switchMenu(menu.HOME);
    $scope.currentTab='apps';
    $scope.switchTab=function(tabName){
        $scope.currentTab=tabName;
        switch (tabName){
            case 'apps':{
                $httpWrapper.post({
                    url:"loadAppsType.htm",
                    success:function(data){
                        require( [
                            'echarts',
                            'echarts/chart/pie', // 使用柱状图就加载bar模块，按需加载
                            'echarts/chart/funnel' // 使用柱状图就加载bar模块，按需加载
                        ], function (echarts) {
                            require(['echarts/theme/shine'], function(curTheme){
                                var option =statistics._generatePieOption({'P':data[0],'C':data[1],'P.AND.C':data[2]},'应用类型分布图','应用类型');
                                var myChart = echarts.init(document.getElementById('statisticsAppsTypes'));
                                myChart.setTheme(curTheme);
                                myChart.setOption(option);
                            });
                        });
                    }
                });
                $httpWrapper.post({
                    url:"loadServiceProtocols.htm",
                    success: function (data) {
                        require( [
                            'echarts',
                            'echarts/chart/pie',// 使用柱状图就加载bar模块，按需加载
                            'echarts/chart/funnel' // 使用柱状图就加载bar模块，按需加载
                        ], function (echarts) {
                            require(['echarts/theme/blue'], function(curTheme){
                                var option =statistics._generatePieOption(data,'暴露协议分布图','暴露协议');
                                var myChart = echarts.init(document.getElementById('statisticsServiceProtocol'));
                                myChart.setTheme(curTheme);
                                myChart.setOption(option);
                            });
                        });

                    }
                });
                break;
            }
            case 'nodes':{
                $httpWrapper.post({
                    url:"loadAppNodes.htm",
                    success:function(data){
                        require( [
                            'echarts',
                            'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
                            'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
                        ], function (echarts) {
                            require(['echarts/theme/macarons'], function(curTheme){
                                var xAxisData=[];
                                var nodes=[];
                                for(var key in data){
                                    xAxisData.push(key);
                                    nodes.push(data[key]);
                                }
                                var option = {
                                    title : {
                                        text: '应用部署节点统计',
                                        subtext: '来自注册中心'
                                    },
                                    tooltip : {
                                        trigger: 'axis'
                                    },
                                    legend: {
                                        data:['部署节点数']
                                    },
                                    toolbox: {
                                        show : true,
                                        feature : {
                                            magicType : {show: true, type: ['line', 'bar']},
                                            restore : {show: true},
                                            saveAsImage : {show: true}
                                        }
                                    },
                                    calculable : true,
                                    xAxis : [
                                        {
                                            type : 'category',
                                            data : xAxisData,
                                            show:false
                                        }
                                    ],
                                    yAxis : [
                                        {
                                            type : 'value'
                                        }
                                    ],
                                    series : [
                                        {
                                            name:'部署节点数',
                                            type:'bar',
                                            data:nodes
                                        }
                                    ]
                                };
                                var myChart = echarts.init(document.getElementById('nodes'));
                                myChart.setTheme(curTheme)
                                myChart.setOption(option);
                                var ecConfig = require('echarts/config');
                                myChart.on(ecConfig.EVENT.CLICK, function (params) {
                                    location.hash="#/admin/"+params.name+"/nodes";
                                });
                            });
                        });
                    }
                });
                break;
            }
            case 'service':{
                $httpWrapper.post({
                    url:"loadAppServices.htm",
                    success:function(data){
                        require( [
                            'echarts',
                            'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
                            'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
                        ], function (echarts) {
                            require(['echarts/theme/macarons'], function(curTheme){
                                var xAxisData=[];
                                var provides=[];
                                var consumes=[];
                                for(var key in data){
                                    xAxisData.push(key);
                                    provides.push(data[key][0]);
                                    consumes.push(data[key][1]);
                                }
                                var option = {
                                    title : {
                                        text: '应用发布服务/订阅服务统计',
                                        subtext: '来自注册中心'
                                    },
                                    tooltip : {
                                        trigger: 'axis'
                                    },
                                    legend: {
                                        data:['发布的服务','订阅的服务']
                                    },
                                    toolbox: {
                                        show : true,
                                        feature : {
                                            magicType : {show: true, type: ['line', 'bar']},
                                            restore : {show: true},
                                            saveAsImage : {show: true}
                                        }
                                    },
                                    calculable : true,
                                    xAxis : [
                                        {
                                            type : 'category',
                                            data : xAxisData,
                                            show:false
                                        }
                                    ],
                                    yAxis : [
                                        {
                                            type : 'value'
                                        }
                                    ],
                                    series : [
                                        {
                                            name:'发布的服务',
                                            type:'bar',
                                            data:provides
                                        },
                                        {
                                            name:'订阅的服务',
                                            type:'bar',
                                            data:consumes
                                        }
                                    ]
                                };
                                var myChart = echarts.init(document.getElementById('serviceStatus'));
                                myChart.setTheme(curTheme)
                                myChart.setOption(option);
                                var ecConfig = require('echarts/config');
                                myChart.on(ecConfig.EVENT.CLICK, function (params) {
                                    if(params.seriesIndex==0){
                                        location.hash="#/admin/"+params.name+"/provides";
                                    }else{
                                        location.hash="#/admin/"+params.name+"/consumes";
                                    }
                                })
                            });
                        });
                    }
                });
                break;
            }
            case 'dependencies':{ 
                $httpWrapper.post({
                url:"loadAppsDependencies.htm",
                success:function(data){
                     /*var nodeSize = 20;
                     var graph={};
                     var nodes=[];
                     for(var i=0;i<nodeSize;i++){
                        var node={};
                         node.name="node"+i;
                         node.category=parseInt(Math.random()*2);
                         nodes.push(node);
                     }
                     var edges=[];
                    for(var i=0;i<nodeSize;i++){
                        var size = Math.random()*10;
                        for(var j=0;j<size;j++){
                            var edge={};
                            edge.source=nodes[i].name;
                            edge.target="node"+parseInt(Math.random()*nodeSize);
                            edges.push(edge);
                        }
                    }*/

                    $scope.graph=data;
                }
            });
                break;
            }
        }
    }
    $scope.switchTab('apps');

});

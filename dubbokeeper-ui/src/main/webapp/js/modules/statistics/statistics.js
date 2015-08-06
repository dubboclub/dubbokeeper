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
                                myChart.setTheme(curTheme)
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
                    require( [
                        'echarts',
                        'echarts/chart/force', // 使用柱状图就加载bar模块，按需加载
                        'echarts/chart/chord' // 使用柱状图就加载bar模块，按需加载
                    ], function (echarts) {
                        require(['echarts/theme/blue'], function(curTheme){
                            var originNodes = data.nodes.filter(function(){return true});
                            var originLinks = data.links.filter(function(){return true});
                            var option = {
                                title : {
                                    text: '应用依赖关系图',
                                    subtext: '数据来自注册中心',
                                    x:'center'
                                },
                                tooltip : {
                                    trigger: 'item',
                                    formatter: '{a} : {b}'
                                },
                                toolbox: {
                                    show : true,
                                    feature : {
                                        restore : {show: true},
                                        magicType: {show: true, type: ['force', 'chord']},
                                        saveAsImage : {show: true}
                                    }
                                },
                                legend: {
                                    x: 'left',
                                    data:['纯提供者','纯消费者','即是提供者也是消费者']
                                },
                                series : [
                                    {
                                        type:'force',
                                        name : "应用关系",
                                        ribbonType: false,
                                        roam:'scale',
                                        nodePadding:50,
                                        orient:'radial',
                                        categories : [
                                            {
                                                name: '纯提供者'
                                            },
                                            {
                                                name: '纯消费者'
                                            },
                                            {
                                                name:'即是提供者也是消费者'
                                            }
                                        ],
                                        itemStyle: {
                                            normal: {
                                                label: {
                                                    show: true,
                                                    textStyle: {
                                                        color: '#333'
                                                    }
                                                },
                                                nodeStyle : {
                                                    brushType : 'both',
                                                    borderColor : 'rgba(255,215,0,0.4)',
                                                    borderWidth : 1
                                                }
                                            },
                                            emphasis: {
                                                label: {
                                                    show: false
                                                    // textStyle: null      // 默认使用全局文本样式，详见TEXTSTYLE
                                                },
                                                nodeStyle : {
                                                    //r: 30
                                                },
                                                linkStyle : {}
                                            }
                                        },
                                        minRadius : 15,
                                        maxRadius : 25,
                                        gravity: 1.1,
                                        scaling: 1.2,
                                        draggable: false,
                                        linkSymbol: 'arrow',
                                        steps: 10,
                                        coolDown: 0.9,
                                        //preventOverlap: true,
                                        nodes: data.nodes,
                                        links :data.links
                                    }
                                ]
                            };
                            var myChart = echarts.init(document.getElementById('dependencies'),curTheme);
                            window.onresize = myChart.resize;
                            myChart.showLoading();
                            myChart.setOption(option,true);

                            var originSeries = option.series;
                            var ecConfig = require('echarts/config');
                            function focus(param) {
                                var data = param.data;
                                var links = option.series[0].links;
                                var nodes = option.series[0].nodes;
                                var currentSeries = option.series[0];
                                if (data.source != null&& data.target != null) { //点击的是边
                                    location.hash="#/admin/"+data.target+"/"+data.source+"/consumes";
                                } else { // 点击的是点
                                    console.log("选中了" + data.name + '(' + data.value + ')');
                                    var currentNodes = [data];
                                    var currentNodeLinks = links.filter(function (link) {
                                        var matched = link.source == data.name|| link.target==data.name;
                                        if(matched){
                                            currentNodes.push(nodes.filter(function (node) {
                                                return data.name!=node.name&&(node.name==link.target||node.name==link.source);
                                            })[0]);
                                        }
                                        return matched;
                                    });
                                    if(currentNodeLinks.length>0){
                                        currentSeries.links=currentNodeLinks;
                                        currentSeries.nodes= currentNodes;
                                        myChart.showLoading();
                                        myChart.setSeries([currentSeries],true);
                                    }
                                }
                            }
                            myChart.on(ecConfig.EVENT.CLICK, focus)
                            myChart.on(ecConfig.EVENT.FORCE_LAYOUT_END, function(){
                                myChart.hideLoading();
                            });
                            myChart.on(ecConfig.EVENT.RESTORE,function(param){
                                var currentSeries = option.series[0];
                                currentSeries.nodes=originNodes;
                                currentSeries.links=originLinks;
                                myChart.showLoading();
                                myChart.setSeries([currentSeries]);
                            });
                        });
                    });
                }
            });
                break;
            }
        }
    }
    $scope.switchTab('apps');

});

var statistics = angular.module("statistics",['ngRoute']);

statistics.config(function($routeProvider){
    $routeProvider.when("/",{
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
                            require(['js/echarts/theme/shine'], function(curTheme){
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
                            require(['js/echarts/theme/blue'], function(curTheme){
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
            case 'service':{

                $httpWrapper.post({
                    url:"loadAppServices.htm",
                    success:function(data){
                        require( [
                            'echarts',
                            'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
                            'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
                        ], function (echarts) {
                            require(['js/echarts/theme/macarons'], function(curTheme){
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
                                            data : xAxisData
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
                            require(['js/echarts/theme/blue'], function(curTheme){
                                var xAxisData=[];
                                var provides=[];
                                var consumes=[];
                                for(var key in data){
                                    xAxisData.push(key);
                                    provides.push(data[key][0]);
                                    consumes.push(data[key][0]);
                                }
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
                                            categories : [
                                                {
                                                    name: '纯提供者'
                                                },
                                                {
                                                    name: '纯消费者'
                                                },
                                                {
                                                    name:'既是提供者也是消费者'
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
                                var myChart = echarts.init(document.getElementById('dependencies'));
                                myChart.setTheme(curTheme)
                                myChart.setOption(option);
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

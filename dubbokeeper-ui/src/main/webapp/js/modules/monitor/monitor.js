var monitor = angular.module("monitor",['ngRoute']);

monitor.config(function($routeProvider){
    $routeProvider.when("/admin/monitor/:application/:service/overview",{
        templateUrl:"templates/monitor/monitor-overview.html",
        controller:"monitorOverview"
    }).when("/admin/monitor/:application/:service/:method/charts",{
        templateUrl:"templates/monitor/monitor-charts.html",
        controller:"monitorCharts"
    });
});

monitor.controller("monitorCharts",function($scope,$httpWrapper,$routeParams,$breadcrumb,$menu,$interval){
    $scope.service = $routeParams.service;
    $scope.application=$routeParams.application;
    $scope.method=$routeParams.method;
    $menu.switchMenu("admin/apps");
    $breadcrumb.pushCrumb("方法"+$routeParams.service+"."+$routeParams.method+"监控室","方法"+$routeParams.service+"."+$routeParams.method+"监控室","monitor-charts");
    $scope.timeRange=100;
    var loadStatisticsData = function(){
        $httpWrapper.post({
            url:"monitor/"+$routeParams.application+"/"+$routeParams.service+"/"+$routeParams.method+"/"+$scope.timeRange+"/monitors.htm",
            success:function(statistics){
                generateElapsedOptions(statistics);
                generateConcurrentOptions(statistics);
                generateKBPS(statistics);
                generateTps(statistics);
                generateFailureAndSuccess(statistics);
                generateInputAndOutPut(statistics);
            }
        });
    }
    var generateRendingData =function(statistics,dataKeys){
        var rendingData={};
        var xAxisData =[];
        for(var i=0;i<statistics.length;i++){
            var date = new Date(statistics[i].timestamp);
            xAxisData.push(date.getDate()+"日"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());
        }
        rendingData.xAxisData=xAxisData;
        rendingData.mainData=[];
        for(var i=0;i<dataKeys.length;i++){
            var data=[];
            for(var j=0;j<statistics.length;j++){
                data.push(statistics[j][dataKeys[i]]);
            }
            rendingData.mainData.push(data);
        }
        return rendingData;
    }

    var generateElapsedOptions = function(statistics){
        require( [
            'echarts',
            'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
            'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
        ], function (echarts) {
            require(['echarts/theme/macarons'], function(curTheme){
                var rendingData = generateRendingData(statistics,["elapsed"]);
                var option = {
                    title : {
                        text: '方法耗时监控',
                        subtext: '来自应用端'
                    },
                    tooltip : {
                        trigger: 'axis'
                    },
                    legend: {
                        data:['耗时']
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
                            data : rendingData.xAxisData,
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
                            symbolSize:0,
                            name:'耗时',
                            type:'line',
                            data:rendingData.mainData[0],
                            markLine : {
                                data : [
                                    {type : 'average', name : '平均值'}
                                ]
                            },
                            itemStyle:{
                                normal:{
                                    label:{
                                        show:false
                                    }
                                }
                            }
                        }
                    ]
                };
                var myChart = echarts.init(document.getElementById('elapsed'));
                myChart.setTheme(curTheme)
                myChart.setOption(option);
            });
        });
    }

    var generateConcurrentOptions = function(statistics){
        require( [
            'echarts',
            'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
            'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
        ], function (echarts) {
            require(['echarts/theme/macarons'], function(curTheme){
                var rendingData = generateRendingData(statistics,["concurrent"]);
                var option = {
                    title : {
                        text: '方法并发监控',
                        subtext: '来自应用端'
                    },
                    tooltip : {
                        trigger: 'axis'
                    },
                    legend: {
                        data:['并发']
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
                            data : rendingData.xAxisData,
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
                            symbolSize:0,
                            name:'并发',
                            type:'line',
                            data:rendingData.mainData[0],
                            markLine : {
                                data : [
                                    {type : 'average', name : '平均值'}
                                ]
                            },
                            itemStyle:{
                                normal:{
                                    label:{
                                        show:false
                                    }
                                }
                            }
                        }
                    ]
                };
                var myChart = echarts.init(document.getElementById('concurrent'));
                myChart.setTheme(curTheme)
                myChart.setOption(option);
            });
        });
    }

    var generateTps = function(statistics){
        require( [
            'echarts',
            'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
            'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
        ], function (echarts) {
            require(['echarts/theme/macarons'], function(curTheme){
                var rendingData = generateRendingData(statistics,["tps"]);
                var option = {
                    title : {
                        text: 'TPS',
                        subtext: '来自应用端'
                    },
                    tooltip : {
                        trigger: 'axis'
                    },
                    legend: {
                        data:['TPS']
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
                            data : rendingData.xAxisData,
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
                            symbolSize:0,
                            name:'TPS',
                            type:'line',
                            data:rendingData.mainData[0],
                            markLine : {
                                data : [
                                    {type : 'average', name : '平均值'}
                                ]
                            },
                            itemStyle:{
                                normal:{
                                    label:{
                                        show:false
                                    }
                                }
                            }
                        }
                    ]
                };
                var myChart = echarts.init(document.getElementById('tps'));
                myChart.setTheme(curTheme)
                myChart.setOption(option);
            });
        });
    }
    
    var generateKBPS = function (statistics) {
        require( [
            'echarts',
            'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
            'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
        ], function (echarts) {
            require(['echarts/theme/macarons'], function(curTheme){
                var rendingData = generateRendingData(statistics,["kbps"]);
                var option = {
                    title : {
                        text: 'KBPS',
                        subtext: '来自应用端'
                    },
                    tooltip : {
                        trigger: 'axis'
                    },
                    legend: {
                        data:['KBPS']
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
                            data : rendingData.xAxisData,
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
                            symbolSize:0,
                            name:'KBPS',
                            type:'line',
                            data:rendingData.mainData[0],
                            markLine : {
                                data : [
                                    {type : 'average', name : '平均值'}
                                ]
                            },
                            itemStyle:{
                                normal:{
                                    label:{
                                        show:false
                                    }
                                }
                            }
                        }
                    ]
                };
                var myChart = echarts.init(document.getElementById('kbps'));
                myChart.setTheme(curTheme)
                myChart.setOption(option);
            });
        });
    }
    var generateInputAndOutPut = function (statistics) {
        require( [
            'echarts',
            'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
            'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
        ], function (echarts) {
            require(['echarts/theme/macarons'], function(curTheme){
                var rendingData = generateRendingData(statistics,["input","output"]);
                var option = {
                    title : {
                        text: '方法数据传输监控',
                        subtext: '来自应用端'
                    },
                    tooltip : {
                        trigger: 'axis'
                    },
                    legend: {
                        data:['输出数据','输入数据']
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
                            data : rendingData.xAxisData,
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
                            symbolSize:0,
                            name:'输出数据',
                            type:'line',
                            data:rendingData.mainData[0],
                            markLine : {
                                data : [
                                    {type : 'average', name : '平均值'}
                                ]
                            },
                            itemStyle:{
                                normal:{
                                    label:{
                                        show:false
                                    }
                                }
                            }
                        }, {
                            symbolSize:0,
                            name:'输入数据',
                            type:'line',
                            data:rendingData.mainData[1],
                            markLine : {
                                data : [
                                    {type : 'average', name : '平均值'}
                                ]
                            },
                            itemStyle:{
                                normal:{
                                    label:{
                                        show:false
                                    }
                                }
                            }
                        }
                    ]
                };
                var myChart = echarts.init(document.getElementById('input-output'));
                myChart.setTheme(curTheme)
                myChart.setOption(option);
            });
        });
    }
    var generateFailureAndSuccess = function(statistics){
        require( [
            'echarts',
            'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
            'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
        ], function (echarts) {
            require(['echarts/theme/macarons'], function(curTheme){
                var rendingData = generateRendingData(statistics,["successCount","failureCount"]);
                var option = {
                    title : {
                        text: '方法调用成功率',
                        subtext: '来自应用端'
                    },
                    tooltip : {
                        trigger: 'axis'
                    },
                    legend: {
                        data:['成功次数','失败次数']
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
                            data : rendingData.xAxisData,
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
                            symbolSize:0,
                            name:'成功次数',
                            type:'line',
                            data:rendingData.mainData[0],
                            markLine : {
                                data : [
                                    {type : 'average', name : '平均值'}
                                ]
                            },
                            itemStyle:{
                                normal:{
                                    label:{
                                        show:false
                                    }
                                }
                            }
                        }, {
                            symbolSize:0,
                            name:'失败次数',
                            type:'line',
                            data:rendingData.mainData[1],
                            markLine : {
                                data : [
                                    {type : 'average', name : '平均值'}
                                ]
                            },
                            itemStyle:{
                                normal:{
                                    label:{
                                        show:false
                                    }
                                }
                            }
                        }
                    ]
                };
                var myChart = echarts.init(document.getElementById('failure-success'));
                myChart.setTheme(curTheme)
                myChart.setOption(option);
            });
        });
    }
    loadStatisticsData();
});

monitor.controller("monitorOverview",function($scope,$httpWrapper,$routeParams,$breadcrumb,$menu,$interval){
    $menu.switchMenu("admin/apps");
    $breadcrumb.pushCrumb("查看服务"+$routeParams.service+"监控信息概要列表","查看服务"+$routeParams.service+"监控信息概要列表","monitor-overview");
    $scope.service=$routeParams.service;
    $scope.application=$routeParams.application;
    $scope.timeRange=100;
    var loadOverviewData = function(){
        $httpWrapper.post({
            url:"monitor/"+$routeParams.application+"/"+$routeParams.service+"/"+$scope.timeRange+"/monitors.htm",
            success:function(data){
                if(data.length>0){
                    $scope.details=data;
                }else{
                    $scope.isEmpty=true;
                }
            }
        });
    }
    loadOverviewData();

});

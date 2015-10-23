var monitor = angular.module("monitor",['ngRoute','lineChart']);

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
    $scope.elapsedOptions={};
    $scope.concurrentOptions={};
    $scope.tpsOptions={};
    $scope.kbpsOptions={};
    $scope.inputoutputOptions={};
    $scope.failuresuccessOptions={};
    $breadcrumb.pushCrumb("方法"+$routeParams.service+"."+$routeParams.method+"监控室","方法"+$routeParams.service+"."+$routeParams.method+"监控室","monitor-charts");
    $scope.timeRange=1000;
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
    var generateChart = function(title,subTitle,seriesConfig,xAxisData,targetDom){
        var legends=[];
        var series=[];
        for(var i=0;i<seriesConfig.length;i++){
            var seriesTpl = {
                symbolSize:0,
                name:seriesConfig[i].name,
                type:'line',
                data:seriesConfig[i].data,
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
            };
            series.push(seriesTpl);
            legends.push(seriesConfig[i].name);
        }
        require( [
            'echarts',
            'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
            'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
        ], function (echarts) {
            require(['echarts/theme/macarons'], function(curTheme){
                var option = {
                    title : {
                        text: title,
                        subtext: subTitle
                    },
                    tooltip : {
                        trigger: 'axis'
                    },
                    legend: {
                        data:legends
                    },
                    toolbox: {
                        show : true,
                        feature : {
                            magicType : {show: true, type: ['line', 'bar']},
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
                    series : series
                };
                var myChart = echarts.init(targetDom);
                myChart.setTheme(curTheme)
                myChart.setOption(option);
            });
        });
    }
    var generateElapsedOptions = function(statistics){
        var rendingData = generateRendingData(statistics,["elapsed"]);
        var options = {};
        options.title="方法耗时监控";
        options.subTitle="来自应用端";
        options.seriesConfig=[{name:'耗时(ms)',data:rendingData.mainData[0]}];
        options.xAxis=rendingData.xAxisData;
        $scope.elapsedOptions=options;
    }

    var generateConcurrentOptions = function(statistics){
        var rendingData = generateRendingData(statistics,["concurrent"]);
        var options = {};
        options.title="方法并发监控";
        options.subTitle="来自应用端";
        options.seriesConfig=[{name:'并发',data:rendingData.mainData[0]}];
        options.xAxis=rendingData.xAxisData;
        $scope.concurrentOptions=options;
    }

    var generateTps = function(statistics){
        var rendingData = generateRendingData(statistics,["tps"]);
        var options = {};
        options.title="TPS";
        options.subTitle="来自应用端";
        options.seriesConfig=[{name:'TPS',data:rendingData.mainData[0]}];
        options.xAxis=rendingData.xAxisData;
        $scope.tpsOptions=options;
    }
    
    var generateKBPS = function (statistics) {
        var rendingData = generateRendingData(statistics,["kbps"]);
        var options = {};
        options.title="KBPS";
        options.subTitle="来自应用端";
        options.seriesConfig=[{name:'KBPS',data:rendingData.mainData[0]}];
        options.xAxis=rendingData.xAxisData;
        $scope.kbpsOptions=options;
    }
    var generateInputAndOutPut = function (statistics) {
        var rendingData = generateRendingData(statistics,["input","output"]);
        var options = {};
        options.title="方法数据传输监控";
        options.subTitle="来自应用端";
        options.seriesConfig=[{name:'输出数据',data:rendingData.mainData[0]},{name:'输入数据',data:rendingData.mainData[1]}];
        options.xAxis=rendingData.xAxisData;
        $scope.inputoutputOptions=options;
        //generateChart('方法数据传输监控','来自应用端',[{name:'输出数据',data:rendingData.mainData[0]},{name:'输入数据',data:rendingData.mainData[1]}],rendingData.xAxisData,document.getElementById('input-output'));
    }
    var generateFailureAndSuccess = function(statistics){
        var rendingData = generateRendingData(statistics,["successCount","failureCount"]);
        var options = {};
        options.title="方法调用成功率";
        options.subTitle="来自应用端";
        options.seriesConfig=[{name:'成功次数',data:rendingData.mainData[0]},{name:'失败次数',data:rendingData.mainData[1]}];
        options.xAxis=rendingData.xAxisData;
        $scope.failuresuccessOptions=options;
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

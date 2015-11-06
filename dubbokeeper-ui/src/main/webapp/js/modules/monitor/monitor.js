var monitor = angular.module("monitor",['ngRoute','lineChart','isteven-multi-select']);

monitor.config(function($routeProvider){
    $routeProvider.when("/admin/monitor/:application/:service/overview",{
        templateUrl:"templates/monitor/monitor-overview.html",
        controller:"monitorOverview"
    }).when("/admin/monitor/:application/:service/:method/charts",{
        templateUrl:"templates/monitor/monitor-charts.html",
        controller:"monitorCharts"
    }).when("/monitor",{
        templateUrl:"templates/monitor/index.html",
        controller:"index"
    });
});

monitor.controller("index",function($scope,$httpWrapper,$routeParams,$breadcrumb,$menu){
    $menu.switchBarOnly("monitor");
    var oneDay=24*60*60*1000;
    $scope.dayRange=30;
    $httpWrapper.post({
        url:"monitor/index.htm",
        success:function(data){
            if(data){
                $scope.appOptions = [];
                for(var i=0;i<data.length;i++){
                    var option = {};
                    option.name=data[i];
                    option.ticked=true;
                    option.elapsed={};
                    option.concurrent={};
                    option.fault={};
                    option.success={};
                    if(i==0){
                        option.show=true;
                        loadApplicationOverview(option);
                    }
                    option.showType='chart';
                    $scope.appOptions.push(option);
                }
                $scope.applications = $scope.appOptions;
            }
        }
        }
    );
    $scope.switchView = function(app,type){
        app.showType=type;
    }
    var loadApplicationOverview=function(app){
        $httpWrapper.post({
            url:"monitor/"+app.name+"/"+$scope.dayRange+"/overview.htm",
            success:function(data){
                app.overview=data;
                generateOptions(app,data.elapsedItems,'elapsed','耗时(ms)');
                generateOptions(app,data.concurrentItems,'concurrent','并发');
                generateOptions(app,data.faultItems,'fault','失败次数');
                generateOptions(app,data.successItems,'success','成功次数');
            }
        })
    }

    var generateOptions=function(app,values,prop,name){
        var options = {};
        if(!values){
            app[prop]=options;
            return;
        }
        options.title="在"+$scope.dayRange+"天内的"+name+"TOP200";
        options.subTitle="来自应用端";
        options.seriesConfig=[{name:name,data:generateMainData(values,prop)}];
        options.xAxis=generateXAxisData(values);
        app[prop]=options;
    }

    var generateMainData = function(items,prop){
        var values = [];
        for(var i=0;i<items.length;i++){
            values.push(items[i][prop]);
        }
        return values;
    }
    var generateXAxisData=function(items){
        var xAxisData = [];
        for(var i=0;i<items.length;i++){
            var date = new Date(items[i].timestamp);
            var item = items[i].service+"."+items[i].method+"于"+date.getDate()+"日"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()
            xAxisData.push(item);
        }
        return xAxisData;
    }
    $scope.toggleAppCharts=function(app){
        if(app.show){
            app.show=false;
        }else{
            loadApplicationOverview(app);
            app.show=true;
        }
    }
    $scope.switchTimeRange=function(dayRange){
        $scope.dayRange=dayRange;
        for(var i=0;i<$scope.applications.length;i++){
            if($scope.applications[i].show){
                loadApplicationOverview($scope.applications[i]);
            }
        }

    }
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
    $scope.pipOptions={};
    $scope.timeRange={};
    var currentDate = new Date();
    $scope.timeRange.startTime= currentDate.getTime()-60*60*1000;
    $scope.timeRange.endTime=currentDate.getTime();
    $scope.statMsg="准备加载数据...";
    $breadcrumb.pushCrumb("方法"+$routeParams.service+"."+$routeParams.method+"监控室","方法"+$routeParams.service+"."+$routeParams.method+"监控室","monitor-charts");
    var loadStatisticsData = function(){
        $scope.statMsg="正在查询....";
        $httpWrapper.post({
            url:"monitor/"+$routeParams.application+"/"+$routeParams.service+"/"+$routeParams.method+"/"+$scope.timeRange.startTime+"-"+$scope.timeRange.endTime+"/monitors.htm",
            success:function(statistics){
                generateElapsedOptions(statistics.statisticsCollection);
                generateConcurrentOptions(statistics.statisticsCollection);
                generateKBPS(statistics.statisticsCollection);
                generateTps(statistics.statisticsCollection);
                generateFailureAndSuccess(statistics.statisticsCollection);
                generateInputAndOutPut(statistics.statisticsCollection);
                generateUsagePipe(statistics.usageCollection);
                intervalLoadStatistics();
                $scope.statMsg="暂停查询";
            }
        });
    }
    $scope.$watch("timeRange",function(){
        clearTimeout($scope.intervalLoad);
        loadStatisticsData();
    });
    var generateUsagePipe=function(usages){
        var options = {};
        options.title="方法调用情况统计";
        options.name="方法调用情况统计";
        var keys = [];
        var dataset = [];
        for(var i=0;i<usages.length;i++){
            keys.push(usages[i].remoteAddress);
            var item={};
            item.name=usages[i].remoteAddress;
            item.value=usages[i].count;
            dataset.push(item);
        }
        options.keys=keys;
        options.dataset=dataset;
        $scope.pipOptions=options;
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
    $scope.stopInterval=function(){
        if($scope.intervalLoad){
            clearTimeout($scope.intervalLoad);
        }
        $scope.statMsg="已暂停查询";
    }
    var intervalLoadStatistics = function(){
        $scope.intervalLoad = setTimeout(function(){
            loadStatisticsData();
        },10000);
    }

});

monitor.controller("monitorOverview",function($scope,$httpWrapper,$routeParams,$breadcrumb,$menu,$interval){
    $menu.switchMenu("admin/apps");
    $breadcrumb.pushCrumb("查看服务"+$routeParams.service+"监控信息概要列表","查看服务"+$routeParams.service+"监控信息概要列表","monitor-overview");
    $scope.service=$routeParams.service;
    $scope.application=$routeParams.application;
    $scope.timeRange={};
    var currentDate = new Date();
    $scope.timeRange.startTime= currentDate.getTime()-60*60*1000;
    $scope.timeRange.endTime=currentDate.getTime();
    $scope.statMsg="准备加载数据...";
    var loadOverviewData = function(){
        $scope.statMsg="正在查询....";
        $httpWrapper.post({
            url:"monitor/"+$routeParams.application+"/"+$routeParams.service+"/"+$scope.timeRange.startTime+"-"+$scope.timeRange.endTime+"/monitors.htm",
            success:function(data){
                if(data.length>0){
                    $scope.details=data;
                }else{
                    $scope.isEmpty=true;
                }
                $scope.statMsg="暂停查询";
                intervalLoad();
            }
        });
    }
    loadOverviewData();
    $scope.$watch("timeRange",function(){
        clearTimeout($scope.loadTimeout);
        loadOverviewData();
    });
    $scope.stopInterval=function(){
        if($scope.loadTimeout){
            clearTimeout($scope.loadTimeout);
        }
        $scope.statMsg="已暂停查询";
    }
    var intervalLoad= function(){
        $scope.loadTimeout =  setTimeout(function(){
            loadOverviewData();
        },10000);
    }


});

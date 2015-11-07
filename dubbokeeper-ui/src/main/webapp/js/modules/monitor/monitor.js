var monitor = angular.module("monitor",['ngRoute','lineChart','breadCrumb','isteven-multi-select']);

monitor.config(function($routeProvider){
    $routeProvider.when("/monitor/:application/:service/overview",{
        templateUrl:"templates/monitor/monitor-overview.html",
        controller:"monitorOverview"
    }).when("/monitor/:application/:service/:method/charts",{
        templateUrl:"templates/monitor/monitor-charts.html",
        controller:"monitorCharts"
    }).when("/monitor/:application/index",{
        templateUrl:"templates/monitor/application-index.html",
        controller:"applicationOverview"
    }).when("/monitor",{
        templateUrl:"templates/monitor/index.html",
        controller:"index"
    });
});

monitor.controller("applicationOverview",function($scope,$httpWrapper,$routeParams,$breadcrumb,$menu){
    $menu.switchMenu("monitor/index");
    var oneDay=24*60*60*1000;
    $scope.app=$routeParams.application;
    $breadcrumb.pushCrumb("应用"+$scope.app+"监控大盘","应用"+$scope.app+"监控大盘","monitor-applicationOverview");
    $scope.dayRange=1;
    $httpWrapper.post({
            url:"monitor/"+$scope.app+"/services.htm",
            success:function(data){
                if(data){
                    $scope.serviceOptions = [];
                    for(var i=0;i<data.length;i++){
                        var option = {};
                        option.name=data[i].name;
                        option.remoteType=data[i].remoteType;
                        option.ticked=true;
                        option.elapsed={};
                        option.concurrent={};
                        option.fault={};
                        option.success={};
                        if(i==0){
                            option.show=true;
                            loadServiceOverview(option);
                        }
                        option.showType='chart';
                        $scope.serviceOptions.push(option);
                    }
                    $scope.services = $scope.serviceOptions;
                }
            }
        }
    );
    $scope.switchView = function(service,type){
        service.showType=type;
    }
    var loadServiceOverview=function(service){
        $httpWrapper.post({
            url:"monitor/"+$scope.app+"/"+service.name+"/"+$scope.dayRange+"/overview.htm",
            success:function(data){
                service.overview=data;
                generateOptions(service,data.elapsedItems,'elapsed','耗时(ms)',$scope.dayRange);
                generateOptions(service,data.concurrentItems,'concurrent','并发',$scope.dayRange);
                generateOptions(service,data.faultItems,'fault','失败次数',$scope.dayRange);
                generateOptions(service,data.successItems,'success','成功次数',$scope.dayRange);
            }
        })
    }
    $scope.toggleAppCharts=function(service){
        if(service.show){
            service.show=false;
        }else{
            loadServiceOverview(service);
            service.show=true;
        }
    }
    $scope.switchTimeRange=function(dayRange){
        $scope.dayRange=dayRange;
        for(var i=0;i<$scope.services.length;i++){
            if($scope.services[i].show){
                loadServiceOverview($scope.services[i]);
            }
        }

    }
})

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
        var item = items[i].method+"于"+date.getDate()+"日"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()
        xAxisData.push(item);
    }
    return xAxisData;
}
var generateOptions=function(app,values,prop,name,dayRange){
    var options = {};
    if(!values){
        app[prop]=options;
        return;
    }
    options.title="在"+dayRange+"天内的"+name+"TOP200";
    options.subTitle="来自应用端";
    options.seriesConfig=[{name:name,data:generateMainData(values,prop)}];
    options.xAxis=generateXAxisData(values);
    app[prop]=options;
}

monitor.controller("index",function($scope,$httpWrapper,$routeParams,$breadcrumb,$menu){
    $menu.switchMenu("monitor/index");
    $breadcrumb.pushCrumb("监控大盘","监控大盘","monitor-index");
    var oneDay=24*60*60*1000;
    $scope.dayRange=1;
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
                generateOptions(app,data.elapsedItems,'elapsed','耗时(ms)',$scope.dayRange);
                generateOptions(app,data.concurrentItems,'concurrent','并发',$scope.dayRange);
                generateOptions(app,data.faultItems,'fault','失败次数',$scope.dayRange);
                generateOptions(app,data.successItems,'success','成功次数',$scope.dayRange);
            }
        })
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

monitor.controller("monitorCharts",function($scope,$rootScope,$httpWrapper,$routeParams,$breadcrumb,$menu,$interval){
    $scope.service = $routeParams.service;
    $scope.application=$routeParams.application;
    $scope.method=$routeParams.method;
    $menu.switchMenu("monitor/index");
    $breadcrumb.pushCrumb("应用"+$scope.application+"下服务"+$scope.service+"."+$scope.method+"监控大盘","应用"+$scope.application+"下服务"+$scope.service+"."+$scope.method+"监控大盘","monitor-monitorCharts");
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
    var loadStatisticsData = function(){
        if(!$routeParams.application){
            $scope.stopInterval();
            return;
        }
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
                intervalLoadStatistics();
                $scope.statMsg="暂停查询";
            }
        });
    }
    $scope.$watch("timeRange",function(){
        clearTimeout($scope.intervalLoad);
        loadStatisticsData();
    });
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

    $scope.$on('$destroy',function(){
        $scope.stopInterval();
    })
});

monitor.controller("monitorOverview",function($scope,$httpWrapper,$routeParams,$breadcrumb,$menu,$interval){
    $menu.switchMenu("monitor/index");
    $scope.service=$routeParams.service;
    $scope.application=$routeParams.application;
    $breadcrumb.pushCrumb("应用"+$scope.application+"下服务"+$scope.service+"基本监控信息","应用"+$scope.application+"下服务"+$scope.service+"基本监控信息","monitor-monitorOverview");
    $scope.timeRange={};
    var currentDate = new Date();
    $scope.timeRange.startTime= currentDate.getTime()-60*60*1000;
    $scope.timeRange.endTime=currentDate.getTime();
    $scope.statMsg="准备加载数据...";
    var loadOverviewData = function(){
        $scope.statMsg="正在查询....";
        if(!$routeParams.application){
            $scope.stopInterval();
            return;
        }
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

    $scope.$on('$destroy',function(){
        $scope.stopInterval();
    })
});

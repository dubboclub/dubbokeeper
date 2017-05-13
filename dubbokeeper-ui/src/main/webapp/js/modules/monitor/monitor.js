var monitor = angular.module("monitor",['ngRoute','fullScreen','lineChart','breadCrumb','isteven-multi-select']);

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
    }).when("/monitor/:application/:dayRange/top200.html",{
        templateUrl:"templates/monitor/application-top200.html",
        controller:"applicationTop200"
    }).when("/monitor/:application/:service/:dayRange/service/top200.html",{
        templateUrl:"templates/monitor/service-top200.html",
        controller:"serviceTop200"
    });
});
monitor.controller("serviceTop200",function($scope,$httpWrapper,$routeParams,$breadcrumb,$menu){
    $menu.switchMenu("monitor/index");
    var oneDay=24*60*60*1000;
    $scope.app=$routeParams.application;
    $scope.name = $routeParams.service;
    $scope.dayRange=$routeParams.dayRange;
    $breadcrumb.pushCrumb("服务"+$scope.name+"TOP200","服务"+$scope.name+"TOP200","monitor-serviceTop200");
    $scope.service={};
    $scope.service.name=$scope.name;
    $scope.service.elapsed={};
    $scope.service.concurrent={};
    $scope.service.fault={};
    $scope.service.success={};
    $scope.showType='chart';
    var loadServiceOverview=function(){
        $httpWrapper.post({
            url:"monitor/"+$scope.app+"/"+ $scope.name+"/"+$scope.dayRange+"/overview.htm",
            success:function(data){
                $scope.service.overview=data;
                generateOptions($scope.service,data.elapsedItems,'elapsed','耗时(ms)',$scope.dayRange);
                generateOptions($scope.service,data.concurrentItems,'concurrent','并发',$scope.dayRange);
                generateOptions($scope.service,data.faultItems,'fault','失败次数',$scope.dayRange);
                generateOptions($scope.service,data.successItems,'success','成功次数',$scope.dayRange);
            }
        })
    }
    $scope.switchView = function(type){
        $scope.showType=type;
    }
    loadServiceOverview();
    $scope.switchTimeRange=function(dayRange){
        $scope.dayRange=dayRange;
        loadServiceOverview();
    }

});
monitor.controller("applicationTop200",function($scope,$httpWrapper,$routeParams,$breadcrumb,$menu){
    $menu.switchMenu("monitor/index");
    var oneDay=24*60*60*1000;
    $scope.name=$routeParams.application;
    $scope.dayRange=$routeParams.dayRange;
    $breadcrumb.pushCrumb("应用"+$scope.name+"TOP200","应用"+$scope.name+"TOP200","monitor-applicationTop200");
    $scope.app={};
    $scope.app.name=$scope.name;
    $scope.app.elapsed={};
    $scope.app.concurrent={};
    $scope.app.fault={};
    $scope.app.success={};
    $scope.showType='chart';
    var loadApplicationOverview=function(){
        $httpWrapper.post({
            url:"monitor/"+$scope.name+"/"+$scope.dayRange+"/overview.htm",
            success:function(data){
                $scope.app.overview=data;
                generateOptions($scope.app,data.elapsedItems,'elapsed','耗时(ms)',$scope.dayRange);
                generateOptions($scope.app,data.concurrentItems,'concurrent','并发',$scope.dayRange);
                generateOptions($scope.app,data.faultItems,'fault','失败次数',$scope.dayRange);
                generateOptions($scope.app,data.successItems,'success','成功次数',$scope.dayRange);
            }
        })
    }
    $scope.switchView = function(type){
        $scope.showType=type;
    }
    loadApplicationOverview();
    $scope.switchTimeRange=function(dayRange){
        $scope.dayRange=dayRange;
        loadApplicationOverview();
    }
})

monitor.controller("applicationOverview",function($scope,$httpWrapper,$routeParams,$breadcrumb,$menu){
    $menu.switchMenu("monitor/index");
    var oneDay=24*60*60*1000;
    $scope.app=$routeParams.application;
    $breadcrumb.pushCrumb("应用"+$scope.app+"监控大盘","应用"+$scope.app+"监控大盘","monitor-applicationOverview");
    $scope.dayRange=1;
    var queryServiceInfo = function(){
        $httpWrapper.post({
                url:"monitor/"+$scope.app+"/"+$scope.dayRange+"/services.htm",
                success:function(data){
                    if(data){
                        $scope.serviceOptions = [];
                        for(var i=0;i<data.length;i++){
                            var option = {};
                            option.name=data[i].name;
                            option.remoteType=data[i].remoteType;
                            option.maxConcurrent = data[i].maxConcurrent;
                            option.maxElapsed = data[i].maxElapsed;
                            option.maxFault = data[i].maxFault;
                            option.maxSuccess = data[i].maxSuccess;
                            option.remoteType=data[i].remoteType;
                            option.ticked=true;
                            option.elapsed={};
                            option.concurrent={};
                            option.fault={};
                            option.success={};
                            option.show=true;
                            option.showType='chart';
                            $scope.serviceOptions.push(option);
                        }
                        var providers=[];
                        var consumers=[];
                        for(var i=0;i<$scope.serviceOptions.length;i++){
                            if($scope.serviceOptions[i].remoteType=='CONSUMER'){
                                providers.push($scope.serviceOptions[i]);
                            }else{
                                consumers.push($scope.serviceOptions[i]);
                            }
                        }

                        var services =[];
                        services.push({
                            name: '<strong>所有服务</strong>',
                            serviceGroup: true
                        });
                        if(providers.length>0){
                            services.push({
                                name: '<strong>提供的服务</strong>',
                                serviceGroup: true
                            });
                            services=services.concat(providers);
                            services.push({
                                serviceGroup: false
                            });
                        }
                        if(consumers.length>0){
                            services.push({
                                name: '<strong>消费的服务</strong>',
                                serviceGroup: true
                            });
                            services=services.concat(consumers);
                            services.push({
                                serviceGroup: false
                            });
                        }
                        services.push({
                            serviceGroup: false
                        });
                        $scope.serviceGroup = services;
                        $scope.services = $scope.serviceOptions;
                    }
                }
            }
        );
    }
    queryServiceInfo();
    $scope.switchView = function(service,type){
        service.showType=type;
    }
    $scope.switchTimeRange=function(dayRange){
        $scope.dayRange=dayRange;
        queryServiceInfo();
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
    $breadcrumb.pushCrumb("监控大盘","监控大盘","monitor/index");
    var oneDay=24*60*60*1000;
    $scope.dayRange=1;
    $scope.fullScreenChange=function(isFull){
        console.log(isFull);
    }
    $httpWrapper.post({
        url:"monitor/index.htm",
        success:function(data){
            if(data){
                $scope.appOptions = [];
                for(var i=0;i<data.length;i++){
                    var option = {};
                    option.name=data[i].applicationName;
                    option.maxConcurrent = data[i].maxConcurrent;
                    option.maxFault = data[i].maxFault;
                    option.maxElapsed = data[i].maxElapsed;
                    option.maxSuccess = data[i].maxSuccess;
                    option.applicationType = data[i].applicationType;
                    option.ticked=true;
                    option.elapsed={};
                    option.concurrent={};
                    option.fault={};
                    option.success={};
                    option.show=true;
                    $scope.appOptions.push(option);
                }
                var consumers = [];
                var providers = [];
                var consumerAndProvider = [];
                for(var i=0;i<$scope.appOptions.length;i++){
                    if($scope.appOptions[i].applicationType==1){
                        providers.push($scope.appOptions[i]);
                    }else if($scope.appOptions[i].applicationType==0){
                        consumers.push($scope.appOptions[i]);
                    }else{
                        consumerAndProvider.push($scope.appOptions[i]);
                    }
                }
                var apps = [];
                apps.push({
                    name: '<strong>所有应用</strong>',
                    appGroup: true
                });
                if(consumerAndProvider.length>0){
                    apps.push({
                        name: '<strong>即是消费者也是提供者</strong>',
                        appGroup: true
                    });
                    apps=apps.concat(consumerAndProvider);
                    apps.push({
                        appGroup: false
                    });
                }
                if(providers.length>0){
                    apps.push({
                        name: '<strong>提供者</strong>',
                        appGroup: true
                    });
                    apps=apps.concat(providers);
                    apps.push({
                        appGroup: false
                    });
                }
                if(consumers.length>0){
                    apps.push({
                        name: '<strong>消费者</strong>',
                        appGroup: true
                    });
                    apps=apps.concat(consumers);
                    apps.push({
                        appGroup: false
                    });
                }
                apps.push({
                    appGroup: false
                });
                $scope.multiOptions = apps;
                $scope.applications = $scope.appOptions;
            }
        }
        }
    );

   /* var loadApplicationOverview=function(app){
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
    }*/
    var loadApplicationInfo = function(app){
        $httpWrapper.post({
            url:"monitor/"+app.name+"/"+$scope.dayRange+"/info.htm",
            success:function(data){
                app.maxConcurrent=data.maxConcurrent;
                app.maxElapsed = data.maxElapsed;
                app.maxSuccess = data.maxSuccess;
                app.maxFault = data.maxFault;
            }
        })
    }



    $scope.switchTimeRange=function(dayRange){
        $scope.dayRange=dayRange;
        for(var i=0;i<$scope.applications.length;i++){
            if($scope.applications[i].show){
                loadApplicationInfo($scope.applications[i]);
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
    $scope.statMsg="查询实时数据";
    $scope.fullScreenChange=function(){
        if($scope.realTimeQuery){
            realtimeStatisticsData();
        }else{
            loadStatisticsData();
        }
    }
    var realtimeStatisticsData = function(){
        $scope.realTimeQuery=true;
        $httpWrapper.post({
            url:"monitor/"+$routeParams.application+"/"+$routeParams.service+"/"+$routeParams.method+"/now.htm",
            success:function(statistics){
                if(!$scope.intervalLoad){
                    return;
                }
                var statisticsCollection = _quickSort(statistics.statisticsCollection,'timestamp')
                generateElapsedOptions(statisticsCollection);
                generateConcurrentOptions(statisticsCollection);
                generateKBPS(statisticsCollection);
                generateTps(statisticsCollection);
                generateFailureAndSuccess(statisticsCollection);
                generateInputAndOutPut(statisticsCollection);
                intervalLoadStatistics();
            }
        });
    }
    $scope.realTimeQuery=false;

    var loadStatisticsData = function(){
        $scope.realTimeQuery=false;
        $httpWrapper.post({
            url:"monitor/"+$routeParams.application+"/"+$routeParams.service+"/"+$routeParams.method+"/"+$scope.timeRange.startTime+"-"+$scope.timeRange.endTime+"/monitors.htm",
            success:function(statistics){
                var statisticsCollection = _quickSort(statistics.statisticsCollection,'timestamp')
                generateElapsedOptions(statisticsCollection);
                generateConcurrentOptions(statisticsCollection);
                generateKBPS(statisticsCollection);
                generateTps(statisticsCollection);
                generateFailureAndSuccess(statisticsCollection);
                generateInputAndOutPut(statisticsCollection);
            }
        });
    }
    $scope.$watch("timeRange",function(){
        $scope.stopInterval();
        loadStatisticsData();
    });
    $scope.interval=10000;
    var loadInterval=function(){
        $httpWrapper.post({
            url:"monitor//load-interval.htm",
            success:function(interval){
                $scope.interval=interval;
            }
        });
    }
    loadInterval();
    var generateRendingData =function(statistics,dataKeys){
        var rendingData={};
        var xAxisData =[];
        for(var i=0;i<statistics.length;i++){
            var date = new Date(statistics[i].timestamp);
            xAxisData.push((date.getMonth()+1)+"月"+date.getDate()+"日"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());
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
     var _quickSort=function(array,key){
        if(array.length==0){
            return [];
        }
        var left=[];
        var right=[];
        var pivotIndex=Math.floor(array.length / 2);
        var pivot=array[pivotIndex][key];
        for(var i=0;i<array.length;i++){
            if(array[i][key]>pivot){
                right.push(array[i]);
            }else if(array[i][key]<pivot){
                left.push(array[i]);
            }
        }
        return _quickSort(left,key).concat(array[pivotIndex],_quickSort(right,key))

    };
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
    $scope.stopInterval=function(){
        if($scope.intervalLoad){
            clearTimeout($scope.intervalLoad);
            $scope.intervalLoad=undefined;
            $scope.statMsg="查询实时数据";
        }
    }
    var intervalLoadStatistics = function(){
        $scope.statMsg="暂停实时查询";
        $scope.intervalLoad = setTimeout(function(){
            realtimeStatisticsData();
        },$scope.interval);
    }
    $scope.queryRealTimeData=function(){
        if($scope.intervalLoad){
            $scope.stopInterval();
        }else{
            intervalLoadStatistics();
        }
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
    $scope.statMsg="查询实时数据";
    $scope.interval=10000;
    var loadInterval=function(){
        $httpWrapper.post({
            url:"monitor//load-interval.htm",
            success:function(interval){
                $scope.interval=interval;
            }
        });
    }
    loadInterval();
    var loadOverviewDataRealTime = function(){
        $httpWrapper.post({
            url:"monitor/"+$routeParams.application+"/"+$routeParams.service+"/now.htm",
            success:function(data){
                if(data.length>0){
                    $scope.details=data;
                }else{
                    $scope.isEmpty=true;
                }
                intervalLoad();
            }
        });
    }
    var loadOverviewData = function(){
        $scope.stopInterval();
        $httpWrapper.post({
            url:"monitor/"+$routeParams.application+"/"+$routeParams.service+"/"+$scope.timeRange.startTime+"-"+$scope.timeRange.endTime+"/monitors.htm",
            success:function(data){
                if(data.length>0){
                    $scope.details=data;
                }else{
                    $scope.isEmpty=true;
                }
            }
        });
    }

    $scope.$watch("timeRange",function(){
        clearTimeout($scope.loadTimeout);
        loadOverviewData();
    });
    $scope.stopInterval=function(){
        if($scope.loadTimeout){
            clearTimeout($scope.loadTimeout);
            $scope.loadTimeout=undefined;
            $scope.statMsg="查询实时数据";
        }
    }
    var intervalLoad= function(){
        $scope.statMsg="暂停实时查询";
        $scope.loadTimeout =  setTimeout(function(){
            loadOverviewDataRealTime();
        },$scope.interval);
    }
    $scope.realTime=function(){
        if($scope.loadTimeout){
            $scope.stopInterval();
        }else{
            loadOverviewDataRealTime();
        }
    }

    $scope.$on('$destroy',function(){
        $scope.stopInterval();
    })
    loadOverviewData();
});

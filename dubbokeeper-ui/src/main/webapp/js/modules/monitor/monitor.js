var monitor = angular.module("monitor",['ngRoute']);

monitor.config(function($routeProvider){
    $routeProvider.when("/monitor/elapsed",{
        templateUrl:"templates/monitor/monitor.html",
        controller:"monitorElapsed"
    }).when("/monitor/elapsed/:service",{
        templateUrl:"templates/monitor/monitor.html",
        controller:"monitorElapsed"
    });
});
monitor._generateElapsedOption = function(data){
	var xAxis = [];
	var elapsed = [];
	var maxElapsed = [];
	angular.forEach(data, function(item){
		var timestamp = new Date();
		timestamp.setTime(item.timestamp);
		xAxis.push(timestamp.toLocaleTimeString().replace(/^\D*/,''));
		
		elapsed.push(item.elapsed);
		maxElapsed.push(item.maxElapsed);
	});
	var option = {
	    title : {
	        text: '服务调用耗时',
	        subtext: '单位(ms)'
	    },
	    tooltip : {
	        trigger: 'axis'
	    },
	    legend: {
	        data:['平均耗时', '最大耗时']
	    },
	    toolbox: {
	        show : true,
	        feature : {
	            mark : {show: true},
	            dataView : {show: true, readOnly: false},
	            magicType : {show: true, type: ['line', 'bar']},
	            restore : {show: true},
	            saveAsImage : {show: true}
	        }
	    },
	    dataZoom : {
	        show : false,
	        start : 0,
	        end : 100
	    },
	    xAxis : [
	        {
	            type : 'category',
	            boundaryGap : true,
	            data : xAxis
	        }
	    ],
	    yAxis : [
	        {
	            type : 'value',
	            scale: true,
	            name : '时间 (ms)',
	            boundaryGap: [0.2, 0.2]
	        }
	    ],
	    series : [
	        {
	            name:'平均耗时',
	            type:'line',
	            data:elapsed
	        },
	        {
	            name:'最大耗时',
	            type:'line',
	            data:maxElapsed
	        }
	    ]
	};
	return option;
}
monitor.controller("monitorElapsed",function($scope,$httpWrapper,$routeParams,$breadcrumb,$menu,$interval){
    $menu.switchMenu("monitor/elapsed");
    $breadcrumb.pushCrumb($routeParams.address,"查看服务"+$routeParams.service+"的执行耗时","monitor/elapsed");
    var dataUrl = "monitor/" + $routeParams.service + "/monitors.htm";
    var stop;
    var lastTimestamp;
    $scope.refresh = function() {
        // Don't start a new refresh if we are already refreshing
        if ( angular.isDefined(stop) ) return;
        stop = $interval(function() {
        	$httpWrapper.post({
                url: dataUrl,
                data: {lastTimestamp: lastTimestamp},
                success:function(data){
                	if(data.length == 0){
                		return;
                	}
            		lastTimestamp = data[data.length -1].timestamp;
                	var xAxis = [];
                	var elapsed = [];
                	var maxElapsed = [];
                	angular.forEach(data, function(item){
                		var timestamp = new Date();
                		timestamp.setTime(item.timestamp);
                		xAxis.push(timestamp.toLocaleTimeString().replace(/^\D*/,''));
                		
                		elapsed.push(item.elapsed);
                		maxElapsed.push(item.maxElapsed);
                	});
                	var i = 0;
                	while(i < elapsed.length){
	                	$scope.chart.addData([
	                	                      [
	                	                       0,        // 系列索引
	                	                       elapsed[i], // 新增数据
	                	                       false,     // 新增数据是否从队列头部插入
	                	                       true     // 是否增加队列长度，false则自定删除原有数据，队头插入删队尾，队尾插入删队头
	                	                       ],
	                	                       [
	                	                        1,        // 系列索引
	                	                        maxElapsed[i], // 新增数据
	                	                        false,    // 新增数据是否从队列头部插入
	                	                        true,    // 是否增加队列长度，false则自定删除原有数据，队头插入删队尾，队尾插入删队头
	                	                        xAxis[i]  // 坐标轴标签
	                	                        ]
	                	                      ]);
	                	i++;
                	}
            	}
            });
        }, 2000);
    };
    $scope.stopRefresh = function() {
    	if (angular.isDefined(stop)) {
    		$interval.cancel(stop);
    		stop = undefined;
	    }
	};
	$scope.$on('$destroy', function() {
		// Make sure that the interval is destroyed too
        $scope.stopRefresh();
    });
    $httpWrapper.post({
        url: dataUrl,
        success:function(data){
        	if(data.lenght > 0){
        		lastTimpstamp = data[data.length -1].timpstamp;
        	}
        	
            require( [
                'echarts',
                'echarts/chart/line', 
                'echarts/chart/bar'
            ], function (echarts) {
                require(['echarts/theme/shine'], function(curTheme){
                	var option = monitor._generateElapsedOption(data);
            		$scope.chart = echarts.init(document.getElementById('elapsed'));
            		$scope.chart.setTheme(curTheme)
                    $scope.chart.setOption(option);
            		$scope.refresh();
                });
            });
        }
    });
});

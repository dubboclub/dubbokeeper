var lineChart = angular.module('lineChart', []);
lineChart.directive('pipChart',function(){
    return {
        restrict:"EA",
        scope: {
            chartOptions:"=chartOptions"
        },
        replace:true,
        template:"<div  class=\"col-md-12 col-sm-12 col-xs-12 col-lg-12\" style=\"height: 300px;text-align: center;\">暂时没有数据展示</div>",
        link:function($scope,element){
            $scope.$watch("chartOptions",function(){
                if(!$scope.chartOptions.dataset||$scope.chartOptions.dataset.length<=0){
                    if($scope.myChart){
                        try{
                            $scope.myChart.dispose();
                        }catch(e){

                        }
                        $scope.myChart=undefined;
                    }
                    return ;
                }
                var option = {
                    title : {
                        text: $scope.chartOptions.title,
                        x:'center'
                    },
                    tooltip : {
                        trigger: 'item',
                        formatter: "{a} <br/>{b} : {c} ({d}%)"
                    },
                    legend: {
                        orient : 'vertical',
                        x : 'left',
                        data:$scope.chartOptions.keys
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
                            name:$scope.chartOptions.name,
                            type:'pie',
                            radius : '55%',
                            center: ['50%', '60%'],
                            data:$scope.chartOptions.dataset
                        }
                    ]
                };
                require( [
                    'echarts',
                    'echarts/chart/pie',// 使用柱状图就加载bar模块，按需加载
                    'echarts/chart/funnel' // 使用柱状图就加载bar模块，按需加载
                ], function (echarts) {
                    require(['echarts/theme/macarons'], function(curTheme){
                        $scope.myChart = echarts.init(element.get(0));
                        $scope.myChart.setTheme(curTheme);
                        $scope.myChart.setOption(option);
                    })
                });
            });
        }
    }
});
lineChart.directive('lineChart', function() {
    return {
        restrict:"EA",
        scope: {
            chartOptions:"=chartOptions"
        },
        replace:true,
        template:"<div  class=\"col-md-12 col-sm-12 col-xs-12 col-lg-12\" style=\"height: 300px; text-align: center;\">暂时没有数据展示</div>",
        link:function($scope,element){
            $scope.$watch("chartOptions",function(){
                if(!$scope.chartOptions.seriesConfig||$scope.chartOptions.seriesConfig.length<=0){
                    if($scope.myChart){
                       try{
                           $scope.myChart.dispose();
                       }catch(e){
                           //do nothing
                       }
                        $scope.myChart=undefined;
                    }
                    return ;
                }
                var legends=[];
                var series=[];
                for(var i=0;i<$scope.chartOptions.seriesConfig.length;i++){
                    if(!$scope.chartOptions.seriesConfig[i].data||$scope.chartOptions.seriesConfig[i].data.length<=0){
                        if($scope.myChart){
                           try{
                               $scope.myChart.dispose();
                           }catch(e){
                               //do nothing
                           }
                        }
                        return;
                    }
                    var seriesTpl = {
                        symbolSize:0,
                        name:$scope.chartOptions.seriesConfig[i].name,
                        type:'line',
                        data:$scope.chartOptions.seriesConfig[i].data,
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
                    legends.push($scope.chartOptions.seriesConfig[i].name);
                }
                if($scope.myChart){
                    try{
                        $scope.myChart.dispose();
                    }catch(e){
                        //do nothing
                    }
                    $scope.myChart=undefined;
                }
                require( [
                    'echarts',
                    'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
                    'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
                ], function (echarts) {
                    require(['echarts/theme/macarons'], function(curTheme){
                        var option = {
                            title : {
                                text: $scope.chartOptions.title,
                                subtext: $scope.chartOptions.subTitle
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
                                    data : $scope.chartOptions.xAxis,
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
                        $scope.myChart = echarts.init(element.get(0));
                        $scope.myChart.setTheme(curTheme);
                        $scope.myChart.setOption(option);
                    });
                });
            });
        }
    }
});
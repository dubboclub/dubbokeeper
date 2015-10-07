var echarts = angular.module('echarts', []);
echarts.directive('echarts', function() {
    return {
        restrict:"A",
        scope: {
            chartOptions: '=chartOptions'
        },
        controller:["$scope",function($scope,element){
            require( [
                'echarts',
                'echarts/chart/bar', // 使用柱状图就加载bar模块，按需加载
                'echarts/chart/line' // 使用柱状图就加载bar模块，按需加载
            ], function (echarts) {
                require(['echarts/theme/macarons'], function(curTheme){
                    var myChart = echarts.init(element);
                    myChart.setTheme(curTheme)
                    myChart.setOption($scope.chartOptions);
                });
            });
        }]
    }
});
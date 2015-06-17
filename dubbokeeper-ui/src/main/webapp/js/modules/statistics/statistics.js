var statistics = angular.module("statistics",['ngRoute']);

statistics.config(function($routeProvider){
    $routeProvider.when("/",{
        templateUrl:"templates/statistics/statistics.html",
        controller:"statisticsIndex"
    });
});
statistics.controller("statisticsIndex",function($scope,$httpWrapper,$breadcrumb,$menu){
    $breadcrumb.pushCrumb("Home","首页","statisticsIndex");
    $menu.switchMenu(menu.HOME);
    $httpWrapper.post({
        url:"loadAppsType.htm",
        success:function(data){
           var option = {
                title : {
                    text: '应用类型分布图',
                    x:'center'
                },
                tooltip : {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    orient : 'vertical',
                    x : 'left',
                    data:['P.AND.C','P','C']
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
                        name:'应用类型',
                        type:'pie',
                        radius : '55%',
                        center: ['50%', '60%'],
                        data:[
                            {value:data.integerList[0], name:'P'},
                            {value:data.integerList[1], name:'C'},
                            {value:data.integerList[2], name:'P.AND.C'}
                        ]
                    }
                ]
            };
            var myChart = echarts.init(document.getElementById('statisticsAppsTypes'));
            myChart.setOption(option);
        }
    });
});

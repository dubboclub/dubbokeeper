var dateRangePicker =  angular.module('dateRangePicker', ['ngAnimate']);

dateRangePicker.directive("dateRangePicker",function(){
    return {
        restrict:"EA",
        replace:true,
        scope: {
            timeRange:"=timeRange"
        },
        templateUrl:"templates/datepicker/datepicker.html",
        link:function($scope,element){
            element.find(".select-range").css({cursor:'pointer'});
            $scope.startMinuteEvent=function(type){
                if(type=='plus'){
                    if($scope.startMinute==59){
                        if($scope.startHour==23){
                            $scope.startHour=0;
                        }else{
                            $scope.startHour++;
                        }
                        $scope.startMinute=0;
                    }else{
                        $scope.startMinute++;
                    }
                }else{
                    if($scope.startMinute==0){
                        if($scope.startHour==0){
                            $scope.startHour=23;
                        }else{
                            $scope.startHour--;
                        }
                        $scope.startMinute=59;
                    }else{
                        $scope.startMinute--;
                    }
                }
                formatHourAndMinute();
            }
            var init = function(){
                var initStartDate = new Date($scope.timeRange.startTime);
                var initEndDate = new Date($scope.timeRange.endTime);
                $scope.startDate = initStartDate.getFullYear()+"-"+numberFormat(initStartDate.getMonth()+1)+"-"+numberFormat(initStartDate.getDate());
                $scope.endDate = initEndDate.getFullYear()+"-"+numberFormat(initEndDate.getMonth()+1)+"-"+numberFormat(initEndDate.getDate());
                $scope.startHour = initStartDate.getHours();
                $scope.startMinute = initStartDate.getMinutes();
                $scope.endHour = initEndDate.getHours();
                $scope.endMinute = initEndDate.getMinutes();
                formatHourAndMinute();
                start.data('datepicker').setDate(initStartDate);
                end.data('datepicker').setDate(initEndDate);
            }

            $scope.startHourEvent=function(type){
                if(type=='plus'){
                    if($scope.startHour==24){
                        $scope.startHour=0;
                    }else{
                        $scope.startHour++;
                    }
                }else{
                    if($scope.startHour==0){
                        $scope.startHour=23;
                    }else{
                        $scope.startHour--;
                    }
                }
                formatHourAndMinute();
            }
            $scope.endMinuteEvent=function(type){
                if(type=='plus'){
                    if($scope.endMinute==59){
                        if($scope.endHour==23){
                            $scope.endHour=0;
                        }else{
                            $scope.endHour++;
                        }
                        $scope.endMinute=0;
                    }else{
                        $scope.endMinute++;
                    }
                }else{
                    if($scope.endMinute==0){
                        if($scope.endHour==0){
                            $scope.endHour=23;
                        }else{
                            $scope.endHour--;
                        }
                        $scope.endMinute=59;
                    }else{
                        $scope.endMinute--;
                    }
                }
                formatHourAndMinute();
            }
            $scope.endHourEvent=function(type){
                if(type=='plus'){
                    if($scope.endHour==24){
                        $scope.endHour=0;
                    }else{
                        $scope.endHour++;
                    }
                }else{
                    if($scope.endHour==0){
                        $scope.endHour=23;
                    }else{
                        $scope.endHour--;
                    }
                }
                formatHourAndMinute();
            }

            $scope.toggleRangeBlock=function(){
                if($scope.rangeSlipUp){
                    $scope.rangeSlipUp=false;
                }else{
                    $scope.rangeSlipUp=true;
                }
            }

            var numberFormat=function(number){
                if(number<10){
                    return "0"+number;
                }
                return number+"";
            }
            var formatHourAndMinute=function(){
                $scope.startHourTxt = numberFormat($scope.startHour);
                $scope.startMinuteTxt = numberFormat($scope.startMinute);
                $scope.endHourTxt = numberFormat($scope.endHour);
                $scope.endMinuteTxt = numberFormat($scope.endMinute);
            }
            $scope.startHour=0;
            $scope.startMinute=0;
            $scope.endHour=0;
            $scope.endMinute=0;
            formatHourAndMinute();

            $scope.submitRange = function(){
                $scope.rangeSlipUp=false;
                var startDate = $scope.startDate+"T"+$scope.startHourTxt+":"+$scope.startMinuteTxt;
                var endDate =  $scope.endDate+"T"+$scope.endHourTxt+":"+$scope.endMinuteTxt;
                var temp = {};
                temp.startTime = new Date(startDate).getTime();
                temp.endTime = new Date(endDate).getTime();
                $scope.timeRange = temp;
            }

            $scope.changeRange=function(rangeSize){
                var endDate = new Date();
                var startDate = new Date(endDate.getTime()-rangeSize*60*60*1000);
                start.data('datepicker').setDate(startDate);
                end.data('datepicker').setDate(endDate);
                $scope.startHour = startDate.getHours();
                $scope.startMinute = startDate.getMinutes();
                $scope.endHour = endDate.getHours();
                $scope.endMinute = endDate.getMinutes();
                $scope.startDate = startDate.getFullYear()+"-"+numberFormat(startDate.getMonth()+1)+"-"+numberFormat(startDate.getDate());
                $scope.endDate = endDate.getFullYear()+"-"+numberFormat(endDate.getMonth()+1)+"-"+numberFormat(endDate.getDate());
                formatHourAndMinute();
            }

            var start = $(element.get(0)).find(".start").datepicker({
                format: "yyyy-mm-dd",
                language: "zh-CN",
                autoclose:true
            });
            start.on("changeDate",function(e){
                end.data('datepicker').setStartDate(e.date);
            });
            var end = $(element.get(0)).find(".end").datepicker({
                format: "yyyy-mm-dd",
                language: "zh-CN",
                autoclose:true
            });
            end.on("changeDate",function(e){
                var time = e.date.getTime()+60*60*1000;
                start.data('datepicker').setEndDate(new Date(time));
            });
            init();
        }
    }
});
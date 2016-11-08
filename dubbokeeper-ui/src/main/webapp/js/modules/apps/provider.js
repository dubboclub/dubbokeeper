var serviceProvider=angular.module("serviceProvider",['ngAnimate','ngRoute','queryFilter','breadCrumb']);

serviceProvider.config(function($routeProvider){
    $routeProvider.when("/admin/:serviceKey/providers",{
        templateUrl:"templates/apps/service-providers.html",
        controller:"serviceProviders"
    }).when("/admin/:serviceKey/service-readme",{
        templateUrl:"templates/apps/service-readme.html",
        controller:"serviceReadme"
    }).when("/admin/edit/:serviceKey/:address/:id/provider",{
        templateUrl:"templates/apps/edit-provider.html",
        controller:"editProvider"
    }).when("/admin/view/:serviceKey/:address/:id/detail",{
        templateUrl:"templates/apps/view-provider.html",
        controller:"viewProvider"
    }).when("/admin/operation/:type/:service/:address/:id/provider",{
        templateUrl:"templates/apps/edit-provider.html",
        controller:"operateProvider"
    }).when("/admin/:application/:host/providers",{
        templateUrl:"templates/apps/service-providers.html",
        controller:"serviceProviders"
    });
});
serviceProvider.controller("viewProvider",function($scope,$httpWrapper,$routeParams,$breadcrumb,$httpWrapper,$menu){
    $menu.switchMenu("admin/apps");
    $scope.provider={};
    $scope.serviceKey=decodeURIComponent($routeParams.serviceKey);
    $breadcrumb.pushCrumb($routeParams.address,"查看服务"+$routeParams.serviceKey+"提供者明细","viewProvider");
    $httpWrapper.post({url:"provider/"+$routeParams.id+"/provider-detail.htm",success:function(data){
        $scope.provider=data;
        $scope.parameters=queryString2Object(data.parameters);
        $scope.parameters.enabled=data.enabled;
        $scope.parameters.weight=data.weight;
    }})
});
serviceProvider.controller("editProvider",function($scope,$http,$routeParams,$breadcrumb,$dialog,$httpWrapper,$menu){
    $menu.switchMenu("admin/apps");
    $scope.provider={};
    $scope.serviceKey=decodeURIComponent($routeParams.serviceKey);
    $breadcrumb.pushCrumb($routeParams.address,"编辑服务"+$scope.serviceKey+"提供者","editProvider");
    $scope.enabledOptios=[{
        val:true,
        text:"启用"
    },{
        val:false,
        text:"禁用"
    }];
    $httpWrapper.post({
        url:"provider/"+$routeParams.id+"/provider-detail.htm",
        success: function (data) {
            $scope.provider=data;
            $scope.parameters=queryString2Object(data.parameters);
            $scope.parameters.enabled=data.enabled;
            $scope.parameters.weight=data.weight;
        }
    });
    $scope.update=function(){
        $dialog.confirm({content:"确认提交修改内容？",callback:function(){
            $httpWrapper.post({
                url:"provider/edit-provider.htm",
                data:"parameters="+object2QueryString($scope.parameters)+"&id="+$routeParams.id,
                config:{ headers: { 'Content-Type': 'application/x-www-form-urlencoded'}},
                success:function(data){
                    $dialog.info({content:"成功更新"+$scope.serviceKey+"服务信息！"});
                }
            });
        }})
    }

});



serviceProvider.controller("serviceProviders",function($scope,$http,$routeParams,$queryFilter,$breadcrumb,$dialog,$httpWrapper,$menu){
    $menu.switchMenu("admin/apps");
    $scope.details=[];
    $scope.isEmpty=false;
    if($routeParams.serviceKey){
        $scope.serviceKey=decodeURIComponent($routeParams.serviceKey);
    }else{
        $scope.host=decodeURIComponent($routeParams.host);
        $scope.application=$routeParams.application;
    }
    $scope.query={};
    $scope.dynamicOptios=[{
        val:true,
        text:"动态"
    },{
        val:false,
        text:"静态"
    }];
    $scope.enabledOptios=[{
        val:true,
        text:"已启用"
    },{
        val:false,
        text:"已禁用"
    }];
    if($scope.serviceKey){
        $breadcrumb.pushCrumb($scope.serviceKey,"查看服务"+$scope.serviceKey+"提供者列表","serviceProviders");
    }else{
        $breadcrumb.pushCrumb($scope.application+"-"+decodeURIComponent($scope.host),"查看应用"+$scope.application+"在"+decodeURIComponent($scope.host)+"上的提供的服务列表","serviceProviders");
    }

    var requestUrl = "provider/"+encodeURIComponent($routeParams.serviceKey)+"/providers.htm";
    if($routeParams.host){
        requestUrl="provider/"+$routeParams.application+"/"+$routeParams.host+"/providers.htm";
    }
    var refreshData = function(){
        $httpWrapper.post({
            url:requestUrl,
            success:function(data){
                $scope.details=data;
                if(!data||data.length<=0){
                    $scope.isEmpty=true;
                }
                $scope.originData= $scope.details;
            }
        });
    }
    refreshData();
    $scope.filter=function(){
        var filterResult=[];
        if($scope.isEmpty){
            return ;
        }
        $scope.details=$queryFilter($scope.originData,$scope.query);
    }
    $scope.operation=function(type,provider){
        var submit = function(){
            $httpWrapper.post({
                url:"provider/"+provider.id+"/"+type+"/operate.htm",
                success:function(data){
                    $dialog.info({content:"成功更新服务信息！",callback:refreshData});
                }
            });
        };
        $dialog.confirm({content:"确认进行此操作？",size:'small',callback: function () {
            submit();
        }});
    }
    $scope.batchOperation=function(type){
        var selected=[];
        $("tbody tr input[type='checkbox']").each(function(){
            if(this.checked){
                selected.push(this.value);
            }
        });
        if(selected.length<=0){
            $dialog.alert({content:"请选择操作数据！",size:'small'});
            return;
        }
        var submit=function(){
            $httpWrapper.post({
                url:"provider/"+type+"/batch-operate.htm",
                data:"ids="+selected.join(","),
                config:{ headers: { 'Content-Type': 'application/x-www-form-urlencoded'}},
                success:function(){
                    $dialog.info({content:"成功更新服务信息！",callback:refreshData});
                    $("thead tr input[type='checkbox']")[0].checked=false;
                }
            });
        };
        $dialog.confirm({content:"确认进行此操作？",size:'small',callback: function () {
            submit();
        }});
    }
    $scope.select=function(){
        var selectAll = $(".selector").get(0).checked;
        $("input[type='checkbox']").each(function(){
            if(selectAll){
                this.checked=true;
            }else{
                this.checked=false;
            }

        });
    }
});
serviceProvider.controller("serviceReadme",function($scope,$http,$routeParams,$queryFilter,$breadcrumb,$dialog,$httpWrapper,$menu){
    $scope.serviceKey=decodeURIComponent($routeParams.serviceKey);
    $menu.switchMenu("admin/apps");
    $breadcrumb.pushCrumb($scope.serviceKey,"查看服务"+$scope.serviceKey+"调用文档","serviceReadme");
	(function(){
        $httpWrapper.get({
            url:"templates/apps/service-readme.md",
            success:function(mdText){
            	$httpWrapper.post({
            		url:"provider/"+encodeURIComponent($routeParams.serviceKey)+"/service-readme.htm",
                    success:function(data){
                    	var converter = new showdown.Converter({extensions: ['table']});
                    	if(data.providers.length > 0){
                    		var serviceName = data.providers[0].serviceKey;
                    		var beanNameArr = serviceName.split('.');
                    		var demoBeanName = beanNameArr[beanNameArr.length - 1][0].toLowerCase() 
                    				+ beanNameArr[beanNameArr.length - 1].substring(1)
                    		var demoBeanClassName = beanNameArr[beanNameArr.length - 1];
                    		var params = data.providers[0]. parameters.split('&');
                    		var demoMethod;
                    		angular.forEach(params, function(value, key) {
                    			if(value.indexOf('methods') === 0){
                    				demoMethod = value.split('=')[1].split(',')[0];
                    				return;
                    			}
                			});
                    		var libs = {
                    				default: [
	                    		        'log4j/log4j/1.2.17',
										'commons-logging/commons-logging/1.2',
										'com.alibaba/dubbo/2.5.3',
										'org.javassist/javassist/3.15.0-GA',
										'io.netty/netty/3.7.0.Final',
										'org.slf4j/slf4j-api/1.7.7',
										'org.slf4j/org.slf4j/slf4j-log4j12/1.7.7',
										'org.springframework/spring-core/2.5.6.SEC03',
										'com.101tec/zkclient/0.4',
										'org.apache.zookeeper/zookeeper/3.4.6'
									],
                    				jsonrpc : [
	                    		        'com.github.briandilley.jsonrpc4j/jsonrpc4j/1.1',
										'com.ofpay/dubbo-rpc-jsonrpc/1.0.1'
									]
                    		}
                    		var libsNeeded = [];
                    		angular.forEach(libs.default, function(lib, key) {
            					libsNeeded.push({
            						groupId:lib.split('/')[0],
        							artifactId:lib.split('/')[1],
            						version:lib.split('/')[2]
            					});
            				});
                    		angular.forEach(data.providers, function(provider, key) {
                    			var protocol = provider.url.split(':')[0];
                    			libs_protocol = libs[protocol];
                    			if(libs_protocol){
                    				angular.forEach(libs_protocol, function(lib, key) {
                    					libsNeeded.push({
                    						groupId:lib.split('/')[0],
                    						artifactId:lib.split('/')[1],
                    						version:lib.split('/')[2]
                    					});
                					});
                				};
                			});
                    	}
                    	var context = {providers: data.providers, 
                    				serviceName: serviceName,
                    				demoBeanName: demoBeanName,
                    				demoBeanClassName: demoBeanClassName,
                    				demoMethod: demoMethod,
                    				libsNeeded: libsNeeded,
                    				registry: data.registry
                    			};
                        html = converter.makeHtml(Mustache.render(mdText, context));
                    	$(".mardown-body").html(html);
                    	$(".mardown-body table").addClass('table table-bordered');
                    }
            	 });
            }
        });
    })();
});
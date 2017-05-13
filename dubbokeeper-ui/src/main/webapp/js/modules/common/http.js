var httpWrapper=angular.module("httpWrapper",[]);
httpWrapper.$httpWrapper=function(){
    var http=function($http,$dialog){
        this.$http=$http;
        this.$dialog=$dialog;
    };
    http.prototype.get=function(options){
        var me=this;
        me.$http.get(options.url,options.data,options.config).success(function(data){
            if(!data.result||data.result==ajaxResultStatu.SUCCESS){
                if(options.success){
                    options.success(data);
                }
            }else{
                me.$dialog.info({content:data.memo});
            }
        }).error(function(e){
            if(options.error){
                options.error(e);
            }
            console.log(e);
            me.$dialog.info({content:"后端系统出现异常，请稍后再试！"});
        });
    };
    http.prototype.post=function(options){
        var me=this;
        me.$http.post(options.url,options.data,options.config).success(function(data){
            if(!data.result||data.result==ajaxResultStatu.SUCCESS){
                if(options.success){
                    options.success(data);
                }
            }else{
                me.$dialog.info({content:data.memo});
            }
        }).error(function(e){
            if(options.error){
                options.error(e);
            }
            console.log(e);
            me.$dialog.info({content:"后端系统出现异常，请稍后再试！"});
        });
    };
    this.$get =['$http','$dialog',function($http,$dialog){
        return new http($http,$dialog);
    }];
}

httpWrapper.provider("$httpWrapper",httpWrapper.$httpWrapper);
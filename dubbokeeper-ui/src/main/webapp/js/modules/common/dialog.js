var dialog=angular.module("dialog",['ngCookies']);
dialog.directive("dialogTpl",function(){
    return {
        restrict:"E",
        templateUrl:"templates/dialog/dialog.html",
        controller:"dialogCtl"
    };
});
dialog.controller("dialogCtl",function($scope,$dialog,$location){
    $scope.dialogParams={};
    $dialog.init($scope,$location);
    $scope.ok=function(){
        $dialog.ok();
    }
    $scope.cancel=function(){
        $dialog.cancel();
    }

});
dialog.$dialog=function(){

    var Dialog=function(){
        this.status=0;
        var me= this;
        this.first=true;
    }
    Dialog.prototype._bindHidden=function(){
        if(this.first){
            var me=this;
            $('#dialog-modal').on("hidden.bs.modal",function(){
                if(me.hiddenAction){
                    me.hiddenAction();
                    me.hiddenAction=undefined;
                }
            });
            this.first=false;
        }

    }
    Dialog.prototype.info=function(options){
        this._bindHidden();
        this.scope.dialogParams.type='info';
        this.scope.dialogParams.content=this.$sce.trustAsHtml(options.content);
        this.scope.dialogParams.size= options.size?options.size:"normal";
        this.scope.dialogParams.title="提示";
        this.scope.dialogParams.cancleBtn=false;
        this.scope.dialogParams.confirmBtn=true;
        this.scope.dialogParams.confirmContent="确定";
        this.scope.dialogParams.confirmLink=options.confirmLink;
        this.boundOk=options.callback;
        this.status=1;
        $('#dialog-modal').modal('show');
    };
    Dialog.prototype.confirm=function(options){
        this._bindHidden();
        this.scope.dialogParams.type="confirm";
        this.scope.dialogParams.content=this.$sce.trustAsHtml(options.content);
        this.scope.dialogParams.size= options.size?options.size:"normal";
        this.scope.dialogParams.title="确认";
        this.scope.dialogParams.cancleBtn=true;
        this.scope.dialogParams.confirmBtn=true;
        this.scope.dialogParams.confirmContent="确定";
        this.scope.dialogParams.confirmLink=options.confirmLink;
        this.boundOk=options.callback;
        this.status=1;
        $('#dialog-modal').modal('show');
    };
    Dialog.prototype.alert=function(options){
        this._bindHidden();
        this.scope.dialogParams.type='alert';
        this.scope.dialogParams.content=this.$sce.trustAsHtml(options.content);
        this.scope.dialogParams.size= options.size?options.size:"normal";
        this.scope.dialogParams.title="警告";
        this.scope.dialogParams.cancleBtn=false;
        this.scope.dialogParams.confirmBtn=true;
        this.scope.dialogParams.confirmContent="确定";
        this.scope.dialogParams.confirmLink=options.confirmLink;
        this.boundOk=options.callback;
        this.status=1;
        $('#dialog-modal').modal('show');
    }

    Dialog.prototype.cancel=function(){
        $('#dialog-modal').modal('hide');
        if(this.boundCancel){
            this.hiddenAction=this.boundCancel;
        }
    }

    Dialog.prototype.ok=function(){
        $('#dialog-modal').modal('hide');
        if(this.boundOk){
            this.hiddenAction=this.boundOk;
        }
    }


    Dialog.prototype.init= function ($scope,$location) {
        this.scope=$scope;
        this.location=$location;
    };


    var d=new Dialog();
    this.$get =['$sce',function ($sce) {
        d.$sce=$sce;
        return d;
    }] ;
}
dialog.provider("$dialog",dialog.$dialog);
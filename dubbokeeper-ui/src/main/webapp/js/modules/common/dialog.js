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
    }
    Dialog.prototype.info=function(options){
        $('#dialog-modal').modal('hide');
        this.scope.dialogParams.type='info';
        this.scope.dialogParams.content=options.content;
        this.scope.dialogParams.size= options.size?options.size:"normal";
        this.scope.dialogParams.title="提示";
        this.scope.dialogParams.cancleBtn=false;
        this.scope.dialogParams.confirmBtn=true;
        this.scope.dialogParams.confirmContent="确定";
        this.scope.dialogParams.confirmLink=options.confirmLink;
        this.boundOk=options.callback;
        $('#dialog-modal').modal('show');
    };
    Dialog.prototype.confirm=function(options){
        $('#dialog-modal').modal('hide');
        this.scope.dialogParams.type="confirm";
        this.scope.dialogParams.content=options.content;
        this.scope.dialogParams.size= options.size?options.size:"normal";
        this.scope.dialogParams.title="确认";
        this.scope.dialogParams.cancleBtn=true;
        this.scope.dialogParams.confirmBtn=true;
        this.scope.dialogParams.confirmContent="确定";
        this.scope.dialogParams.confirmLink=options.confirmLink;
        this.boundOk=options.callback;
        $('#dialog-modal').modal('show');
    };
    Dialog.prototype.alert=function(options){
        $('#dialog-modal').modal('hide');
        this.scope.dialogParams.type='alert';
        this.scope.dialogParams.content=options.content;
        this.scope.dialogParams.size= options.size?options.size:"normal";
        this.scope.dialogParams.title="警告";
        this.scope.dialogParams.cancleBtn=false;
        this.scope.dialogParams.confirmBtn=true;
        this.scope.dialogParams.confirmContent="确定";
        this.scope.dialogParams.confirmLink=options.confirmLink;
        this.boundOk=options.callback;
        $('#dialog-modal').modal('show');
    }

    Dialog.prototype.cancel=function(){
        $('#dialog-modal').modal('hide');
        if(this.boundCancel){
            this.boundCancel();
        }
    }

    Dialog.prototype.ok=function(){
        $('#dialog-modal').modal('hide');
        if(this.boundOk){
            this.boundOk();
        }
    }


    Dialog.prototype.init= function ($scope,$location) {
        this.scope=$scope;
        this.location=$location;
    };


    var d=new Dialog();
    this.$get = function () {
        return d;
    };
}
dialog.provider("$dialog",dialog.$dialog);
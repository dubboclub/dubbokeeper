<%--
  Created by IntelliJ IDEA.
  User: bieber
  Date: 2015/6/4
  Time: 0:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/header.jsp"%>
<div class="container-fluid " ng-controller="dubbokeeperCtrl">
    <div class="row">
        <div class="col-md-2 col-lg-2 col-xs-2" ng-show="hasMenu">
            <div class="row">
                <div class="col-md-12 col-lg-12 col-xs-12">
                    <menu-tpl></menu-tpl>
                </div>
            </div>
        </div>
        <div class="{{hasMenu?'col-md-10 col-lg-10 col-xs-10':'col-md-12 col-lg-12 col-xs-12'}} app-container">
            <breadcrumb-tpl ng-show="needBreadCrumb"></breadcrumb-tpl>
            <div class="row">
                <div class="col-md-12 col-lg-12 col-xs-12" ng-view>
                </div>
            </div>
        </div>
    </div>
</div>
<dialog-tpl></dialog-tpl>
<%@include file="common/scripts.jsp"%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/theme/theme.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/router/router.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/override/override.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/statistics/statistics.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/monitor/monitor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/zoopeeper/zoopeeper.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/common/filter.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/common/basic.module.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/common/http.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/common/stickup.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/common/dialog.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/common/echarts.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/common/echarts.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/common/fullscreen.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/common/apps-dependencies.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/common/date-range-picker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/breadcrumb/breadcrumb.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/apps/provider.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/apps/apps.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/head/head.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/menu/menu.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/aboutus/aboutus.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/index.js"></script>
<%@include file="common/footer.jsp"%>
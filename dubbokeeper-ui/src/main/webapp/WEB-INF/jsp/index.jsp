<%--
  Created by IntelliJ IDEA.
  User: bieber
  Date: 2015/6/4
  Time: 0:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/header.jsp"%>
<div class="container-fluid ">
    <div class="row">
        <div class="col-md-2 col-lg-2 col-xs-2">
            <div class="row">
                <div class="col-md-12 col-lg-12 col-xs-12">
                    <menu-tpl></menu-tpl>
                </div>
            </div>
        </div>
        <div class="col-md-10 col-lg-10 col-xs-10 app-container">
            <breadcrumb-tpl></breadcrumb-tpl>
            <div class="row">
                <div class="col-md-12 col-lg-12 col-xs-12" ng-view>
                    <app-list></app-list>
                </div>
            </div>
        </div>
    </div>
</div>
<%@include file="common/scripts.jsp"%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/breadcrumb/breadcrumb.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/apps/provider.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/apps/apps.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/head/head.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/menu/menu.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/index.js"></script>
<%@include file="common/footer.jsp"%>
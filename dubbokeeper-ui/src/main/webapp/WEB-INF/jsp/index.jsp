<%--
  Created by IntelliJ IDEA.
  User: bieber
  Date: 2015/6/4
  Time: 0:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/header.jsp"%>
<div class="container" ng-app="indexApp" >
    <div class="row">
        <div class="col-md-12 col-lg-12 col-xs-12" style="height: 550px;">
            <app-list></app-list>
        </div>
    </div>
</div>
<%@include file="common/scripts.jsp"%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/index.js"></script>
<%@include file="common/footer.jsp"%>
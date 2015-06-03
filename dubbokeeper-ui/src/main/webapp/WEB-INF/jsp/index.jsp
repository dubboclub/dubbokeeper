<%--
  Created by IntelliJ IDEA.
  User: bieber
  Date: 2015/6/4
  Time: 0:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>dubbokeeper</title>
</head>
<body ng-controller="indexCtrl">
<%@include file="common/header.jsp"%>
<div class="container">
    <div class="row">
        <div class="col-md-12 col-lg-12 col-xs-12">
            <table class="table table-bordered">
                <thead>
                <tr>
                    <th>应用名</th>
                    <th>拥有者</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <application-details></application-details>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@include file="common/footer.jsp"%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/index.js"></script>
</body>
</html>

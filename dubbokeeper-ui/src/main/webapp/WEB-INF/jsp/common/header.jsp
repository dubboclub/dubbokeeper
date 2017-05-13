<%--
  Created by IntelliJ IDEA.
  User: bieber
  Date: 2015/4/26
  Time: 18:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html  ng-app="dubbokeeper">
<head>
    <title>DubboKeeper</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/libs/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/libs/bootstrap-table.css">
    <%--<link rel="stylesheet" href="${pageContext.request.contextPath}/css/libs/bootstrap/css/bootstrap-theme.min.css">--%>
    <link id="dubboKeeperTheme" rel="stylesheet" href="" media="screen">
    <%--<link rel="stylesheet" href="${pageContext.request.contextPath}/css/libs/themes/sandstone/bootstrap.css" media="screen">--%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/libs/themes/assets/css/bootswatch.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/animation.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/libs/font-awesome/font-awesome.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/validator.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/codemirror/lib/codemirror.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/codemirror/theme/ttcn.css">
    <!--[if IE 7]>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/libs/font-awesome/font-awesome-ie7.css">
    <![endif]-->
    <%--link rel="stylesheet" href="${pageContext.request.contextPath}/flat-ui/css/flat-ui.css">--%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dubbokeeper.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/libs/angularjs-ui-tree/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/libs/datepicker/css/bootstrap-datepicker3.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/datepicker.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/libs/multiselect/isteven-multi-select.css">
</head>
<body>

<head-tpl></head-tpl>
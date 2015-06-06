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
    <title>dubbokeeper</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/libs/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/libs/bootstrap-table.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/libs/bootstrap/css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/libs/font-awesome/font-awesome.css">
    <!--[if IE 7]>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/libs/font-awesome/font-awesome-ie7.css">
    <![endif]-->
    <%--link rel="stylesheet" href="${pageContext.request.contextPath}/flat-ui/css/flat-ui.css">--%>
</head>
<style>
    body{
        padding-top: 70px;
    }
    *{
        font-family: 幼圆,"Microsoft YaHei", Arial, Helvetica, sans-serif, "宋体";;
    }
</style>
<body>
<head-tpl></head-tpl>
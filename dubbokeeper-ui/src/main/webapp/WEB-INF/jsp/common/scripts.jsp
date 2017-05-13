<%--
  Created by IntelliJ IDEA.
  User: bieber
  Date: 2015/6/6
  Time: 8:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/codemirror/lib/codemirror.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/codemirror/mode/javascript/javascript.js"></script>
<script src="${pageContext.request.contextPath}/js/codemirror/addon/selection/active-line.js"></script>
<script src="${pageContext.request.contextPath}/js/codemirror/addon/edit/matchbrackets.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/esprima.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/jquery/jquery-2.1.4.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/jquery/jquery.fullscreen.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/cytoscape.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/angularjs/angular.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/angularjs/angular-route.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/angularjs/angular-animate.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/angularjs/angular-cookies.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/common/filter.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/common/util.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/modules/common/libs-mapping.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/angularjs-ui-tree/treeView.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/bootstrap/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/css/libs/themes/assets/js/bootswatch.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/showdown/showdown.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/showdown/showdown-table.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/mustache.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/echarts/echarts.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/datepicker/js/bootstrap-datepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/datepicker/locale/bootstrap-datepicker.zh-CN.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/multiselect/isteven-multi-select.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/libs/jquery/jquery.fullscreen.js"></script>
<script type="text/javascript">
    // 路径配置
    require.config({
        paths: {
            echarts: 'js/echarts'
        }
    });
</script>
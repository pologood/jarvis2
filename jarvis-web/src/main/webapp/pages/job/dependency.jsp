
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="../common/header.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
    <jsp:param name="platform" value="${platform}"/>
    <jsp:param name="platforms" value="${platforms}"/>
</jsp:include>

<link type="text/css" rel="stylesheet" href="/assets/jarvis/plugins/d3/d3-collapsible-tree.css" />


<div class="container">

    <div class="row">
        <div class="col-md-6">
            <nav>
                <ol class="cd-breadcrumb triangle">
                    <li><a href="/jarvis/">首页</a></li>
                    <li ><a href="/jarvis/job">任务管理</a></li>
                    <li class="current"><em>任务依赖</em></li>
                </ol>
            </nav>
        </div>
    </div>

    <hr>

    <div id="dependTree"></div>
</div>


<jsp:include page="../common/login.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
</jsp:include>



<jsp:include page="../common/footer.jsp">
    <jsp:param name="menuMap" value="${menuMap}"/>
</jsp:include>

<script type="text/javascript">
    var jobVo=${jobVo};
</script>
<script type="text/javascript" src="/assets/jarvis/js/jarvis/job/concept-graph.js" charset="UTF-8"></script>

<script type="text/javascript" src="/assets/jarvis/js/jarvis/job/dependency.js"></script>


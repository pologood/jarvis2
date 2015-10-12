
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="../common/header.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
    <jsp:param name="platform" value="${platform}"/>
    <jsp:param name="platforms" value="${platforms}"/>
</jsp:include>


<div class="container">

    <div class="row">
        <div class="col-md-6">
            <nav>
                <ol class="cd-breadcrumb triangle">
                    <li><a href="/jarvis/">Home</a></li>
                    <li ><a href="/jarvis/job">任务管理</a></li>
                    <li class="current"><em>任务依赖</em></li>
                </ol>
            </nav>
        </div>
    </div>

    <hr>

    <div class="row">
        <div class="col-md-12">
            <div id="dependTree" style="width:100%;min-height:500px"></div>

        </div>
    </div>





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

<script type="text/javascript" src="/assets/jarvis/js/jarvis/job/dependency.js"></script>


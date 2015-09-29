
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
                    <li class="current"><em>调度系统管理</em></li>
                </ol>
            </nav>
        </div>
    </div>

    <div class="row top-buffer">
        <div class="col-md-12">
            <div class="row">
                <div class="col-md-6 col-md-offset-3 top-buffer">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">设置并行度</span>
                        <input class="form-control" id="" value="" />
                    </div>
                </div>

                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <button type="button" class="btn btn-default">并行度</button>
                        <button type="button" class="btn btn-default">启动</button>
                        <button type="button" class="btn btn-default">暂停</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<jsp:include page="../common/login.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
</jsp:include>


<jsp:include page="../common/footer.jsp">
    <jsp:param name="menuMap" value="${menuMap}"/>
</jsp:include>

<script type="text/javascript" src="/assets/jarvis/js/jarvis/manage/manage.js"></script>
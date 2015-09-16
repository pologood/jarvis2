
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
                    <li class="current"><em>作业管理</em></li>
                </ol>
            </nav>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">

            <div class="row">
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">作业ID</span>
                        <select id="jobId" ></select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">作业名称</span>
                        <select id="jobName"  ></select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">作业类型</span>
                        <select id="jobType" ></select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">发布者</span>
                        <select id="submitUser" ></select>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">执行周期</span>
                        <select id="executeCycle" ></select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">来源</span>
                        <select id="jobSource" ></select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">优先级</span>
                        <select id="jobPriority" ></select>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="row">
                        <div class="col-md-6 col-md-offset-6">
                            <div class="input-group">
                                <button type="button" class="btn btn-primary" onclick="search()">查询</button>
                                <button type="button" class="btn btn-primary" onclick="reset()" style="margin-left: 3px">重置</button>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>

    <hr>

    <div class="row top-buffer">
        <div class="col-md-12">
            <div id="add">
                <a class="btn btn-primary" href="/jarvis/job/trash" target="_blank">回收站</a>
                <a class="btn btn-primary" href="/jarvis/job/addOrEdit" target="_blank">新增作业</a>
            </div>
            <table id="content" >

            </table>

        </div>

    </div>


</div>


<jsp:include page="../common/login.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
</jsp:include>


<jsp:include page="../common/footer.jsp">
    <jsp:param name="menuMap" value="${menuMap}"/>
</jsp:include>

<script type="text/javascript" src="/assets/jarvis/js/jarvis/job/job.js"></script>

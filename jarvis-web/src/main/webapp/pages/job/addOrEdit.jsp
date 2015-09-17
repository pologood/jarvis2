
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
                    <li class="current"><em>新增任务</em></li>
                </ol>
            </nav>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">

            <div class="row">
                <div class="col-md-4 col-md-offset-4">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务名称</span>
                        <input id="jobName" class="form-control" />
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-4 col-md-offset-4">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">选择脚本</span>
                        <select id="content"  ></select>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-4 col-md-offset-4">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">调度时间</span>
                        <input id="crontab" class="form-control" />
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-4 col-md-offset-4">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务参数</span>
                        <input id="params" class="form-control" />
                    </div>
                </div>
            </div>


            <div class="row top-buffer">
                <div class="col-md-4 col-md-offset-4">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">应用名称</span>
                        <select id="appName"  ></select>
                    </div>
                </div>
            </div>


            <div class="row top-buffer">
                <div class="col-md-4 col-md-offset-4">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务类型</span>
                        <select id="jobType"  ></select>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-4 col-md-offset-4">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务状态</span>
                        <select id="jobFlag"  ></select>
                    </div>
                </div>
            </div>


            <div class="row top-buffer">
                <div class="col-md-4 col-md-offset-4">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">优先级</span>
                        <select id="jobPriority"></select>
                    </div>
                </div>
            </div>


            <div class="row top-buffer">
                <div class="col-md-4 col-md-offset-4">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">依赖任务</span>
                        <select id="jobDependency" multiple></select>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-4 col-md-offset-4 text-center">
                    <button type="button" class="btn btn-primary">提交</button>
                    <button type="button" class="btn btn-primary" onclick="reset()">重置</button>
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

<script type="text/javascript" src="/assets/jarvis/js/jarvis/job/addOrEdit.js"></script>

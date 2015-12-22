<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
                    <li><a href="${contextPath}/">首页</a></li>
                    <li class="current"><em>任务管理</em></li>
                </ol>
            </nav>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">

            <div class="row">
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:45%">任务ID</span>
                        <select id="jobId" multiple>

                        </select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:45%">任务名称</span>
                        <select id="jobName" multiple>

                        </select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:45%">任务类型</span>
                        <select id="jobType" multiple>
                        </select>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:48%">状态</span>
                        <select id="jobStatus" multiple></select>
                    </div>
                </div>

            </div>

            <div class="row top-buffer">

                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:45%">发布者</span>
                        <select id="submitUser" multiple>
                            <option value="all">全部</option>
                            <c:forEach items="${submitUsers}" var="submitUser" varStatus="status">
                                <option value="${submitUser}">${submitUser}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:45%">优先级</span>
                        <select id="jobPriority" multiple></select>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:45%">应用名称</span>
                        <select id="appId" multiple>
                            <option value="">全部</option>
                            <c:forEach items="${appVoList}" var="app">
                                <option value="${app.appId}">${app.appName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:48%">WorkerGroup名</span>
                        <select id="workerGroupId" multiple>
                            <option value="all">全部</option>
                            <c:forEach items="${workerGroupVoList}" var="workerGroup">
                                <option value="${workerGroup.id}">${workerGroup.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

            </div>

            <div class="row top-buffer">
                <div class="col-md-3 pull-right">
                    <div class="row">
                        <div class="col-md-6 col-lg-offset-6">
                            <div class="input-group">
                                <button type="button" class="btn btn-primary" onclick="search()">查询</button>
                                <button type="button" class="btn btn-primary" onclick="reset()"
                                        style="margin-left: 3px">重置
                                </button>
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
                <a class="btn btn-primary" href="${contextPath}/job/addOrEdit" target="_blank">新增任务</a>
            </div>
            <table id="content">

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
<script type="text/javascript">
    var user = '${user.uname}';
</script>

<script type="text/javascript" src="${contextPath}/assets/js/jarvis/job/job.js"></script>

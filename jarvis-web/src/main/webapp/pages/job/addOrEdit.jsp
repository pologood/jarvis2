
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
                    <c:choose>
                        <c:when test="${jobVo!=null}">
                            <li class="current"><em>编辑任务</em></li>
                        </c:when>
                        <c:otherwise>
                            <li class="current"><em>新增任务</em></li>
                        </c:otherwise>
                    </c:choose>
                </ol>
            </nav>
        </div>
    </div>

    <div class="row" id="jobData">
        <div class="col-md-12">
            <!-- 用户名必须 -->
            <input type="hidden" id="user" desc="用户名" value="${user.uname}" />
            <input type="hidden" id="job_id" desc="任务id" value="${jobVo.jobId}" />


            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">应用名称<span class="text-danger" style="vertical-align: middle" >*</span></span>
                        <select id="app_name" desc="应用名称" >
                            <c:forEach items="${appVoList}" var="app" varStatus="status">
                                <option value="${app.appName}" app_key="${app.appKey}" <c:choose><c:when test="${app.appName==jobVo.appName}">selected</c:when></c:choose> >${app.appName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务名称<span class="text-danger" style="vertical-align: middle" >*</span></span>
                        <input id="job_name" class="form-control" desc="任务名称" value="${jobVo.jobName}" onblur="checkJobName(this)" />
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">起始时间</span>

                        <input id="start_time" class="form-control" value="<fmt:formatDate value="${jobVo.activeStartDate}" pattern="yyyy-MM-dd"></fmt:formatDate>" />

                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">结束时间</span>
                        <input id="end_time" class="form-control" value="<fmt:formatDate value="${jobVo.activeEndDate}" pattern="yyyy-MM-dd"></fmt:formatDate>" />
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务类型<span class="text-danger" style="vertical-align: middle" >*</span></span>
                        <select id="job_type"  desc="任务类型">

                        </select>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">执行命令<span class="text-danger" style="vertical-align: middle" >*</span></span>
                        <textarea id="command" class="form-control" desc="执行命令" rows="4" onclick="changeTextArea(this,15,10)" onblur="changeTextArea(this,4,10)">${jobVo.content}</textarea>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务参数</span>
                        <input id="parameters" class="form-control" value="${jobVo.params}" />
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">Worker Group<span class="text-danger" style="vertical-align: middle" >*</span></span>
                        <select id="group_id" desc="Worker Group" >
                            <c:forEach items="${WorkerGroupVoList}" var="workerGroup" varStatus="status">
                                <option value="${workerGroup.id}" <c:choose><c:when test="${jobVo.workerGroupId==workerGroup.id}">selected</c:when></c:choose>  >${workerGroup.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>


            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">Cron表达式</span>
                        <input id="cron_expression" class="form-control" value="${cronTabVo.cronExpression}" />
                    </div>
                </div>
            </div>


            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">拒绝重试数</span>
                        <input id="reject_retries" class="form-control" value="${jobVo.rejectAttempts}" desc="拒绝重试数" placeholder="0" onblur="checkNum(this)"/>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">拒绝重试间隔(秒)</span>
                        <input id="reject_interval" class="form-control" value="${jobVo.rejectInterval}" desc="拒绝重试间隔(秒)" placeholder="3" onblur="checkNum(this)" />
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">失败重试数</span>
                        <input id="failed_retries" class="form-control" value="${jobVo.failedAttempts}" desc="失败重试数" placeholder="0" onblur="checkNum(this)" />
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">失败重试间隔(秒)</span>
                        <input id="failed_interval" class="form-control" value="${jobVo.failedInterval}" desc="失败重试间隔(秒)" placeholder="3" onblur="checkNum(this)" />
                    </div>
                </div>
            </div>


            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">优先级</span>
                        <select id="priority"></select>
                    </div>
                </div>
            </div>


            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">依赖任务</span>
                        <select id="dependency_jobids" multiple>
                            <c:forEach items="${jobVoList}" var="job" varStatus="status">
                                <option value="${job.jobId}">${job.jobName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3 text-center">
                    <c:choose>
                        <c:when test="${jobVo==null}">
                            <button type="button" class="btn btn-primary" onclick="submit()">提交</button>
                        </c:when>
                        <c:otherwise>
                            <button type="button" class="btn btn-primary" onclick="edit()">更新</button>
                        </c:otherwise>
                    </c:choose>



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

<script type="text/javascript">
    var jobType=undefined;
    var jobPriority=undefined;
    var dependIds=undefined;
    <c:choose>
    <c:when test="${jobVo!=null}">
        jobType='${jobVo.jobType}';
        jobPriority='${jobVo.priority}';
        dependIds='${dependIds}';
    </c:when>
    </c:choose>
</script>

<script type="text/javascript" src="/assets/jarvis/js/jarvis/job/addOrEdit.js"></script>



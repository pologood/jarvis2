<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
                    <li><a href="${contextPath}/">首页</a></li>
                    <li><a href="${contextPath}/task">执行流水</a></li>
                    <li class="current"><em>执行详情</em></li>
                </ol>
            </nav>
        </div>
    </div>

    <div class="row top-buffer">
        <div class="col-md-12">
            <div class="row">
                <table class="table table-bordered">
                    <tbody>
                    <tr>
                        <td class="emphasize bg-warning">任务ID</td>
                        <td>${jobVo.jobId}</td>
                        <td class="emphasize bg-warning">任务名称</td>
                        <td>${jobVo.jobName}</td>
                        <td class="emphasize bg-warning">参数</td>
                        <td>${taskVo.params}</td>
                    </tr>
                    <tr>
                        <td class="emphasize bg-warning">执行ID</td>
                        <td>${taskVo.taskId}</td>
                        <td class="emphasize bg-warning">执行者</td>
                        <td>${taskVo.executeUser}</td>
                        <td class="emphasize bg-warning">状态</td>
                        <td>
                            <c:choose>
                                <c:when test="${taskVo.status==1}">
                                    等待
                                </c:when>
                                <c:when test="${taskVo.status==2}">
                                    准备
                                </c:when>
                                <c:when test="${taskVo.status==3}">
                                    执行
                                </c:when>
                                <c:when test="${taskVo.status==4}">
                                    成功
                                </c:when>
                                <c:when test="${taskVo.status==5}">
                                    失败
                                </c:when>
                                <c:when test="${taskVo.status==6}">
                                    终止
                                </c:when>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <td class="emphasize bg-warning">调度时间</td>
                        <td><fmt:formatDate value="${taskVo.scheduleTime}"
                                            pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
                        <td class="emphasize bg-warning">执行进度</td>
                        <td><fmt:formatNumber type="number" value="${taskVo.progress*100}"
                                              maxIntegerDigits="2"></fmt:formatNumber>%
                        </td>
                        <td class="emphasize bg-warning">最近30次平均耗时(秒)</td>
                        <td>${taskVo.avgExecuteTime}</td>
                    </tr>
                    <tr>
                        <td class="emphasize bg-warning">开始时间</td>
                        <td><fmt:formatDate value="${taskVo.executeStartTime}"
                                            pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
                        <td class="emphasize bg-warning">结束时间</td>
                        <td><fmt:formatDate value="${taskVo.executeEndTime}"
                                            pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></td>
                        <td class="emphasize bg-warning">耗时(秒)</td>
                        <td>${taskVo.executeTime}</td>
                    </tr>
                    <tr>
                        <td class="emphasize bg-warning">应用ID</td>
                        <td>${taskVo.appId}</td>
                        <td class="emphasize bg-warning">workerId</td>
                        <td>${taskVo.workerId}</td>
                    </tr>

                    <tr>
                        <td colspan="6">
                            <div id="container" style="height:400px;width:100%"></div>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6">
                            <div>
                                <ul class="nav nav-tabs">
                                    <li role="presentation" class="active">
                                        <a href="#executeContent" data-toggle="tab">执行内容</a>
                                    </li>
                                    <li role="presentation">
                                        <a href="#log" data-toggle="tab">日志</a>
                                    </li>
                                </ul>

                                <div class="tab-content">
                                    <div id="executeContent" class="tab-pane active">
                                        <pre>
                                            ${taskVo.content}
                                        </pre>
                                    </div>

                                    <div id="log" class="tab-pane">

                                    </div>
                                </div>


                            </div>


                        </td>
                    </tr>
                    </tbody>
                </table>
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
    var taskVoList =${taskVoList};
    var taskId = '${taskVo.taskId}';
    var jobId = '${taskVo.jobId}';
    var attemptId = '${taskVo.attemptId}';
</script>
<script type="text/javascript" src="${contextPath}/assets/js/jarvis/task/detail.js"></script>
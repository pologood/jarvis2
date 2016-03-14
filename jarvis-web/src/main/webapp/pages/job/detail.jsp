<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
    <jsp:param name="platform" value="${platform}"/>
    <jsp:param name="platforms" value="${platforms}"/>
</jsp:include>

<link type="text/css" rel="stylesheet" href="${contextPath}/assets/plugins/d3/d3-collapsible-tree.css"/>


<div class="container">

    <div class="row">
        <div class="col-md-6">
            <nav>
                <ol class="cd-breadcrumb triangle">
                    <li><a href="${contextPath}/">首页</a></li>
                    <li><a href="${contextPath}/job">任务管理</a></li>
                    <li class="current"><em>任务详情</em></li>
                </ol>
            </nav>
        </div>
    </div>

    <hr>

    <div class="row" id="jobDetail">
        <div class="col-md-12">
            <table class="table table-bordered table-striped">
                <tbody>
                <tr>
                    <td class="bg-warning" width="7%">id</td>
                    <td class="bg-info">
                        <span id="jobId"></span>
                    </td>

                    <td class="bg-warning" width="7%">名称</td>
                    <td class="bg-info">
                        <span id="jobName"></span>
                    </td>

                    <td class="bg-warning" width="7%">状态</td>
                    <td class="bg-info">
                        <span id="status"></span>
                    </td>
                </tr>

                <tr>
                    <td class="bg-warning" width="7%">类型</td>
                    <td class="bg-info">
                        <span id="jobType"></span>
                    </td>

                    <td class="bg-warning" width="7%">内容</td>
                    <td class="bg-info">
                        <span id="content"></span>
                    </td>

                    <td class="bg-warning" width="7%">参数</td>
                    <td class="bg-info">
                        <span id="params"></span>
                    </td>
                </tr>

                <tr>
                    <td class="bg-warning" width="7%">权重</td>
                    <td class="bg-info">
                        <span id="priority"></span>
                    </td>

                    <td class="bg-warning" width="7%">提交者</td>
                    <td class="bg-info">
                        <span id="submitUser"></span>
                    </td>

                    <td class="bg-warning" width="7%">最后更新</td>
                    <td class="bg-info">
                        <span id="updateUser"></span>
                    </td>
                </tr>

                <tr>
                    <td class="bg-warning" width="7%">是否并行</td>
                    <td class="bg-info">
                        <span id="isSerial"></span>
                    </td>

                    <td class="bg-warning" width="7%">临时任务</td>
                    <td class="bg-info">
                        <span id="isTemp"></span>
                    </td>

                    <td class="bg-warning" width="7%">表达式</td>
                    <td class="bg-info">
                        <span id="expression"></span>
                    </td>
                </tr>

                <tr>
                    <td class="bg-warning" width="7%">应用</td>
                    <td class="bg-info">
                        <span id="appId"></span>
                    </td>

                    <td class="bg-warning" width="7%">worker组</td>
                    <td class="bg-info">
                        <span id="workerGroupId"></span>
                    </td>

                    <td class="bg-warning" width="7%">业务类型</td>
                    <td class="bg-info">
                        <span id="bizGroupId"></span>
                    </td>
                </tr>

                <tr>
                    <td class="bg-warning" width="7%">开始日期</td>
                    <td class="bg-info">
                        <span id="activeStartDate"></span>
                    </td>

                    <td class="bg-warning" width="7%">结束日期</td>
                    <td class="bg-info">
                        <span id="activeEndDate"></span>
                    </td>

                    <td class="bg-warning" width="7%">超时时长</td>
                    <td class="bg-info">
                        <span id="expiredTime"></span>
                    </td>
                </tr>

                <tr>
                    <td class="bg-warning" width="7%">重试次数</td>
                    <td class="bg-info">
                        <span id="failedAttempts"></span>
                    </td>

                    <td class="bg-warning" width="7%">重试间隔</td>
                    <td class="bg-info">
                        <span id="failedInterval"></span>
                    </td>

                    <td class="bg-warning" width="7%"></td>
                    <td class="bg-info">
                        <span></span>
                    </td>

                </tr>

                </tbody>
            </table>
        </div>
    </div>

    <div id="dependTree"></div>
</div>


<jsp:include page="../common/footer.jsp">
    <jsp:param name="menuMap" value="${menuMap}"/>
</jsp:include>

<script type="text/javascript">
    var jobVo =${jobVo};
</script>
<%--<script type="text/javascript" src="${contextPath}/assets/js/jarvis/job/delete_concept-graph.js" charset="UTF-8"></script>--%>

<script type="text/javascript" src="${contextPath}/assets/js/jarvis/job/detail.js"></script>


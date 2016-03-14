<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
    <jsp:param name="platform" value="${platform}"/>
    <jsp:param name="platforms" value="${platforms}"/>
</jsp:include>

<link type="text/css" rel="stylesheet" href="${contextPath}/assets/plugins/d3/d3-collapsible-tree.css"/>


<style>
    a:link {
        color: #31708f;
    }

    a:visited {
        color: #31708f;
    }
</style>


<div class="container">

    <div class="row">
        <div class="col-md-6">
            <nav>
                <ol class="cd-breadcrumb triangle">
                    <li><a href="${contextPath}/">首页</a></li>
                    <li><a href="${contextPath}/job">任务管理</a></li>
                    <li class="current"><em>任务依赖</em></li>
                </ol>
            </nav>
        </div>
        <div class="col-md-6 top-buffer">
               <span class="h3 pull-right">
                   任务:
                    <span>
                        <strong>
                            <a class="text-info" target="_blank"
                            href="${contextPath}/job/detail?jobId=${jobId}">${jobName}</a>
                        </strong>
                    </span>&nbsp依赖图&nbsp以及
                <span>
                    <strong>
                        <a class="text-info" target="_blank"
                           href="${contextPath}/task/?jobIdList=[${jobId}]&scheduleDate=${scheduleDate}">当天执行状态</a>
                    </strong>
                </span>
            </span>
        </div>
    </div>
    <hr>

    <div>
        <div>任务状态:&nbsp
            <span><i class="fa-circle fa-x"
                     style="color: #FFFFFF;border:1px solid steelblue;"></i>有效</span>
            <span><i class="fa-circle fa-x" style="color: #111211;background-color: #111211"></i>暂停</span>
            <span><i class="fa-circle fa-x" style="color: #111211;background-color: #111211"></i>过期</span>
            <span><i class="fa-circle fa-x" style="color: #111211;background-color: #111211"></i>禁用</span>
        </div>

        <div>任务"有效"时,当天的执行状态:&nbsp
            <span><i class="fa-circle fa-x" style="color: #FF851B;background-color: #FF851B"></i>等待</span>
            <span><i class="fa-circle fa-x" style="color: #FFDC00;background-color: #FFDC00"></i>准备</span>
            <span><i class="fa-circle fa-x" style="color: #0074D9;background-color: #0074D9"></i>运行</span>
            <span><i class="fa-circle fa-x" style="color: #2ECC40;background-color: #2ECC40"></i>成功</span>
            <span><i class="fa-circle fa-x" style="color: #FF4136;background-color: #FF4136"></i>失败</span>
            <span><i class="fa-circle fa-x" style="color: #111111;background-color: #111111"></i>终止</span>
            <span><i class="fa-circle fa-x" style="color: #AAAAAA;background-color: #AAAAAA"></i>混合状态</span>
            <span><i class="fa-circle fa-x"
                     style="color: #FFFFFF;border:1px solid steelblue;"></i>未执行</span>
        </div>

        <div id="pattern" style="display: none">
            <div class="row">
                <div class="col-md-11">
                    <dl>
                        <dd>
                            jobId:<span name="jobId" class="text-primary"></span>&nbsp&nbsp
                            提交者:<span name="submitUser" class="text-primary"></span>
                        </dd>
                        <dd>
                            类型:<span name="jobType" class="text-primary"></span>&nbsp&nbsp
                            任务状态:<span name="status" class="text-primary"></span>
                        </dd>
                        <dd>
                            优先级:<span name="priority" class="text-primary"></span>&nbsp&nbsp
                            业务组名:<span name="bizGroupName" class="text-primary"></span>
                        </dd>
                        <dd>
                            有效开始时间:<span name="activeStartDate" class="text-primary"></span>
                        </dd>
                        <dd>
                            有效结束时间:<span name="activeEndDate" class="text-primary"></span>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>

        <div id="toTaskModal" class="modal fade">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">请点击状态图标选择对应执行查看详情</h4>
                    </div>
                    <div class="modal-body">

                    </div>

                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" data-dismiss="modal">关闭</button>
                    </div>
                </div>
            </div>
        </div>


    </div>

    <div id="popoverContainer"></div>

    <div id="dependTree"></div>
</div>


<jsp:include page="../common/footer.jsp">
    <jsp:param name="menuMap" value="${menuMap}"/>
</jsp:include>

<script type="text/javascript">
    var query = {
        jobId:${jobId},
        showTaskStartTime:${showTaskStartTime},
        showTaskEndTime:${showTaskEndTime}
    };
    var jobId =${jobId};
    var stautsColor = {};
</script>
<script type="text/javascript" src="${contextPath}/assets/js/jarvis/job/concept-graph.js" charset="UTF-8"></script>

<script type="text/javascript" src="${contextPath}/assets/js/jarvis/job/dependency.js"></script>


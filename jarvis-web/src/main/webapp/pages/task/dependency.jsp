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
                    <li><a href="${contextPath}/task">Task管理</a></li>
                    <li class="current"><em>Task依赖</em></li>
                </ol>
            </nav>
        </div>
        <div class="col-md-6 top-buffer">
            <span class="h3 pull-right">
                Task:&nbsp
                <span>
                    <strong>
                        <a class="text-info" target="_blank"
                           href="${contextPath}/task/detail?taskId=${taskId}">${taskId}
                        </a>
                    </strong>的依赖图
                </span>
            </span>
        </div>
    </div>

    <hr>
    <div>
        <span><i class="fa fa-circle fa-2x" style="color: #FF851B"></i>等待</span>
        <span><i class="fa fa-circle fa-2x" style="color: #FFDC00"></i>准备</span>
        <span><i class="fa fa-circle fa-2x" style="color: #0074D9"></i>运行</span>
        <span><i class="fa fa-circle fa-2x" style="color: #2ECC40"></i>成功</span>
        <span><i class="fa fa-circle fa-2x" style="color: #FF4136"></i>失败</span>
        <span><i class="fa fa-circle fa-2x" style="color: #111111"></i>终止</span>


        <div id="pattern" style="display: none">
            <div class="row">
                <div class="col-md-11">
                    <dl>
                        <dd>
                            taskId:<span name="taskId" class="text-primary"></span>
                            执行人:<span name="executeUser" class="text-primary"></span>
                        </dd>
                        <dd>
                            计划调度:<span name="scheduleTime" class="text-primary"></span>
                        </dd>
                        <dd>开始时间:<span name="executeStartTime" class="text-primary"></span>
                        </dd>
                        <dd>结束时间:<span name="executeEndTime" class="text-primary"></span>
                        </dd>
                        <dd>时长:<span name="executeTime" class="text-primary"></span>
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
    var taskDependQo = ${taskDependQo};
</script>
<script type="text/javascript" src="${contextPath}/assets/js/jarvis/task/concept-graph.js" charset="UTF-8"></script>

<script type="text/javascript" src="${contextPath}/assets/js/jarvis/task/dependency.js"></script>


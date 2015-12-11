
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="../common/header.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
    <jsp:param name="platform" value="${platform}"/>
    <jsp:param name="platforms" value="${platforms}"/>
</jsp:include>

<link type="text/css" rel="stylesheet" href="/assets/jarvis/plugins/d3/d3-collapsible-tree.css" />


<style>
    .popover{
        max-width: 450px;
    }
</style>


<div class="container">

    <div class="row" >
        <div class="col-md-6">
            <nav>
                <ol class="cd-breadcrumb triangle">
                    <li><a href="${contextPath}/">首页</a></li>
                    <li ><a href="${contextPath}/plan">执行计划</a></li>
                    <li class="current"><em>执行情况</em></li>
                </ol>
            </nav>
        </div>
        <div class="col-md-6 top-buffer">
            <span class="h3 pull-right"><span class="text-info"><strong>${jobVo.jobName}</strong></span> 的执行情况</span>
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
        <span><i class="fa fa-circle fa-2x" style="color: #AAAAAA"></i>混合状态</span>
        <span><i class="fa fa-circle fa-2x" style="color: #B10DC9"></i>失效或过期</span>
        <span><i class="fa fa-circle fa-2x" style="color: #FFFFFF;border:1px solid steelblue;border-radius:50%;"></i>无执行</span>


        <div id="pattern" style="display: none">
            <div class="row" >
                <div class="col-md-1" >
                    <a name="status" href=""><i class="fa fa-circle" ></i></a>&nbsp;
                </div>

                <div class="col-md-11">
                    <dl>
                        <dd>
                            执行Id:<span name="taskId" class="text-primary"></span>&nbsp;
                            执行人:<span name="executeUser" class="text-primary"></span>&nbsp;

                        </dd>
                        <dd>
                            计划调度:<span name="scheduleTime" class="text-primary"></span>&nbsp
                            时长:<span name="executeTime" class="text-primary"></span>
                        </dd>
                        <dd>开始时间:<span name="executeStartTime" class="text-primary"></span>&nbsp;
                            结束:<span name="executeEndTime" class="text-primary"></span>&nbsp;
                        </dd>
                    </dl>
                </div>
            </div>
        </div>


        <div id="toTaskModal" class="modal fade">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
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


<jsp:include page="../common/login.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
</jsp:include>



<jsp:include page="../common/footer.jsp">
    <jsp:param name="menuMap" value="${menuMap}"/>
</jsp:include>

<script type="text/javascript">
    var taskId=${taskId};
    var stautsColor={};
</script>
<script type="text/javascript" src="${contextPath}/assets/js/jarvis/plan/concept-graph.js" charset="UTF-8"></script>

<script type="text/javascript" src="${contextPath}/assets/js/jarvis/plan/dependency.js"></script>


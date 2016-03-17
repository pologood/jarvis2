<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="../common/header.jsp">
    <jsp:param name="uname" value="${user.uname}"/>
    <jsp:param name="platform" value="${platform}"/>
    <jsp:param name="platforms" value="${platforms}"/>
</jsp:include>

<style>
    .modal-dialog {
        width: 900px;
    }
</style>


<div class="container">

    <div class="row">
        <div class="col-md-6">
            <nav>
                <ol class="cd-breadcrumb triangle">
                    <li><a href="${contextPath}/">首页</a></li>
                    <li class="current"><em>执行流水</em></li>
                </ol>
            </nav>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">

            <div class="row">
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:50%">调度日期</span>
                        <input id="scheduleDate" class="form-control"/>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:50%">执行日期</span>
                        <input id="executeDate" class="form-control"/>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:50%">执行开始日期>=</span>
                        <input id="startDate" class="form-control"/>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:50%">执行结束日期<</span>
                        <input id="endDate" class="form-control"/>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:50%">任务ID</span>
                        <select id="jobId" multiple>

                        </select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:50%">任务名称</span>
                        <select id="jobName" multiple>

                        </select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:50%">任务类型</span>
                        <select id="jobType" multiple></select>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:50%">执行用户</span>
                        <select id="executeUser" multiple>
                        </select>
                    </div>
                </div>

            </div>

            <div class="row top-buffer">

                <div class="col-md-6">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:23.5%">状态</span>

                        <div class="form-control" id="taskStatus">
                        </div>
                    </div>
                </div>

                <div class="col-md-3 col-md-offset-3">
                    <div class="row">
                        <div class="col-md-6 col-md-offset-6">
                            <div class="input-group pull-right">
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
            <div id="toolBar">
                <span><i class="fa fa-circle fa-2x" style="color: #FF851B"></i>等待</span>
                <span><i class="fa fa-circle fa-2x" style="color: #FFDC00"></i>准备</span>
                <span><i class="fa fa-circle fa-2x" style="color: #0074D9"></i>运行</span>
                <span><i class="fa fa-circle fa-2x" style="color: #2ECC40"></i>成功</span>
                <span><i class="fa fa-circle fa-2x" style="color: #FF4136"></i>失败</span>
                <span><i class="fa fa-circle fa-2x" style="color: #111111"></i>终止</span>
                <span><i class="fa fa-circle fa-2x" style="color: #ab279d"></i>删除</span>
            </div>
            <table id="content" data-toolbar="#toolBar">

            </table>

        </div>

    </div>

    <!-- 显示 task history的模态框 -->
    <div id="taskHistoryModal" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title">执行流水重试记录</h4>
                </div>
                <div class="modal-body">
                    <table id="taskHistory">

                    </table>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary">确定</button>
                </div>
            </div>
        </div>
    </div>


</div>


<jsp:include page="../common/footer.jsp">
    <jsp:param name="menuMap" value="${menuMap}"/>
</jsp:include>

<script type="text/javascript">
    var taskQo = ${taskQo};
</script>

<script type="text/javascript" src="${contextPath}/assets/js/jarvis/task/task.js"></script>

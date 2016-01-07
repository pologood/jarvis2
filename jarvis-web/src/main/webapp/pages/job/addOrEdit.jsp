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
                    <li><a href="${contextPath}/job">任务管理</a></li>
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
            <input type="hidden" id="jobId" desc="任务id" value="${jobVo.jobId}"/>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务名称<span class="text-danger"
                                                                                    style="vertical-align: middle">*</span></span>
                        <input id="jobName" class="form-control" desc="任务名称" value="${jobVo.jobName}"
                               onblur="checkJobName(this)"/>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">起始时间</span>

                        <input id="activeStartTime" class="form-control"
                               value="<fmt:formatDate value="${jobVo.activeStartDate}" pattern="yyyy-MM-dd"></fmt:formatDate>"/>

                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">结束时间</span>
                        <input id="activeEndTime" class="form-control"
                               value="<fmt:formatDate value="${jobVo.activeEndDate}" pattern="yyyy-MM-dd"></fmt:formatDate>"/>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务类型<span class="text-danger"
                                                                                    style="vertical-align: middle">*</span></span>
                        <select id="jobType" desc="任务类型">

                        </select>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">Worker Group<span class="text-danger"
                                                                                            style="vertical-align: middle">*</span></span>
                        <select id="workerGroupId" desc="Worker Group">
                            <c:forEach items="${WorkerGroupVoList}" var="workerGroup" varStatus="status">
                                <option value="${workerGroup.id}"
                                        <c:choose>
                                            <c:when test="${jobVo.workerGroupId==workerGroup.id}">selected</c:when>
                                        </c:choose>  >${workerGroup.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">执行内容<span class="text-danger"
                                                                                    style="vertical-align: middle">*</span></span>
                        <textarea id="content" class="form-control" desc="执行内容" rows="4"
                                  onclick="changeTextArea(this,15,10)"
                                  onblur="changeTextArea(this,4,10)">${jobVo.content}</textarea>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">任务参数</span>
                        <input id="parameters" class="form-control" value='<c:choose><c:when test="${jobVo.params!=null}">${jobVo.params}</c:when><c:otherwise>{}</c:otherwise></c:choose>' onclick="showParaModel()"/>
                    </div>
                </div>

                <div id="paraModal" class="modal fade">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                        aria-hidden="true">&times;</span></button>
                                <h4 class="modal-title">任务参数</h4>
                            </div>
                            <div class="modal-body">
                                <table id="pattern" style="display: none">
                                    <tr>
                                        <td>
                                            <input name="key" class="form-control" placeholder="请输入属性的key">
                                        </td>
                                        <td>
                                            <input name="value" class="form-control" placeholder="请输入属性的value">
                                        </td>
                                        <td>
                                            <a class="glyphicon glyphicon-plus" href="javascript:void(0)"
                                               onclick="addPara(this)"></a>
                                            <a class="glyphicon glyphicon-minus" href="javascript:void(0)"
                                               onclick="deletePara(this)"></a>
                                        </td>
                                    </tr>
                                </table>

                                <table id="paras" class="table table-bordered">
                                    <thead>
                                    <tr>
                                        <th>key</th>
                                        <th>value</th>
                                        <th>操作</th>
                                    </tr>

                                    </thead>
                                    <tbody>

                                    </tbody>
                                </table>
                                <a class="glyphicon glyphicon-plus" href="javascript:void(0)"
                                   onclick="addPara(null)"></a>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                                <button type="button" class="btn btn-primary" onclick="ensurePara()">确定</button>
                            </div>
                        </div>
                        <!-- /.modal-content -->
                    </div>
                    <!-- /.modal-dialog -->
                </div>
                <!-- /.modal -->
            </div>



            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">表达式类型</span>
                        <select id="expressionType">

                        </select>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">表达式</span>
                        <input id="expression" class="form-control" value="${jobVo.expression}"/>
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
                        <select id="dependJobIds" multiple>
                            <c:forEach items="${jobVoList}" var="job" varStatus="status">
                                <c:choose>
                                    <c:when test="${jobVo!=null}">
                                        <c:choose>
                                            <c:when test="${jobVo.jobId!=job.jobId}">
                                                <option value="${job.jobId}">${job.jobName}</option>
                                            </c:when>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${job.jobId}">${job.jobName}</option>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>
            <div class="row top-buffer">
                <div class="col-md-5 col-md-offset-4">

                    <div id="strategyPattern" style="display: none">

                        <div class="col-md-9" style="margin-bottom: 2px">
                            <div class="input-group commonStrategy" style="width:100%">
                                <span name="dependJob" class="input-group-addon"
                                      style="width:50%;background-color:#d9edf7"></span>
                                <span class="input-group-addon" style="width:10%">通用</span>
                                <select name="commonStrategy" class="form-control">

                                </select>
                            </div>
                        </div>

                        <div class="col-md-3" style="margin-bottom: 2px">
                            <div class="input-group offsetStrategy" style="margin-left:-25px;margin-right: -15px"
                                 style="width:100%">
                                <span class="input-group-addon" style="width:30%;">偏移</span>
                                <input name="offsetStrategy" class="form-control" value="cd" placeholder="默认cd为当天"/>
                            </div>
                        </div>

                    </div>

                    <dl id="strategyList">

                    </dl>
                </div>
                <span>
                    <i class="fa fa-question text-info fa-2x"
                       style="cursor: pointer;position: relative;position: absolute;margin-left:10px;"
                       onmouseover="showDescription(this)" onmouseout="hideDescription(this)"></i>
                </span>
            </div>


            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">拒绝重试数</span>
                        <input id="rejectRetries" class="form-control" value="<c:choose><c:when test="${jobVo.failedAttempts!=null}">${jobVo.failedAttempts}</c:when><c:otherwise>0</c:otherwise></c:choose>" desc="失败重试数"
                               placeholder="0" onblur="checkNum(this)"/>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">拒绝重试间隔(秒)</span>
                        <input id="rejectInterval" class="form-control" value="<c:choose><c:when test="${jobVo.failedInterval!=null}">${jobVo.failedInterval}</c:when><c:otherwise>3</c:otherwise></c:choose>" desc="失败重试间隔(秒)"
                               placeholder="3" onblur="checkNum(this)"/>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">失败重试数</span>
                        <input id="failedRetries" class="form-control" value="<c:choose><c:when test="${jobVo.failedAttempts!=null}">${jobVo.failedAttempts}</c:when><c:otherwise>0</c:otherwise></c:choose>" desc="失败重试数"
                               placeholder="0" onblur="checkNum(this)"/>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">失败重试间隔(秒)</span>
                        <input id="failedInterval" class="form-control" value="<c:choose><c:when test="${jobVo.failedInterval!=null}">${jobVo.failedInterval}</c:when><c:otherwise>3</c:otherwise></c:choose>" desc="失败重试间隔(秒)"
                               placeholder="3" onblur="checkNum(this)"/>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">报警人</span>
                        <select id="alarm" multiple></select>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">报警类型</span>
                        <div id="alarmType" class="form-control">
                            <input type="checkbox" onclick="changeAll(this)">全部
                        </div>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">报警启用/禁用</span>
                        <div id="alarmStatus" class="form-control">
                            <input name="alarmStatus" type="radio" value="0" >禁用
                            <input name="alarmStatus" type="radio" value="1" checked="checked">启用
                        </div>

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
    var jobType = undefined;
    var jobPriority = undefined;
    var dependIds = undefined;
    var expressionType = undefined;
    var expression = undefined;
    var dependJobs = undefined;
    var existAlarmList = undefined;
    <c:choose>
    <c:when test="${jobVo!=null}">
    jobType = '${jobVo.jobType}';
    jobPriority = '${jobVo.priority}';
    dependIds = '${dependIds}';
    expressionType = '${jobVo.expressionType}';
    expression = '${jobVo.expression}';
    dependJobs = '${dependJobs}';
    existAlarmList = '${existAlarmList}';
    </c:when>
    </c:choose>
</script>

<script type="text/javascript" src="${contextPath}/assets/js/jarvis/job/addOrEdit.js"></script>



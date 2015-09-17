
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
                    <li class="current"><em>任务触发</em></li>
                </ol>
            </nav>
        </div>
    </div>

    <div class="row top-buffer">
        <div class="col-md-12">

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">选择脚本</span>
                        <select id="content"  ></select>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">开始日期</span>
                        <input id="jobStart"  class="form-control"/>
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">结束日期</span>
                        <input id="jobEnd"  class="form-control" />
                    </div>
                </div>
            </div>

            <div class="row top-buffer">
                <div class="col-md-6 col-md-offset-3">
                    <div class="input-group" style="width:100%">
                        <span class="input-group-addon" style="width:35%">重跑后续依赖任务</span>
                        <div class="form-control">
                            <input id="reRunNext" type="checkbox"/>
                        </div>

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

<script type="text/javascript" src="/assets/jarvis/js/jarvis/trigger/trigger.js"></script>

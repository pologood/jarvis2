
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
                    <li><a href="${contextPath}/">首页</a></li>
                    <li><a href="${contextPath}/manage/app">应用管理</a></li>
                    <li class="current"><em>
                        <c:choose>
                            <c:when test="${appVo==null}">
                                新增应用
                            </c:when>
                            <c:otherwise>
                                编辑应用
                            </c:otherwise>
                        </c:choose>
                    </em></li>
                </ol>
            </nav>
        </div>
    </div>

    <!-- 应用id -->
    <input id="appId" type="hidden" value="${appVo.appId}">

    <div class="row top-buffer">
        <div class="col-md-6 col-md-offset-3">
            <div class="input-group" style="width:100%">
                <span class="input-group-addon" style="width:35%">应用名称</span>
                <input class="form-control" id="appName" value="${appVo.appName}" onblur="checkAppName()"/>
            </div>
        </div>
        <!--
        <div class="col-md-6 col-md-offset-3 top-buffer">
            <div class="input-group" style="width:100%">
                <span class="input-group-addon" style="width:35%">状态</span>
                <select id="status">
                    <option value="0" <c:choose><c:when test="${appVo!=null&&appVo.status==0}">selected</c:when></c:choose> >停用</option>
                    <option value="1" <c:choose><c:when test="${appVo!=null&&appVo.status==1}">selected</c:when></c:choose> >启用</option>
                </select>
            </div>
        </div>
        -->

        <div class="col-md-6 col-md-offset-3 top-buffer text-center">
            <div class="input-group" style="width:100%">
                <c:choose>
                    <c:when test="${appVo!=null}">
                        <button type="button" class="btn btn-default" onclick="updateApp()">更新</button>
                    </c:when>
                    <c:otherwise>
                        <button type="button" class="btn btn-default" onclick="addApp()">新增</button>
                    </c:otherwise>
                </c:choose>
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

<script type="text/javascript" src="${contextPath}/js/jarvis/manage/appAddOrEdit.js"></script>
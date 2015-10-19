
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="./common/header.jsp">
  <jsp:param name="uname" value="${user.uname}"/>
  <jsp:param name="platform" value="${platform}"/>
  <jsp:param name="platforms" value="${platforms}"/>
</jsp:include>


<div class="container">

  <div class="row">
    <div class="col-md-6">
      <nav>
        <ol class="cd-breadcrumb triangle">
          <li><a href="/admin/">首页</a></li>
          <li class="current"><em>平台管理</em></li>
        </ol>
      </nav>
    </div>
  </div>

  <div class="row top-buffer">
    <div class="col-md-12">
      index
    </div>
  </div>
</div>


<jsp:include page="./common/login.jsp">
  <jsp:param name="uname" value="${user.uname}"/>
</jsp:include>


<jsp:include page="./common/footer.jsp">
  <jsp:param name="menuMap" value="${menuMap}"/>
</jsp:include>

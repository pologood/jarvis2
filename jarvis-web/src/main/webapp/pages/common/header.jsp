<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="contextPath" scope="application" value="<%=request.getContextPath()%>" />

<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" href="${contextPath}/assets/img/favicon.ico" type="image/x-icon">
    <link rel="stylesheet" href="${contextPath}/assets/css/reset.css" type="text/css" charset="utf-8">
    <link rel="stylesheet" href="${contextPath}/assets/css/style.css" type="text/css" charset="utf-8">
    <link rel="stylesheet" href="${contextPath}/assets/plugins/bootstrap-3.3.5/css/bootstrap.min.css" type="text/css" charset="utf-8">
    <link rel="stylesheet" href="${contextPath}/assets/plugins/bootstrap-3.3.5/css/bootstrap-theme.min.css" type="text/css" charset="utf-8">
    <link rel="stylesheet" href="${contextPath}/assets/plugins/font-awesome/css/font-awesome.min.css" type="text/css" charset="utf-8">
    <link rel="stylesheet" href="${contextPath}/assets/css/login.css" type="text/css" charset="utf-8">
    <link rel="stylesheet" href="${contextPath}/assets/plugins/bootstrap-datetimepicker/css/bootstrap-datetimepicker.min.css" type="text/css" charset="utf-8">
    <link rel="stylesheet" href="${contextPath}/assets/plugins/bootstrap-switch/css/bootstrap3/bootstrap-switch.min.css" type="text/css" charset="utf-8">
    <link rel="stylesheet" href="${contextPath}/assets/plugins/back-to-top/css/style.css" type="text/css" charset="utf-8">
    <link rel="stylesheet" href="${contextPath}/assets/css/cart.css" type="text/css" charset="utf-8">
    <link rel="stylesheet" href="${contextPath}/assets/plugins/breadcrumbs/css/style.css" type="text/css" charset="utf-8">
    <link rel="stylesheet" href="${contextPath}/assets/css/main.css" type="text/css" charset="utf-8">
    <link rel="stylesheet" href="${contextPath}/assets/plugins/select2/4.0.1/css/select2.min.css" type="text/css" charset="utf-8">
    <link type="text/css" rel="stylesheet" href="${contextPath}/assets/plugins/qtip2/jquery.qtip.min.css" />
    <link type="text/css" rel="stylesheet" href="${contextPath}/assets/plugins/pnotify/pnotify.custom.min.css" />
    <link type="text/css" rel="stylesheet" href="${contextPath}/assets/plugins/bootstrap-table/bootstrap-table.min.css" />
    <link type="text/css" rel="stylesheet" href="${contextPath}/assets/css/select2_special.css" />
    <link type="text/css" rel="stylesheet" href="${contextPath}/assets/plugins/jstree/3.2.1/themes/default/style.min.css" />



    <script src="${contextPath}/assets/plugins/modernizr/modernizr.js" type="application/javascript" charset="utf-8"></script>
    <script src="${contextPath}/assets/plugins/jquery/jquery-2.1.4.min.js" type="application/javascript" charset="utf-8"></script>


    <script>
        var contextPath = "${contextPath}";
    </script>
    <title>蘑菇街 - 大数据</title>
</head>
<body>
<header>

  <div id="cd-logo" class="bdmenu">
    <ul>
      <li class="active">
        <a  href="${contextPath}/">${platform.name}</a>
      </li>
    </ul>
  </div>


    <nav id="cd-top-nav" class="navbar">
        <ul class="main-menu">
            <c:forEach var="m" items="${menu}">
                <c:choose>
                    <c:when test="${m.menuMap.size() > 1}">
                        <li class="main-menu dropdown">
                            <a class="main-menu dropdown-toggle <c:if test="${m.isCurrent}">current</c:if>"  data-toggle="dropdown" href="javascript:void(0);">${m.name}</a>
                            <ul class="dropdown-menu">

                                <c:forEach var="sm" items="${m.menuMap}">
                                    <li <c:if test="${currentUri == sm.value}">class="active"</c:if>><a href="${contextPath}${sm.value}">${sm.key}</a></li>
                                </c:forEach>
                            </ul>
                        </li>
                    </c:when>
                    <c:when test="${m.menuMap.size() == 1}">
                        <c:forEach var="sm" items="${m.menuMap}">
                            <li class="main-menu"><a class="main-menu <c:if test="${m.isCurrent}">current</c:if>"  href="${contextPath}${sm.value}">${m.name}</a></li>
                        </c:forEach>
                    </c:when>
                </c:choose>
            </c:forEach>
        </ul>
    </nav>


    <c:choose>
        <c:when test="${null != user && user.uname != ''}">
            <a id="cd-menu-trigger" href="javascript:void(0);">
                <span class="cd-menu-text">${user.uname}</span>
            </a>
        </c:when>
        <c:otherwise>
            <a id="cd-sign-trigger" class="cd-signin" href="javascript:void(0);">
                <span class="cd-menu-text">Login</span>
            </a>
        </c:otherwise>
    </c:choose>


</header>

<main class="cd-main-content">
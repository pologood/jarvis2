<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="FilterTypeAdpater" class="com.mogu.bigdata.report.common.sql.filters.FilterTypeAdapter"/>



<div>
    <c:forEach items="${filters}" var="filter">
        <c:choose>
            <c:when test="${filter.type == FilterTypeAdpater.date()}">
                <div class="row" style="margin-top: 15px;">
                    <div class="col-md-3" >
                        <div class="input-group">
                            <span class="input-group-addon">开始日期</span>
                            <input class="date input-sm" name="startDate" type="text" value="${startDate}" placeholder="开始日期">
                        </div>
                    </div>

                    <div class="col-md-3">
                        <div class="input-group">
                            <span class="input-group-addon">结束日期</span>
                            <input class="date input-sm" name="endDate" type="text" value="${endDate}"  placeholder="结束日期">
                        </div>
                    </div>
                </div>
            </c:when>

            <c:when test="${filter.type == FilterTypeAdpater.text()}">
                <div class="row" style="margin-top: 15px;">
                    <div class="col-md-10" >
                        <div class="input-group">
                            <span class="input-group-addon">${filter.name}</span>
                            <input class="input-sm" name="${filter.field}" type="text"  placeholder="${filter.name}">
                        </div>
                    </div>
                </div>
            </c:when>
            <c:when test="${filter.type == FilterTypeAdpater.select()}">
                <div class="row" style="margin-top: 15px;">
                    <div class="col-md-10" >
                        <div class="input-group">
                            <span class="input-group-addon">${filter.name}</span>
                            <select class="select-single form-control" name="${filter.field}">
                                <option value="">全部</option>
                                <c:forEach items="${filterMap.get(filter.field)}" var="op">
                                    <option <c:if test="${params.get(filter.field) == op}" >selected="selected"</c:if>  >${op}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </c:when>

            <c:when test="${filter.type == FilterTypeAdpater.multiSelect()}">
                <div class="row" style="margin-top: 15px;">
                    <div class="col-md-10" >
                        <div class="input-group">
                            <span class="input-group-addon">${filter.name}</span>
                            <select>
                                <option></option>
                            </select>
                        </div>
                    </div>
                </div>
            </c:when>

        </c:choose>
    </c:forEach>
</div>


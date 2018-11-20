<%@page import="com.emg.projectsmanage.common.CommonConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<script>
<%
	String account = session.getAttribute(CommonConstants.SESSION_USER_NAME).toString();
%>
</script>
<div class="headline">
	<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header" style="width: 12%; min-width: 144px;">
				<a class="navbar-brand" href="#" title="这里什么都没有--"><strong>项目管理系统</strong></a>
			</div>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<c:forEach items="${menus }" var="menu">
						<c:choose>
							<c:when test="${menu.active }">
								<li class="active"><a href="${menu.url }">${menu.label }</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="${menu.url }">${menu.label }</a></li>
    						</c:otherwise>
						</c:choose>
					</c:forEach>
				</ul>

				<ul class="nav navbar-nav navbar-right">
					<li><a href="#"><c:out value="<%=account%>" /></a></li>
					<li><a href="<c:url value='./logout.web'/>">退出</a></li>
				</ul>
			</div>
		</div>
	</nav>
</div>

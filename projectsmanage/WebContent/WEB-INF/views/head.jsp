<%@page import="com.emg.poiwebeditor.common.CommonConstants"%>
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
<script src="resources/bootstrap-hover-dropdown/bootstrap-hover-dropdown.js" ></script>
<div class="headline">
	<nav class="navbar navbar-default navbar-fixed-top" role="navigation">
		<div class="container-fluid">
			<div class="navbar-header" style="width: 12%; min-width: 144px;">
				<a class="navbar-brand" href="#" title="这里什么都没有--"><strong>POI线上编辑器</strong></a>
			</div>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<c:forEach items="${menus }" var="menu">
						<c:choose>
							<c:when test="${menu.children != null and menu.children.size() > 0 }">
								<li class="${menu.active ? 'active':'' } dropdown">
									<a href="${menu.url }" class="dropdown-toggle" data-hover="dropdown">${menu.label }&nbsp;<span class="caret"></span></a>
									<c:if test="${menu.children != null and menu.children.size() > 0 }">
										<ul class="dropdown-menu">
											<c:forEach items="${menu.children }" var="child">
												<li class="${child.active ? 'active':'' }"><a href="${child.url }">${child.label }</a></li>
											</c:forEach>
										</ul>
									</c:if>
								</li>
							</c:when>
							<c:otherwise>
								<li class="${menu.active ? 'active':'' }">
									<a href="${menu.url }">${menu.label }</a>
								</li>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</ul>

				<ul class="nav navbar-nav navbar-right">
					<li><a href="#"><span class="glyphicon glyphicon-user"></span>&nbsp;<c:out value="<%=account%>" /></a></li>
					<li><a href="<c:url value='./logout.web'/>"><span class="glyphicon glyphicon-log-out"></span>&nbsp;退出</a></li>
				</ul>
			</div>
		</div>
	</nav>
</div>

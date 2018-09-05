<%@page import="com.emg.projectsmanage.common.ParamUtils"%>
<%@page import="com.emg.projectsmanage.common.CommonConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<script>
<%!String getMenuCode(String url) {
		if (url.indexOf("/systemsets.web") > 0) {
			return "990";
		} else if (url.indexOf("/usersmanage.web") > 0) {
			return "999";
		} else if (url.indexOf("/skillsmanage.web") > 0) {
			return "1000";
		} else if (url.indexOf("/processesmanage.web") > 0) {
			return "1001";
		} else if (url.indexOf("/projectsmanage.web") > 0) {
			return "1002";
		} else if (url.indexOf("/worktasks.web") > 0) {
			return "1003";
		} else if (url.indexOf("/projectsprocess.web") > 0) {
			return "1004";
		} else if (url.indexOf("/capacitycount.web") > 0) {
			return "1005";
		} else if (url.indexOf("/itemsetmanage.web") > 0) {
			return "1006";
		} else if (url.indexOf("/errorsetmanage.web") > 0) {
			return "1007";
		} else if (url.indexOf("/errorsmanage.web") > 0) {
			return "1008";
		} else if (url.indexOf("/iteminfo.web") > 0) {
			return "1009";
		}
		return "0000";
	}%>
<%
	String menucode = getMenuCode(ParamUtils.getAttribute(request, "fromurl"));
	String account = session.getAttribute(CommonConstants.SESSION_USER_NAME).toString();
%>
</script>
<div class="headline">
	<nav class="navbar navbar-default">
		<div class="container-fluid">
			<div class="navbar-header" style="width: 16%; min-width: 160px;">
				<a class="navbar-brand headicon" href="#"></a> <span
					class="headword"><strong>项目管理系统</strong></span>
			</div>
			<div class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<sec:authorize access="hasAnyRole('ROLE_ADMIN' ,'ROLE_SUPERADMIN')">
						<li class="<%="999".equals(menucode) ? "active" : ""%>"><a
							href="<c:url value='usersmanage.web'/>">人员信息管理</a></li>
					</sec:authorize>
					<sec:authorize access="hasAnyRole('ROLE_POIVIDEOEDIT' )">
						<li class="<%="1001".equals(menucode) ? "active" : ""%>"><a
							href="<c:url value='processesmanage.web'/>">项目管理</a></li>
					</sec:authorize>
					<sec:authorize
						access="hasAnyRole('ROLE_POIVIDEOEDIT' ,'ROLE_WORKER' ,'ROLE_CHECKER')">
						<li class="<%="1003".equals(menucode) ? "active" : ""%>"><a
							href="<c:url value='worktasks.web'/>">作业任务</a></li>
					</sec:authorize>
					<sec:authorize
						access="hasAnyRole('ROLE_POIVIDEOEDIT' ,'ROLE_WORKER' ,'ROLE_CHECKER')">
						<li class="<%="1004".equals(menucode) ? "active" : ""%>"><a
							href="<c:url value='projectsprocess.web'/>">项目进度</a></li>
					</sec:authorize>
					<sec:authorize
						access="hasAnyRole('ROLE_POIVIDEOEDIT' ,'ROLE_WORKER' ,'ROLE_CHECKER')">
						<li class="<%="1005".equals(menucode) ? "active" : ""%>"><a
							href="<c:url value='capacitycount.web'/>">产能统计</a></li>
					</sec:authorize>
					<sec:authorize
						access="hasAnyRole('ROLE_POIVIDEOEDIT' )">
						<li class="<%="1008".equals(menucode) ? "active" : ""%>"><a
							href="<c:url value='errorsmanage.web'/>">错误导出</a></li>
					</sec:authorize>
					<sec:authorize access="hasAnyRole('ROLE_POIVIDEOEDIT' )">
						<li class="<%="1006".equals(menucode) ? "active" : ""%>"><a
							href="<c:url value='itemsetmanage.web'/>">质检集合配置</a></li>
					</sec:authorize>
					<sec:authorize access="hasAnyRole('ROLE_POIVIDEOEDIT' )">
						<li class="<%="1007".equals(menucode) ? "active" : ""%>"><a
							href="<c:url value='errorsetmanage.web'/>">错误筛选配置</a></li>
					</sec:authorize>
					<sec:authorize access="hasAnyRole('ROLE_POIVIDEOEDIT' )">
						<li class="<%="1009".equals(menucode) ? "active" : ""%>"><a
							href="<c:url value='iteminfo.web'/>">质检项配置</a></li>
					</sec:authorize>
					<sec:authorize access="hasAnyRole('ROLE_POIVIDEOEDIT' )">
						<li class="<%="990".equals(menucode) ? "active" : ""%>"><a
							href="<c:url value='systemsets.web'/>">系统配置</a></li>
					</sec:authorize>
				</ul>

				<ul class="nav navbar-nav navbar-right" style="width: 24%;">
					<li><a href="#"><c:out value="<%=account%>" /></a></li>
					<li id="limsg"></li>
					<li><a href="<c:url value='./logout.web'/>">退出</a></li>
				</ul>
			</div>
		</div>
	</nav>
</div>
<script type="text/javascript">
	$("#loginbtn").bind('click', function() {
		$.webeditor.gotoLogin('<c:url value="/"/>');
	});
</script>

<div></div>
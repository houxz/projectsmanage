<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<title>欢迎</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<meta name="robots" content="nofollow" />

<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css"
	rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />

<script src="resources/jquery/jquery-3.2.1.min.js" ></script>
<script src="resources/js/jquery.formvalidate.js" ></script>
<script src="resources/js/validatezh.js" ></script>
<script src="resources/js/webeditor.js" ></script>

<script type="text/javascript">
	$(document).ready(function() {
		$('#f').submit(function(e) {
			e.preventDefault();
			$(this).formvalidate({
				failureMessages : true,
				successMessages : true,
				messageFailureClass : 'label label-warning',
				messageSuccessClass : 'label label-success',
				language : 'zh'
			});
		});
	});
</script>
</head>
<body onload="document.f.username.focus();">
	<div class="container">
		<div id="headdiv"></div>
		<div class="row">
			<div class="col-md-offset-1">
				<h1>项目管理系统</h1>
			</div>
			<div class="col-md-offset-1 col-md-3" style="margin-top: 60px;">
				<c:if test="${not empty param.login_error }">
					<c:choose>
						<c:when test="${param.login_error == 1}">
							<div class="label label-warning">您的登录没有成功。</div>
							<br />
							<div class="label label-warning">
								<c:out value="用户名或密码不正确，请重新输入。" />
							</div>
						</c:when>
						<c:when test="${param.login_error == 2}">
							<div class="label label-warning">您的登录没有成功。</div>
							<br />
							<div class="label label-warning">
								<c:out value="没有权限，请联系管理员分配权限。" />
							</div>
						</c:when>
						<c:otherwise>
							<div class="label label-warning">操作没有成功。</div>
							<br />
							<div class="label label-warning">
								<c:out value="未知原因。" />
							</div>
						</c:otherwise>
					</c:choose>
					<%
						session.invalidate();
					%>
				</c:if>
				<c:if test="${not empty param.logout }">
					<c:choose>
						<c:when test="${param.logout == 1}">
							<div class="label label-warning">您已退出登录。</div>
							<br />
							<div class="label label-warning">
								<c:out value="如需要，请重新登录。" />
							</div>
						</c:when>
						<c:when test="${param.logout == 2}">
							<div class="label label-warning">登录超时。</div>
							<br />
							<div class="label label-warning">
								<c:out value="请重新登录。" />
							</div>
						</c:when>
						<c:otherwise>
							<div class="label label-warning">退出失败。</div>
							<br />
							<div class="label label-warning">
								<c:out value="未知原因。" />
							</div>
						</c:otherwise>
					</c:choose>
					<%
						session.invalidate();
					%>
				</c:if>
				<form name="f" id="f" method="post"
					action="<c:url value='./login'/>">
					<div class="form-group">
						<label for="username">用户名：</label> <input type="text"
							class="form-control required" id="username" name="username"
							placeholder="请输入您的用户名" data-trim maxlength="50" />
					</div>
					<div class="form-group">
						<label for="password">密码：</label> <input type="password"
							class="form-control required" id="password" name="password"
							placeholder="请输入密码" data-trim maxlength="50"
							onkeypress="if (event.keyCode==13) { document.f.submit(); }" />
					</div>
					<button type="submit" class="btn btn-default" name="login">登陆</button>
				</form>
			</div>
			<div class="col-md-offset-1 col-md-3" style="margin-top: 60px;">

			</div>
		</div>
	</div>
</body>
</html>

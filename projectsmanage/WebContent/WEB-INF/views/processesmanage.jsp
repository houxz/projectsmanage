<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<title>流程管理</title>
<meta charset="UTF-8" />
<meta name="robots" content="none">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css" rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />
<link href="resources/bootstrap-table-1.11.1/bootstrap-table.min.css" rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/bootstrap-table.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/extensions/filter-control/bootstrap-table-filter-control.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/locale/bootstrap-table-zh-CN.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$.webeditor.getHead();
		//$.webeditor.getFoot();
	});
</script>

</head>
<body>
	<div class="container">
		<div id="headdiv"></div>
		<div style="width:100%;height:200px;padding-top: 20px;display: flex;background-color: aliceblue;">
			<div style="width:20%;height:100px;background-color: aquamarine;">
			</div>
			<div style="width:80%;height:100px;background-color: bisque;">
			</div>
		</div>
		<div id="footdiv"></div>
	</div>
</body>
</html>
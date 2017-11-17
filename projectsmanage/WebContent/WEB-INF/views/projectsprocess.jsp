<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<title>项目进度</title>
<meta charset="UTF-8" />
<meta name="robots" content="none">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css" rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />
<link href="resources/jquery-ui-1.12.1.custom/jquery-ui.min.css" rel="stylesheet">
<link href="resources/bootstrap-table-1.11.1/bootstrap-table.min.css" rel="stylesheet">
<link href="resources/css/message.css" rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/bootstrap-table.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/extensions/filter-control/bootstrap-table-filter-control.js"></script>
<script src="resources/bootstrap-table-1.11.1/locale/bootstrap-table-zh-CN.js"></script>
<script src="resources/js/message.js"></script>


<script type="text/javascript">
	$(document).ready(function() {
		$.webeditor.getHead();
		$('[data-toggle="projectsprocess"]').bootstrapTable({
			locale : 'zh-CN'
		});
	});
	function queryParams(params) {
		if (params.filter != undefined) {
			var filterObj = eval('(' + params.filter + ')');
			if (filterObj.state != undefined) {
				filterObj["state"] = filterObj.state;
				delete filterObj.state;
				params.filter = JSON.stringify(filterObj);
			}
		}
		return params;
	}
	function idleFormat(value, row, index) {
		return row.totaltask - row.edittask - row.qctask - row.checktask
				- row.completetask;
	}
</script>

</head>
<body>
	<div class="container">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px">
			<table id="projectsprocesslist" data-unique-id="id"
				data-query-params="queryParams"
				data-url="./projectsprocess.web?atn=pages"
				data-side-pagination="server" data-filter-control="true"
				data-pagination="true" data-toggle="projectsprocess"
				data-height="714" data-page-list="[15, 30, 50, All]"
				data-page-size="15" data-search-on-enter-key='true'
				data-align='center'>
				<thead>
					<tr>
						<th data-field="projectid" data-width="70" data-filter-control="input" data-filter-control-placeholder="">项目编号</th>
						<th data-field="projectname" data-width="320" data-filter-control="input" data-filter-control-placeholder="">项目名称</th>
						<th data-field="totaltask">任务总数</th>
						<th data-field="idletask" data-formatter='idleFormat'>空闲任务</th>
						<th data-field="edittask">编辑中任务</th>
						<th data-field="qctask">质检中任务</th>
						<th data-field="checktask">校正中任务</th>
						<th data-field="completetask">已完成任务</th>
					</tr>
				</thead>
			</table>
		</div>
		<div id="footdiv"></div>
	</div>
</body>
</html>
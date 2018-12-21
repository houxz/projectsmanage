<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<title>作业任务</title>
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
	var roleTypes = {5:"制作",6:"校正"};
	var processTypes = eval('(${processTypes})');
	
	function roleFormat(value, row, index) {
		return roleTypes[row.roleid];
	}
	function processTypesFormat(value, row, index) {
		return processTypes[row.processtype];
	}
	
	$(document).ready(function() {
		$.webeditor.getHead();
		$('[data-toggle="worktasks"]').bootstrapTable({
			locale : 'zh-CN',
			sortable: true
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
</script>

</head>
<body>
	<div class="container">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px">
			<table id="worktasklist" data-unique-id="projectid"
				data-query-params="queryParams" data-url="./worktasks.web?atn=pages"
				data-side-pagination="server" data-filter-control="true"
				data-pagination="true" data-toggle="worktasks" data-height="714"
				data-page-list="[10, 20, 50, All]" data-page-size="10"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr>
						<th data-field="username" data-filter-control="input" data-filter-control-placeholder=""
							data-width="80">人员名称</th>
						<th data-field="roleid" data-formatter="roleFormat"
							data-filter-control="select" data-filter-data="var:roleTypes"
							data-width="80">角色名称</th>
						<th data-field="processid" data-filter-control="input"
							data-filter-control-placeholder="" data-width="60">项目编号</th>
						<th data-field="processname" data-filter-control="input"
							data-filter-control-placeholder=""
							data-width="200">项目名称</th>
						<th data-field="processtype" data-formatter="processTypesFormat"
							data-filter-control="select" data-width="120"
							data-filter-data="var:processTypes">项目类型</th>
						<th data-field="totaltask" data-sortable="true">任务总数</th>
						<th data-field="edittask" data-sortable="true">编辑中任务</th>
						<th data-field="qctask" data-sortable="true">质检中任务</th>
						<th data-field="checktask" data-sortable="true">校正中任务</th>
						<th data-field="completetask" data-sortable="true">已完成任务</th>
						<th data-field="time" data-filter-control="input"
							data-filter-control-placeholder="" data-width="100">更新时间</th>
					</tr>
				</thead>
			</table>
		</div>
		<div id="footdiv"></div>
	</div>
</body>
</html>
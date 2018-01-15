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

<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css"
	rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />
<link href="resources/bootstrap-table-1.11.1/bootstrap-table.min.css"
	rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/bootstrap-table.min.js"></script>
<script
	src="resources/bootstrap-table-1.11.1/extensions/filter-control/bootstrap-table-filter-control.min.js"></script>
<script
	src="resources/bootstrap-table-1.11.1/locale/bootstrap-table-zh-CN.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$.webeditor.getHead();
		//$.webeditor.getFoot();

		$('[data-toggle="processes"]').bootstrapTable({
			locale : 'zh-CN'
		});
	});
	var processStates = eval('(${processStates})');

	function statesFormat(value, row, index) {
		return processStates[row.state];
	}

	function progressFormat(value, row, index) {
		var html = new Array();
		html.push('<div class="progress progress-striped active" style="margin-bottom: 0;">');
		html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
						+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: 25%;">'
						+ ' <span class="glyphicon glyphicon-edit" style="margin:0 6px;"></span><span style="margin:0 6px;">100&#8453;</span><span class="glyphicon glyphicon-play" style="margin:0 6px;"></span>'
						+ ' </div>')
		html.push('<div class="progress-bar progress-bar-info" role="progressbar"'
						+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: 25%;">'
						+ ' <span class="glyphicon glyphicon-edit" style="margin:0 6px;"></span><span style="margin:0 6px;">100&#8453;</span><span class="glyphicon glyphicon-play" style="margin:0 6px;"></span>'
						+ ' </div>')
		html.push('<div class="progress-bar" role="progressbar"'
						+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: 25%;">'
						+ ' <span class="glyphicon glyphicon-edit" style="margin:0 6px;"></span><span style="margin:0 6px;">100&#8453;</span><span class="glyphicon glyphicon-play" style="margin:0 6px;"></span>'
						+ ' </div>')
		html.push('<div class="progress-bar progress-bar-success" role="progressbar"'
						+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: 25%;">'
						+ ' <span class="glyphicon glyphicon-edit" style="margin:0 6px;"></span><span style="margin:0 6px;">100&#8453;</span><span class="glyphicon glyphicon-play" style="margin:0 6px;"></span>'
						+ ' </div>')
		html.push('</div>');

		return html.join('');
	}

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
			<table id="processeslist" data-unique-id="id"
				data-query-params="queryParams"
				data-url="./processesmanage.web?atn=pages"
				data-side-pagination="server" data-filter-control="true"
				data-pagination="true" data-toggle="processes" data-height="714"
				data-page-list="[3, 6, 9, All]" data-page-size="3"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr>
						<th data-field="id" data-filter-control="input"
							data-filter-control-placeholder="" data-width="60">流程编号</th>
						<th data-field="name" data-filter-control="input"
							data-filter-control-placeholder="" data-width="120">流程名称</th>
						<th data-field="userid" data-filter-control="input"
							data-filter-control-placeholder="" data-width="120">创建者</th>
						<th data-field="state" data-formatter="statesFormat"
							data-filter-control="select" data-width="100"
							data-filter-data="var:processStates">流程状态</th>
						<th data-field="progress" data-formatter="progressFormat"
							data-width="600">流程进度</th>
						<th data-field="createtime" data-filter-control="input"
							data-filter-control-placeholder="" data-width="240">创建时间</th>
					</tr>
				</thead>
			</table>
		</div>
		<div id="footdiv"></div>
	</div>
</body>
</html>
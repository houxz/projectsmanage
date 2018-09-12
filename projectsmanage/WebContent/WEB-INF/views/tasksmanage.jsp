<%@page import="com.emg.projectsmanage.common.ProcessType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<meta name="robots" content="all">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>任务列表</title>

<link href="resources/jquery-ui-1.12.1.custom/jquery-ui.min.css"
	rel="stylesheet">
<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css"
	rel="stylesheet" />
<link href="resources/bootstrap-table-1.11.1/bootstrap-table.min.css"
	rel="stylesheet">
<link href="resources/css/css.css" rel="stylesheet" />

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/js/common.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/bootstrap-table.min.js"></script>
<script
	src="resources/bootstrap-table-1.11.1/extensions/filter-control/bootstrap-table-filter-control.min.js"></script>
<script
	src="resources/bootstrap-table-1.11.1/locale/bootstrap-table-zh-CN.js"></script>

<script type="text/javascript">
	
	var processTypes = eval('(${processTypes})');
	var editers = eval('(${editers})');
	var checkers = eval('(${checkers})');
	
	var stateDeses = {"未制作":"未制作", "编辑中":"编辑中", "校正错误修改中":"校正错误修改中", "质检中":"质检中", "质检完成":"质检完成", "校正中":"校正中", "完成":"完成", "预发布完成":"预发布完成", "悬挂点创建中":"悬挂点创建中"};
	
	var editersSelected = {};
	if (editers.length > 0) {
		for ( var w in editers) {
			editersSelected[editers[w].id] = editers[w].realname;
		}
	}
	var checkersSelected = {};
	if (checkers.length > 0) {
		for ( var w in checkers) {
			checkersSelected[checkers[w].id] = checkers[w].realname;
		}
	}

	function editersFormat(value, row, index) {
		var editer = Number(row.editid) - 500000;
		return editersSelected[editer];
	}
	function checkersFormat(value, row, index) {
		var checker = Number(row.checkid) - 600000;
		return checkersSelected[checker];
	}

	function processTypeFormat(value, row, index) {
		return processTypes[row.processtype];
	}
	
	function opttimeFormat(value, row, index) {
		var html = new Array();
		html.push(value);
		return html.join("");
	}

	$(document).ready(function() {
		$.webeditor.getHead();

		$('[data-toggle="qctasks"]').bootstrapTable({
			locale : 'zh-CN'
		});

	});

	function queryParams(params) {
		return params;
	}
</script>
</head>
<body>
	<div class="container">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px;">
			<table id="qctaskslist" data-unique-id="id"
				data-query-params="queryParams" data-content-type="application/x-www-form-urlencoded;charset=UTF-8"
				data-url="./tasksmanage.web?atn=pages"
				data-side-pagination="server"
				data-filter-control="true" data-pagination="true"
				data-toggle="qctasks" data-height="714"
				data-page-list="[10, 20, 50, 100]" data-page-size="10"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr>
						<th data-field="processid" data-width="70"
							data-filter-control="input" data-filter-control-placeholder="">
							项目编号</th>
						
						<th data-field="processname"
							data-filter-control="input" data-filter-control-placeholder="">
							项目名称</th>
						
						<c:choose>
							<c:when test="${not empty param.process}">
								<th data-field="processtype" data-width="140" data-formatter="processTypeFormat" 
									data-filter-control="select" data-filter-data="var:processTypes" data-filter-default-value="${param.process}">
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;项目类型&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
							</c:when>
							<c:otherwise>
								<th data-field="processtype" data-width="140" data-formatter="processTypeFormat" 
									data-filter-control="select" data-filter-data="var:processTypes" data-filter-default-value="<%= ProcessType.ATTACH.getValue() %>">
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;项目类型&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
							</c:otherwise>
						</c:choose>
						
						<th data-field="id" data-width="70"
							data-filter-control="input" data-filter-control-placeholder="">
							任务编号</th>

						<th data-field="name"
							data-filter-control="input" data-filter-control-placeholder="">
							任务名称</th>
						
						<th data-field="statedes"
							data-filter-control="select" data-filter-data="var:stateDeses">
							任务状态</th>
						
						<th data-field="editid" data-width="105" data-formatter="editersFormat"
							data-filter-control="select" data-filter-data="var:editersSelected" >
							编辑人</th>
						
						<th data-field="checkid" data-width="105" data-formatter="checkersFormat"
							data-filter-control="select" data-filter-data="var:checkersSelected">
							校正人</th>
						
						<th data-field="opttime" data-width="100">
							更新时间</th>
						
					</tr>
				</thead>
			</table>
		</div>
	</div>
</body>
</html>
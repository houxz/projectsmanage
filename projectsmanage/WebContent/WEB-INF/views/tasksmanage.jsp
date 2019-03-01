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
	var taskTypes = eval('(${taskTypes})');
	var priorityLevels = eval('(${priorityLevels})');
	
	var stateDeses = {"未制作":"未制作", "制作中":"制作中", "制作完成":"制作完成", "校正错误修改中":"校正错误修改中", "待质检":"待质检","异常":"异常","质检中":"质检中", "质检完成":"质检完成", "未校正":"未校正", "校正中":"校正中", "校正完成":"校正完成", "完成":"完成", "预发布完成":"预发布完成", "悬挂点创建中":"悬挂点创建中"};
	
	function changePriority(processtype, taskid) {
		var priority = $("#priority_" + taskid).val();
		$.webeditor.showMsgBox("info", "保存中...");
		jQuery.post("./tasksmanage.web", {
			"atn" : "changePriority",
			"processType" : processtype,
			"taskid" : taskid,
			"priority" : priority
		}, function(json) {
			$.webeditor.showMsgBox("close");
			$('[data-toggle="qctasks"]').bootstrapTable('refresh');
			$.webeditor.showMsgLabel("success",'优先级修改成功');
		}, "json");
	}
	
	function processTypeFormat(value, row, index) {
		return processTypes[row.processtype];
	}
	function priFormat(value, row, index) {
		/* var html = [];
		html.push("<select name='priority_" + row.id + "' id='priority_"
				+ row.id + "' onchange='changePriority(" + row.processtype + "," + row.id
				+ ")'  class='form-control'>");
		for ( var priorityLevel in priorityLevels) {
			if (priorityLevel == value) {
				html.push("<option value='"+ value +"' selected='selected' >"
						+ priorityLevels[value] + "</option>");
			} else {
				html.push("<option value='"+ priorityLevel +"'>"
						+ priorityLevels[priorityLevel] + "</option>");
			}
		}
		html.push("</select>");
		return html.join(""); */
		return priorityLevels[row.priority];
	}
	function taskTypeFormat(value, row, index) {
		return taskTypes[row.tasktype];
	}
	
	function opttimeFormat(value, row, index) {
		var html = new Array();
		html.push(value);
		return html.join("");
	}

	$(document).ready(function() {
		$.webeditor.getHead();

		$.webeditor.showMsgBox("info", "数据加载中，请稍候...");
		$('[data-toggle="qctasks"]').bootstrapTable({
			locale : 'zh-CN',
			onSearch : function (text) {
				$.webeditor.showMsgBox("info", "数据加载中，请稍候...");
			},
			onPageChange :function (number, size) {
				$.webeditor.showMsgBox("info", "数据加载中，请稍候...");
			},
			onLoadSuccess : function (data) {
				$.webeditor.showMsgBox("close");
			},
			onLoadError : function (status) {
				$.webeditor.showMsgBox("close");
			}
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
						
						<th data-field="processname" data-width="120"
							data-filter-control="input" data-filter-control-placeholder="">
							项目名称</th>
						
						<c:choose>
							<c:when test="${not empty param.process}">
								<th data-field="processtype" data-width="100" data-formatter="processTypeFormat" 
									data-filter-control="select" data-filter-data="var:processTypes" data-filter-default-value="${param.process}">
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;项目类型&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
							</c:when>
							<c:otherwise>
								<th data-field="processtype" data-width="100" data-formatter="processTypeFormat" 
									data-filter-control="select" data-filter-data="var:processTypes" data-filter-default-value="<%= ProcessType.GEN.getValue() %>">
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;项目类型&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
							</c:otherwise>
						</c:choose>
						
						<th data-field="id" data-width="70"
							data-filter-control="input" data-filter-control-placeholder="">
							任务编号</th>

						<th data-field="name"
							data-filter-control="input" data-filter-control-placeholder="">
							任务名称</th>
						<th data-field="tasktype" data-formatter="taskTypeFormat" 
							data-filter-control="select" data-filter-data="var:taskTypes">
							任务类型</th>
						<th data-field="priority" data-formatter="priFormat"
							data-filter-control="select" data-width="95"
							data-filter-data="var:priorityLevels">优先级</th>
							
						<th data-field="batchid" data-width="100"
							data-filter-control="input"  data-filter-control-placeholder="">
							批次号</th>
							
						<!-- <th data-field="state">state</th>
						<th data-field="process">process</th> -->
						
						<th data-field="statedes"
							data-filter-control="select" data-filter-data="var:stateDeses">
							任务状态</th>
						
						<th data-field="editname" data-width="70"
							data-filter-control="input" data-filter-control-placeholder="">
							编辑人</th>
						
						<th data-field="checkname" data-width="70"
							data-filter-control="input" data-filter-control-placeholder="">
							校正人</th>
							
						<th data-field="fielddatarest">
							剩余<br>资料数</th>
							
						<th data-field="errorrest">
							剩余<br>错误数</th>
						
						<th data-field="fielddatatotal">
							总数<br>资料数</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
</body>
</html>
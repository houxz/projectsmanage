<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<title>产能统计</title>
<meta charset="UTF-8" />
<meta name="robots" content="none">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

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
<script src="resources/js/project/giveworker.js"></script>
<script src="resources/js/project/attributemanage.js"></script>
<script src="resources/js/project/priorityadjust.js"></script>
<script src="resources/js/bootstrapDialog.js"></script>

<script type="text/javascript">
	var roleTypes = {5:"制作",6:"校正"};
	var poiTaskTypes = eval('(${poiTaskTypes})');
	var isWorkTimes = eval('(${isWorkTimes})');
	
	$(document).ready(function() {
		$.webeditor.getHead();
		$('[data-toggle="capacity"]').bootstrapTable({
			locale : 'zh-CN'
		});
	});
	
	function isWorkTimesFormat(value, row, index) {
		return isWorkTimes[row.iswork];
	}
	
	function roleTypesFormat(value, row, index) {
		return roleTypes[row.roleid];
	}
	
	function poiTaskTypesFormat(value, row, index) {
		return poiTaskTypes[row.tasktype];
	}
	
	function errorCountFormat(value, row, index) {
		var html = new Array();
		if (row.errorcount && row.errorcount > 0 && (row.tasktype == 15106 || row.tasktype == 15108 || row.tasktype == 15119)) {
			html.push('<a href="#" onclick="getDetails(' + row.tasktype + ','
					+ row.projectid + ','
					+ row.userid + ','
					+ row.roleid + ',\''
					+ row.time + '\','
					+ row.iswork + ');">' + row.errorcount + '</a>');
		} else {
			html.push(row.errorcount);
		}
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
	
	function getDetails(tasktype, projectid, userid, roleid, time, iswork) {
		$("#dlgDetails").bootstrapDialog({
			filterControl: false,
			queryParams : function(params) {
				params["tasktype"] = tasktype;
				params["projectid"] = projectid;
				params["userid"] = userid;
				params["roleid"] = roleid;
				params["time"] = time.trim();
				params["iswork"] = iswork;
				return params;
			},
			onLoadSuccess : function(data) {
				$(this.self).bootstrapTable("load", data.rows);
			}
		}, {
			width : document.documentElement.clientWidth * 0.6,
			title : "错误详情",
			buttons : [
				{
					text : "关闭",
					class : "btn btn-default",
					click : function() {
						$(this).dialog("close");
					}
				}
			]
		});
	}
</script>

</head>
<body>
	<div class="container">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px">
			<table id="capacitylist" data-unique-id="id"
				data-query-params="queryParams" data-url="./capacity.web?atn=pages"
				data-side-pagination="server" data-filter-control="true"
				data-pagination="true" data-toggle="capacity" data-height="714"
				data-page-list="[10, 30, 50, 100, all]" data-page-size="10"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr>
						<th data-field="id" data-width="60"
							data-filter-control="input" data-filter-control-placeholder="">编号</th>
							
						<th data-field="tasktype" data-width="160" data-formatter="poiTaskTypesFormat"
							data-filter-control="select" data-filter-data="var:poiTaskTypes">任务类型</th>
							
						<th data-field="processname" data-width="160"
							data-filter-control="input" data-filter-control-placeholder="">项目名称</th>
							
						<th data-field="username" data-width="80"
							data-filter-control="input" data-filter-control-placeholder="">人员</th>
							
						<th data-field="roleid" data-width="80" data-formatter="roleTypesFormat"
							data-filter-control="select" data-filter-data="var:roleTypes">角色</th>
							
						<th data-field="time" data-width="100"
							data-filter-control="input" data-filter-control-placeholder="">统计日期</th>
							
						<th data-field="iswork" data-width="80" data-formatter="isWorkTimesFormat"
							data-filter-control="select" data-filter-data="var:isWorkTimes">工作时间</th>
							
						<th data-field="taskcount" data-sortable="true">&nbsp;&nbsp;制作&nbsp;&nbsp;<br>任务</th>
						
						<th data-field="fielddatacount" data-sortable="true">&nbsp;&nbsp;修改&nbsp;&nbsp;<br>资料</th>
						
						<th data-field="createpoi" data-sortable="true">&nbsp;&nbsp;新增&nbsp;&nbsp;<br>POI</th>
						
						<th data-field="modifypoi" data-sortable="true">&nbsp;&nbsp;修改&nbsp;&nbsp;<br>POI</th>
						
						<th data-field="deletepoi" data-sortable="true">新增删除<br>POI</th>
						
						<th data-field="existdeletepoi" data-sortable="true">存量删除<br>POI</th>
						
						<!-- <th data-field="confirmpoi" data-sortable="true">&nbsp;&nbsp;确认&nbsp;&nbsp;<br>POI</th> -->
						
						<th data-field="errorcount" data-sortable="true" data-formatter="errorCountFormat">&nbsp;&nbsp;修改&nbsp;&nbsp;<br>错误</th>
						
						<th data-field="visualerrorcount" data-sortable="true">&nbsp;&nbsp;目视&nbsp;&nbsp;<br>错误</th>
						
					</tr>
				</thead>
			</table>
		</div>
	</div>
	<div id="dlgDetails" style="display: none;">
		<table id="detailslist" class="table table-condensed"
			data-url="./capacity.web?atn=getdetails"
			data-toggle="details"
			data-height="560">
			<thead>
				<tr>
					<th data-field="id" data-width="60" data-align='center'>编号</th>
							
					<th data-field="tasktype" data-width="160" data-formatter="poiTaskTypesFormat">任务类型</th>
							
					<th data-field="processname">项目名称</th>
							
					<th data-field="username" data-width="80" data-align='center'>人员</th>
							
					<th data-field="roleid" data-width="80" data-align='center' data-formatter="roleTypesFormat">角色</th>
						
					<th data-field="errortype" data-width="80">错误类型</th>
							
					<th data-field="time" data-width="100" data-align='center'>统计日期</th>
							
					<th data-field="iswork" data-width="80" data-align='center' data-formatter="isWorkTimesFormat">工作时间</th>
							
					<th data-field="errorcount" data-width="120" data-align='center'
						 data-sortable="true">修改错误数</th>
				</tr>
			</thead>
		</table>
	</div>
</body>
</html>
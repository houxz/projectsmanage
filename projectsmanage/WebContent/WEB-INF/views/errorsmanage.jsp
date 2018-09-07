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
<title>错误导出</title>

<link href="resources/jquery-flexselect-0.9.0/flexselect.css"
	rel="stylesheet">
<link href="resources/jquery-ui-1.12.1.custom/jquery-ui.min.css"
	rel="stylesheet">
<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css"
	rel="stylesheet" />
<link href="resources/bootstrap-table-1.11.1/bootstrap-table.min.css"
	rel="stylesheet">
<link href="resources/css/css.css" rel="stylesheet" />
<link href="resources/jquery-flexselect-0.9.0/flexselect.css"
	rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/js/common.js"></script>
<script src="resources/js/consMap.js"></script>
<script src="resources/js/consSet.js"></script>
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
<script src="resources/jquery-flexselect-0.9.0/jquery.flexselect.js"></script>
<script src="resources/jquery-flexselect-0.9.0/liquidmetal.js"></script>
<script src="resources/jquery-flexselect-0.9.0/jquery.flexselect.js"></script>
<script src="resources/jquery-flexselect-0.9.0/liquidmetal.js"
	type="text/javascript"></script>
	
<script type="text/javascript">
	$(document).ready(function() {
		$.webeditor.getHead();

		$("#batchid").flexselect();
		$("#errorset").flexselect();
		
		$('[data-toggle="errors"]').bootstrapTable({
			locale : 'zh-CN'
		});
	});
	
	function queryParams(params) {
		params["batchid"] = $("#batchid").val();
		params["errorsetid"] = $("#errorset").val();
		params["taskdb"] = $("#taskdb").val();
		params["errordb"] = $("#errordb").val();
		return params;
	}
	
	function errorremarkFormat(value, row, index) {
		var html = new Array();
		html.push("<pre style='word-wrap: break-word; white-space: pre-wrap; white-space: -moz-pre-wrap' >");
		html.push(value);
		html.push("</pre>");
		return html.join("");
	}
	
	function getErrors() {
		$('[data-toggle="errors"]').bootstrapTable("refresh");
	}
	
	function getErrorSets (taskdb) {
		$("#errorset").empty();
		$("#batchid").empty();
		jQuery.post("./errorsmanage.web", {
			"atn" : "geterrorsets",
			"taskdb" : taskdb
		}, function(json) {
			if (json.ret && json.ret > 0) {
				var errorSets = json.errorsets;
				if(errorSets && errorSets.length > 0) {
					$.each(errorSets, function(i, errorSet){
						$("#errorset").append('<option value="' + errorSet.id + '">' + errorSet.name + '</option>');
					});
					$("#errorset").flexselect();
					$("#errorset").val(null);
				}
				var batchids = json.batchids;
				if(batchids && batchids.length > 0) {
					$.each(batchids, function(i, batchid){
						$("#batchid").append('<option value="' + batchid + '">' + batchid + '</option>');
					});
					$("#batchid").flexselect();
					$("#batchid").val(null);
				}
			} else {
				$.webeditor.showMsgLabel("alert", "获取ErrorSets失败");
			}
		}, "json");
	}
	
	function exportErrors() {
		$.webeditor.showMsgBox("", "导入中...");
		var batchid = $("#batchid").val();
		var errorsetid = $("#errorset").val();
		var taskdb = $("#taskdb").val();
		var errordb = $("#errordb").val();
		var error2db = $("#error2db").val();
		
		jQuery.post("./errorsmanage.web", {
			"atn" : "exporterrors",
			"batchid" : batchid,
			"errorsetid" : errorsetid,
			"taskdb" : taskdb,
			"errordb" : errordb,
			"error2db" : error2db
		}, function(json) {
			if (json.ret && json.ret > 0) {
				$.webeditor.showConfirmBox("success", "导入成功", function(){
					$("#comm_msgbox").remove();
				});
			} else {
				$.webeditor.showConfirmBox("alert", "导入失败", function(){
					$("#comm_msgbox").remove();
				});
			}
		}, "json");
	}
	
</script>
</head>
<body>
	<div class="container" style="max-width: 90%;">
		<div id="headdiv"></div>
		<div style="margin: 20px auto;">
			<div class="input-group" style="width: 100%; margin: auto;">
				<span class="input-group-addon">质检任务库:</span>
				<select name="taskdb" class="form-control" id="taskdb" onchange="getErrorSets(this.options[this.options.selectedIndex].value);">
					<c:forEach items="${taskdbs }" var="dbmodel">
						<option value="${dbmodel['id'] }">${dbmodel['dbname']}<c:if test="${not empty dbmodel['dbschema']}">.${dbmodel['dbschema']}</c:if>(${dbmodel['ip']}:${dbmodel['port']})</option>
					</c:forEach>
				</select>
				<span class="input-group-addon">质检错误库:</span>
				<select name="errordb" class="form-control" id="errordb" onchange="getErrorSets(this.options[this.options.selectedIndex].value);">
					<c:forEach items="${errordbs }" var="dbmodel">
						<option value="${dbmodel['id'] }">${dbmodel['dbname']}<c:if test="${not empty dbmodel['dbschema']}">.${dbmodel['dbschema']}</c:if>(${dbmodel['ip']}:${dbmodel['port']})</option>
					</c:forEach>
				</select>
				<span class="input-group-addon">导入错误库:</span>
				<select name="error2db" class="form-control" id="error2db" onchange="getErrorSets(this.options[this.options.selectedIndex].value);">
					<c:forEach items="${error2dbs }" var="dbmodel">
						<option value="${dbmodel['id'] }">${dbmodel['dbname']}<c:if test="${not empty dbmodel['dbschema']}">.${dbmodel['dbschema']}</c:if>(${dbmodel['ip']}:${dbmodel['port']})</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div style="margin: 20px auto;">
			<div class="input-group" style="width: 100%; margin: auto;">
				<span class="input-group-addon">批次:</span>
				<select name="batchid" class="form-control" id="batchid">
					<option value="-1"></option>
					<c:forEach var="batchid" items="${batchids }">
						<option value="${batchid }">${batchid }</option>
					</c:forEach>
				</select> 
				<span class="input-group-addon">错误筛选集合:</span> 
				<select name="errorset" class="form-control" id="errorset">
					<option value="-1"></option>
					<c:forEach var="errorset" items="${errorSets }">
						<option value="${errorset['id'] }">${errorset['name'] }</option>
					</c:forEach>
				</select>
				<span class="input-group-btn" style="width: 40%; margin: auto;">
					<button class="btn btn-default" type="button" onclick="getErrors();">查看筛选结果</button>
					<button class="btn btn-default" type="button" onclick="exportErrors();">导入错误</button>
				</span>
			</div>
		</div>
		<div class="row">
			<table id="errorslist" data-unique-id="id"
				data-query-params="queryParams"
				data-url="./errorsmanage.web?atn=pages" data-side-pagination="server"
				data-filter-control="true" data-pagination="true"
				data-toggle="errors" data-height="714"
				data-page-list="[5, 10, 20, All]" data-page-size="5"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr>
						<th data-field="id" data-filter-control="input"
							data-filter-control-placeholder="" data-width="80">编号</th>
						<th data-field="taskid">taskid</th>
						<th data-field="blockid">blockid</th>
						<th data-field="featureid">featureid</th>
						<th data-field="layerid">layerid</th>
						<th data-field="editver">editver</th>
						<th data-field="fieldName">field_name</th>
						<th data-field="editvalue">editvalue</th>
						<th data-field="checkvalue">checkvalue</th>
						<th data-field="qid">qid</th>
						<th data-field="errortype">errortype</th>
						<th data-field="level">level</th>
						<th data-field="modifystate">modifystate</th>
						<th data-field="errorstate">errorstate</th>
						<th data-field="errorremark" data-formatter="errorremarkFormat">errorremark</th>
						<th data-field="checkroleid">checkroleid</th>
						<th data-field="changeroleid">changeroleid</th>
						<th data-field="batchid">batchid</th>
						<th data-field="isrelated">isrelated</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
</body>
</html>
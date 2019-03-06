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
<title>错误导出任务</title>

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
<link href="resources/bootstrap-datetimepicker/css/bootstrap-datetimepicker.css" rel="stylesheet">

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
<script src="resources/js/bootstrapDialog.js"></script>
<script type="text/javascript" src="resources/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js" charset="UTF-8"></script>
<script type="text/javascript" src="resources/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
	
<script type="text/javascript">
	$(document).ready(function() {
		$.webeditor.getHead();
		
		$("#config_batchid").flexselect();
		$('#datetimepicker').datetimepicker({
			autoclose : true,
			language : 'zh-CN'
		});

		$('[data-toggle="errorsTask"]').bootstrapTable({
			locale : 'zh-CN',
			onLoadSuccess : function(data) {
				$("[data-toggle='tooltip']").tooltip();
			}
		});
	});
	
	var errorsetSysTypes = eval('(${errorsetSysTypes})');
	var errorsetTypes = eval('(${errorsetTypes})');
	var errorsetUnits = eval('(${errorsetUnits})');
	var jobStates = eval('(${jobStatus})');
	
	function response(json) {
		if (json && json.result > 0)
			return json;
		else {
			$.webeditor.showMsgLabel("alert",json.resultMsg);
			return json;
		}
	}
	
	function indexFormat(value, row, index) {
		return index;
	}
	function checkboxFormat(value, row, index) {
		var selections = $("#" + this.valueBand).val();
		var values = new Array();
		if (selections) {
			try{
				var map = JSON.parse(selections);
				if (map && typeof map == 'object') {
					for (var key in map){
						values.push(String(key));
					}
				} else if (map && typeof map == 'number') {
					values.push(String(map));
				}
			}catch(e){
				$.each(selections.split(","), function(index, domEle) {
					values[index] = String(domEle);
				});
			}
    	}
		var uniqueId = this.uniqueId ? this.uniqueId : "id";
		if (values.indexOf(String(row[uniqueId])) >= 0)
			return true;
		else
			return false;
	}
	function sysFormat(value, row, index) {
		return errorsetSysTypes[row.systype];
	}
	function typeFormat(value, row, index) {
		return errorsetTypes[row.type];
	}
	function unitFormat(value, row, index) {
		return errorsetUnits[row.unit];
	}
	function statesFormat(value, row, index) {
		return jobStates[row.state];
	}
	function processFormat(value, row, index) {
		var pro = row.maxerrorid ? ((parseFloat((row.curerrorid-row.minerrorid+1)*100/(row.maxerrorid-row.minerrorid+1)).toFixed(3) > 0.01) ? parseFloat((row.curerrorid-row.minerrorid+1)*100/(row.maxerrorid-row.minerrorid+1)).toFixed(3) : 0) : 0;
		var html = new Array();
		html.push('<div>');
		html.push('<div data-toggle="tooltip" data-html="true" data-placement="top" title="最大错误ID：' + row.maxerrorid + '<br>最小错误ID：' + row.minerrorid + '<br>当前错误ID：' + row.curerrorid + '" >');
		html.push('<div class="progress');
		if (row.state == 1)
			html.push(' progress-striped active');
		html.push('"style="margin-bottom: 3px;">');
		html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
						+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
						+ pro
						+ '%;background-color: LimeGreen;">'
						+ ' <span style="margin:0 6px;color: black;">'
						+ pro + '&#8453;</span>' + ' </div>');
		html.push('</div></div>');
		html.push('</div>');
		return html.join('');
	}
	function operationFormat(value, row, index) {
		var html = new Array();
		if (row.state == 1) {
			html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="pauseErrorTask(' + row.id + ');">暂停</div>');
		} else if (row.state == 2 || row.state == -1 || row.state == 3 || row.state == 4) {
			html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="resumeErrorTask(' + index + ');">继续</div>');
		}
		html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="deleteErrorTask(' + row.id + ');">删除</div>');
		return html.join('');
	}
	function qctaskFormat(value, row, index) {
		var html = new Array();
		html.push(row.remarkname + "<br>");
		html.push(row.qctaskdbname + "<br>");
		html.push(row.qctaskdbschema + "<br>");
		html.push(row.qctaskip + "<br>");
		html.push(row.qctaskport + "<br>");
		return html.join('');
	}
	function errorsrcFormat(value, row, index) {
		var html = new Array();
		html.push(row.remarkname + "<br>");
		html.push(row.errorsrcdbname + "<br>");
		html.push(row.errorsrcdbschema + "<br>");
		html.push(row.errorsrcip + "<br>");
		html.push(row.errorsrcport + "<br>");
		return html.join('');
	}
	function errortarFormat(value, row, index) {
		var html = new Array();
		html.push(row.remarkname + "<br>");
		html.push(row.errortardbname + "<br>");
		html.push(row.errortardbschema + "<br>");
		html.push(row.errortarip + "<br>");
		html.push(row.errortarport + "<br>");
		return html.join('');
	}
	
	function queryParams(params) {
		return params;
	}
	
	function pauseErrorTask(taskid) {
		$.webeditor.showMsgBox("info", "保存中...");
		jQuery.post("./errorstask.web", {
			"atn" : "pausetask",
			"taskid" : taskid
		}, function(json) {
			if (json.result && json.result > 0) {
				$('[data-toggle="errorsTask"]').bootstrapTable('refresh');
			} else {
				$.webeditor.showMsgLabel("alert", json.resultMsg);
			}
			$.webeditor.showMsgBox("close");
		}, "json");
	}
	
	function resumeErrorTask(index) {
		$.webeditor.showConfirmBox("alert","重新识别错误，并继续导出吗？", function(){
			$.webeditor.showMsgBox("info", "识别错误中...");
			
			var data = $('[data-toggle="errorsTask"]').bootstrapTable("getData")[index];
			
			jQuery.post("./errorstask.web", {
				"atn" : "resumetask",
				"taskid" : data.id,
				"qctask" : data.qctask,
				"errorsrc" : data.errorsrc,
				"dotasktime" : data.dotasktime,
				"batchid" : data.batchid,
				"errorsetid" : data.errorsetid
			}, function(json) {
				if (json.result && json.result > 0) {
					$('[data-toggle="errorsTask"]').bootstrapTable('refresh');
				} else {
					$.webeditor.showMsgLabel("alert", json.resultMsg);
				}
				$.webeditor.showMsgBox("close");
			}, "json");
		});
	}
	
	function deleteErrorTask(taskid) {
		$.webeditor.showConfirmBox("alert","确定要删除这个任务吗？", function(){
			$.webeditor.showMsgBox("info", "删除中...");
			jQuery.post("./errorstask.web", {
				"atn" : "deletetask",
				"taskid" : taskid
			}, function(json) {
				if (json.result && json.result > 0) {
					$('[data-toggle="errorsTask"]').bootstrapTable('refresh');
					$.webeditor.showMsgLabel("success",'任务删除成功');
				} else {
					$.webeditor.showMsgLabel("alert", json.resultMsg);
				}
				$.webeditor.showMsgBox("close");
			}, "json");
		});
	}
	
	function getBatches(taskdb) {
		$("#config_batchid").empty();
		$.webeditor.showMsgBox("info", "加载中...");
		jQuery.post("./errorstask.web", {
			"atn" : "getbatches",
			"taskdb" : taskdb
		}, function(json) {
			if (json.result && json.result > 0) {
				var batchids = json.rows;
				if(batchids && batchids.length > 0) {
					$.each(batchids, function(i, batchid){
						$("#config_batchid").append('<option value="' + batchid + '">' + batchid + '</option>');
					});
					$("#config_batchid").flexselect();
					$("#config_batchid").val(null);
				}
			} else {
				$.webeditor.showMsgLabel("alert", json.resultMsg);
			}
			$.webeditor.showMsgBox("close");
		}, "json");
	}
	
	function loadDefaultConfig() {
		$("#config_id").val("");
		$("#config_name").val("");
		$("#config_qctask").prop('selectedIndex', 0);
		$("#config_errorsrc").prop('selectedIndex', 0);
		$("#config_errortar").prop('selectedIndex', 0);
		$("#config_dotasktime").val("");
		$("#config_batchid + input").val("");
		$("#config_errorsetid").val("");
		$("#config_errorsetid span").text(0);
	}
	
	function showConfigDlg(index) {
		$("#configDlg").dialog({
			modal : true,
			width : 620,
			title : "任务配置",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
				
				if (index >= 0) {
					var data = $('[data-toggle="errorsTask"]').bootstrapTable("getData")[index];
					$("#config_id").val(data.id);
					$("#config_name").val(data.name );
					$("#config_qctask").val(data.qctask);
					$("#config_errorsrc").val(data.errorsrc);
					$("#config_errortar").val(data.errortar);
					$("#config_dotasktime").val(data.dotasktime);
					$("#config_batchid + input").val(data.batchid);
					$("#config_errorsetid").val(data.errorsetid);
					$("#config_errorsetid span").text(1);
					
					getBatches(data.qctask);
				} else {
					loadDefaultConfig();
				}
			},
			buttons : [
					{
						text : "提交",
						class : "btn btn-default",
						click : function() {
							var taskid = $("#config_id").val();
							var name = $("#config_name").val();
							var qctask = $("#config_qctask").val();
							var errorsrc = $("#config_errorsrc").val();
							var errortar = $("#config_errortar").val();
							var dotasktime = $("#config_dotasktime").val();
							var batchid = $("#config_batchid + input").val();
							var errorsetid = $("#config_errorsetid").val();

							if (!name || name.length <= 0) {
								$.webeditor.showMsgLabel("alert", "名称不能为空");
								return;
							}
							
							$.webeditor.showMsgBox("info", "保存中...");
							jQuery.post("./errorstask.web", {
								"atn" : "newtask",
								"taskid" : taskid,
								"name" : name,
								"qctask" : qctask,
								"errorsrc" : errorsrc,
								"errortar" : errortar,
								"dotasktime" : dotasktime,
								"batchid" : batchid,
								"errorsetid" : errorsetid
							},
							function(json) {
								if (json.result > 0) {
									$.webeditor.showMsgBox("close");
									$('[data-toggle="errorsTask"]').bootstrapTable('refresh');
									$.webeditor.showMsgLabel("success",'任务配置成功');
									$("#configDlg").dialog("close");
								} else {
									$.webeditor.showMsgBox("close");
									$.webeditor.showMsgLabel("alert",json.resultMsg);
								}
							}, "json");
						}
					}, {
						text : "关闭",
						class : "btn btn-default",
						click : function() {
							$(this).dialog("close");
						}
					} ]
		});
	}
	
	function showErrors(qctask, errorsrc, batchid, errorsetid) {
		$('[data-toggle="errors"]').bootstrapTable({
			locale : 'zh-CN',
			queryParams : function(params) {
				params["qctask"] = qctask;
				params["errorsrc"] = errorsrc;
				params["batchid"] = batchid;
				params["errorsetid"] = errorsetid;
				return params;
			}
		});
		
		$("#errorsDlg").dialog({
			modal : true,
			width : document.documentElement.clientWidth * 0.8,
			title : "错误详情",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			close : function (event, ui) {
				$('[data-toggle="errors"]').bootstrapTable("destroy");
			},
			buttons : [{
						text : "关闭",
						class : "btn btn-default",
						click : function() {
							$(this).dialog("close");
						}
					} ]
		});
	}
	
	function getErrors() {
		$('[data-toggle="errors"]').bootstrapTable({
			locale : 'zh-CN',
			queryParams : function(params) {
				var qctask = $("#config_qctask").val();
				var errorsrc = $("#config_errorsrc").val();
				var batchid = $("#config_batchid + input").val();
				var errorsetid = $("#config_errorsetid").val();
				
				params["qctask"] = qctask;
				params["errorsrc"] = errorsrc;
				params["batchid"] = batchid;
				params["errorsetid"] = errorsetid;
				return params;
			}
		});
		
		$("#errorsDlg").dialog({
			modal : true,
			width : document.documentElement.clientWidth * 0.8,
			title : "错误详情",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			close : function (event, ui) {
				$('[data-toggle="errors"]').bootstrapTable("destroy");
			},
			buttons : [{
						text : "关闭",
						class : "btn btn-default",
						click : function() {
							$(this).dialog("close");
						}
					} ]
		});
	}
	
	function getErrorSets() {
		$("#dlgErrorSets").bootstrapDialog({
			queryParams : function(params) {
				params["taskdb"] = $("#config_qctask").val();
				return params;
			}
		}, {
			width : 660,
			title : "选择错误筛选集合"
		});
	}
	
</script>
</head>
<body>
	<div class="container" style="max-width: 90%;">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px">
			<table id="errorsTaskList" data-unique-id="id" class="table table-condensed"
				data-query-params="queryParams"
				data-url="./errorstask.web?atn=pages" data-side-pagination="server"
				data-filter-control="true" data-pagination="true"
				data-toggle="errorsTask" data-height="714"
				data-page-list="[5, 10, 20, 50]" data-page-size="5"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr>
						<th data-field="id" data-filter-control="input"
							data-filter-control-placeholder="" data-width="50">编号
							<div class="btn btn-default btn-xs" title="新建任务"
								onclick="showConfigDlg(-1);">
								<span class="glyphicon glyphicon-plus"></span>
							</div>
						</th>
						
						<th data-field="name" data-width="80" data-filter-control="input"
							data-filter-control-placeholder="">任务名称</th>
						
						<th data-field="qctaskname" data-width="80" data-formatter="qctaskFormat">质检任务库</th>
						
						<th data-field="errorsrcname" data-width="80" data-formatter="errorsrcFormat">错误库</th>
						
						<th data-field="errortarname" data-width="80" data-formatter="errortarFormat">导入库</th>
						
						<th data-field="state" data-width="60" data-formatter="statesFormat"
							data-filter-control="select" data-filter-data="var:jobStates">状态</th>
						
						<th data-field="dotasktime" data-width="80" data-filter-control="input"
							data-filter-control-placeholder="">执行时间</th>
						
						<th data-field="batchid" data-width="80" data-filter-control="input"
							data-filter-control-placeholder="">批次</th>
						
						<th data-field="errorsetname" data-width="80">错误筛选集合</th>
						
						<!-- <th data-field="createtime" data-width="90">创建时间</th> -->
						
						<th data-formatter="processFormat" data-width="120">任务进度</th>
						
						<th data-formatter="operationFormat" data-width="70">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	<div id="configDlg" style="display: none;">
		<table class="table table-condensed" style="margin-bottom: 0;">
			<tbody>
				<tr>
					<td class="configKey">编号</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="config_id" disabled>
					</td>
				</tr>
				<tr>
					<td class="configKey">名称</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="config_name"
						placeholder="请输入新任务名称">
					</td>
				</tr>
				<tr>
					<td class="configKey">质检任务库</td>
					<td class="configValue">
						<select name="taskdb" class="form-control" id="config_qctask" onchange="getBatches(this.options[this.options.selectedIndex].value);">
							<c:forEach items="${taskdbs }" var="dbmodel">
								<option value="${dbmodel['id'] }">${dbmodel['remarkname']}_${dbmodel['dbname']}<c:if test="${not empty dbmodel['dbschema']}">.${dbmodel['dbschema']}</c:if>(${dbmodel['ip']}:${dbmodel['port']}_<c:if test="${dbmodel['online'] == 0}">线上</c:if><c:if test="${dbmodel['online'] == 1}">线下</c:if>)</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td class="configKey">错误库</td>
					<td class="configValue">
						<select name="errordb" class="form-control" id="config_errorsrc">
							<c:forEach items="${errordbs }" var="dbmodel">
								<option value="${dbmodel['id'] }">${dbmodel['remarkname']}_${dbmodel['dbname']}<c:if test="${not empty dbmodel['dbschema']}">.${dbmodel['dbschema']}</c:if>(${dbmodel['ip']}:${dbmodel['port']}_<c:if test="${dbmodel['online'] == 0}">线上</c:if><c:if test="${dbmodel['online'] == 1}">线下</c:if>)</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td class="configKey">导入库</td>
					<td class="configValue">
						<select name="error2db" class="form-control" id="config_errortar">
							<c:forEach items="${error2dbs }" var="dbmodel">
								<option value="${dbmodel['id'] }">${dbmodel['remarkname']}_${dbmodel['dbname']}<c:if test="${not empty dbmodel['dbschema']}">.${dbmodel['dbschema']}</c:if>(${dbmodel['ip']}:${dbmodel['port']}_<c:if test="${dbmodel['online'] == 0}">线上</c:if><c:if test="${dbmodel['online'] == 1}">线下</c:if><c:if test="${dbmodel['online'] == 2}">POI</c:if><c:if test="${dbmodel['online'] == 3}">YY</c:if><c:if test="${dbmodel['online'] == 4}">关系附属</c:if>)</option>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td class="configKey">执行时间</td>
					<td class="configValue">
						<div class="input-append date" id="datetimepicker" data-date-format="yyyy-mm-dd hh:ii">
						    <input size="16" type="text" class="form-control configValue" placeholder="请设定执行时间" id="config_dotasktime" readonly>
						    <span class="add-on"><i class="icon-th"></i></span>
						</div>
					</td>
				</tr>
				<tr>
					<td class="configKey">批次</td>
					<td class="configValue">
						<select name="batchid" class="form-control" id="config_batchid">
							<option value="-1"></option>
							<c:forEach var="batchid" items="${batchids }">
								<option value="${batchid }">${batchid }</option>
							</c:forEach>
						</select> 
					</td>
				</tr>
				<tr>
					<td class="configKey">错误筛选集合</td>
					<td class="configValue">
						<button type="button" class="btn btn-default"
							onclick="getErrorSets();">选择错误筛选集合</button>
						<p class="help-block" id="config_errorsetid">
							已选择<span>0</span>个错误筛选集合
						</p>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div id="errorsDlg" style="display: none;">
		<table id="errorslist" class="table-condensed"
			data-unique-id="id"  data-side-pagination="server"
			data-url="./errorstask.web?atn=geterrors"
			data-toggle="errors" data-pagination="true"
			data-height="580"
			data-page-list="[15, 30, 50, 100]" data-page-size="15"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="id">id</th>
					<th data-field="taskid">taskid</th>
					<th data-field="blockid">blockid</th>
					<th data-field="featureid">featureid</th>
					<th data-field="layerid">layerid</th>
					<th data-field="editver">editver</th>
					<th data-field="updatetime">updatetime</th>
					<th data-field="field_name">field_name</th>
					<th data-field="editvalue">editvalue</th>
					<th data-field="checkvalue">checkvalue</th>
					<th data-field="qid">qid</th>
					<th data-field="errortype">errortype</th>
					<th data-field="level">level</th>
					<th data-field="modifystate">modifystate</th>
					<th data-field="errorstate">errorstate</th>
					<th data-field="errorremark">errorremark</th>
					<th data-field="checkroleid">checkroleid</th>
					<th data-field="changeroleid">changeroleid</th>
					<th data-field="md5">md5</th>
					<th data-field="batchid">batchid</th>
					<th data-field="isrelated">isrelated</th>
					<th data-field="sbound">sbound</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="dlgErrorSets" style="display: none;">
		<table id="errorSetslist" class="table-condensed"
			data-unique-id="id"
			data-url="./errorstask.web?atn=geterrorsets"
			data-toggle="errorSets"
			data-height="580"
			data-value-band="config_errorsetid"
			data-response-handler="response">
			<thead>
				<tr>
					<th data-field="state" data-radio="true" data-value-band="config_errorsetid" data-formatter="checkboxFormat"></th>
					<th data-field="index" data-class="indexHidden" data-formatter="indexFormat"></th>
					
					<th data-field="id" data-filter-control-placeholder=""
						data-filter-control="input" data-width="50">编号</th>
						
					<th data-field="name" data-filter-control-placeholder=""
						data-filter-control="input">名称</th>
						
					<th data-field="type" data-formatter="typeFormat" data-width="60">类型</th>
					
					<th data-field="systype" data-formatter="sysFormat">操作系统</th>
					
					<th data-field="unit" data-formatter="unitFormat">质检单位</th>
				</tr>
			</thead>
		</table>
	</div>
</body>
</html>
<%@page import="com.emg.projectsmanage.common.ProcessType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<title>项目管理</title>
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
<link href="resources/css/css.css" rel="stylesheet" />
<link href="resources/bootstrap-table-1.11.1/bootstrap-table.min.css"
	rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/js/common.js"></script>
<script src="resources/js/consMap.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/bootstrap-table.min.js"></script>
<script src="resources/js/bootstrapDialog.js"></script>
<script
	src="resources/bootstrap-table-1.11.1/extensions/filter-control/bootstrap-table-filter-control.min.js"></script>
<script
	src="resources/bootstrap-table-1.11.1/locale/bootstrap-table-zh-CN.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$.webeditor.getHead();
		//$.webeditor.getFoot();
		
		$('[data-toggle="processes"]').bootstrapTable({
			locale : 'zh-CN',
			onLoadSuccess : function(data) {
				$("[data-toggle='tooltip']").tooltip();
			},
			onPostHeader : function() {
				if (!!window.EventSource) {
					if (autoRefreshProcess) {
						$("#imgRefresh").attr("src", "resources/images/refresh.gif");
						$("#imgRefresh").attr("title", "关闭自动刷新项目进度");
					} else {
						$("#imgRefresh").attr("src", "resources/images/stop.jpg");
						$("#imgRefresh").attr("title", "开启自动刷新项目进度");
					}
				} else {
					$("#imgRefresh").remove();
				}
			}
		});
		
	});
	
	var source = null;
	var matchByIDAndIndex = new Map();
	var autoRefreshProcess = false;
	
	var colors = [ "LimeGreen", "MediumSeaGreen", "MediumVioletRed", "Crimson", "Crimson" ];
	var processStates = eval('(${processStates})');
	var processTypes = eval('(${processTypes})');
	var itemAreaTypes = eval('(${itemAreaTypes})');
	var priorityLevels = eval('(${priorityLevels})');
	var itemsetEnables = eval('(${itemsetEnables})');
	var itemsetSysTypes = eval('(${itemsetSysTypes})');
	var itemsetTypes = eval('(${itemsetTypes})');
	var itemsetUnits = eval('(${itemsetUnits})');
	
	function indexFormat(value, row, index) {
		return index;
	}
	function statesFormat(value, row, index) {
		return processStates[row.state];
	}
	function processTypesFormat(value, row, index) {
		return processTypes[row.type];
	}
	function areaTypesFormat(value, row, index) {
		return itemAreaTypes[row.type];
	}

	function sysFormat(value, row, index) {
		return itemsetSysTypes[row.systype];
	}
	function itemsetTypesFormat(value, row, index) {
		return itemsetTypes[row.type];
	}
	function unitFormat(value, row, index) {
		return itemsetUnits[row.unit];
	}
	
	function progressFormat(value, row, index) {
		var values = value.split(',');
		var processType = row.type;
		
		var html = new Array();
		if(processType == 1) {
		
			html.push('<div>');
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="质检准备进度：' + parseFloat(values[0]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[0] > 0 && values[0] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[0]).toFixed(3) > 100 ? 100 : parseFloat(values[0]).toFixed(3))
							+ '%;background-color: '
							+ colors[0]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[0]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div>');
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="质检进度：'
							+ parseFloat(values[1]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[1] > 0 && values[1] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[1]).toFixed(3) > 100 ? 100 : parseFloat(values[1]).toFixed(3))
							+ '%;background-color: '
							+ colors[1]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[1]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div></div>');
	
			html.push('<div>');
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="bottom" title="改错准备进度：'
							+ parseFloat(values[2]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[2] > 0 && values[2] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[2]).toFixed(3) > 100 ? 100 : parseFloat(values[2]).toFixed(3))
							+ '%;background-color: '
							+ colors[2]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[2]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div>');
	
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="bottom" title="改错进度：' + parseFloat(values[3]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[3] > 0 && values[3] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[3]).toFixed(3) > 100 ? 100 : parseFloat(values[3]).toFixed(3))
							+ '%;background-color: '
							+ colors[3]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[3]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div></div>');
		} else if(processType == 2) {
			html.push('<div>');
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="编辑准备进度：' + parseFloat(values[0]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[0] > 0 && values[0] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[0]).toFixed(3) > 100 ? 100 : parseFloat(values[0]).toFixed(3))
							+ '%;background-color: '
							+ colors[0]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[0]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div>');
			
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="编辑进度：'
							+ parseFloat(values[1]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[1] > 0 && values[1] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[1]).toFixed(3) > 100 ? 100 : parseFloat(values[1]).toFixed(3))
							+ '%;background-color: '
							+ colors[1]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[1]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div></div>');
			
			html.push('<div>');
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="bottom" title="发布准备进度：'
							+ parseFloat(values[2]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[2] > 0 && values[2] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[2]).toFixed(3) > 100 ? 100 : parseFloat(values[2]).toFixed(3))
							+ '%;background-color: '
							+ colors[2]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[2]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div>');
		} else if(processType == 3) {
			html.push('<div>');
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="编辑准备进度：' + parseFloat(values[0]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[0] > 0 && values[0] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[0]).toFixed(3) > 100 ? 100 : parseFloat(values[0]).toFixed(3))
							+ '%;background-color: '
							+ colors[0]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[0]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div>');
			
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="编辑与校正：'
							+ parseFloat(values[1]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[1] > 0 && values[1] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[1]).toFixed(3) > 100 ? 100 : parseFloat(values[1]).toFixed(3))
							+ '%;background-color: '
							+ colors[1]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[1]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div></div>');
			
			html.push('<div>');
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="bottom" title="发布准备进度：'
							+ parseFloat(values[2]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[2] > 0 && values[2] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[2]).toFixed(3) > 100 ? 100 : parseFloat(values[2]).toFixed(3))
							+ '%;background-color: '
							+ colors[2]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[2]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div>');
		}else if(processType == 4) {
		
			html.push('<div>');
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="质检准备进度：' + parseFloat(values[0]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[0] > 0 && values[0] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[0]).toFixed(3) > 100 ? 100 : parseFloat(values[0]).toFixed(3))
							+ '%;background-color: '
							+ colors[0]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[0]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div>');
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="质检进度：'
							+ parseFloat(values[1]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[1] > 0 && values[1] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[1]).toFixed(3) > 100 ? 100 : parseFloat(values[1]).toFixed(3))
							+ '%;background-color: '
							+ colors[1]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[1]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div></div>');
		} else if(processType == 5) {
			html.push('<div>');
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="POI编辑准备进度：' + parseFloat(values[0]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[0] > 0 && values[0] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[0]).toFixed(3) > 100 ? 100 : parseFloat(values[0]).toFixed(3))
							+ '%;background-color: '
							+ colors[0]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[0]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div>');
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="POI编辑进度：'
							+ parseFloat(values[1]).toFixed(3) + '&#8453;">');
			html.push('<div class="progress');
			if (values[1] > 0 && values[1] < 100 && row.state == 1)
				html.push(' progress-striped active');
			html.push('"style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ (parseFloat(values[1]).toFixed(3) > 100 ? 100 : parseFloat(values[1]).toFixed(3))
							+ '%;background-color: '
							+ colors[1]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ parseFloat(values[1]).toFixed(3) + '&#8453;</span>' + ' </div>');
			html.push('</div></div></div>');
		}
		return html.join('');
	}

	function priFormat(value, row, index) {
		var html = [];
		html.push("<select name='priority_" + row.id + "' id='priority_"
				+ row.id + "' onchange='changePriority(" + row.id
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
		return html.join("");
	}
	function nameFormat(value, row, index) {
		var html = new Array();
		html.push("<div class='bootstrapColumn' >");
		html.push(value);
		html.push("</div>");
		return html.join("");
	}
	function layernameFormat(value, row, index) {
		var html = new Array();
		html.push("<pre class='bootstrapColumn' >");
		html.push(value);
		html.push("</pre>");
		return html.join("");
	}
	function referdataFormat(value, row, index) {
		var html = new Array();
		html.push("<pre class='bootstrapColumn' >");
		html.push(value);
		html.push("</pre>");
		return html.join("");
	}
	function descFormat(value, row, index) {
		var html = new Array();
		html.push("<div class='bootstrapColumn' >");
		html.push(value);
		html.push("</div>");
		return html.join("");
	}
	function samplingFormat(value, row, index) {
		var samplingValue = '';
		try{
			var str_values = $("#config_2_18").val();
			var map = JSON.parse(str_values);
			if (map && typeof map == 'object') {
				var id = row.id;
				if( id && map[id]) {
					samplingValue = map[id];
				}
			}
		}catch(e){
		}
		var html = new Array();
		html.push("<div class='input-group input-group-sm'>");
		html.push("<input type='text' class='form-control' id='sampling-" + row.id + "' maxlength='3' value='" + samplingValue + "'>");
		html.push("<span class='input-group-addon'>%</span>");
		html.push("</div>");
		return html.join("");
	}

	function changeState(state, processid) {
		$.webeditor.showMsgBox("info", "保存中...");
		jQuery.post("./processesmanage.web", {
			"atn" : "changeState",
			"processid" : processid,
			"state" : state
		}, function(json) {
			$.webeditor.showMsgBox("close");
			$('[data-toggle="processes"]').bootstrapTable('refresh');
			$.webeditor.showMsgLabel("success", "项目状态修改成功");
		}, "json");
	}

	function changePriority(processid) {
		var priority = $("#priority_" + processid).val();
		$.webeditor.showMsgBox("info", "保存中...");
		jQuery.post("./processesmanage.web", {
			"atn" : "changePriority",
			"processid" : processid,
			"priority" : priority
		}, function(json) {
			$.webeditor.showMsgBox("close");
			$('[data-toggle="processes"]').bootstrapTable('refresh');
			$.webeditor.showMsgLabel("success",'项目优先级修改成功，<div class="btn btn-default btn-xs" onclick="gotoPage(' + processid + ');">跳转</div>到所在页');
		}, "json");
	}

	function operationFormat(value, row, index) {
		var html = new Array();
		html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="getConfig('
						+ row.id
						+ ',\''
						+ row.name
						+ '\','
						+ row.priority
						+ ','
						+ row.type
						+ ','
						+ row.state
						+ ');">配置</div>');
		if (row.state == 0) {
			if (row.type == 5) {
				html.push('<div class="btn btn-default disabled" style="margin-bottom:3px;" >开始</div>');
			} else {
				html.push('<div class="btn btn-default" style="margin-bottom:3px;" onclick="changeState(1,' + row.id + ')" >开始</div>');
			}
		} else if (row.state == 1) {
			html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="changeState(2,' + row.id + ')">暂停</div>');
		} else if (row.state == 2) {
			html.push('<div class="btn btn-default" style="margin-bottom:3px;" onclick="changeState(1,' + row.id + ')" >开始</div>');
		}

		return html.join('');
	}

	function projcetsIDFormat(value, row, index) {
		var html = new Array();
		html.push('<input type="checkbox" value="' + row.id + '">' + row.id
				+ '');
		return html.join('');
	}

	function queryParams(params) {
		return params;
	}

	function loadDefaultConfig() {
		$("#config_processid").val("");
		$("#config_processname").val("");
		$("#config_processpriority").val(0);
		$("#config_processprotype").val(1);
		$("#config_processstatus").val(0);
		
		$("#config_1_5").prop('selectedIndex', 0);
		$("#config_1_6").val("");
		$("#config_1_6 span").text(0);
		$("#config_1_7").val("");
		$("#config_1_7 span").text(0);
		
		$("#config_2_18").val("");
		$("#config_2_18 span").text(0);
		$("#config_2_19").prop('selectedIndex', 0);
		$("#config_2_21").val("");
		$("#config_2_21 span").text(0);
		$("#config_2_22").prop('selectedIndex', 0);
		$("#config_2_23").prop('selectedIndex', 0);
		$("#config_2_25").val("");
		$("#config_2_25 span").text(0);
		
		processTypeChange(1);
	}

	function getConfig(processid, processname, priority, processtype, state) {
		loadDefaultConfig();
		processTypeChange(processtype);
		if (processid > 0) {
			if (state != 0) {
				disableByProcessType(processtype);
			}else {
				enableByProcessType();
			}
			jQuery.post("./processesmanage.web", {
				"atn" : "getconfigvalues",
				"processid" : processid
			}, function(json) {
				if (json.configValues && json.configValues.length > 0) {
					var configValues = json.configValues;
					for ( var index in configValues) {
						var obj = $("#" + "config_" + configValues[index].moduleid + "_" + configValues[index].configid);
						if (obj) {
							$(obj).val(configValues[index].value);
						}
					}
					
					var config_1_6 = $("#config_1_6").val();
					$("#config_1_6 span").text(config_1_6 ? config_1_6.split(",").length : 0);

					var config_1_7 = $("#config_1_7").val();
					$("#config_1_7 span").text(config_1_7 ? config_1_7.split(",").length : 0);

					var config_2_18 = $("#config_2_18").val();
					$("#config_2_18 span").text(config_2_18 ? config_2_18.split(",").length : 0);
					
					var config_2_21 = $("#config_2_21").val();
					$("#config_2_21 span").text(config_2_21 ? config_2_21.split(",").length : 0);
					
					var config_2_25 = $("#config_2_25").val();
					$("#config_2_25 span").text(config_2_25 ? config_2_25.split(",").length : 0);
				}
			}, "json");
		} else {
			enableByProcessType();
		}
		showConfigDlg(processid, processname, priority, processtype, state);
	}

	function showConfigDlg(processid, processname, priority, processtype, state) {
		$("#configDlg").dialog({
							modal : true,
							width : 520,
							title : "项目配置",
							open : function(event, ui) {
								$("#config_processid").val(processid);
								$("#config_processname").val(processname);
								$("#config_processstatus").val(state);
								
								$("#config_processpriority").val(priority);
								$("#config_processprotype").val(processtype);
								$(".ui-dialog-titlebar-close").hide();
							},
							buttons : [
									{
										text : "提交",
										class : "btn btn-default",
										click : function() {
											var processid = $("#config_processid").val();
											var newProcessName = $("#config_processname").val();
											var priority = $("#config_processpriority").val();
											var protype = $("#config_processprotype").val();
											var config_1_5 = $("#config_1_5").val();
											var config_1_6 = $("#config_1_6").val();
											var config_1_7 = $("#config_1_7").val();

											var config_2_18 = $("#config_2_18").val();
											var config_2_19 = $("#config_2_19").val();
											var config_2_21 = $("#config_2_21").val();
											var config_2_22 = $("#config_2_22").val();
											var config_2_23 = $("#config_2_23").val();
											var config_2_25 = $("#config_2_25").val();
											var config_2_26 = $("#config_2_26").val();

											if (!newProcessName || newProcessName.length <= 0) {
												$.webeditor.showMsgLabel("alert", "项目名不能为空");
												return;
											}
											
											switch(protype) {
											case 1:
											case "1":
											case 4:
											case "4":
												if(!config_1_6 || config_1_6.lenth <= 0) {
													$.webeditor.showMsgLabel("alert", "没有配置图层");
													return;
												}
												if(!config_1_7 || config_1_7.lenth <= 0) {
													$.webeditor.showMsgLabel("alert", "没有配置区域");
													return;
												}
												break;
											case 2:
											case "2":
											case 3:
											case "3":
												if(!config_1_7 || config_1_7.lenth <= 0) {
													$.webeditor.showMsgLabel("alert", "没有配置区域");
													return;
												}
												break;
											case 5:
											case "5":
												break;
											default:
												console.log("processTypeChange--错误的项目类型：" + selectValue);
												break;
											}
											
											if(config_2_19 == 1 && (!config_2_18 || config_2_18.length <= 0)) {
												$.webeditor.showMsgLabel("alert", "私有项目需要添加人员");
												return;
											}

											$.webeditor.showMsgBox("info", "保存中...");
											jQuery.post("./processesmanage.web", {
												"atn" : "newprocess",
												"processid" : processid,
												"newProcessName" : newProcessName,
												"priority" : priority,
												"type" : protype,
												"config_1_5" : config_1_5,
												"config_1_6" : config_1_6,
												"config_1_7" : config_1_7,
												"config_2_18" : config_2_18,
												"config_2_19" : config_2_19,
												"config_2_21" : config_2_21,
												"config_2_22" : config_2_22,
												"config_2_23" : config_2_23,
												"config_2_25" : config_2_25,
												"config_2_26" : config_2_26
											},
											function(json) {
												if (json.result > 0) {
													$.webeditor.showMsgBox("close");
													$('[data-toggle="itemAreas"]').bootstrapTable("destroy");
													$("#configDlg").dialog("close");
													$('[data-toggle="processes"]').bootstrapTable('refresh');
													$.webeditor.showMsgLabel("success",'项目配置成功，<div class="btn btn-default btn-xs" onclick="gotoPage(' + json.pid + ');">跳转</div>到所在页');
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
	
	function gotoPage(processID) {
		jQuery.post("./processesmanage.web", {
			"atn" : "getRNByProcessID",
			"processid" : processID
		}, function(json) {
			var pageSize = $(".page-size").text();
			if(pageSize == "All") return;
			var pageNum = Math.ceil(parseInt(json.ret)/parseInt(pageSize));
			$('[data-toggle="processes"]').bootstrapTable('selectPage', pageNum);
		}, "json");
		
	}
	
	function getItemAreas() {
		$("#itemAreasDlg").bootstrapDialog({
			valueBand: "config_1_7",
			queryParams : function(params) {
				if (params.filter != undefined) {
					var filterObj = eval('(' + params.filter + ')');
					if (filterObj.state != undefined) {
						filterObj["state"] = filterObj.state;
						delete filterObj.state;
						params.filter = JSON.stringify(filterObj);
					}
				}
				params["type"] = $("#config_1_5").val();
				params["processType"] = $("#config_processprotype").val();
				return params;
			}
		}, {
			width : 480,
			title : "质检区域配置"
		});
	}
	
	function getItemsets() {
		$("#itemsetsDlg").bootstrapDialog({
			valueBand: "config_1_6",
			queryParams : function(params) {
				if (params.filter != undefined) {
					var filterObj = eval('(' + params.filter + ')');
					if (filterObj.state != undefined) {
						filterObj["state"] = filterObj.state;
						delete filterObj.state;
						params.filter = JSON.stringify(filterObj);
					}
				}
				params["type"] = $("#config_1_5").val();
				params["processType"] = $("#config_processprotype").val();
				return params;
			}
		}, {
			width : document.documentElement.clientWidth * 0.6,
			title : "质检图层配置"
		});
	}
	
	function getWorkers() {
		$("#workersDlg").bootstrapDialog({
			valueBand: "config_2_18"
		}, {
			width : 480,
			title : "添加制作人员",
			buttons : [
				{
					text : "<",
					title : "上一条",
					class : "btn btn-default",
					click : function() {
						$(this).find("table").bootstrapTable("gotoLast");
					}
				},
				{
					text : ">",
					title : "下一条",
					class : "btn btn-default",
					click : function() {
						$(this).find("table").bootstrapTable("gotoNext");
					}
				},
				{
					text : "提交",
					class : "btn btn-default",
					click : function() {
						var bootstrapDialogIDSelected = $(this).find("table").bootstrapTable("getOptions").bootstrapDialogIDSelected;
						var length = bootstrapDialogIDSelected.length;
						if (length > 0) {
							var m = {};
							$.each(bootstrapDialogIDSelected, function(i, workerID){
								var sampling = $("#sampling-" + workerID).val();
								m[workerID] = sampling;
							});
							$("#config_2_18").val(JSON.stringify(m));
							$("#config_2_18 span").text(length);

							$(this).dialog("close");
						} else {
							$.webeditor.showMsgLabel("alert", "请选择人员");
						}
					}
				},
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
	
	function getCheckers() {
		$("#checkersDlg").bootstrapDialog({
			valueBand: "config_2_21"
		}, {
			width : 480,
			title : "添加校正人员"
		});
	}
	
	function getDataset() {
		$("#datasetsDlg").bootstrapDialog({
			valueBand: "config_2_25"
		}, {
			width : document.documentElement.clientWidth * 0.8,
			title : "绑定资料"
		});
	}

	function refreshProgress(obj) {
		if (!autoRefreshProcess) {
			autoRefreshProcess = !autoRefreshProcess;
			
			var data = $('[data-toggle="processes"]').bootstrapTable("getData");
			if (data && data.length > 0) {
				var ids = new Array();
				for ( var i = 0; i < data.length; i++) {
					matchByIDAndIndex.set(data[i].id, i);
					ids.push(data[i].id);
				}
				source = new EventSource('/projectsmanage/sse.web?action=refreshprogress&ids=' + ids.join(","));

				source.onmessage = function(e) {
					var progresses = JSON.parse(e.data);
					if(progresses instanceof Array){
						for(var index in progresses) {
							if(matchByIDAndIndex && matchByIDAndIndex.has(progresses[index].id)) {
								$('[data-toggle="processes"]').bootstrapTable('updateCell', {
									index: matchByIDAndIndex.get(progresses[index].id),
									field: "progress",
									value: progresses[index].progress
								});
							}
						}
					}
				};
			}
		} else {
			autoRefreshProcess = !autoRefreshProcess;
			$("#imgRefresh").attr("src", "resources/images/stop.jpg");
			$("#imgRefresh").attr("title", "开启自动刷新项目进度");
			
			if (source) {
				source.close();
				source = null;
			}
		}
	}
	
	function processTypeChange(selectValue) {
		switch(selectValue) {
		case 1:
		case "1":
			$("#config_1_5").parents("tr").show();
			$("#config_1_6").parents("tr").show();
			$("#config_1_7").parents("tr").show();
			$("#config_2_18").parents("tr").show();
			$("#config_2_19").parents("tr").show();
			$("#config_2_21").parents("tr").show();
			$("#config_2_22").parents("tr").hide();
			$("#config_2_23").parents("tr").hide();
			$("#config_2_25").parents("tr").hide();
			break;
		case 2:
		case "2":
			$("#config_1_5").parents("tr").hide();
			$("#config_1_6").parents("tr").hide();
			$("#config_1_7").parents("tr").show();
			$("#config_2_18").parents("tr").show();
			$("#config_2_19").parents("tr").show();
			$("#config_2_21").parents("tr").show();
			$("#config_2_22").parents("tr").hide();
			$("#config_2_23").parents("tr").hide();
			$("#config_2_25").parents("tr").hide();
			break;
		case 3:
		case "3":
			$("#config_1_5").parents("tr").hide();
			$("#config_1_6").parents("tr").hide();
			$("#config_1_7").parents("tr").show();
			$("#config_2_18").parents("tr").show();
			$("#config_2_19").parents("tr").show();
			$("#config_2_21").parents("tr").show();
			$("#config_2_22").parents("tr").hide();
			$("#config_2_23").parents("tr").hide();
			$("#config_2_25").parents("tr").hide();
			break;
		case 4:
		case "4":
			$("#config_1_5").parents("tr").show();
			$("#config_1_6").parents("tr").show();
			$("#config_1_7").parents("tr").show();
			$("#config_2_18").parents("tr").show();
			$("#config_2_19").parents("tr").show();
			$("#config_2_21").parents("tr").show();
			$("#config_2_22").parents("tr").hide();
			$("#config_2_23").parents("tr").hide();
			$("#config_2_25").parents("tr").hide();
			break;
		case 5:
		case "5":
			$("#config_1_5").parents("tr").hide();
			$("#config_1_6").parents("tr").hide();
			$("#config_1_7").parents("tr").hide();
			$("#config_2_18").parents("tr").show();
			$("#config_2_19").parents("tr").show();
			$("#config_2_21").parents("tr").show();
			$("#config_2_22").parents("tr").show();
			$("#config_2_23").parents("tr").show();
			$("#config_2_25").parents("tr").show();
			break;
		default:
			console.log("processTypeChange--错误的项目类型：" + selectValue);
			break;
		}
	}
	
	function enableByProcessType() {
		$("#config_processname").removeAttr("disabled");
		$("#config_processprotype").removeAttr("disabled");
		$("#config_processpriority").removeAttr("disabled");
		
		$("#config_1_5").removeAttr("disabled");
		$("#config_2_19").removeAttr("disabled");
	}
	
	function disableByProcessType(selectValue) {
		if(selectValue == 1 || selectValue == 2) {
			$("#config_processname").attr("disabled", true);
			$("#config_1_5").attr("disabled", true);
		} else {
			$("#config_processname").removeAttr("disabled");
			$("#config_1_5").removeAttr("disabled");
		}
		$("#config_processprotype").attr("disabled", true);
		$("#config_processpriority").removeAttr("disabled");
		$("#config_2_19").removeAttr("disabled");
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
				data-page-list="[5, 10, 20, All]" data-page-size="5"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr>
						<th data-field="id" data-filter-control="input"
							data-filter-control-placeholder="" data-width="120">项目编号
							<div class="btn btn-default btn-xs" title="新建项目"
								onclick="getConfig(0,'',0,1,0);">
								<span class="glyphicon glyphicon-plus"></span>
							</div>
						</th>
						<th data-field="name" data-filter-control="input"
							data-filter-control-placeholder="" data-width="160">项目名称</th>
						<th data-field="type" data-formatter="processTypesFormat"
							data-filter-control="select" data-width="140"
							data-filter-data="var:processTypes">项目类型</th>
						<th data-field="username" data-filter-control="input"
							data-filter-control-placeholder="" data-width="120">创建者</th>
						<th data-field="priority" data-formatter="priFormat"
							data-filter-control="select" data-width="120"
							data-filter-data="var:priorityLevels">优先级</th>
						<th data-field="state" data-formatter="statesFormat"
							data-filter-control="select" data-width="80"
							data-filter-data="var:processStates">项目状态</th>
						<th data-field="progress" data-formatter="progressFormat"
							data-width="500">项目进度
							<img id="imgRefresh" src="resources/images/stop.jpg" style="cursor: pointer;" title="开启自动刷新项目进度"  onclick="refreshProgress(this);">
						</th>
						<th data-formatter="operationFormat" data-width="70">操作</th>
					</tr>
				</thead>
			</table>
		</div>
		<div id="footdiv"></div>
	</div>
	<div id="configDlg" style="display: none;">
		<table class="table table-condensed" style="margin-bottom: 0;">
			<tbody>
				<tr>
					<td class="configKey">项目编号</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="config_processid" disabled>
					</td>
				</tr>
				<tr>
					<td class="configKey">项目名称</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="config_processname"
						placeholder="请输入新项目名">
						<input type="text"
						style="display:none" id="config_processstatus"
						></td>
				</tr>
				<tr>
					<td class="configKey">项目类型</td>
					<td class="configValue"><select
						class="form-control configValue" id="config_processprotype" onchange="processTypeChange(this.options[this.options.selectedIndex].value);">
							<c:set var="processTypes" value="<%= ProcessType.values() %>"/>
							<c:forEach items="${processTypes }" var="processType">
								<c:if test="${processType.getValue() > 0 }">
									<option value="${processType.getValue() }">${processType.getDes() }</option>
								</c:if>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">项目优先级</td>
					<td class="configValue"><select
						class="form-control configValue" id="config_processpriority">
							<option value="-2">极低</option>
							<option value="-1">低</option>
							<option value="0" selected="selected">一般</option>
							<option value="1">高</option>
							<option value="2">极高</option>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">质检集合</td>
					<td class="configValue"><select class="form-control"
						id="config_1_5">
							<option value="1" selected="selected">九宫格</option>
							<option value="2">全国</option>
							<option value="3">市</option>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">区域</td>
					<td class="configValue">
						<button type="button" class="btn btn-default"
							onclick="getItemAreas();">配置区域</button>
						<p class="help-block" id="config_1_7">
							已选择<span>0</span>个区域
						</p>
				</tr>
				<tr>
					<td class="configKey">图层</td>
					<td class="configValue">
						<button type="button" class="btn btn-default"
							onclick="getItemsets();">配置图层</button>
						<p class="help-block" id="config_1_6">
							已选择<span>0</span>个图层
						</p>
				</tr>
				<tr>
					<td class="configKey">人员</td>
					<td class="configValue">
						<button type="button" class="btn btn-default"
							onclick="getWorkers();">添加改错人员</button>
						<button type="button" class="btn btn-default"
							onclick="getCheckers();">添加校正人员</button>
						<p class="help-block" id="config_2_18">
							已添加<span>0</span>位改错人员
						</p>
						<p class="help-block" id="config_2_21">
							已添加<span>0</span>位校正人员
						</p>
				</tr>
				<tr>
					<td class="configKey">公有私有</td>
					<td class="configValue"><select class="form-control" id="config_2_19">
							<option value="0">公有</option>
							<option value="1">私有</option>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">创建任务方式</td>
					<td class="configValue"><select class="form-control" id="config_2_22">
							<option value="0">条</option>
							<option value="1">块</option>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">免校正</td>
					<td class="configValue"><select class="form-control" id="config_2_23">
							<option value="0">否</option>
							<option value="1">是</option>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">资料</td>
					<td class="configValue">
						<button type="button" class="btn btn-default"
							onclick="getDataset();">绑定资料</button>
						<p class="help-block" id="config_2_25">
							已绑定<span>0</span>份资料
						</p>
					</td>
				</tr>
				<tr>
					<td class="configKey">制作任务数</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="config_2_26" value="10">
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div id="workersDlg" style="display: none;">
		<table id="workerlist" class="table-condensed" data-unique-id="id"
			data-url="./processesmanage.web?atn=getworkers" data-cache="false"
			data-side-pagination="server" data-filter-control="true"
			data-click-to-select="true" data-single-select="false"
			data-select-item-name="checkboxName" data-pagination="false"
			data-toggle="workers" data-height="420"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true"></th>
					<th data-field="index" data-class="indexHidden" data-formatter="indexFormat"></th>
					<th data-field="id" data-filter-control="input"
						data-filter-control-placeholder="" data-width="80">编号</th>
					<th data-field="realname" data-filter-control="input"
						data-filter-control-placeholder="">人员姓名</th>
					<th data-field="sampling" data-width="100" data-formatter="samplingFormat">抽检比例</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="checkersDlg" style="display: none;">
		<table id="checkerlist" class="table-condensed" data-unique-id="id"
			data-url="./processesmanage.web?atn=getcheckers" data-cache="false"
			data-side-pagination="server" data-filter-control="true"
			data-click-to-select="true" data-single-select="false"
			data-select-item-name="checkboxName" data-pagination="false"
			data-toggle="checkers" data-height="420"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true"></th>
					<th data-field="index" data-class="indexHidden" data-formatter="indexFormat"></th>
					<th data-field="id" data-filter-control="input"
						data-filter-control-placeholder="" data-width="20">编号</th>
					<th data-field="realname" data-filter-control="input"
						data-filter-control-placeholder="" data-width="120">人员姓名</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="itemAreasDlg" style="display: none;">
		<table id="itemAreaslist" class="table-condensed" data-unique-id="id"
			data-url="./processesmanage.web?atn=getitemareas" data-cache="false"
			data-side-pagination="server" data-filter-control="true"
			data-click-to-select="true" data-single-select="false"
			data-select-item-name="checkboxName" data-pagination="false"
			data-toggle="itemAreas" data-height="420"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true"></th>
					<th data-field="index" data-class="indexHidden" data-formatter="indexFormat"></th>
					<th data-field="id" data-filter-control="input"
						data-filter-control-placeholder="" data-width="20">区域编号</th>
					<th data-field="areatype" data-filter-control="select"
						data-formatter="areaTypesFormat"
						data-filter-data="var:itemAreaTypes"
						data-filter-control-placeholder="" data-width="20">区域级别</th>
					<th data-field="province" data-filter-control="input"
						data-filter-control-placeholder="" data-width="140">省</th>
					<th data-field="city" data-filter-control="input"
						data-filter-control-placeholder="">市</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="itemsetsDlg" style="display: none;">
		<table id="itemsetslist" class="table-condensed" data-unique-id="id"
			data-url="./processesmanage.web?atn=getitemsets" data-cache="false"
			data-side-pagination="server" data-filter-control="true"
			data-click-to-select="true" data-single-select="false"
			data-select-item-name="checkbox" data-pagination="false"
			data-toggle="itemsets" data-height="520"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true"></th>
					<th data-field="index" data-class="indexHidden"
						data-formatter="indexFormat"></th>
					<th data-field="id" data-filter-control="input"
						data-filter-control-placeholder="" data-width="40">编号</th>
					<th data-field="name" data-filter-control="input" data-width="100"
						data-formatter="nameFormat" data-filter-control-placeholder="">图层集合名称</th>
					<th data-field="layername" data-filter-control="input"
						data-width="160" data-formatter="layernameFormat"
						data-filter-control-placeholder="">图层</th>
					<!-- <th data-field="type" data-formatter="itemsetTypesFormat"
						data-filter-control="select" data-filter-data="var:itemsetTypes">类型</th> -->
					<!-- <th data-field="systype" data-formatter="sysFormat"
						data-filter-control="select"
						data-filter-data="var:itemsetSysTypes">操作系统</th> -->
					<th data-field="referdata" data-filter-control="input"
						data-formatter="referdataFormat"
						data-filter-control-placeholder="" data-width="200">参考图层</th>
					<!-- <th data-field="unit" data-formatter="unitFormat"
						data-filter-control="select" data-filter-data="var:itemsetUnits">质检单位</th> -->
					<th data-field="desc" data-filter-control="input" data-width="140"
						data-formatter="descFormat" data-filter-control-placeholder="">描述</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="datasetsDlg" style="display: none;">
		<table id="datasetslist" class="table-condensed" data-unique-id="id"
			data-url="./processesmanage.web?atn=getdatasets" data-cache="false"
			data-side-pagination="server" data-filter-control="true"
			data-click-to-select="true" data-single-select="false"
			data-select-item-name="checkbox" data-pagination="false"
			data-page-list="[15, 30, 50, All]"
			data-toggle="datasets" data-height="520"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true"></th>
					<th data-field="index" data-class="indexHidden"
						data-formatter="indexFormat"></th>
					<th data-field="id" data-filter-control="input"
						data-filter-control-placeholder="" data-width="50">编号</th>
						
					<th data-field="name" data-filter-control="input"
						data-formatter="nameFormat" data-filter-control-placeholder="">名称</th>
						
					<th data-field="path" data-filter-control-placeholder=""
						data-filter-control="input">路径</th>
						
					<th data-field="datatype" data-width="40"
						data-filter-control="input" data-filter-control-placeholder="">类型</th>
						
					<th data-field="batchid" data-width="180"
						data-filter-control="input" data-filter-control-placeholder="">批次号</th>
				</tr>
			</thead>
		</table>
	</div>
</body>
</html>

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
				console.log("onPostBody：" + autoRefreshProcess);
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
	
	var itemAreaFirstIn = true;
	var itemAreaFirstClick = true;
	var itemAreaSelected = new Array();
	var itemAreaIDSelected = new Array();
	var itemAreaOn = -1;
	
	var itemSetFirstIn = true;
	var itemSetFirstClick = true;
	var itemSetSelected = new Array();
	var itemSetIDSelected = new Array();
	var itemSetOn = -1;
	
	var workerFirstIn = true;
	var workerFirstClick = true;
	var workerSelected = new Array();
	var workerIDSelected = new Array();
	var workerOn = -1;
	
	var checkerFirstIn = true;
	var checkerFirstClick = true;
	var checkerSelected = new Array();
	var checkerIDSelected = new Array();
	var checkerOn = -1;
	
	var datasetFirstIn = true;
	var datasetFirstClick = true;
	var datasetSelected = new Array();
	var datasetIDSelected = new Array();
	var datasetOn = -1;

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
		if (row.state == 1) {
			html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="changeState(2,' + row.id + ')">暂停</div>');
		} else if (row.state == 2 || row.state == 0) {
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
		$("#config_processid").val(new String());
		$("#config_processname").val(new String());
		$("#config_processpriority").val(0);
		$("#config_processprotype").val(1);
		$("#config_processstatus").val(0);
		
		$("#config_1_5").prop('selectedIndex', 0);
		$("#config_1_6").val(new String());
		$("#config_1_6Count").text(0);
		$("#config_1_7").val(new String());
		$("#config_1_7Count").text(0);
		
		$("#config_2_18").val(new String());
		$("#config_2_18Count").text(0);
		$("#config_2_19").prop('selectedIndex', 0);
		$("#config_2_21").val(new String());
		$("#config_2_21Count").text(0);
		$("#config_2_22").prop('selectedIndex', 0);
		$("#config_2_23").prop('selectedIndex', 0);
		$("#config_2_25").val(new String());
		$("#config_2_25Count").text(0);
		
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
					$("#config_1_6Count").text(config_1_6 ? config_1_6.split(",").length : 0);

					var config_1_7 = $("#config_1_7").val();
					$("#config_1_7Count").text(config_1_7 ? config_1_7.split(",").length : 0);

					var config_2_18 = $("#config_2_18").val();
					$("#config_2_18Count").text(config_2_18 ? config_2_18.split(",").length : 0);
					
					var config_2_21 = $("#config_2_21").val();
					$("#config_2_21Count").text(config_2_21 ? config_2_21.split(",").length : 0);
					
					var config_2_25 = $("#config_2_25").val();
					$("#config_2_25Count").text(config_2_25 ? config_2_25.split(",").length : 0);
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
							width : document.documentElement.clientWidth * 0.35,
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
	
	function getWorkers() {
		$('[data-toggle="workers"]').bootstrapTable(
				{
					locale : 'zh-CN',
					queryParams : function(params) {
						if (params.filter != undefined) {
							var filterObj = eval('(' + params.filter + ')');
							if (filterObj.state != undefined) {
								filterObj["state"] = filterObj.state;
								delete filterObj.state;
								params.filter = JSON.stringify(filterObj);
							}
						}
						return params;
					},
					onLoadSuccess : function(data) {
						workerOn = -1;
						workerSelected = new Array();
						workerFirstClick = true;
						
						var values = new Array();
						if(workerFirstIn) {
							var str_values = $("#config_2_18").val();
							try{
								var map = JSON.parse(str_values);
								if (map && typeof map == 'object') {
									for (var key in map){
										values.push(parseInt(key));
									}
								} else if (map && typeof map == 'number') {
									values.push(map);
								}
							}catch(e){
								$.each(str_values.split(","), function(index, domEle) {
									values[index] = parseInt(domEle);
								});
							}
						} else {
							$.each(workerIDSelected, function(index, domEle) {
								values.push(parseInt(domEle));
							});
						}
						
						$('[data-toggle="workers"]').bootstrapTable("checkBy",
								{
									field : "id",
									values : values
								});
						workerFirstIn = false;
					},
					onCheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						if(workerSelected.indexOf(index) < 0) {
							workerOn = index;
							workerSelected.push(index);
							workerSelected.sort(compare);
						}
						var id = row.id;
						if(workerIDSelected.indexOf(id) < 0) {
							workerIDSelected.push(id);
						}
					},
					onUncheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						var indexIn = workerSelected.indexOf(index);
						if(indexIn >= 0) {
							workerOn = workerSelected[indexIn == 0 ? 0 : indexIn -1];
							workerSelected.splice(indexIn,1).sort(compare);
						}
						var id = row.id;
						var idIn = workerIDSelected.indexOf(id);
						if(idIn >= 0) {
							workerIDSelected.splice(idIn, 1);
						}
					},
					onCheckAll : function(rows) {
						var elements = $('[data-toggle="workers"] td.indexHidden');
						$.each(elements, function(i, element){
							var index = parseInt($(element).text());
							if(workerSelected.indexOf(index) < 0) {
								workerSelected.push(index);
							}
						});
						workerOn = parseInt($('[data-toggle="workers"] td.indexHidden:last').text());
						workerSelected.sort(compare);
						$.each(rows, function(i, row){
							var id = row.id;
							if(workerIDSelected.indexOf(id) < 0) {
								workerIDSelected.push(id);
							}
						});
					},
					onUncheckAll : function(rows) {
						var elements = $('[data-toggle="workers"] td.indexHidden');
						$.each(elements, function(i, element){
							var index = parseInt($(element).text());
							var indexIn = workerSelected.indexOf(index);
							if(indexIn >= 0) {
								workerOn = workerSelected[indexIn == 0 ? 0 : indexIn -1];
								workerSelected.splice(indexIn,1).sort(compare);
							}
						});
						workerOn = parseInt($('[data-toggle="workers"] td.indexHidden:last').text());
						workerSelected.sort(compare);
						$.each(rows, function(i, row){
							var id = row.id;
							var idIn = workerIDSelected.indexOf(id);
							if(idIn >= 0) {
								workerIDSelected.splice(idIn, 1);
							}
						});
					}
				});

		showWorkersDlg();
	}

	function showWorkersDlg() {
		$("#workersDlg").dialog(
				{
					modal : true,
					width : 500,
					title : "添加制作人员",
					open : function(event, ui) {
						workerFirstClick = true;
						workerFirstIn= true;
						$(".ui-dialog-titlebar-close").hide();
					},
					close : function() {
						workerOn = -1;
						workerSelected = new Array();
						workerIDSelected = new Array();
						workerFirstClick = true;
						workerFirstIn= true;
						$('[data-toggle="workers"]').bootstrapTable("destroy");
					},
					buttons : [
							{
								text : "<",
								title : "上一条",
								class : "btn btn-default",
								click : function() {
									if(!workerSelected || workerSelected.length <= 0) {
										$.webeditor.showMsgLabel("warning", "没有勾选项");
										return;
									}
									if(workerFirstClick) {
										$('[data-toggle="workers"]').bootstrapTable('scrollTo',workerSelected[0] * 41);
										workerOn = workerSelected[0];
										workerFirstClick = false;
									} else {
										if (workerOn < 0) {
											$('[data-toggle="workers"]').bootstrapTable('scrollTo', 0);
											$.webeditor.showMsgLabel("warning","已经跳转到第一条");
										} else {
											var index = workerSelected.indexOf(workerOn);
											if (index < 0) {
												$('[data-toggle="workers"]').bootstrapTable('scrollTo',0);
											} else if (index > workerSelected.length - 1) {
												$('[data-toggle="workers"]').bootstrapTable('scrollTo','bottom');
											} else {
												if (index == 0) {
													$('[data-toggle="workers"]').bootstrapTable('scrollTo',workerSelected[0] * 41);
													workerOn = workerSelected[0];
													$.webeditor.showMsgLabel("warning","已经跳转到第一条");
												} else {
													var preIndex = index - 1;
													$('[data-toggle="workers"]').bootstrapTable('scrollTo',workerSelected[preIndex] * 41);
													workerOn = workerSelected[preIndex];
												}
											}
										}
									}
								}
							},
							{
								text : ">",
								title : "下一条",
								class : "btn btn-default",
								click : function() {
									if(!workerSelected || workerSelected.length <= 0) {
										$.webeditor.showMsgLabel("warning", "没有勾选项");
										return;
									}
									if(workerFirstClick) {
										$('[data-toggle="workers"]').bootstrapTable('scrollTo',workerSelected[0] * 41);
										workerOn = workerSelected[0];
										workerFirstClick = false;
									} else {
										if (workerOn < 0) {
											$('[data-toggle="workers"]').bootstrapTable('scrollTo', 0);
										} else {
											var index = workerSelected.indexOf(workerOn);
											if (index < 0) {
												$('[data-toggle="workers"]').bootstrapTable('scrollTo',0);
											} else if (index > workerSelected.length - 1) {
												$('[data-toggle="workers"]').bootstrapTable('scrollTo','bottom');
											} else {
												if (index == workerSelected.length - 1) {
													var nextIndex = workerSelected.length - 1;
													$('[data-toggle="workers"]').bootstrapTable('scrollTo',workerSelected[nextIndex] * 41);
													workerOn = workerSelected[nextIndex];
													$.webeditor.showMsgLabel("warning","已经跳转到最后一条");
												} else {
													var nextIndex = index + 1;
													$('[data-toggle="workers"]').bootstrapTable('scrollTo',workerSelected[nextIndex] * 41);
													workerOn = workerSelected[nextIndex];
												}
											}
										}
									}
								}
							},
							{
								text : "提交",
								class : "btn btn-default",
								click : function() {
									var length = workerIDSelected.length;
									if (length > 0) {
										var m = {};
										$.each(workerIDSelected, function(i, workerID){
											var sampling = $("#sampling-" + workerID).val();
											m[workerID] = sampling;
										});
										$("#config_2_18").val(JSON.stringify(m));
										$("#config_2_18Count").text(length);

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
							} ]
				});
	}
	
	function getCheckers() {
		$('[data-toggle="checkers"]').bootstrapTable(
				{
					locale : 'zh-CN',
					queryParams : function(params) {
						if (params.filter != undefined) {
							var filterObj = eval('(' + params.filter + ')');
							if (filterObj.state != undefined) {
								filterObj["state"] = filterObj.state;
								delete filterObj.state;
								params.filter = JSON.stringify(filterObj);
							}
						}
						return params;
					},
					onLoadSuccess : function(data) {
						checkerOn = -1;
						checkerSelected = new Array();
						checkerFirstClick = true;
						
						var values = new Array();
						if(checkerFirstIn) {
							var str_values = $("#config_2_21").val();
							$.each(str_values.split(","), function(index, domEle) {
								values[index] = parseInt(domEle);
							});
						} else {
							$.each(checkerIDSelected, function(index, domEle) {
								values.push(parseInt(domEle));
							});
						}
						
						$('[data-toggle="checkers"]').bootstrapTable("checkBy",
								{
									field : "id",
									values : values
								});
						checkerFirstIn = false;
					},
					onCheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						if(checkerSelected.indexOf(index) < 0) {
							checkerOn = index;
							checkerSelected.push(index);
							checkerSelected.sort(compare);
						}
						var id = row.id;
						if(checkerIDSelected.indexOf(id) < 0) {
							checkerIDSelected.push(id);
						}
					},
					onUncheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						var indexIn = checkerSelected.indexOf(index);
						if(indexIn >= 0) {
							checkerOn = checkerSelected[indexIn == 0 ? 0 : indexIn -1];
							checkerSelected.splice(indexIn,1).sort(compare);
						}
						var id = row.id;
						var idIn = checkerIDSelected.indexOf(id);
						if(idIn >= 0) {
							checkerIDSelected.splice(idIn, 1);
						}
					},
					onCheckAll : function(rows) {
						var elements = $('[data-toggle="checkers"] td.indexHidden');
						$.each(elements, function(i, element){
							var index = parseInt($(element).text());
							if(checkerSelected.indexOf(index) < 0) {
								checkerSelected.push(index);
							}
						});
						checkerOn = parseInt($('[data-toggle="checkers"] td.indexHidden:last').text());
						checkerSelected.sort(compare);
						$.each(rows, function(i, row){
							var id = row.id;
							if(checkerIDSelected.indexOf(id) < 0) {
								checkerIDSelected.push(id);
							}
						});
					},
					onUncheckAll : function(rows) {
						var elements = $('[data-toggle="checkers"] td.indexHidden');
						$.each(elements, function(i, element){
							var index = parseInt($(element).text());
							var indexIn = checkerSelected.indexOf(index);
							if(indexIn >= 0) {
								checkerOn = checkerSelected[indexIn == 0 ? 0 : indexIn -1];
								checkerSelected.splice(indexIn,1).sort(compare);
							}
						});
						checkerOn = parseInt($('[data-toggle="checkers"] td.indexHidden:last').text());
						checkerSelected.sort(compare);
						$.each(rows, function(i, row){
							var id = row.id;
							var idIn = checkerIDSelected.indexOf(id);
							if(idIn >= 0) {
								checkerIDSelected.splice(idIn, 1);
							}
						});
					}
				});

		showCheckersDlg();
	}

	function showCheckersDlg() {
		$("#checkersDlg").dialog(
				{
					modal : true,
					width : 500,
					title : "添加校正人员",
					open : function(event, ui) {
						checkerFirstClick = true;
						checkerFirstIn= true;
						$(".ui-dialog-titlebar-close").hide();
					},
					close : function() {
						checkerOn = -1;
						checkerSelected = new Array();
						checkerIDSelected = new Array();
						checkerFirstClick = true;
						checkerFirstIn= true;
						$('[data-toggle="checkers"]').bootstrapTable("destroy");
					},
					buttons : [
							{
								text : "<",
								title : "上一条",
								class : "btn btn-default",
								click : function() {
									if(!checkerSelected || checkerSelected.length <= 0) {
										$.webeditor.showMsgLabel("warning", "没有勾选项");
										return;
									}
									if(checkerFirstClick) {
										$('[data-toggle="checkers"]').bootstrapTable('scrollTo',checkerSelected[0] * 31);
										checkerOn = checkerSelected[0];
										checkerFirstClick = false;
									} else {
										if (checkerOn < 0) {
											$('[data-toggle="checkers"]').bootstrapTable('scrollTo', 0);
											$.webeditor.showMsgLabel("warning","已经跳转到第一条");
										} else {
											var index = checkerSelected.indexOf(checkerOn);
											if (index < 0) {
												$('[data-toggle="checkers"]').bootstrapTable('scrollTo',0);
											} else if (index > checkerSelected.length - 1) {
												$('[data-toggle="checkers"]').bootstrapTable('scrollTo','bottom');
											} else {
												if (index == 0) {
													$('[data-toggle="checkers"]').bootstrapTable('scrollTo',checkerSelected[0] * 31);
													checkerOn = checkerSelected[0];
													$.webeditor.showMsgLabel("warning","已经跳转到第一条");
												} else {
													var preIndex = index - 1;
													$('[data-toggle="checkers"]').bootstrapTable('scrollTo',checkerSelected[preIndex] * 31);
													checkerOn = checkerSelected[preIndex];
												}
											}
										}
									}
								}
							},
							{
								text : ">",
								title : "下一条",
								class : "btn btn-default",
								click : function() {
									if(!checkerSelected || checkerSelected.length <= 0) {
										$.webeditor.showMsgLabel("warning", "没有勾选项");
										return;
									}
									if(checkerFirstClick) {
										$('[data-toggle="checkers"]').bootstrapTable('scrollTo',checkerSelected[0] * 31);
										checkerOn = checkerSelected[0];
										checkerFirstClick = false;
									} else {
										if (checkerOn < 0) {
											$('[data-toggle="checkers"]').bootstrapTable('scrollTo', 0);
										} else {
											var index = checkerSelected.indexOf(checkerOn);
											if (index < 0) {
												$('[data-toggle="checkers"]').bootstrapTable('scrollTo',0);
											} else if (index > checkerSelected.length - 1) {
												$('[data-toggle="checkers"]').bootstrapTable('scrollTo','bottom');
											} else {
												if (index == checkerSelected.length - 1) {
													var nextIndex = checkerSelected.length - 1;
													$('[data-toggle="checkers"]').bootstrapTable('scrollTo',checkerSelected[nextIndex] * 31);
													checkerOn = checkerSelected[nextIndex];
													$.webeditor.showMsgLabel("warning","已经跳转到最后一条");
												} else {
													var nextIndex = index + 1;
													$('[data-toggle="checkers"]').bootstrapTable('scrollTo',checkerSelected[nextIndex] * 31);
													checkerOn = checkerSelected[nextIndex];
												}
											}
										}
									}
								}
							},
							{
								text : "提交",
								class : "btn btn-default",
								click : function() {
									var length = checkerIDSelected.length;
									if (length > 0) {
										$("#config_2_21").val(checkerIDSelected.join(","));
										$("#config_2_21Count").text(length);

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
							} ]
				});
	}

	function getItemAreas() {
		$('[data-toggle="itemAreas"]').bootstrapTable(
				{
					locale : 'zh-CN',
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
					},
					onLoadSuccess : function(data) {
						itemAreaOn = -1;
						itemAreaSelected = new Array();
						itemAreaFirstClick = true;
						
						var values = new Array();
						if(itemAreaFirstIn) {
							var str_values = $("#config_1_7").val();
							$.each(str_values.split(","), function(index, domEle) {
								values.push(parseInt(domEle));
							});
						} else {
							$.each(itemAreaIDSelected, function(index, domEle) {
								values.push(parseInt(domEle));
							});
						}
						
						$('[data-toggle="itemAreas"]').bootstrapTable(
								"checkBy", {
									field : "id",
									values : values
								});
						itemAreaFirstIn = false;
						var state = $("#config_processstatus").val();
						if(state !== "0") {
							$("#itemAreaslist input:checkbox").attr("disabled", true);
						} else {
							$("#itemAreaslist input:checkbox").removeAttr("disabled");
						} 
					},
					onCheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						if(itemAreaSelected.indexOf(index) < 0) {
							itemAreaOn = index;
							itemAreaSelected.push(index);
							itemAreaSelected.sort(compare);
						}
						var id = row.id;
						if(itemAreaIDSelected.indexOf(id) < 0) {
							itemAreaIDSelected.push(id);
						}
					},
					onUncheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						var indexIn = itemAreaSelected.indexOf(index);
						if(indexIn >= 0) {
							itemAreaOn = itemAreaSelected[indexIn == 0 ? 0 : indexIn -1];
							itemAreaSelected.splice(indexIn,1).sort(compare);
						}
						var id = row.id;
						var idIn = itemAreaIDSelected.indexOf(id);
						if(idIn >= 0) {
							itemAreaIDSelected.splice(idIn, 1);
						}
					},
					onCheckAll : function(rows) {
						var elements = $('[data-toggle="itemAreas"] td.indexHidden');
						$.each(elements, function(i, element){
							var index = parseInt($(element).text());
							if(itemAreaSelected.indexOf(index) < 0) {
								itemAreaSelected.push(index);
							}
						});
						itemAreaOn = parseInt($('[data-toggle="itemAreas"] td.indexHidden:last').text());
						itemAreaSelected.sort(compare);
						$.each(rows, function(i, row){
							var id = row.id;
							if(itemAreaIDSelected.indexOf(id) < 0) {
								itemAreaIDSelected.push(id);
							}
						});
					},
					onUncheckAll : function(rows) {
						var elements = $('[data-toggle="itemAreas"] td.indexHidden');
						$.each(elements, function(i, element){
							var index = parseInt($(element).text());
							var indexIn = itemAreaSelected.indexOf(index);
							if(indexIn >= 0) {
								itemAreaOn = itemAreaSelected[indexIn == 0 ? 0 : indexIn -1];
								itemAreaSelected.splice(indexIn,1).sort(compare);
							}
						});
						itemAreaOn = parseInt($('[data-toggle="itemAreas"] td.indexHidden:last').text());
						itemAreaSelected.sort(compare);
						$.each(rows, function(i, row){
							var id = row.id;
							var idIn = itemAreaIDSelected.indexOf(id);
							if(idIn >= 0) {
								itemAreaIDSelected.splice(idIn, 1);
							}
						});
					}
				});

		showItemAreasDlg();
	}

	function showItemAreasDlg() {
		$("#itemAreasDlg").dialog(
				{
					modal : true,
					width : 660,
					title : "质检区域配置",
					open : function(event, ui) {
						itemAreaFirstClick = true;
						itemAreaFirstIn = true;
						$(".ui-dialog-titlebar-close").hide();
					},
					close : function() {
						itemAreaOn = -1;
						itemAreaSelected = new Array();
						itemAreaIDSelected = new Array();
						itemAreaFirstClick = true;
						itemAreaFirstIn = true;
						$('[data-toggle="itemAreas"]').bootstrapTable("destroy");
					},
					buttons : [
							{
								text : "<",
								title : "上一条",
								class : "btn btn-default",
								click : function() {
									if(!itemAreaSelected || itemAreaSelected.length <= 0) {
										$.webeditor.showMsgLabel("warning", "没有勾选项");
										return;
									}
									if(itemAreaFirstClick) {
										$('[data-toggle="itemAreas"]').bootstrapTable('scrollTo', itemAreaSelected[0]*31);
										itemAreaOn = itemAreaSelected[0];
										itemAreaFirstClick = false;
									} else {
										if(itemAreaOn < 0) {
											$('[data-toggle="itemAreas"]').bootstrapTable('scrollTo', 0);
											$.webeditor.showMsgLabel("warning","已经跳转到第一条");
										} else {
											var index = itemAreaSelected.indexOf(itemAreaOn);
											if(index < 0) {
												$('[data-toggle="itemAreas"]').bootstrapTable('scrollTo', 0);
											} else if(index > itemAreaSelected.length-1) {
												$('[data-toggle="itemAreas"]').bootstrapTable('scrollTo', 'bottom');
											} else {
												if(index == 0) {
													$('[data-toggle="itemAreas"]').bootstrapTable('scrollTo', itemAreaSelected[0]*31);
													itemAreaOn = itemAreaSelected[0];
													$.webeditor.showMsgLabel("warning", "已经跳转到第一条");
												} else {
													var preIndex = index-1;
													$('[data-toggle="itemAreas"]').bootstrapTable('scrollTo', itemAreaSelected[preIndex]*31);
													itemAreaOn = itemAreaSelected[preIndex];
												}
											}
										}
									}
								}
							},
							{
								text : ">",
								title : "下一条",
								class : "btn btn-default",
								click : function() {
									if(!itemAreaSelected || itemAreaSelected.length <= 0) {
										$.webeditor.showMsgLabel("warning", "没有勾选项");
										return;
									}
									if(itemAreaFirstClick) {
										$('[data-toggle="itemAreas"]').bootstrapTable('scrollTo', itemAreaSelected[0]*31);
										itemAreaOn = itemAreaSelected[0];
										itemAreaFirstClick = false;
									} else {
										if(itemAreaOn < 0) {
											$('[data-toggle="itemAreas"]').bootstrapTable('scrollTo', 0);
										} else {
											var index = itemAreaSelected.indexOf(itemAreaOn);
											if(index < 0) {
												$('[data-toggle="itemAreas"]').bootstrapTable('scrollTo', 0);
											} else if(index > itemAreaSelected.length-1) {
												$('[data-toggle="itemAreas"]').bootstrapTable('scrollTo', 'bottom');
											} else {
												if(index == itemAreaSelected.length-1) {
													var nextIndex = itemAreaSelected.length-1;
													$('[data-toggle="itemAreas"]').bootstrapTable('scrollTo', itemAreaSelected[nextIndex]*31);
													itemAreaOn = itemAreaSelected[nextIndex];
													$.webeditor.showMsgLabel("warning", "已经跳转到最后一条");
												} else {
													var nextIndex = index+1;
													$('[data-toggle="itemAreas"]').bootstrapTable('scrollTo', itemAreaSelected[nextIndex]*31);
													itemAreaOn = itemAreaSelected[nextIndex];
												}
											}
										}
									}
								}
							},
							{
								text : "提交",
								class : "btn btn-default",
								click : function() {
									var length = itemAreaIDSelected.length;
									if (length > 0) {
										$("#config_1_7").val(itemAreaIDSelected.join(","));
										$("#config_1_7Count").text(length);

										$(this).dialog("close");
									} else {
										$.webeditor.showMsgLabel("alert", "请选择质检区域");
									}
								}
							},
							{
								text : "关闭",
								class : "btn btn-default",
								click : function() {
									$(this).dialog("close");
								}
							} ]
				});
	}

	function getItemsets() {
		$('[data-toggle="itemsets"]').bootstrapTable(
				{
					locale : 'zh-CN',
					queryParams : function(params) {
						params["processType"] = $("#config_processprotype").val();
						return params;
					},
					onLoadSuccess : function(data) {
						itemSetOn = -1;
						itemSetSelected = new Array();
						itemSetFirstClick = true;
						
						var values = new Array();
						if(itemSetFirstIn) {
							$.each($("#config_1_6").val().split(","), function(index, domEle) {
								values.push(parseInt(domEle));
							});
						} else {
							$.each(itemSetIDSelected, function(index, domEle) {
								values.push(parseInt(domEle));
							});
						}
						
						$('[data-toggle="itemsets"]').bootstrapTable("checkBy",
								{
									field : "id",
									values : values
								});
						itemSetFirstIn = false;
						var state = $("#config_processstatus").val();
						if(state !== "0") {
							$("#itemsetslist input:checkbox").attr("disabled", true);
						} else {
							$("#itemsetslist input:checkbox").removeAttr("disabled");
						} 
					},
					onCheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						if(itemSetSelected.indexOf(index) < 0) {
							itemSetOn = index;
							itemSetSelected.push(index);
							itemSetSelected.sort(compare);
						}
						var id = row.id;
						if(itemSetIDSelected.indexOf(id) < 0) {
							itemSetIDSelected.push(id);
						}
					},
					onUncheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						var indexIn = itemSetSelected.indexOf(index);
						if(indexIn >= 0) {
							itemSetOn = itemSetSelected[indexIn == 0 ? 0 : indexIn -1];
							itemSetSelected.splice(indexIn,1).sort(compare);
						}
						var id = row.id;
						var idIn = itemSetIDSelected.indexOf(id);
						if(idIn >= 0) {
							itemSetIDSelected.splice(idIn, 1);
						}
					},
					onCheckAll : function(rows) {
						var elements = $('[data-toggle="itemsets"] td.indexHidden');
						$.each(elements, function(i, element){
							var index = parseInt($(element).text());
							if(itemSetSelected.indexOf(index) < 0) {
								itemSetSelected.push(index);
							}
						});
						itemSetOn = parseInt($('[data-toggle="itemsets"] td.indexHidden:last').text());
						itemSetSelected.sort(compare);
						$.each(rows, function(i, row){
							var id = row.id;
							if(itemSetIDSelected.indexOf(id) < 0) {
								itemSetIDSelected.push(id);
							}
						});
					},
					onUncheckAll : function(rows) {
						var elements = $('[data-toggle="itemsets"] td.indexHidden');
						$.each(elements, function(i, element){
							var index = parseInt($(element).text());
							var indexIn = itemSetSelected.indexOf(index);
							if(indexIn >= 0) {
								itemSetOn = itemSetSelected[indexIn == 0 ? 0 : indexIn -1];
								itemSetSelected.splice(indexIn,1).sort(compare);
							}
						});
						itemSetOn = parseInt($('[data-toggle="itemsets"] td.indexHidden:last').text());
						itemSetSelected.sort(compare);
						$.each(rows, function(i, row){
							var id = row.id;
							var idIn = itemSetIDSelected.indexOf(id);
							if(idIn >= 0) {
								itemSetIDSelected.splice(idIn, 1);
							}
						});
					}
				});

		showItemsetsDlg();
	}

	function showItemsetsDlg() {
		$("#itemsetsDlg").dialog(
				{
					modal : true,
					width : 920,
					title : "质检图层配置",
					open : function(event, ui) {
						itemSetFirstClick = true;
						itemSetFirstIn = true;
						$(".ui-dialog-titlebar-close").hide();
					},
					close : function() {
						itemSetOn = -1;
						itemSetSelected = new Array();
						itemSetIDSelected = new Array();
						itemSetFirstClick = true;
						itemSetFirstIn = true;
						$('[data-toggle="itemsets"]').bootstrapTable("destroy");
					},
					buttons : [
									{
										text : "提交",
										class : "btn btn-default",
										click : function() {
											var length = itemSetIDSelected.length;
											var value = new String();
											if (length > 0) {
												value = itemSetIDSelected.join(",");
												$("#config_1_6").val(value);
												$("#config_1_6Count").text(length);

												$(this).dialog("close");
											} else {
												$.webeditor.showMsgLabel("alert", "请选择质检区域");
											}
										}
									},
									{
										text : "关闭",
										class : "btn btn-default",
										click : function() {
											$(this).dialog("close");
										}
									} ]
						});
	}
	
	function getDataset() {
		$('[data-toggle="datasets"]').bootstrapTable(
				{
					locale : 'zh-CN',
					queryParams : function(params) {
						if (params.filter != undefined) {
							var filterObj = eval('(' + params.filter + ')');
							if (filterObj.state != undefined) {
								filterObj["state"] = filterObj.state;
								delete filterObj.state;
								params.filter = JSON.stringify(filterObj);
							}
						}
						return params;
					},
					onLoadSuccess : function(data) {
						datasetOn = -1;
						datasetSelected = new Array();
						datasetFirstClick = true;
						
						var values = new Array();
						if(datasetFirstIn) {
							var str_values = $("#config_2_25").val();
							$.each(str_values.split(","), function(index, domEle) {
								values[index] = parseInt(domEle);
							});
						} else {
							$.each(datasetIDSelected, function(index, domEle) {
								values.push(parseInt(domEle));
							});
						}
						
						$('[data-toggle="datasets"]').bootstrapTable("checkBy",
								{
									field : "id",
									values : values
								});
						datasetFirstIn = false;
					},
					onCheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						if(datasetSelected.indexOf(index) < 0) {
							datasetOn = index;
							datasetSelected.push(index);
							datasetSelected.sort(compare);
						}
						var id = row.id;
						if(datasetIDSelected.indexOf(id) < 0) {
							datasetIDSelected.push(id);
						}
					},
					onUncheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						var indexIn = datasetSelected.indexOf(index);
						if(indexIn >= 0) {
							datasetOn = datasetSelected[indexIn == 0 ? 0 : indexIn -1];
							datasetSelected.splice(indexIn,1).sort(compare);
						}
						var id = row.id;
						var idIn = datasetIDSelected.indexOf(id);
						if(idIn >= 0) {
							datasetIDSelected.splice(idIn, 1);
						}
					},
					onCheckAll : function(rows) {
						var elements = $('[data-toggle="datasets"] td.indexHidden');
						$.each(elements, function(i, element){
							var index = parseInt($(element).text());
							if(datasetSelected.indexOf(index) < 0) {
								datasetSelected.push(index);
							}
						});
						datasetOn = parseInt($('[data-toggle="datasets"] td.indexHidden:last').text());
						datasetSelected.sort(compare);
						$.each(rows, function(i, row){
							var id = row.id;
							if(datasetIDSelected.indexOf(id) < 0) {
								datasetIDSelected.push(id);
							}
						});
					},
					onUncheckAll : function(rows) {
						var elements = $('[data-toggle="datasets"] td.indexHidden');
						$.each(elements, function(i, element){
							var index = parseInt($(element).text());
							var indexIn = datasetSelected.indexOf(index);
							if(indexIn >= 0) {
								datasetOn = datasetSelected[indexIn == 0 ? 0 : indexIn -1];
								datasetSelected.splice(indexIn,1).sort(compare);
							}
						});
						datasetOn = parseInt($('[data-toggle="datasets"] td.indexHidden:last').text());
						datasetSelected.sort(compare);
						$.each(rows, function(i, row){
							var id = row.id;
							var idIn = datasetIDSelected.indexOf(id);
							if(idIn >= 0) {
								datasetIDSelected.splice(idIn, 1);
							}
						});
					}
				});

		showBatchesDlg();
	}

	function showBatchesDlg() {
		$("#datasetsDlg").dialog(
				{
					modal : true,
					width : 1000,
					title : "绑定资料",
					open : function(event, ui) {
						datasetFirstClick = true;
						datasetFirstIn= true;
						$(".ui-dialog-titlebar-close").hide();
					},
					close : function() {
						datasetOn = -1;
						datasetSelected = new Array();
						datasetIDSelected = new Array();
						datasetFirstClick = true;
						datasetFirstIn= true;
						$('[data-toggle="datasets"]').bootstrapTable("destroy");
					},
					buttons : [
							{
								text : "<",
								title : "上一条",
								class : "btn btn-default",
								click : function() {
									if(!datasetSelected || datasetSelected.length <= 0) {
										$.webeditor.showMsgLabel("warning", "没有勾选项");
										return;
									}
									if(datasetFirstClick) {
										$('[data-toggle="datasets"]').bootstrapTable('scrollTo',datasetSelected[0] * 31);
										datasetOn = datasetSelected[0];
										datasetFirstClick = false;
									} else {
										if (datasetOn < 0) {
											$('[data-toggle="datasets"]').bootstrapTable('scrollTo', 0);
											$.webeditor.showMsgLabel("warning","已经跳转到第一条");
										} else {
											var index = datasetSelected.indexOf(datasetOn);
											if (index < 0) {
												$('[data-toggle="datasets"]').bootstrapTable('scrollTo',0);
											} else if (index > datasetSelected.length - 1) {
												$('[data-toggle="datasets"]').bootstrapTable('scrollTo','bottom');
											} else {
												if (index == 0) {
													$('[data-toggle="datasets"]').bootstrapTable('scrollTo',datasetSelected[0] * 31);
													datasetOn = datasetSelected[0];
													$.webeditor.showMsgLabel("warning","已经跳转到第一条");
												} else {
													var preIndex = index - 1;
													$('[data-toggle="datasets"]').bootstrapTable('scrollTo',datasetSelected[preIndex] * 31);
													datasetOn = datasetSelected[preIndex];
												}
											}
										}
									}
								}
							},
							{
								text : ">",
								title : "下一条",
								class : "btn btn-default",
								click : function() {
									if(!datasetSelected || datasetSelected.length <= 0) {
										$.webeditor.showMsgLabel("warning", "没有勾选项");
										return;
									}
									if(datasetFirstClick) {
										$('[data-toggle="datasets"]').bootstrapTable('scrollTo',datasetSelected[0] * 31);
										datasetOn = datasetSelected[0];
										datasetFirstClick = false;
									} else {
										if (datasetOn < 0) {
											$('[data-toggle="datasets"]').bootstrapTable('scrollTo', 0);
										} else {
											var index = datasetSelected.indexOf(datasetOn);
											if (index < 0) {
												$('[data-toggle="datasets"]').bootstrapTable('scrollTo',0);
											} else if (index > datasetSelected.length - 1) {
												$('[data-toggle="datasets"]').bootstrapTable('scrollTo','bottom');
											} else {
												if (index == datasetSelected.length - 1) {
													var nextIndex = datasetSelected.length - 1;
													$('[data-toggle="datasets"]').bootstrapTable('scrollTo',datasetSelected[nextIndex] * 31);
													datasetOn = datasetSelected[nextIndex];
													$.webeditor.showMsgLabel("warning","已经跳转到最后一条");
												} else {
													var nextIndex = index + 1;
													$('[data-toggle="datasets"]').bootstrapTable('scrollTo',datasetSelected[nextIndex] * 31);
													datasetOn = datasetSelected[nextIndex];
												}
											}
										}
									}
								}
							},
							{
								text : "提交",
								class : "btn btn-default",
								click : function() {
									var length = datasetIDSelected.length;
									if (length > 0) {
										$("#config_2_25").val(datasetIDSelected.join(","));
										$("#config_2_25Count").text(length);

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
							} ]
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
					<td class="configValue"><input type="hidden" id="config_1_7"
						value="">
						<button type="button" class="btn btn-default"
							onclick="getItemAreas();">配置区域</button>
						<p class="help-block">已选择<span id="config_1_7Count">0</span>个区域</p></td>
				</tr>
				<tr>
					<td class="configKey">图层</td>
					<td class="configValue"><input type="hidden" id="config_1_6"
						value="">
						<button type="button" class="btn btn-default"
							onclick="getItemsets();">配置图层</button>
						<p class="help-block">已选择<span id="config_1_6Count">0</span>个图层</p></td>
				</tr>
				<tr>
					<td class="configKey">人员</td>
					<td class="configValue">
						<input type="hidden" id="config_2_18" value="">
						<button type="button" class="btn btn-default"
							onclick="getWorkers();">添加改错人员</button>
						<input type="hidden" id="config_2_21" value="">
						<button type="button" class="btn btn-default"
							onclick="getCheckers();">添加校正人员</button>
						<p class="help-block">已添加<span id="config_2_18Count">0</span>位改错人员，<span id="config_2_21Count">0</span>位校正人员</p></td>
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
					<td class="configValue"><input type="hidden" id="config_2_25"
						value="">
						<button type="button" class="btn btn-default"
							onclick="getDataset();">绑定资料</button>
						<p class="help-block">已绑定<span id="config_2_25Count">0</span>份资料</p></td>
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
			data-toggle="workers" data-height="400"
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
			data-toggle="checkers" data-height="400"
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
			data-toggle="itemAreas" data-height="520"
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
						data-filter-control-placeholder="">省</th>
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

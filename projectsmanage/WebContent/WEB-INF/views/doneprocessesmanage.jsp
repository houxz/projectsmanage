<%@page import="com.emg.poiwebeditor.common.ProcessType"%>
<%@page import="com.emg.poiwebeditor.common.ModelEnum"%>
<%@page import="com.emg.poiwebeditor.common.ProcessEditType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<title>已完成项目</title>
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
	var itemmodels = eval('(${itemmodels})');
	var poiprojecTypes = eval('(${poiprojectTypes})');
	
	function ajaxRequest(params){
		$.ajax({
			url: "./processesmanage.web?atn=pages",
			type: "POST",
			dataType: "json",
			success: function(json){
				console.log(json);
				var rows = json.rows;
                params.success({
		            total: rows.length,
		            rows: rows
		        });
			},
			error: function(json){
				console.log(json);
			}
		});
	}
	
	function checkboxFormat(value, row, index) {
		var workers = $("#" + this.valueBand).val();
		var values = new Array();
		if (workers) {
			try{
				var map = JSON.parse(workers);
				if (map && typeof map == 'object') {
					for (var key in map){
						values.push(parseInt(key));
					}
				} else if (map && typeof map == 'number') {
					values.push(map);
				}
			}catch(e){
				$.each(workers.split(","), function(index, domEle) {
					values[index] = parseInt(domEle);
				});
			}
    	}
		if (values.indexOf(row.id) >= 0)
			return true;
		else
			return false;
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
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="bottom" title="发布进度：'
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
		} else if(processType == 3 || processType == 8 || processType == 9) {
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
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="bottom" title="发布进度：'
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
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="资料制作进度：' + parseFloat(values[0]).toFixed(3) + '&#8453;">');
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
			html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="错误修改进度：'
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
		} else if(processType == 6) {
			html.push('<div>');
			html.push('<div style="width: 80%;float: left;" data-toggle="tooltip" data-placement="top" title="编辑与校正进度：' + parseFloat(values[0]).toFixed(3) + '&#8453;">');
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
			html.push('</div>');
		}
		return html.join('');
	}

	function priFormat(value, row, index) {
		return priorityLevels[value];
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
	function samplingSet(value, id) {
		$("#workerlist").bootstrapTable("getRowByUniqueId", id).sampling = value;
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
		row.sampling = samplingValue;
		var html = new Array();
		html.push("<div class='input-group input-group-sm'>");
		html.push("<input type='text' class='form-control' maxlength='3' value='" + samplingValue + "' onchange='samplingSet(this.value, " + row.id + ");'>");
		html.push("<span class='input-group-addon'>%</span>");
		html.push("</div>");
		return html.join("");
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

		return html.join('');
	}

	function projcetsIDFormat(value, row, index) {
		var html = new Array();
		html.push('<input type="checkbox" value="' + row.id + '">' + row.id
				+ '');
		return html.join('');
	}
	
	function poiprojecttypeFormat(value, row, index) {
		return poiprojecTypes[row.poiprojecttype];
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
		$("#config_2_26").val(10);
		//add by lianhr begin 2019/02/14
		$("#strbatch").val("15" + new Date().Format("yyyyMMddHHmmss"));
		$("#config_1_29").prop('selectedIndex', 0);
		//add by lianhr end
		$("#config_2_30").prop('selectedIndex', 0);
		
		processTypeChange(1);
	}

	function getConfig(processid, processname, priority, processtype, state) {
		loadDefaultConfig();
		processTypeChange(processtype);
		if (processid > 0) {
			if (state != 0) {
				disableByProcessType(processtype);
			} else {
				enableAllConfigs();
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
						var a = configValues[index].moduleid;
						var b = configValues[index].configid;
						var aa = 2;
						var bb = 25;
						//add by lianhr begin 2019/02/14
						if(a==aa && b==bb){
							$("#strbatch").val(configValues[index].value);
						}
						//add by lianhr end
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
			enableAllConfigs();
		}
		showConfigDlg(processid, processname, priority, processtype, state);
	}

	function showConfigDlg(processid, processname, priority, processtype, state) {
		$("#configDlg").dialog({
			modal : true,
			width : 580,
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
						text : "关闭",
						class : "btn btn-default",
						click : function() {
							$(this).dialog("close");
						}
					} ]
		});
	}
	
	function getItemAreas() {
		$("#itemAreasDlg").bootstrapDialog({
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
				$(this.self).bootstrapTable("load", data.rows);
				
				var state = $("#config_processstatus").val();
				if(state !== "0") {
					$(this.self).find("input:checkbox").attr("disabled", true);
				} else {
					$(this.self).find("input:checkbox").removeAttr("disabled");
				} 
			}
		}, {
			width : 480,
			title : "质检区域配置",
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
					text : "关闭",
					class : "btn btn-default",
					click : function() {
						$(this).dialog("close");
					}
				}
			]
		});
	}
	
	function getItemsets() {
		$("#itemsetsDlg").bootstrapDialog({
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
				$(this.self).bootstrapTable("load", data.rows);
				
				var state = $("#config_processstatus").val();
				if(state !== "0") {
					$(this.self).find("input:checkbox").attr("disabled", true);
				} else {
					$(this.self).find("input:checkbox").removeAttr("disabled");
				} 
			}
		}, {
			width : document.documentElement.clientWidth * 0.6,
			title : "质检图层配置",
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
					text : "关闭",
					class : "btn btn-default",
					click : function() {
						$(this).dialog("close");
					}
				}
			]
		});
	}
	
	function getWorkers() {
		$("#workersDlg").bootstrapDialog({}, {
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
		$("#checkersDlg").bootstrapDialog({}, {
			width : 480,
			title : "添加校正人员",
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
					text : "关闭",
					class : "btn btn-default",
					click : function() {
						$(this).dialog("close");
					}
				}
			]
		});
	}
	
	function getDataset() {
		$("#datasetsDlg").bootstrapDialog({
			onLoadSuccess : function(data) {
				$(this.self).bootstrapTable("load", data.rows);
				
				var state = $("#config_processstatus").val();
				if(state !== "0") {
					$(this.self).find("input:checkbox:checked").attr("disabled", true);
				} else {
					$(this.self).find("input:checkbox").removeAttr("disabled");
				} 
			}
		}, {
			width : document.documentElement.clientWidth * 0.8,
			title : "绑定资料",
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
					text : "关闭",
					class : "btn btn-default",
					click : function() {
						$(this).dialog("close");
					}
				}
			]
		});
	}

	// TODO: 新增配置项需要补充啥时候显示
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
			$("#config_2_26").parents("tr").hide();
			$("#config_1_29").parents("tr").hide();
			$("#strbatch").parents("tr").hide();
			$("#config_2_30").parents("tr").hide();
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
			$("#config_2_26").parents("tr").hide();
			$("#config_1_29").parents("tr").hide();
			$("#strbatch").parents("tr").hide();
			$("#config_2_30").parents("tr").hide();
			break;
		case 3:
		case "3":
		case 9:
		case "9":
			$("#config_1_5").parents("tr").hide();
			$("#config_1_6").parents("tr").hide();
			$("#config_1_7").parents("tr").show();
			$("#config_2_18").parents("tr").show();
			$("#config_2_19").parents("tr").show();
			$("#config_2_21").parents("tr").show();
			$("#config_2_22").parents("tr").hide();
			$("#config_2_23").parents("tr").hide();
			$("#config_2_25").parents("tr").hide();
			$("#config_2_26").parents("tr").hide();
			$("#config_1_29").parents("tr").hide();
			$("#strbatch").parents("tr").hide();
			$("#config_2_30").parents("tr").hide();
			break;
		case 4:
		case "4":
			$("#config_1_5").parents("tr").show();
			$("#config_1_6").parents("tr").show();
			$("#config_1_7").parents("tr").show();
			$("#config_2_18").parents("tr").hide();
			$("#config_2_19").parents("tr").hide();
			$("#config_2_21").parents("tr").hide();
			$("#config_2_22").parents("tr").hide();
			$("#config_2_23").parents("tr").hide();
			$("#config_2_25").parents("tr").hide();
			$("#config_2_26").parents("tr").hide();
			$("#strbatch").parents("tr").show();
			$("#config_1_29").parents("tr").show();
			$("#config_2_30").parents("tr").hide();
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
			$("#config_2_26").parents("tr").show();
			$("#strbatch").parents("tr").hide();
			$("#config_1_29").parents("tr").hide();
			$("#config_2_30").parents("tr").hide();
			break;
		case 6:
		case "6":
			$("#config_1_5").parents("tr").hide();
			$("#config_1_6").parents("tr").hide();
			$("#config_1_7").parents("tr").hide();
			$("#config_2_18").parents("tr").show();
			$("#config_2_19").parents("tr").show();
			$("#config_2_21").parents("tr").show();
			$("#config_2_22").parents("tr").hide();
			$("#config_2_23").parents("tr").hide();
			$("#config_2_25").parents("tr").hide();
			$("#config_2_26").parents("tr").hide();
			$("#strbatch").parents("tr").hide();
			$("#config_1_29").parents("tr").hide();
			$("#config_2_30").parents("tr").hide();
			break;
		case 7:
		case "7":
			$("#config_1_5").parents("tr").hide();
			$("#config_1_6").parents("tr").hide();
			$("#config_1_7").parents("tr").hide();
			$("#config_2_18").parents("tr").show();
			$("#config_2_19").parents("tr").show();
			$("#config_2_21").parents("tr").show();
			$("#config_2_22").parents("tr").hide();
			$("#config_2_23").parents("tr").hide();
			$("#config_2_25").parents("tr").hide();
			$("#config_2_26").parents("tr").hide();
			$("#strbatch").parents("tr").hide();
			$("#config_1_29").parents("tr").hide();
			$("#config_2_30").parents("tr").hide();
			break;
		case 8:
		case "8":
			$("#config_1_5").parents("tr").hide();
			$("#config_1_6").parents("tr").hide();
			$("#config_1_7").parents("tr").show();
			$("#config_2_18").parents("tr").show();
			$("#config_2_19").parents("tr").show();
			$("#config_2_21").parents("tr").show();
			$("#config_2_22").parents("tr").hide();
			$("#config_2_23").parents("tr").hide();
			$("#config_2_25").parents("tr").hide();
			$("#config_2_26").parents("tr").hide();
			$("#config_1_29").parents("tr").hide();
			$("#strbatch").parents("tr").hide();
			$("#config_2_30").parents("tr").show();
			break;
		default:
			console.log("processTypeChange--错误的项目类型：" + selectValue);
			break;
		}
	}
	
	function enableAllConfigs() {
		$("#config_processname").removeAttr("disabled");
		$("#config_processprotype").removeAttr("disabled");
		$("#config_processpriority").removeAttr("disabled");
		
		$("#config_1_5").removeAttr("disabled");
		$("#config_2_19").removeAttr("disabled");
		$("#config_2_22").removeAttr("disabled");
		$("#config_2_26").removeAttr("disabled");
		$("#config_2_30").removeAttr("disabled");
	}
	
	function disableByProcessType(selectValue) {
		if(selectValue == 1 || selectValue == 2) {
			$("#config_processname").attr("disabled", true);
			$("#config_1_5").attr("disabled", true);
		} else if(selectValue == 5) {
			$("#config_2_22").attr("disabled", true);
			$("#config_2_26").attr("disabled", true);
		} else {
			$("#config_processname").removeAttr("disabled");
			$("#config_1_5").removeAttr("disabled");
			$("#config_2_22").removeAttr("disabled");
			$("#config_2_26").removeAttr("disabled");
		}
		$("#config_processprotype").attr("disabled", true);
		$("#config_2_30").attr("disabled", true);
	}
	//add by lianhr begin 2019/02/14
    Date.prototype.Format = function (fmt) {  
        var o = {  
            "M+": this.getMonth() + 1,
            "d+": this.getDate(),
            "H+": this.getHours(),
            "m+": this.getMinutes(),
            "s+": this.getSeconds(),
            "q+": Math.floor((this.getMonth() + 3) / 3),
            "S": this.getMilliseconds()
        };  
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));  
        for (var k in o)  
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));  
        return fmt;  
    }
	//add by lianhr end
</script>

</head>
<body>
	<div class="container">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px">
			<table id="processeslist" data-unique-id="id"
				data-query-params="queryParams"
				data-url="./doneprocesses.web?atn=pages"
				data-side-pagination="server" data-filter-control="true"
				data-pagination="true" data-toggle="processes" data-height="714"
				data-page-list="[5, 10, 20, 100]" data-page-size="5"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr>
						<th data-field="id" data-filter-control="input"
							data-filter-control-placeholder="" data-width="120">项目编号
						</th>
						<th data-field="name" data-filter-control="input"
							data-filter-control-placeholder="" data-width="160">项目名称</th>
<!-- 						<th data-field="type" data-formatter="processTypesFormat" -->
<!-- 							data-filter-control="select" data-width="170" -->
<!-- 							data-filter-data="var:processTypes">项目类型</th> -->
						<th data-field="poiprojecttype" data-formatter="poiprojecttypeFormat"
							data-filter-control="select" data-width="90"
							data-filter-data="var:poiprojecTypes" >POI项目类型</th>
						<th data-field="username" data-filter-control="input"
							data-filter-control-placeholder="" data-width="90">创建者</th>
						<th data-field="priority" data-formatter="priFormat"
							data-filter-control="select" data-width="120"
							data-filter-data="var:priorityLevels">优先级</th>
						<th data-field="progress" data-formatter="progressFormat"
							data-width="500">项目进度
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
					<td class="configKey">质检模式</td>
					<td class="configValue"><select class="form-control"
						id="config_1_29">
							<c:set var="itemmodels" value="<%= ModelEnum.values() %>"/>
							<c:forEach items="${itemmodels }" var="itemmodel">
								<c:if test="${itemmodel.getValue() > 0 }">
									<option value="${itemmodel.getValue() }">${itemmodel.getDes() }</option>
								</c:if>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">编辑类型</td>
					<td class="configValue"><select class="form-control"
						id="config_2_30">
							<c:set var="processEditTypes" value="<%= ProcessEditType.values() %>"/>
							<c:forEach items="${processEditTypes }" var="processEditType">
								<c:if test="${processEditType.getValue() > 0 }">
									<option value="${processEditType.getValue() }">${processEditType.getDes() }</option>
								</c:if>
							</c:forEach>
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
							onclick="getWorkers();">添加制作人员</button>
						<button type="button" class="btn btn-default"
							onclick="getCheckers();">添加校正人员</button>
						<p class="help-block" id="config_2_18">
							已添加<span>0</span>位制作人员
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
				<tr>
					<td class="configKey">批次</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="strbatch" value="" disabled>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div id="workersDlg" style="display: none;">
		<table id="workerlist" class="table-condensed"
			data-unique-id="id" data-value-band="config_2_18"
			data-url="./processesmanage.web?atn=getworkers"
			data-toggle="workers"
			data-height="420">
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true" data-value-band="config_2_18" data-formatter="checkboxFormat"></th>
					<th data-field="id" data-filter-control="input"
						data-filter-control-placeholder="" data-width="80">编号</th>
					<th data-field="realname" data-filter-control="input"
						data-filter-control-placeholder="">人员姓名</th>
					<th data-field="sampling" data-width="100" data-click-to-select="false" data-formatter="samplingFormat">抽检比例</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="checkersDlg" style="display: none;">
		<table id="checkerlist" class="table-condensed"
			data-unique-id="id" data-value-band="config_2_21"
			data-url="./processesmanage.web?atn=getcheckers"
			data-toggle="checkers"
			data-height="420">
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true" data-value-band="config_2_21" data-formatter="checkboxFormat"></th>
					<th data-field="id" data-filter-control="input"
						data-filter-control-placeholder="" data-width="20">编号</th>
					<th data-field="realname" data-filter-control="input"
						data-filter-control-placeholder="" data-width="120">人员姓名</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="itemAreasDlg" style="display: none;">
		<table id="itemAreaslist" class="table-condensed"
			data-unique-id="id" data-value-band="config_1_7"
			data-url="./processesmanage.web?atn=getitemareas"
			data-toggle="itemAreas"
			data-height="420">
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true" data-value-band="config_1_7" data-formatter="checkboxFormat"></th>
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
		<table id="itemsetslist" class="table-condensed"
			data-unique-id="id" data-value-band="config_1_6"
			data-url="./processesmanage.web?atn=getitemsets"
			data-toggle="itemsets"
			data-height="520">
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true" data-value-band="config_1_6" data-formatter="checkboxFormat"></th>
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
		<table id="datasetslist" class="table-condensed"
			data-unique-id="id" data-value-band="config_2_25"
			data-url="./processesmanage.web?atn=getdatasets"
			data-toggle="datasets"
			data-height="520">
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true" data-value-band="config_2_25" data-formatter="checkboxFormat"></th>
					<th data-field="id" data-filter-control="input"
						data-filter-control-placeholder="" data-width="50">编号</th>
						
					<th data-field="name" data-filter-control="input"
						data-formatter="nameFormat" data-filter-control-placeholder="">名称</th>
						
					<th data-field="recordcount" data-width="60">资料数</th>
						
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

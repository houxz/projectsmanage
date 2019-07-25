<%@page import="com.emg.poiwebeditor.common.ProcessType"%>
<%@page import="com.emg.poiwebeditor.common.ModelEnum"%>
<%@page import="com.emg.poiwebeditor.common.ProcessEditType"%>
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
//		 	单击单元格时
			onClickCell(field,value,row, $element){
				if(field != "progress")
					return;
				var processid = row.id;
				showTaskinfo(processid);
			},
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
	var priorityLevels = eval('(${priorityLevels})');
	


	
	
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

	function sysFormat(value, row, index) {
		return itemsetSysTypes[row.systype];
	}
	function itemsetTypesFormat(value, row, index) {
		return itemsetTypes[row.type];
	}
	function unitFormat(value, row, index) {
		return itemsetUnits[row.unit];
	}
	
	
// 	项目进度
	function progressFormat(value, row, index) {
		var values = value.split(',');
		var processType = row.type;
		
		var html = new Array();
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
// 		html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="错误修改进度：'
// 						+ parseFloat(values[1]).toFixed(3) + '&#8453;">');
// 		html.push('<div class="progress');
// 		if (values[1] > 0 && values[1] < 100 && row.state == 1)
// 			html.push(' progress-striped active');
// 		html.push('"style="margin-bottom: 3px;">');
// 		html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
// 						+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
// 						+ (parseFloat(values[1]).toFixed(3) > 100 ? 100 : parseFloat(values[1]).toFixed(3))
// 						+ '%;background-color: '
// 						+ colors[1]
// 						+ ';">'
// 						+ ' <span style="margin:0 6px;color: black;">'
// 						+ parseFloat(values[1]).toFixed(3) + '&#8453;</span>' + ' </div>');
// 		html.push('</div></div></div>');
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

	function changeState(state, processid) {
		if (state == 3) {
			$.webeditor.showConfirmBox("alert","确定要手动完成这个项目吗？", function(){
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
			});
		} else {
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
			} else if(row.type == 4){
				html.push('<div class="btn btn-default" style="margin-bottom:3px;" disabled onclick="changeState(1,' + row.id + ')" >开始</div>');
			} else {
				html.push('<div class="btn btn-default" style="margin-bottom:3px;" onclick="changeState(1,' + row.id + ')" >开始</div>');
			}
		} else if (row.state == 1) {
			html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="changeState(2,' + row.id + ')">暂停</div>');
			if (row.type == 5) {
			    html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="changeState(3,' + row.id + ')">完成</div>');
			}
		} else if (row.state == 2) {
			html.push('<div class="btn btn-default" style="margin-bottom:3px;" onclick="changeState(1,' + row.id + ')" >开始</div>');
			if (row.type == 5) {
			    html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="changeState(3,' + row.id + ')">完成</div>');
			}
		}

		return html.join('');
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
	}

	function getConfig(processid, processname, priority, processtype, state) {
		loadDefaultConfig();
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
						text : "提交",
						class : "btn btn-default",
						click : function() {
							var processid = $("#config_processid").val();
							var newProcessName = $("#config_processname").val();
							var priority = $("#config_processpriority").val();
// 							var protype = $("#config_processprotype").val();
							var protype = 10;
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
							//add by lianhr begin 2019/02/14
							var strbatch = $("#strbatch").val();
							var config_1_29 = $("#config_1_29").val();
							//add by lianhr end
							var config_2_30 = $("#config_2_30").val();
							
							var config_2_31 = $("#config_2_31").val();

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
								//add by lianhr begin 2019/02/14
								if(!strbatch || strbatch.length <= 0) {
									$.webeditor.showMsgLabel("alert", "请设定批次");
									return;
								}
								//add by lianhr edn
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
								if(!config_2_26 || config_2_26.lenth <= 0) {
									$.webeditor.showMsgLabel("alert", "请设定制作任务数");
									return;
								}
								var zhizuorenwushu = 0;
								try{
									zhizuorenwushu = parseInt(config_2_26);
								} catch(e) {
									$.webeditor.showMsgLabel("alert", "制作任务数无效值");
									return;
								}
								if (!zhizuorenwushu || zhizuorenwushu <= 0) {
									$.webeditor.showMsgLabel("alert", "制作任务数无效值");
									return;
								}
								break;
							case 6:
							case "6":
							case 10://byhxz 人工确认项目
							case "10":
								if( (!config_2_25 || config_2_25.length <=0) && processid == 0 ){
									$.webeditor.showMsgLabel("alert","请关联资料");
									return ;
								} 
								break;
							default:
								console.log("错误的项目类型：" + protype);
								break;
							}
							
							if(config_2_19 == 1 && (!config_2_18 || config_2_18.length <= 0)) {
								$.webeditor.showMsgLabel("alert", "私有项目需要添加人员");
								return;
							}
							
							if(protype == 5 && (!config_2_25 || config_2_25.lenth <= 0)) {
								$.webeditor.showConfirmBox("alert", "确定不绑定资料就配置项目吗？", function(){
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
								});
							} else {
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
									"config_2_26" : config_2_26,
									//add by lianhr begin 2019/02/14
									"strbatch" : strbatch,
									"config_1_29" : config_1_29,
									//add by lianhr end
									"config_2_30" : config_2_30,
									"config_2_31" : config_2_31
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
					text : "提交",
					class : "btn btn-default",
					click : function() {
						var selections = $(this).find("table").bootstrapTable("getAllSelections");
						var length = selections.length;
						if (length > 0) {
							var str = (function () {
								var d = {};
								selections.forEach(function (value, key, mapObj) {
								    	d[value.id] = value.sampling;
								});
				                return JSON.stringify(d);
				            })();
							$("#" + $(this).find("table").bootstrapTable("getOptions").valueBand).val(str);
							$("#" + $(this).find("table").bootstrapTable("getOptions").valueBand + " span").text(length);

							$(this).dialog("close");
						} else {
							$.webeditor.showMsgLabel("alert", "请确认已勾选");
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
		$("#checkersDlg").bootstrapDialog({}, {
			width : 480,
			title : "添加校正人员"
		});
	}
	
	function getDataset() {
		$("#datasetsDlg").bootstrapDialog({
			queryParams:function(params) {
				var datasetids =$("#config_2_25").val();
				params["datasetids"]= datasetids;
				return params;
			},
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
					text : "提交",
					class : "btn btn-default",
					click : function() {
						var selections = $(this).find("table").bootstrapTable("getAllSelections");
						var length = selections.length;
						var uniqueId = $(this).find("table").bootstrapTable("getOptions").uniqueId;
						if (length > 0) {
							var str = new String();
							var d = [];
							var types = new Array();
							selections.forEach(function (value, key, mapObj) {
							    d.push(value[uniqueId]);
							    if (types.indexOf(value["datatype"]) < 0) {
							    	types.push(value["datatype"]);
							    }
							});
							str = d.join(",");
							if (types.length > 1) {
								$.webeditor.showMsgLabel("alert", "请勿勾选多类型资料");
								return;
							}
							$("#" + $(this).find("table").bootstrapTable("getOptions").valueBand).val(str);
							$("#" + $(this).find("table").bootstrapTable("getOptions").valueBand + " span").text(length);

							$(this).dialog("close");
						} else {
							$.webeditor.showMsgLabel("alert", "请确认已勾选");
						}
					}
				},
				{
					text : "关闭",
					"class" : "btn btn-default",
					click : function() {
						$(this).dialog("close");
					}
				}
			]
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
				source = new EventSource('/poiwebeditor/sse.web?action=refreshprogress&ids=' + ids.join(","));

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
	
	function showTaskinfo(processid) {
		$("#taskinfoDlg").bootstrapDialog({
			queryParams:function(params) {
				params["processid"]= processid;
				return params;
			},
			onLoadSuccess : function(data) {
				$(this.self).bootstrapTable("load", data.rows);
			}
		}, {
			width : document.documentElement.clientWidth * 0.8,
			title : "未完成任务信息",
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
						$(this).find("table").bootstrapTable("nextPage");
					}
				},
				{
					text : "关闭",
					"class" : "btn btn-default",
					click : function() {
						$(this).dialog("close");
					}
				}
			]
		});
	}
	
	function taskState(value, row, index){
		var state = row.state;
		var process = row.process;
		if( state == 0 && process == 0){
			return "新任务";
		}else if(state == 1 && process == 5){
			return "制作中";
		}else if(state ==2 && process == 5){
			return "待质检";
		}else if(state ==5 && process == 5){
			return "制作跳过";
		} else if(state = 0 && process == 6){
			return "待改错";
		}else if(state == 1 && process == 6){
			return "改错中";
		}else if(state == 2 && process == 6){
			return "待质检";
		}else if(state == 0 && process == 7){
			return "抽检待作业";
		}
		else if(state == 1 && process == 7){
			return "抽检中";
		}else if(state ==2 && process == 7){
			return "待质检";
		}else{
			var ret = state;
			ret +=",";
			ret += process;
			return ret;
		}
	}
	
</script>

</head>
<body>
	<div class="container">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px">
			<table id="processeslist" data-unique-id="id"
				data-url="./processesmanage.web?atn=pages"
				data-side-pagination="server" data-filter-control="true"
				data-pagination="true" data-toggle="processes" data-height="714"
				data-page-list="[5, 10, 20, 100]" data-page-size="5"
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
						<th data-field="username" data-filter-control="input"
							data-filter-control-placeholder="" data-width="90">创建者</th>
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
							<option value="1">私有</option>
							<option value="0">公有</option>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">免校正</td>
					<td class="configValue"><select class="form-control" id="config_2_23">
							<option value="1">是</option>
							<option value="0">否</option>
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
				<td class="configKey">是否为可信数据源</td>
				<td class="configValue"><select class="form-control" id="config_2_31">
					<option value="1">是</option>
					<option value="0">否</option>
				</select></td>
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
	<div id="datasetsDlg" style="display: none;">
		<table id="datasetslist" class="table-condensed"
			data-unique-id="id" data-value-band="config_2_25"
			data-url="./processesmanage.web?atn=getdatasets"
			data-toggle="datasets"
			data-height="520">
			<thead>
				<tr>
					<th data-field="check" data-checkbox="true" data-value-band="config_2_25" data-formatter="checkboxFormat"></th>
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
	<div id="taskinfoDlg" style="display: none;">
		<table id="taskinfolist" class="table-condensed"
			data-unique-id="id2" 
			data-url="./processesmanage.web?atn=gettaskinfo"
			data-side-pagination="server"
			data-pagination="true"
		   	data-toggle="table"
			data-height="520" data-page-list="[10, 20, 100]" data-page-size="10"
			>
			<thead>
				<tr>
					<th data-field="id"  data-width="150">编号</th>
					<th data-field="taskstate" data-formatter = "taskState" data-width="160" >状态</th>
					<th data-field="editname"  data-width="160">制作人</th>
					<th data-field="checkname" data-width="160">抽检人</th>
					<th data-field="processid" data-width="160">项目编号</th>
				</tr>
			</thead>
		</table>
	</div>
</body>
</html>

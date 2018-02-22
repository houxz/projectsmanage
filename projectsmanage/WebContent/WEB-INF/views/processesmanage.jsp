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
			}
		});

	});

	var colors = [ "LimeGreen", "MediumSeaGreen", "MediumVioletRed", "Crimson", "Crimson" ];
	var processStates = eval('(${processStates})');
	var itemAreaTypes = eval('(${itemAreaTypes})');
	var priorityLevels = eval('(${priorityLevels})');
	var itemsetEnables = eval('(${itemsetEnables})');
	var itemsetSysTypes = eval('(${itemsetSysTypes})');
	var itemsetTypes = eval('(${itemsetTypes})');
	var itemsetUnits = eval('(${itemsetUnits})');

	function statesFormat(value, row, index) {
		return processStates[row.state];
	}
	function areaTypesFormat(value, row, index) {
		return itemAreaTypes[row.type];
	}
	
	function enableFormat(value, row, index) {
		return itemsetEnables[row.enable];
	}
	function sysFormat(value, row, index) {
		return itemsetSysTypes[row.systype];
	}
	function typeFormat(value, row, index) {
		return itemsetTypes[row.type];
	}
	function unitFormat(value, row, index) {
		return itemsetUnits[row.unit];
	}
	function enableFormat(value, row, index) {
		return itemsetEnables[row.enable];
	}

	function progressFormat(value, row, index) {
		var values = value.split(',');

		var html = new Array();
		html.push('<div>');
		html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="质检准备进度：'
						+ values[0] + '&#8453;">');
		html.push('<div class="progress');
		if (values[0] > 0 && values[0] < 100 && row.state == 1)
			html.push(' progress-striped active');
		html.push('"style="margin-bottom: 3px;">');
		html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
						+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: ' + values[0] + '%;background-color: ' + colors[0] + ';">'
						+ ' <span style="margin:0 6px;color: black;">' + values[0] + '&#8453;</span>' + ' </div>');
		html.push('</div></div>');
		html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="top" title="质检进度：' + values[1] + '&#8453;">');
		html.push('<div class="progress');
		if (values[1] > 0 && values[1] < 100 && row.state == 1)
			html.push(' progress-striped active');
		html.push('"style="margin-bottom: 3px;">');
		html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
						+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: ' + values[1] + '%;background-color: ' + colors[1] + ';">'
						+ ' <span style="margin:0 6px;color: black;">' + values[1] + '&#8453;</span>' + ' </div>');
		html.push('</div></div></div>');

		html.push('<div>');
		html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="bottom" title="改错准备进度：'
						+ values[2] + '&#8453;">');
		html.push('<div class="progress');
		if (values[2] > 0 && values[2] < 100 && row.state == 1)
			html.push(' progress-striped active');
		html.push('"style="margin-bottom: 3px;">');
		html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
						+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
						+ values[2]
						+ '%;background-color: '
						+ colors[2]
						+ ';">'
						+ ' <span style="margin:0 6px;color: black;">'
						+ values[2] + '&#8453;</span>' + ' </div>');
		html.push('</div></div>');

		html.push('<div style="width: 50%;float: left;" data-toggle="tooltip" data-placement="bottom" title="改错进度：'
						+ values[3] + '&#8453;">');
		html.push('<div class="progress');
		if (values[3] > 0 && values[3] < 100 && row.state == 1)
			html.push(' progress-striped active');
		html.push('"style="margin-bottom: 3px;">');
		html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
						+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
						+ values[3]
						+ '%;background-color: '
						+ colors[3]
						+ ';">'
						+ ' <span style="margin:0 6px;color: black;">'
						+ values[3] + '&#8453;</span>' + ' </div>');
		html.push('</div></div></div>');

		return html.join('');
	}

	function priFormat(value, row, index) {
		var html = [];
		html.push("<select name='priority_" + row.id + "' id='priority_" + row.id + "' onchange='changePriority(" + row.id + ")'  class='form-control'>");
		for ( var priorityLevel in priorityLevels) {
			if (priorityLevel == value) {
				html.push("<option value='"+ value +"' selected='selected' >" + priorityLevels[value] + "</option>");
			} else {
				html.push("<option value='"+ priorityLevel +"'>" + priorityLevels[priorityLevel] + "</option>");
			}
		}
		html.push("</select>");
		return html.join("");
	}

	function changeState(state, processid) {
		jQuery.post("./processesmanage.web", {
			"atn" : "changeState",
			"processid" : processid,
			"state" : state
		}, function(json) {
			$('[data-toggle="processes"]').bootstrapTable('refresh');
			$.webeditor.showMsgLabel("success","项目状态修改成功");
		}, "json");
	}

	function changePriority(processid) {
		var priority = $("#priority_" + processid).val();
		jQuery.post("./processesmanage.web", {
			"atn" : "changePriority",
			"processid" : processid,
			"priority" : priority
		}, function(json) {
			$('[data-toggle="processes"]').bootstrapTable('refresh');
			$.webeditor.showMsgLabel("success","项目优先级修改成功");
		}, "json");
	}

	function operationFormat(value, row, index) {
		var html = new Array();
		html .push('<button class="btn btn-default"  style="margin-bottom:3px;" onclick="getConfig(' + row.id + ',\'' + row.name + '\',' + row.priority + ');">配置</button>');
		if (row.state == 1) {
			html .push('<button class="btn btn-default"  style="margin-bottom:3px;" onclick="changeState(2,' + row.id + ')">暂停</button>');
		} else if (row.state == 2 || row.state == 0) {
			html .push('<button class="btn btn-default" style="margin-bottom:3px;" onclick="changeState(1,' + row.id + ')" >开始</button>');
		}

		return html.join('');
	}

	function projcetsIDFormat(value, row, index) {
		var html = new Array();
		html.push('<input type="checkbox" value="' + row.id + '">' + row.id + '');
		return html.join('');
	}

	function queryParams(params) {
		return params;
	}

	function loadDefaultConfig() {
		$("#config_0_1").val(new String());
		$("#config_0_2").val(new String());
		$("#config_0_3").val(0);

		$("#config_1_5").prop('selectedIndex', 0);
		$("#config_1_6").prop('selectedIndex', 0);
		$("#config_1_6").siblings("p").text("已选择0个质检图层");
		$("#config_1_7").val(new String());
		$("#config_1_7").siblings("p").text("已选择0个质检区域");
		$("#config_1_8").prop('selectedIndex', 0);

		$("#config_2_17").prop('selectedIndex', 0);
		$("#config_2_18").val(new String());
		$("#config_2_18").siblings("p").text("已添加人员0位");
		$("#config_2_19").prop('selectedIndex', 0);
	}

	function getConfig(processid, processname, priority) {
		loadDefaultConfig();
		if (processid > 0) {
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

					var config_1_3 = $("#config_1_3").val();
					var config_1_4 = $("#config_1_4").val();
					$("#config_1_4").siblings("p").text( "已关联项目:" + config_1_4 + "(" + config_1_3 + ")");
					
					var config_1_6 = $("#config_1_6").val();
					$("#config_1_6").siblings("p").text("已选择" + (config_1_6 ? config_1_6.split(",").length : 0) + "个质检图层");

					var config_1_7 = $("#config_1_7").val();
					$("#config_1_7").siblings("p").text("已选择" + (config_1_7 ? config_1_7.split(",").length : 0) + "个质检区域");

					var config_2_11 = $("#config_2_11").val();
					var config_2_12 = $("#config_2_12").val();
					$("#config_2_12").siblings("p").text("已关联项目:" + config_2_12 + "(" + config_2_11 + ")");

					var config_2_18 = $("#config_2_18").val();
					$("#config_2_18").siblings("p").text("已添加人员" + (config_2_18 ? config_2_18.split(",").length : 0) + "位");
				}
			}, "json");
		}
		showConfigDlg(processid, processname, priority);
	}

	function showConfigDlg(processid, processname, priority) {
		$("#configDlg").dialog(
			{
				modal : true,
				height : 600,
				width : document.documentElement.clientWidth * 0.4,
				title : "项目配置",
				open : function(event, ui) {
					$("#config_0_1").val(processid);
					$("#config_0_2").val(processname);
					$("#config_0_3").val(priority);
					$(".ui-dialog-titlebar-close").hide();
					$('.navbar-example').scrollspy({
						target : '.navbar-example'
					});
				},
				buttons : [
						{
							text : "提交",
							class : "btn btn-default",
							click : function() {
								var processid = $("#config_0_1").val();
								var newProcessName = $("#config_0_2").val();
								var priority = $("#config_0_3").val();
								var config_1_3 = $("#config_1_3").val();
								var config_1_4 = $("#config_1_4").val();
								var config_1_5 = $("#config_1_5").val();
								var config_1_6 = $("#config_1_6").val();
								var config_1_7 = $("#config_1_7").val();
								var config_1_8 = $("#config_1_8").val();

								var config_2_11 = $("#config_2_11").val();
								var config_2_12 = $("#config_2_12").val();
								var config_2_17 = $("#config_2_17").val();
								var config_2_18 = $("#config_2_18").val();
								var config_2_19 = $("#config_2_19").val();
								
								if(!newProcessName || newProcessName.length <= 0) {
									$.webeditor.showMsgLabel("alert","项目名不能为空");
									return;
								}

								jQuery.post(
									"./processesmanage.web",
									{
										"atn" : "newprocess",
										"processid" : processid,
										"newProcessName" : newProcessName,
										"priority" : priority,
										"config_1_3" : config_1_3,
										"config_1_4" : config_1_4,
										"config_1_5" : config_1_5,
										"config_1_6" : config_1_6,
										"config_1_7" : config_1_7,
										"config_1_8" : config_1_8,
										"config_2_11" : config_2_11,
										"config_2_12" : config_2_12,
										"config_2_17" : config_2_17,
										"config_2_18" : config_2_18,
										"config_2_19" : config_2_19
									},
									function(json) {
										if (json.result > 0) {
											$.webeditor.showMsgLabel("success","项目配置成功");
											$('[data-toggle="itemAreas"]').bootstrapTable("destroy");
											$("#configDlg").dialog("close");
											$('[data-toggle="processes"]').bootstrapTable('refresh');
										} else {
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
					var values = new Array();
					$.each($("#config_2_18").val().split(","), function(index, domEle) {
						values[index] = parseInt(domEle);
					});
					$('[data-toggle="workers"]').bootstrapTable("checkBy",
							{
								field : "id",
								values : values
							});
				}
			});

		showWorkersDlg();
	}

	function showWorkersDlg() {
		$("#workers").dialog(
			{
				modal : true,
				height : 500,
				width : document.documentElement.clientWidth * 0.3,
				title : "添加人员",
				open : function(event, ui) {
					$(".ui-dialog-titlebar-close").hide();
				},
				buttons : [
					{
						text : "提交",
						class : "btn btn-default",
						click : function() {
							var selections = $('[data-toggle="workers"]').bootstrapTable('getSelections');
							var length = selections.length;
							var value = new String();
							if (length > 0) {
								var subStr = "[";
								$.each(selections, function(index,domEle) {
									value += domEle.id + ",";
									subStr += '{"uid":' + domEle.id + ', "username":"' + domEle.realname + '"},';
								});
								value = value.substring(0, value.length - 1);
								subStr = subStr.substring(0, subStr.length - 1);
								subStr += ']';
								$("#config_2_18").val(value);
								$("#config_2_18").siblings("p").text("已添加人员" + length + "位");
								
								$('[data-toggle="workers"]').bootstrapTable("destroy");
								$(this).dialog("close");
							} else {
								$.webeditor.showMsgLabel("alert","请选择人员");
							}
							
						}
					},
					{
						text : "关闭",
						class : "btn btn-default",
						click : function() {
							$('[data-toggle="workers"]').bootstrapTable("destroy");
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
						return params;
					},
					onLoadSuccess : function(data) {
						var values = new Array();
						$.each($("#config_1_7").val().split(","), function(index, domEle) {
							values[index] = parseInt(domEle);
						});
						$('[data-toggle="itemAreas"]').bootstrapTable("checkBy", {
									field : "id",
									values : values
								});
					}
				});

		showItemAreasDlg();
	}

	function showItemAreasDlg() {
		$("#itemAreasDlg").dialog(
			{
				modal : true,
				height : 500,
				width : document.documentElement.clientWidth * 0.3,
				title : "质检区域配置",
				open : function(event, ui) {
					$(".ui-dialog-titlebar-close").hide();
				},
				buttons : [
					{
						text : "提交",
						class : "btn btn-default",
						click : function() {
							var selections = $('[data-toggle="itemAreas"]').bootstrapTable('getSelections');
							var length = selections.length;
							var value = new String();
							if (length > 0) {
								$.each(selections, function(index,domEle) {
									value += domEle.id + ",";
								});
								value = value.substring(0,value.length - 1);
								$("#config_1_7").val(value);
								$("#config_1_7").siblings("p").text("已选择" + length + "个质检区域");
								
								$('[data-toggle="itemAreas"]').bootstrapTable("destroy");
								$(this).dialog("close");
							} else {
								$.webeditor.showMsgLabel("alert","请选择质检区域");
							}
						}
					},
					{
						text : "关闭",
						class : "btn btn-default",
						click : function() {
							$('[data-toggle="itemAreas"]').bootstrapTable("destroy");
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
						return params;
					},
					onLoadSuccess : function(data) {
						var values = new Array();
						$.each($("#config_1_6").val().split(","), function(index, domEle) {
							values[index] = parseInt(domEle);
						});
						$('[data-toggle="itemsets"]').bootstrapTable("checkBy", {
									field : "id",
									values : values
								});
					}
				});

		showItemsetsDlg();
	}
	
	function showItemsetsDlg() {
		$("#itemsetsDlg").dialog(
			{
				modal : true,
				height : 600,
				width : document.documentElement.clientWidth * 0.6,
				title : "质检图层配置",
				open : function(event, ui) {
					$(".ui-dialog-titlebar-close").hide();
				},
				buttons : [
					{
						text : "提交",
						class : "btn btn-default",
						click : function() {
							var selections = $('[data-toggle="itemsets"]').bootstrapTable('getSelections');
							var length = selections.length;
							var value = new String();
							if (length > 0) {
								$.each(selections, function(index,domEle) {
									value += domEle.id + ",";
								});
								value = value.substring(0,value.length - 1);
								$("#config_1_6").val(value);
								$("#config_1_6").siblings("p").text("已选择" + length + "个质检图层");
								
								$('[data-toggle="itemsets"]').bootstrapTable("destroy");
								$(this).dialog("close");
							} else {
								$.webeditor.showMsgLabel("alert","请选择质检区域");
							}
						}
					},
					{
						text : "关闭",
						class : "btn btn-default",
						click : function() {
							$('[data-toggle="itemsets"]').bootstrapTable("destroy");
							$(this).dialog("close");
						}
					} ]
			});
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
							<button class="btn btn-default btn-xs" title="新建项目"
								onclick="getConfig(0,'',0);">
								<span class="glyphicon glyphicon-plus"></span>
							</button>
						</th>
						<th data-field="name" data-filter-control="input"
							data-filter-control-placeholder="" data-width="120">项目名称</th>
						<th data-field="username" data-filter-control="input"
							data-filter-control-placeholder="" data-width="120">创建者</th>
						<th data-field="priority" data-formatter="priFormat"
							data-filter-control="select" data-width="120"
							data-filter-data="var:priorityLevels">优先级</th>
						<th data-field="state" data-formatter="statesFormat"
							data-filter-control="select" data-width="100"
							data-filter-data="var:processStates">项目状态</th>
						<th data-field="progress" data-formatter="progressFormat"
							data-width="500">项目进度</th>
						<th data-field="createtime" data-filter-control-placeholder=""
							data-width="200">创建时间</th>
						<th data-formatter="operationFormat" data-width="70">操作</th>
					</tr>
				</thead>
			</table>
		</div>
		<div id="footdiv"></div>
	</div>
	<div id="configDlg" style="display: none;">
		<div id="navbar-example" style="width: 20%; float: left;">
			<ul class="nav nav-pills nav-stacked">
				<li class="active"><a href="#sc1">基础配置</a></li>
				<li><a href="#sc2">质检配置</a></li>
				<li><a href="#sc3">改错配置</a></li>
			</ul>
		</div>
		<div class="navbar-example"
			style="width: 79%; height: 472px; float: left; overflow-y: auto;"
			data-spy="scroll" data-target="#navbar-example">
			<div class="panel panel-default" id="sc1">
				<div class="panel-heading">基础配置</div>
				<table class="table">
					<tbody>
						<tr>
							<td class="configKey">项目编号</td>
							<td class="configValue"><input type="text"
								class="form-control configValue" id="config_0_1" disabled>
							</td>
						</tr>
						<tr>
							<td class="configKey">项目名称</td>
							<td class="configValue"><input type="text"
								class="form-control configValue" id="config_0_2"
								placeholder="请输入新项目名"></td>
						</tr>
						<tr>
							<td class="configKey">项目优先级</td>
							<td class="configValue"><select
								class="form-control configValue" id="config_0_3">
									<option value="-2">极低</option>
									<option value="-1">低</option>
									<option value="0" selected="selected">一般</option>
									<option value="1">高</option>
									<option value="2">极高</option>
							</select></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="panel panel-default" id="sc2">
				<div class="panel-heading">质检配置</div>
				<table class="table">
					<tr style="display: none;">
						<td class="configKey">项目关联</td>
						<td class="configValue"><input type="hidden" id="config_1_3"
							value=""> <input type="hidden" id="config_1_4" value="">
							<p class="help-block">已关联项目&lceil;&rfloor;</p></td>
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
						<td class="configKey">质检区域</td>
						<td class="configValue"><input type="hidden" id="config_1_7"
							value="">
							<button type="button" class="btn btn-default"
								onclick="getItemAreas();">配置质检区域</button>
							<p class="help-block">已选择0个质检区域</p></td>
					</tr>
					<tr>
						<td class="configKey">质检图层</td>
						<!-- <td class="configValue"><select class="form-control"
							id="config_1_6">
								<option value="1" selected="selected">POI</option>
								<option value="2">Road</option>
								<option value="3">背景</option>
						</select></td> -->
						<td class="configValue"><input type="hidden" id="config_1_6"
							value="">
							<button type="button" class="btn btn-default"
								onclick="getItemsets();">配置质检图层</button>
							<p class="help-block">已选择0个质检图层</p></td>
					</tr>
					<tr>
						<td class="configKey">启动类型</td>
						<td class="configValue"><select class="form-control"
							id="config_1_8">
								<option value="1">手动</option>
								<option value="2" selected="selected">自动</option>
								<option value="3">自动延迟</option>
						</select></td>
					</tr>
				</table>
			</div>
			<div class="panel panel-default" id="sc3">
				<div class="panel-heading">改错配置</div>
				<table class="table">
					<tr style="display: none;">
						<td class="configKey">项目关联</td>
						<td class="configValue"><input type="hidden" id="config_2_11"
							value=""> <input type="hidden" id="config_2_12" value="">
							<p class="help-block">已关联项目&lceil;&rfloor;</p></td>
					</tr>
					<tr>
						<td class="configKey">启动类型</td>
						<td class="configValue"><select class="form-control"
							id="config_2_17">
								<option value="1">手动</option>
								<option value="2" selected="selected">自动</option>
								<option value="3">自动延迟</option>
						</select></td>
					</tr>
					<tr>
						<td class="configKey">改错人员</td>
						<td class="configValue"><input type="hidden" id="config_2_18"
							value="">
							<button type="button" class="btn btn-default"
								onclick="getWorkers();">添加人员</button>
							<p class="help-block">已添加人员0位</p></td>
					</tr>
					<tr>
						<td class="configKey">公有私有</td>
						<td class="configValue"><select class="form-control"
							id="config_2_19">
								<option value="0">公有</option>
								<option value="1">私有</option>
						</select></td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	<div id="workers" style="display: none;">
		<table id="workerlist" class="table-condensed" data-unique-id="id"
			data-url="./processesmanage.web?atn=getworkers" data-cache="false"
			data-side-pagination="server" data-filter-control="true"
			data-click-to-select="true" data-single-select="false"
			data-select-item-name="checkboxName" data-pagination="false"
			data-toggle="workers" data-height="374"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true"></th>
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
			data-toggle="itemAreas" data-height="374"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true"></th>
					<th data-field="id" data-filter-control="input"
						data-filter-control-placeholder="" data-width="20">区域编号</th>
					<th data-field="type" data-filter-control="select"
						data-formatter="areaTypesFormat"
						data-filter-data="var:itemAreaTypes"
						data-filter-control-placeholder="" data-width="20">区域级别</th>
					<th data-field="province" data-filter-control="input"
						data-filter-control-placeholder="" data-width="120">省</th>
					<th data-field="city" data-filter-control="input"
						data-filter-control-placeholder="" data-width="120">市</th>
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
			data-toggle="itemsets" data-height="474"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true"></th>
					<th data-field="id" data-filter-control="input"
						data-filter-control-placeholder="" data-width="20">编号</th>
					<th data-field="name" data-filter-control="input"
						data-filter-control-placeholder="">图层集合名称</th>
					<th data-field="layername" data-filter-control="input"
						data-filter-control-placeholder="">图层</th>
					<!-- <th data-field="type" data-formatter="typeFormat"
						data-filter-control="select" data-filter-data="var:itemsetTypes">类型</th> -->
					<!-- <th data-field="enable" data-formatter="enableFormat"
						data-filter-control="select" data-filter-data="var:itemsetEnables">状态</th> -->
					<!-- <th data-field="systype" data-formatter="sysFormat"
						data-filter-control="select"
						data-filter-data="var:itemsetSysTypes">操作系统</th> -->
					<th data-field="referdata" data-filter-control="input"
						data-filter-control-placeholder="" data-width="200">参考图层</th>
					<!-- <th data-field="unit" data-formatter="unitFormat"
						data-filter-control="select" data-filter-data="var:itemsetUnits">质检单位</th> -->
					<th data-field="desc" data-filter-control="input"
						data-filter-control-placeholder="">描述</th>
				</tr>
			</thead>
		</table>
	</div>
</body>
</html>

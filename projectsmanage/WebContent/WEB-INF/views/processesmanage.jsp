<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<title>流程管理</title>
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
			locale : 'zh-CN'
		});

	});

	var colors = [ "#f0ad4e", "#5bc0de", "cornflowerblue", "#5cb85c", "#d9534f" ];
	var processStates = eval('(${processStates})');
	var itemAreaTypes = eval('(${itemAreaTypes})');

	function statesFormat(value, row, index) {
		return processStates[row.state];
	}
	function areaTypesFormat(value, row, index) {
		return itemAreaTypes[row.type];
	}

	function progressFormat(value, row, index) {
		var values = value.split(',');
		var html = new Array();
		for ( var i = 0; i < values.length; i++) {
			html.push('<div>');
			var v = values[i];
			if (v > 0 && v < 100 && row.state == 1)
				html
						.push('<div class="progress progress-striped active" style="margin-bottom: 3px;">');
			else
				html.push('<div class="progress" style="margin-bottom: 3px;">');
			html
					.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: '
							+ v
							+ '%;background-color: '
							+ colors[i]
							+ ';">'
							+ ' <span style="margin:0 6px;color: black;">'
							+ v
							+ '&#8453;</span>' + ' </div>');
			html.push('</div></div>');
		}

		return html.join('');
	}

	function changeState(state, processid) {

	}

	function operationFormat(value, row, index) {
		var html = new Array();
		html.push('<button class="btn btn-default"  style="margin-bottom:3px;" onclick="getConfig('
						+ row.id + ',\'' + row.name + '\');">配置</button>');
		if (row.state == 1) {
			html.push('<button class="btn btn-default"  style="margin-bottom:3px;" onclick="changeState(2,'
							+ row.id + ')">暂停</button>');
		} else if (row.state == 2 || row.state == 0) {
			html.push('<button class="btn btn-default" style="margin-bottom:3px;" onclick="changeState(1,'
							+ row.id + ')" >开始</button>');
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
	
	function loadDefaultConfig() {
		$("#config_0_1").val(new String());
		$("#config_0_2").val(new String());
		$('#config_1_1').prop('selectedIndex', 0);
		$("#config_1_2").prop('selectedIndex', 0);
		$("#config_1_4_rds input:radio:last").attr("checked", false);
		$("#config_1_4_rds input:radio:first").attr("checked", true);
		$("#projectName332").text(new String());
		$("#projectID332").text(new String());
		$("#config_1_5").prop('selectedIndex', 0);
		$("#config_1_6").val(new String());
		$("#config_1_6").siblings("p").text("已选择0个质检图层");
		$("#config_1_7").val(new String());
		$("#config_1_7").siblings("p").text("已选择0个质检区域");
		$("#config_1_8").prop('selectedIndex', 0);
		$("#config_2_9").prop('selectedIndex', 0);
		$("#config_2_10").prop('selectedIndex', 0);
		$("#config_2_11_rds input:radio:last").attr("checked", false);
		$("#config_2_11_rds input:radio:first").attr("checked", true);
		$("#projectName349").text(new String());
		$("#projectID349").text(new String());
		$("#config_2_12").prop('selectedIndex', 0);
		$("#config_2_13").val(100);
		$("#config_2_14").val(10000);
		$("#config_2_15").prop('selectedIndex', 0);
		$("#config_1_16").val(new String());
		$("#config_2_17").val(new String());
		$("#config_2_18").prop('selectedIndex', 0);
	}

	function getConfig(processid, processname) {
		loadDefaultConfig();
		if(processid > 0) {
			jQuery.post("./processesmanage.web",
	            {
					"atn" : "getconfigvalues", 
					"processid" : processid
	            },function(json) {
	            	if(json.configValues && json.configValues.length > 0) {
	            		var configValues = json.configValues;
	            		for(var index in configValues) {
	            			$("#" + "config_" + configValues[index].moduleid + "_" + configValues[index].configid).val(configValues[index].value);
	            			if(configValues[index].moduleid == 1 && configValues[index].configid == 16 && configValues[index].value) {
	            				$("#config_1_4_rds input:radio:eq(1)").attr("checked", true);
	            				$("#projectID332").text(configValues[index].value);
	            			}
	            			if(configValues[index].moduleid == 1 && configValues[index].configid == 4 && configValues[index].value) {
	            				$("#projectName332").text(configValues[index].value);
	            			}
	            			if(configValues[index].moduleid == 2 && configValues[index].configid == 17 && configValues[index].value) {
	            				$("#config_2_11_rds input:radio:eq(1)").attr("checked", true);
	            				$("#projectID349").text(configValues[index].value);
	            			}
	            			if(configValues[index].moduleid == 2 && configValues[index].configid == 11 && configValues[index].value) {
	            				$("#projectName349").text(configValues[index].value);
	            			}
	            			if(configValues[index].moduleid == 1 && configValues[index].configid == 6 && configValues[index].value) {
	            				var items = configValues[index].value.split(",");
	            				$("#config_1_6").siblings("p").text("已选择" + items.length + "个质检图层");
	            			}
	            			if(configValues[index].moduleid == 1 && configValues[index].configid == 7 && configValues[index].value) {
	            				var items = configValues[index].value.split(",");
	            				$("#config_1_7").siblings("p").text("已选择" + items.length + "个质检区域");
	            			}
	            		}
	            	}
	            }, "json");
		}
		showConfigDlg(processid, processname);
	}

	function showConfigDlg(processid, processname) {
		$("#configDlg").dialog({
			modal : true,
			height : 600,
			width : document.documentElement.clientWidth * 0.4,
			title : "流程配置",
			open : function(event, ui) {
				$("#config_0_1").val(processid);
				$("#config_0_2").val(processname);
				$(".ui-dialog-titlebar-close").hide();
				$('.navbar-example').scrollspy({
					target : '.navbar-example'
				});
			},
			buttons : [ {
				text : "提交",
				class : "btn btn-default",
				click : function() {
					var newProcessName = $("#config_0_2").val();
					var config_1_1 = $("#config_1_1").val();
					var config_1_2 = $("#config_1_2").val();
					var newproject332 = $("#config_1_4_rds input:radio:checked").val() == 0;
					var config_1_4 = newproject332 ? $("#newproname332").val() : $("#projectName332").text();
					var config_1_5 = $("#config_1_5").val();
					var config_1_6 = $("#config_1_6").val();
					var config_1_7 = $("#config_1_7").val();
					var config_1_8 = $("#config_1_8").val();
					var config_2_9 = $("#config_2_9").val();
					var config_2_10 = $("#config_2_10").val();
					var newproject349 = $("#config_2_11_rds input:radio:checked").val() == 0;
					var config_2_11 = newproject349 ? $("#newproname349").val() : $("#projectName349").text();
					var config_2_12 = $("#config_2_12").val();
					var config_2_13 = $("#config_2_13").val();
					var config_2_14 = $("#config_2_14").val();
					var config_2_15 = $("#config_2_15").val();
					var config_1_16 = newproject332 ? 0 : $("#projectID332").text();
					var config_2_17 = newproject349 ? 0 : $("#projectID349").text();
					var config_2_18 = $("#config_2_18").val();
					
					jQuery.post("./processesmanage.web",
	                    {
							"atn" : "newprocess",
							"processid" : processid,
							"newProcessName" : newProcessName,
							"config_1_1" : config_1_1,
							"config_1_2" : config_1_2,
							"config_1_4" : config_1_4,
							"config_1_5" : config_1_5,
							"config_1_6" : config_1_6,
							"config_1_7" : config_1_7,
							"config_1_8" : config_1_8,
							"config_2_9" : config_2_9,
							"config_2_10" : config_2_10,
							"config_2_11" : config_2_11,
							"config_2_12" : config_2_12,
							"config_2_13" : config_2_13,
							"config_2_14" : config_2_14,
							"config_2_15" : config_2_15,
							"config_1_16" : config_1_16,
							"config_2_17" : config_2_17,
							"config_2_18" : config_2_18
	                    },function(json) {
	                    	if (json.result > 0) {
	                            $.webeditor.showMsgLabel("success", "新建流程成功");
	                            $('[data-toggle="projects332"]').bootstrapTable("destroy");
	                            $('[data-toggle="projects349"]').bootstrapTable("destroy");
	                            $('[data-toggle="itemSets"]').bootstrapTable("destroy");
	                            $('[data-toggle="itemAreas"]').bootstrapTable("destroy");
	                            $("#configDlg").dialog("close");
	                        } else {
	                            $.webeditor.showMsgLabel("alert", "新建流程失败");
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

	function getProjects332() {
		$('[data-toggle="projects332"]').bootstrapTable({
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
				params["configDBid"] = $("#config_1_1").val();
				params["systemid"] = 332;
				return params;
			},
			onLoadSuccess : function(data) {
				var values = new Array(1);
				values[0] = parseInt($("#config_1_16").val());
				$('[data-toggle="projects332"]').bootstrapTable("checkBy", {field: "id", values: values});
			}
		});
		showProjectsDlg332();
	}

	function showProjectsDlg332() {
		$("#projectsDlg332").dialog(
			{
				modal : true,
				height : 500,
				width : document.documentElement.clientWidth * 0.3,
				title : "项目关联",
				open : function(event, ui) {
					$(".ui-dialog-titlebar-close").hide();
				},
				buttons : [
						{
							text : "确定",
							class : "btn btn-default",
							click : function() {
								var selections = $('[data-toggle="projects332"]').bootstrapTable('getAllSelections');
								if (selections.length > 0) {
									var projectID = selections[0].id;
									var projectName = selections[0].name;
									$("#config_1_4").val(projectName);
									$("#config_1_16").val(projectID);
									$("#projectName332").text(projectName);
									$("#projectID332").text(projectID);
								}
								$('[data-toggle="projects332"]').bootstrapTable("destroy");
								$(this).dialog("close");
							}
						}, {
							text : "关闭",
							class : "btn btn-default",
							click : function() {
								$('[data-toggle="projects332"]').bootstrapTable("destroy");
								$(this).dialog("close");
							}
						} ]
			});
	}
	
	function getProjects349() {
		$('[data-toggle="projects349"]').bootstrapTable({
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
				params["configDBid"] = $("#config_2_9").val();
				params["systemid"] = 349;
				return params;
			},
			onLoadSuccess : function(data) {
				var values = new Array(1);
				values[0] = parseInt($("#config_2_17").val());
				$('[data-toggle="projects349"]').bootstrapTable("checkBy", {field: "id", values: values});
			}
		});
		showProjectsDlg349();
	}

	function showProjectsDlg349() {
		$("#projectsDlg349").dialog(
			{
				modal : true,
				height : 500,
				width : document.documentElement.clientWidth * 0.3,
				title : "项目关联",
				open : function(event, ui) {
					$(".ui-dialog-titlebar-close").hide();
				},
				buttons : [
						{
							text : "确定",
							class : "btn btn-default",
							click : function() {
								var selections = $('[data-toggle="projects349"]').bootstrapTable('getAllSelections');
								if (selections.length > 0) {
									var projectID = selections[0].id;
									var projectName = selections[0].name;
									$("#config_2_11").val(projectName);
									$("#config_2_17").val(projectID);
									$("#projectName349").text(projectName);
									$("#projectID349").text(projectID);
								}
								$('[data-toggle="projects349"]').bootstrapTable("destroy");
								$(this).dialog("close");
							}
						}, {
							text : "关闭",
							class : "btn btn-default",
							click : function() {
								$('[data-toggle="projects349"]').bootstrapTable("destroy");
								$(this).dialog("close");
							}
						} ]
			});
	}

	function getItemSets() {
		$('[data-toggle="itemSets"]').bootstrapTable({
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
				params["configDBid"] = $("#config_1_2").val();
				return params;
			},
			onLoadSuccess : function(data) {
				var values = new Array();
				$.each($("#config_1_6").val().split(","), function (index, domEle) {
					values[index] = parseInt(domEle);
				});
				$('[data-toggle="itemSets"]').bootstrapTable("checkBy", {field: "id", values: values});
			}
		});
		
		showItemSetsDlg();
	}

	function showItemSetsDlg() {
		$("#itemSetsDlg").dialog(
			{
				modal : true,
				height : 500,
				width : document.documentElement.clientWidth * 0.6,
				title : "质检图层配置",
				open : function(event, ui) {
					$(".ui-dialog-titlebar-close").hide();
				},
				buttons : [
						{
							text : "确定",
							class : "btn btn-default",
							click : function() {
								var selections = $('[data-toggle="itemSets"]').bootstrapTable('getAllSelections');
								var length = selections.length;
								var value = new String();
								if (length > 0) {
									$.each(selections, function(
											index, domEle) {
										value += domEle.id + ",";
									});
									value = value.substring(0,value.length-1);
									$("#config_1_6").val(value);
									$("#config_1_6").siblings("p").text("已选择" + length + "个质检图层");
								}
								$('[data-toggle="itemSets"]').bootstrapTable("destroy");
								$(this).dialog("close");
							}
						}, {
							text : "关闭",
							class : "btn btn-default",
							click : function() {
								$('[data-toggle="itemSets"]').bootstrapTable("destroy");
								$(this).dialog("close");
							}
						} ]
			});
	}
	
	function getItemAreas() {
		$('[data-toggle="itemAreas"]').bootstrapTable({
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
				params["configDBid"] = $("#config_1_2").val();
				return params;
			},
			onLoadSuccess : function(data) {
				var values = new Array();
				$.each($("#config_1_7").val().split(","), function (index, domEle) {
					values[index] = parseInt(domEle);
				});
				$('[data-toggle="itemAreas"]').bootstrapTable("checkBy", {field: "id", values: values});
			}
		});
		
		showItemAreasDlg();
	}
	
	function showItemAreasDlg() {
		$("#itemAreasDlg").dialog({
			modal : true,
			height : 500,
			width : document.documentElement.clientWidth * 0.3,
			title : "质检区域配置",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			buttons : [ {
				text : "提交",
				class : "btn btn-default",
				click : function() {
					var selections = $('[data-toggle="itemAreas"]').bootstrapTable('getAllSelections');
					var length = selections.length;
					var value = new String();
					if (length > 0) {
						$.each(selections, function(index, domEle) {
							value += domEle.id + ",";
						});
						value = value.substring(0,value.length-1);
						$("#config_1_7").val(value);
						$("#config_1_7").siblings("p").text("已选择" + length + "个质检区域");
					}
					$('[data-toggle="itemAreas"]').bootstrapTable("destroy");
					$(this).dialog("close");
				}
			}, {
				text : "关闭",
				class : "btn btn-default",
				click : function() {
					$('[data-toggle="itemAreas"]').bootstrapTable("destroy");
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
							data-filter-control-placeholder="" data-width="120">流程编号
							<button class="btn btn-default btn-xs" title="新建流程"
								onclick="getConfig(0,'');">
								<span class="glyphicon glyphicon-plus"></span>
							</button>
						</th>
						<th data-field="name" data-filter-control="input"
							data-filter-control-placeholder="" data-width="120">流程名称</th>
						<th data-field="username" data-filter-control="input"
							data-filter-control-placeholder="" data-width="120">创建者</th>
						<th data-field="state" data-formatter="statesFormat"
							data-filter-control="select" data-width="100"
							data-filter-data="var:processStates">流程状态</th>
						<th data-field="progress" data-formatter="progressFormat"
							data-width="600">流程进度</th>
						<th data-field="createtime" data-filter-control="input"
							data-filter-control-placeholder="" data-width="240">创建时间</th>
						<th data-formatter="operationFormat" data-width="70">操作</th>
					</tr>
				</thead>
			</table>
		</div>
		<div id="footdiv"></div>
	</div>
	<div id="configDlg" style="display: none;">
		<div id="navbar-example" style="width: 16%; float: left;">
			<ul class="nav nav-pills nav-stacked">
				<li class="active"><a href="#sc0">基础配置</a></li>
				<li><a href="#sc1">质检配置</a></li>
				<li><a href="#sc2">改错配置</a></li>
			</ul>
		</div>
		<div class="navbar-example"
			style="width: 83%; height: 472px; float: left; overflow-y: auto;"
			data-spy="scroll" data-target="#navbar-example">
			<div class="panel panel-default" id="sc0">
				<div class="panel-heading">基础配置</div>
				<table class="table">
					<tr>
						<td>流程编号</td>
						<td><input type="text" class="form-control" id="config_0_1"></td>
					</tr>
					<tr>
						<td>流程名称</td>
						<td><input type="text" class="form-control" id="config_0_2"
							placeholder="请输入新流程名"></td>
					</tr>
				</table>
			</div>
			<div class="panel panel-default" id="sc1">
				<div class="panel-heading">质检配置</div>
				<table class="table">
					<tr>
						<td>项目库</td>
						<td><select class="form-control" id="config_1_1">
								<c:forEach items="${configDBModels }" var="configDBModel">
									<c:if
										test="${configDBModel['connname'].equals('projectmanager') }">
										<option value="${configDBModel['id']}">${configDBModel['dbname']}(${configDBModel['ip']})</option>
									</c:if>
								</c:forEach>
						</select></td>
					</tr>
					<tr>
						<td>任务库</td>
						<td><select class="form-control" id="config_1_2">
								<c:forEach items="${configDBModels }" var="configDBModel">
									<c:if test="${configDBModel['connname'].equals('task') }">
										<option value="${configDBModel['id']}">${configDBModel['dbname']}(${configDBModel['ip']})</option>
									</c:if>
								</c:forEach>
						</select></td>
					</tr>
					<tr>
						<td>项目id</td>
						<td><input type="text" class="form-control" id="config_1_16" value="">
					</tr>
					<tr>
						<td>关联项目</td>
						<td>
							<input id="config_1_4" type="hidden" value="">
							<div id="config_1_4_rds">
								<div class="radio">
									<label> <input type="radio" name="radios1" value="0">新建项目<input id="newproname332" type="text" class="form-control"
										placeholder="请输入新项目名"</label>
								</div>
								<div class="radio">
									<label> <input type="radio" name="radios1" value="1"
										onclick="getProjects332();">选择已有项目
										<p class="help-block">已选择项目: <span id="projectName332"></span>(<span id="projectID332"></span>)</p>
									</label>
								</div>
							</div>
						</td>
					</tr>
					<tr>
						<td>质检集合</td>
						<td><select class="form-control" id="config_1_5">
								<option value="1">九宫格</option>
								<option value="2">全国</option>
								<option value="3">市</option>
						</select></td>
					</tr>
					<tr>
						<td>质检图层</td>
						<td><input type="hidden" id="config_1_6" value="">
						<button type="button" class="btn btn-default"
								onclick="getItemSets();">配置质检图层</button>
							<p class="help-block">已选择0个质检图层</p></td>
					</tr>
					<tr>
						<td>质检区域</td>
						<td><input type="hidden" id="config_1_7" value="">
						<button type="button" class="btn btn-default"
								onclick="getItemAreas();">配置质检区域</button>
							<p class="help-block">已选择0个质检区域</p></td>
					</tr>
					<tr>
						<td>启动类型</td>
						<td><select class="form-control" id="config_1_8">
								<option value="1">手动</option>
								<option value="2" selected="selected">自动</option>
								<option value="3">自动延迟</option>
						</select></td>
					</tr>
				</table>
			</div>
			<div class="panel panel-default" id="sc2">
				<div class="panel-heading">改错配置</div>
				<table class="table">
					<tr>
						<td>项目库</td>
						<td><select class="form-control" id="config_2_9">
								<c:forEach items="${configDBModels }" var="configDBModel">
									<c:if
										test="${configDBModel['connname'].equals('projectmanager') }">
										<option value="${configDBModel['id']}">${configDBModel['dbname']}(${configDBModel['ip']})</option>
									</c:if>
								</c:forEach>
						</select></td>
					</tr>
					<tr>
						<td>任务库</td>
						<td><select class="form-control" id="config_2_10">
								<c:forEach items="${configDBModels }" var="configDBModel">
									<c:if test="${configDBModel['connname'].equals('task') }">
										<option value="${configDBModel['id']}">${configDBModel['dbname']}(${configDBModel['ip']})</option>
									</c:if>
								</c:forEach>
						</select></td>
					</tr>
					<tr>
						<td>项目id</td>
						<td><input type="text" class="form-control" id="config_2_17" value="">
					</tr>
					<tr>
						<td>关联项目</td>
						<td>
							<input id="config_2_11" type="hidden" value="">
							<div id="config_2_11_rds">
								<div class="radio">
									<label> <input type="radio" name="radios2" value="0">新建项目<input type="text" id="newproname349" class="form-control"
										placeholder="请输入新项目名">
									</label>

								</div>
								<div class="radio">
									<label> <input type="radio" name="radios2" value="1"
										onclick="getProjects349();">选择已有项目
										<p class="help-block">已选择项目: <span id="projectName349"></span>(<span id="projectID349"></span>)</p>
									</label>
								</div>
							</div>
						</td>
					</tr>
					<tr>
						<td>任务类型</td>
						<td><select class="form-control" id="config_2_12">
								<option value="1">POI改错任务</option>
								<option value="2">背景改错</option>
								<option value="3">道路改错</option>
								<option value="4">附属表继承改错</option>
								<option value="5">附属表继承批量创建</option>
								<option value="6">批量创建</option>
						</select></td>
					</tr>
					<tr>
						<td>错误个数</td>
						<td><input type="text" class="form-control" id="config_2_13"
							value="100"></td>
					</tr>
					<tr>
						<td>错误距离</td>
						<td><input type="text" class="form-control" id="config_2_14"
							value="10000"></td>
					</tr>
					<tr>
						<td>错误库</td>
						<td><select class="form-control" id="config_2_15">
								<c:forEach items="${configDBModels }" var="configDBModel">
									<c:if test="${configDBModel['connname'].equals('error') }">
										<option value="${configDBModel['id']}">${configDBModel['dbname']}(${configDBModel['ip']})</option>
									</c:if>
								</c:forEach>
						</select></td>
					</tr>
					<tr>
						<td>启动类型</td>
						<td><select class="form-control" id="config_2_18">
								<option value="1">手动</option>
								<option value="2" selected="selected">自动</option>
								<option value="3">自动延迟</option>
						</select></td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	<div id="projectsDlg332" style="display: none;">
		<table id="projectslist" class="table-condensed" data-unique-id="id"
			data-url="./processesmanage.web?atn=getprojects" data-cache="false"
			data-side-pagination="server" data-filter-control="true"
			data-click-to-select="true" data-single-select="true"
			data-select-item-name="radioName" data-pagination="false"
			data-toggle="projects332" data-height="374"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-radio="true"></th>
					<th data-field="id" data-filter-control="input"
						data-filter-control-placeholder="" data-width="20">项目编号</th>
					<th data-field="name" data-filter-control="input"
						data-filter-control-placeholder="" data-width="120">项目名称</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="projectsDlg349" style="display: none;">
		<table id="projectslist" class="table-condensed" data-unique-id="id"
			data-url="./processesmanage.web?atn=getprojects" data-cache="false"
			data-side-pagination="server" data-filter-control="true"
			data-click-to-select="true" data-single-select="true"
			data-select-item-name="radioName" data-pagination="false"
			data-toggle="projects349" data-height="374"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-radio="true"></th>
					<th data-field="id" data-filter-control="input"
						data-filter-control-placeholder="" data-width="20">项目编号</th>
					<th data-field="name" data-filter-control="input"
						data-filter-control-placeholder="" data-width="120">项目名称</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="itemSetsDlg" style="display: none;">
		<table id="itemSetslist" class="table-condensed" data-unique-id="id"
			data-url="./processesmanage.web?atn=getitemsets" data-cache="false"
			data-side-pagination="server" data-filter-control="true"
			data-click-to-select="true" data-single-select="false"
			data-select-item-name="checkboxName" data-pagination="false"
			data-toggle="itemSets" data-height="374"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true"></th>
					<th data-field="id" data-filter-control="input"
						data-filter-control-placeholder="" data-width="20">项目编号</th>
					<th data-field="layername" data-filter-control="input"
						data-filter-control-placeholder="" data-width="240">图层名称</th>
					<th data-field="systype" data-filter-control="input"
						data-filter-control-placeholder="" data-width="20">操作系统</th>
					<th data-field="referdata" data-filter-control="input"
						data-filter-control-placeholder="" data-width="240">参考图层及参考层数</th>
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
						 data-formatter="areaTypesFormat" data-filter-data="var:itemAreaTypes"
						data-filter-control-placeholder="" data-width="20">区域级别</th>
					<th data-field="province" data-filter-control="input"
						data-filter-control-placeholder="" data-width="120">省</th>
					<th data-field="city" data-filter-control="input"
						data-filter-control-placeholder="" data-width="120">市</th>
				</tr>
			</thead>
		</table>
	</div>
</body>
</html>

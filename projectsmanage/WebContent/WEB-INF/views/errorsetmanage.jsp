<%@page import="com.emg.projectsmanage.common.ItemSetType"%>
<%@page import="com.emg.projectsmanage.common.ItemSetSysType"%>
<%@page import="com.emg.projectsmanage.common.ItemSetUnit"%>
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
<title>错误筛选配置</title>

<link href="resources/jquery-ui-1.12.1.custom/jquery-ui.min.css"
	rel="stylesheet">
<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css"
	rel="stylesheet" />
<link href="resources/bootstrap-table-1.11.1/bootstrap-table.min.css"
	rel="stylesheet">
	<link href="resources/bootstrap-treeview-1.2.0/bootstrap-treeview.min.css" rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />

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
<script src="resources/bootstrap-treeview-1.2.0/bootstrap-treeview.min.js" /></script>

<script type="text/javascript">
	$(document).ready(function() {
		$.webeditor.getHead();

		$('[data-toggle="errorsets"]').bootstrapTable({
			locale : 'zh-CN',
			onLoadSuccess : function(data) {
			}
		});
	});
	
	var errortypeFirstIn = true;
	var errortypeFirstClick = true;
	var errortypeSelected = new Array();
	var errortypeIDSelected = new Array();
	var errortypeOn = -1;
	
	function indexFormat(value, row, index) {
		return index;
	}
	
	var errorsetSysTypes = eval('(${errorsetSysTypes})');
	var errorsetTypes = eval('(${errorsetTypes})');
	var errorsetUnits = eval('(${errorsetUnits})');
	var processTypes = eval('(${processTypes})');
	
	function queryParams(params) {
		return params;
	}
	
	function processTypeFormat(value, row, index) {
		return processTypes[row.processType];
	}
	
	function operationFormat(value, row, index) {
		var html = new Array();
		html.push('<button class="btn btn-default"  style="margin-bottom:3px;" onclick="getErrorSet(' + row.id + ',' + row.processType + ');">详情</button>');
		html.push('<button class="btn btn-default"  style="margin-bottom:3px;" onclick="deleteErrorSet(' + row.id + ',' + row.processType + ');">删除</button>');
		return html.join('');
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

	function loadDefaultErrorSet() {
		$("#dlgErrorSet table #id").val(0);
		$("#dlgErrorSet table #name").val(new String());
		$("#dlgErrorSet table #errorTypes").val(new String());
		$("#errorTypesCount").text(0);
		$("#dlgErrorSet table #type").prop('selectedIndex', 0);
		$("#dlgErrorSet table #systype").prop('selectedIndex', 0);
		$("#dlgErrorSet table #unit").prop('selectedIndex', 0);
		$("#dlgErrorSet table #desc").val(new String());
		$("#dlgErrorSet table #updatetime").val(new String());
	}

	function getErrorSet(errorsetid, processType) {
		loadDefaultErrorSet();
		if (errorsetid > 0) {
			jQuery.post("./errorsetmanage.web", {
				"atn" : "geterrorset",
				"errorsetid" : errorsetid,
				"processType" : processType
			}, function(json) {
				if (json.errorset) {
					var errorset = json.errorset;
					var errorsetDetails = json.errorsetDetails;
					
					$("#dlgErrorSet table #id").val(errorset.id);
					$("#dlgErrorSet table #name").val(errorset.name);
					$("#dlgErrorSet table #type").val(errorset.type);
					$("#dlgErrorSet table #processType").val(errorset.processType);
					$("#dlgErrorSet table #systype").val(errorset.systype);
					$("#dlgErrorSet table #unit").val(errorset.unit);
					$("#dlgErrorSet table #desc").val(errorset.desc);
					$("#dlgErrorSet table #updatetime").val(errorset.updatetime);
					$("#dlgErrorSet table #errorTypes").val(errorsetDetails);
					$("#errorTypesCount").text(errorsetDetails ? errorsetDetails.split(";").length : 0);
				}
			}, "json");
		}
		showErrorSetDlg();
	}

	
	function showErrorSetDlg() {
		$("#dlgErrorSet").dialog(
				{
					modal : true,
					width : 720,
					title : "错误筛选集合配置",
					open : function(event, ui) {
						$(".ui-dialog-titlebar-close").hide();
					},
					buttons : [
							{
								text : "提交",
								class : "btn btn-default",
								click : submitErrorSet
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
	
    function getErrorTypes() {
		$('[data-toggle="errortypes"]').bootstrapTable(
				{
					locale : 'zh-CN',
					queryParams : function(params) {
						params["processType"] = $("#dlgErrorSet table #processType").val();
						return params;
					},
					onLoadSuccess : function(data) {
						errortypeOn = -1;
						errortypeSelected = new Array();
						errortypeFirstClick = true;
						
						var values = new Array();
						if(errortypeFirstIn) {
							$.each($("#dlgErrorSet table #errorTypes").val().split(";"), function(index, domEle) {
								if(domEle)
									values.push(parseInt(domEle));
							});
						} else {
							$.each(errortypeIDSelected, function(index, domEle) {
								values.push(parseInt(domEle));
							});
						}
						
						if(values.length > 0) {
							$('[data-toggle="errortypes"]').bootstrapTable("checkBy", {
									field : "id",
									values : values
								});
						}
						errortypeFirstIn = false;
					},
					onCheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						if(errortypeSelected.indexOf(index) < 0) {
							errortypeOn = index;
							errortypeSelected.push(index);
							errortypeSelected.sort(compare);
						}
						var errortypeID = row.id;
						if(errortypeIDSelected.indexOf(errortypeID) < 0) {
							errortypeIDSelected.push(errortypeID);
						}
					},
					onUncheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						var indexIn = errortypeSelected.indexOf(index);
						if(indexIn >= 0) {
							errortypeOn = errortypeSelected[indexIn == 0 ? 0 : indexIn -1];
							errortypeSelected.splice(indexIn,1).sort(compare);
						}
						var errortypeID = row.id;
						var errortypeIDIn = errortypeIDSelected.indexOf(errortypeID);
						if(errortypeIDIn >= 0) {
							errortypeIDSelected.splice(errortypeIDIn, 1);
						}
					},
					onCheckAll : function(rows) {
						var elements = $('[data-toggle="errortypes"] td.indexHidden');
						$.each(elements, function(i, element){
							var index = parseInt($(element).text());
							if(errortypeSelected.indexOf(index) < 0) {
								errortypeSelected.push(index);
							}
						});
						errortypeOn = parseInt($('[data-toggle="errortypes"] td.indexHidden:last').text());
						errortypeSelected.sort(compare);
						$.each(rows, function(i, row){
							var name = row.id;
							if(errortypeIDSelected.indexOf(name) < 0) {
								errortypeIDSelected.push(name);
							}
						});
					},
					onUncheckAll : function(rows) {
						var elements = $('[data-toggle="errortypes"] td.indexHidden');
						$.each(elements, function(i, element){
							var index = parseInt($(element).text());
							var indexIn = errortypeSelected.indexOf(index);
							if(indexIn >= 0) {
								errortypeOn = errortypeSelected[indexIn == 0 ? 0 : indexIn -1];
								errortypeSelected.splice(indexIn,1).sort(compare);
							}
						});
						errortypeOn = parseInt($('[data-toggle="errortypes"] td.indexHidden:last').text());
						errortypeSelected.sort(compare);
						$.each(rows, function(i, row){
							var name = row.id;
							var nameIn = errortypeIDSelected.indexOf(name);
							if(nameIn >= 0) {
								errortypeIDSelected.splice(nameIn, 1);
							}
						});
					}
				});
		
		showErrorTypesDlg();
	}
	
	function showErrorTypesDlg() {
		$("#dlgErrorTypes").dialog(
				{
					modal : true,
					width : 1400,
					title : "选择质检项",
					open : function(event, ui) {
						errortypeFirstClick = true;
						errortypeFirstIn = true;
						$(".ui-dialog-titlebar-close").hide();
					},
					close : function(event, ui) {
						errortypeOn = -1;
						errortypeSelected = new Array();
						errortypeIDSelected = new Array();
						errortypeFirstClick = true;
						errortypeFirstIn = true;
						$('[data-toggle="errortypes"]').bootstrapTable("destroy");
					},
					buttons : [
							{
								text : "<",
								title : "上一条",
								class : "btn btn-default",
								click : function() {
									if(!errortypeSelected || errortypeSelected.length <= 0) {
										$.webeditor.showMsgLabel("warning", "没有勾选项");
										return;
									}
									if(errortypeFirstClick) {
										$('[data-toggle="errortypes"]').bootstrapTable('scrollTo',errortypeSelected[0] * 31);
										errortypeOn = errortypeSelected[0];
										errortypeFirstClick = false;
									} else {
										if (errortypeOn < 0) {
											$('[data-toggle="errortypes"]').bootstrapTable('scrollTo', 0);
											$.webeditor.showMsgLabel("warning","已经跳转到第一条");
										} else {
											var index = errortypeSelected.indexOf(errortypeOn);
											if (index < 0) {
												$('[data-toggle="errortypes"]').bootstrapTable('scrollTo',0);
											} else if (index > errortypeSelected.length - 1) {
												$('[data-toggle="errortypes"]').bootstrapTable('scrollTo','bottom');
											} else {
												if (index == 0) {
													$('[data-toggle="errortypes"]').bootstrapTable('scrollTo',errortypeSelected[0] * 31);
													errortypeOn = errortypeSelected[0];
													$.webeditor.showMsgLabel("warning","已经跳转到第一条");
												} else {
													var preIndex = index - 1;
													$('[data-toggle="errortypes"]').bootstrapTable('scrollTo',errortypeSelected[preIndex] * 31);
													errortypeOn = errortypeSelected[preIndex];
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
									if(!errortypeSelected || errortypeSelected.length <= 0) {
										$.webeditor.showMsgLabel("warning", "没有勾选项");
										return;
									}
									if(errortypeFirstClick) {
										$('[data-toggle="errortypes"]').bootstrapTable('scrollTo',errortypeSelected[0] * 31);
										errortypeOn = errortypeSelected[0];
										errortypeFirstClick = false;
									} else {
										if (errortypeOn < 0) {
											$('[data-toggle="errortypes"]').bootstrapTable('scrollTo', 0);
										} else {
											var index = errortypeSelected.indexOf(errortypeOn);
											if (index < 0) {
												$('[data-toggle="errortypes"]').bootstrapTable('scrollTo',0);
											} else if (index > errortypeSelected.length - 1) {
												$('[data-toggle="errortypes"]').bootstrapTable('scrollTo','bottom');
											} else {
												if (index == errortypeSelected.length - 1) {
													var nextIndex = errortypeSelected.length - 1;
													$('[data-toggle="errortypes"]').bootstrapTable('scrollTo',errortypeSelected[nextIndex] * 31);
													errortypeOn = errortypeSelected[nextIndex];
													$.webeditor.showMsgLabel("warning","已经跳转到最后一条");
												} else {
													var nextIndex = index + 1;
													$('[data-toggle="errortypes"]').bootstrapTable('scrollTo',errortypeSelected[nextIndex] * 31);
													errortypeOn = errortypeSelected[nextIndex];
												}
											}
										}
									}
								}
							},
							{
								text : "保存",
								class : "btn btn-default",
								click : function() {
									var length = errortypeIDSelected.length;
									if (length > 0) {
										$("#errorTypes").val(errortypeIDSelected.join(";"));
										$("#errorTypesCount").text(length);
										
										$(this).dialog("close");
									} else {
										$.webeditor.showMsgLabel("alert","请选择质检项");
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
	
	function importErrorTypes() {
		$("#dlgImportErrorTypes").dialog({
			modal : true,
			width : 600,
			title : "导入错误类型",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			close : function(event, ui) {
				$("#ImportErrorTypes").val(new String());
			},
			buttons : [ {
				text : "新增",
				class : "btn btn-default",
				click : function() {
					var importErrorTypes = $("#ImportErrorTypes").val();
					if (!importErrorTypes) {
						$.webeditor.showMsgLabel("alert", "请输入错误类型");
						return;
					}
					if (importErrorTypes.indexOf("；") >= 0) {
						$.webeditor.showMsgLabel("alert", "请使用英文半角分号");
						return;
					}
					$.webeditor.showMsgBox("info", "保存中...");
					jQuery.post("./errorsetmanage.web", {
						"atn" : "recogniseErrortypes",
						"processType" : $("#dlgErrorSet table #processType").val(),
						"errortypes" : importErrorTypes
					}, function(json) {
						if (json.result > 0) {
							var curErrorTypesArray = new Array();
							var curErrorTypesCount = 0;
							var curErrorTypes = $("#errorTypes").val().trim();
							if (curErrorTypes.length > 0) {
								curErrorTypesArray = curErrorTypes.split(";");
								curErrorTypesCount = curErrorTypesArray.length;
							}
							
							var pushin = 0, pushout = 0;
							$.each(json.rows, function(index, domEle) {
								var id = domEle.id;
								if (curErrorTypesArray.indexOf(String(id)) < 0) {
									curErrorTypesArray.push(id);
									pushin++;
								} else {
									pushout++;
								}
							});
							
							$("#errorTypes").val(curErrorTypesArray.join(";"));
							$("#errorTypesCount").text(curErrorTypesArray.length);
							$.webeditor.showMsgBox("close");
							$.webeditor.showMsgLabel("success", "新识别错误类型" + pushin + "个，去重" + pushout + "个");
							$("#dlgImportErrorTypes").dialog("close");
						} else {
							$.webeditor.showMsgBox("close");
							$.webeditor.showMsgLabel("alert", json.resultMsg);
						}
					}, "json");
				}
			}, {
				text : "替换",
				class : "btn btn-default",
				click : function() {
					var importErrorTypes = $("#ImportErrorTypes").val();
					if (!importErrorTypes) {
						$.webeditor.showMsgLabel("alert", "请输入错误类型");
						return;
					}
					if (importErrorTypes.indexOf("；") >= 0) {
						$.webeditor.showMsgLabel("alert", "请使用英文半角分号");
						return;
					}
					$.webeditor.showMsgBox("info", "保存中...");
					jQuery.post("./errorsetmanage.web", {
						"atn" : "recogniseErrortypes",
						"processType" : $("#dlgErrorSet table #processType").val(),
						"errortypes" : importErrorTypes
					}, function(json) {
						if (json.result > 0) {
							var ids = new Array();
							$.each(json.rows, function(index, domEle) {
								var id = domEle.id;
								ids.push(id);
							});
							$("#errorTypes").val(ids.join(";"));
							$("#errorTypesCount").text(json.total);
							$.webeditor.showMsgBox("close");
							$.webeditor.showMsgLabel("success", "共识别错误类型" + json.total + "个");
							$("#dlgImportErrorTypes").dialog("close");
						} else {
							$.webeditor.showMsgBox("close");
							$.webeditor.showMsgLabel("alert", json.resultMsg);
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

	function submitErrorSet() {
		var id = $("#dlgErrorSet table #id").val();
		var name = $("#dlgErrorSet table #name").val();
		var type = $("#dlgErrorSet table #type").val();
		var systype = $("#dlgErrorSet table #systype").val();
		var unit = $("#dlgErrorSet table #unit").val();
		var desc = $("#dlgErrorSet table #desc").val();
		var errorTypes = $("#dlgErrorSet table #errorTypes").val();
		var processType = $("#dlgErrorSet table #processType").val();

		if (name.length <= 0) {
			$.webeditor.showMsgLabel("alert", "错误筛选集合名称不能为空");
			return;
		}

		$.webeditor.showMsgBox("info", "保存中...");
		jQuery.post("./errorsetmanage.web", {
			"atn" : "submiterrorset",
			"errorSetID" : id,
			"name" : name,
			"type" : type,
			"systype" : systype,
			"unit" : unit,
			"desc" : desc,
			"errorTypes" : errorTypes,
			"processType" : processType
		}, function(json) {
			if (json.result) {
				$.webeditor.showMsgBox("close");
				$.webeditor.showMsgLabel("success", "错误筛选集合配置成功");
				$("#dlgErrorSet").dialog("close");
				$('[data-toggle="errorsets"]').bootstrapTable('refresh');
			} else {
				$.webeditor.showMsgBox("close");
				$.webeditor.showMsgLabel("alert", json.resultMsg);
			}
		}, "json");
	}

	function deleteErrorSet(errorSetID, processType) {
		$.webeditor.showConfirmBox("alert","确定要删除错误筛选集合吗？", function(){
			if (!errorSetID || errorSetID <= 0) {
				$.webeditor.showMsgLabel("alert", "错误筛选集合删除失败");
				return;
			}
			
			$.webeditor.showMsgBox("info", "保存中...");
			jQuery.post("./errorsetmanage.web", {
				"atn" : "deleteerrorset",
				"errorSetID" : errorSetID,
				"processType" : processType
			}, function(json) {
				if (json.result > 0) {
					$.webeditor.showMsgBox("close");
					$.webeditor.showMsgLabel("success", "错误筛选集合删除成功");
					$('[data-toggle="errorsets"]').bootstrapTable('refresh');
				} else {
					$.webeditor.showMsgBox("close");
					$.webeditor.showMsgLabel("alert", json.resultMsg);
				}
			}, "json");
		});
	}
</script>
</head>
<body>
	<div class="container" style="max-width: 80%;">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px;">
			<table id="errorsetslist" data-unique-id="id"
				data-query-params="queryParams"
				data-url="./errorsetmanage.web?atn=pages" data-side-pagination="server"
				data-filter-control="true" data-pagination="true"
				data-toggle="errorsets" data-height="714"
				data-page-list="[5, 10, 20, All]" data-page-size="5"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr>
						<th data-field="id" data-filter-control="input"
							data-filter-control-placeholder="" data-width="80">编号
							<button class="btn btn-default btn-xs" onclick="getErrorSet(0,0);">
								<span class="glyphicon glyphicon-plus"></span>
							</button>
						</th>
						<th data-field="name" data-filter-control="input"
							data-filter-control-placeholder="">错误筛选集合名称</th>
						<c:choose>
							<c:when test="${not empty param.process}">
								<th data-field="processType" data-width="120" data-formatter="processTypeFormat" 
									data-filter-control="select" data-filter-data="var:processTypes" data-filter-default-value="${param.process}">
									&nbsp;&nbsp;&nbsp;&nbsp;适用项目类型&nbsp;&nbsp;&nbsp;&nbsp;</th>
							</c:when>
							<c:otherwise>
								<th data-field="processType" data-width="120" data-formatter="processTypeFormat" 
									data-filter-control="select" data-filter-data="var:processTypes" data-filter-default-value="<%= ProcessType.ERROR.getValue() %>">
									&nbsp;&nbsp;&nbsp;&nbsp;适用项目类型&nbsp;&nbsp;&nbsp;&nbsp;</th>
							</c:otherwise>
						</c:choose>
						<th data-field="type" data-formatter="typeFormat" data-width="80"
							data-filter-control="select" data-filter-data="var:errorsetTypes">类型</th>
						<th data-field="systype" data-formatter="sysFormat"
							data-filter-control="select" data-width="100"
							data-filter-data="var:errorsetSysTypes">操作系统</th>
						<th data-field="unit" data-formatter="unitFormat" data-width="80"
							data-filter-control="select" data-filter-data="var:errorsetUnits">质检单位</th>
						<th data-field="desc" data-filter-control="input"
							data-filter-control-placeholder="">描述</th>
						<!-- <th data-field="updatetime">更新时间</th> -->
						<th data-formatter="operationFormat" data-width="80">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	<div id="dlgErrorSet" style="display: none;">
		<table class="table">
			<tbody>
				<tr>
					<td class="configKey">编号</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="id" readonly>
					</td>
				</tr>
				<tr>
					<td class="configKey">错误筛选集合名称</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="name"
						placeholder="请输入错误筛选集合名称"></td>
				</tr>
				<tr>
					<td class="configKey">适用项目类型</td>
					<td class="configValue"><select
						class="form-control configValue" id="processType">
							<c:set var="processTypes" value="<%= ProcessType.values() %>"/>
							<c:forEach items="${processTypes }" var="processType">
								<c:if test="${processType.getValue() > 0 }">
									<option value="${processType.getValue() }">${processType.getDes() }</option>
								</c:if>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">错误类型</td>
					<td class="configValue">
						<input type="hidden" id="errorTypes" value="">
						<button type="button" class="btn btn-default"
							onclick="getErrorTypes();">选择错误类型</button>
						<button type="button" class="btn btn-default"
							onclick="importErrorTypes();">导入错误类型</button>
						<p class="help-block">已选择<span id="errorTypesCount"></span>个错误类型</p></td>
				</tr>
				<tr>
					<td class="configKey">类型</td>
					<td class="configValue"><select
						class="form-control configValue" id="type">
							<c:set var="types" value="<%= ItemSetType.values() %>"/>
							<c:forEach items="${types }" var="type">
								<option value="${type.getValue() }">${type.getDes() }</option>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">操作系统</td>
					<td class="configValue"><select
						class="form-control configValue" id="systype">
							<c:set var="sysTypes" value="<%= ItemSetSysType.values() %>"/>
							<c:forEach items="${sysTypes }" var="sysType">
								<option value="${sysType.getValue() }">${sysType.getDes() }</option>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">质检单位</td>
					<td class="configValue"><select
						class="form-control configValue" id="unit">
						<c:set var="units" value="<%= ItemSetUnit.values() %>"/>
						<c:forEach items="${units }" var="unit">
							<option value="${unit.getValue() }">${unit.getDes() }</option>
						</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">描述</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="desc"
						placeholder="请输入描述"></td>
				</tr>
				<tr>
					<td class="configKey">更新时间</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="updatetime" readonly></td>
				</tr>
			</tbody>
		</table>
	</div>
	<div id="dlgErrorTypes" style="display: none;">
		<table id="errortypeslist" class="table-condensed" data-unique-id="id"
			data-url="./errorsetmanage.web?atn=geterrortypes" data-cache="false"
			data-side-pagination="server" data-filter-control="true"
			data-click-to-select="true" data-single-select="false"
			data-select-item-name="checkboxName" data-pagination="false"
			data-toggle="errortypes" data-height="625"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true"></th>
					<th data-field="index" data-class="indexHidden" data-formatter="indexFormat"></th>
					<th data-field="id" data-filter-control-placeholder=""
						data-filter-control="input" data-width="60">编号</th>
					<th data-field="name" data-filter-control-placeholder=""
						data-filter-control="input">质检项名称</th>
					<th data-field="qid" data-filter-control-placeholder=""
						data-filter-control="input" data-width="80">质检项</th>
					<th data-field="errortype" data-filter-control-placeholder=""
						data-filter-control="input" data-width="80">错误类型编码</th>
					<th data-field="desc" data-filter-control-placeholder=""
						data-filter-control="input">错误类型名称</th>
				</tr>
			</thead>
		</table>
		</div>
	</div>
	<div id="dlgImportErrorTypes" style="display: none;">
		<textarea class="form-control" rows="12" id="ImportErrorTypes"></textarea>
	</div>
</body>
</html>
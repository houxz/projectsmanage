<%@page import="com.emg.projectsmanage.common.ItemSetType"%>
<%@page import="com.emg.projectsmanage.common.ItemSetEnable"%>
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
<title>质检集合配置</title>

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
	$(document).ready(function() {
		$.webeditor.getHead();
		$(function () { $("[data-toggle='tooltip']").tooltip(); });
		$('[data-toggle="itemsets"]').bootstrapTable({
			locale : 'zh-CN',
			onLoadSuccess : function(data) {
			}
		});
	});
	
	var itemsetSysTypes = eval('(${itemsetSysTypes})');
	var itemsetTypes = eval('(${itemsetTypes})');
	//add by lianhr begin 2018/01/25
	var itemInfoTypes2 = {"质检项":"质检项", "工具":"工具", "九宫格质检":"九宫格质检", "全域质检":"全域质检"};
	var itemsetSysTypes2 = {"32位系统":"32位系统", "64位系统":"64位系统"};
	//add by lianhr end
	var itemsetUnits = eval('(${itemsetUnits})');
	var itemInfoSysTypes = eval('(${itemsetSysTypes})');
	var itemInfoTypes = eval('(${itemsetTypes})');
	var itemInfoUnits = eval('(${itemsetUnits})');
	var layerEles = eval('(${layerElements})');
	var processTypes = eval('(${processTypes})');
	
	var curFangAn = 'custom';
	
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
	
	function queryParams(params) {
		return params;
	}
	
	function operationFormat(value, row, index) {
		var html = new Array();
		html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="getItemSet(' + row.id + ',' + row.processType + ');">详情</div>');
		html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="deleteItemSet(' + row.id + ',' + row.processType + ');">删除</div>');
		return html.join('');
	}
	
	function nameFormat(value, row, index) {
		var html = new Array();
		html.push("<div class='bootstrapColumn' >");
		html.push(value);
		html.push("</div>");
		return html.join("");
	}
	function processTypeFormat(value, row, index) {
		return processTypes[row.processType];
	}
	
	function layernameFormat(value, row, index) {
		var html = new Array();
		html.push("<pre class='bootstrapColumn' >");
		html.push(value);
		html.push("</pre>");
		return html.join("");
	}
	function sysFormat(value, row, index) {
		return itemsetSysTypes[row.systype];
	}
	function typeFormat(value, row, index) {
		return itemsetTypes[row.type];
	}
	//add by lianhr begin 2018/01/25
	function typeInfoFormat(value, row, index) {
		return itemInfoTypes[row.type];
	}
	//add by lianhr end
	
	function unitFormat(value, row, index) {
		return itemsetUnits[row.unit];
	}
	function itemInfoUnitFormat(value, row, index) {
		return itemInfoUnits[row.unit];
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

	function loadDefaultItemSet() {
		$("#dlgItemSet table #id").val(0);
		$("#dlgItemSet table #name").val("");
		$("#dlgItemSet table #layername").val("");
		$("#dlgItemSet table #layername span").text(0);
		$("#dlgItemSet table #items").val("");
		$("#dlgItemSet table #items span").text(0);
		$("#dlgItemSet table #itemInfos").val("");
		$("#dlgItemSet table #itemInfos span").text(0);
		$("#dlgItemSet table #type").prop('selectedIndex', 0);
		$("#dlgItemSet table #systype").prop('selectedIndex', 0);
		$("#dlgItemSet table #referdata").val("");
		$("#dlgItemSet table #unit").prop('selectedIndex', 0);
		$("#dlgItemSet table #desc").val("");
		$("#dlgItemSet table #updatetime").val("");
		
		$("#fangans ul li.active").removeClass('active');
		$("#fangans ul li:eq(0)").addClass('active');
		
		$("#fangans table").hide();
		$("#fangans table:eq(0)").show();
	}

	function getItemSet(itemsetid, processType) {
		loadDefaultItemSet();
		if (itemsetid > 0) {
			$("#importItems").attr("disabled", true);
			
			jQuery.post("./itemsetmanage.web", {
				"atn" : "getitemset",
				"itemsetid" : itemsetid,
				"processType" : processType
			}, function(json) {
				if (json.itemset) {
					var itemset = json.itemset;
					
					$("#dlgItemSet table #id").val(itemset.id);
					$("#dlgItemSet table #name").val(itemset.name);
					$("#dlgItemSet table #processType").val(itemset.processType);
					$("#dlgItemSet table #layername").val(itemset.layername.replace(/;/g,","));
					$("#dlgItemSet table #layername span").text(itemset.layername.split(";").length);
					$("#dlgItemSet table #type").val(itemset.type);
					$("#dlgItemSet table #systype").val(itemset.systype);
					$("#dlgItemSet table #referdata").val(itemset.referdata);
					$("#dlgItemSet table #unit").val(itemset.unit);
					$("#dlgItemSet table #desc").val(itemset.desc);
					$("#dlgItemSet table #updatetime").val(itemset.updatetime);
					$("#dlgItemSet table #items").val(json.items);
					$("#dlgItemSet table #items span").text(json.items ? json.items.split(",").length : 0);
					
					var itemInfos = json.itemInfos;
					var values = new Array();
					$.each(itemInfos, function(index, itemInfo) {
						values.push(itemInfo.id);
					});
					$("#dlgItemSet table #itemInfos").val(values.join(','));
					$("#dlgItemSet table #itemInfos span").text(values.length);
				}
			}, "json");
		} else {
			$("#importItems").removeAttr("disabled");
		}
		showItemSetDlg();
	}

	function showItemSetDlg() {
		$("#dlgItemSet").dialog({
			modal : true,
			width : 600,
			title : "质检集合配置",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			buttons : [
					{
						text : "保存",
						class : "btn btn-default",
						click : submitItemSet
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
	
	function getLayers() {
		$("#dlgLayers").bootstrapDialog({
			onLoadSuccess : function(data) {
				$(this.self).bootstrapTable("load", data.rows);
				
				var curItemSetID = $("#dlgItemSet table #id").val();
				if(curItemSetID !== "0") {
					$("#dlgLayers input:checkbox").attr("disabled", true);
				} else {
					$("#dlgLayers input:checkbox").removeAttr("disabled");
				}
			}
		}, {
			width : 480,
			title : "选择图层"
		});
	}
		
	function getItems() {
		$("#dlgItems").bootstrapDialog({
			queryParams : function(params) {
				params["processType"] = $("#dlgItemSet table #processType").val();
				return params;
			},
			onLoadSuccess : function(data) {
				$(this.self).bootstrapTable("load", data.rows);
				
				var curItemSetID = $("#dlgItemSet table #id").val();
				if(curItemSetID !== "0") {
					$("#dlgItems input:checkbox:not(:checked)").attr("disabled", true);
				} else {
					$("#dlgItems input:checkbox").removeAttr("disabled");
				}
			}
		}, {
			width : 480,
			title : "选择质检项"
		});
	}
	
	function importItems() {
		$("#dlgImportItems").dialog({
			modal : true,
			width : 520,
			title : "导入质检项",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			close : function(event, ui) {
				$("#importItems").val("");
			},
			buttons : [ {
				text : "新增",
				class : "btn btn-default",
				click : function() {
					var importItems = $("#importItems").val();
					if (!importItems) {
						$.webeditor.showMsgLabel("alert", "请输入质检项");
						return;
					}
					if (importItems.indexOf("；") >= 0) {
						$.webeditor.showMsgLabel("alert", "请使用英文半角分号");
						return;
					}
					var curItemsArray = new Array();
					var curItemsCount = 0;
					var curItems = $("#dlgItemSet table #items").val().trim();
					if (curItems.length > 0) {
						curItemsArray = curItems.split(",");
						curItemsCount = curItemsArray.length;
					}
					
					var pushin = 0, pushout = 0;
					$.each(importItems.split(";"), function(index, domEle) {
						if (domEle.trim() && curItemsArray.indexOf(String(domEle.trim())) < 0) {
							curItemsArray.push(String(domEle.trim()));
							pushin++;
						} else {
							pushout++;
						}
					});
					
					$("#dlgItemSet table #items").val(curItemsArray.join(","));
					$("#dlgItemSet table #items span").text(curItemsArray.length);
					$.webeditor.showMsgBox("close");
					$.webeditor.showMsgLabel("success", "新识别质检项" + pushin + "个，去重" + pushout + "个");
					$("#dlgImportItems").dialog("close");
				}
			}, {
				text : "替换",
				class : "btn btn-default",
				click : function() {
					var importItems = $("#importItems").val();
					if (!importItems) {
						$.webeditor.showMsgLabel("alert", "请输入质检项");
						return;
					}
					if (importItems.indexOf("；") >= 0) {
						$.webeditor.showMsgLabel("alert", "请使用英文半角分号");
						return;
					}
					var curItemsArray = new Array();
					var curItemsCount = 0;
					
					var pushin = 0, pushout = 0;
					$.each(importItems.split(";"), function(index, domEle) {
						if (domEle.trim() && curItemsArray.indexOf(String(domEle.trim())) < 0) {
							curItemsArray.push(String(domEle.trim()));
							pushin++;
						} else {
							pushout++;
						}
					});
					
					$("#dlgItemSet table #items").val(curItemsArray.join(","));
					$("#dlgItemSet table #items span").text(curItemsArray.length);
					$.webeditor.showMsgBox("close");
					$.webeditor.showMsgLabel("success", "新识别质检项" + pushin + "个，去重" + pushout + "个");
					$("#dlgImportItems").dialog("close");
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
	
	
	function getItemInfos() {
		$("#dlgItemInfos").bootstrapDialog({
			queryParams : function(params) {
				params["processType"] = $("#dlgItemSet table #processType").val();
				return params;
			}
		}, {
			width : document.documentElement.clientWidth * 0.8,
			title : "选择图层与质检项"
		});
	}
	
	//add by lianhr begin 2019/02/20
	function importInfos() {
		$("#dlgImportInfos").dialog({
			modal : true,
			width : 520,
			title : "导入图层与质检项",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			close : function(event, ui) {
				$("#importInfos").val("");
			},
			
			buttons : [ {
				text : "新增",
				class : "btn btn-default",
				click : function() {
					var importItems = $("#importInfos").val();
					if (!importItems) {
						$.webeditor.showMsgLabel("alert", "请输入质检项");
						return;
					}
					if (importItems.indexOf("；") >= 0) {
						$.webeditor.showMsgLabel("alert", "请使用英文半角分号");
						return;
					}
					if (importItems.indexOf("：") >= 0) {
						$.webeditor.showMsgLabel("alert", "请使用英文半角冒号");
						return;
					}
					jQuery.post("./itemsetmanage.web", {
						"atn" : "getInfoID",
						"curItemsArray" : importItems,
						"processType" : $("#dlgItemSet table #processType").val()
					}, function(json) {
						if (json.itemInfos) {
							importItems = json.itemInfos;
							var curItemsArray = new Array();
							var curItemsCount = 0;
							var curItems = $("#dlgItemSet table #itemInfos").val().trim();
							if (curItems.length > 0) {
								curItemsArray = curItems.split(",");
								curItemsCount = curItemsArray.length;
							}
							
							var pushin = 0, pushout = 0;
							$.each(importItems.split(","), function(index, domEle) {
								if (domEle.trim() && curItemsArray.indexOf(String(domEle.trim())) < 0) {
									curItemsArray.push(String(domEle.trim()));
									pushin++;
								} else {
									pushout++;
								}
							});
							
							$("#dlgItemSet table #itemInfos").val(curItemsArray.join(","));
							$("#dlgItemSet table #itemInfos span").text(curItemsArray.length);
							$.webeditor.showMsgBox("close");
							$.webeditor.showMsgLabel("success", "新识别质检项" + pushin + "个，去重" + pushout + "个");
							$("#dlgImportInfos").dialog("close");
						}
					}, "json");
					
				}
			}, {
				text : "替换",
				class : "btn btn-default",
				click : function() {
					var importItems = $("#importInfos").val();
					if (!importItems) {
						$.webeditor.showMsgLabel("alert", "请输入质检项");
						return;
					}
					if (importItems.indexOf("；") >= 0) {
						$.webeditor.showMsgLabel("alert", "请使用英文半角分号");
						return;
					}
					if (importItems.indexOf("：") >= 0) {
						$.webeditor.showMsgLabel("alert", "请使用英文半角冒号");
						return;
					}
					jQuery.post("./itemsetmanage.web", {
						"atn" : "getInfoID",
						"curItemsArray" : importItems,
						"processType" : $("#dlgItemSet table #processType").val()
					}, function(json) {
						if (json.itemInfos) {
							importItems = json.itemInfos;
							var curItemsArray = new Array();
							var curItemsCount = 0;
							
							var pushin = 0, pushout = 0;
							$.each(importItems.split(","), function(index, domEle) {
								if (domEle.trim() && curItemsArray.indexOf(String(domEle.trim())) < 0) {
									curItemsArray.push(String(domEle.trim()));
									pushin++;
								} else {
									pushout++;
								}
							});
							
							$("#dlgItemSet table #itemInfos").val(curItemsArray.join(","));
							$("#dlgItemSet table #itemInfos span").text(curItemsArray.length);
							$.webeditor.showMsgBox("close");
							$.webeditor.showMsgLabel("success", "新识别质检项" + pushin + "个，去重" + pushout + "个");
							$("#dlgImportInfos").dialog("close");
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
	//add by lianhr end
	
	function submitItemSet() {
		var id = $("#dlgItemSet table #id").val();
		var name = $("#dlgItemSet table #name").val();
		var layername = $("#dlgItemSet table #layername").val();
		var type = $("#dlgItemSet table #type").val();
		var systype = $("#dlgItemSet table #systype").val();
		var referdata = $("#dlgItemSet table #referdata").val();
		var unit = $("#dlgItemSet table #unit").val();
		var desc = $("#dlgItemSet table #desc").val();
		var items = $("#dlgItemSet table #items").val();
		var processType = $("#dlgItemSet table #processType").val();
		var itemInfos = $("#dlgItemSet table #itemInfos").val();
		
		if(name.length <= 0) {
			$.webeditor.showMsgLabel("alert", "图层集合名称不能为空");
			return;
		}
		
		if (curFangAn == "custom") {
			$.webeditor.showMsgBox("info", "保存中...");
			jQuery.post("./itemsetmanage.web", {
				"atn" : "submititemsetcustom",
				"itemSetID" : id,
				"name" : name,
				"itemInfos" : itemInfos,
				"type" : type,
				"systype" : systype,
				"unit" : unit,
				"desc" : desc,
				"processType" : processType
			},
			function(json) {
				if (json.result) {
					$.webeditor.showMsgBox("close");
					$.webeditor.showMsgLabel("success","质检集合配置成功");
					$("#dlgItemSet").dialog("close");
					$('[data-toggle="itemsets"]').bootstrapTable('refresh');
				} else {
					$.webeditor.showMsgBox("close");
					$.webeditor.showMsgLabel("alert", json.resultMsg);
				}
			}, "json");
		} else if (curFangAn == "preliminary1") {
			$.webeditor.showMsgBox("info", "保存中...");
			jQuery.post("./itemsetmanage.web", {
				"atn" : "submititemsetpreliminary",
				"itemSetID" : id,
				"name" : name,
				"layername" : layername,
				"type" : type,
				"systype" : systype,
				"unit" : unit,
				"desc" : desc,
				"items" : items,
				"processType" : processType,
				"preliminary" : 1
			},
			function(json) {
				if (json.result) {
					$.webeditor.showMsgBox("close");
					$.webeditor.showMsgLabel("success","质检集合配置成功");
					$("#dlgItemSet").dialog("close");
					$('[data-toggle="itemsets"]').bootstrapTable('refresh');
				} else {
					$.webeditor.showMsgBox("close");
					$.webeditor.showMsgLabel("alert", json.resultMsg);
				}
			}, "json");
		} else {
			$.webeditor.showMsgLabel("alert", "方案未知，质检集合保存失败！");
		}
		
	}
	
	function deleteItemSet(itemSetID, processType) {
		$.webeditor.showConfirmBox("alert","确定要删除质检集合吗？", function(){
			if(!itemSetID || itemSetID <= 0) {
				$.webeditor.showMsgLabel("alert", "质检集合删除失败");
				return;
			}
			$.webeditor.showMsgBox("info", "保存中...");
			jQuery.post("./itemsetmanage.web",
				{
					"atn" : "deleteitemset",
					"itemSetID" : itemSetID,
					"processType" : processType
				},
				function(json) {
					if (json.result > 0) {
						$.webeditor.showMsgBox("close");
						$.webeditor.showMsgLabel("success","质检集合删除成功");
						$('[data-toggle="itemsets"]').bootstrapTable('refresh');
					} else {
						$.webeditor.showMsgBox("close");
						$.webeditor.showMsgLabel("alert",json.resultMsg);
					}
				}, "json");
		});
	}
	
	function itemResponse(json) {
		if (json && json.result > 0)
			return json;
		else {
			$.webeditor.showMsgLabel("alert",json.resultMsg);
			return json;
		}
	}
	
	function fanganChange(id, obj) {
		$(obj).parent().siblings().removeClass('active');
		$(obj).parent().addClass('active');
		
		$("#fangans table").hide();
		$("#fangans table#" + id).show();
		
		curFangAn = id;
	}
</script>
</head>
<body>
	<div class="container" style="max-width: 80%;">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px;">
			<table id="itemsetslist" data-unique-id="id"
				data-query-params="queryParams"
				data-url="./itemsetmanage.web?atn=pages" data-side-pagination="server"
				data-filter-control="true" data-pagination="true"
				data-toggle="itemsets" data-height="714" data-response-handler="itemResponse"
				data-page-list="[5, 10, 20, All]" data-page-size="5"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr>
						<th data-field="id" data-filter-control="input" data-width="80"
							data-filter-control-placeholder="">编号
							<div class="btn btn-default btn-xs" onclick="getItemSet(0,0);">
								<span class="glyphicon glyphicon-plus"></span>
							</div>
						</th>
						<th data-field="name" data-filter-control="input" data-width="140"
							data-formatter="nameFormat" data-filter-control-placeholder="">质检集合名称</th>
						<c:choose>
							<c:when test="${not empty param.process}">
								<th data-field="processType" data-width="120" data-formatter="processTypeFormat" 
									data-filter-control="select" data-filter-data="var:processTypes" data-filter-default-value="${param.process}">
									&nbsp;&nbsp;&nbsp;&nbsp;适用项目类型&nbsp;&nbsp;&nbsp;&nbsp;</th>
							</c:when>
							<c:otherwise>
								<th data-field="processType" data-width="120" data-formatter="processTypeFormat" 
									data-filter-control="select" data-filter-data="var:processTypes" data-filter-default-value="<%= ProcessType.ATTACH.getValue() %>">
									&nbsp;&nbsp;&nbsp;&nbsp;适用项目类型&nbsp;&nbsp;&nbsp;&nbsp;</th>
							</c:otherwise>
						</c:choose>
						<th data-field="layername" data-filter-control="input"
							data-width="180" data-formatter="layernameFormat"
							data-filter-control-placeholder="">图层</th>
						<th data-field="type" data-formatter="typeFormat"
							data-filter-control="select" data-filter-data="var:itemsetTypes"
							data-width="140">类型</th>
						<th data-field="systype" data-formatter="sysFormat"
							data-filter-control="select" data-width="100"
							data-filter-data="var:itemsetSysTypes">操作系统</th>
						<th data-field="referdata" data-width="220"
							data-formatter="referdataFormat" data-filter-control="input"
							data-filter-control-placeholder="">参考图层</th>
						<th data-field="unit" data-formatter="unitFormat" data-width="100"
							data-filter-control="select" data-filter-data="var:itemsetUnits">质检单位</th>
						<th data-field="desc" data-filter-control="input" data-width="100"
							data-formatter="descFormat" data-filter-control-placeholder="">描述</th>
						<!-- <th data-field="updatetime">更新时间</th> -->
						<th data-formatter="operationFormat" data-width="70">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	<div id="dlgItemSet" style="display: none;">
		<table class="table table-condensed">
			<tbody>
				<tr>
					<td class="configKey">编号</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="id" readonly>
					</td>
				</tr>
				<tr>
					<td class="configKey">质检集合名称</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="name"
						placeholder="请输入图层集合名称"></td>
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
					<td colspan="2" id="fangans">
						<ul class="nav nav-tabs" style="margin: 0px auto; width: 70%;">
							<li class="active"><a href="#" onclick="fanganChange('custom', this);">自定义</a></li>
							<li><a href="#" onclick="fanganChange('preliminary1', this);">预选方案1 
								<span class="glyphicon glyphicon-exclamation-sign" data-toggle="tooltip" data-html="true" title="POI+其他<br>Road+其他<br>Road+POI<br>其他"></span>
								</a>
							</li>
						</ul>
						<table class="table table-condensed" style="margin-bottom: 0;" id="custom">
							<tbody>
							<tr>
								<td class="configKey" style="border-top-color: #fff;">图层与质检项</td>
								<td class="configValue" style="border-top-color: #fff;">
									<button type="button" class="btn btn-default"
										onclick="getItemInfos();">选择图层与质检项</button>
									<button type="button" class="btn btn-default"
										onclick="importInfos();">导入图层与质检项</button>
									<p class="help-block" id="itemInfos">
										已选择<span>0</span>个图层与质检项
									</p>
								</td>
							</tr>
							</tbody>
						</table>
						<table class="table table-condensed" style="margin-bottom: 0; display: none;" id="preliminary1">
							<tbody>
							<tr>
								<td class="configKey" style="border-top-color: #fff;">图层</td>
								<td class="configValue" style="border-top-color: #fff;">
									<button type="button" class="btn btn-default"
										onclick="getLayers();">选择图层</button>
									<p class="help-block" id="layername">
										已选择<span>0</span>个图层
									</p>
								</td>
							</tr>
							<tr>
								<td class="configKey" style="border-top-color: #fff;">质检项</td>
								<td class="configValue" style="border-top-color: #fff;">
									<button type="button" class="btn btn-default"
										onclick="getItems();">选择质检项</button>
									<button type="button" class="btn btn-default"
										onclick="importItems();">导入质检项</button>
									<p class="help-block" id="items">
										已选择<span>0</span>个质检项
									</p>
								</td>
							</tr>
							</tbody>
						</table>
					</td>
				</tr>
				<tr>
					<td class="configKey">类型</td>
					<td class="configValue"><select
						class="form-control configValue" id="type">
							<c:set var="itemsetTypes" value="<%= ItemSetType.values() %>"/>
							<c:forEach items="${itemsetTypes }" var="itemsetType">
								<option value="${itemsetType.getValue() }">${itemsetType.getDes() }</option>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">操作系统</td>
					<td class="configValue"><select
						class="form-control configValue" id="systype">
							<c:set var="itemsetSysTypes" value="<%= ItemSetSysType.values() %>"/>
							<c:forEach items="${itemsetSysTypes }" var="itemsetSysType">
								<option value="${itemsetSysType.getValue() }">${itemsetSysType.getDes() }</option>
							</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">参考图层</td>
					<td class="configValue">
						<textarea class="form-control " rows="3" id="referdata" readonly></textarea>
					</td>
				</tr>
				<tr>
					<td class="configKey">质检单位</td>
					<td class="configValue"><select
						class="form-control configValue" id="unit">
						<c:set var="itemsetUnits" value="<%= ItemSetUnit.values() %>"/>
						<c:forEach items="${itemsetUnits }" var="itemsetUnit">
							<option value="${itemsetUnit.getValue() }">${itemsetUnit.getDes() }</option>
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
	<div id="dlgLayers" style="display: none;">
		<table id="layerslist" class="table-condensed"
			data-unique-id="name"
			data-url="./itemsetmanage.web?atn=getlayers"
			data-toggle="layers"
			data-height="420"
			data-value-band="layername"
			data-response-handler="itemResponse">
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true" data-value-band="layername" data-unique-id="name" data-formatter="checkboxFormat"></th>
					<th data-field="index" data-class="indexHidden" data-formatter="indexFormat"></th>
					<th data-field="id"	data-filter-control="input"
						data-filter-control-placeholder="" data-width="20">编号</th>
					<th data-field="name" data-filter-control="input"
						data-filter-control-placeholder="" data-width="20">代码</th>
					<th data-field="desc" data-filter-control="input"
						data-filter-control-placeholder="" data-width="120">描述</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="dlgItems" style="display: none;">
		<table id="itemslist" class="table-condensed" 
			data-unique-id="oid"
			data-url="./itemsetmanage.web?atn=getqids"
			data-toggle="items"
			data-height="420"
			data-value-band="items"
			data-response-handler="itemResponse">
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true" data-value-band="items" data-unique-id="oid" data-formatter="checkboxFormat"></th>
					<th data-field="index" data-class="indexHidden" data-formatter="indexFormat"></th>
					<th data-field="oid" data-filter-control-placeholder=""
						data-filter-control="input" data-width="80">QID</th>
					<th data-field="name" data-filter-control-placeholder=""
						data-filter-control="input" data-width="260">名称</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="dlgItemInfos" style="display: none;">
		<table id="itemInfoslist" class="table-condensed"
			data-unique-id="id"
			data-url="./itemsetmanage.web?atn=getiteminfos"
			data-toggle="itemInfos"
			data-height="620"
			data-value-band="itemInfos"
			data-response-handler="itemResponse">
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true" data-value-band="itemInfos" data-formatter="checkboxFormat"></th>
					<th data-field="index" data-class="indexHidden" data-formatter="indexFormat"></th>
					
					<th data-field="id" data-filter-control-placeholder=""
						data-filter-control="input" data-width="50">编号</th>
						
					<th data-field="oid" data-filter-control-placeholder=""
						data-filter-control="input" data-width="100">OID</th>
						
					<th data-field="name" data-filter-control-placeholder=""
						data-filter-control="input">名称</th>
						
					<th data-field="layername" data-filter-control-placeholder=""
						data-filter-control="input" data-width="90">图层</th>
						
					<th data-field="referdata" data-formatter="referdataFormat" data-filter-control="input"
							data-filter-control-placeholder="">参考图层</th>
					
					<th data-field="typeInfo" data-formatter="typeInfoFormat" data-width="100"
							data-filter-control="select" data-filter-data="var:itemInfoTypes2">类型</th>
					
					<th data-field="systype2" data-formatter="sysFormat" data-filter-control="select" 
					        data-filter-control="select" data-filter-data="var:itemsetSysTypes2">操作系统</th>
					
					<th data-field="unit" data-formatter="unitFormat">质检单位</th>
				</tr>
			</thead>
		</table>
	</div>
	<div id="dlgImportItems" style="display: none;">
		<textarea class="form-control" rows="12" id="importItems"></textarea>
	</div>
	<div id="dlgImportInfos" style="display: none;">
		<textarea class="form-control" rows="12" id="importInfos"></textarea>
	</div>
</body>
</html>
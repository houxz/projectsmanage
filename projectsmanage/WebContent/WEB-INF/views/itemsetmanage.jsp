<%@page import="com.emg.projectsmanage.common.ItemSetType"%>
<%@page import="com.emg.projectsmanage.common.ItemSetEnable"%>
<%@page import="com.emg.projectsmanage.common.ItemSetSysType"%>
<%@page import="com.emg.projectsmanage.common.ItemSetUnit"%>
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

<link href="resources/jquery-flexselect-0.9.0/flexselect.css"
	rel="stylesheet">
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
<script src="resources/jquery-flexselect-0.9.0/jquery.flexselect.js"></script>
<script src="resources/jquery-flexselect-0.9.0/liquidmetal.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$.webeditor.getHead();

		$('[data-toggle="itemsets"]').bootstrapTable({
			locale : 'zh-CN',
			onLoadSuccess : function(data) {
			}
		});
	});
	
	var itemFirstIn = true;
	var itemSelected = new Array();
	var itemOn = -1;
	var layerFirstIn = true;
	var layerSelected = new Array();
	var layerOn = -1;
	
	var itemsetEnables = eval('(${itemsetEnables})');
	var itemsetSysTypes = eval('(${itemsetSysTypes})');
	var itemsetTypes = eval('(${itemsetTypes})');
	var itemsetUnits = eval('(${itemsetUnits})');
	var layerEles = eval('(${layerElements})');
	
	function indexFormat(value, row, index) {
		return index;
	}
	
	function queryParams(params) {
		return params;
	}
	
	function operationFormat(value, row, index) {
		var html = new Array();
		html.push('<button class="btn btn-default"  style="margin-bottom:3px;" onclick="getItemSet(' + row.id + ');">详情</button>');
		html.push('<button class="btn btn-default"  style="margin-bottom:3px;" onclick="deleteItemSet(' + row.id + ');">删除</button>');
		return html.join('');
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
	function referdataFormat(value, row, index) {
		var html = new Array();
		html.push("<pre style='word-wrap: break-word; white-space: pre-wrap; white-space: -moz-pre-wrap' >");
		html.push(value);
		html.push("</pre>");
		return html.join("");
	}

	function loadDefaultItemSet() {
		$("#dlgItemSet table #id").val(0);
		$("#dlgItemSet table #name").val(new String());
		$("#dlgItemSet table #layername").val(new String());
		$("#layerscount").text(0);
		$("#dlgItemSet table #items").val(new String());
		$("#itemscount").text(0);
		$("#dlgItemSet table #type").prop('selectedIndex', 0);
		$("#dlgItemSet table #systype").prop('selectedIndex', 0);
		$("#dlgItemSet table #referdata").val(new String());
		$("#dlgItemSet table #unit").prop('selectedIndex', 0);
		$("#dlgItemSet table #desc").val(new String());
		$("#dlgItemSet table #updatetime").val(new String());
	}

	function getItemSet(itemsetid) {
		loadDefaultItemSet();
		if (itemsetid > 0) {
			jQuery.post("./itemsetmanage.web", {
				"atn" : "getitemset",
				"itemsetid" : itemsetid
			}, function(json) {
				if (json.itemset) {
					var itemset = json.itemset;
					
					$("#dlgItemSet table #id").val(itemset.id);
					$("#dlgItemSet table #name").val(itemset.name);
					$("#dlgItemSet table #layername").val(itemset.layername);
					$("#layerscount").text(itemset.layername.split(";").length);
					$("#dlgItemSet table #type").val(itemset.type);
					$("#dlgItemSet table #systype").val(itemset.systype);
					$("#dlgItemSet table #referdata").val(itemset.referdata);
					$("#dlgItemSet table #unit").val(itemset.unit);
					$("#dlgItemSet table #desc").val(itemset.desc);
					$("#dlgItemSet table #updatetime").val(itemset.updatetime);
					$("#dlgItemSet table #items").val(json.items);
					$("#itemscount").text(json.items ? json.items.split(";").length : 0);
				}
			}, "json");
		}
		showItemSetDlg();
	}

	
	function showItemSetDlg() {
		$("#dlgItemSet").dialog(
				{
					modal : true,
					height: 600,
					width : 720,
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
		$('[data-toggle="layers"]').bootstrapTable(
				{
					locale : 'zh-CN',
					queryParams : function(params) {
						return params;
					},
					onLoadSuccess : function(data) {
						var values = new Array();
						$.each($("#dlgItemSet table #layername").val().split(";"), function(index, domEle) {
							values[index] = domEle;
						});
						$('[data-toggle="layers"]').bootstrapTable("checkBy", {
									field : "name",
									values : values
								});
						var curItemSetID = $("#dlgItemSet table #id").val();
						if(curItemSetID !== "0") {
							$("#dlgLayers input:checkbox").attr("disabled", true);
						} else {
							$("#dlgLayers input:checkbox").removeAttr("disabled");
						}
					},
					onCheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						if(layerSelected.indexOf(index) < 0) {
							layerOn = index;
							layerSelected.push(index);
							layerSelected.sort(compare);
						}
					},
					onUncheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						var indexIn = layerSelected.indexOf(index);
						if(indexIn >= 0) {
							layerOn = layerSelected[indexIn == 0 ? 0 : indexIn -1];
							layerSelected.splice(indexIn,1).sort(compare);
						}
					}
				});
		
		showLayersDlg();
	}
	
	function showLayersDlg() {
		$("#dlgLayers").dialog(
				{
					modal : true,
					width : 540,
					title : "选择图层",
					open : function(event, ui) {
						layerFirstIn = true;
						$(".ui-dialog-titlebar-close").hide();
					},
					close : function(event, ui) {
						layerOn = -1;
						layerSelected = new Array();
						layerFirstIn = true;
						$('[data-toggle="layers"]').bootstrapTable("destroy");
					},
					buttons : [
							{
								text : "<",
								title : "上一条",
								class : "btn btn-default",
								click : function() {
									if(layerFirstIn) {
										$('[data-toggle="layers"]').bootstrapTable('scrollTo',layerSelected[0] * 31);
										layerOn = layerSelected[0];
										layerFirstIn = false;
									} else {
										if (layerOn < 0) {
											$('[data-toggle="layers"]').bootstrapTable('scrollTo', 0);
											$.webeditor.showMsgLabel("warning","已经跳转到第一条");
										} else {
											var index = layerSelected.indexOf(layerOn);
											if (index < 0) {
												$('[data-toggle="layers"]').bootstrapTable('scrollTo',0);
											} else if (index > layerSelected.length - 1) {
												$('[data-toggle="layers"]').bootstrapTable('scrollTo','bottom');
											} else {
												if (index == 0) {
													$('[data-toggle="layers"]').bootstrapTable('scrollTo',layerSelected[0] * 31);
													layerOn = layerSelected[0];
													$.webeditor.showMsgLabel("warning","已经跳转到第一条");
												} else {
													var preIndex = index - 1;
													$('[data-toggle="layers"]').bootstrapTable('scrollTo',layerSelected[preIndex] * 31);
													layerOn = layerSelected[preIndex];
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
									if(layerFirstIn) {
										$('[data-toggle="layers"]').bootstrapTable('scrollTo',layerSelected[0] * 31);
										layerOn = layerSelected[0];
										layerFirstIn = false;
									} else {
										if (layerOn < 0) {
											$('[data-toggle="layers"]').bootstrapTable('scrollTo', 0);
										} else {
											var index = layerSelected.indexOf(layerOn);
											if (index < 0) {
												$('[data-toggle="layers"]').bootstrapTable('scrollTo',0);
											} else if (index > layerSelected.length - 1) {
												$('[data-toggle="layers"]').bootstrapTable('scrollTo','bottom');
											} else {
												if (index == layerSelected.length - 1) {
													var nextIndex = layerSelected.length - 1;
													$('[data-toggle="layers"]').bootstrapTable('scrollTo',layerSelected[nextIndex] * 31);
													layerOn = layerSelected[nextIndex];
													$.webeditor.showMsgLabel("warning","已经跳转到最后一条");
												} else {
													var nextIndex = index + 1;
													$('[data-toggle="layers"]').bootstrapTable('scrollTo',layerSelected[nextIndex] * 31);
													layerOn = layerSelected[nextIndex];
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
									var selections = $('[data-toggle="layers"]').bootstrapTable('getAllSelections');
									var length = selections.length;
									
									if (length > 0) {
										var value = new String();
										$.each(selections, function(index,domEle) {
											value += domEle.name + ";";
										});
										value = value.substring(0, value.length - 1);
										$("#layername").val(value);
										$("#layerscount").text(length);
										
										$(this).dialog("close");
									} else {
										$.webeditor.showMsgLabel("alert","请选择图层");
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
	
	function getItems() {
		$('[data-toggle="items"]').bootstrapTable(
				{
					locale : 'zh-CN',
					queryParams : function(params) {
						return params;
					},
					onLoadSuccess : function(data) {
						var values = new Array();
						$.each($("#dlgItemSet table #items").val().split(";"), function(index, domEle) {
							if(domEle)
								values[index] = domEle;
						});
						if(values.length > 0) {
							$('[data-toggle="items"]').bootstrapTable("checkBy", {
									field : "oid",
									values : values
								});
							var curItemSetID = $("#dlgItemSet table #id").val();
							if(curItemSetID !== "0") {
								$("#dlgItems input:checkbox").attr("disabled", true);
							} else {
								$("#dlgItems input:checkbox").removeAttr("disabled");
							}
						}
					},
					onCheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						if(itemSelected.indexOf(index) < 0) {
							itemOn = index;
							itemSelected.push(index);
							itemSelected.sort(compare);
						}
					},
					onUncheck : function(row, element) {
						var index = parseInt($(element).parent().next().text());
						var indexIn = itemSelected.indexOf(index);
						if(indexIn >= 0) {
							itemOn = itemSelected[indexIn == 0 ? 0 : indexIn -1];
							itemSelected.splice(indexIn,1).sort(compare);
						}
					}
				});
		
		showItemsDlg();
	}
	
	function showItemsDlg() {
		$("#dlgItems").dialog(
				{
					modal : true,
					width : 600,
					title : "选择质检项",
					open : function(event, ui) {
						itemFirstIn = true;
						$(".ui-dialog-titlebar-close").hide();
					},
					close : function(event, ui) {
						itemOn = -1;
						itemSelected = new Array();
						itemFirstIn = true;
						$('[data-toggle="items"]').bootstrapTable("destroy");
					},
					buttons : [
							{
								text : "<",
								title : "上一条",
								class : "btn btn-default",
								click : function() {
									if(itemFirstIn) {
										$('[data-toggle="items"]').bootstrapTable('scrollTo',itemSelected[0] * 31);
										itemOn = itemSelected[0];
										itemFirstIn = false;
									} else {
										if (itemOn < 0) {
											$('[data-toggle="items"]').bootstrapTable('scrollTo', 0);
											$.webeditor.showMsgLabel("warning","已经跳转到第一条");
										} else {
											var index = itemSelected.indexOf(itemOn);
											if (index < 0) {
												$('[data-toggle="items"]').bootstrapTable('scrollTo',0);
											} else if (index > itemSelected.length - 1) {
												$('[data-toggle="items"]').bootstrapTable('scrollTo','bottom');
											} else {
												if (index == 0) {
													$('[data-toggle="items"]').bootstrapTable('scrollTo',itemSelected[0] * 31);
													itemOn = itemSelected[0];
													$.webeditor.showMsgLabel("warning","已经跳转到第一条");
												} else {
													var preIndex = index - 1;
													$('[data-toggle="items"]').bootstrapTable('scrollTo',itemSelected[preIndex] * 31);
													itemOn = itemSelected[preIndex];
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
									if(itemFirstIn) {
										$('[data-toggle="items"]').bootstrapTable('scrollTo',itemSelected[0] * 31);
										itemOn = itemSelected[0];
										itemFirstIn = false;
									} else {
										if (itemOn < 0) {
											$('[data-toggle="items"]').bootstrapTable('scrollTo', 0);
										} else {
											var index = itemSelected.indexOf(itemOn);
											if (index < 0) {
												$('[data-toggle="items"]').bootstrapTable('scrollTo',0);
											} else if (index > itemSelected.length - 1) {
												$('[data-toggle="items"]').bootstrapTable('scrollTo','bottom');
											} else {
												if (index == itemSelected.length - 1) {
													var nextIndex = itemSelected.length - 1;
													$('[data-toggle="items"]').bootstrapTable('scrollTo',itemSelected[nextIndex] * 31);
													itemOn = itemSelected[nextIndex];
													$.webeditor.showMsgLabel("warning","已经跳转到最后一条");
												} else {
													var nextIndex = index + 1;
													$('[data-toggle="items"]').bootstrapTable('scrollTo',itemSelected[nextIndex] * 31);
													itemOn = itemSelected[nextIndex];
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
									var selections = $('[data-toggle="items"]').bootstrapTable('getAllSelections');
									var length = selections.length;
									
									if (length > 0) {
										var value = new String();
										$.each(selections, function(index,domEle) {
											value += domEle.oid + ";";
										});
										value = value.substring(0, value.length - 1);
										$("#items").val(value);
										$("#itemscount").text(length);
										
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
		
		if(name.length <= 0) {
			$.webeditor.showMsgLabel("alert", "图层集合名称不能为空");
			return;
		}
		
		jQuery.post("./itemsetmanage.web",
			{
				"atn" : "submititemset",
				"itemSetID" : id,
				"name" : name,
				"layername" : layername,
				"type" : type,
				"systype" : systype,
				"referdata" : referdata,
				"unit" : unit,
				"desc" : desc,
				"items" : items
			},
			function(json) {
				if (json.result) {
					$.webeditor.showMsgLabel("success","质检集合配置成功");
					$("#dlgItemSet").dialog("close");
					$('[data-toggle="itemsets"]').bootstrapTable('refresh');
				} else {
					$.webeditor.showMsgLabel("alert",json.option);
				}
			}, "json");
	}
	
	function deleteItemSet(itemSetID) {
		if(!itemSetID || itemSetID <= 0) {
			$.webeditor.showMsgLabel("alert", "质检集合删除失败");
			return;
		}
		jQuery.post("./itemsetmanage.web",
			{
				"atn" : "deleteitemset",
				"itemSetID" : itemSetID
			},
			function(json) {
				if (json.result > 0) {
					$.webeditor.showMsgLabel("success","质检集合删除成功");
					$('[data-toggle="itemsets"]').bootstrapTable('refresh');
				} else {
					$.webeditor.showMsgLabel("alert",json.resultMsg);
				}
			}, "json");
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
				data-toggle="itemsets" data-height="714"
				data-page-list="[5, 10, 20, All]" data-page-size="5"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr>
						<th data-field="id" data-filter-control="input" data-width="80"
							data-filter-control-placeholder="">编号
							<button class="btn btn-default btn-xs" onclick="getItemSet(0);">
								<span class="glyphicon glyphicon-plus"></span>
							</button>
						</th>
						<th data-field="name" data-filter-control="input"
							data-filter-control-placeholder="">质检集合名称</th>
						<th data-field="layername" data-filter-control="input"
							data-filter-control-placeholder="">图层</th>
						<th data-field="type" data-formatter="typeFormat"
							data-filter-control="select" data-filter-data="var:itemsetTypes" data-width="80">类型</th>
						<th data-field="systype" data-formatter="sysFormat"
							data-filter-control="select"
							data-filter-data="var:itemsetSysTypes">操作系统</th>
						<th data-field="referdata" data-formatter="referdataFormat"
							data-filter-control="input" data-filter-control-placeholder=""
							data-width="200">参考图层</th>
						<th data-field="unit" data-formatter="unitFormat"
							data-filter-control="select" data-filter-data="var:itemsetUnits">质检单位</th>
						<th data-field="desc" data-filter-control="input"
							data-filter-control-placeholder="">描述</th>
						<!-- <th data-field="updatetime">更新时间</th> -->
						<th data-formatter="operationFormat" data-width="80">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	<div id="dlgItemSet" style="display: none;">
		<table class="table">
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
					<td class="configKey">图层</td>
					<td class="configValue">
						<input type="hidden" id="layername" value="">
						<button type="button" class="btn btn-default"
							onclick="getLayers();">选择图层</button>
						<p class="help-block">已选择<span id="layerscount"></span>个图层</p></td>
				</tr>
				<tr>
					<td class="configKey">质检项</td>
					<td class="configValue">
						<input type="hidden" id="items" value="">
						<button type="button" class="btn btn-default"
							onclick="getItems();">选择质检项</button>
						<p class="help-block">已选择<span id="itemscount"></span>个质检项</p></td>
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
		<table id="layerslist" class="table-condensed" data-unique-id="id"
			data-url="./itemsetmanage.web?atn=getlayers" data-cache="false"
			data-side-pagination="server" data-filter-control="true"
			data-click-to-select="true" data-single-select="false"
			data-select-item-name="checkboxName" data-pagination="false"
			data-toggle="layers" data-height="325"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true"></th>
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
		<table id="itemslist" class="table-condensed" data-unique-id="id"
			data-url="./itemsetmanage.web?atn=getqids" data-cache="false"
			data-side-pagination="server" data-filter-control="true"
			data-click-to-select="true" data-single-select="false"
			data-select-item-name="checkboxName" data-pagination="false"
			data-toggle="items" data-height="325"
			data-search-on-enter-key='true' data-align='center'>
			<thead>
				<tr>
					<th data-field="state" data-checkbox="true"></th>
					<th data-field="index" data-class="indexHidden" data-formatter="indexFormat"></th>
					<th data-field="oid" data-filter-control-placeholder=""
						data-filter-control="input" data-width="80">QID</th>
					<th data-field="name" data-filter-control-placeholder=""
						data-filter-control="input" data-width="260">名称</th>
				</tr>
			</thead>
		</table>
	</div>
</body>
</html>
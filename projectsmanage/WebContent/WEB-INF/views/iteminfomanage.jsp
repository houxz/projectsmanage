<%@page import="com.emg.projectsmanage.common.ItemSetType"%>
<%@page import="com.emg.projectsmanage.common.ItemSetEnable"%>
<%@page import="com.emg.projectsmanage.common.ItemSetSysType"%>
<%@page import="com.emg.projectsmanage.common.ItemSetUnit"%>
<%@page import="com.emg.projectsmanage.common.LayerElement"%>
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
<title>质检项配置</title>

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

		$('[data-toggle="iteminfos"]').bootstrapTable({
			locale : 'zh-CN',
			onLoadSuccess : function(data) {
			}
		});
	});
	
	var itemsetEnables = eval('(${itemsetEnables})');
	var itemsetSysTypes = eval('(${itemsetSysTypes})');
	var itemsetTypes = eval('(${itemsetTypes})');
	var itemsetUnits = eval('(${itemsetUnits})');
	var layerEles = eval('(${layerElements})');
	
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
	function preValueFormat(value, row, index) {
		if(!value)
			return "-";
		var html = new Array();
		html.push("<pre class='bootstrapColumn' >");
		html.push(value);
		html.push("</pre>");
		return html.join("");
	}
	
	function queryParams(params) {
		return params;
	}
	
	function operationFormat(value, row, index) {
		var html = new Array();
		html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="getItemInfo(' + row.id + ');">配置</div>');
		html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="deleteItemInfo(' + row.id + ');">删除</div>');
		return html.join('');
	}
	
	function loadDefaultItemSet() {
		$("#dlgItemInfo table #id").val(0);
		$("#dlgItemInfo table #oid").val(new String());
		$("#dlgItemInfo table #name").val(new String());
		$("#dlgItemInfo table #layername").prop('selectedIndex', 0);
		$("#dlgItemInfo table #enable").prop('selectedIndex', 0);
		$("#dlgItemInfo table #unit").prop('selectedIndex', 0);
		$("#dlgItemInfo table #type").prop('selectedIndex', 0);
		$("#dlgItemInfo table #systype").prop('selectedIndex', 0);
		$("#dlgItemInfo table #referdata").val(new String());
		$("#dlgItemInfo table #module").val(new String());
		$("#dlgItemInfo table #updatetime").val(new String());
	}
	
	function submitItemInfo() {
		var id = $("#dlgItemInfo table #id").val();
		var oid = $("#dlgItemInfo table #oid").val();
		var name = $("#dlgItemInfo table #name").val();
		var layername = $("#dlgItemInfo table #layername").val();
		var enable = $("#dlgItemInfo table #enable").val();
		var unit = $("#dlgItemInfo table #unit").val();
		var type = $("#dlgItemInfo table #type").val();
		var systype = $("#dlgItemInfo table #systype").val();
		var referdata = $("#dlgItemInfo table #referdata").val();
		var module = $("#dlgItemInfo table #module").val();
		
		if(name.length <= 0) {
			$.webeditor.showMsgLabel("alert", "名称不能为空");
			return;
		}
		
		jQuery.post("./iteminfo.web",
			{
				"atn" : "submititeminfo",
				"id" : id,
				"oid" : oid,
				"name" : name,
				"layername" : layername,
				"enable" : enable,
				"unit" : unit,
				"type" : type,
				"systype" : systype,
				"referdata" : referdata,
				"module" : module
			},
			function(json) {
				if (json.result) {
					$.webeditor.showMsgLabel("success", "质检项配置成功");
					$("#dlgItemInfo").dialog("close");
					$('[data-toggle="iteminfos"]').bootstrapTable('refresh');
				} else {
					$.webeditor.showMsgLabel("alert", "质检项配置失败");
				}
			}, "json");
	}
	
	function getItemInfo(itemInfoID) {
		loadDefaultItemSet();
		if (itemInfoID > 0) {
			jQuery.post("./iteminfo.web", {
				"atn" : "getiteminfo",
				"id" : itemInfoID
			}, function(json) {
				if (json.iteminfo) {
					var iteminfo = json.iteminfo;
					
					$("#dlgItemInfo table #id").val(iteminfo.id);
					$("#dlgItemInfo table #oid").val(iteminfo.oid);
					$("#dlgItemInfo table #name").val(iteminfo.name);
					$("#dlgItemInfo table #layername").val(iteminfo.layername);
					$("#dlgItemInfo table #enable").val(iteminfo.enable);
					$("#dlgItemInfo table #unit").val(iteminfo.unit);
					$("#dlgItemInfo table #type").val(iteminfo.type);
					$("#dlgItemInfo table #systype").val(iteminfo.systype);
					$("#dlgItemInfo table #referdata").val(iteminfo.referdata);
					$("#dlgItemInfo table #module").val(iteminfo.module);
					$("#dlgItemInfo table #updatetime").val(iteminfo.updatetime);
				}
			}, "json");
		}
		showItemInfoDlg();
	}
	
	function showItemInfoDlg() {
		$("#dlgItemInfo").dialog(
				{
					modal : true,
					height: 700,
					width : 720,
					title : "质检项配置",
					open : function(event, ui) {
						$(".ui-dialog-titlebar-close").hide();
					},
					buttons : [
							{
								text : "保存",
								class : "btn btn-default",
								click : submitItemInfo
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
	
	function deleteItemInfo(itemInfoID) {
		$.webeditor.showConfirmBox("alert","确定要删除这个质检项吗？", function(){
			if(!itemInfoID || itemInfoID <= 0) {
				$.webeditor.showMsgLabel("alert", "质检项删除失败");
				return;
			}
			jQuery.post("./iteminfo.web", {
				"atn" : "deleteiteminfo",
				"id" : itemInfoID
			}, function(json) {
				if (json.result) {
					$.webeditor.showMsgLabel("success", "质检项删除成功");
					$('[data-toggle="iteminfos"]').bootstrapTable('refresh');
				} else {
					$.webeditor.showMsgLabel("alert", "质检项删除失败");
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
			<table id="iteminfoslist" data-unique-id="id"
				data-query-params="queryParams"
				data-url="./iteminfo.web?atn=pages" data-side-pagination="server"
				data-filter-control="true" data-pagination="true"
				data-toggle="iteminfos" data-height="714"
				data-page-list="[6, 10, 20, All]" data-page-size="6"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr>
						<th data-field="id" data-filter-control="input" data-width="80"
							data-filter-control-placeholder="">序号
							<div class="btn btn-default btn-xs" onclick="getItemInfo(0);">
								<span class="glyphicon glyphicon-plus"></span>
							</div>
						</th>
						
						<th data-field="oid" data-filter-control="input" data-width="140"
							data-formatter="" data-filter-control-placeholder="">质检项编号</th>
							
						<th data-field="name" data-filter-control="input" data-width="140"
							data-formatter="" data-filter-control-placeholder="">质检项名称</th>
							
						<th data-field="layername" data-filter-control="select" data-width="140"
							data-filter-data="var:layerEles">图层</th>
							
						<th data-field="enable" data-formatter="enableFormat" data-width="100"
							data-filter-control="select" data-filter-data="var:itemsetEnables">状态</th>
							
						<th data-field="unit" data-formatter="unitFormat" data-width="100"
							data-filter-control="select" data-filter-data="var:itemsetUnits">质检单位</th>
							
						<th data-field="type" data-formatter="typeFormat" data-width="100"
							data-filter-control="select" data-filter-data="var:itemsetTypes">类型</th>
							
						<th data-field="systype" data-formatter="sysFormat" data-width="120"
							data-filter-control="select" data-filter-data="var:itemsetSysTypes">操作系统</th>
							
						<th data-field="referdata" data-formatter="preValueFormat" data-width="160"
							data-filter-control="input" data-filter-control-placeholder="">参考图层</th>
							
						<th data-field="module" data-formatter="" data-width="100"
							data-filter-control="input" data-filter-control-placeholder="">备注</th>
							
						<th data-field="updatetime" data-width="100">更新时间</th>
						
						<th data-formatter="operationFormat" data-width="70">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	<div id="dlgItemInfo" style="display: none;">
		<table class="table">
			<tbody>
				<tr>
					<td class="configKey">序号</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="id" readonly>
					</td>
				</tr>
				<tr>
					<td class="configKey">质检项编号</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="oid"
						placeholder="请输入质检项编号"></td>
				</tr>
				<tr>
					<td class="configKey">质检项名称</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="name"
						placeholder="请输入质检项名称"></td>
				</tr>
				<tr>
					<td class="configKey">图层</td>
					<td class="configValue"><select
						class="form-control configValue" id="layername">
						<c:set var="layers" value="<%= LayerElement.values() %>"/>
						<c:forEach items="${layers }" var="layer">
							<option value="${layer.toString() }">${layer.toString() }</option>
						</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">状态</td>
					<td class="configValue"><select
						class="form-control configValue" id="enable">
						<c:set var="enables" value="<%= ItemSetEnable.values() %>"/>
						<c:forEach items="${enables }" var="enable">
							<option value="${enable.getValue() }">${enable.getDes() }</option>
						</c:forEach>
					</select></td>
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
						<c:set var="systypes" value="<%= ItemSetSysType.values() %>"/>
						<c:forEach items="${systypes }" var="systype">
							<option value="${systype.getValue() }">${systype.getDes() }</option>
						</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td class="configKey">参考图层</td>
					<td class="configValue">
						<textarea class="form-control " rows="2" id="referdata"></textarea>
					</td>
				</tr>
				<tr>
					<td class="configKey">备注</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="module"
						placeholder="请输入备注"></td>
				</tr>
				<tr>
					<td class="configKey">更新时间</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="updatetime" readonly></td>
				</tr>
			</tbody>
		</table>
	</div>
</body>
</html>
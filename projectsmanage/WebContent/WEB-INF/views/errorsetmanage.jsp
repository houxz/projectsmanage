<%@page import="com.emg.projectsmanage.common.ItemSetType"%>
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
<title>错误筛选配置</title>

<link href="resources/jquery-flexselect-0.9.0/flexselect.css"
	rel="stylesheet">
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
	
	var errorsetSysTypes = eval('(${errorsetSysTypes})');
	var errorsetTypes = eval('(${errorsetTypes})');
	var errorsetUnits = eval('(${errorsetUnits})');
	
	var errorTypesTree = null;
	
	function queryParams(params) {
		return params;
	}
	
	function operationFormat(value, row, index) {
		var html = new Array();
		html.push('<button class="btn btn-default"  style="margin-bottom:3px;" onclick="getErrorSet(' + row.id + ');">配置</button>');
		html.push('<button class="btn btn-default"  style="margin-bottom:3px;" onclick="deleteErrorSet(' + row.id + ');">删除</button>');
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
		$("#dlgErrorSet table #layername").val(new String());
		$("#layerscount").text(0);
		$("#dlgErrorSet table #type").prop('selectedIndex', 0);
		$("#dlgErrorSet table #enable").prop('selectedIndex', 0);
		$("#dlgErrorSet table #systype").prop('selectedIndex', 0);
		$("#dlgErrorSet table #referdata").val(new String());
		$("#dlgErrorSet table #unit").prop('selectedIndex', 0);
		$("#dlgErrorSet table #desc").val(new String());
		$("#dlgErrorSet table #updatetime").val(new String());
	}

	function getErrorSet(errorsetid) {
		loadDefaultErrorSet();
		if (errorsetid > 0) {
			jQuery.post("./errorsetmanage.web", {
				"atn" : "geterrorset",
				"errorsetid" : errorsetid
			}, function(json) {
				if (json.errorset) {
					var errorset = json.errorset;
					
					$("#dlgErrorSet table #id").val(errorset.id);
					$("#dlgErrorSet table #name").val(errorset.name);
					$("#dlgErrorSet table #layername").val(errorset.layername);
					$("#layerscount").text(errorset.layername.split(";").length);
					$("#dlgErrorSet table #type").val(errorset.type);
					$("#dlgErrorSet table #enable").val(errorset.enable);
					$("#dlgErrorSet table #systype").val(errorset.systype);
					$("#dlgErrorSet table #referdata").val(errorset.referdata);
					$("#dlgErrorSet table #unit").val(errorset.unit);
					$("#dlgErrorSet table #desc").val(errorset.desc);
					$("#dlgErrorSet table #updatetime").val(errorset.updatetime);
					$("#dlgErrorSet table #items").val(json.itemDetails);
					$("#itemscount").text(json.itemDetails ? json.itemDetails.split(";").length : 0);
				}
			}, "json");
		}
		showErrorSetDlg();
	}

	
	function showErrorSetDlg() {
		$("#dlgErrorSet").dialog(
				{
					modal : true,
					height: 600,
					width : document.documentElement.clientWidth * 0.4,
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
	
	function findExpandibleNodess() {
    	if(errorTypesTree) {
    		errorTypesTree.treeview('uncheckAll', { silent: true });
    		errorTypesTree.treeview('collapseAll', { silent: true });
    		return errorTypesTree.treeview('search', [ $('#input-expand-node').val(), { ignoreCase: false, exactMatch: false } ]);
    	} else {
    		return null;
    	}
        
    }
    function checkExpandibleNodess() {
    	if(errorTypesTree) {
        	var expandibleNodes = findExpandibleNodess();
        	errorTypesTree.treeview('checkNode', [ expandibleNodes, { silent: false }]);
    	}
    }
	
	function getErrorTypes() {
		if(!errorTypesTree) {
			jQuery.post("./errorsetmanage.web", {
				"atn" : "geterrortypes"
			}, function(json) {
				var errorTypes = json.rows;
				if(errorTypes && errorTypes.length > 0) {//data, idStr, pidStr, pText, childrenStr, cText
					var data = $.webeditor.transJsonData2Tree2(errorTypes, "id", "qid", "name", "nodes", "desc");
					errorTypesTree = $('#errorTypesTree').treeview(
						{
							data : data,
							showIcon : false,
							showCheckbox : true,
							highlightSelected : false,
							expandIcon : 'glyphicon glyphicon-menu-right',
							collapseIcon : 'glyphicon glyphicon-menu-down',
							onNodeChecked : function(event, node) {
								if(node.pid == 0) {
                            		var children = node.nodes;
                            		$.each(children, function(index, child){
                            			errorTypesTree.treeview('checkNode', [ child.nodeId, { silent: false }]);
                            		});
                            	}
							},
							onNodeUnchecked : function(event, node) {
								if(node.pid == 0) {
                            		var children = node.nodes;
                            		$.each(children, function(index, child){
                            			errorTypesTree.treeview('uncheckNode', [ child.nodeId, { silent: false }]);
                            		});
                            	}
							}
						});
				}
			}, "json");
		} else {
		}
		
		showErrorTypesDlg();
	}

	function showErrorTypesDlg() {
		$("#dlgErrorTypes").dialog({
			modal : true,
			width : document.documentElement.clientWidth * 0.4,
			height: 600,
			title : "选择质检项",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			buttons : [ {
				text : "提交",
				class : "btn btn-default",
				click : function() {
					var expandibleNodes = new Array();
		        	if(errorTypesTree)
		        		expandibleNodes = errorTypesTree.treeview('getChecked');
		        	if(expandibleNodes && expandibleNodes.length > 0) {
			        	var length = 0;
			        	var value = new String();
			        	$.each(expandibleNodes, function(index, node){
			        		if(node.id) {
			        			value += node.id + ";";
			        			length ++;
			        		}
	            		});
			        	value = value.substring(0, value.length - 1);
			        	$("#dlgErrorSet table #items").val(value);
			        	$("#itemscount").text(length);
			        	
			        	errorTypesTree.treeview('uncheckAll', { silent: true });
			    		errorTypesTree.treeview('collapseAll', { silent: true });
			        	$(this).dialog("close");
		        	} else {
		        		$.webeditor.showMsgLabel("alert", "请选择错误类型");
		        		return;
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

	function submitErrorSet() {
		var id = $("#dlgErrorSet table #id").val();
		var name = $("#dlgErrorSet table #name").val();
		var layername = $("#dlgErrorSet table #layername").val();
		var type = $("#dlgErrorSet table #type").val();
		var enable = $("#dlgErrorSet table #enable").val();
		var systype = $("#dlgErrorSet table #systype").val();
		var referdata = $("#dlgErrorSet table #referdata").val();
		var unit = $("#dlgErrorSet table #unit").val();
		var desc = $("#dlgErrorSet table #desc").val();
		var items = $("#dlgErrorSet table #items").val();

		if (name.length <= 0) {
			$.webeditor.showMsgLabel("alert", "错误筛选集合名称不能为空");
			return;
		}

		jQuery.post("./errorsetmanage.web", {
			"atn" : "submiterrorset",
			"errorSetID" : id,
			"name" : name,
			"layername" : layername,
			"type" : type,
			"enable" : enable,
			"systype" : systype,
			"referdata" : referdata,
			"unit" : unit,
			"desc" : desc,
			"items" : items
		}, function(json) {
			if (json.result) {
				$.webeditor.showMsgLabel("success", "质检集合配置成功");
				$("#dlgErrorSet").dialog("close");
				$('[data-toggle="errorsets"]').bootstrapTable('refresh');
			} else {
				$.webeditor.showMsgLabel("alert", json.resultMsg);
			}
		}, "json");
	}

	function deleteErrorSet(errorSetID) {
		if (!errorSetID || errorSetID <= 0) {
			$.webeditor.showMsgLabel("alert", "质检集合删除失败");
			return;
		}
		jQuery.post("./errorsetmanage.web", {
			"atn" : "deleteerrorset",
			"errorSetID" : errorSetID
		}, function(json) {
			if (json.result > 0) {
				$.webeditor.showMsgLabel("success", "质检集合删除成功");
				$('[data-toggle="errorsets"]').bootstrapTable('refresh');
			} else {
				$.webeditor.showMsgLabel("alert", json.resultMsg);
			}
		}, "json");
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
							data-filter-control-placeholder="">编号
							<button class="btn btn-default btn-xs" onclick="getErrorSet(0);">
								<span class="glyphicon glyphicon-plus"></span>
							</button>
						</th>
						<th data-field="name" data-filter-control="input"
							data-filter-control-placeholder="">错误筛选集合名称</th>
						<th data-field="type" data-formatter="typeFormat"
							data-filter-control="select" data-filter-data="var:errorsetTypes">类型</th>
						<th data-field="systype" data-formatter="sysFormat"
							data-filter-control="select"
							data-filter-data="var:errorsetSysTypes">操作系统</th>
						<th data-field="referdata" data-filter-control="input"
							data-filter-control-placeholder="" data-width="200">参考图层</th>
						<th data-field="unit" data-formatter="unitFormat"
							data-filter-control="select" data-filter-data="var:errorsetUnits">质检单位</th>
						<th data-field="desc" data-filter-control="input"
							data-filter-control-placeholder="">描述</th>
						<!-- <th data-field="updatetime">更新时间</th> -->
						<th data-formatter="operationFormat">操作</th>
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
						class="form-control configValue" id="id" disabled>
					</td>
				</tr>
				<tr>
					<td class="configKey">错误筛选集合名称</td>
					<td class="configValue"><input type="text"
						class="form-control configValue" id="name"
						placeholder="请输入集合名"></td>
				</tr>
				<tr>
					<td class="configKey">质检项</td>
					<td class="configValue">
						<input type="hidden" id="items" value="">
						<button type="button" class="btn btn-default"
							onclick="getErrorTypes();">选择质检项</button>
						<p class="help-block">已选择<span id="itemscount"></span>个质检项</p></td>
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
					<td class="configKey">参考图层</td>
					<td class="configValue">
						<textarea class="form-control" rows="3" id="referdata"></textarea>
					</td>
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
						class="form-control configValue" id="updatetime" disabled></td>
				</tr>
			</tbody>
		</table>
	</div>
	<div id="dlgErrorTypes" style="display: none;">
		<div class="">
			<div class="panel panel-default" style="margin-bottom: 0;">
				<div class="panel-body">
					<div class="input-group">
						<input type="input" class="form-control" id="input-expand-node"
							placeholder="查找人员"
							onkeypress="if (event.keyCode==13) { findExpandibleNodess(); }">
						<div class="input-group-btn">
							<button type="button" class="btn btn-default" tabindex="-1"
								onclick="findExpandibleNodess();">
								<span class="glyphicon glyphicon-search" aria-hidden="true">&nbsp;</span>查找
							</button>
							<button type="button" class="btn btn-default" tabindex="-1"
								onclick="checkExpandibleNodess();">
								<span class="glyphicon glyphicon-check" aria-hidden="true">&nbsp;</span>勾选
							</button>
						</div>
					</div>
				</div>
				<div id="errorTypesTree" style="height: 406px; overflow: auto;"></div>
			</div>
		</div>
	</div>
</body>
</html>
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

<link href="resources/jquery-ui-1.12.1.custom/jquery-ui.min.css" rel="stylesheet">
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

	function statesFormat(value, row, index) {
		return processStates[row.state];
	}

	function progressFormat(value, row, index) {
		var values = value.split(',');
		var html = new Array();
		for ( var i = 0; i < values.length; i++) {
			html.push('<div>');
			var v = values[i];
			if (v > 0 && v < 100 && row.state == 1)
				html.push('<div class="progress progress-striped active" style="margin-bottom: 3px;">');
			else
				html.push('<div class="progress" style="margin-bottom: 3px;">');
			html.push('<div class="progress-bar progress-bar-warning" role="progressbar"'
							+ ' aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: ' + v + '%;background-color: ' + colors[i] + ';">'
							+ ' <span style="margin:0 6px;color: black;">' + v + '&#8453;</span>' + ' </div>');
			html.push('</div></div>');
		}

		return html.join('');
	}
	
	function changeState(state, processid) {
		
	}

	function operationFormat(value, row, index) {
		var html = new Array();
		html.push('<button class="btn btn-default"  style="margin-bottom:3px;" onclick="getConfig(' + row.id + ');">配置</button>');
		if (row.state == 1) {
			html.push('<button class="btn btn-default"  style="margin-bottom:3px;" onclick="changeState(2,' + row.id + ')">暂停</button>');
		} else if (row.state == 2 || row.state == 0) {
			html.push('<button class="btn btn-default" style="margin-bottom:3px;" onclick="changeState(1,' + row.id + ')" >开始</button>');
		}

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
	
	function getConfig(processid) {
		
		showConfigDlg(processid);
	}
	
	function showConfigDlg(processid) {
		$("#configDlg").dialog({
			modal : true,
			height : document.documentElement.clientHeight * 0.8,
			width : document.documentElement.clientWidth * 0.4,
			title : "流程配置",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			buttons : [ {
				text : "提交",
				class: "btn btn-default",
				click : function() {
				}
			}, {
				text : "关闭",
				class: "btn btn-default",
				click : function() {
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
							<button class="btn btn-default btn-xs" title="新建流程" onclick="getConfig(0);">
								<span class="glyphicon glyphicon-plus"></span>
							</button>
						</th>
						<th data-field="name" data-filter-control="input"
							data-filter-control-placeholder="" data-width="120">流程名称</th>
						<th data-field="userid" data-filter-control="input"
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
	</div>
</body>
</html>
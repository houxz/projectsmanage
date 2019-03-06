<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>错误清单导出</title>
<meta name="robots" content="none">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css"
	rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />
<link href="resources/bootstrap-table-1.11.1/bootstrap-table.min.css"
	rel="stylesheet">
<link href="resources/jquery-ui-1.12.1.custom/jquery-ui.min.css" rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/bootstrap-table.min.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script
	src="resources/bootstrap-table-1.11.1/extensions/filter-control/bootstrap-table-filter-control.min.js"></script>
<script
	src="resources/bootstrap-table-1.11.1/locale/bootstrap-table-zh-CN.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$.webeditor.getHead();
		$.webeditor.showMsgBox("info", "数据加载中，请稍候...");
		$('[data-toggle="worktasks"]').bootstrapTable({
			locale : 'zh-CN',
			onSearch : function (text) {
				$.webeditor.showMsgBox("info", "数据加载中，请稍候..."); 
			},
			onPageChange :function (number, size) {
				$.webeditor.showMsgBox("info", "数据加载中，请稍候...");
			},
			onLoadSuccess : function (data) {
				$.webeditor.showMsgBox("close");
			},
			onLoadError : function (status) {
				$.webeditor.showMsgBox("close");
			},
			
			onPostHeader : function () {
				var obj = $(".form-control.bootstrap-table-filter-control-batchid");
				$(obj).change(function() {
					var batchid = $(".form-control.bootstrap-table-filter-control-batchid").val();
					$("#strbatchid").val(batchid);
				});
			}
		});
	});
	
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
</script>
</head>
<body>
	<div class="container">
		<div id="headdiv"></div>
			<div class="input-group" style="width: 100%;">
		</div>
		<div id="dlgInfo" class="row" style="padding-top: 20px">
			<table id="worktasklist" data-unique-id="qid" 
				data-query-params="queryParams" data-url="./errorlistexport.web?atn=pages"
				data-side-pagination="server" data-filter-control="true"
				data-pagination="true" data-toggle="worktasks" data-height="714"
				data-page-list="[10, 20, 50, 100]" data-page-size="10"
				data-search-on-enter-key='true' data-align='center'> 
				<thead>
					<tr>
						<th data-field="batchid" data-filter-control="input" 
							data-filter-control-placeholder="" data-width="120" >批次ID
							<form method="post" action="./errorlistexport.web" id="exportPOIsForm" class="btn btn-default btn-xs">
								<input id="strbatchid" name="batchid" type="hidden">
								<div  title="导出Excel">
									<span class="glyphicon glyphicon-plus" onclick="$('#exportPOIsForm').submit();"></span>
								</div>
							</form>
						</th>
						<th data-field="qid"  data-filter-control-placeholder=""
							data-filter-control="input" data-width="80">QID</th>
						<th data-field="errortype" data-filter-control="input"
							data-filter-control-placeholder="" data-width="60">错误类型</th>
						<th data-field="errorremark" data-filter-control="input"
							data-filter-control-placeholder="" data-width="200">错误备注</th>
						<th data-field="updatetime" data-filter-control="input"
							data-filter-control-placeholder="" data-width="150">更新时间</th>
						<th data-field="countnum" data-filter-control="input" data-width="100">数量</th>
					</tr>
				</thead>
			</table>
		</div>

		<div id="footdiv"></div>
	</div>
</body>
</html>
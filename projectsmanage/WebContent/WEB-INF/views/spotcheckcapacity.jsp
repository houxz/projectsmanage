<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<title>抽检统计</title>
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
	var roleTypes = {5:"制作",6:"校正"};

	$(document).ready(function() {
		$.webeditor.getHead();
		$('[data-toggle="spotcheckcapacity"]').bootstrapTable({
			locale : 'zh-CN'
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
		<div class="row" style="padding-top: 20px">
			<table id="spotcheckcapacitylist" data-unique-id="id"
				data-query-params="queryParams" data-url="./spotcheckcapacity.web?atn=pages"
				data-side-pagination="server" data-filter-control="true"
				data-pagination="true" data-toggle="spotcheckcapacity" data-height="714"
				data-page-list="[10, 30, 50, 100, all]" data-page-size="10"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr>
						<th data-field="id" data-width="60"
							data-filter-control="input" data-filter-control-placeholder="">编号</th>
							
						<th data-field="processid" data-width="160" 
							data-filter-control="input" data-filter-control-placeholder="">项目编号</th>
							
						<th data-field="processname" data-width="260"
							data-filter-control="input" data-filter-control-placeholder="">项目名称</th>
							
						<th data-field="username" data-width="80"
							data-filter-control="input" data-filter-control-placeholder="">被抽检人</th>
						
						<th data-field="errorcount" data-sortable="true">错误数</th>
						
					</tr>
				</thead>
			</table>
		</div>
	</div>
</body>
</html>
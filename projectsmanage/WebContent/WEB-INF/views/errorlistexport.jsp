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
<link href="resources/bootstrap-datetimepicker/css/bootstrap-datetimepicker.css" rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/bootstrap-table.min.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script
	src="resources/bootstrap-table-1.11.1/extensions/filter-control/bootstrap-table-filter-control.min.js"></script>
<script
	src="resources/bootstrap-table-1.11.1/locale/bootstrap-table-zh-CN.js"></script>

<script type="text/javascript" src="resources/bootstrap-datetimepicker/js/bootstrap-datetimepicker.js" charset="UTF-8"></script>
<script type="text/javascript" src="resources/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN.js" charset="UTF-8"></script>
<script type="text/javascript">
    var strparams;
	$(document).ready(function() {
		$.webeditor.getHead();
		
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
					
					if(strparams != undefined){
						if (strparams.filter != undefined && strparams.filter.length > 0) {
							var filterObj = eval('(' + strparams.filter + ')');
							if(filterObj != undefined){
								filterObj.batchid = batchid;
								strparams.filter = JSON.stringify(filterObj);
							} else {
								var filterObj = {batchid : batchid};
								strparams.filter = JSON.stringify(filterObj);
							}
						} else {
							var filterObj = {batchid : batchid};
							strparams.filter = JSON.stringify(filterObj);
						}
					}
				});
				var objqid = $(".form-control.bootstrap-table-filter-control-qid");
				$(objqid).change(function() {
					var qid = $(".form-control.bootstrap-table-filter-control-qid").val();
					$("#strqid").val(qid);
					
					if(strparams != undefined){
						if (strparams.filter != undefined && strparams.filter.length > 0) {
							var filterObj = eval('(' + strparams.filter + ')');
							if(filterObj != undefined){
								filterObj.qid = qid;
								strparams.filter = JSON.stringify(filterObj);
							} else {
								var filterObj = {qid : qid};
								strparams.filter = JSON.stringify(filterObj);
							}
						} else {
							var filterObj = {qid : qid};
							strparams.filter = JSON.stringify(filterObj);
						}
					}
				});
				var objerrortype = $(".form-control.bootstrap-table-filter-control-errortype");
				$(objerrortype).change(function() {
					var errortype = $(".form-control.bootstrap-table-filter-control-errortype").val();
					$("#strerrortype").val(errortype);
					
					if(strparams != undefined){
						if (strparams.filter != undefined && strparams.filter.length > 0) {
							var filterObj = eval('(' + strparams.filter + ')');
							if(filterObj != undefined){
								filterObj.errortype = errortype;
								strparams.filter = JSON.stringify(filterObj);
							} else {
								var filterObj = {errortype : errortype};
								strparams.filter = JSON.stringify(filterObj);
							}
						} else {
							var filterObj = {errortype : errortype};
							strparams.filter = JSON.stringify(filterObj);
						}
					}
				});
				var objerrorremark = $(".form-control.bootstrap-table-filter-control-errorremark");
				$(objerrorremark).change(function() {
					var errorremark = $(".form-control.bootstrap-table-filter-control-errorremark").val();
					$("#strerrorremark").val(errorremark);
					
					if(strparams != undefined){
						if (strparams.filter != undefined && strparams.filter.length > 0) {
							var filterObj = eval('(' + strparams.filter + ')');
							if(filterObj != undefined){
								filterObj.errorremark = errorremark;
								strparams.filter = JSON.stringify(filterObj);
							} else {
								var filterObj = {errorremark : errorremark};
								strparams.filter = JSON.stringify(filterObj);
							}
						} else {
							var filterObj = {errorremark : errorremark};
							strparams.filter = JSON.stringify(filterObj);
						}
					}
				});
				
				$('#updatetime1').datetimepicker({
					autoclose : true,
					language : 'zh-CN'
				}).on('changeDate',function(ev){
					var updatetime11 = $("#updatetime1").val();
					$("#strupdatetime11").val(updatetime11);
					if(strparams != undefined){
						if (strparams.filter != undefined && strparams.filter.length > 0) {
							var filterObj = eval('(' + strparams.filter + ')');
							if(filterObj != undefined){
								filterObj.updatetime1 = $("#updatetime1").val();
								strparams.filter = JSON.stringify(filterObj);
							} else {
								var filterObj = {updatetime1 : $("#updatetime1").val()};
								strparams.filter = JSON.stringify(filterObj);
							}
						} else {
							var filterObj = {updatetime1 : $("#updatetime1").val()};
							strparams.filter = JSON.stringify(filterObj);
						}
					}
					
				});
				
				$('#updatetime2').datetimepicker({
					autoclose : true,
					language : 'zh-CN'
				}).on('changeDate',function(ev){
					var updatetime22 =$("#updatetime2").val();
					$("#strupdatetime22").val(updatetime22);
					if(strparams != undefined){
						if (strparams.filter != undefined && strparams.filter.length > 0) {
							var filterObj = eval('(' + strparams.filter + ')');
							if(filterObj != undefined){
								filterObj.updatetime2 = $("#updatetime2").val();
								strparams.filter = JSON.stringify(filterObj);
							} else {
								var filterObj = {updatetime1 : $("#updatetime1").val()};
								strparams.filter = JSON.stringify(filterObj);
							}
						} else {
							var filterObj = {updatetime2 : $("#updatetime2").val()};
							strparams.filter = JSON.stringify(filterObj);
						}
					}
				});
			}
		});
	});
	
	function queryParams(params) {
		if (params.filter != undefined) {
			var filterObj = eval('(' + params.filter + ')');
			
			filterObj["updatetime1"] = $("#updatetime1").val();
			filterObj["updatetime2"] = $("#updatetime2").val();
			params.filter = JSON.stringify(filterObj);
		}
		strparams = params;
		return params;
	}
</script>
</head>
<body>
	<div class="container">
		<div id="headdiv"></div>
		<div id="dlgInfo" class="row" style="padding-top: 20px">
			<table id="worktasklist" data-unique-id="qid" 
				data-query-params="queryParams" data-url="./errorlistexport.web?atn=pages"
				data-side-pagination="server" data-filter-control="true"
				data-pagination="true" data-toggle="worktasks" data-height="714" 
				data-page-list="[10, 20, 50, 100]" data-page-size="10"
				data-search-on-enter-key='true' data-align='center' > 
				<thead>
					<tr>
						<th data-field="batchid" data-filter-control="input" 
							data-filter-control-placeholder="" data-width="120" >批次ID
							<button type="button" class="btn btn-default btn-xs" title="导出到Excel">
								<span class="glyphicon glyphicon-floppy-save" onclick="$('#exportPOIsForm').submit();"></span>
							</button>
						</th>
						<th data-field="qid"  data-filter-control-placeholder=""
							data-filter-control="input" data-width="80">QID</th>
						<th data-field="errortype" data-filter-control="input"
							data-filter-control-placeholder="" data-width="60">错误类型</th>
						<th data-field="errorremark" data-filter-control="input"
							data-filter-control-placeholder="" data-width="200">错误备注</th>
						<th data-field="updatetime" 
							 data-width="150" >更新时间
							<br>
							<input id="updatetime1" data-field="updatetime1" type="text" placeholder="请输入开始时间" style="width: 140px; height: 34px; padding: 6px 12px; font-size: 14px; line-height: 1.42857143; color: #555; border: 1px solid #ccc; border-radius: 4px;">
							~
							<input id="updatetime2" data-field="updatetime2" type="text" placeholder="请输入结束时间" style="width: 140px; height: 34px; padding: 6px 12px; font-size: 14px; line-height: 1.42857143; color: #555; border: 1px solid #ccc; border-radius: 4px;">
						</th>
						<th data-field="countnum" data-filter-control="input" data-width="100" >数量</th>
					</tr>
				</thead>
			</table>
		</div>

		<div id="footdiv"></div>
	</div>
	<form method="post" action="./errorlistexport.web" id="exportPOIsForm">
		<input id="strbatchid" name="batchid" type="hidden">
		<input id="strqid" name="qid" type="hidden">
		<input id="strerrortype" name="errortype" type="hidden">
		<input id="strerrorremark" name="errorremark" type="hidden">
		<input id="strupdatetime11" name="updatetime11" type="hidden">
		<input id="strupdatetime22" name="updatetime22" type="hidden">
	</form>
</body>
</html>
<%@page import="com.emg.poiwebeditor.common.ProcessType"%>
<%@page import="com.emg.poiwebeditor.common.ModelEnum"%>
<%@page import="com.emg.poiwebeditor.common.ProcessEditType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<title>抽检项目</title>
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
<script src="resources/js/common.js"></script>
<script src="resources/js/consMap.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/bootstrap-table.min.js"></script>
<script src="resources/js/bootstrapDialog.js"></script>
<script
	src="resources/bootstrap-table-1.11.1/extensions/filter-control/bootstrap-table-filter-control.min.js"></script>
<script
	src="resources/bootstrap-table-1.11.1/locale/bootstrap-table-zh-CN.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$.webeditor.getHead();
		$('[data-toggle="processes"]').bootstrapTable({
			locale : 'zh-CN',
			onLoadSuccess : function(data) {
				$("[data-toggle='tooltip']").tooltip();
			},
			onClickRow:function(row){
				//查询id对应的任务
				var processid = row.id;
				$("#spotcheckeditid").bootstrapTable('destroy');
				$('[data-toggle="processes2"]').bootstrapTable({
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
							params["processid"] = processid;
							return params;
						},
						onLoadSuccess : function(data) {
						$("[data-toggle='tooltip']").tooltip();
					}
				})

				
			
			}//onClickRow:function(row)
		});
		
		$('[data-toggle="processes2"]').bootstrapTable({
			locale : 'zh-CN',
			onLoadSuccess : function(data) {
				$("[data-toggle='tooltip']").tooltip();
			},
			onClickRow:function(row){
				alert("bbb");
			}
		});
		
		
	});
	
	function drawspotchecktask(tasklist){
		var tbody = $("#spotcheckeditid tbody");
		tbody.empty();
		
		if(tasklist == null)
			return;
		var count = tasklist.length;
		var html = new Array();
		for(var i = 0 ;i < count ; i++){
			var taskinfo = tasklist[i];
		
			html.push("<tr>");
			html.push('<td class="tdValue" data-key="processid">'+taskinfo.processid+'</td>');
			html.push('<td class="tdValue" data-key="editid">'+taskinfo.editid+'</td>');
			html.push('<td class="tdValue" data-key="name">'+taskinfo.username+'</td>');
			html.push('<td class="tdValue" data-key="editnum">'+taskinfo.editnum+'</td>');
			html.push('<td class="tdValue" data-key="percent"><input class="form-control input-sm" type="text"></td>');
			html.push('<td class="btn btn-default" style="margin-bottom:3px;" onclick="getspotchecktaskinfo(this)">'+"查看"+'</td>');
			html.push("</tr>");
		}
		tbody.append(html.join(""));
	}
	

	function nameFormat(value, row, index) {
		var html = new Array();
		html.push("<div class='bootstrapColumn' >");
		html.push(value);
		html.push("</div>");
		return html.join("");
	}
	
	function operationFormat(value, row, index) {
		var html = new Array();
		html.push('<div class="btn btn-default"  style="margin-bottom:3px;" onclick="getspotchecktaskinfo('
						+ row.processid
						+ ','
						+ row.editid
						+ ',\''
						+ row.username
						+ '\''
						+ ');">查看</div>');
		
		return html.join('');
	}
	
	function percentFormat(value, row, index) {
		var html = new Array();
		html.push('<div><input type="text"></div>');
		return html.join('');
	}
	
	function getspotchecktaskinfo( processid,editid,username ) { 
		 $("#datasetsDlg").bootstrapDialog({
				queryParams : function(params) {
					if (params.filter != undefined) {
						var filterObj = eval('(' + params.filter + ')');
						if (filterObj.state != undefined) {
							filterObj["state"] = filterObj.state;
							delete filterObj.state;
							params.filter = JSON.stringify(filterObj);
						}
					}
					params["processid"] = processid;
					params["editid"] = editid;
					params["username"]=username;
					return params;
				},
				onLoadSuccess : function(data) {
					$(this.self).bootstrapTable("load", data.rows);
					
					var state = $("#config_processstatus").val();
					if(state !== "0") {
						$(this.self).find("input:checkbox:checked").attr("disabled", true);
					} else {
						$(this.self).find("input:checkbox").removeAttr("disabled");
					} 
				}
			}, {
				width : document.documentElement.clientWidth * 0.8,
				title : "项目人员抽查信息",
				buttons : [
					{
						text : "<",
						title : "上一条",
						class : "btn btn-default",
						click : function() {
							$(this).find("table").bootstrapTable("gotoLast");
						}
					},
					{
						text : ">",
						title : "下一条",
						class : "btn btn-default",
						click : function() {
							$(this).find("table").bootstrapTable("gotoNext");
						}
					},
					{
						text : "关闭",
						"class" : "btn btn-default",
						click : function() {
							$(this).dialog("close");
						}
					}
				]
			});
	}
	
	function createproject(){
//		showtip();
		var tbodytrs = $("#spotcheckeditid tbody>tr");
		var array = new Array();
		var iscancreate = true;
		for( var i = 0 ;i <tbodytrs.length;i++){
			if(tbodytrs[i].children[4].firstChild.firstChild.value == "")
				continue;
			
			var obj = new Object();
			obj.processid =	tbodytrs[i].children[0].innerText;
			obj.editid    = tbodytrs[i].children[1].innerText;
			obj.username  = tbodytrs[i].children[2].innerText;
			obj.editnum   = tbodytrs[i].children[3].innerText;
			obj.percent   = tbodytrs[i].children[4].firstChild.firstChild.value;
			if(obj.percent <0 || obj.percent>100){
				iscancreate = false;
				break;
			}
			array.push(obj);
			
		}
		
		if(array.length ==0){
			$.webeditor.showMsgLabel("alert","请填写抽检比例!");
			return;
		}
		
		if( iscancreate){
			jQuery.post("./checkprocessesmanage.web", {
				"atn" : "createspotchecktask",
				"spotchekcinfos" : JSON.stringify(array),
				 beforeSend:function () {
		                showLoading()
		            },
		            complete:function(){
// 		                hideLoading()
		            }
			}, function(json) {
				if (json && json.result > 0) {
					
					for( var i = 0 ;i <tbodytrs.length;i++){
						if(tbodytrs[i].children[4].firstChild.firstChild.value == "")
							continue;
						 tbodytrs[i].children[4].firstChild.firstChild.value ="";	
					}
					$.webeditor.showMsgLabel("success", "抽查任务创建成功");
				} else {
					$.webeditor.showMsgLabel("alert", "抽查任务创建失败");
				}
				$.webeditor.showMsgBox("close");
				
// 				{
// 					 console.log(json)
// 	         		 var _html = ' ';
// 	                var list = res.data;
// 	                var tpl = $('#task_list_tpl').html();
// 	                for (var i = 0, len = list.length; i < len; i++) {
// 	                    var item = list[i];
// 	                    _html += renderTpl(tpl, item)
// 	                }
// 	                $('.ranger-box2 tbody').html(_html);
// 				}
hideLoading();
				
				
			}, "json");
		}else{
			$.webeditor.showMsgLabel("alert","填写抽检比例不对!");
		}
		
	}
	

	showLoading = function(){
	　　　　$('#loadingModal').modal({backdrop: 'static', keyboard: false});
	　　}
	　　hideLoading = function(){
	　　　　$('#loadingModal').modal('hide');
	　　}

	function showtip(){
		 $.ajax({
	            url: 'checkprocessesmanage.web&&' + 'run_task/',
	            data: {},
	            beforeSend:function () {
	                showLoading()
	            },
	            complete:function(){
	                hideLoading()
	            },
	            success: function (res) {
	                console.log(res.data)
	         		 var _html = ' ';
	                var list = res.data;
	                var tpl = $('#task_list_tpl').html();
	                for (var i = 0, len = list.length; i < len; i++) {
	                    var item = list[i];
	                    _html += renderTpl(tpl, item)
	                }
	                $('.ranger-box2 tbody').html(_html);
	            }
	        })
	}
	
</script>

</head>
<body>
	<div class="container-fluid">
		<div class="row">
			<div class="col-md-4">
				<div id="headdiv"></div>
				<div class="row" style="padding-top: 120px; padding-left: 20px">
					<table id="processeslist" data-unique-id="id"
						data-url="./checkprocessesmanage.web?atn=pages"
						data-side-pagination="server" data-filter-control="true"
						data-pagination="true" data-toggle="processes" data-height="714"
						data-page-list="[5, 10, 20, 100]" data-page-size="5"
						data-search-on-enter-key='true' data-align='center'>
						<thead>
							<tr>
								<th data-field="id" data-filter-control="input"
									data-filter-control-placeholder="" data-width="120">项目编号</th>
								<th data-field="name" data-filter-control="input"
									data-filter-control-placeholder="" data-width="160">项目名称</th>
							</tr>
						</thead>
					</table>
				</div>
				<div id="footdiv"></div>
			</div>

			<!-- 		<div class="col-md-1"></div> -->

			<div class="col-md-7">
				<div id="headdiv"></div>
				<div class="row" style="padding-top: 120px; padding-left: 20px">
					<table id="spotcheckeditid" data-unique-id="id"
						data-url="./checkprocessesmanage.web?atn=pages2"
						data-side-pagination="server" data-filter-control="false"
						data-pagination="true" data-toggle="processes2" data-height="714"
						data-page-list="[5, 10, 20, 100]" data-page-size="5"
						data-search-on-enter-key='true' data-align='center'>
						<thead>
							<tr>
								<th data-field="processid">项目编号</th>
								<th data-field="editid">人员ID</th>
								<th data-field="username">人员</th>
								<th data-field="editnum">修改资料数</th>
								<th data-formatter="percentFormat" data-width="70" >抽检比例</th>
								<th data-formatter="operationFormat" data-width="70">抽检信息</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>

				</div>
				<div id="footdiv"></div>
				<div
							style="position: absolute; left: 0; right: 0; bottom: 0px; height: 5px; text-align: right;">
							<button class="btn btn-default" onClick="createproject();">创建抽检项目</button>
							
				</div>
			</div>
		</div>
	</div>

	<div id="datasetsDlg" style="display: none;">
		<table id="datasetslist" class="table-condensed" data-unique-id="id"
			data-value-band="config_2_25"
			data-url="./checkprocessesmanage.web?atn=getdatasets"
			data-toggle="datasets" data-height="520">
			<thead>
				<tr>
					<th data-field="id" data-width="50">编号</th>

					<th data-field="processid" data-width="50">被抽查项目编号</th>
					<th data-field="username" data-formatter="nameFormat"
						data-width="50">被抽查人员</th>
					<th data-field="newprocessid" data-width="60">新生成的项目编号</th>

					<th data-field="percent" data-width="50">抽查比例</th>
				</tr>
			</thead>
		</table>
	</div>
	
	<div class="modal fade" id="loadingModal" backdrop="static" keyboard="false">
　　<div style="width: 200px;height:200px; z-index: 20000; position: absolute; text-align: center; left: 50%; top: 50%;margin-left:-100px;margin-top:-10px">
<!-- 　　　　<div class="progress progress-striped active" style="margin-bottom: 0;"> -->
<!--         ![loadding](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tYWdpY2JveC5iay50ZW5jZW50LmNvbS9zdGF0aWNfYXBpL3YzL2NvbXBvbmVudHMvbG9hZGluZzEvaW1hZ2VzL2xvYWRpbmdfMl8yNHgyNC5naWY) 数据加载中,请稍候... -->
<!-- 　　　　</div> -->

		<div class="progress progress-striped active" style="margin-bottom: 0;">
   			<image src="https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tYWdpY2JveC5iay50ZW5jZW50LmNvbS9zdGF0aWNfYXBpL3YzL2NvbXBvbmVudHMvbG9hZGluZzEvaW1hZ2VzL2xvYWRpbmdfMl8yNHgyNC5naWY"></image>
   			创建任务中,请稍候...
　　　　</div>
　　</div>
</div>

	
</body>
</html>

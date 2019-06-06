<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>

<!-- 处理上传 -0->
<%@ page import="java.io.*,java.util.*,javax.servlet.*,javax.servlet.http.*" %>
<%@ page import="java.rmi.ServerException"%>
<!-- 处理上传 -1->

<!DOCTYPE html>
<html>
<head>
<title>资料管理</title>
<meta charset="UTF-8">

<!-- <meta name="robots" content="none"> -->
<!-- <meta http-equiv="Pragma" content="no-cache"> -->
<!-- <meta http-equiv="Cache-Control" CONTENT="no-cache"> -->
<!-- <meta http-equiv="X-UA-Compatible" content="IE=edge"> -->
<!-- <meta name="viewport" content="width=device-width, initial-scale=1.0" /> -->

<link href="resources/jquery-ui-1.12.1.custom/jquery-ui.min.css"
	rel="stylesheet">
<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css"
	rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />
<link href="resources/bootstrap-table-1.11.1/bootstrap-table.min.css"
	rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script src="resources/jquery-form/jquery.form.js"></script>
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
	



</head>

<script type="text/javascript">
	$(document).ready(function(){
		$.webeditor.getHead();
		
		$('[data-toggle="fielddata"]').bootstrapTable({
			locale:'zh-CN',
			onLoadSuccess:function(data){},
			onPostHeader:function(){}
		});
		
		var options={
				target:'#loadmsg',
				success:showResponse,
				restForm:true,
				dataType:'json'
		};
		
		$('#uploadform').submit( function(){
			$(this).ajaxSubmit(options);
			return false;
		});
	
	});
	
	
	function showResponse( statusText, xhr, $form) {
		var a = statusText.result;
		//xhr：说明你可以用ajax来自己再次发出请求
		//$form：是那个form对象，是一个jquery对象
		//statusText：状态，成功则为success
		//responseText，服务器返回的是字符串（当然包括html，不包括json）
		
		if( xhr == 'success' && a == 1){
			
			//$.webeditor.showMsgBox("info","上传成功");
			//location.reload();
			$.webeditor.showConfirmBox("info","上传成功",function(){
				$.webeditor.showMsgBox("info", "刷新资料列表中...");
// 				jQuery.post("./fielddatamanage.web",{
// 					atn:'pages'
// 				},function(json){
					
// 					$.webeditor.showMsgBox("close");
// 				},"json");
				location.reload();
				$.webeditor.showMsgBox("close");
			});
			
			
		}else{
			$.webeditor.showMsgBox("info","上传失败");
			$.webeditor.showMsgBox("close");
		}
		
	}
	
	function stateFormat(value, row, index) {
 		var state = row.state;
 		var process = row.process;
 		if( state == 1 && process == 1){
 			return "上传中";
 		}else if(state == 2 && process ==1){
 			return "上传异常";
 		}else if(state == 3 &&  process == 1){
 			return "未处理";
 		}else if(state==1 && process ==2){
 			return "处理中";
 		}else if(state ==2 && process ==2){
 			return "处理异常";
 		}else if(state == 3 && process ==2){
 			return "处理完成";
 		}else if(state == 2 && process == 3){
 			return "创建任务异常";
 		}else if(state == 3 && process == 3){
 			return "创建任务完成";
 		}else{
 			return "未知";
 		}
	}
	
	
	
</script>

<body>

<div class="container" >
	<div id='uploadid'></div>
	<div class='row' style='padding-top:20px'>
	<div>上传资料</div>
	<form id='uploadform' name='uploadfile' action="./fielddatamanage.web?atn=springUpload" method='post' enctype='multipart/form-data'>
	<input id='fp' type='file' name='uploadfile' />
	<input type="hidden" name = "filepath" value = "path111"/>
	<br/><br/>
	<input  type="submit" value='上传'/>
	</form>
	
	</div>
	<div id='loadmsg'></div>
	
	<div id="headdiv"></div>
    <div class="row" style="padding-top:60px">
    	<table id="fielddatalist" data-unique-id="id"
    		data-url="./fielddatamanage.web?atn=pages"
    		data-side-pagination="server" data-filter-control="true"
    		data-pagination="true" data-toggle="fielddata" data-height="714"
    		data-page-list="[5,10,20,100]" data-page-size="5"
    		data-search-on-enter-key='true' data-align='center'>
    		<thead>
    			<tr>
    				<th data-field="id" data-width="120">编号 </th>
    				<th data-field="name" data-filter-control="input" 
    				 data-filter-control-placeholder=""	 data-width="160">资料名称</th>
    				<th data-field="recordcount" data-width="160">资料个数</th>
    				<th data-field="states"  data-formatter="stateFormat" data-filter-control="input"
    					data-filter-control-placeholder=""	data-width="160">状态</th>
    				<th data-field="startdatetime" data-width="360">创建时间</th>
    				<th data-field="username" data-width="160">创建人</th>
    			</tr>
    		</thead>
    	</table>
    </div>
    <div id="footdiv"></div>
   
  
</div>

</body>
</html>
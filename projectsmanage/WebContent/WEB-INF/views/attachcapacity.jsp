<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<title>附属表产能统计</title>
<meta charset="UTF-8" />
<meta name="robots" content="none">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css" rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />
<link href="resources/bootstrap-table-1.11.1/bootstrap-table.min.css" rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js" type="text/javascript"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/bootstrap-table.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/extensions/filter-control/bootstrap-table-filter-control.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/extensions/export/bootstrap-table-export.js"></script>
<script src="resources/bootstrap-table-1.11.1/locale/bootstrap-table-zh-CN.js"></script>

<script type="text/javascript">
	var roleTypes = {5:"制作",6:"校正"};
	var projectTypes = {1: "自动匹配", 2: "更新作业"}
	$(document).ready(function() {
		$.webeditor.getHead();
		$('[data-toggle="attachcapacity"]').bootstrapTable({
			locale : 'zh-CN'
		});
		
		
	});
	
	
	function roleTypesFormat(value, row, index) {
		return roleTypes[row.roleid];
	}
	
	
	function projectTypesFormat(value, row, index) {
		return projectTypes[row.projectType];
	}
	
	function projectCheckTypesFormat(value, row, index) {
		return projectTypes[row.projectTypeCheck];
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
	
	$(function() {
		$('#attachcapacitymake').bootstrapTable({showExport: true,
	        exportDataType: $(this).val()});
		$('#attachheckcapacitycheck').bootstrapTable({showExport: true,
	        exportDataType: $(this).val()});
        $('#myTab a:first').tab('show'); //初始化显示哪个tab
        $('#myTab a').click(function(e) {
            e.preventDefault(); //阻止a链接的跳转行为
            $(this).tab('show'); //显示当前选中的链接及关联的content
        });
        
 		$("#searchmakedata").click(function() {
 			 var opt = {
 	                url: "./attachcapacity.web?atn=pages",
 	                silent: true, //静默刷新
 	                query:{
 	                    //请求参数
 	                    startdate: $("#startdate").val(), 
 	                    enddate: $("#enddate").val()
 	                }
 	            };
 			$('#attachcapacitymake').bootstrapTable("refresh", opt);
 		});
 		
 		
		$("#searchcheckdata").click(function() {
 			 var opt = {
 	                url: "./attachcapacity.web?atn=checks",
 	                silent: true, //静默刷新
 	                query:{
 	                    //请求参数
 	                    startdate: $("#checkstartdate").val(), 
 	                    enddate: $("#checkenddate").val()
 	                }
 	            };
 			$('#attachheckcapacitycheck').bootstrapTable("refresh", opt);
 		});
    });
	
</script>

</head>
<body>
	<div class="container">
	
		<div id="headdiv" class="form-group">
			 
		  </div>
		  
		  <ul id="myTab" class="nav nav-tabs">
		    <li class="active">
		        <a href="#makediv" data-toggle="tab">
		           制作
		        </a>
		    </li>
		    <li><a href="#checkdiv" data-toggle="tab">校正</a></li>
		</ul>
		
		<div id="myTabContent" class="tab-content">
		<div id="makediv" class="tab-pane fade in active">
		<form method="post" action="./attachcapacity.web?atn=exportmake" id="exportMakeData">
			  <div class="form-group">
			    <label  for="startdate">开始时间: </label>
			    <input type="date" id="startdate" name="startdate" />
			    <label  for="enddate">结束时间: </label>
			    <input type="date" id="enddate" name="enddate"/>
			    <input id="flag" name="flag" value="make" type="hidden"/>
			    <button id="searchmakedata" type="button" class="btn btn-default">搜索</button>
			    <button id="exportmake" type="button" class="btn btn-default" onclick="$('#exportMakeData').submit();">导出</button>
			  </div>
		  </form>
		<table id="attachcapacitymake" data-unique-id="id"
				data-query-params="queryParams" data-url="./attachcapacity.web?atn=pages"
				data-side-pagination="server" data-filter-control="true"
				data-pagination="true" data-toggle="attachcapacity" data-height="714"
				data-page-list="[15, 30, 50, 100, all]" data-page-size="30"
				
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr >
					<th data-field="countdate" 
							data-filter-control="input" data-filter-control-placeholder=""  data-valign="top" data-align="center" rowspan="2">统计日期</th>
					
						
						<!-- <th data-field="id"  
							data-filter-control="input" data-filter-control-placeholder=""  data-valign="top" data-align="center" rowspan="2">编号</th>
						 --><th data-field="username" 
							data-filter-control="input" data-filter-control-placeholder=""  data-valign="top" data-align="center" rowspan="2">人员</th>
						  <th data-field="projectType"  data-formatter="projectTypesFormat"
							data-filter-control="select" data-filter-data="var:projectTypes" rowspan="2" data-valign="top" >作业类型</th>
							<th data-field="worktime" 
							 data-sortable="true"  data-valign="top" data-align="center" rowspan="2">工期</th>
						<th data-field="makeErrorCount" data-sortable="true"   data-valign="top" data-align="center" rowspan="2"><br>错误量</th>
						<th data-field="correctRate" data-sortable="true"   data-valign="top" data-align="center" rowspan="2"><br>正确率</th>
						<th data-field="efficiency" data-sortable="true"   data-valign="top" data-align="center" rowspan="2"><br>效率</th>
						<th colspan="3">方向</th>
						<th data-align="center" colspan="3">车道</th>
						<th data-align="center" colspan="3">路口放大图</th>
					</tr>
					<tr > 
						<th data-field="directionCreate"  data-valign="bottom" data-sortable="true" >新增</th>
						
						<th data-field="directionUpdate" data-sortable="true">修改</th>
						
						<th data-field="directionDelete" data-sortable="true">删除</th>
						<th data-field="laneCreate" data-sortable="true">新增</th>
						
						<th data-field="laneUpdate" data-sortable="true">修改</th>
						
						<th data-field="laneDelete" data-sortable="true">删除</th>
						<th data-field="junctionviewCreate" data-sortable="true">新增</th>
						
						<th data-field="junctionviewUpdate" data-sortable="true">修改</th>
						
						<th data-field="junctionviewDelete" data-sortable="true">删除</th>
					</tr>
				</thead>
			</table>
		</div>
		<div id="checkdiv" class="tab-pane fade">	
		 <form method="post" action="./attachcapacity.web?atn=exportmake" id="exportCheckData">
			  <div class="form-group">
			    <label  for="checkstartdate">开始时间: </label>
			    <input type="date" id="startdate" name="startdate"/>
			    <label  for="checkenddate">结束时间: </label>
			    <input type="date" id="enddate" name="enddate" />
			     <input id="flagcheck" name="flag" value="check" type="hidden"/>
			    <button id="searchcheckdata" type="button" class="btn btn-default">搜索</button>
			    <button id="exportcheck" type="button" class="btn btn-default" onclick="$('#exportCheckData').submit();">导出</button>
			  </div>
		  </form>
			<table id="attachheckcapacitycheck" data-unique-id="id"
				data-query-params="queryParams" data-url="./attachcapacity.web?atn=checks"
				data-side-pagination="server" data-filter-control="true"
				data-pagination="true" data-toggle="attachcapacity" data-height="714"
				data-page-list="[15, 30, 50, 100, all]" data-page-size="30"
				data-search-on-enter-key='true' data-align='center'>
				<thead>
					<tr >
					<th data-field="countdate" 
							data-filter-control="input" data-filter-control-placeholder=""  data-valign="top" data-align="center" data-width="100px" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;统计日期&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
						
						<!-- <th data-field="id"  
							data-filter-control="input" data-filter-control-placeholder=""  data-valign="top" data-align="center" >编号</th> -->
						<th data-field="username" 
							data-filter-control="input" data-filter-control-placeholder=""  data-valign="top" data-align="center" data-width="120px" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;人员&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
						  <th data-field="projectTypeCheck"  data-formatter="projectCheckTypesFormat"
							data-filter-control="select" data-filter-data="var:projectTypes"  data-valign="top" >作业类型</th>
						<th data-field="worktime" data-sortable="true"   data-valign="top" data-align="center" ><br>工期</th>
						<th data-field="errorCount" data-sortable="true"   data-valign="top" data-align="center" ><br>错误量</th>
						<th data-field="checkCount" data-sortable="true"   data-valign="top" data-align="center" ><br>校正量</th>
						<th data-field="efficiency" data-sortable="true"   data-valign="top" data-align="center" ><br>效率</th>
						
						<th data-field="lostDirection"  data-align="center" data-sortable="true"  data-valign="top">漏制作方向<br>信息</th>
						<th data-field="makeMoreDirection" data-sortable="true" data-valign="top">多制作方向<br>信息</th>
						<th data-field="endRoadDirection" data-sortable="true" data-valign="top">结束路段<br>错误</th>
						<th data-field="infoDirection" data-sortable="true" data-valign="top">方向类型及名称<br>（包括中文名称、拼音名称、<br>英文名称）</th>
						<th data-field="exitCodeDirection" data-sortable="true" data-valign="top">出口编号<br>错误</th>
						<th data-field="exitDirection" data-sortable="true" data-valign="top">出口方向<br>错误</th>
						<th data-field="unknownDirection" data-sortable="true" data-valign="top">其它未定义错误<br>（方向信息）</th>
						<th data-field="lostLane" data-sortable="true" data-valign="top">漏制作车道<br>信息</th>
						<th data-field="makeMoreLane" data-sortable="true" data-valign="top">多制作车道<br>信息</th>
						<th data-field="turnLane" data-sortable="true" data-valign="top">车道数/箭头方向<br>/可调头漏制作<br>错误</th>
						<th data-field="endRoadLane" data-sortable="true" data-valign="top">车道号/箭头数<br>/终止道路错误</th>
						<th data-field="innerLinkLane" data-sortable="true" data-valign="top">扩展内连接<br>标识错误<br>（lane信息）</th>
						<th data-field="unknownLane" data-sortable="true" data-valign="top">其它未定义错误<br>（lane信息）</th>
						<th data-field="lostSceneJunctionview" data-sortable="true" data-valign="top">漏制作路口放大图<br>（实景图）</th>
						<th data-field="lostPatternJunctionview" data-sortable="true" data-valign="top">漏制作路口放大图<br>（模式图）</th>
						<th data-field="makeMoreSceneJunctionview" data-sortable="true" data-valign="top">多制作路口放大图<br>（实景图）</th>
						<th data-field="makeMorePatternJunctionview" data-sortable="true" data-valign="top">多制作路口放大图<br>（模式图）</th>
						<th data-field="pictureTypeSceneJunctionview" data-sortable="true" data-valign="top">图形种类错误<br>（实景图）</th>
						<th data-field="pictureTypePatternJunctionview" data-sortable="true" data-valign="top">图形种类错误<br>（模式图）</th>
						<th data-field="arrowSceneJunctionview" data-sortable="true" data-valign="top">箭头图编号错误<br>（实景图）</th>
						<th data-field="arrowPatternJunctionview" data-sortable="true" data-valign="top">箭头图编号错误<br>（模式图）</th>
						<th data-field="endRoadSceneJunctionview" data-sortable="true" data-valign="top">结束道路错误<br>（实景图）</th>
						<th data-field="endRoadPatternJunctionview" data-sortable="true" data-valign="top">结束道路错误<br>（模式图）</th>
						<th data-field="pictureChoiceSceneJunctionview" data-sortable="true" data-valign="top">图片选择错误<br>（实景图）</th>
						<th data-field="pictureChoicePatternJunctionview" data-sortable="true" data-valign="top">图片选择错误<br>（模式图）</th>
						<th data-field="unknownJunctionview" data-sortable="true" data-valign="top">其他未定义错误<br>（路口放大图）</th>
					</tr>
				</thead>
			</table>
			</div>
		</div>
		</div>
	</div>
</body>
</html>
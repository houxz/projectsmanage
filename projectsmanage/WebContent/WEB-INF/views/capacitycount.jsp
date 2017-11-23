<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.emg.projectsmanage.common.ParamUtils"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path;
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<meta name="robots" content="all">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>产能统计</title>

<link href="resources/jquery-ui-1.12.1.custom/jquery-ui.min.css"
	rel="stylesheet">
<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css"
	rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />
<link href="resources/jquery-flexselect-0.9.0/flexselect.css"
	rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/jquery-flexselect-0.9.0/jquery.flexselect.js"></script>
<script src="resources/jquery-flexselect-0.9.0/liquidmetal.js"
	type="text/javascript"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$.webeditor.getHead();
		$("#start").datepicker({
			/* onSelect:function(dateText,inst){
				$("#end").datepicker("option","minDate",dateText);
				},
				maxDate:"+1y" */
			});
		$("#end").datepicker({
			/* onSelect:function(dateText,inst){
				$("#start").datepicker("option","maxDate",dateText);
				},
				maxDate:"+1y" */
			});
		$("#project").flexselect();
	});
	jQuery(function($){
		$.datepicker.regional['zh-CN'] = {
			closeText: '关闭',
			prevText: '<上月',
			nextText: '下月>',
			currentText: '今天',
			monthNames: ['一月','二月','三月','四月','五月','六月',
			             '七月','八月','九月','十月','十一月','十二月'],
			monthNamesShort: ['一','二','三','四','五','六','七','八','九','十','十一','十二'],
			dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],
			dayNamesShort: ['周日','周一','周二','周三','周四','周五','周六'],
			dayNamesMin: ['日','一','二','三','四','五','六'],
			weekHeader: '周',
			dateFormat: 'yy-mm-dd',
			firstDay: 1,
			isRTL: false,
			showMonthAfterYear: true,
			yearSuffix: '年'};
		$.datepicker.setDefaults($.datepicker.regional['zh-CN']);
	});

	//查询编辑工作量
	function queryModifyCount(){
		clearCapacityCounts();
		var start = $("#start").val();
		var end = $("#end").val();
		var project = $("#project").val();
		$.post("<%=path%>/capacitycount.web",
			{"atn":"modifycount",
			"begin":start,
			"end":end,
			"capacityProject":project
			},
			function(json) {
				if ("1" == json.result) {
					var objList =  json.resultList;
					if(objList && objList.length > 0) {
						$("#capacityCountsTime").html(json.lastTime);
						showCapacityCounts(objList);
					} else {
						$("#capacityCountsTime").html("无数据");
					}
				} else {
					showMsgLabel("alert", "查询失败");
				}
			}, "json");
	}
	
	//查询属性错误工作量
	function queryProptyErrorCount(){
		clearCapacityFieldErrorCounts();
		var start = $("#start").val();
        var end = $("#end").val();
        var project = $("#project").val();
			$.post("<%=path%>/capacitycount.web",
				{"atn":"capacityproptyerrorcountforobj",
				"begin":start,
				"end":end,
				"capacityProptyErrorProject":project
				},
				function(json) {
					if ("1" == json.result) {
		                var list =  json.resultList;
		                if(list && list.length > 0) {
		                	$("#capacityFieldTime").html(json.lastTime);
		                	showCapacityFieldErrorObjCounts(list);
						} else {
							$("#capacityFieldTime").html("无数据");
						}
		            } else {
		            	showMsgLabel("alert", "查询失败");
		            }
				}, "json"
		    );
	}
	//查询几何错误工作量
	function queryGeoErrorCount(){
        clearCapacityGeoCounts();
        var start = $("#start").val();
        var end = $("#end").val();
        var project = $("#project").val();
		$.post("<%=path%>/capacitycount.web",
			{"atn":"capacitygeoerrorcount", 
			"begin":start,
			"end":end,
			"capacityGeoErrorProject":project
			},
			function(json) {
				if ("1" == json.result) {
					var objGeoList =  json.resultList;
					if(objGeoList && objGeoList.length > 0) {
						$("#capacityGeoTime").html(json.lastTime);
						showCapacityGeoCounts(objGeoList);
					} else {
						$("#capacityGeoTime").html("无数据");
					}
				} else {
					showMsgLabel("alert", "查询失败");
				}
			}, "json"
		);
	}
	
	//查询POI错误工作量
	function queryPOIErrorCount(){
		clearCapacityPOICounts();
		var start = $("#start").val();
		var end = $("#end").val();
		var project = $("#project").val();
		$.post("<%=path%>/capacitycount.web",
				{
					"atn" : "capacitypoierrorcount",
					"begin" : start,
					"end" : end,
					"capacityPOIErrorProject" : project
				},
				function(json) {
					if ("1" == json.result) {
						var objPOIList = json.resultList;
						if(objPOIList && objPOIList.length > 0) {
							$("#capacityPOITime").html(json.lastTime);
							showCapacityPOICounts(objPOIList);
						} else {
							$("#capacityPOITime").html("无数据");
						}
					} else {
						showMsgLabel("alert", "查询失败");
					}
				}, "json");
	}

	function queryCountResult() {
		var countMode = $("#countMode").val();
		switch (countMode) {
		case "modifyCount"://编辑工作量
			queryModifyCount();
			break;
		case "errorCount"://属性错误工作量
			queryProptyErrorCount();
			break;
		case "errorGeoCount"://几何错误工作量
			queryGeoErrorCount();
			break;
		case "errorPOICount"://POI错误工作量
			queryPOIErrorCount();
			break;
		}
	}

	//显示编辑工作量
	function showCapacityCounts(obj) {
		html = "";
		html = "<table class='table table-bordered table-hover'>";
		html += "<tr><th>人员名称</th><th>人员类型</th><th>POI类型</th><th>新增POI个数</th><th>确认POI个数</th><th>修改POI个数</th><th>删除POI个数</th><th>总计</th></tr>";
		for ( var i = 0; i < obj.length; i++) {
			html += "<tr>";
			html += "<td>" + obj[i].realName + "</td>";
			html += "<td>" + obj[i].identity + "</td>";
			html += "<td>" + obj[i].poiType + "</td>";
			html += "<td>" + obj[i].poiAddCountModel.count + "</td>";
			html += "<td>" + obj[i].poiSureCountModel.count + "</td>";
			html += "<td>" + obj[i].poiModifyCountModel.count + "</td>";
			html += "<td>" + obj[i].poiDelCountModel.count + "</td>";
			html += "<td>" + obj[i].poiCount + "</td>";
			html += "</tr>";
		}
		html += "</table>";
		$("#capacityCounts").append(html);
	}

	//显示几何错误编辑工作量
	function showCapacityGeoCounts(obj) {
		var html = "";
		html = "<table class='table table-bordered table-hover'>";
		html += "<tr><th>人员名称</th><th>人员类型</th><th>漏新增</th><th>漏确认</th><th>多新增</th><th>多确认</th><th>漏删除</th> <th>多删除</th> <th>真位置错误</th> <th>引导位置错误</th> <th>方位错误</th> <th>总计</th></tr>";
		for ( var i = 0; i < obj.length; i++) {
			html += "<tr>";
			html += "<td>" + obj[i].realName + "</td>";
			html += "<td>" + obj[i].identity + "</td>";
			html += "<td>" + obj[i].capacityGeoMissAddCountModel.count
					+ "</td>";
			html += "<td>" + obj[i].capacityGeoMissSureCountModel.count
					+ "</td>";
			html += "<td>" + obj[i].capacityGeoMoreAddCountModel.count
					+ "</td>";
			html += "<td>" + obj[i].capacityGeoMoreSureCountModel.count
					+ "</td>";
			html += "<td>" + obj[i].capacityGeoMissDelCountModel.count
					+ "</td>";
			html += "<td>" + obj[i].capacityGeoMoreDelCountModel.count
					+ "</td>";
			html += "<td>" + obj[i].capacityGeoPosErrorCountModel.count
					+ "</td>";
			html += "<td>" + obj[i].capacityGeoPosGuideErrorCountModel.count
					+ "</td>";
			html += "<td>" + obj[i].capacityGeoDirectionErrorCountModel.count
					+ "</td>";
			html += "<td>" + obj[i].geoCount + "</td>";
			html += "</tr>";
		}
		html += "</table>";
		$("#capacityGeoErrorCounts").append(html);
	}

	//显示几何错误编辑工作量
	function showCapacityPOICounts(obj) {
		var html = "";
		html = "<table class='table table-bordered table-hover'>";
		html += "<tr><th>人员名称</th><th>人员类型</th><th>A类错误POI个数</th><th>B类错误POI个数</th><th>C类错误POI个数</th><th>错误POI总数</th></tr>";
		for ( var i = 0; i < obj.length; i++) {
			html += "<tr>";
			html += "<td>" + obj[i].realName + "</td>";
			html += "<td>" + obj[i].identity + "</td>";
			html += "<td>" + obj[i].capacityPOIErrorACountModel.count + "</td>";
			html += "<td>" + obj[i].capacityPOIErrorBCountModel.count + "</td>";
			html += "<td>" + obj[i].capacityPOIErrorCCountModel.count + "</td>";
			html += "<td>" + obj[i].capacityPOIErrorCountModel.count + "</td>";
			html += "</tr>";
		}
		html += "</table>";
		$("#capacityPOIErrorCounts").append(html);
	}

	//显示属性错误工作量
	function showCapacityFieldErrorObjCounts(list) {
		var html = "";
		html = "<table class='table table-bordered table-hover table-condensed'>";
		html += "<tr><th>人员名称</th><th>人员类型</th><th>错误类型</th><th>中文正式名称</th><th>英文正式名称</th><th>对象类型代码</th>";
		html += "<th>补充类别</th><th>系列代码</th><th>电话号码</th><th>等级</th><th>附加信息</th><th>详细地址乡镇级</th>";
		html += "<th>详细地址村级、除行政区划外的其它区域</th><th>详细地址大街路巷里</th><th>详细地址号组</th><th>详细地址其它</th><th>备注</th><th>总计/错误类型</th><th>总计/人</th></tr>";

		for ( var i = 0; i < list.length; i++) {

			var userObj = list[i];
			html += "<tr>";
			html += "<td rowspan='6'>" + userObj.realName + "</td>";
			html += "<td rowspan='6'>" + userObj.identity + "</td>";
			html += "<td>未指定错误类型</td>";
			var dataHtml = addHtmlContentByObjData(userObj.missErrorTypeModel);
			html += dataHtml;
			html += "<td rowspan='6'>" + userObj.count + "</td>";
			html += "</tr>";

			html += "<tr>";
			html += "<td>错别字</td>";
			var dataHtml = addHtmlContentByObjData(userObj.errorCodeTypeModel);
			html += dataHtml;
			html += "</tr>";

			html += "<tr>";
			html += "<td>未按规则制作</td>";
			var dataHtml = addHtmlContentByObjData(userObj.errorRuleTypeModel);
			html += dataHtml;
			html += "</tr>";

			html += "<tr>";
			html += "<td>多制作</td>";
			var dataHtml = addHtmlContentByObjData(userObj.errorMoreTypeModel);
			html += dataHtml;
			html += "</tr>";

			html += "<tr>";
			html += "<td>漏制作</td>";
			var dataHtml = addHtmlContentByObjData(userObj.missDoneTypeModel);
			html += dataHtml;
			html += "</tr>";

			html += "<tr>";
			html += "<td>未对保密信息处理</td>";
			var dataHtml = addHtmlContentByObjData(userObj.errorSecretTypeModel);
			html += dataHtml;
			html += "</tr>";

		}
		html += "</table>";

		$("#capacityFieldErrorCounts").append(html);
	}

	//清除相关
	function clearCapacityFieldErrorCounts() {
		$("#capacityFieldErrorCounts table").remove();
	}
	function clearCapacityCounts() {
		$("#capacityCounts table").remove();
	}
	function clearCapacityGeoCounts() {
		$("#capacityGeoErrorCounts table").remove();
	}
	function clearCapacityPOICounts() {
		$("#capacityPOIErrorCounts table").remove();
	}

	//属性错误工作量统计根据对象类型添加数据
	function addHtmlContentByObjData(typeObj) {

		var html = "";
		html += "<td>" + typeObj.chineseNameFieldModel.count + "</td>";
		html += "<td>" + typeObj.englishNameFieldModel.count + "</td>";
		html += "<td>" + typeObj.objTypeFieldModel.count + "</td>";
		html += "<td>" + typeObj.addTypeFieldModel.count + "</td>";
		html += "<td>" + typeObj.seriesCodeFieldModel.count + "</td>";
		html += "<td>" + typeObj.telFieldModel.count + "</td>";
		html += "<td>" + typeObj.levelFieldModel.count + "</td>";
		html += "<td>" + typeObj.addInfoFieldModel.count + "</td>";
		html += "<td>" + typeObj.addrTownFieldModel.count + "</td>";
		html += "<td>" + typeObj.addrAreaFieldModel.count + "</td>";
		html += "<td>" + typeObj.addrStreetFieldModel.count + "</td>";
		html += "<td>" + typeObj.addrNumFieldModel.count + "</td>";
		html += "<td>" + typeObj.addrOtherFieldModel.count + "</td>";
		html += "<td>" + typeObj.remarkFieldModel.count + "</td>";
		html += "<td>" + typeObj.count + "</td>";

		return html;
	}

	//导出excel  
	function exportExcel() {
		var html = "";
		var countMode = $("#countMode").val();
		switch (countMode) {
		case "modifyCount"://编辑工作量
			html = $("#capacityCounts table").html();
			break;
		case "errorCount"://属性错误工作量
			html = $("#capacityFieldErrorCounts table").html();
			break;
		case "errorGeoCount"://几何错误工作量
			html = $("#capacityGeoErrorCounts table").html();
			break;
		case "errorPOICount"://POI错误工作量
			html = $("#capacityPOIErrorCounts table").html();
			break;
		}
		$('#tablehtml').val("<table>" + html + "</table>");
		$("#output").submit();
	}
	
	function countModeChange(countMode) {
		switch (countMode) {
		case "modifyCount"://编辑工作量
			$("#modifyDiv").show();
			$("#modifyDiv").siblings().hide();
			break;
		case "errorCount"://属性错误工作量
			$("#errorDiv").show();
			$("#errorDiv").siblings().hide();
			break;
		case "errorGeoCount"://几何错误工作量
			$("#errorGeoDiv").show();
			$("#errorGeoDiv").siblings().hide();
			break;
		case "errorPOICount"://POI错误工作量
			$("#errorPOIDiv").show();
			$("#errorPOIDiv").siblings().hide();
			break;
		}
	}

</script>
</head>
<body>
	<div class="container" style="min-width: 80%;">
		<div id="headdiv"></div>
		<div class="capacityfilter">
			<div>
				<div class="input-group">
					<span class="input-group-addon">统计内容:</span> <select
						class="form-control" id="countMode" onchange="countModeChange(this.value);" >
						<option value="modifyCount">编辑工作量</option>
						<option value="errorCount">属性错误工作量</option>
						<option value="errorGeoCount">几何错误工作量</option>
						<option value="errorPOICount">POI错误工作量</option>
					</select> <span class="input-group-addon">起始日期:</span> <input type="text"
						class="form-control" id="start"> <span
						class="input-group-addon">结束日期:</span> <input type="text"
						class="form-control" id="end"> <span
						class="input-group-addon">项目名称:</span> <select name="project"
						class="form-control" id="project">
						<option value="-1">所有项目</option>
						<c:forEach var="map" items="${capacityProjectList }">
							<option value="${map['id'] }">${map['name'] }</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div style="text-align: right; margin: 1%;">
				<button type="button" class="btn btn-default"
					onclick="queryCountResult();">查看统计结果</button>
				<button type="button" class="btn btn-default"
					onclick="exportExcel();">导出Excel</button>
			</div>
		</div>

		<div id="countResult">
			<!-- 编辑工作量统计 -->
			<div id="modifyDiv">
				<div class="panel panel-default" id="capacityCounts">
					<div class="panel-heading">
						<h3 class="panel-title">编辑工作量统计</h3>
					</div>
					<div class="panel-body">
						<font color="#FF0000" size="2">最新统计时间：<span
							id="capacityCountsTime">无数据</span></font>
					</div>
				</div>
			</div>
	
			<!-- 属性错误工作量统计-->
			<div id="errorDiv" style="display: none;">
				<div class="panel panel-default" id="capacityFieldErrorCounts">
					<div class="panel-heading">
						<h3 class="panel-title">属性错误工作量统计</h3>
					</div>
					<div class="panel-body">
						<font color="#FF0000" size="2">最新统计时间：<span
							id="capacityFieldTime">无数据</span></font>
					</div>
				</div>
			</div>
	
			<!-- 几何错误工作量统计-->
			<div id="errorGeoDiv" style="display: none;">
				<div class="panel panel-default" id="capacityGeoErrorCounts">
					<div class="panel-heading">
						<h3 class="panel-title">几何错误工作量统计</h3>
					</div>
					<div class="panel-body">
						<font color="#FF0000" size="2">最新统计时间：<span
							id="capacityGeoTime">无数据</span></font>
					</div>
				</div>
			</div>
	
			<!-- POI错误工作量统计 -->
			<div id="errorPOIDiv" style="display: none;">
				<div class="panel panel-default" id="capacityPOIErrorCounts">
					<div class="panel-heading">
						<h3 class="panel-title">POI错误工作量统计</h3>
					</div>
					<div class="panel-body">
						<font color="#FF0000" size="2">最新统计时间：<span
							id="capacityPOITime">无数据</span></font>
					</div>
				</div>
			</div>
		</div>

		<div style="display: none;">
			<form method="post" action="./downloadexcel.web" id="output">
				<input type="hidden" id="tablehtml" name="tablehtml" value="" />
			</form>
		</div>
	</div>
</body>
</html>
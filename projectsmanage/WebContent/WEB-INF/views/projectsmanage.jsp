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
<title>项目管理</title>

<link href="resources/jquery-flexselect-0.9.0/flexselect.css" rel="stylesheet">
<link href="resources/jquery-ui-1.12.1.custom/jquery-ui.min.css" rel="stylesheet">
<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css" rel="stylesheet" />
<link href="resources/bootstrap-table-1.11.1/bootstrap-table.min.css" rel="stylesheet">
<link href="resources/css/css.css" rel="stylesheet" />
<link href="resources/css/message.css" rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/bootstrap-table.min.js"></script>
<script src="resources/bootstrap-table-1.11.1/extensions/filter-control/bootstrap-table-filter-control.js"></script>
<script src="resources/bootstrap-table-1.11.1/locale/bootstrap-table-zh-CN.js"></script>
<script src="resources/js/project/giveworker.js"></script>
<script src="resources/js/project/attributemanage.js"></script>
<script src="resources/js/project/priorityadjust.js"></script>
<script src="resources/jquery-flexselect-0.9.0/jquery.flexselect.js"></script>
<script src="resources/jquery-flexselect-0.9.0/liquidmetal.js"></script>
<script src="resources/js/message.js"></script>


<style type="text/css">
#selectWorker ul {
	list-style-type: none;
	border: 1
}

#selectWorker ul li {
	float: left;
	margin: 2px 8px;
}

#selectChecker ul {
	list-style-type: none;
	border: 1
}

#selectChecker ul li {
	float: left;
	margin: 2px 8px;
}
</style>

<script type="text/javascript">
	var currentPageProjects = {};
	{
		var jobstatus = eval('(${jobstatus})');
		var skillLevels = eval('(${skillLevels})');
		var difficuties = eval('(${difficuties})');
		var priorityLevels = eval('(${priorityLevels})');
		var ownerLevels = {"-1": "不可用", "0": "公有", "1": "私有"};
		
		var workersForSelect = eval('(${workers})');
		var selectedworkusers = {};
		if (workersForSelect.length > 0) {
			for ( var w in workersForSelect) {
				selectedworkusers[workersForSelect[w].userid] = workersForSelect[w].username;
			}
		}
		var checkersForSelect = eval('(${checkers})');
		var selectedcheckusers = {};
		if (checkersForSelect.length > 0) {
			for ( var w in checkersForSelect) {
				selectedcheckusers[checkersForSelect[w].userid] = checkersForSelect[w].username;
			}
		}

		$(document).ready(function() {
			$.webeditor.getHead();
			//$.webeditor.getFoot();
			$('[data-toggle="jobsinfos"]').bootstrapTable({
				locale : 'zh-CN',
				onLoadSuccess : function(data) {
					currentPageProjects = data.rows;
				}
			});
			$("#workerse").flexselect();
			$("#checkers").flexselect();
			sortable();
		});

		function stateFormat(value, row, index) {
			return jobstatus[row.overstate];
		}

		function workersFormat(value, row, index) {
			var uhtml = "";
			if (value != undefined && value.length > 0) {
				var workers = row.workusers;
				var names = "";
				for ( var w in workers) {
					names += workers[w].username + ",";
				}
				if(names.indexOf(',') > 0)	names = names.substring(0,names.lastIndexOf(','));
				uhtml += "<a  style='cursor:hand;'  href='javascript:void(0)' onclick='changeworker("
						+ row.id + ",0)' title='" + names + "'>";
				uhtml += workers[0].username;
				if (workers.length == 2) {
					uhtml += "," + workers[1].username;
				}else if(workers.length > 2){
					uhtml += "," + workers[1].username + "...";
				}
				uhtml += "</a>";
			} else {
				uhtml = "<a  href='javascript:void(0)'   id='" + row.id
						+ "_worker' onclick='addworker(" + row.id
						+ ",0)'>添加</a>";
			}
			return uhtml;
		}
		function checkersFormart(value, row, index) {
			var uhtml = "";
			if (value != undefined && value.length > 0) {
				var checkusers = row.checkusers;
				var names = "";
				for ( var w in checkusers) {
					names += checkusers[w].username + ",";
				}
				if(names.indexOf(',') > 0)	names = names.substring(0,names.lastIndexOf(','));
				uhtml += "<a  style='cursor:hand;'  href='javascript:void(0)' onclick='changeworker("
						+ row.id + ",1)'  title='" + names + "'>";
				uhtml += checkusers[0].username;
				if (checkusers.length == 2) {
					uhtml += "," + checkusers[1].username;
				}else if(checkusers.length > 2){
					uhtml += "," + checkusers[1].username + "...";
				}
				uhtml += "</a>";
			} else {
				uhtml = "<a  href='javascript:void(0)'   id='" + row.id
						+ "_checker' onclick='addworker(" + row.id
						+ ",1)'>添加</a>";
			}
			return uhtml;
		}

		function operationFormat(value, row, index) {
			var html = new Array();
			//var worktype = $('#condition input[name="prostate"]:checked').val(); //作业类型，0制作 ，1校验。  state:1作业中，2暂停
			var worktype = "overstate";
			if (row[worktype] == 1) {
				html
						.push('<button type="button" class="btn btn-default"  style="margin-right:3px;" onclick="changeProState(2,'
								+ row.id
								+ ')">暂停</button>');
			} else if (row[worktype] == 2) {
				html
						.push('<button type="button" class="btn btn-default" style="margin-right:3px;" onclick="changeProState(1,'
								+ row.id
								+ ')" >开始</button>');
			}

			return html.join('');
		}
	}
	function refresh() {
		$('#jobsinfos').bootstrapTable('refresh');
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

	function changeProConditonState() {
		refresh();
	}

	function diffFormat(value, row, index) {
		var html = [];
		html.push("<select name='pdifficulty_" + row.id + "' id='pdifficulty_"
				+ row.id + "' onchange='changeDiff(" + row.id
				+ ")'  class='form-control'>");
		for ( var difficuty in difficuties) {
			if (difficuty == value) {
				html.push("<option value='"+ value +"' selected='selected' >"
								+ difficuties[value] + "</option>");
			} else {
				html.push("<option value='"+ difficuty +"'>"
						+ difficuties[difficuty] + "</option>");
			}
		}
		html.push("</select>");
		return html.join("");
	}

	function priFormat(value, row, index) {
		var html = [];
		html.push("<select name='priority_" + row.id + "' id='priority_"
				+ row.id + "' onchange='changePriority(" + row.id
				+ ")'  class='form-control'>");
		for ( var priorityLevel in priorityLevels) {
			if (priorityLevel == value) {
				html.push("<option value='"+ value +"' selected='selected' >"
						+ priorityLevels[value] + "</option>");
			} else {
				html.push("<option value='"+ priorityLevel +"'>"
						+ priorityLevels[priorityLevel] + "</option>");
			}
		}
		html.push("</select>");
		return html.join("");
	}

	function ownerFormat(value, row, index) {
		var html = [];
		html.push("<select name='access_" + row.id + "' id='access_" + row.id
				+ "' onchange='changeAccess(" + row.id
				+ ")'  class='form-control'>");

		if (value == 0) {
			html.push("<option value='0' selected='selected'>公有</option>");
			html.push("<option value='1' >私有</option>");
		} else if (value == 1) {
			html.push("<option value='0'>公有</option>");
			html.push("<option value='1' selected='selected'>私有</option>");
		} else {
			html.push("<option value='-1' selected='selected'>不可用</option>");
			html.push("<option value='0'>公有</option>");
			html.push("<option value='1'>私有</option>");
		}
		html.push("</select>");
		return html.join("");
	}
</script>

</head>
<body>
	<div class="container">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px">
			<div>
				<table id="jobsinfos" data-unique-id="id"
					data-query-params="queryParams" data-url="./videoproject.web"
					data-side-pagination="server" data-filter-control="true"
					data-pagination="true" data-toggle="jobsinfos" data-height="714"
					data-page-list="[ 10, 30, 50, All]" data-page-size="10"
					data-search-on-enter-key='true' data-align='center'>
					<thead>
						<tr>
							<th data-field="id" data-filter-control="input"
								data-filter-control-placeholder=""
								data-width="70">项目编号</th>
							<th data-field="name" data-filter-control="input"
								data-filter-control-placeholder=""
								data-width="320">项目名称</th>
							<th data-field="overstate" data-formatter="stateFormat"
								data-filter-control="select" data-filter-data="var:jobstatus"
								data-width="100">作业状态</th>
							<th data-field="workers" data-formatter="workersFormat"
								data-filter-control="select" data-width="120"
								data-filter-data="var:selectedworkusers">制作人</th>
							<th data-field="checkers" data-formatter="checkersFormart"
								data-filter-control="select" data-width="120"
								data-filter-data="var:selectedcheckusers">校验人</th>
							<th data-field="priority" data-formatter="priFormat"
								data-filter-control="select" data-width="100"
								data-filter-data="var:priorityLevels">优先级</th>
							<th data-field="pdifficulty" data-formatter="diffFormat"
								data-filter-control="select" data-width="100"
								data-filter-data="var:difficuties">难度</th>
							<th data-field="owner" data-formatter="ownerFormat"
								data-filter-control="select" data-width="100"
								data-filter-data="var:ownerLevels">公有/私有</th>
							<th data-formatter="operationFormat" data-width="70">操作</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
		<div id="footdiv"></div>
	</div>

	<div id="selectWorker" style="display: none;">
		<div id="hasWorker_div" style="height: 70%; margin: 2% auto;">
			<ul id="hasWorker_ul" style="display: table-cell;padding: 0;"></ul>
		</div>
		<div class="input-group" style="width: 80%; margin: 1% auto;">
			<span class="input-group-addon">添加作业人员： </span>
			<select class="form-control" id="workerse">
				<option value="0">请选择作业人员</option>
				<c:forEach items="${allWorkersList }" var="worker">
					<option value="${worker['userid'] }_${worker['username'] }">
						<c:if test="${worker['username'] != null}">${worker['username']}
						<!-- 
							【技能等级:
							<c:choose>
								<c:when test="${worker['skilldes'] != null}">${worker['skilldes']}</c:when>
								<c:otherwise>无</c:otherwise>
							</c:choose>
							】 -->
						</c:if>
					</option>
				</c:forEach>
			</select>
			<span class="input-group-btn">
				<button class="btn btn-default" type="button" onclick="confirmSelect()">添加</button>
				<button class="btn btn-default" type="button" onclick="removeAllWorkers()">清空作业员</button>
			</span>
		</div>
	</div>

	<div id="selectChecker" style="display: none;">
		<div id="hasChecker_div" style="height: 70%; margin: 2% auto;">
			<ul id="hasChecker_ul" style="display: table-cell;padding: 0;"></ul>
		</div>
		<div class="input-group" style="width: 80%; margin: 1% auto;">
			<span class="input-group-addon">添加检验人员：</span>
			<select class="form-control" id="checkers">
				<option value="0">请选择校验人员</option>
				<c:forEach items="${allCheckersList }" var="check">
					<option value="${check['userid'] }_${check['username'] }">
						<c:if test="${check['username'] != null}">${check['username']}
							<!-- 【技能等级:
							<c:choose>
								<c:when test="${check['skilldes'] != null}">${check['skilldes']}</c:when>
								<c:otherwise>无</c:otherwise>
							</c:choose>
							】 -->
						</c:if>
					</option>
				</c:forEach>
			</select>
			<span class="input-group-btn">
				<button class="btn btn-default" type="button" onclick="confirmSelect()">添加</button>
				<button class="btn btn-default" type="button" onclick="removeAllWorkers()">清空校验员</button>
			</span>
		</div>
	</div>
</body>
</html>
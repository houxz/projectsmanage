<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	String baseUrl = request.getScheme()+"://"+ request.getServerName()+":"+request.getServerPort()+"/projectsmanage/interface.web"+"?";
	String path = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
<title>接口测试</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<meta name="robots" content="nofollow" />

<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css"  rel="stylesheet" />
	
<script src="resources/jquery/jquery-3.2.1.min.js" ></script>
<script src="resources/js/jquery.formvalidate.js" ></script>

<script type="text/javascript">
	var models = [
	              {"name":"查看某平台下所有项目","type":"post","action":"selectAllProjects","params":["systemid"]},
	              {"name":"查看某平台下未完成项目","type":"post","action":"selectUndoProjects","params":["systemid"]},
	              {"name":"查看某平台下项目数","type":"post","action":"selectCountOfProjects","params":["systemid"]},
	              {"name":"基于ID查看某平台下项目","type":"post","action":"selectProjectByID","params":["pid"]},
	              {"name":"基于项目类型查看某平台下项目","type":"post","action":"selectProjectByProtype","params":["systemid", "protype"]},
	              {"name":"查看某平台下新项目","type":"post","action":"selectTopProjects","params":["systemid","limit"]},
	              {"name":"之后创建的某平台下项目","type":"post","action":"selectProjectsByStarttime","params":["systemid","starttime"]},
	              {"name":"之前创建的某平台下项目","type":"post","action":"selectProjectsByEndtime","params":["systemid","endtime"]},
	              {"name":"某时间段内创建的某平台下项目","type":"post","action":"selectProjectsByCreatetime","params":["systemid","starttime","endtime"]},
	              {"name":"新增一个某平台下项目","type":"post","action":"insertNewProject","params":["protype","pdifficulty","priority","tasknum","systemid","description","createby","area","name","state","progress","owner","checkprogress","checkstate","modifyprogress","modifystate","confirmprogress","confirmstate","overprogress"]},
	              {"name":"用户在某平台下是否有权限","type":"post","action":"checkUserInSystem","params":["systemid","userid","roleid"]},
	              {"name":"基于id查看用户角色对应表","type":"post","action":"selectUserRoleByID","params":["id"]},
	              {"name":"基于userid查看用户角色对应表","type":"post","action":"selectUserRoleByUserID","params":["userid"]},
	              {"name":"基于roleid查看用户角色对应表","type":"post","action":"selectUserRoleByRoleID","params":["roleid"]},
	              {"name":"查看用户角色对应表所有记录","type":"post","action":"selectAllUserRoles","params":[]},
	              {"name":"查看用户角色对应表记录数","type":"post","action":"selectUserRoleCount","params":[]},
	              {"name":"基于ID更新任务数","type":"post","action":"updateProjectTasknumByID","params":["pid", "tasknum"]},
	              {"name":"基于ID更新项目状态","type":"post","action":"updateProjectOverstateByID","params":["pid", "overstate"]},
	              {"name":"基于ID提交任务","type":"post","action":"submitTaskByID","params":["pid", "type"]},
	              {"name":"提交任务状态变化","type":"post","action":"submitTaskStatus","params":["systemid", "projectid", "taskid", "userid", "statebefore", "processbefore", "stateafter", "processafter"]},
	              {"name":"基于userid获取下一个可用项目","type":"post","action":"selectNextProject","params":["systemid", "userid", "pid"]},
	              {"name":"查看是否存在某用户","type":"post","action":"checkUserByUsernameAndPassword","params":["username", "password"]},
	              {"name":"查看部门中是否存在某用户","type":"post","action":"checkUserByUsernameAndDepartment","params":["username", "department"]},
	              {"name":"查看部门中是否存在用户名密码用户","type":"post","action":"checkUserByUsernameAndPasswordAndDepartment","params":["username", "password", "department"]},
	              {"name":"基于username获取用户信息","type":"post","action":"selectUserInfoByUsername","params":["username"]},
	              {"name":"基于userid获取用户信息","type":"post","action":"selectUserInfoByUserID","params":["userid"]},
	              {"name":"获取部门列表","type":"post","action":"selectAllDepartment","params":[]},
	              {"name":"基于项目name获取项目信息","type":"post","action":"selectProjectByName","params":["name","proType","systemid"]},
	              {"name":"获取QC质检项目","type":"post","action":"QCUndoProjects","params":["systemid", "limit"]},
	              {"name":"获取QC质检项目id","type":"post","action":"QCUndoProjectIDs","params":[]},
	];
	
	function delResult(){
		$("#result").text("");
		$(".panel").removeClass("panel-default");
		$(".panel").removeClass("panel-success");
		$(".panel").removeClass("panel-warning");
		$(".panel").removeClass("panel-danger");
		
		$(".panel").addClass("panel-default");
	};
	
	function makeUrl(){
		delResult();
		var action = $("#action").val();
       	var params = "action=" + action;
       	var keys = $("#tbParams td .key");
       	var values = $("#tbParams td .value");
       	if(keys.length > 0 && keys.length == values.length){
       		for(var i=0;i<keys.length;i++){
				params += "&" + keys.get(i).value + "=" + values.get(i).value;
       		}
       	}
       	var url = "<%=baseUrl%>" + params;
       	$(".panel").addClass("panel-success");
		$("#result").text(url);
	};
	
    function doPost() {
    	delResult();
       	var action = $("#action").val();
       	var type = $("#type").val();
       	var params = {};
       	params["action"] = action;
       	var keys = $("#tbParams td .key");
       	var values = $("#tbParams td .value");
       	if(keys.length > 0 && keys.length == values.length){
       		for(var i=0;i<keys.length;i++){
       			params[keys.get(i).value] = values.get(i).value;
       		}
       	}
		$.ajax({
			type : type,
			url : "<%=path%>/interface.web",
			data : params,
			dataType : "json",
			contentType: "application/x-www-form-urlencoded; charset=UTF-8",
			success : function(data) {
				$(".panel").removeClass("panel-default");
				
				if(data.status){
					$(".panel").addClass("panel-success");
					$("#result").text(JSON.stringify(data));
				}else{
					$(".panel").addClass("panel-warning");
					$("#result").text(JSON.stringify(data));
				}
				
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				$(".panel").removeClass("panel-default");
				$(".panel").addClass("panel-danger");
				$("#result").text(errorThrown);
			}
		});

	};

	function addParam(obj) {
		var tr = $(obj).parents('tr');
		var html = "<tr class='trParam'>" + $("#trModel").html() + "</tr>";
		tr.after(html);
	};

	function delParam(obj) {
		var tr = $(obj).parents('tr');
		tr.remove();
	};

	function loadModel(index) {
		var type = models[index].type;
		var action = models[index].action;
		var params = models[index].params;
		
		$("#action").val(action);
       	$("#type").val(type);
       	
       	$(".trParam").remove();
       	var html = "<tr class='trParam'>" + $("#trModel").html() + "</tr>";
       	for(var i=0;i<params.length;i++){
       		$("#tbParams").append(html);
    	}
       	var trParams = $(".trParam");
       	var keys = trParams.find("td:eq(0) input");
       	for(var i=0;i<keys.length;i++){
       		keys[i].value = params[i];
    	}
	};
	
	$(document).ready(function() {
    	for(var i=0;i<models.length;i++){
    		$("#divModels").append("<button type='button' class='btn btn-default' style='padding: 2px 8px;' onclick='loadModel(" + i + ");'>" + models[i].name + "</button>");
    	}
    });
</script>
</head>
<body style="background-size: inherit;">
	<div style="margin: 20px auto; position: relative; display: table; background-color: #fff; border-radius: 5px;">
		<div style="width: 680px;">
			<div style="padding: 20px;">
				<h3>参数设置</h3>
				<table class="table table-hover" id="tbParams">
					<caption></caption>
					<thead>
						<tr>
							<th>KEY</th>
							<th>VALUE</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><input type="text" class="form-control" value="TYPE" disabled></td>
							<td><select class="form-control" id="type">
									<option value="get">GET</option>
									<option value="post">POST</option>
							</select></td>
							<td><button type="button" class="btn btn-default disabled">
									<span class="glyphicon glyphicon glyphicon-plus"></span>
								</button>
								<button type="button" class="btn btn-default disabled">
									<span class="glyphicon glyphicon-minus"></span>
								</button></td>
						</tr>
						<tr>
							<td><input type="text" class="form-control" value="ACTION" disabled></td>
							<td><input type="text" class="form-control" id="action"></td>
							<td>
								<button type="button" class="btn btn-default" onclick="addParam(this);">
									<span class="glyphicon glyphicon glyphicon-plus"></span>
								</button>
								<button type="button" class="btn btn-default disabled">
									<span class="glyphicon glyphicon-minus"></span>
								</button>
							</td>
						</tr>
					</tbody>
				</table>

				<div class="panel panel-default">
					<div class="panel-heading">
						<h3 class="panel-title"><strong>请求结果</strong></h3>
					</div>
					<div class="panel-body" id="result" style="word-wrap:break-word;word-break:break-all;"></div>
				</div>
				<div>
					<table style="display: none;">
						<tr id="trModel">
							<td><input type="text" class="form-control key"></td>
							<td><input type="text" class="form-control value"></td>
							<td>
								<button type="button" class="btn btn-default" onclick="addParam(this);">
									<span class="glyphicon glyphicon glyphicon-plus"></span>
								</button>
								<button type="button" class="btn btn-default" onclick="delParam(this);">
									<span class="glyphicon glyphicon-minus"></span>
								</button>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>
	<div class="nav pinned" style="right: 15%; top: 100px; position: fixed;">
		<h4><strong>操作</strong></h4>
		<div class="btn-group btn-group-vertical">
			<button type="button" class="btn btn-default" onclick="delResult();">清空结果</button>
			<button type="button" class="btn btn-default" onclick="makeUrl();">生成URL</button>
			<button type="button" class="btn btn-default" onclick="doPost();">发送请求</button>
		</div>
	</div>
	<div class="nav pinned" style="left: 10%; top: 0px; position: fixed;">
		<h4 onclick="$('#divModels').toggle();"><strong>模板</strong></h4>
		<div class="btn-group-vertical" id="divModels"></div>
	</div>
</body>
</html>

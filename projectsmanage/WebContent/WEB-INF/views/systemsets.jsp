<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<meta name="robots" content="all">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>系统设置</title>

<link href="resources/jquery-ui-1.12.1.custom/jquery-ui.min.css"
	rel="stylesheet">
<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css"
	rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>

<script type="text/javascript">
	$(document).ready(function() {
		$.webeditor.getHead();

		$('.navbar-example').scrollspy({
			target : '.navbar-example'
		});
		
		getDefaultValues();
	});

	function getDefaultValues() {
		jQuery.post("./systemsets.web", {
			"atn" : "getdefaultvalues"
		}, function(json) {
			if (json.ret && json.ret == 1) {
				var configs = json.configs;
        		for(var index in configs) {
        			var obj = $("#config_" + configs[index].moduleid + "_" + configs[index].id);
        			if(obj) {
        				$(obj).val(configs[index].defaultValue);
        			}
        		}
			}
		}, "json");
	}

	function setDefaultValues() {
		var params = {
			"atn" : "setdefaultvalues"
		};
		$(".systemSet").each(function() {
			var id = $(this).attr("id");
			if (id.indexOf("config_") >= 0) {
				params[id] = $(this).val();
			}
		});

		jQuery.post("./systemsets.web", params, function(json) {
			if (json.ret && json.ret == 1) {
				$.webeditor.showMsgLabel("success", "保存成功");
			} else {
				$.webeditor.showMsgLabel("success", "保存失败");
			}
		}, "json");
	}
</script>
</head>
<body>
	<div class="container" style="max-width: 80%;">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px">
			<div id="navbar-example" style="width: 16%; float: left;">
				<ul class="nav nav-pills nav-stacked">
					<li class="active"><a href="#sc1">项目基础配置</a></li>
				</ul>
			</div>
			<div class="navbar-example"
				style="width: 83%; float: left; overflow-y: auto;" data-spy="scroll"
				data-target="#navbar-example">
				<div class="panel panel-default" id="sc1">
					<div class="panel-heading">项目基础配置</div>
					<div class="panel-body">
						<table class="table">
							<tr>
								<td>质检项目库</td>
								<td><select class="form-control systemSet" id="config_1_1">
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if
												test="${configDBModel['connname'].equals('projectmanager') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}(${configDBModel['ip']})</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td>质检任务库</td>
								<td><select class="form-control systemSet" id="config_1_2">
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}(${configDBModel['ip']})</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td>改错项目库</td>
								<td><select class="form-control systemSet" id="config_2_9">
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if
												test="${configDBModel['connname'].equals('projectmanager') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}(${configDBModel['ip']})</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td>改错任务库</td>
								<td><select class="form-control systemSet" id="config_2_10">
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}(${configDBModel['ip']})</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td>改错错误库</td>
								<td><select class="form-control systemSet" id="config_2_16">
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('error') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}(${configDBModel['ip']})</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td>改错任务组织方式</td>
								<td><select class="form-control systemSet" id="config_2_13">
										<option value="0">多要素组织</option>
										<option value="1">单要素组织</option>
								</select></td>
							</tr>
							<tr>
								<td>错误个数</td>
								<td><input type="text" class="form-control systemSet"
									id="config_2_14" value="100"></td>
							</tr>
							<tr>
								<td>错误距离</td>
								<td><input type="text" class="form-control systemSet"
									id="config_2_15" value="10000"></td>
							</tr>
							<tr>
								<td></td>
								<td><div class="btn-group">
										<button type="button" class="btn btn-default"
											onclick="setDefaultValues();">保存</button>
									</div></td>
							</tr>
						</table>
					</div>
					<div class="panel-footer"></div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
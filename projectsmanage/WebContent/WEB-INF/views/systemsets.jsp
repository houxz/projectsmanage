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
				var configDefaultModels = json.configDefaultModels;
        		for(var index in configDefaultModels) {
        			var configid = configDefaultModels[index].id;
        			var defaultValue = configDefaultModels[index].defaultvalue;
        			var obj = $("#config_" + configid);
        			if(obj) {
        				$(obj).val(defaultValue);
        			}
        		}
			}
			$(".row").show();
		}, "json");
	}

	function setDefaultValues(fid) {
		var params = {
			"atn" : "setdefaultvalues"
		};
		$("#" + fid + " .systemSet").each(function() {
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
	<div class="container" style="max-width: 80%; padding-top: 20px;">
		<div id="headdiv"></div>
		<div class="row" style="display: none;">
			<div id="navbar-example" style="width: 16%; float: left;">
				<ul class="nav nav-pills nav-stacked">
					<li class="active"><a href="#sc1" style="color: #000000;">质检任务库配置</a></li>
					<li><a href="#sc2" style="color: #000000;">质检错误库配置</a></li>
					<li><a href="#sc3" style="color: #000000;">编辑任务库配置</a></li>
					<li><a href="#sc4" style="color: #000000;">编辑数据库配置</a></li>
					<li><a href="#sc5" style="color: #000000;">资料库配置</a></li>
					<li><a href="#sc6" style="color: #000000;">其它配置</a></li>
				</ul>
			</div>
			<div class="navbar-example"
				style="width: 83%; height: 80vh; float: left; overflow-y: auto; position: relative;" data-spy="scroll"
				data-target="#navbar-example" data-offset="0" >
				<div class="panel panel-default" id="sc1">
					<div class="panel-heading">质检任务库配置</div>
					<div class="panel-body">
						<table class="table" style="margin-bottom: 0;">
							<tr>
								<td class="configKey">综检改错项目</td>
								<td><select class="form-control systemSet" id="config_2">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">NR/FC项目</td>
								<td><select class="form-control systemSet" id="config_3">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">关系附属表项目</td>
								<td><select class="form-control systemSet" id="config_4">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">全国质检项目</td>
								<td><select class="form-control systemSet" id="config_5">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">行政区划项目</td>
								<td><select class="form-control systemSet" id="config_38">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							
							<tr>
								<td class="configKey"></td>
								<td><div class="btn-group">
										<button type="button" class="btn btn-default"
											onclick="setDefaultValues('sc1');">保存</button>
									</div>
								</td>
							</tr>
						</table>
					</div>
				</div>
				<div class="panel panel-default" id="sc2">
					<div class="panel-heading">质检错误库配置</div>
					<div class="panel-body">
						<table class="table" style="margin-bottom: 0;">
							<tr>
								<td class="configKey">综检改错项目</td>
								<td><select class="form-control systemSet" id="config_25">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('error') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">NR/FC项目</td>
								<td><select class="form-control systemSet" id="config_26">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('error') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">关系附属表项目</td>
								<td><select class="form-control systemSet" id="config_27">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('error') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">全国质检项目</td>
								<td><select class="form-control systemSet" id="config_28">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('error') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">POI编辑项目</td>
								<td><select class="form-control systemSet" id="config_29">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('error') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">易淘金线上编辑项目</td>
								<td><select class="form-control systemSet" id="config_37">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('error') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">行政区划项目</td>
								<td><select class="form-control systemSet" id="config_40">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('error') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							
							<tr>
								<td class="configKey"></td>
								<td><div class="btn-group">
										<button type="button" class="btn btn-default"
											onclick="setDefaultValues('sc2');">保存</button>
									</div>
								</td>
							</tr>
						</table>
					</div>
				</div>
				<div class="panel panel-default" id="sc3">
					<div class="panel-heading">编辑任务库配置</div>
					<div class="panel-body">
						<table class="table" style="margin-bottom: 0;">
							<tr>
								<td class="configKey">综检改错项目</td>
								<td><select class="form-control systemSet" id="config_9">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">NR/FC项目</td>
								<td><select class="form-control systemSet" id="config_10">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">关系附属表项目</td>
								<td><select class="form-control systemSet" id="config_11">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">POI编辑项目</td>
								<td><select class="form-control systemSet" id="config_19">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">整图编辑项目</td>
								<td><select class="form-control systemSet" id="config_24">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">易淘金线上编辑项目</td>
								<td><select class="form-control systemSet" id="config_35">
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">行政区划项目</td>
								<td><select class="form-control systemSet" id="config_39">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('task') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							
							<tr>
								<td class="configKey"></td>
								<td><div class="btn-group">
										<button type="button" class="btn btn-default"
											onclick="setDefaultValues('sc3');">保存</button>
									</div>
								</td>
							</tr>
						</table>
					</div>
				</div>
				<div class="panel panel-default" id="sc4">
					<div class="panel-heading">编辑数据库配置</div>
					<div class="panel-body">
						<table class="table">
							<tr>
								<td class="configKey">综检改错项目</td>
								<td><select class="form-control systemSet" id="config_30">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('data') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">NR/FC项目</td>
								<td><select class="form-control systemSet" id="config_31">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('data') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">关系附属表项目</td>
								<td><select class="form-control systemSet" id="config_32">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('data') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">POI编辑项目</td>
								<td><select class="form-control systemSet" id="config_33">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('data') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">整图编辑项目</td>
								<td><select class="form-control systemSet" id="config_34">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('data') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">易淘金线上编辑项目</td>
								<td><select class="form-control systemSet" id="config_36">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('data') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">行政区划项目</td>
								<td><select class="form-control systemSet" id="config_41">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('data') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							
							<tr>
								<td class="configKey"></td>
								<td><div class="btn-group">
										<button type="button" class="btn btn-default"
											onclick="setDefaultValues('sc4');">保存</button>
									</div>
								</td>
							</tr>
						</table>
					</div>
				</div>
				<div class="panel panel-default" id="sc5">
					<div class="panel-heading">资料库配置</div>
					<div class="panel-body">
						<table class="table" style="margin-bottom: 0;">
							<tr>
								<td class="configKey">POI编辑项目</td>
								<td><select class="form-control systemSet" id="config_22">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('fielddata') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<td class="configKey">易淘金线上编辑项目</td>
								<td><select class="form-control systemSet" id="config_42">
										<option value=""></option>
										<c:forEach items="${configDBModels }" var="configDBModel">
											<c:if test="${configDBModel['connname'].equals('fielddata') }">
												<option value="${configDBModel['id']}">${configDBModel['dbname']}<c:if test="${not empty configDBModel['dbschema']}">.${configDBModel['dbschema']}</c:if>(${configDBModel['ip']}:${configDBModel['port']}_<c:if test="${configDBModel['online'] == 0}">线上</c:if><c:if test="${configDBModel['online'] == 1}">线下</c:if>)</option>
											</c:if>
										</c:forEach>
								</select></td>
							</tr>
							
							<tr>
								<td class="configKey"></td>
								<td><div class="btn-group">
										<button type="button" class="btn btn-default"
											onclick="setDefaultValues('sc5');">保存</button>
									</div>
								</td>
							</tr>
						</table>
					</div>
				</div>
				<div class="panel panel-default" id="sc6">
					<div class="panel-heading">其它配置</div>
					<div class="panel-body">
						<table class="table">
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
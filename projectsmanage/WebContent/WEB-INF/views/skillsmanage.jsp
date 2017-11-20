<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.emg.projectsmanage.common.ParamUtils"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<%
	String path = request.getContextPath();
	String pagemsg = ParamUtils.getAttribute(request, "pagemsg");
%>
<!DOCTYPE html>
<html>
<head>
<title>人员技能</title>
<meta charset="UTF-8" />
<meta name="robots" content="none">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<link href="resources/jquery-ui-1.12.1.custom/jquery-ui.min.css" rel="stylesheet">
<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css" rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />
<link href="resources/jquery.zTree-3.5.29/css/zTreeStyle/zTreeStyle.css" rel="stylesheet" />
<link href="resources/css/message.css" rel="stylesheet">

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/jquery-ui-1.12.1.custom/jquery-ui.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src="resources/jquery.zTree-3.5.29/js/jquery.ztree.core.min.js"></script>
<script src="resources/jquery.zTree-3.5.29/js/jquery.ztree.excheck.min.js"></script>
<script src="resources/js/message.js"></script>

<script type="text/javascript">
        var setting = {
        		check: {
        			enable: true,
        			chkboxType:{ "Y" : "ps", "N" : "ps" }
        		},
        		data: {
        			simpleData: {
        				enable: true
        			}
        		},
        		callback: {
        			onClick: onClick
        		}
        };
        $(document).ready(function() {
        	$.webeditor.getHead();
        	loadeple();
        });
        
        function loadeple() {
            jQuery.post("<%=path%>/skillsmanage.web", 
                    {"atn":"epletree"},
                    function(json) {
                        $.fn.zTree.init($("#epletree"), setting, json.eplelist);
                       // setCheck();
                        $("#py").bind("change", setCheck);
                        $("#sy").bind("change", setCheck);
                        $("#pn").bind("change", setCheck);
                        $("#sn").bind("change", setCheck);
                    }, "json");
        }
        function setCheck() {
            var zTree = $.fn.zTree.getZTreeObj("epletree"),
            py = $("#py").attr("checked")? "p":"",
            sy = $("#sy").attr("checked")? "s":"",
            pn = $("#pn").attr("checked")? "p":"",
            sn = $("#sn").attr("checked")? "s":"",
            type = { "Y":py + sy, "N":pn + sn};
            zTree.setting.check.chkboxType = type;
        }
        
        function onClick(event, treeId, treeNode, clickFlag) {
        	if (clickFlag == 1) { //选择
        		if (treeNode.isdept == 0) {
        			getEpleSkill(treeNode.id);
        		}
        	}
        }
        
        function getEpleSkill(userid) {
        	var sysid = $("#systems").val();
        	jQuery.post("<%=path%>/skillsmanage.web", 
                    {"atn":"geteplelevel", "userid":userid,"sysid":sysid},
                    function(json) {
                    	if(json.epleSkillList.length <= 0) {
                    		$("#epleroletb").empty();
                    		return;
                    	}
                        var html = new Array();
                        html.push("<thead><tr><th>技能领域</th><th>角色</th><th>技能等级</th><th>操作</th></tr></thead><tbody>");
                        for (var i=0; i<json.epleSkillList.length;i++) {
                            html.push("<tr>");
                            html.push("<td>" + json.epleSkillList[i].skillModuleDesc + "</td>");
                            html.push("<td>" + json.epleSkillList[i].rolename + "</td>");
                            html.push("<td>" + json.epleSkillList[i].skillLevelDesc + "</td>");
                            html.push("<td> <button class='btn btn-default btn-sm' onclick='delEpleSill("+json.epleSkillList[i].id + ","+ json.epleSkillList[i].skillmodule + "," + json.epleSkillList[i].userid +")'>删除</button></td>");
                        }
                        html.push("</tbody>");
                        $("#epleroletb").html(html.join(''));
                    }, "json");
        }
        
        function addEpleSkill() {
        	var sysid = $("#systems").val();
        	var zTree = $.fn.zTree.getZTreeObj("epletree");
        	var rolename = $("#rolelist").val();
        	nodes = zTree.getCheckedNodes(true);
            var checkCount = nodes.length;
            var epleid = new Array();
            var eplename = new Array();
            for (var i=0; i<checkCount; i++) {
            	if (nodes[i].isdept == 0) {
            		epleid.push(nodes[i].id);
            		eplename.push(nodes[i].name);
            	}
            }
            if (epleid.length < 1) {
            	$.webeditor.showMsgBox("alert", "请勾选左边的人员");
            	return ;
            }
            var skillmodule = $("#skillmoduleselect").val();
            var skilllevel = $("#skilllevelselect").val();
            $.post("<%=path%>/skillsmanage.web",
                    {"atn":"addEpleSkill",
            			"sysid":sysid,
            			"skillmodule":skillmodule,
            			"epleid":epleid.join(','),
            			"eplename":eplename.join(','),
            			"skilllevel":skilllevel,
            			"rolename":rolename},
                    function(json) {
                        if (json.result == 1) {
                            $.webeditor.showMsgBox("info", "添加成功");
                        } else {
                            $.webeditor.showMsgBox("alert", json.msg);
                        }
                    }, "json");
        }
        
        function delEpleSill(id,skillmodule,userid) {
        	var sys = $("#systems").val();
        	 $.post("<%=path%>/skillsmanage.web", {
			"atn" : "deleplesill",
			"id" : id,
			"skillmodule" : skillmodule,
			"sysid" : sys
		}, function(json) {
			if (json.result == 0) {
				$.webeditor.showMsgBox("info", "删除成功");
				getEpleSkill(userid);
			} else {
				$.webeditor.showMsgBox("alert", json.msg);
			}
		}, "json");
	}
</script>
</head>
<body>
	<div class="container">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px">
			<div class="col-md-4">
				<div>
					<span class="label label-default">人员技能等级信息</span>
				</div>
				<div class="well">
					<ul id="tree" class="ztree"></ul>
					<ul id=epletree class="ztree"></ul>
				</div>
			</div>
			<div class="col-md-8">
				<div>
					<span class="label label-default">技能等级信息</span>
				</div>
				<div class="well">
					<blockquote>
						<p>
							显示人员技能等级。<br /> <span style="color: red;">点击</span>左侧的人员，可显示该人员拥有的技能等级。
						</p>
					</blockquote>
					<div style="margin: 0 5%;">
						<table class="table table-hover table-condensed" id="epleroletb"></table>
					</div>
					<hr />
					<blockquote>
						<p>
							为人员添加技能等级。<br /> <span style="color: red;">勾选</span>左侧的人员，选择技能领域、角色、技能等级，然后点击添加人员技能等级。
						</p>
					</blockquote>
					<div id="skilllistdiv" style="margin: 0 5%;">
						<select id="skillmoduleselect" class="form-control" style="width: 40%; margin-bottom: 2px;">
							<c:forEach items="${skillModules }" var="skillModule">
								<option value='${skillModule["value"] }'>${skillModule["desc"]}</option>
							</c:forEach>
						</select> <select id="rolelist" class="form-control" style="width: 40%; margin-bottom: 2px;">
							<c:forEach items="${roles }" var="role">
								<option value='${role["name"] }'>${role["remark"] }</option>
							</c:forEach>
						</select> <select id="skilllevelselect" class="form-control" style="width: 20%;">
							<c:forEach items="${skillLevels }" var="skillLevel">
								<option value='${skillLevel["value"] }'>${skillLevel["desc"]}</option>
							</c:forEach>
						</select>
					</div>
					<div style="margin: 3% 5%;">
						<button type="button" class="btn btn-default"
							onclick="addEpleSkill();">添加人员技能等级</button>
					</div>
					<hr />
				</div>
			</div>
		</div>
	</div>
</body>
</html>
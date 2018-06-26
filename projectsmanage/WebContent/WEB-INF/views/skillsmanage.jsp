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

<link href="resources/bootstrap-3.3.7/css/bootstrap.min.css" rel="stylesheet" />
<link href="resources/css/css.css" rel="stylesheet" />
<link href="resources/bootstrap-treeview-1.2.0/bootstrap-treeview.min.css" rel="stylesheet" />

<script src="resources/jquery/jquery-3.2.1.min.js"></script>
<script src="resources/js/webeditor.js"></script>
<script src="resources/bootstrap-3.3.7/js/bootstrap.min.js"></script>
<script src="resources/bootstrap-treeview-1.2.0/bootstrap-treeview.min.js" /></script>

<script type="text/javascript">
        $(document).ready(function() {
        	$.webeditor.getHead();
        	loadeple();
        });
        
        function loadeple() {
            jQuery.post("<%=path%>/usersmanage.web", 
                {"atn":"epletree"},
                function(json) {
                    bootstrapTree = $('#treeview-checkable').treeview({
                    	data: $.webeditor.transJsonData2Tree(json.eplelist, "id", "pId", "nodes"),
                        showIcon: false,
                        showCheckbox: true,
                        highlightSelected: true,
                        expandIcon: 'glyphicon glyphicon-menu-right',
                        collapseIcon: 'glyphicon glyphicon-menu-down',
                        selectedColor: "#000000",
                        selectedBackColor: "#e7e7e7",
                        onNodeChecked: function(event, node) {
                        	if(node.isdept == 1) {//部门节点
                        		var children = node.nodes;
                        		$.each(children, function(index, child){
                        			bootstrapTree.treeview('checkNode', [ child.nodeId, { silent: false }]);
                        		});
                        	}
                        },
                        onNodeUnchecked: function(event, node) {
                        	if(node.isdept == 1) {//部门节点
                        		var children = node.nodes;
                        		$.each(children, function(index, child){
                        			bootstrapTree.treeview('uncheckNode', [ child.nodeId, { silent: false }]);
                        		});
                        	}
                        },
                        onNodeSelected: function(event, node) {
                        	if(node.isdept == 0) {//人员节点
                        		$("#eplerolediv").empty();
                        		getEpleSkill(node.id, node.name);
                        	}
                        }
                      });
                }, "json");
        }
        
        function getEpleSkill(userid, username) {
        	var sysid = $("#systems").val();
        	jQuery.post("<%=path%>/skillsmanage.web", 
                    {"atn":"geteplelevel", "userid":userid,"sysid":sysid},
                    function(json) {
                    	if(json.epleSkillList.length <= 0) {
                    		$("#epleroletb").empty();
                    		return;
                    	}
                        var html = new Array();
                        html.push("<table class='table table-condensed'>");
                        html.push("<tr><td><div>" + username + "</div></td><td></td><td></td><td></td></tr>");
                        html.push("<tr><td>技能领域</td><td>角色</td><td>技能等级</td><td>操作</td></tr>");
                        for (var i=0; i<json.epleSkillList.length;i++) {
                            html.push("<tr>");
                            html.push("<td>" + json.epleSkillList[i].skillModuleDesc + "</td>");
                            html.push("<td>" + json.epleSkillList[i].rolename + "</td>");
                            html.push("<td>" + json.epleSkillList[i].skillLevelDesc + "</td>");
                            html.push("<td><span title=\"删除\" class=\"glyphicon glyphicon-remove-circle\" aria-hidden=\"true\" onclick=\"delEpleSill(" + json.epleSkillList[i].id + "," + json.epleSkillList[i].skillmodule + "," + json.epleSkillList[i].userid + ",'" + json.epleSkillList[i].username + "');\"></span></td></tr>");
                        }
                        html.push("</table>");
                        $("#epleroletb").html(html.join(''));
                    }, "json");
        }
        
        function addEpleSkill() {
        	var sysid = $("#systems").val();
        	var expandibleNodes = new Array();
        	if(bootstrapTree)
        		expandibleNodes = bootstrapTree.treeview('getChecked');
        	var rolename = $("#rolelist").val();
            var checkCount = expandibleNodes.length;
            var epleid = new Array();
            var eplename = new Array();
            for (var i=0; i<checkCount; i++) {
            	if (expandibleNodes[i].isdept == 0) {
            		epleid.push(expandibleNodes[i].id);
            		eplename.push(expandibleNodes[i].name);
            	}
            }
            if (epleid.length < 1) {
            	$.webeditor.showMsgLabel("alert", "请勾选左边的人员");
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
            			"rolename":rolename
            		}, function(json) {
                        if (json.result == 1) {
                            $.webeditor.showMsgLabel("success", "人员技能等级添加成功");
                        } else {
                            $.webeditor.showMsgLabel("alert", "人员技能等级添加失败");
                        }
                    }, "json");
        }
        
        function delEpleSill(id, skillmodule, userid, username) {
        	var sys = $("#systems").val();
        	$.post("<%=path%>/skillsmanage.web",
        			{"atn" : "deleplesill",
        				"id" : id,
        				"skillmodule" : skillmodule,
        				"sysid" : sys
        			}, function(json) {
        				if (json.result == 0) {
        					$.webeditor.showMsgLabel("success", "人员技能等级删除成功");
        					getEpleSkill(userid, username);
        				} else {
        					$.webeditor.showMsgLabel("alert", "人员技能等级删除失败");
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
				<div class="panel panel-default">
					<div class="panel-body">
						<div class="input-group">
							<input class="form-control" id="input-expand-node"
								placeholder="查找人员">
							<div class="input-group-btn">
								<button type="button" class="btn btn-default" tabindex="-1"
									onclick="findExpandibleNodess();">
									<span class="glyphicon glyphicon-search" aria-hidden="true">&nbsp;</span>查找
								</button>
								<button type="button" class="btn btn-default" tabindex="-1"
									onclick="checkExpandibleNodess();">
									<span class="glyphicon glyphicon-check" aria-hidden="true">&nbsp;</span>勾选
								</button>
							</div>
						</div>
					</div>
					<div id="treeview-checkable"
						style="height: 550px; overflow: auto; font-size: 13px;"></div>
				</div>
			</div>
			<div class="col-md-8">
				<div class="panel panel-default">
					<div class="panel-body">
					<blockquote>
						<p>
							显示人员技能等级。<br /> <span style="color: red;">点击</span>左侧的人员，可显示该人员拥有的技能等级。
						</p>
					</blockquote>
					<div id="epleroletb" style="width: 60%; margin: 0 5%;"></div>
					<hr />
					<blockquote>
						<p>
							为人员添加技能等级。<br /> <span style="color: red;">勾选</span>左侧的人员，选择技能领域、角色、技能等级，然后点击添加人员技能等级。
						</p>
					</blockquote>
					<div class="input-group" style="width: 50%; margin: 2px 5%;">
						<span class="input-group-addon" style="width: 30%;text-align: left;">技能领域：</span>
						<select id="skillmoduleselect" class="form-control">
							<c:forEach items="${skillModules }" var="skillModule">
								<option value='${skillModule["value"] }'>${skillModule["desc"]}</option>
							</c:forEach>
						</select>
					</div>
					<div class="input-group" style="width: 50%; margin: 2px 5%;">
						<span class="input-group-addon" style="width: 30%;text-align: left;">角色：</span>
						<select id="rolelist" class="form-control">
							<c:forEach items="${roles }" var="role">
								<option value='${role["name"] }'>${role["remark"] }</option>
							</c:forEach>
						</select>
					</div>
					<div class="input-group" style="width: 50%; margin: 2px 5%;">
						<span class="input-group-addon" style="width: 30%;text-align: left;">技能等级：</span>
						<select id="skilllevelselect" class="form-control">
							<c:forEach items="${skillLevels }" var="skillLevel">
								<option value='${skillLevel["value"] }'>${skillLevel["desc"]}</option>
							</c:forEach>
						</select>
					</div>
					<button type="button" class="btn btn-default" name="register"
						onclick="addEpleSkill();" style="margin: 2px 5%;">
						<span class="glyphicon glyphicon-plus" aria-hidden="true">&nbsp;</span>添加人员技能等级
					</button>
				</div>
			</div>
		</div>
	</div>
	</div>
</body>
</html>
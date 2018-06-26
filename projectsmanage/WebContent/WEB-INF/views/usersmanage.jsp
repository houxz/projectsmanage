<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt'%>
<%
	String path = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
<title>人员信息</title>
<meta charset="UTF-8" />
<meta name="robots" content="none">
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
        var bootstrapTree = null;
        
        $(document).ready(function() {
        	$.webeditor.getHead();
        	loadeple();
        });
        
        function findExpandibleNodess() {
        	if(bootstrapTree) {
        		bootstrapTree.treeview('uncheckAll', { silent: true });
        		bootstrapTree.treeview('collapseAll', { silent: true });
        		return bootstrapTree.treeview('search', [ $('#input-expand-node').val(), { ignoreCase: false, exactMatch: false } ]);
        	} else {
        		return null;
        	}
            
        }
        function checkExpandibleNodess() {
        	if(bootstrapTree) {
	        	var expandibleNodes = findExpandibleNodess();
	        	bootstrapTree.treeview('checkNode', [ expandibleNodes, { silent: false }]);
        	}
        }
        
        
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
                            		getEpleRoles(node.id, node.name);
                            	}
                            }
                          });
                    }, "json");
        }
        
        function getEpleRoles(uid, username) {
        	$("#eplerolediv").empty();
        	jQuery.post("<%=path%>/usersmanage.web", 
                    {"atn":"getepleroles", "id":uid},
                    function(json) {
                    	if(json.epleRolesList.length > 0) {
	                        var html = new Array();
	                        html.push("<table class='table table-condensed'>");
	                        html.push("<tr><td><div>" + username + "</div></td><td></td></tr>");
	                        for (var i=0; i<json.epleRolesList.length;i++) {
	                            html.push("<tr><td><div>" + json.epleRolesList[i].remark + "</div></td>");
	                            html.push("<td><span title=\"删除" + json.epleRolesList[i].remark + "权限\" class=\"glyphicon glyphicon-remove-circle\" aria-hidden=\"true\" onclick=\"delEpleRole(" + json.epleRolesList[i].urid + ", " + uid + ", '" + username + "');\"></span></td></tr>");
	                        }
	                        html.push("</table>");
	                        $("#eplerolediv").html(html.join(''));
                    	}
                    }, "json");
        }
        
        function addEpleRole() {
        	var expandibleNodes = new Array();
        	if(bootstrapTree)
        		expandibleNodes = bootstrapTree.treeview('getChecked');
            var checkCount = expandibleNodes.length;
            var epleid = new Array();
            for (var i=0; i<checkCount; i++) {
            	if (expandibleNodes[i].isdept == 0) {
            		epleid.push(expandibleNodes[i].id);
            	}
            }
            if (epleid.length < 1) {
            	$.webeditor.showMsgLabel("alert", "请勾选左边的人员");
            	return ;
            }
            $.post("<%=path%>/usersmanage.web", 
                    {"atn":"addeplerole", "epleid":epleid.join(','), "roleid":$("#rolelistselect").val()},
                    function(json) {
                        if (json.result == 1) {
                            $.webeditor.showMsgLabel("success", "人员权限添加成功");
                        } else {
                            $.webeditor.showMsgLabel("alert", "人员权限添加失败");
                        }
                    }, "json");
        }
        
        function delEpleRole(urid, uid, username) {
        	 $.post("<%=path%>/usersmanage.web", 
                     {"atn":"deleplerole", "urid":urid},
                     function(json) {
                         if (json.result == 1) {
                             $.webeditor.showMsgLabel("success", "人员权限删除成功");
                             getEpleRoles(uid, username);
                         } else {
                             $.webeditor.showMsgLabel("alert", "人员权限删除失败");
                         }
                     }, "json");
        }
        
        function addRole() {
            if ($("#rolename").val() == "" || $("#roleremark").val() == "") {
            	$.webeditor.showMsgLabel("alert", "必须填写权限名称和权限说明");
            	return ;
            }
            $.post("<%=path%>/usersmanage.web", {
				"atn" : "addrole",
				"name" : $("#rolename").val(),
				"remark" : $("#roleremark").val()
			}, function(json) {
				if (json.result == 1 && json.role) {
					$("#rolename").val("");
					$("#roleremark").val("");
					var newRole = json.role;
					var html = new Array();
					html.push("<option value='"+newRole.id+"'>" + newRole.remark
							+ "</option>");
					$("#rolelistselect").append(html.join(''));
					$.webeditor.showMsgLabel("success", "权限添加成功");
				} else {
					$.webeditor.showMsgLabel("alert", "权限添加失败");
				}
			}, "json");
		}
</script>
</head>
<body>
	<div class="container">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px">
			<div class="col-md-6">
				<div class="panel panel-default">
					<div class="panel-body">
						<div class="input-group">
							<input class="form-control" id="input-expand-node"
								placeholder="查找人员" onkeypress="if (event.keyCode==13) { findExpandibleNodess(); }">
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
			<div class="col-md-6">
				<div class="panel panel-default">
					<div class="panel-body">
						<blockquote>
							<p>
								显示人员权限。<br /> <span style="color: red;">点击</span>左侧的人员，可显示该人员拥有的权限。
							</p>
						</blockquote>
						<div id="eplerolediv" style="width: 60%; margin: 0 5%;"></div>
						<hr />
						<blockquote>
							<p>
								为人员添加权限。<br /> <span style="color: red;">勾选</span>左侧的人员，选择权限，然后点击添加人员权限。
							</p>
						</blockquote>
						<div class="input-group" style="width: 60%; margin: 0 5%;">
							<select id="rolelistselect" class="form-control">
								<c:forEach items="${rolelist }" var="role">
									<option value='${role["id"] }'>${role["remark"]}</option>
								</c:forEach>
							</select> <span class="input-group-btn">
								<button type="button" class="btn btn-default"
									onclick="addEpleRole();">
									<span class="glyphicon glyphicon-plus" aria-hidden="true">&nbsp;</span>添加人员权限
								</button>
							</span>
						</div>
						<!-- /input-group -->
						<hr />
						<blockquote>
							<p>
								仅添加权限。<br />输入权限名称和描述，点击添加权限。
							</p>
						</blockquote>
						<div class="input-group" style="width: 60%; margin: 2px 5%;">
							<span class="input-group-addon">权限名称：</span> <input type="text"
								class="form-control required" id="rolename" name="rolename"
								placeholder="请输入权限名称" value='' />
						</div>
						<div class="input-group" style="width: 60%; margin: 2px 5%;">
							<span class="input-group-addon">权限描述：</span> <input type="text"
								class="form-control required" id="roleremark" name="roleremark"
								placeholder="请输入权限说明" value='' />
						</div>
						<button type="button" class="btn btn-default" name="register"
							onclick="addRole();" style="margin: 2px 5%;">
							<span class="glyphicon glyphicon-plus" aria-hidden="true">&nbsp;</span>添加权限
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
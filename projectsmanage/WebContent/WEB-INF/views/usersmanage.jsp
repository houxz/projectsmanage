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
        	setRoleList();
        });
        
        function loadeple() {
            jQuery.post("<%=path%>/usersmanage.web", 
                    {"atn":"epletree"},
                    function(json) {
                        $.fn.zTree.init($("#epletree"), setting, json.eplelist);
                      //  setCheck();
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
        			getEpleRoles(treeNode.id);
        		}
        	}
        }
        function getEpleRoles(uid) {
        	jQuery.post("<%=path%>/usersmanage.web", 
                    {"atn":"getepleroles", "id":uid},
                    function(json) {
                        var html = new Array();
                        html.push("<ul>");
                        for (var i=0; i<json.epleRolesList.length;i++) {
                            html.push("<li>"+json.epleRolesList[i].remark);
                            html.push("<a style='margin-left:50px' href='#' onclick=\"delEpleRole("+json.epleRolesList[i].urid+", "+uid+")\">删除</a></li>");
                        }
                        html.push("</ul>");
                        $("#eplerolediv").html(html.join(''));
                    }, "json");
        }
        
        function addEpleRole() {
        	var zTree = $.fn.zTree.getZTreeObj("epletree");
        	nodes = zTree.getCheckedNodes(true);
            var checkCount = nodes.length;
            var epleid = new Array();
            for (var i=0; i<checkCount; i++) {
            	if (nodes[i].isdept == 0) {
            		epleid.push(nodes[i].id);
            	}
            }
            if (epleid.length < 1) {
            	$.webeditor.showMsgBox("alert", "请勾选左边的人员");
            	return ;
            }
            $.post("<%=path%>/usersmanage.web", 
                    {"atn":"addeplerole", "epleid":epleid.join(','), "roleid":$("#rolelistselect").val()},
                    function(json) {
                        if (json.result == 1) {
                            $.webeditor.showMsgBox("info", "添加成功");
                        } else {
                            $.webeditor.showMsgBox("alert", json.msg);
                        }
                    }, "json");
        }
        
        function delEpleRole(urid, uid) {
        	 $.post("<%=path%>/usersmanage.web", 
                     {"atn":"deleplerole", "urid":urid},
                     function(json) {
                         if (json.result == 1) {
                             $.webeditor.showMsgBox("info", "删除成功");
                             getEpleRoles(uid);
                         } else {
                             $.webeditor.showMsgBox("alert", json.msg);
                         }
                     }, "json");
        }
        
        function addRole() {
            if ($("#rolename").val() == "" || $("#roleremark").val() == "") {
            	$.webeditor.showMsgBox("alert", "必须填写权限名称和权限说明");
            	return ;
            }
            $.post("<%=path%>/usersmanage.web", 
            	    {"atn":"addrole", "name":$("#rolename").val(), "remark":$("#roleremark").val()},
                    function(json) {
            	        if (json.result == 1) {
            	        	$.webeditor.showMsgBox("info", "添加成功");
            	        	setRoleList();
            	        } else {
            	        	$.webeditor.showMsgBox("alert", json.msg);
            	        }
                    }, "json");
        }
        
        function setRoleList() {
        	$.post("<%=path%>/usersmanage.web",
						{
							"atn" : "getrolelist"
						},
						function(json) {
							var html = new Array();
							html
									.push("<select id='rolelistselect'  style='padding: 5px; border-radius: 4px; min-width: 100px; margin-bottom: 5px;'>");
							for ( var i = 0; i < json.rolelist.length; i++) {
								html
										.push("<option value='"+json.rolelist[i].id+"'>"
												+ json.rolelist[i].remark
												+ "</option>");
							}
							html.push("</select>");
							$("#rolelistdiv").html(html.join(''));
						}, "json");
	}
</script>
</head>
<body>
	<div class="container">
		<div id="headdiv"></div>
		<div class="row" style="padding-top: 20px">
			<div class="col-md-6">
				<div>
					<span class="label label-default">人员信息</span>
				</div>
				<div class="well">
					<ul id="tree" class="ztree"></ul>
					<ul id=epletree class="ztree"></ul>
				</div>
			</div>
			<div class="col-md-6">
				<div>
					<span class="label label-default">权限信息</span>
				</div>
				<div class="well">
					<blockquote>
						<p>
							显示人员权限。<br />
							<span style="color: red;">点击</span>左侧的人员，可显示该人员拥有的权限。
						</p>
					</blockquote>
					<div id="eplerolediv"></div>
					<hr />
					<blockquote>
						<p>
							为人员添加权限。<br />
							<span style="color: red;">勾选</span>左侧的人员，选择权限，然后点击添加人员权限。
						</p>
					</blockquote>
					<div id="rolelistdiv"></div>
					<button type="button" class="btn btn-default"
						onclick="addEpleRole();">添加人员权限</button>
					<hr />
					<div class="form-group">
						<blockquote>
							<p>
								仅添加权限。<br />输入权限名称和描述，点击添加权限。
							</p>
						</blockquote>
						<label for="r_username">权限名称：</label> <input type="text"
							class="form-control required" id="rolename" name="rolename"
							placeholder="请输入权限名称" value='' />
					</div>
					<div class="form-group">
						<label for="r_username">权限描述：</label> <input type="text"
							class="form-control required" id="roleremark" name="roleremark"
							placeholder="请输入权限说明" value='' />
					</div>
					<button type="button" class="btn btn-default" name="register"
						onclick="addRole();">添加权限</button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
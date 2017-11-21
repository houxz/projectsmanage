// -----------------------------------------------------------------------------------人力资源分配 开始 -----------------------------------------------------------------------------------
{
	var addWorkers = [];
	var delWorkers = [];
	var hasWorkers = [];
	var worktype = 0;// 工作类型：0制作，1校验
	var currentProject;
	var orgUsers = [];
	// 根据Proid获取当前页面的项目。由于项目可拖拽，所以不能以INDEX为标记获取
	function getProjectByPID(proid) {
		for ( var p in currentPageProjects) {
			if (currentPageProjects[p].id == proid) {
				return currentPageProjects[p];
			}
		}
	}
	function setOrgUsers(pro) {
		if (worktype == 0) {
			orgUsers = currentProject.workusers.concat();
		} else {
			orgUsers = currentProject.checkusers.concat();
		}
	}
	// 原来没有，添加
	function addworker(proid, worktypeval) {
		currentProject = getProjectByPID(proid);
		worktype = worktypeval;
		setOrgUsers(currentProject);
		if (worktypeval == 1) {
			$("#hasChecker_ul").empty();
		} else {
			$("#hasWorker_ul").empty();
		}
		showSelect(worktypeval);
	}
	// 原来有，改变
	function changeworker(proid, worktypeval) {
		currentProject = getProjectByPID(proid);
		worktype = worktypeval;
		setOrgUsers(currentProject);
		if (worktypeval == 1) {
			$("#hasChecker_ul").empty();
		} else {
			$("#hasWorker_ul").empty();
		}
		
		var html = new Array();
		
		for ( var i = 0; i < orgUsers.length; i++) {
			var w = orgUsers[i];
			html.push("<li id='" + w.userid + "_li'>");
			html.push("<div class='input-group'>");
			html.push("<span class='input-group-addon'>" + w.username + "</span>");
			html.push("<span class='input-group-addon'><div onclick='removeWorker(" + w.userid + ");'><span class='glyphicon glyphicon-remove-circle' aria-hidden='true'></span></div></span>");
			html.push("</div></li>");
		}
		hasWorkers = orgUsers;
		if (worktypeval == 1) {
			$("#hasChecker_ul").append(html.join(""));
		} else {
			$("#hasWorker_ul").append(html.join(""));
		}
		showSelect(worktypeval);
	}

	// 显示人力分配对话框
	function showSelect(worktypeval) {
		var divID = "selectWorker";
		if (worktypeval == 1) {
			divID = "selectChecker";
		}

		$("#" + divID).dialog({
			modal : true,
			height : document.documentElement.clientHeight * 0.5,
			width : document.documentElement.clientWidth * 0.5,
			title : "作业人员分配",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			buttons : [ {
				text : "提交",
				class: "btn btn-default",
				click : function() {
					submitWorkers();
				}

			}, {
				text : "关闭",
				class: "btn btn-default",
				click : function() {
					cleanParas();
					$(this).dialog("close");
				}

			} ]
		});
	}
	// 检验某用户是否已添加过
	function checkIsExist(userid) {
		for ( var i = 0; i < hasWorkers.length; i++) {
			if (userid == hasWorkers[i].userid) {
				return true;
			}
		}

		for ( var i = 0; i < addWorkers.length; i++) {
			if (userid == addWorkers[i].userid) {
				return true;
			}
		}
		return false;
	}
	// 确认添加
	function confirmSelect() {
		var workusertype = "workerse";
		var workerul = "hasWorker_ul";
		if (worktype == 1) {
			workusertype = "checkers";
			workerul = "hasChecker_ul";
		}
		
		var user = $("#" + workusertype).val();
		if (user == 0) {
			$.webeditor.showMsgLabel("alert", "请选择作业人员");
			return;
		}
		var userinfo = user.split("_");
		if (checkIsExist(userinfo[0])) {
			$.webeditor.showMsgLabel("alert", "该人员已添加");
			return;
		}
		var w = {
			userid : userinfo[0],
			username : userinfo[1]
		};
		var flag = false;
		for ( var i = 0; i < delWorkers.length; i++) {
			if (w.userid == delWorkers[i].userid) {
				delWorkers.splice(i, 1);
				hasWorkers.push(w);
				flag = true;
				break;
			}
		}
		if (!flag) {
			addWorkers.push(w);
		}
		var html = new Array();
		html.push("<li id='" + w.userid + "_li'>");
		html.push("<div class='input-group'>");
		html.push("<span class='input-group-addon'>" + w.username + "</span>");
		html.push("<span class='input-group-addon'><div onclick='removeWorker(" + w.userid + ");'><span class='glyphicon glyphicon-remove-circle' aria-hidden='true'></span></div></span>");
		html.push("</div></li>");
		
		$("#" + workerul).append(html.join(""));
	}

	// 清空作业人员
	function removeAllWorkers() {
		var removeIDs = [];
		var divID = "hasWorker_ul";
		if (worktype == 1) {
			divID = "hasChecker_ul";
		}
		var idstr = "#" + divID + " li";
		$(idstr).each(function() {
			removeIDs.push($(this).attr("id"));
		});
		if (removeIDs.length <= 0) {
			$.webeditor.showMsgLabel("alert", "当前无作业人员");
			return;
		}
		for ( var i = 0; i < removeIDs.length; i++) {
			var id = removeIDs[i].split("_")[0];
			removeWorker(id);
		}
	}
	// 移除指定ID的人
	function removeWorker(userid) {
		var flag = false;
		for ( var i = 0; i < addWorkers.length; i++) {
			if (userid == addWorkers[i].userid) {
				addWorkers.splice(i, 1);
				break;
			}
		}
		for ( var i = 0; i < hasWorkers.length; i++) {
			if (userid == hasWorkers[i].userid) {
				var temp = {
					userid : hasWorkers[i].userid,
					username : hasWorkers[i].username

				};
				hasWorkers.splice(i, 1);
				delWorkers.push(temp);
				break;
			}
		}
		$("#" + userid + "_li").remove();
	}
	// 提交人力资源变更
	function submitWorkers() {
		if (addWorkers.length == 0 && delWorkers.length == 0) {
			$.webeditor.showMsgLabel("alert", "人员未发生变化");
			return;
		}
		var subStr = "[";
		for ( var i = 0; i < addWorkers.length; i++) {
			var w = addWorkers[i];
			subStr += '{"uid":' + w.userid + ', "username":"' + w.username + '"}';
			if (i != addWorkers.length - 1) {
				subStr += ",";
			}
		}
		subStr += ']';

		var delStr = "[";
		for ( var i = 0; i < delWorkers.length; i++) {
			var w = delWorkers[i];
			delStr += '{"uid":' + w.userid + ', "username":"' + w.username + '"}';
			if (i != delWorkers.length - 1) {
				delStr += ",";
			}
		}
		delStr += ']';

		$.ajax({
			async : false,
			type : 'POST',
			url : './projectsmanage.web',
			data : {
				atn : "add",
				proid : currentProject.id,
				addworkers : subStr,
				delworkers : delStr,
				worktype : worktype
			},
			dataType : 'json',
			success : function(result) {
			}
		}).done(function(json) {
			if (json.result == 0) {
				refresh();
				var divID = "selectWorker";
				if (worktype == 1) {
					divID = "selectChecker";
				}
				$("#" + divID).dialog("close");
				cleanParas();
			} else {
				$.webeditor.showMsgLabel("alert", "作业人员分配失败");
			}
		});
	}

	function cleanParas() {
		addWorkers = [];
		delWorkers = [];
		hasWorkers = [];
		worktype = 0;// 工作类型：0制作，1校验
		currentProject = null;
		orgUsers = [];
	}
}
// -----------------------------------------------------------------------------------人力资源分配
// 结束
// -----------------------------------------------------------------------------------

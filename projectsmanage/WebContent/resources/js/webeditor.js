/**
 * 图片编辑系统
 */
jQuery.webeditor = {
	getHead : function() {
		$("#headdiv").load(
				"head.web?fromurl=" + location.href + "&r=" + Math.random());
	},
	getFoot : function() {
		$("#footdiv").load("foot.web?r=" + Math.random());
	},
	gotoLogin : function(webpath) {
		top.location.href = webpath + "login.jsp";
	},
	showMsgBox : function(type, msg, w, h) {
		$("#comm_msgbox").remove();
		var html = new Array();
		var title = new String();
		if ("alert" == type) {
			title = "警告";
			html.push("<div id=\"comm_msgbox\">");
			html.push("    <div class=\"alert alert-danger alert-dismissable\" style=\"margin-bottom: 0;\">");
			html.push(msg);
			html.push("    </div>");
			html.push("</div>");
		} else if("info" == type) {
			title = "消息";
			html.push("<div id=\"comm_msgbox\">");
			html.push("    <div class=\"alert alert-success alert-dismissable\" style=\"margin-bottom: 0;\">");
			html.push(msg);
			html.push("    </div>");
			html.push("</div>");
		} else {
			title = "消息";
			html.push("<div id=\"comm_msgbox\">");
			html.push("    <div class=\"alert alert-success alert-dismissable\" style=\"margin-bottom: 0;\">");
			html.push(msg);
			html.push("    </div>");
			html.push("</div>");
		}
		$("body").append(html.join(""));
		var opt = {
			title : title,
			autoOpen : true,
			width : w ? w : 550,
			modal : true,
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			buttons : {
				"确定" : function() {
					$(this).dialog("close");
				}
			}
		};
		if (h) {
			opt.height = h;
		}
		$('#comm_msgbox').dialog(opt);
	},
	showMsgLabel : function(type, msg, w, h) {
		var speed = 300;
		var html = new Array();
		html.push("<div id=\"comm_msglabel\" style=\"display:none;position:fixed;right:5%;bottom:6%;min-width:20%;z-index:99999;\">");
		if ("success" == type) {
			html.push("<div class=\"alert alert-success alert-dismissable\">");
			html.push("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
			html.push("<span class=\"glyphicon glyphicon-ok-sign\" aria-hidden=\"true\">&nbsp;</span>");
			html.push(msg);
			html.push("</div>");
		} else if ("info" == type) {
			html.push("<div class=\"alert alert-info alert-dismissable\">");
			html.push("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
			html.push("<span class=\"glyphicon glyphicon-info-sign\" aria-hidden=\"true\">&nbsp;</span>");
			html.push(msg);
			html.push("</div>");
		} else if ("warning" == type) {
			html.push("<div class=\"alert alert-warning alert-dismissable\">");
			html.push("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
			html.push("<span class=\"glyphicon glyphicon-question-sign\" aria-hidden=\"true\">&nbsp;</span>");
			html.push(msg);
			html.push("</div>");
		} else if ("alert" == type) {
			html.push("<div class=\"alert alert-danger alert-dismissable\">");
			html.push("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
			html.push("<span class=\"glyphicon glyphicon-remove-sign\" aria-hidden=\"true\">&nbsp;</span>");
			html.push(msg);
			html.push("</div>");
		} else {
			html.push("<div class=\"alert alert-info alert-dismissable\">");
			html.push("<button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>");
			html.push("<span class=\"glyphicon glyphicon-remove-sign\" aria-hidden=\"true\">&nbsp;</span>");
			html.push(msg);
			html.push("</div>");
		}
		html.push("</div>");
		if ($("#comm_msglabel") && $("#comm_msglabel").length > 0) {
			$("#comm_msglabel").fadeOut(speed, function() {
				$("#comm_msglabel").remove();

				$("body").append(html.join(""));
				$("#comm_msglabel").fadeIn(speed, function() {
					setTimeout(function() {
						$("#comm_msglabel").fadeOut(speed, function() {
							$("#comm_msglabel").remove();
						});
					}, 5000);
				});
			});
		} else {
			$("body").append(html.join(""));
			$("#comm_msglabel").fadeIn(speed, function() {
				setTimeout(function() {
					$("#comm_msglabel").fadeOut(speed, function() {
						$("#comm_msglabel").remove();
					});
				}, 5000);
			});
		}

	},
	transJsonData2Tree : function(data, idStr, pidStr, chindrenStr) {
		var r = [], hash = {}, id = idStr, pid = pidStr, children = chindrenStr, i = 0, j = 0, len = data.length;
		for (; i < len; i++) {
			hash[data[i][id]] = data[i];
		}
		for (; j < len; j++) {
			var aVal = data[j], hashVP = hash[aVal[pid]];
			if (hashVP) {
				!hashVP[children] && (hashVP[children] = []);
				hashVP[children].push(aVal);
			} else {
				r.push(aVal);
			}
		}
		return r;
	}
};
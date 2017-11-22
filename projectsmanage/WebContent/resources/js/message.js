var webSocket = null;

var showzChatDialog = function() {
	$("#messageDialog").dialog({
		modal : true,
		height : 600,
		width : 700,
		title : "我的消息",
		open : function(event, ui) {
			$(".ui-dialog-titlebar-close").hide();
			$("#newMessage").focus();
			$("#newMessage").val("");
		},
		close : function() {
		},
		buttons : [ {
			text : "发送",
			click : sendMessage
		}, {
			text : "关闭",
			click : function() {
				$(this).dialog("close");
			}
		} ]
	});
};

var sendMessage = function() {
	var contactID = $("#messageDialog .contacts ul li[class=contactActive]")
			.attr("id").replace("contactID_", "");
	var message = $("#newMessage").val();
	if (!message || !contactID) {
		$("#newMessage").val("");
		$("#newMessage").focus();
		return;
	}
	jQuery.ajax({
		async : false,
		type : 'POST',
		url : './chat.web',
		data : {
			atn : "sendmessage",
			contactID : contactID,
			message : message
		},
		dataType : 'json',
		success : function(result) {
		}
	}).done(function(json) {
		$("#newMessage").val("");
		$("#newMessage").focus();
	});
};

var getMyContacts = function() {
	jQuery.post("./message.web",
				{
					action : "getmycontacts"
				},
				function(json) {
					$("#newContacts").empty();
					var contacts = json.contacts;
					$("#newContacts").data("contacts", contacts);
					var html = new Array();
					for ( var i in contacts) {
						var contact = contacts[i];
						var li = $("#messageDialog .contacts ul li[id='contactID_"
								+ contact.id + "']");
						if (!li || li.length <= 0) {
							html.push("<input type=\"button\" class=\"btn btn-default btn-sm contactbtn\" key=\""
									+ contact.id
									+ "\" value=\""
									+ contact.realname
									+ "\" onClick=\"contactBtnActive(this);\" >");
						}
					}
					$("#newContacts").append(html.join(""));
				});
};

var disMyContacts = function() {
	if (contactSource != null) {
		contactSource.close();
		contactSource = null;
	}
};

var disMessages = function() {
	if (messageSource != null) {
		messageSource.close();
		messageSource = null;
	}
};

var active = function(obj) {
	$(obj).parent().addClass("contactActive");
	$(obj).parent().siblings().removeClass("contactActive");
};

var contactBtnActive = function(obj) {
	$(obj).addClass("active");
	$(obj).siblings().removeClass("active");
};

var getMessages = function(contactID) {
	$("#messageDialog .messages ul").empty();
	disMessages();

	messageSource = new EventSource("chat.web?action=getmessages&contactid="
			+ contactID);
	messageSource.onmessage = function(event) {
		var checkMessageIDs = new Array();
		var messages = JSON.parse(event.data);
		for ( var i in messages) {
			var html = new Array();
			var messageID = messages[i].id;
			var receiver = messages[i].receiver;
			var message = messages[i].message;
			var checked = messages[i].checked;
			var createtime = messages[i].createtime;

			var li = $("#messageDialog .messages ul li[id='messageID_"
					+ messageID + "']");
			if (!li || li.length <= 0) {
				html.push("<li class='list-group-item "
						+ (contactID == receiver ? "right" : "left")
						+ "' id='messageID_" + messageID + "'>");
				html.push("<span class='msgtime'>");
				html.push(createtime);
				html.push("</span><span class='msgcontent'>");
				html.push(message);
				html.push("</span></li>");

				$("#messageDialog .messages ul").append(html.join(""));
				$("#messageDialog .messages ul").scrollTop(
						$("#messageDialog .messages ul").height());
				$("#newMessage").focus();
				$("#newMessage").val("");
			}

			if (checked == 0 && contactID != receiver) {
				checkMessageIDs.push(messageID);
			}
		}
		if (checkMessageIDs.length > 0) {
			$.ajax({
				async : false,
				type : 'POST',
				url : './chat.web',
				data : {
					atn : "checkmessage",
					contactID : contactID,
					messageids : checkMessageIDs.join(",")
				},
				dataType : 'json',
				success : function(result) {
				}
			}).done(function(json) {
			});
		}
	};
};

var getAllContacts = function() {
	jQuery
			.post(
					"./usersmanage.web",
					{
						"atn" : "epletree"
					},
					function(json) {
						$("#newContacts").empty();
						var contacts = json.contacts;
						$("#newContacts").data("contacts", contacts);
						var html = new Array();
						for ( var i in contacts) {
							var contact = contacts[i];
							var li = $("#messageDialog .contacts ul li[id='contactID_"
									+ contact.id + "']");
							if (!li || li.length <= 0) {
								html
										.push("<input type=\"button\" class=\"btn btn-default btn-sm contactbtn\" key=\""
												+ contact.id
												+ "\" value=\""
												+ contact.realname
												+ "\" onClick=\"contactBtnActive(this);\" >");
							}
						}
						$("#newContacts").append(html.join(""));
					});
};

var showAllContacts = function() {
	$("#messageDialog .messages ul").empty();
	disMessages();
	$("#newContactDialog")
			.dialog(
					{
						modal : true,
						height : 300,
						width : 400,
						title : "选择联系人",
						open : function(event, ui) {
							$(".ui-dialog-titlebar-close").hide();
						},
						close : function() {
						},
						buttons : [
								{
									text : "确定",
									click : function() {
										var obj = $("#newContacts :input.active");
										var contactID = $(obj).attr("key");
										var contactName = $(obj).val();

										var html = new Array();
										html.push("<li id='" + "contactID_"
												+ contactID + "''>");
										html
												.push("<a href='#' onclick='active(this); getMessages("
														+ contactID + ");'>");
										html.push(contactName);
										html.push("</a></li>");
										$("#messageDialog .contacts ul")
												.prepend(html.join(""));

										$(
												"#messageDialog .contacts ul li a:first")
												.click();
										$(this).dialog("close");
										$("#newMessage").focus();
									}
								}, {
									text : "关闭",
									click : function() {
										$(this).dialog("close");
									}
								} ]
					});
};

var searchContact = function() {
	$("#newContacts").empty();
	var contacts = $("#newContacts").data("contacts");
	var search = $("#newContactDialog div input:first").val();
	var html = new Array();
	for ( var i in contacts) {
		var contact = contacts[i];
		var li = $("#messageDialog .contacts ul li[id='contactID_" + contact.id
				+ "']");
		if ((!li || li.length <= 0) && contact.realname.indexOf(search) != -1) {
			html
					.push("<input type=\"button\" class=\"btn btn-default btn-sm contactbtn\" key=\""
							+ contact.id
							+ "\" value=\""
							+ contact.realname
							+ "\" onClick=\"contactBtnActive(this);\" >");
		}
	}
	$("#newContacts").append(html.join(""));
};

(function($) {
	var init = function(select, options) {
		var html = new Array();
		html.push("<a href=\"#\" onclick=\"showzChatDialog();\">");
		html.push("消息");
		html
				.push("<span class=\"badge\" id=\"msgcount\" style=\"display: none;\">");
		html.push("0");
		html.push("</span>");
		html.push("</a>");
		$(select).append(html.join(""));
		$(select).show();

		$("body").find("#messageDialog").remove();
		html = Array();
		html.push("<div id=\"messageDialog\" class=\"messagedlg\">");
		html.push("  <div class=\"contacts\">");
		html.push("    <ul class=\"nav nav-pills nav-stacked\">");
		html.push("      <li id=\"contactID_new\">");
		html
				.push("        <a href=\"#\" onclick=\"active(this); showAllContacts();\">新建会话</a>");
		html.push("      </li>");
		html.push("    </ul>");
		html.push("  </div>");
		html.push("  <div class=\"messages panel panel-default\">");
		html.push("    <ul class=\"list-group\"></ul>");
		html.push("  </div>");
		html
				.push("  <textarea id=\"newMessage\" onkeypress=\"if (event.keyCode==13) { sendMessage(); }\"></textarea>");
		html.push("</div>");
		$("body").append(html.join(""));

		$("body").find("#newContactDialog").remove();
		html = Array();
		html.push("<div id=\"newContactDialog\" class=\"messagedlg\">");
		html.push("  <div class=\"input-group\">");
		html.push("    <input type=\"text\" class=\"form-control\">");
		html.push("    <span class=\"input-group-btn\">");
		html
				.push("      <button class=\"btn btn-default\" type=\"button\" onClick=\"searchContact();\">");
		html.push("        查找");
		html.push("      </button>");
		html.push("    </span>");
		html.push("  </div>");
		html.push("  <p id=\"newContacts\">");
		html.push("     加载联系人中...");
		html.push("  </p>");
		html.push("</div>");
		$("body").append(html.join(""));

		if (doLink()) {
			getMyContacts();
		}
	};

	var doLink = function() {
		webSocket = new WebSocket(
				"ws://localhost:8090/projectsmanage/socket.web");
		if (webSocket && webSocket.readyState == WebSocket.OPEN) {
			webSocket.onmessage = function(json) {
				var count = json.data;
				if (count > 0) {
					$("#msgcount").show();
					$("#msgcount").text(count);
				} else {
					$("#msgcount").hide();
					$("#msgcount").text(count);
				}
			};
			return true;
		} else {
			return false;
		}
	};

	$.fn.zChat = function(options) {
		if (typeof (WebSocket) !== "undefined") {
			init(this, options);
		}
	};
})(jQuery);

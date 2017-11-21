// -----------------------------------------------------------------------------------项目属性变更开始 -----------------------------------------------------------------------------------
function changeProState(tostate, proid) {
	$.ajax({
		async : false,
		type : 'POST',
		url : './projectsmanage.web',
		data : {
			atn : "changestate",
			proid : proid,
			tostate : tostate
		},
		dataType : 'json',
		success : function(result) {
		}
	}).done(function(json) {
		if (json.result == 1) {
			refresh();
			$.webeditor.showMsgLabel("success", "项目状态更改成功");
		} else {
			$.webeditor.showMsgLabel("alert", "项目状态更改失败");
		}
	});
}

// 项目难度
function changeDiff(proid) {
	var dval = $("#pdifficulty_" + proid).val();
	$.ajax({
		async : false,
		type : 'POST',
		url : './projectsmanage.web',
		data : {
			atn : "resetdiff",
			proid : proid,
			diff : dval
		},
		dataType : 'json',
		success : function(result) {
		}
	}).done(function(json) {
		if (json.result == 1) {
			$.webeditor.showMsgLabel("success", "项目难度更改成功");
		} else {
			$.webeditor.showMsgLabel("alert", "项目难度更改失败");
		}
	});
}

// 项目难度
function changePriority(proid) {
	var priority = $("#priority_" + proid).val();
	$.ajax({
		async : false,
		type : 'POST',
		url : './projectsmanage.web',
		data : {
			atn : "resetpriority",
			proid : proid,
			priority : priority
		},
		dataType : 'json',
		success : function(result) {
		}
	}).done(function(json) {
		if (json.result == 1) {
			refresh();
			$.webeditor.showMsgLabel("success", "项目优先级更改成功");
		} else {
			$.webeditor.showMsgLabel("alert", "项目优先级更改失败");
		}
	});
}

function changeAccess(proid) {
	var aval = $("#access_" + proid).val();
	$.ajax({
		async : false,
		type : 'POST',
		url : './projectsmanage.web',
		data : {
			atn : "resetpaccess",
			proid : proid,
			paccess : aval
		},
		dataType : 'json',
		success : function(result) {
		}
	}).done(function(json) {
		if (json.result == 1) {
			$.webeditor.showMsgLabel("success", "项目公有/私有更改成功");
		} else {
			$.webeditor.showMsgLabel("alert", "项目公有/私有更改失败");
		}
	});
}

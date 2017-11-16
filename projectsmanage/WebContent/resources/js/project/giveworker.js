// -----------------------------------------------------------------------------------人力资源分配 开始 -----------------------------------------------------------------------------------
{
	var addWorkers = [];
	var delWorkers = [];
	var hasWorkers = [];
	var worktype = 0;//工作类型：0制作，1校验
	var currentProject;
	var orgUsers = [];
	//根据Proid获取当前页面的项目。由于项目可拖拽，所以不能以INDEX为标记获取
	function getProjectByPID(proid){
		for(var p in currentPageProjects){
			if(currentPageProjects[p].id == proid){
				return currentPageProjects[p];
			} 
		}
	}
	function setOrgUsers(pro){
		if(worktype == 0){
			orgUsers =  currentProject.workusers;
		}else{
			orgUsers =  currentProject.checkusers;
		}
	}
	//原来没有，添加
	function addworker(proid,worktypeval) {
		currentProject = getProjectByPID(proid);
		worktype = worktypeval;
		setOrgUsers(currentProject);
		if(worktypeval == 1){
			$("#hasChecker_ul").empty();
		}else{
			$("#hasWorker_ul").empty();
		}
		showSelect(worktypeval);
	}
	//原来有，改变
	function changeworker(proid,worktypeval) {
		currentProject = getProjectByPID(proid);
		worktype = worktypeval;
		setOrgUsers(currentProject);
		if(worktypeval == 1){
			$("#hasChecker_ul").empty();
		}else{
			$("#hasWorker_ul").empty();
		}
		
		var html = "";
		for(var i = 0 ;i < orgUsers.length;i++){
			var w = orgUsers[i];
			html += "<li id='"+ w.userid +"_li'><input type='checkbox' id='"+ w.userid +"_checkbox'/><label for='"+ w.userid +"_checkbox'>"+ w.username +"</label></li>";
		}
		hasWorkers = orgUsers;
		if(worktypeval == 1){
			$("#hasChecker_ul").append(html);
		}else{
			$("#hasWorker_ul").append(html);
		}
		showSelect(worktypeval);
	}
	
	
	//显示人力分配对话框
	function showSelect(worktypeval) {
		var divID= "selectWorker";
		if(worktypeval == 1){
			divID="selectChecker";
		}
		
		$("#"+divID).dialog({  
			  modal: true,
			  height: document.documentElement.clientHeight*0.5,
			  width: document.documentElement.clientWidth*0.5,
			  title:"作业人员分配",
			  open: function(event, ui) { 
		            $(".ui-dialog-titlebar-close").hide();
		        },
			  buttons: [
			            {
			              text: "提交",
			              click: function() {
			            	  submitWorkers();
			              }
			         
			            },
			            {
			              text: "关闭",
			              click: function() {
			            	  cleanParas();
			                $( this ).dialog( "close" );
			              }
			         
			            }
			          ]
			  });
	}
    //检验某用户是否已添加过
	function checkIsExist(uid){
		for(var i = 0 ;i < hasWorkers.length;i++){
			if(uid == hasWorkers[i].userid ){
				return true;
			}
		}
		
		for(var i = 0 ;i < addWorkers.length;i++){
			if(uid == addWorkers[i].userid){
				return true;
			}
		}
		return false;
	}
	//确认添加
	function confirmSelect() {
		var workusertype = "workerse";
		if(worktype == 1){
			workusertype = "checkers";
		}
		var user  = $("#"+workusertype).val();
		if (user == 0) {
			alert("请选择作业人员");
			return;
		}
		var userinfo  = user.split("_");
		if(checkIsExist(userinfo[0])){
			alert("该人员已添加");
			return;
		}
		var w = {
				uid:userinfo[0],
				username:userinfo[1] 
			};
		var flag = false;
		for(var i = 0 ;i < delWorkers.length;i++){
			if(w.uid == delWorkers[i].uid){
				delWorkers.splice(i,1);
				hasWorkers.push(w);
				flag = true;
				break;
			}
		}
		if(!flag){
			addWorkers.push(w);
		}
		var html = "<li id='"+ w.uid +"_li'><input type='checkbox' id='"+ w.uid +"_checkbox'/><label for='"+ w.uid +"_checkbox'>"+ w.username +"</label></li>";
		var workerul = "hasWorker_ul";
		if(worktype == 1){
			workerul = "hasChecker_ul";
		}
		
		$("#"+workerul).append(html);
	}
	
	//移除选中的作业人员
	function removeWorkers(){
		var removeIDs = [];
		var divID= "hasWorker_div";
		if(worktype == 1){
			divID="hasChecker_div";
		}
		var idstr = "#"+ divID +" input:checkbox:checked";
		$(idstr).each(function(){
			removeIDs.push($(this).attr("id"));
		});
		for(var i = 0 ;i < removeIDs.length;i++){
			var id = removeIDs[i].split("_")[0];
			removeWorker(id);
		}
	}
	//移除指定ID的人
	function removeWorker(uid){
		var flag = false;
		for(var i = 0 ;i < addWorkers.length;i++){
			if(uid == addWorkers[i].uid){
				addWorkers.splice(i,1);
				break ;
			}
		}
		for(var i = 0 ;i < hasWorkers.length;i++){
			if(uid == hasWorkers[i].userid){
				var temp  = {
						uid:hasWorkers[i].userid,
						username:hasWorkers[i].username 
						 
				};
				hasWorkers.splice(i,1);
				delWorkers.push(temp);
				break ;
			}
		}
		$("#" + uid +"_li").remove();
	}
	//提交人力资源变更
	function submitWorkers(){
		if( addWorkers.length == 0 && delWorkers.length == 0){
			alert("人员未发生变化");
			return;
		}
		var subStr = "[";
		for(var i = 0 ;i < addWorkers.length;i++){
			var w = addWorkers[i];
			subStr += '{"uid":'+ w.uid +', "username":"'+ w.username +'"}';
			if( i != addWorkers.length - 1){
				subStr += ",";
			}
		}
		subStr += ']';
		
		var delStr = "[";
		for(var i = 0 ;i < delWorkers.length;i++){
			var w = delWorkers[i];
			delStr += '{"uid":'+ w.uid +', "username":"'+ w.username +'"}';
			if( i != delWorkers.length - 1){
				delStr += ",";
			}
		}
		delStr += ']';
		
		$.ajax({
	    	async:false,
	    	type:'POST',
	    	url:'./projectsmanage.web',
			data:{atn:"add",
					proid: currentProject.id,
					addworkers:subStr,
					delworkers:delStr,
					worktype:worktype
				  },
			dataType:'json',
	    	success:function(result){
	    	}
	    }).done(function(json) {
	    	if(json.result == 0){
	    		refresh();
	    		var divID= "selectWorker";
	    		if(worktype == 1){
	    			divID="selectChecker";
	    		}
	    		$("#"+divID).dialog( "close" );
	    		cleanParas();
	    	} else{
	    		alert("更改失败");
	    	}
	    });
	}
	
	function cleanParas(){
		 addWorkers = [];
		  delWorkers = [];
		  hasWorkers = [];
		  worktype = 0;//工作类型：0制作，1校验
		  currentProject = null;
		  orgUsers = [];
	}
}
	// -----------------------------------------------------------------------------------人力资源分配 结束 -----------------------------------------------------------------------------------

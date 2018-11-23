var bootstrapDialogFirstIn = true;
	var bootstrapDialogFirstClick = true;
	var bootstrapDialogSelected = new Array();
	var bootstrapDialogUniqSelected = new Array();
	var bootstrapDialogOn = -1;
	
(function($) {
	var initBootstrap = function($$, options) {
		var bootstrap = $($$).children("table");
		$(bootstrap).bootstrapTable({
			locale : 'zh-CN',
			queryParams : function(params) {
				if (params.filter != undefined) {
					var filterObj = eval('(' + params.filter + ')');
					if (filterObj.state != undefined) {
						filterObj["state"] = filterObj.state;
						delete filterObj.state;
						params.filter = JSON.stringify(filterObj);
					}
				}
				return params;
			},
			onLoadSuccess : function(data) {
				datasetOn = -1;
				datasetSelected = new Array();
				datasetFirstClick = true;
				
				var values = new Array();
				if(datasetFirstIn) {
					var str_values = $("#config_2_25").val();
					$.each(str_values.split(","), function(index, domEle) {
						values[index] = parseInt(domEle);
					});
				} else {
					$.each(datasetIDSelected, function(index, domEle) {
						values.push(parseInt(domEle));
					});
				}
				
				$('[data-toggle="datasets"]').bootstrapTable("checkBy",
						{
							field : "id",
							values : values
						});
				datasetFirstIn = false;
			},
			onCheck : function(row, element) {
				var index = parseInt($(element).parent().next().text());
				if(datasetSelected.indexOf(index) < 0) {
					datasetOn = index;
					datasetSelected.push(index);
					datasetSelected.sort(compare);
				}
				var id = row.id;
				if(datasetIDSelected.indexOf(id) < 0) {
					datasetIDSelected.push(id);
				}
			},
			onUncheck : function(row, element) {
				var index = parseInt($(element).parent().next().text());
				var indexIn = datasetSelected.indexOf(index);
				if(indexIn >= 0) {
					datasetOn = datasetSelected[indexIn == 0 ? 0 : indexIn -1];
					datasetSelected.splice(indexIn,1).sort(compare);
				}
				var id = row.id;
				var idIn = datasetIDSelected.indexOf(id);
				if(idIn >= 0) {
					datasetIDSelected.splice(idIn, 1);
				}
			},
			onCheckAll : function(rows) {
				var elements = $('[data-toggle="datasets"] td.indexHidden');
				$.each(elements, function(i, element){
					var index = parseInt($(element).text());
					if(datasetSelected.indexOf(index) < 0) {
						datasetSelected.push(index);
					}
				});
				datasetOn = parseInt($('[data-toggle="datasets"] td.indexHidden:last').text());
				datasetSelected.sort(compare);
				$.each(rows, function(i, row){
					var id = row.id;
					if(datasetIDSelected.indexOf(id) < 0) {
						datasetIDSelected.push(id);
					}
				});
			},
			onUncheckAll : function(rows) {
				var elements = $('[data-toggle="datasets"] td.indexHidden');
				$.each(elements, function(i, element){
					var index = parseInt($(element).text());
					var indexIn = datasetSelected.indexOf(index);
					if(indexIn >= 0) {
						datasetOn = datasetSelected[indexIn == 0 ? 0 : indexIn -1];
						datasetSelected.splice(indexIn,1).sort(compare);
					}
				});
				datasetOn = parseInt($('[data-toggle="datasets"] td.indexHidden:last').text());
				datasetSelected.sort(compare);
				$.each(rows, function(i, row){
					var id = row.id;
					var idIn = datasetIDSelected.indexOf(id);
					if(idIn >= 0) {
						datasetIDSelected.splice(idIn, 1);
					}
				});
			}
		});
	};
	
	var initDialog = function ($$, options) {
		$("#datasetsDlg").dialog({
			modal : true,
			width : 1000,
			title : "绑定资料",
			open : function(event, ui) {
				datasetFirstClick = true;
				datasetFirstIn= true;
				$(".ui-dialog-titlebar-close").hide();
			},
			close : function() {
				datasetOn = -1;
				datasetSelected = new Array();
				datasetIDSelected = new Array();
				datasetFirstClick = true;
				datasetFirstIn= true;
				$('[data-toggle="datasets"]').bootstrapTable("destroy");
			},
			buttons : [
					{
						text : "<",
						title : "上一条",
						class : "btn btn-default",
						click : function() {
							if(!datasetSelected || datasetSelected.length <= 0) {
								$.webeditor.showMsgLabel("warning", "没有勾选项");
								return;
							}
							if(datasetFirstClick) {
								$('[data-toggle="datasets"]').bootstrapTable('scrollTo',datasetSelected[0] * 31);
								datasetOn = datasetSelected[0];
								datasetFirstClick = false;
							} else {
								if (datasetOn < 0) {
									$('[data-toggle="datasets"]').bootstrapTable('scrollTo', 0);
									$.webeditor.showMsgLabel("warning","已经跳转到第一条");
								} else {
									var index = datasetSelected.indexOf(datasetOn);
									if (index < 0) {
										$('[data-toggle="datasets"]').bootstrapTable('scrollTo',0);
									} else if (index > datasetSelected.length - 1) {
										$('[data-toggle="datasets"]').bootstrapTable('scrollTo','bottom');
									} else {
										if (index == 0) {
											$('[data-toggle="datasets"]').bootstrapTable('scrollTo',datasetSelected[0] * 31);
											datasetOn = datasetSelected[0];
											$.webeditor.showMsgLabel("warning","已经跳转到第一条");
										} else {
											var preIndex = index - 1;
											$('[data-toggle="datasets"]').bootstrapTable('scrollTo',datasetSelected[preIndex] * 31);
											datasetOn = datasetSelected[preIndex];
										}
									}
								}
							}
						}
					},
					{
						text : ">",
						title : "下一条",
						class : "btn btn-default",
						click : function() {
							if(!datasetSelected || datasetSelected.length <= 0) {
								$.webeditor.showMsgLabel("warning", "没有勾选项");
								return;
							}
							if(datasetFirstClick) {
								$('[data-toggle="datasets"]').bootstrapTable('scrollTo',datasetSelected[0] * 31);
								datasetOn = datasetSelected[0];
								datasetFirstClick = false;
							} else {
								if (datasetOn < 0) {
									$('[data-toggle="datasets"]').bootstrapTable('scrollTo', 0);
								} else {
									var index = datasetSelected.indexOf(datasetOn);
									if (index < 0) {
										$('[data-toggle="datasets"]').bootstrapTable('scrollTo',0);
									} else if (index > datasetSelected.length - 1) {
										$('[data-toggle="datasets"]').bootstrapTable('scrollTo','bottom');
									} else {
										if (index == datasetSelected.length - 1) {
											var nextIndex = datasetSelected.length - 1;
											$('[data-toggle="datasets"]').bootstrapTable('scrollTo',datasetSelected[nextIndex] * 31);
											datasetOn = datasetSelected[nextIndex];
											$.webeditor.showMsgLabel("warning","已经跳转到最后一条");
										} else {
											var nextIndex = index + 1;
											$('[data-toggle="datasets"]').bootstrapTable('scrollTo',datasetSelected[nextIndex] * 31);
											datasetOn = datasetSelected[nextIndex];
										}
									}
								}
							}
						}
					},
					{
						text : "提交",
						class : "btn btn-default",
						click : function() {
							var length = datasetIDSelected.length;
							if (length > 0) {
								$("#config_2_25").val(datasetIDSelected.join(","));
								$("#config_2_25Count").text(length);

								$(this).dialog("close");
							} else {
								$.webeditor.showMsgLabel("alert", "请选择人员");
							}

						}
					},
					{
						text : "关闭",
						class : "btn btn-default",
						click : function() {
							$(this).dialog("close");
						}
					} ]
		});
	};
	
	$.fn.bootstrapDialog = function(bootstrapOptions, dialogOptions) {
		initBootstrap(this, bootstrapOptions);
		initDialog(this, dialogOptions);
		showBootstrapDialog();
	};
})(jQuery);

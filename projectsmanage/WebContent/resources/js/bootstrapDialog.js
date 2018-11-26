(function($) {
	'use strict';
	
	var reckonHeight = function(heights, index) {
		var offset = 0;
		for (var i = 0; i < index; i++) {
			offset += heights[i];
		}
		return offset;
	};
	
	$.extend($.fn.bootstrapTable.defaults, {
		bootstrapDialogHeights : new Array(),
		bootstrapDialogFirstClick : true,
		bootstrapDialogSelected : new Array(),
		bootstrapDialogIDSelected : new Array(),
		bootstrapDialogOn : -1,
		valueBand: "default"
    });
	
	$.fn.bootstrapTable.methods.push('gotoLast', 'gotoNext');
	
	var BootstrapTable = $.fn.bootstrapTable.Constructor;
	
	BootstrapTable.prototype.gotoLast = function () {
		if(!this.options.bootstrapDialogSelected || this.options.bootstrapDialogSelected.length <= 0) {
			$.webeditor.showMsgLabel("warning", "没有勾选项");
			return;
		}
		if(this.options.bootstrapDialogFirstClick) {
			this.scrollTo(reckonHeight(this.options.bootstrapDialogHeights, this.options.bootstrapDialogSelected[0]));
			this.options.bootstrapDialogOn = this.options.bootstrapDialogSelected[0];
			this.options.bootstrapDialogFirstClick = false;
		} else {
			if (this.options.bootstrapDialogOn < 0) {
				this.scrollTo( 0);
			} else {
				var index = this.options.bootstrapDialogSelected.indexOf(this.options.bootstrapDialogOn);
				if (index < 0) {
					this.scrollTo(0);
				} else if (index > this.options.bootstrapDialogSelected.length - 1) {
					this.scrollTo('bottom');
				} else {
					if (index == 0) {
						this.scrollTo(reckonHeight(this.options.bootstrapDialogHeights, this.options.bootstrapDialogSelected[0]));
						this.options.bootstrapDialogOn = this.options.bootstrapDialogSelected[0];
						$.webeditor.showMsgLabel("warning","已跳转到第一条");
					} else {
						var preIndex = index - 1;
						this.scrollTo(reckonHeight(this.options.bootstrapDialogHeights, this.options.bootstrapDialogSelected[preIndex]));
						this.options.bootstrapDialogOn = this.options.bootstrapDialogSelected[preIndex];
					}
				}
			}
		}
	};
	
	BootstrapTable.prototype.gotoNext = function () {
		if(!this.options.bootstrapDialogSelected || this.options.bootstrapDialogSelected.length <= 0) {
			$.webeditor.showMsgLabel("warning", "没有勾选项");
			return;
		}
		if(this.options.bootstrapDialogFirstClick) {
			this.scrollTo(reckonHeight(this.options.bootstrapDialogHeights, this.options.bootstrapDialogSelected[0]));
			this.options.bootstrapDialogOn = this.options.bootstrapDialogSelected[0];
			this.options.bootstrapDialogFirstClick = false;
		} else {
			if (this.options.bootstrapDialogOn < 0) {
				this.scrollTo(0);
			} else {
				var index = this.options.bootstrapDialogSelected.indexOf(this.options.bootstrapDialogOn);
				if (index < 0) {
					this.scrollTo(0);
				} else if (index > this.options.bootstrapDialogSelected.length - 1) {
					this.scrollTo('bottom');
				} else {
					if (index == this.options.bootstrapDialogSelected.length - 1) {
						var nextIndex = this.options.bootstrapDialogSelected.length - 1;
						this.scrollTo(reckonHeight(this.options.bootstrapDialogHeights, this.options.bootstrapDialogSelected[nextIndex]));
						this.options.bootstrapDialogOn = this.options.bootstrapDialogSelected[nextIndex];
						$.webeditor.showMsgLabel("warning","已跳转到最后一条");
					} else {
						var nextIndex = index + 1;
						this.scrollTo(reckonHeight(this.options.bootstrapDialogHeights, this.options.bootstrapDialogSelected[nextIndex]));
						this.options.bootstrapDialogOn = this.options.bootstrapDialogSelected[nextIndex];
					}
				}
			}
		}
	};
	
	$.bootstrapDialog = function(select, bootstrapOptions, dialogOptions) { this.init(select, bootstrapOptions, dialogOptions); };
	
	$.extend($.bootstrapDialog.prototype, {
		bootstrap: null,
		dialog: null,
	    init: function(select, bootstrapOptions, dialogOptions) {
	    	var values = new Array();
	    	var idSelect = $("#" + bootstrapOptions.valueBand).val();
	    	if (idSelect) {
				try{
					var map = JSON.parse(idSelect);
					if (map && typeof map == 'object') {
						for (var key in map){
							values.push(parseInt(key));
						}
					} else if (map && typeof map == 'number') {
						values.push(map);
					}
				}catch(e){
					$.each(idSelect.split(","), function(index, domEle) {
						values[index] = parseInt(domEle);
					});
				}
	    	}
	        
	        $(select).children("table").bootstrapTable($.extend({}, this.bootstrapOptions, {self: $(select).children("table")}, bootstrapOptions, {
	        	bootstrapDialogIDSelected: values
	        }));
	        $(select).dialog($.extend({}, this.dialogOptions, dialogOptions));
	    },
	    bootstrapOptions: {
			locale : 'zh-CN',
			self: null,
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
				$(this.self).bootstrapTable("getOptions").bootstrapDialogOn = -1;
				$(this.self).bootstrapTable("getOptions").bootstrapDialogSelected = new Array();
				$(this.self).bootstrapTable("getOptions").bootstrapDialogFirstClick = true;
				
				var heights = new Array();
				for (var i = 1, len = data.rows.length; i <= len; i++) {
					heights.push($(this.self).find("tr:eq(" + i + ")").height());
				}
				$(this.self).bootstrapTable("getOptions").bootstrapDialogHeights = heights;
				
				var values = new Array();
				$.each($(this.self).bootstrapTable("getOptions").bootstrapDialogIDSelected, function(index, domEle) {
					if (values.indexOf(parseInt(domEle)) < 0)
						values.push(parseInt(domEle));
				});
				
				$(this.self).bootstrapTable("checkBy", {
					field : $(this.self).bootstrapTable("getOptions").uniqueId,
					values : values
				});
			},
			onCheck : function(row, element) {
				var index = ($(element).parent().next().text());
				if($(this.self).bootstrapTable("getOptions").bootstrapDialogSelected.indexOf(index) < 0) {
					$(this.self).bootstrapTable("getOptions").bootstrapDialogOn = index;
					$(this.self).bootstrapTable("getOptions").bootstrapDialogSelected.push(index);
					$(this.self).bootstrapTable("getOptions").bootstrapDialogSelected.sort(compare);
				}
				var id = row[$(this.self).bootstrapTable("getOptions").uniqueId];
				if($(this.self).bootstrapTable("getOptions").bootstrapDialogIDSelected.indexOf(parseInt(id)) < 0) {
					$(this.self).bootstrapTable("getOptions").bootstrapDialogIDSelected.push(parseInt(id));
				}
			},
			onUncheck : function(row, element) {
				var index = ($(element).parent().next().text());
				var indexIn = $(this.self).bootstrapTable("getOptions").bootstrapDialogSelected.indexOf(index);
				if(indexIn >= 0) {
					$(this.self).bootstrapTable("getOptions").bootstrapDialogOn = $(this.self).bootstrapTable("getOptions").bootstrapDialogSelected[indexIn == 0 ? 0 : indexIn -1];
					$(this.self).bootstrapTable("getOptions").bootstrapDialogSelected.splice(indexIn,1).sort(compare);
				}
				var id = row[$(this.self).bootstrapTable("getOptions").uniqueId];
				var idIn = $(this.self).bootstrapTable("getOptions").bootstrapDialogIDSelected.indexOf(parseInt(id));
				if(idIn >= 0) {
					$(this.self).bootstrapTable("getOptions").bootstrapDialogIDSelected.splice(idIn, 1);
				}
			},
			onCheckAll : function(rows) {
				var index = 0;
				if (rows && rows.length > 0) {
					for (var i = 0, len = rows.length; i < len; i++) {
						var id = rows[i][$(this.self).bootstrapTable("getOptions").uniqueId];
						if($(this.self).bootstrapTable("getOptions").bootstrapDialogIDSelected.indexOf(parseInt(id)) < 0) {
							$(this.self).bootstrapTable("getOptions").bootstrapDialogIDSelected.push(parseInt(id));
						}
						if($(this.self).bootstrapTable("getOptions").bootstrapDialogSelected.indexOf(index) < 0) {
							$(this.self).bootstrapTable("getOptions").bootstrapDialogSelected.push(index);
						}
						index++;
					}
				}
				this.bootstrapDialogOn = index;
				this.bootstrapDialogSelected.sort(compare);
			},
			onUncheckAll : function(rows) {
				$(this.self).bootstrapTable("getOptions").bootstrapDialogOn = -1;
				$(this.self).bootstrapTable("getOptions").bootstrapDialogIDSelected = new Array();
				$(this.self).bootstrapTable("getOptions").bootstrapDialogSelected = new Array();
			}
		},
		dialogOptions: {
			modal : true,
			width : 1000,
			title : "title",
			open : function(event, ui) {
				$(".ui-dialog-titlebar-close").hide();
			},
			close : function() {
				$(this).find("table").bootstrapTable("destroy");
			},
			buttons : [
				{
					text : "<",
					title : "上一条",
					class : "btn btn-default",
					click : function() {
						$(this).find("table").bootstrapTable("gotoLast");
					}
				},
				{
					text : ">",
					title : "下一条",
					class : "btn btn-default",
					click : function() {
						$(this).find("table").bootstrapTable("gotoNext");
					}
				},
				{
					text : "提交",
					class : "btn btn-default",
					click : function() {
						var bootstrapDialogIDSelected = $(this).find("table").bootstrapTable("getOptions").bootstrapDialogIDSelected;
						var length = bootstrapDialogIDSelected.length;
						if (length > 0) {
							$("#" + $(this).find("table").bootstrapTable("getOptions").valueBand).val(bootstrapDialogIDSelected.join(","));
							$("#" + $(this).find("table").bootstrapTable("getOptions").valueBand + " span").text(length);

							$(this).dialog("close");
						} else {
							$.webeditor.showMsgLabel("alert", "请确认已勾选");
						}
					}
				},
				{
					text : "关闭",
					class : "btn btn-default",
					click : function() {
						$(this).dialog("close");
					}
				}
			]
		}
	});
	
	$.fn.bootstrapDialog = function(bootstrapOptions, dialogOptions) {
		new $.bootstrapDialog(this, bootstrapOptions, dialogOptions);
		return this;
	};
	
})(jQuery);
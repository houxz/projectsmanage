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
		valueBand: "default"
    });
	
	$.fn.bootstrapTable.methods.push('gotoLast', 'gotoNext');
	
	var BootstrapTable = $.fn.bootstrapTable.Constructor;
	
	BootstrapTable.prototype.gotoLast = function () {
		var curPosition = this.scrollTo();
		
		var trs = this.$el.find("tr");
		var position = 0;
		var to = -1;
		for (var i = 1, len = trs.length; i <= len; i++) {
			if (position >= curPosition) {
				break;
			}
			if ($(trs[i]).hasClass("selected")) {
				to = position;
			}
			position += $(trs[i]).height();
		}
		if (to >= 0)
			this.scrollTo(to);
		else
			$.webeditor.showMsgLabel("alert", "已经到最前一项");
	};
	
	BootstrapTable.prototype.gotoNext = function () {
		var curPosition = this.scrollTo();
		
		var trs = this.$el.find("tr");
		var position = 0;
		for (var i = 1, len = trs.length; i <= len; i++) {
			if (position > curPosition && $(trs[i]).hasClass("selected")) {
				this.scrollTo(position);
				return;
			}
			position += $(trs[i]).height();
		}
		$.webeditor.showMsgLabel("alert", "已经到最后一项");
	};
	
	$.bootstrapDialog = function(select, bootstrapOptions, dialogOptions) { this.init(select, bootstrapOptions, dialogOptions); };
	
	$.extend($.bootstrapDialog.prototype, {
	    init: function(select, bootstrapOptions, dialogOptions) {
	    	$(select).children("table").bootstrapTable($.extend({}, this.bootstrapOptions, {self: $(select).children("table")}, bootstrapOptions));
	        $(select).dialog($.extend({}, this.dialogOptions, dialogOptions));
	    },
	    bootstrapOptions: {
			locale : 'zh-CN',
			self: null,
			cache: false,
			filterControl: true,
			clickToSelect: true,
			selectItemName: "checkboxName",
			searchOnEnterKey: true,
			align: "center",
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
				$(this.self).bootstrapTable("load", data.rows);
			}
		},
		dialogOptions: {
			modal : true,
			width : 520,
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
						var selections = $(this).find("table").bootstrapTable("getAllSelections");
						var length = selections.length;
						var uniqueId = $(this).find("table").bootstrapTable("getOptions").uniqueId;
						if (length > 0) {
							var str = (function () {
								var d = [];
								selections.forEach(function (value, key, mapObj) {
								    d.push(value[uniqueId]);
								});
				                return d.join(",");
				            })();
							$("#" + $(this).find("table").bootstrapTable("getOptions").valueBand).val(str);
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
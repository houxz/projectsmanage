/**
 * jQuery Form Validate Chinese error message translations
 * Include this file *after* the jquery.formvalidate.js in document <head>
 */
jQuery.extend(true, jQuery.fn.formvalidate.options.localization, {
	zh: {
		failure: {
			'default': '{0} 无效.',
			betweenNumeric: '{0} 的值必须在 {2} 和 {3} 之间.',
			date: '{0} 必须是有效的日期.',
			email: '{1} 不是有效的Email地址.',
			url: '{1} 不是有效链接地址.',
			numChars: '{0} 必须是 {2} 个字符.',
			minChars: '{0} 最少是 {2} 个字符.',
			maxChars: '{0} 最多是 {2} 个字符.',
			numOptions: '必须选择 {2} 个选项.',
			minOptions: '最少选择 {2} 个选项.',
			maxOptions: '最多选择 {2} 个选项.',
			'int': '{0} 必须是整数.',
			'float': '{0} 必须是数字.',
			required: '{0} 必须填写.',
			requiredIf: '{0} 必须填写.',
			requiredEither: '{0} 和 {2} 至少填写一项.',
			equalsInput: '{0} 必须相同.',
			lessThan: '{0} 必须小于 {2}.',
			greaterThan: '{0} 必须大于 {2}.'
		},
		success: {
			'default': '{0} OK.'
		}
	}
	
});
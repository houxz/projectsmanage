package com.emg.projectsmanage.ctrl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.common.ParamUtils;

@Controller
@RequestMapping("/head.web")
public class HeadCtrl extends BaseCtrl {

	@RequestMapping()
	public String head(Model model, HttpSession session, HttpServletRequest request) {
		String account = getLoginAccount(session);
		String fromurl = ParamUtils.getParameter(request, "fromurl");
		model.addAttribute("fromurl", fromurl);
		if ("".equals(account)) {
			model.addAttribute("islogin", false);
		} else {
			model.addAttribute("islogin", true);
			model.addAttribute("account", session.getAttribute(CommonConstants.SESSION_USER_NAME));
		}

		return "head";
	}

}

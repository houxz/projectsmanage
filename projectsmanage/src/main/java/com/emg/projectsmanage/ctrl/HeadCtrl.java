package com.emg.projectsmanage.ctrl;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.emg.projectsmanage.auth.MenuAuthModel;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.config.MenuConfig;

@Controller
@RequestMapping("/head.web")
public class HeadCtrl extends BaseCtrl {
	
	@Autowired
	private MenuConfig menuConfig;

	@RequestMapping()
	public String head(Model model, HttpSession session, HttpServletRequest request) {
		String fromurl = ParamUtils.getParameter(request, "fromurl");
		
		Set<MenuAuthModel> menus = new TreeSet<MenuAuthModel>();
		Set<RoleType> auths = new HashSet<RoleType>();
		for (GrantedAuthority ga : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
			auths.add(RoleType.valueOf(ga.getAuthority()));
		}

		for (MenuAuthModel menuAuthModel : menuConfig.getMenus()) {
			if(menuAuthModel.getEnabled()) {
				if(fromurl.indexOf(menuAuthModel.getUrl()) >= 0) {
					menuAuthModel.setActive(true);
				} else {
					menuAuthModel.setActive(false);
				}
				Set<RoleType> result = new HashSet<RoleType>();
				Set<RoleType> roleSet = menuAuthModel.getRoleSet();
				result.clear();
		        result.addAll(roleSet);
		        result.retainAll(auths);
		        if(result.size() > 0) {
		        	menus.add(menuAuthModel);
		        }
			}
		}
		model.addAttribute("menus", menus);
		return "head";
	}

}

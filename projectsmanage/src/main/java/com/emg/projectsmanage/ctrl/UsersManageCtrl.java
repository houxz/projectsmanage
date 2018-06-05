package com.emg.projectsmanage.ctrl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.dao.projectsmanager.UserRoleModelDao;
import com.emg.projectsmanage.pojo.RoleModel;
import com.emg.projectsmanage.pojo.UserRoleModel;
import com.emg.projectsmanage.service.EmapgoAccountService;
import com.emg.projectsmanage.service.ProjectManagerRoleService;

@Controller
@RequestMapping("/usersmanage.web")
public class UsersManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(UsersManageCtrl.class);

	@Autowired
	private EmapgoAccountService emapgoAccountService;

	@Autowired
	private ProjectManagerRoleService projectManagerRoleService;

	@Autowired
	private UserRoleModelDao userRoleModelDao;

	/**
	 * 开发权限管理页面
	 * 
	 * @param model
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping()
	public String openLader(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("UsersManageCtrl-openLader start.");
		try {
			List<RoleModel> authlist = projectManagerRoleService.getAllEnabledRoles();
			List<RoleModel> rolelist = projectManagerRoleService.getAllEnabledRoles();
			model.addAttribute("authlist", authlist);
			model.addAttribute("rolelist", rolelist);
			return "usersmanage";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "redirect:login.jsp";
		}
	}

	/**
	 * 取人员树
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "atn=epletree")
	public ModelAndView getEmployeeTree(Model model) {
		logger.debug("UsersManageCtrl-getEmployeeTree start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<Map<String, Object>> eplelist = emapgoAccountService.getEmployeeListForZTree();
		json.addObject("eplelist", eplelist);
		logger.debug("UsersManageCtrl-getEmployeeTree end!");
		return json;
	}

	/**
	 * 取某人的人员权限
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(params = "atn=getepleroles")
	public ModelAndView getEpleRoles(Model model, @RequestParam("id") int id) {
		logger.debug("UsersManageCtrl-getEpleRoles start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<Map<String, Object>> epleRolesList = userRoleModelDao.getEpleRoles(id);
		json.addObject("epleRolesList", epleRolesList);
		logger.debug("UsersManageCtrl-getEpleRoles end!");
		return json;
	}

	/**
	 * 添加权限，用户类别。
	 * 
	 * @param model
	 * @param rolename
	 * @param roleremark
	 * @return
	 */
	@RequestMapping(params = "atn=addrole")
	public ModelAndView addRole(Model model, @RequestParam("name") String name, @RequestParam("remark") String remark) {
		logger.debug("UsersManageCtrl-addRole start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		RoleModel record = new RoleModel();
		record.setName(name);
		record.setType("U");
		record.setRemark(remark);
		record.setEnabled(1);
		int ret = projectManagerRoleService.addRole(record);
		if (ret > 0) {
			json.addObject("result", 1);
			json.addObject("role", record);
		} else {
			json.addObject("result", 0);
		}
		logger.debug("UsersManageCtrl-addRole end!");
		return json;
	}

	/**
	 * 取所有权限列表
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "atn=getrolelist")
	public ModelAndView getRoleList(Model model) {
		logger.debug("UsersManageCtrl-getRoleList start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		List<RoleModel> rolelist = projectManagerRoleService.getAllEnabledRoles();
		json.addObject("rolelist", rolelist);
		logger.debug("UsersManageCtrl-getRoleList end!");
		return json;
	}

	/**
	 * 添加人员权限关联
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "atn=addeplerole")
	public ModelAndView addEpleRole(Model model, @RequestParam("epleid") String epleid, @RequestParam("roleid") int roleid) {
		logger.debug("UsersManageCtrl-addEpleRole start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			String[] userids = epleid.split(",");
			for (int i = 0; i < userids.length; i++) {
				UserRoleModel record = new UserRoleModel();
				record.setUserid(Integer.valueOf(userids[i]));
				record.setRoleid(roleid);
				userRoleModelDao.delEpleRole(record);
				userRoleModelDao.addEpleRole(record);
			}
			json.addObject("result", 1);
		} catch (Exception e) {
			json.addObject("result", 0);
			json.addObject("msg", e.getMessage());
		}
		logger.debug("UsersManageCtrl-addEpleRole end!");
		return json;
	}

	@RequestMapping(params = "atn=deleplerole")
	public ModelAndView delEpleRole(Model model, @RequestParam("urid") int urid) {
		logger.debug("UsersManageCtrl-delEpleRole start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			UserRoleModel record = new UserRoleModel();
			record.setId(urid);
			int ret = userRoleModelDao.delEpleRole(record);
			if (ret > 0)
				json.addObject("result", 1);
			else
				json.addObject("result", 0);
		} catch (Exception e) {
			json.addObject("result", 0);
			json.addObject("msg", e.getMessage());
		}
		logger.debug("UsersManageCtrl-delEpleRole end!");
		return json;
	}
}

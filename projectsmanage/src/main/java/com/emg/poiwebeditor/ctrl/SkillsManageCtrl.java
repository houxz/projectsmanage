package com.emg.poiwebeditor.ctrl;

import java.util.Date;
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

import com.emg.poiwebeditor.common.CommonConstants;
import com.emg.poiwebeditor.common.RoleType;
import com.emg.poiwebeditor.dao.projectsmanager.EmployeeSkillModelDao;
import com.emg.poiwebeditor.pojo.EmployeeSkillModel;
import com.emg.poiwebeditor.pojo.MetadataModel;
import com.emg.poiwebeditor.pojo.RoleModel;
import com.emg.poiwebeditor.service.CommService;
import com.emg.poiwebeditor.service.EmapgoAccountService;
import com.emg.poiwebeditor.service.ProjectManagerRoleService;

@Controller
@RequestMapping("/skillsmanage.web")
public class SkillsManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(SkillsManageCtrl.class);

	@Autowired
	private EmapgoAccountService emapgoAccountService;

	@Autowired
	private EmployeeSkillModelDao employeeSkillModelDao;

	@Autowired
	private ProjectManagerRoleService projectManagerRoleService;

	@Autowired
	private CommService commService;

	/**
	 * 人员熟练度管理页面
	 * 
	 * @param model
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping()
	public String openLader(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("SkillsManageCtrl-openLader start.");
		try {
			List<MetadataModel> skillModules = commService.getSkillModules();
			model.addAttribute("skillModules", skillModules);
			List<RoleModel> roles = projectManagerRoleService.getAllEnabledRoles();
			model.addAttribute("roles", roles);
			List<MetadataModel> skillLevels = commService.getSkillLevels();
			model.addAttribute("skillLevels", skillLevels);
			return "skillsmanage";
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
		logger.debug("SkillsManageCtrl-getEmployeeTree start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			List<Map<String, Object>> eplelist = emapgoAccountService.getEmployeeListForZTree();
			json.addObject("eplelist", eplelist);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("SkillsManageCtrl-getEmployeeTree end!");
		return json;
	}

	/**
	 * 取某人的人员技能能级
	 * 
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(params = "atn=geteplelevel")
	public ModelAndView getEpleLevel(Model model, @RequestParam("userid") Integer userid, @RequestParam("sysid") Long sysid) {
		logger.debug("SkillsManageCtrl-getEpleLevel start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			EmployeeSkillModel emParam = new EmployeeSkillModel();
			emParam.setUserid(userid);
			emParam.setSystemid(sysid.intValue());
			List<EmployeeSkillModel> em = employeeSkillModelDao.queryEmployeeSkills(emParam);
			List<MetadataModel> skillModules = commService.getSkillModules();
			List<MetadataModel> skillLevels = commService.getSkillLevels();
			List<RoleModel> roles = projectManagerRoleService.getAllEnabledRoles();
			for (EmployeeSkillModel e : em) {
				if (e.getSkilllevel() == null)
					continue;
				for (int i = 0; i < skillModules.size(); i++) {
					if (e.getSkillmodule() == skillModules.get(i).getValue().intValue()) {
						e.setSkillModuleDesc(skillModules.get(i).getDesc());
						break;
					}
				}
				for (int i = 0; i < skillLevels.size(); i++) {
					if (e.getSkilllevel() == skillLevels.get(i).getValue().intValue()) {
						e.setSkillLevelDesc(skillLevels.get(i).getDesc());
						break;
					}
				}
				for (int i = 0; i < roles.size(); i++) {
					if (e.getRoleid() == roles.get(i).getId()) {
						e.setRolename(roles.get(i).getRemark());
						break;
					}
				}
			}

			json.addObject("epleSkillList", em);
			json.addObject("result", 0);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("result", 1);
		}
		logger.debug("SkillsManageCtrl-getEpleLevel end!");
		return json;
	}

	@RequestMapping(params = "atn=deleplesill")
	public ModelAndView delEleSkill(Model model, @RequestParam("sysid") int sysid, @RequestParam("skillmodule") int skillmodule, @RequestParam("id") String id) {
		logger.debug("SkillsManageCtrl-delEleSkill start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			EmployeeSkillModel emParam = new EmployeeSkillModel();
			emParam.setId(id);
			emParam.setSkillmodule(skillmodule);
			emParam.setSystemid(sysid);
			employeeSkillModelDao.delEmployDetail(emParam);
			json.addObject("result", 0);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("result", 1);
		}
		logger.debug("SkillsManageCtrl-delEleSkill end!");
		return json;
	}

	/**
	 * 添加人员技能
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "atn=addEpleSkill")
	public ModelAndView addEpleSkill(Model model,
			HttpSession session,
			HttpServletRequest request,
			@RequestParam("sysid") int sysid,
			@RequestParam("skillmodule") int skillmodule,
			@RequestParam("epleid") String epleid,
			@RequestParam("eplename") String eplename,
			@RequestParam("skilllevel") int skilllevel,
			@RequestParam("rolename") String rolename) {
		logger.debug("SkillsManageCtrl-addEpleSkill start!");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			rolename = rolename == null || rolename.equals("") ? RoleType.ROLE_WORKER.toString() : rolename;
			List<RoleModel> rs = projectManagerRoleService.getAllEnabledRoles();
			int uid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);

			String[] epleidarr = epleid.split(",");
			String[] eplenamesarr = eplename.split(",");
			for (RoleModel r : rs) {
				if (r.getName().equals(rolename)) {
					for (int i = 0; i < epleidarr.length; i++) {
						EmployeeSkillModel emparam = new EmployeeSkillModel();
						emparam.setSystemid(sysid);
						emparam.setSkillmodule(skillmodule);
						emparam.setUserid(Integer.parseInt(epleidarr[i]));
						emparam.setRoleid(r.getId());
						employeeSkillModelDao.delEmployDetail(emparam);
						emparam.setSkilllevel(skilllevel);
						emparam.setUsername(eplenamesarr[i]);
						emparam.setOptime(new Date());
						emparam.setOpuid(uid);
						emparam.setRolename(rolename);
						employeeSkillModelDao.addEmployeDetail(emparam);
					}
				}
			}

			json.addObject("result", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.addObject("result", 0);
			json.addObject("msg", e.getMessage());
		}
		logger.debug("SkillsManageCtrl-addEpleSkill end!");
		return json;
	}

}

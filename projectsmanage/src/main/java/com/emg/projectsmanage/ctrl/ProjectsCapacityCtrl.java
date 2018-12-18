package com.emg.projectsmanage.ctrl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.common.CommonConstants;
import com.emg.projectsmanage.common.TaskTypeEnum;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.dao.projectsmanager.CapacityModelDao;
import com.emg.projectsmanage.pojo.CapacityModel;
import com.emg.projectsmanage.pojo.CapacityModelExample;
import com.emg.projectsmanage.pojo.CapacityModelExample.Criteria;

@Controller
@RequestMapping("/capacity.web")
public class ProjectsCapacityCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ProjectsCapacityCtrl.class);

	@Autowired
	private CapacityModelDao CapacityModelDao;

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProjectsCapacityCtrl-openLader start.");
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"" + TaskTypeEnum.POI_FEISHICE.getValue() + "\":\"" + TaskTypeEnum.POI_FEISHICE.getDes() + "\",");
		sb.append("\"" + TaskTypeEnum.POI_QUANGUOQC.getValue() + "\":\"" + TaskTypeEnum.POI_QUANGUOQC.getDes() + "\",");
		sb.append("\"" + TaskTypeEnum.POI_FEISHICEADDRESSTEL.getValue() + "\":\"" + TaskTypeEnum.POI_FEISHICEADDRESSTEL.getDes() + "\",");
		sb.append("\"" + TaskTypeEnum.POI_KETOU.getValue() + "\":\"" + TaskTypeEnum.POI_KETOU.getDes() + "\",");
		sb.append("\"" + TaskTypeEnum.POI_MC_KETOU.getValue() + "\":\"" + TaskTypeEnum.POI_MC_KETOU.getDes() + "\",");
		sb.append("\"" + TaskTypeEnum.POI_GEN.getValue() + "\":\"" + TaskTypeEnum.POI_GEN.getDes() + "\",");
		sb.append("\"" + TaskTypeEnum.POI_MC_GEN.getValue() + "\":\"" + TaskTypeEnum.POI_MC_GEN.getDes() + "\",");
		sb.append("}");
		model.addAttribute("poiTaskTypes", sb.toString());
		return "capacity";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String sort = ParamUtils.getParameter(request, "sort", "");
			String order = ParamUtils.getParameter(request, "order", "");
			String filter = ParamUtils.getParameter(request, "filter", "");

			CapacityModelExample example = new CapacityModelExample();
			Criteria criteria = example.or();
			
			if(!hasRole(request, RoleType.ROLE_POIVIDEOEDIT.toString())) {
				Integer userid = (Integer) session.getAttribute(CommonConstants.SESSION_USER_ID);
				criteria.andUseridEqualTo(userid);
			}

			if (filter.length() > 0) {
				Map<String, Object> filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						criteria.andIdEqualTo(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "tasktype":
						criteria.andTasktypeEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "processname":
						criteria.andProcessnameLike("%" + filterPara.get(key).toString() + "%");
						break;
					case "username":
						criteria.andUsernameLike("%" + filterPara.get(key).toString() + "%");
						break;
					case "roleid":
						criteria.andRoleidEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "time":
						criteria.andTimeEqualTo(filterPara.get(key).toString());
						break;
					default:
						break;
					}
				}
			}
			if (!sort.isEmpty()) {
				example.setOrderByClause(sort + " " + order);
			} else {
				example.setOrderByClause("time desc");
			}
			if (limit.compareTo(0) > 0)
				example.setLimit(limit);
			if (offset.compareTo(0) > 0)
				example.setOffset(offset);

			List<CapacityModel> rows = CapacityModelDao.selectByExample(example);
			int count = CapacityModelDao.countByExample(example);

			json.addObject("rows", rows);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("END");
		return json;

	}
}

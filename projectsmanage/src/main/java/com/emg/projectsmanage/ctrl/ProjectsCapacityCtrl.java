package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
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
import com.emg.projectsmanage.common.IsWorkTimeEnum;
import com.emg.projectsmanage.common.TaskTypeEnum;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.ResultModel;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.dao.projectsmanager.CapacityModelDao;
import com.emg.projectsmanage.dao.projectsmanager.CapacityQualityModelDao;
import com.emg.projectsmanage.pojo.CapacityModel;
import com.emg.projectsmanage.pojo.CapacityModelExample;
import com.emg.projectsmanage.pojo.CapacityModelExample.Criteria;
import com.emg.projectsmanage.pojo.CapacityQualityModel;
import com.emg.projectsmanage.pojo.CapacityQualityModelExample;

@Controller
@RequestMapping("/capacity.web")
public class ProjectsCapacityCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ProjectsCapacityCtrl.class);

	@Autowired
	private CapacityModelDao capacityModelDao;
	@Autowired
	private CapacityQualityModelDao capacityQualityModelDao;

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
		model.addAttribute("isWorkTimes", IsWorkTimeEnum.toJsonStr());
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
					case "iswork":
						criteria.andIsworkEqualTo(Integer.valueOf(filterPara.get(key).toString()));
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

			List<CapacityModel> rows = capacityModelDao.selectByExample(example);
			int count = capacityModelDao.countByExample(example);

			json.addObject("rows", rows);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("END");
		return json;

	}
	
	@RequestMapping(params = "atn=getdetails")
	public ModelAndView getDetails(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		ResultModel result = new ResultModel();
		try {
			Integer taskType = ParamUtils.getIntParameter(request, "tasktype", -1);
			Long projectid = ParamUtils.getLongParameter(request, "projectid", -1L);
			Integer userid = ParamUtils.getIntParameter(request, "userid", -1);
			Integer roleid = ParamUtils.getIntParameter(request, "roleid", -1);
			String time = ParamUtils.getParameter(request, "time");
			Integer iswork = ParamUtils.getIntParameter(request, "iswork", -1);
			
			CapacityQualityModelExample example = new CapacityQualityModelExample();
			example.or()
				.andTasktypeEqualTo(taskType)
				.andProjectidEqualTo(projectid)
				.andUseridEqualTo(userid)
				.andRoleidEqualTo(roleid)
				.andTimeEqualTo(time)
				.andIsworkEqualTo(iswork.byteValue());
			example.setOrderByClause("errortype ASC");
			Integer total = capacityQualityModelDao.countByExample(example );
			if (total.compareTo(0) > 0) {
				List<CapacityQualityModel> rows = capacityQualityModelDao.selectByExample(example);
				result.setResult(1);
				result.setTotal(total);
				result.setRows(rows);
			} else {
				result.setResult(1);
				result.setTotal(total);
				result.setRows(new ArrayList<CapacityQualityModel>());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResult(0);
			result.setResultMsg(e.getMessage());
		}
		json.addAllObjects(result);
		logger.debug("END");
		return json;
	}
}

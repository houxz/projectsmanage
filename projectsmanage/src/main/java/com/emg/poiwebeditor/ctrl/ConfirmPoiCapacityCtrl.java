package com.emg.poiwebeditor.ctrl;

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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.poiwebeditor.common.CommonConstants;
import com.emg.poiwebeditor.common.IsWorkTimeEnum;
import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.common.PoiProjectType;
import com.emg.poiwebeditor.common.RoleType;
import com.emg.poiwebeditor.common.TaskTypeEnum;
import com.emg.poiwebeditor.dao.projectsmanager.CapacityModelDao;
import com.emg.poiwebeditor.dao.projectsmanager.ConfirmPoiCapacityModelDao;
import com.emg.poiwebeditor.pojo.CapacityModel;
import com.emg.poiwebeditor.pojo.CapacityModelExample;
import com.emg.poiwebeditor.pojo.CapacityModelExample.Criteria;
import com.emg.poiwebeditor.pojo.ConfirmPoiCapacityModel;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/confirmpoicapacity.web")
public class ConfirmPoiCapacityCtrl extends BaseCtrl{
	private static final Logger logger = LoggerFactory.getLogger(ConfirmPoiCapacityCtrl.class);

	@Autowired
	private ConfirmPoiCapacityModelDao capacityModelDao;
	
	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProjectsCapacityCtrl-openLader start.");
	
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"" + TaskTypeEnum.POIPOLYMERIZE.getValue() + "\":\"" + TaskTypeEnum.POIPOLYMERIZE.getDes() + "\",");
		sb.append("}");
		model.addAttribute("poiTaskTypes", sb.toString());
		model.addAttribute("isWorkTimes", IsWorkTimeEnum.toJsonStr());
		
		model.addAttribute("poiprojectTypes",PoiProjectType.toJsonStr() );
		
		return "confirmpoicapacity";
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(params ="atn=pages")
	public ModelAndView pages(Model model,HttpServletRequest request,HttpSession session) {
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
					case "processid":
						criteria.andProcessidEqualTo(Long.valueOf(filterPara.get(key).toString()));
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
					case "poiprojecttype":
						criteria.addPoiProjectType(Integer.valueOf( filterPara.get(key).toString() ) );
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
			
			List<ConfirmPoiCapacityModel> rows = capacityModelDao.selectByExample(example);
			int count = capacityModelDao.countByExample(example);

			json.addObject("rows", rows);
			json.addObject("total", count);
			json.addObject("result", 1);
			
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("END");
		return json;
		
	}
}

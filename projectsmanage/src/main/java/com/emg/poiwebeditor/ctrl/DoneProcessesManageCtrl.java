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

import com.emg.poiwebeditor.common.ItemAreaType;
import com.emg.poiwebeditor.common.ItemSetEnable;
import com.emg.poiwebeditor.common.ItemSetSysType;
import com.emg.poiwebeditor.common.ItemSetType;
import com.emg.poiwebeditor.common.ItemSetUnit;
import com.emg.poiwebeditor.common.ModelEnum;
import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.common.PriorityLevel;
import com.emg.poiwebeditor.common.ProcessState;
import com.emg.poiwebeditor.common.ProcessType;
import com.emg.poiwebeditor.ctrl.BaseCtrl;
import com.emg.poiwebeditor.dao.process.ProcessModelDao;
import com.emg.poiwebeditor.pojo.ProcessModel;
import com.emg.poiwebeditor.pojo.ProcessModelExample;
import com.emg.poiwebeditor.pojo.ProcessModelExample.Criteria;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/doneprocesses.web")
public class DoneProcessesManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(DoneProcessesManageCtrl.class);
	
	@Autowired
	private ProcessModelDao processModelDao;

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("OPENLADER");

		model.addAttribute("processStates", ProcessState.undoneToJsonStr());
		model.addAttribute("processTypes", ProcessType.toJsonStr());
		model.addAttribute("itemAreaTypes", ItemAreaType.toJsonStr());
		model.addAttribute("priorityLevels", PriorityLevel.toJsonStr());
		model.addAttribute("itemsetEnables", ItemSetEnable.toJsonStr());
		model.addAttribute("itemsetSysTypes", ItemSetSysType.toJsonStr());
		model.addAttribute("itemsetTypes", ItemSetType.toJsonStr());
		model.addAttribute("itemsetUnits", ItemSetUnit.toJsonStr());
		//add by lianhr begin 2019/02/19
		model.addAttribute("itemmodels", ModelEnum.toJsonStr());
		//add by lianhr end

		return "doneprocessesmanage";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> filterPara = null;
			ProcessModelExample example = new ProcessModelExample();
			Criteria criteria = example.or();
			criteria.andStateEqualTo(ProcessState.COMPLETE.getValue());
			criteria.andTypeEqualTo(ProcessType.POIPOLYMERIZE.getValue());
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						criteria.andIdEqualTo(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "type":
						criteria.andTypeEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "name":
						criteria.andNameLike("%" + filterPara.get(key).toString() + "%");
						break;
					case "priority":
						criteria.andPriorityEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "username":
						criteria.andUsernameLike("%" + filterPara.get(key).toString() + "%");
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}

			if (limit.compareTo(0) > 0)
				example.setLimit(limit);
			if (offset.compareTo(0) > 0)
				example.setOffset(offset);
			example.setOrderByClause("priority desc, id");

			List<ProcessModel> rows = processModelDao.selectByExample(example);
			int count = processModelDao.countByExample(example);

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

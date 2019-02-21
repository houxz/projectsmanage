package com.emg.projectsmanage.ctrl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.common.IsWorkTimeEnum;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.TaskTypeEnum;
import com.emg.projectsmanage.dao.attach.AttachCapacityModelDao;
import com.emg.projectsmanage.dao.attach.AttachCheckCapacityModelDao;
import com.emg.projectsmanage.dao.attach.CycleModelDao;
import com.emg.projectsmanage.pojo.AttachCapacityModelExample;
import com.emg.projectsmanage.pojo.AttachCapacityModelExample.Criteria;
import com.emg.projectsmanage.pojo.AttachCheckCapacityModel;
import com.emg.projectsmanage.pojo.AttachMakeCapacityModel;
import com.emg.projectsmanage.pojo.CycleModel;
import com.emg.projectsmanage.pojo.CycleModelExample;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/attachcapacity.web")
public class ProjectsAttachCapacityCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ProjectsAttachCapacityCtrl.class);

	@Autowired
	private AttachCapacityModelDao attachCapacityModelDao;
	
	@Autowired
	private AttachCheckCapacityModelDao attachCheckCapacityModelDao;
	
	@Autowired
	private CycleModelDao cycleModelDao;

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ProjectsattachCapacityCtrl-openLader start.");
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
		return "attachcapacity";
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

			AttachCapacityModelExample example = new AttachCapacityModelExample();
			Criteria criteria = example.or();
			String startdate = ParamUtils.getParameter(request, "startdate", "");
			String enddate = ParamUtils.getParameter(request, "enddate", "");
			if(startdate != null && !startdate.isEmpty()) {
				criteria.andCountdateGreaterThanOrEqualTo(transferDate(startdate));
			}
			if (enddate != null && !enddate.isEmpty()) {
				criteria.andCountdateLessThanOrEqualTo(transferDate(enddate));
			}
			if (filter.length() > 0) {
				Map<String, Object> filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {					
					case "projectType":
						criteria.andProjectType(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "username":
						criteria.andUsernameLike("%" + filterPara.get(key).toString() + "%");
						break;	
					case "countdate":		
						criteria.andCountDate(filterPara.get(key).toString());
						break;
					default:
						break;
					}
				}
			}
			if (!sort.isEmpty()) {
				example.setOrderByClause(sort + " " + order);
			} else {
				example.setOrderByClause("countdate desc");
			}
			if (limit.compareTo(0) > 0)
				example.setLimit(limit);
			if (offset.compareTo(0) > 0)
				example.setOffset(offset);
			// cycleModelDao
			List<AttachMakeCapacityModel> rows = attachCapacityModelDao.selectAttachCapacity(example);
			for(int i = rows.size() - 1; i > 0; i--) {
				AttachMakeCapacityModel child = rows.get(i);
				AttachMakeCapacityModel inner = rows.get(i - 1);
				if (inner.getUserid() == child.getUserid() && inner.getProjectType() == child.getProjectType()) {
					inner.setLaneCreate(inner.getLaneCreate() + child.getLaneCreate());
					inner.setLaneUpdate(inner.getLaneUpdate() + child.getLaneUpdate());
					inner.setLaneDelete(inner.getLaneDelete() + child.getLaneDelete());
					inner.setDirectionCreate(inner.getDirectionCreate() + child.getDirectionCreate());
					inner.setDirectionUpdate(inner.getDirectionUpdate() + child.getDirectionUpdate());
					inner.setDirectionDelete(inner.getDirectionDelete() + child.getDirectionDelete());
					inner.setJunctionviewCreate(inner.getJunctionviewCreate() + child.getJunctionviewCreate());
					inner.setJunctionviewUpdate(inner.getJunctionviewUpdate() + child.getJunctionviewUpdate());
					inner.setJunctionviewDelete(inner.getJunctionviewDelete() + child.getJunctionviewDelete());
					inner.setMakeErrorCount(inner.getMakeErrorCount() + child.getMakeErrorCount());
					rows.remove(i);
				}
			}
			CycleModelExample cycleModel = new CycleModelExample();
			CycleModelExample.Criteria cycleCriteria = cycleModel.or();
			if(startdate != null && !startdate.isEmpty()) {
				cycleCriteria.andLogintimeGreaterThan(transferDate(startdate));
			}
			if (enddate != null && !enddate.isEmpty()) {
				cycleCriteria.andLogouttimeLessThan(transferDate(enddate));
			}
			if (filter.length() > 0) {
				Map<String, Object> filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {					
					case "projectType":
						cycleCriteria.andProjecttypeEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "username":
						cycleCriteria.andUsernameLike("%" + filterPara.get(key).toString() + "%");
						break;		
					case "countdate":		
						criteria.andCountDate(filterPara.get(key).toString());
						break;
					default:
						break;
					}
				}
			}
			List<CycleModel> cycles = cycleModelDao.selectByExample(cycleModel);
			for (int i = cycles.size() - 1; i > 0; i--) {
				if(cycles.get(i).getUserid() == cycles.get(i - 1).getUserid() && cycles.get(i).getProjecttype() == cycles.get(i - 1).getProjecttype() ) {
					cycles.get(i-1).setTimecount(cycles.get(i).getTimecount() + cycles.get(i - 1).getTimecount());
					cycles.remove(i);
				}
			}
			for (AttachMakeCapacityModel capacity : rows) {
				for (CycleModel cycle : cycles) {
					if(capacity.getUserid() == cycle.getUserid() && capacity.getProjectType() == cycle.getProjecttype()) {
						capacity.setWorktime(cycle.getTimecount());
					}
				}
			}
			int count = attachCapacityModelDao.countByExample(example);
			json.addObject("startdate", startdate);
			json.addObject("enddate", enddate);
			json.addObject("rows", rows);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("END");
		return json;

	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=checks")
	public ModelAndView checks(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String sort = ParamUtils.getParameter(request, "sort", "");
			String order = ParamUtils.getParameter(request, "order", "");
			String filter = ParamUtils.getParameter(request, "filter", "");
			String startdate = ParamUtils.getParameter(request, "startdate", "");
			String enddate = ParamUtils.getParameter(request, "enddate", "");
			AttachCapacityModelExample example = new AttachCapacityModelExample();
			Criteria criteria = example.or();
			if(startdate != null && !startdate.isEmpty()) {
				criteria.andCountdateGreaterThanOrEqualTo(transferDate(startdate));
			}
			if (enddate != null && !enddate.isEmpty()) {
				criteria.andCountdateLessThanOrEqualTo(transferDate(enddate));
			}
			if (filter.length() > 0) {
				Map<String, Object> filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						criteria.andIdEqualTo(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "projectTypeCheck":
						criteria.andProjectType(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "username":
						criteria.andUsernameLike("%" + filterPara.get(key).toString() + "%");
						break;
					case "roleid":
						criteria.andRoleidEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					default:
						break;
					}
				}
			}
			if (!sort.isEmpty()) {
				example.setOrderByClause(sort + " " + order);
			} else {
				example.setOrderByClause("countdate desc");
			}
			if (limit.compareTo(0) > 0)
				example.setLimit(limit);
			if (offset.compareTo(0) > 0)
				example.setOffset(offset);

			List<AttachCheckCapacityModel> rows = attachCheckCapacityModelDao.selectcheckAttachCapacity(example);
			for(int i = rows.size() - 1; i > 0; i--) {
				AttachCheckCapacityModel child = rows.get(i);
				AttachCheckCapacityModel inner = rows.get(i - 1);
				if (inner.getUserid() == child.getUserid() && inner.getProjectTypeCheck() == child.getProjectTypeCheck()) {
					inner.setArrowPatternJunctionview(inner.getArrowPatternJunctionview() + child.getArrowPatternJunctionview());
					inner.setArrowSceneJunctionview(inner.getArrowSceneJunctionview() + child.getArrowSceneJunctionview());
					inner.setCheckCount(inner.getCheckCount() + child.getCheckCount());
					inner.setEndRoadDirection(inner.getEndRoadDirection() + child.getEndRoadDirection());
					inner.setEndRoadLane(inner.getEndRoadLane() + child.getEndRoadLane());
					inner.setEndRoadPatternJunctionview(inner.getEndRoadPatternJunctionview() + child.getEndRoadPatternJunctionview());
					inner.setEndRoadSceneJunctionview(inner.getEndRoadSceneJunctionview() + child.getEndRoadSceneJunctionview());
					inner.setErrorCount(inner.getErrorCount() + child.getErrorCount());
					inner.setExitCodeDirection(inner.getExitCodeDirection() + child.getExitCodeDirection());
					inner.setExitDirection(inner.getExitDirection() + child.getExitDirection());
					inner.setInfoDirection(inner.getInfoDirection() + child.getInfoDirection());
					inner.setInnerLinkLane(inner.getInnerLinkLane() + child.getInnerLinkLane());
					inner.setLostDirection(inner.getLostDirection() + child.getLostDirection());
					inner.setLostLane(inner.getLostLane() + child.getLostLane());
					inner.setLostPatternJunctionview(inner.getLostPatternJunctionview() + child.getLostPatternJunctionview());
					inner.setLostSceneJunctionview(inner.getLostPatternJunctionview() + child.getLostPatternJunctionview());
					inner.setMakeMoreDirection(inner.getMakeMoreDirection() + child.getMakeMoreDirection());
					inner.setMakeMoreLane(inner.getMakeMoreLane() + child.getMakeMoreLane());
					inner.setMakeMorePatternJunctionview(inner.getMakeMorePatternJunctionview() + child.getMakeMorePatternJunctionview());
					inner.setMakeMoreSceneJunctionview(inner.getMakeMoreSceneJunctionview() + inner.getMakeMoreSceneJunctionview());
					inner.setPictureChoicePatternJunctionview(inner.getPictureChoicePatternJunctionview() + child.getPictureChoicePatternJunctionview());
					inner.setPictureChoiceSceneJunctionview(inner.getPictureChoiceSceneJunctionview() + child.getPictureChoiceSceneJunctionview());
					inner.setPictureTypePatternJunctionview(inner.getPictureTypePatternJunctionview() + child.getPictureTypePatternJunctionview());
					inner.setPictureTypeSceneJunctionview(inner.getPictureTypeSceneJunctionview() + child.getPictureTypeSceneJunctionview());
					inner.setProjectTypeCheck(inner.getProjectTypeCheck() + child.getProjectTypeCheck());
					inner.setTurnLane(inner.getTurnLane() + child.getTurnLane());
					inner.setUnknownDirection(inner.getUnknownDirection() + child.getUnknownDirection());
					inner.setUnknownJunctionview(inner.getUnknownJunctionview() + child.getUnknownJunctionview());
					inner.setUnknownLane(inner.getUnknownLane() + child.getUnknownLane());
				}
			}
			CycleModelExample cycleModel = new CycleModelExample();
			CycleModelExample.Criteria cycleCriteria = cycleModel.or();
			if(startdate != null && !startdate.isEmpty()) {
				cycleCriteria.andLogintimeGreaterThan(transferDate(startdate));
			}
			if (enddate != null && !enddate.isEmpty()) {
				cycleCriteria.andLogouttimeLessThan(transferDate(enddate));
			}
			if (filter.length() > 0) {
				Map<String, Object> filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {					
					case "projectType":
						cycleCriteria.andProjecttypeEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "username":
						cycleCriteria.andUsernameLike("%" + filterPara.get(key).toString() + "%");
						break;		
					case "countdate":		
						criteria.andCountDate(filterPara.get(key).toString());
						break;
					default:
						break;
					}
				}
			}
			List<CycleModel> cycles = cycleModelDao.selectByExample(cycleModel);
			for (int i = cycles.size() - 1; i > 0; i--) {
				if(cycles.get(i).getUserid() == cycles.get(i - 1).getUserid() && cycles.get(i).getProjecttype() == cycles.get(i - 1).getProjecttype() ) {
					cycles.get(i-1).setTimecount(cycles.get(i).getTimecount() + cycles.get(i - 1).getTimecount());
					cycles.remove(i);
				}
			}
			for (AttachCheckCapacityModel capacity : rows) {
				for (CycleModel cycle : cycles) {
					if(capacity.getUserid() == cycle.getUserid() && capacity.getProjectTypeCheck() == cycle.getProjecttype()) {
						capacity.setWorktime(cycle.getTimecount());
					}
				}
			}
			//List<AttachCheckCapacityModel> rows = attachCheckCapacityModelDao.selectcheckAttachCapacity(example);
			int count = attachCheckCapacityModelDao.countByExample(example);

			json.addObject("rows", rows);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("END");
		return json;

	}
	
	private Date transferDate(String str) throws ParseException {
		if (str == null || str.isEmpty()) return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(str);
	}
}

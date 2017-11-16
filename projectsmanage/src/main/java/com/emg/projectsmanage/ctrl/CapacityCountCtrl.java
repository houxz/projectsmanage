package com.emg.projectsmanage.ctrl;

import com.emg.projectsmanage.bean.CapacityErrorTypeModel;
import com.emg.projectsmanage.bean.CapacityUserModel;
import com.emg.projectsmanage.common.ErrorStatus;
import com.emg.projectsmanage.common.GeoErrorType;
import com.emg.projectsmanage.common.ModifyStatus;
import com.emg.projectsmanage.common.OperateType;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.PoiType;
import com.emg.projectsmanage.common.ProptyErrorType;
import com.emg.projectsmanage.common.RoleType;
import com.emg.projectsmanage.dao.projectsmanager.ProjectModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsErrorCountModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsErrorPOICountModelDao;
import com.emg.projectsmanage.dao.projectsmanager.ProjectsWorkCountModelDao;
import com.emg.projectsmanage.pojo.ProjectModel;
import com.emg.projectsmanage.pojo.MetadataModel;
import com.emg.projectsmanage.pojo.ProjectModelExample;
import com.emg.projectsmanage.pojo.ProjectsErrorCountModel;
import com.emg.projectsmanage.pojo.ProjectsErrorPOICountModel;
import com.emg.projectsmanage.pojo.ProjectsWorkCountModel;
import com.emg.projectsmanage.service.CommService;

import java.util.ArrayList;
import java.util.List;
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

@Controller
@RequestMapping({ "/capacitycount.web" })
public class CapacityCountCtrl extends BaseCtrl {
	private static final Logger logger = LoggerFactory.getLogger(CapacityCountCtrl.class);

	@Autowired
	private ProjectModelDao projectModelDao;

	@Autowired
	private CommService commService;

	@Autowired
	private ProjectsWorkCountModelDao projectsWorkCountModelDao;
	@Autowired
	private ProjectsErrorCountModelDao projectsErrorCountModelDao;
	@Autowired
	private ProjectsErrorPOICountModelDao projectsErrorPOICountModelDao;

	@RequestMapping
	public String page(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("capacitycount start.");
		try {
			ProjectModelExample example = new ProjectModelExample();
			example.setOrderByClause("id");
			List<ProjectModel> capacityProjectList = projectModelDao.selectByExample(example);
			model.addAttribute("capacityProjectList", capacityProjectList);

			List<MetadataModel> capacityOprationTypeList = commService.queryAllOprationType();
			model.addAttribute("capacityOprationTypeList", capacityOprationTypeList);

			return "capacitycount";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:login.jsp";
	}

	/**
	 * 编辑工作量统计
	 * 
	 * @param 起始日期
	 *            :begin
	 * @param 结束日期
	 *            :end
	 * @param 项目名称
	 *            :capacityProject
	 * @param 操作类型
	 *            :capacityOprationType
	 * @return
	 */
	@RequestMapping(params = { "atn=modifycount" })
	public ModelAndView QueryModifyCount(HttpSession session, HttpServletRequest request) {
		logger.debug("CapacityCountCtrl-------- QueryModifyCount.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		String starttime = ParamUtils.getParameter(request, "begin", "");
		String endtime = ParamUtils.getParameter(request, "end", "");
		Long capacityProject = ParamUtils.getLongParameter(request, "capacityProject", -1);
		Integer capacityOprationType = ParamUtils.getIntParameter(request, "capacityOprationType", -1);

		List<CapacityUserModel> userList = new ArrayList<CapacityUserModel>();
		List<ProjectsWorkCountModel> capacityCountList = new ArrayList<ProjectsWorkCountModel>();
		List<ProjectsWorkCountModel> capacityRolesIdList = new ArrayList<ProjectsWorkCountModel>();
		String lastTime = "";

		try {
			ProjectsWorkCountModel record = new ProjectsWorkCountModel();
			if (!starttime.isEmpty())
				record.setStarttime(starttime);
			if (!endtime.isEmpty())
				record.setEndtime(endtime);
			if (!capacityProject.equals(-1L))
				record.setProjectid(capacityProject);
			if (!capacityOprationType.equals(-1))
				record.setOperatetype(capacityOprationType);
			capacityCountList = projectsWorkCountModelDao.queryModifyCount(record);
			capacityRolesIdList = projectsWorkCountModelDao.queryDistinctRolesIdList(record);
			lastTime = projectsWorkCountModelDao.queryModifyTime(record);

			for (ProjectsWorkCountModel capacityRolesId : capacityRolesIdList) {
				Integer userid = capacityRolesId.getUserid();
				String username = capacityRolesId.getUsername();
				Integer roleid = capacityRolesId.getRoleid();
				String roleName = capacityRolesId.getRolename();
				Integer poiType = capacityRolesId.getPoitype();

				CapacityUserModel userModel = new CapacityUserModel();
				userModel.setId(userid);
				userModel.setUserName(username);
				userModel.setRealName(username);
				userModel.setIdentity(roleName);
				for (ProjectsWorkCountModel capacityCount : capacityCountList) {
					Integer curUserID = capacityCount.getUserid();
					Integer curRoleID = capacityCount.getRoleid();
					Integer curOpType = capacityCount.getOperatetype();
					Integer curPoiType = capacityCount.getPoitype();
					Integer curCount = capacityCount.getCount();
					if ((curUserID.equals(userid)) && (curRoleID.equals(roleid)) && (curPoiType.equals(poiType))) {
						if (curOpType == OperateType.ADD.getValue()) {
							userModel.getPoiAddCountModel().setCount(curCount);
						} else if (curOpType == OperateType.CONFIRM.getValue()) {
							userModel.getPoiSureCountModel().setCount(curCount);
						} else if (curOpType == OperateType.MODIFY.getValue()) {
							userModel.getPoiModifyCountModel().setCount(curCount);
						} else if (curOpType == OperateType.DELETE.getValue()) {
							userModel.getPoiDelCountModel().setCount(curCount);
						} else {
							logger.debug("QueryModifyCount--------curOpType: " + curOpType);
						}
					}
				}
				if (poiType.equals(PoiType.COMMON.getValue())) {
					userModel.setPoiType(PoiType.COMMON.getDes());
				} else if (poiType.equals(PoiType.SITE.getValue())) {
					userModel.setPoiType(PoiType.SITE.getDes());
				} else {
					userModel.setPoiType(PoiType.UNKNOW.getDes());
				}
				userModel.setPoiCount(userModel.getAllPoiCount());
				userList.add(userModel);
			}
			json.addObject("result", "1");
			json.addObject("resultList", userList);
			json.addObject("lastTime", lastTime);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("result", "-1");
		}
		return json;
	}

	/**
	 * 属性错误工作量统计
	 * 
	 * @param 起始日期
	 *            :begin
	 * @param 结束日期
	 *            :end
	 * @param 项目名称
	 *            :capacityProptyErrorProject
	 * @return
	 */
	@RequestMapping(params = { "atn=capacityproptyerrorcountforobj" })
	public ModelAndView QueryModifyProptyErrorCountForObj(HttpSession session, HttpServletRequest request) {
		logger.debug("CapacityCountCtrl-------- QueryModifyProptyErrorCountForObj.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		String starttime = ParamUtils.getParameter(request, "begin", "");
		String endtime = ParamUtils.getParameter(request, "end", "");
		Long capacityProptyErrorProject = ParamUtils.getLongParameter(request, "capacityProptyErrorProject", -1L);

		List<CapacityUserModel> userList = new ArrayList<CapacityUserModel>();
		List<ProjectsErrorCountModel> capacityCountList = new ArrayList<ProjectsErrorCountModel>();
		List<ProjectsErrorCountModel> capacityRolesIdList = new ArrayList<ProjectsErrorCountModel>();
		String lastTime = "";

		try {
			ProjectsErrorCountModel record = new ProjectsErrorCountModel();
			if (!starttime.isEmpty())
				record.setStarttime(starttime);
			if (!endtime.isEmpty())
				record.setEndtime(endtime);
			if (!capacityProptyErrorProject.equals(-1L))
				record.setProjectid(capacityProptyErrorProject);
			capacityCountList = projectsErrorCountModelDao.queryProptyErrorCount(record);
			capacityRolesIdList = projectsErrorCountModelDao.queryDistinctProptyErrorRolesIdList(record);
			lastTime = projectsErrorCountModelDao.queryProptyErrorTime(record);

			for (ProjectsErrorCountModel capacityRolesId : capacityRolesIdList) {
				CapacityUserModel userModel = new CapacityUserModel();
				Integer userid = capacityRolesId.getUserid();
				String userName = capacityRolesId.getUsername();
				Integer roleid = capacityRolesId.getRoleid();
				String roleName = capacityRolesId.getRolename();

				userModel.setId(userid);
				userModel.setUserName(userName);
				userModel.setRealName(userName);
				userModel.setIdentity(roleName);
				for (ProjectsErrorCountModel capacityCount : capacityCountList) {
					Integer curUserID = capacityCount.getUserid();
					Integer curRoleID = capacityCount.getRoleid();
					if (curUserID.equals(userid) && curRoleID.equals(roleid)) {
						Long curErrorTypeID = capacityCount.getErrortype();
						Integer curModifystate = capacityCount.getModifystate();
						Integer curErrorstate = capacityCount.getErrorstate();
						String currErrorFieldName = capacityCount.getFieldname();
						Integer currCount = capacityCount.getCount();

						// 编辑人员 修改状态为 接收或者其他值 错误状态为已解决
						if (((curRoleID.equals(RoleType.ROLE_WORKER.getValue())
								&& (curModifystate.equals(ModifyStatus.OTHERS.getValue()) || curModifystate.equals(ModifyStatus.ACCEPTED.getValue())) && (curErrorstate
									.equals(ErrorStatus.SOLVED.getValue()))) ||
						// 检验人员 修改状态为接受 错误状态为已解决
						(curRoleID.equals(RoleType.ROLE_CHECKER.getValue()) && (curModifystate.equals(ModifyStatus.ACCEPTED.getValue()) && curErrorstate.equals(ErrorStatus.SOLVED
								.getValue()))))) {
							if (curErrorTypeID.equals(ProptyErrorType.NONE.getValue())) {
								userModel.getMissErrorTypeModel().setId(curErrorTypeID);
								userModel.getMissErrorTypeModel().setName(ProptyErrorType.NONE.getDes());
								int cnt = fillCapacityErrorFieldById(userModel.getMissErrorTypeModel(), currErrorFieldName, currCount);
								userModel.getMissErrorTypeModel().setCount(cnt);
							} else if (curErrorTypeID.equals(ProptyErrorType.TYPOS.getValue())) {
								userModel.getErrorCodeTypeModel().setId(curErrorTypeID);
								userModel.getErrorCodeTypeModel().setName(ProptyErrorType.TYPOS.getDes());
								int cnt = fillCapacityErrorFieldById(userModel.getErrorCodeTypeModel(), currErrorFieldName, currCount);
								userModel.getErrorCodeTypeModel().setCount(cnt);
							} else if (curErrorTypeID.equals(ProptyErrorType.ILLEGAL.getValue())) {
								userModel.getErrorRuleTypeModel().setId(curErrorTypeID);
								userModel.getErrorRuleTypeModel().setName(ProptyErrorType.ILLEGAL.getDes());
								int cnt = fillCapacityErrorFieldById(userModel.getErrorRuleTypeModel(), currErrorFieldName, currCount);
								userModel.getErrorRuleTypeModel().setCount(cnt);
							} else if (curErrorTypeID.equals(ProptyErrorType.EXTRA.getValue())) {
								userModel.getErrorMoreTypeModel().setId(curErrorTypeID);
								userModel.getErrorMoreTypeModel().setName(ProptyErrorType.EXTRA.getDes());
								int cnt = fillCapacityErrorFieldById(userModel.getErrorMoreTypeModel(), currErrorFieldName, currCount);
								userModel.getErrorMoreTypeModel().setCount(cnt);
							} else if (curErrorTypeID.equals(ProptyErrorType.MISS.getValue())) {
								userModel.getMissDoneTypeModel().setId(curErrorTypeID);
								userModel.getMissDoneTypeModel().setName(ProptyErrorType.MISS.getDes());
								int cnt = fillCapacityErrorFieldById(userModel.getMissDoneTypeModel(), currErrorFieldName, currCount);
								userModel.getMissDoneTypeModel().setCount(cnt);
							} else if (curErrorTypeID.equals(ProptyErrorType.SECRECY.getValue())) {
								userModel.getErrorSecretTypeModel().setId(curErrorTypeID);
								userModel.getErrorSecretTypeModel().setName(ProptyErrorType.SECRECY.getDes());
								int cnt = fillCapacityErrorFieldById(userModel.getErrorSecretTypeModel(), currErrorFieldName, currCount);
								userModel.getErrorSecretTypeModel().setCount(cnt);
							} else {
								logger.debug("QueryModifyProptyErrorCountForObj--------curErrorTypeID: " + curErrorTypeID);
							}
						}
					}
				}
				userModel.setCount(userModel.getAllTypeCount());
				userList.add(userModel);
			}

			json.addObject("result", "1");
			json.addObject("resultList", userList);
			json.addObject("lastTime", lastTime);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("result", "-1");
		}
		return json;
	}

	/**
	 * 几何错误工作量统计
	 * 
	 * @param 起始日期
	 *            :begin
	 * @param 结束日期
	 *            :end
	 * @param 项目名称
	 *            :capacityGeoErrorProject
	 * @return
	 */
	@RequestMapping(params = { "atn=capacitygeoerrorcount" })
	public ModelAndView QueryModifyGeoErrorCount(HttpSession session, HttpServletRequest request) {
		logger.debug("CapacityCountCtrl-------- QueryModifyGeoErrorCount.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		String starttime = ParamUtils.getParameter(request, "begin", "");
		String endtime = ParamUtils.getParameter(request, "end", "");
		Long capacityGeoErrorProject = ParamUtils.getLongParameter(request, "capacityGeoErrorProject", -1L);

		List<CapacityUserModel> userList = new ArrayList<CapacityUserModel>();
		List<ProjectsErrorCountModel> capacityCountList = new ArrayList<ProjectsErrorCountModel>();
		List<ProjectsErrorCountModel> capacityRolesIdList = new ArrayList<ProjectsErrorCountModel>();
		String lastTime = "";

		try {
			ProjectsErrorCountModel record = new ProjectsErrorCountModel();
			if (!starttime.isEmpty())
				record.setStarttime(starttime);
			if (!endtime.isEmpty())
				record.setEndtime(endtime);
			if (!capacityGeoErrorProject.equals(-1L))
				record.setProjectid(capacityGeoErrorProject);
			capacityCountList = projectsErrorCountModelDao.queryModifyGeoCount(record);
			capacityRolesIdList = projectsErrorCountModelDao.queryDistinctGeoRolesIdList(record);
			lastTime = projectsErrorCountModelDao.queryModifyGeoTime(record);

			for (ProjectsErrorCountModel capacityRolesId : capacityRolesIdList) {
				CapacityUserModel userModel = new CapacityUserModel();
				Integer userid = capacityRolesId.getUserid();
				String userName = capacityRolesId.getUsername();
				Integer roleid = capacityRolesId.getRoleid();
				String roleName = capacityRolesId.getRolename();

				userModel.setId(userid);
				userModel.setUserName(userName);
				userModel.setRealName(userName);
				userModel.setIdentity(roleName);

				for (ProjectsErrorCountModel capacityCount : capacityCountList) {
					Integer curUserID = capacityCount.getUserid();
					Integer curRoleID = capacityCount.getRoleid();
					Long curErrorType = capacityCount.getErrortype();
					Integer curModifyState = capacityCount.getModifystate();
					Integer curErrorState = capacityCount.getErrorstate();
					Integer curCount = capacityCount.getCount();
					if (((curUserID.equals(userid)) && (curRoleID.equals(roleid))) &&
					// 编辑人员 修改状态为 接收或者其他值 错误状态为已解决
							((curRoleID.equals(RoleType.ROLE_WORKER.getValue())
									&& (curModifyState.equals(ModifyStatus.OTHERS.getValue()) || curModifyState.equals(ModifyStatus.ACCEPTED.getValue())) && (curErrorState
										.equals(ErrorStatus.SOLVED.getValue()))) ||
							// 检验人员 修改状态为接受 错误状态为已解决
							((curRoleID.equals(RoleType.ROLE_CHECKER.getValue())) && ((curModifyState.equals(ModifyStatus.ACCEPTED.getValue())) && (curErrorState
									.equals(ErrorStatus.SOLVED.getValue())))))) {
						if (curErrorType.equals(GeoErrorType.MISSNEW.getValue())) {
							userModel.getCapacityGeoMissAddCountModel().setCount(curCount);
						} else if (curErrorType.equals(GeoErrorType.MISSCONFIRM.getValue())) {
							userModel.getCapacityGeoMissSureCountModel().setCount(curCount);
						} else if (curErrorType.equals(GeoErrorType.EXTRANEW.getValue())) {
							userModel.getCapacityGeoMoreAddCountModel().setCount(curCount);
						} else if (curErrorType.equals(GeoErrorType.EXTRACONFIRM.getValue())) {
							userModel.getCapacityGeoMoreSureCountModel().setCount(curCount);
						} else if (curErrorType.equals(GeoErrorType.MISSDELETE.getValue())) {
							userModel.getCapacityGeoMissDelCountModel().setCount(curCount);
						} else if (curErrorType.equals(GeoErrorType.EXTRADELETE.getValue())) {
							userModel.getCapacityGeoMoreDelCountModel().setCount(curCount);
						} else if (curErrorType.equals(GeoErrorType.TRUEPOSITION.getValue())) {
							userModel.getCapacityGeoPosErrorCountModel().setCount(curCount);
						} else if (curErrorType.equals(GeoErrorType.RELATIVEPOSITION.getValue())) {
							userModel.getCapacityGeoDirectionErrorCountModel().setCount(curCount);
						} else if (curErrorType.equals(GeoErrorType.GUIDEPOSITION.getValue())) {
							userModel.getCapacityGeoPosGuideErrorCountModel().setCount(curCount);
						} else {
							logger.debug("QueryModifyGeoErrorCount--------curErrorType: " + curErrorType);
						}
					}
				}
				userModel.setGeoCount(userModel.getAllGeoCount());
				userList.add(userModel);
			}
			json.addObject("result", "1");
			json.addObject("resultList", userList);
			json.addObject("lastTime", lastTime);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("result", "-1");
		}
		return json;
	}

	/**
	 * POI错误工作量统计
	 * 
	 * @param 起始日期
	 *            :begin
	 * @param 结束日期
	 *            :end
	 * @param 项目名称
	 *            :capacityProptyErrorProject
	 * @return
	 */
	@RequestMapping(params = { "atn=capacitypoierrorcount" })
	public ModelAndView QueryModifyPOIErrorCount(HttpSession session, HttpServletRequest request) {
		logger.debug("CapacityCountCtrl-------- QueryModifyPOIErrorCount.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		String starttime = ParamUtils.getParameter(request, "begin", "");
		String endtime = ParamUtils.getParameter(request, "end", "");
		Long capacityProptyErrorProject = ParamUtils.getLongParameter(request, "capacityProptyErrorProject", -1);

		List<CapacityUserModel> userList = new ArrayList<CapacityUserModel>();
		List<ProjectsErrorPOICountModel> capacityCountList = new ArrayList<ProjectsErrorPOICountModel>();
		List<ProjectsErrorPOICountModel> capacityRolesIdList = new ArrayList<ProjectsErrorPOICountModel>();
		String lastTime = "";

		try {
			ProjectsErrorPOICountModel record = new ProjectsErrorPOICountModel();
			if (!starttime.isEmpty())
				record.setStarttime(starttime);
			if (!endtime.isEmpty())
				record.setEndtime(endtime);
			if (!capacityProptyErrorProject.equals(-1L))
				record.setProjectid(capacityProptyErrorProject);
			capacityCountList = projectsErrorPOICountModelDao.queryModifyPOICount(record);
			capacityRolesIdList = projectsErrorPOICountModelDao.queryDistinctPOIRolesIdList(record);
			lastTime = projectsErrorPOICountModelDao.queryModifyPOITime(record);

			for (ProjectsErrorPOICountModel capacityRolesId : capacityRolesIdList) {
				CapacityUserModel userModel = new CapacityUserModel();
				Integer userid = capacityRolesId.getUserid();
				String realName = capacityRolesId.getUsername();
				Integer roleid = capacityRolesId.getRoleid();
				String rolename = capacityRolesId.getRolename();
				userModel.setId(userid);
				userModel.setUserName(realName);
				userModel.setRealName(realName);
				userModel.setIdentity(rolename);
				for (ProjectsErrorPOICountModel capacityCount : capacityCountList) {
					Integer currUserID = capacityCount.getUserid();
					Integer curRoleID = capacityCount.getRoleid();
					Integer curCountA = capacityCount.getCountErrorA();
					Integer curCountB = capacityCount.getCountErrorB();
					Integer curCountC = capacityCount.getCountErrorC();
					Integer curCount = capacityCount.getCount();
					if ((currUserID.equals(userid)) && (curRoleID.equals(roleid))
							&& (roleid.equals(RoleType.ROLE_WORKER.getValue()) || roleid.equals(RoleType.ROLE_CHECKER.getValue()))) {
						userModel.getCapacityPOIErrorACountModel().setCount(curCountA);
						userModel.getCapacityPOIErrorBCountModel().setCount(curCountB);
						userModel.getCapacityPOIErrorCCountModel().setCount(curCountC);
						userModel.getCapacityPOIErrorCountModel().setCount(curCount);
					}
				}
				userList.add(userModel);
			}
			json.addObject("result", "1");
			json.addObject("resultList", userList);
			json.addObject("lastTime", lastTime);
		} catch (Exception e) {
			e.printStackTrace();
			json.addObject("result", "-1");
		}
		return json;
	}

	private int fillCapacityErrorFieldById(CapacityErrorTypeModel errorTypeModel, String currErrorFieldname, int currcount) {
		int ret = currcount;
		String key = currErrorFieldname.trim().toLowerCase();
		switch (key) {
		case "namec":// 中文正式名称 1
			errorTypeModel.getChineseNameFieldModel().setId(1);
			errorTypeModel.getChineseNameFieldModel().setCount(currcount);
			break;
		case "namee":// 英文正式名称 2
			errorTypeModel.getEnglishNameFieldModel().setId(2);
			errorTypeModel.getEnglishNameFieldModel().setCount(currcount);
			break;
		case "featcode":// 对象类型代码 3
			errorTypeModel.getObjTypeFieldModel().setId(3);
			errorTypeModel.getObjTypeFieldModel().setCount(currcount);
			break;
		case "featcode2":// 补充类别 4
			errorTypeModel.getAddTypeFieldModel().setId(4);
			errorTypeModel.getAddTypeFieldModel().setCount(currcount);
			break;
		case "sortcode":// 系列代码 5
			errorTypeModel.getSeriesCodeFieldModel().setId(5);
			errorTypeModel.getSeriesCodeFieldModel().setCount(currcount);
			break;
		case "tel":// 电话号码 6
			errorTypeModel.getTelFieldModel().setId(6);
			errorTypeModel.getTelFieldModel().setCount(currcount);
			break;
		case "grade":// 等级 7
			errorTypeModel.getLevelFieldModel().setId(7);
			errorTypeModel.getLevelFieldModel().setCount(currcount);
			break;
		case "ai":// 附加信息 8
			errorTypeModel.getAddInfoFieldModel().setId(8);
			errorTypeModel.getAddInfoFieldModel().setCount(currcount);
			break;
		case "address4":// 详细地址乡镇级 9
			errorTypeModel.getAddrTownFieldModel().setId(9);
			errorTypeModel.getAddrTownFieldModel().setCount(currcount);
			break;
		case "address5":// 详细地址村级、除行政区划外的其它区域 10
			errorTypeModel.getAddrAreaFieldModel().setId(10);
			errorTypeModel.getAddrAreaFieldModel().setCount(currcount);
			break;
		case "address6":// 详细地址大街路巷里 11
			errorTypeModel.getAddrStreetFieldModel().setId(11);
			errorTypeModel.getAddrStreetFieldModel().setCount(currcount);
			break;
		case "address7":// 详细地址号组 12
			errorTypeModel.getAddrNumFieldModel().setId(12);
			errorTypeModel.getAddrNumFieldModel().setCount(currcount);
			break;
		case "address8":// 详细地址其它 13
			errorTypeModel.getAddrOtherFieldModel().setId(13);
			errorTypeModel.getAddrOtherFieldModel().setCount(currcount);
			break;
		case "remark":// 备注 14
			errorTypeModel.getRemarkFieldModel().setId(14);
			errorTypeModel.getRemarkFieldModel().setCount(currcount);
			break;
		// case "namep":// 拼音名称 15
		// break;
		// case "address4p":// 详细地址拼音乡镇级 16
		// break;
		// case "address5p":// 详细地址拼音村级、除行政区划外的其它区域 17
		// break;
		// case "address6p":// 详细地址拼音大街路巷里 18
		// break;
		// case "address7p":// 详细地址拼音号组 19
		// break;
		// case "address8p":// 详细地址拼音其它 20
		// break;
		// case "names":// 简称 21
		// break;
		// case "":// 别名1 22
		// break;
		// case "":// 别名2 23
		// break;
		// case "":// POI状态 24
		// break;
		// case "":// 真位置 25
		// break;
		// case "":// 方位 26
		// break;
		// case "":// 引导位置 27
		// break;
		// case "":// 多照片缺失补丁 28
		default:
			ret = 0;
			logger.debug("fillCapacityErrorFieldById : " + currErrorFieldname);
			break;
		}
		return ret;
	}
}

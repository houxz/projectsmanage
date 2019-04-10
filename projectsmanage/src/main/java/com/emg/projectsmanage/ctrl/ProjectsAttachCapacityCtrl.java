package com.emg.projectsmanage.ctrl;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.common.IsWorkTimeEnum;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.common.ResultModel;
import com.emg.projectsmanage.common.TaskTypeEnum;
import com.emg.projectsmanage.dao.attach.AttachCapacityModelDao;
import com.emg.projectsmanage.dao.attach.AttachCheckCapacityModelDao;
import com.emg.projectsmanage.dao.attach.CycleModelDao;
import com.emg.projectsmanage.pojo.AttachCapacityModelExample;
import com.emg.projectsmanage.pojo.AttachCapacityModelExample.Criteria;
import com.emg.projectsmanage.pojo.AttachCheckCapacityModel;
import com.emg.projectsmanage.pojo.AttachMakeCapacityModel;

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
	
	private static final String[] excelColumnsMakeProperties = { "countdate", "username", "projectType", "directionCreate","directionUpdate","directionDelete","laneCreate","laneUpdate","laneDelete","junctionviewCreate","junctionviewUpdate","junctionviewDelete" , "worktime", "makeErrorCount", "efficiency"};
	private static final String[] excelColumnsCheckProperties = { "countdate", "username", "projectType", "lostDirection","makeMoreDirection","endRoadDirection","infoDirection","exitCodeDirection","exitDirection","unknownDirection","lostLane","makeMoreLane" , "turnLane", "endRoadLane","innerLinkLane","unknownLane","lostSceneJunctionview","lostPatternJunctionview","makeMoreSceneJunctionview","makeMorePatternJunctionview","pictureTypeSceneJunctionview","pictureTypePatternJunctionview","arrowSceneJunctionview","arrowPatternJunctionview","endRoadSceneJunctionview","endRoadPatternJunctionview","pictureChoiceSceneJunctionview","pictureChoicePatternJunctionview","unknownJunctionview","worktime", "checkCount", "efficiency"};
	private static final String[] excelColumnsMake = { "统计日期", "人员", "作业类型", "新增方向","修改方向","删除方向","新增车道","修改车道","删除车道","新增路口放大图","修改路口放大图","删除路口放大图", "工期", "错误量", "效率" };
	private static final String[] excelColumnsCheck = { "统计日期", "人员", "作业类型", "漏制作方向信息","多制作方向信息","结束路段错误","方向类型及名称（包括中文名称、拼音名称、英文名称）","出口编号错误","出口方向错误","其它未定义错误（方向信息）","漏制作车道信息","多制作车道信息","车道数/箭头方向/可调头漏制作错误","车道号/箭头数/终止道路错误","扩展内连接标识错误（lane信息）","其它未定义错误（lane信息）","漏制作路口放大图（实景图）","漏制作路口放大图（模式图）","多制作路口放大图（实景图）","多制作路口放大图（模式图）","图形种类错误（实景图）","图形种类错误（模式图）","箭头图编号错误（实景图）","箭头图编号错误（模式图）","结束道路错误（实景图）","结束道路错误（模式图）","图片选择错误（实景图）","图片选择错误（模式图）","其他未定义错误（路口放大图）" , "工期", "错误量",  "效率"};
	private static final Integer[] excelColumnMakeWidth = { 5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000,5000 };
	private static final Integer[] excelColumnCheckWidth = { 5000,5000,5000,5000,5000,5000,5000,10000,10000,10000,10000,10000,10000,10000,10000,10000,10000,5000,5000,5000,5000,5000,5000,5000,10000,10000,10000,10000,10000,10000,10000,10000 };
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

			List<AttachCheckCapacityModel> rows = attachCheckCapacityModelDao.selectcheckAttachCapacity(example);
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
	
	@RequestMapping(params = "atn=exportmake",method = RequestMethod.POST)
	public ModelAndView errorlistexport(HttpServletRequest request, HttpServletResponse response) throws Exception{
		logger.debug("ErrorListExportCtrl-getInfo start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		String startdate = ParamUtils.getParameter(request, "startdate", "");
		String enddate = ParamUtils.getParameter(request, "enddate", "");
		String flag = ParamUtils.getParameter(request, "flag", "");
		AttachCapacityModelExample example = new AttachCapacityModelExample();
		Criteria criteria = example.or();
		if(startdate != null && !startdate.isEmpty()) {
			criteria.andCountdateGreaterThanOrEqualTo(transferDate(startdate));
		}
		if (enddate != null && !enddate.isEmpty()) {
			criteria.andCountdateLessThanOrEqualTo(transferDate(enddate));
		}
		
		example.setOrderByClause("countdate desc");
		
		String[] properties = null;
		String[] columns = null;
		Integer[] width = null;
		List rows = null;
		if (flag.equalsIgnoreCase("make")) {
			properties = excelColumnsMakeProperties;
			columns = excelColumnsMake;
			rows = attachCapacityModelDao.selectAttachCapacity(example);
			width  = excelColumnMakeWidth;
		}else  {
			properties = excelColumnsCheckProperties;
			columns = excelColumnsCheck;
			rows = attachCheckCapacityModelDao.selectcheckAttachCapacity(example);
			width = excelColumnCheckWidth;
		}
		
		OutputStream out = null;
		HSSFWorkbook workBook = null;
		ResultModel result = new ResultModel();
		
		try {
			
			// 输出Excel
			logger.debug("START");
			if (!rows.isEmpty()) {
				workBook = new HSSFWorkbook();
				HSSFSheet sheet = workBook.createSheet();

				sheet.createFreezePane(0, 1, 0, 1);// 冻结第一行

				int rowNo = 0;
				// 第一行
				{
					Row row0 = sheet.createRow(rowNo++);
					HSSFCellStyle style0 = workBook.createCellStyle();
					HSSFFont font0 = workBook.createFont();
					font0.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
					style0.setFont(font0);
					style0.setAlignment(CellStyle.ALIGN_CENTER);// 居中
					style0.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 背景色
					style0.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 背景色
					style0.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
					style0.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
					style0.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
					style0.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框

					for (Integer i = 0; i < columns.length; i++) {
						sheet.setColumnWidth(i, width[i]);

						Cell cell0 = row0.createCell(i);
						cell0.setCellStyle(style0);
						cell0.setCellType(HSSFCell.CELL_TYPE_STRING);
						String column = columns[i];
						cell0.setCellValue(column);
					}
				}

				HSSFCellStyle styleC = workBook.createCellStyle();
				styleC.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
				styleC.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
				styleC.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
				styleC.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
				styleC.setAlignment(CellStyle.ALIGN_CENTER);

				for (Object make : rows) {
					if(flag.equalsIgnoreCase("make")) {
						make = (AttachMakeCapacityModel)make;
					}else {
						make = (AttachCheckCapacityModel)make;
					}
					Row row = sheet.createRow(rowNo++);
					Field[] fs = make.getClass().getDeclaredFields();
					for (Integer i = 0; i < properties.length; i++) {
						Cell cell = row.createCell(i);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						for (Field f : fs) {
							f.setAccessible(true);
							if (f.get(make) == null) {
								continue;
							}
							if (f.getName().equalsIgnoreCase(properties[i]) ) {
								
								if ( f.getName().equalsIgnoreCase("projectType") ) {
									if (f.getInt(make) == 1) {
										cell.setCellValue("自动匹配");
									}else {
										cell.setCellValue("更新作业");
									}
								} else {
									cell.setCellValue(f.get(make).toString());
								}
								
							} 
								
						}
					}
				}

				response.setContentType("application/vnd.ms-excel");
				String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls";
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
				response.setCharacterEncoding("UTF-8");
				out = response.getOutputStream();
				workBook.write(out);
				out.flush();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (workBook != null) {
				try {
					workBook.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		json.addAllObjects(result);
		logger.debug("ErrorListExportCtrl-getInfo end.");
		return json;
	}
	
	private Date transferDate(String str) throws ParseException {
		if (str == null || str.isEmpty()) return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(str);
	}
}

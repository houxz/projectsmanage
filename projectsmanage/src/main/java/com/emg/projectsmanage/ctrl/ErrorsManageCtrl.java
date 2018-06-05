package com.emg.projectsmanage.ctrl;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.dao.task.ErrorModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
import com.emg.projectsmanage.pojo.ErrorAndErrorRelatedModel;
import com.emg.projectsmanage.pojo.ErrorModel;
import com.emg.projectsmanage.pojo.ErrorSetModel;
import com.emg.projectsmanage.pojo.ItemConfigModel;
import com.emg.projectsmanage.pojo.ProcessConfigModel;

@Controller
@RequestMapping("/errorsmanage.web")
public class ErrorsManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(ErrorsManageCtrl.class);

	@Autowired
	private ProcessConfigModelDao processConfigModelDao;
	@Autowired
	private ConfigDBModelDao configDBModelDao;

	private ErrorModelDao errorModelDao = new ErrorModelDao();

	/**
	 * 系统配置页面
	 * 
	 * @param model
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping()
	public String openLader(Model model, HttpSession session, HttpServletRequest request) {
		logger.debug("ErrorsManageCtrl-openLader start.");
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(16);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			List<String> batchids = errorModelDao.getErrorBatchids(configDBModel);
			model.addAttribute("batchids", batchids);

			ProcessConfigModel _config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel _configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(_config.getDefaultValue()));
			List<ErrorSetModel> errorSets = errorModelDao.getErrorSets(_configDBModel);
			model.addAttribute("errorSets", errorSets);

			return "errorsmanage";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "redirect:login.jsp";
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("ErrorsManageCtrl-pages start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");

			Long batchID = ParamUtils.getLongParameter(request, "batchid", 0);
			if (batchID == null || batchID <= 0) {
				json.addObject("result", 0);
				json.addObject("option", "批次信息有误");
				return json;
			}
			Long errorSetID = ParamUtils.getLongParameter(request, "errorsetid", 0);

			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			List<Long> itemIDs = errorModelDao.getErrorSetDetailsByErrorSetID(configDBModel, errorSetID);
			List<Long> errortypes = new ArrayList<Long>();
			if (itemIDs != null && !itemIDs.isEmpty()) {
				List<ItemConfigModel> itemConfigs = errorModelDao.selectErrorTypesByIDs(configDBModel, itemIDs);
				for (ItemConfigModel itemConfig : itemConfigs) {
					errortypes.add(itemConfig.getErrortype());
				}
			}

			Map<String, Object> filterPara = null;
			ErrorModel record = new ErrorModel();
			record.setBatchid(batchID);

			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						record.setId(Long.valueOf(filterPara.get(key).toString()));
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}

			ProcessConfigModel _config = processConfigModelDao.selectByPrimaryKey(16);
			ConfigDBModel _configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(_config.getDefaultValue()));

			List<ErrorModel> rows = errorModelDao.selectErrors(_configDBModel, record, limit, offset, errortypes);
			Integer count = errorModelDao.countErrors(_configDBModel, record, errortypes);

			json.addObject("rows", rows);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("ErrorsManageCtrl-pages end.");
		return json;
	}

	@RequestMapping(params = "atn=exporterrors")
	public ModelAndView exportErrors(Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		logger.debug("ErrorsManageCtrl-exportErrors start.");
		Integer ret = -1;
		try {
			Long batchID = ParamUtils.getLongParameter(request, "batchid", 0);
			if (batchID == null || batchID == 0) {
				json.addObject("result", 0);
				json.addObject("option", "批次信息有误");
				return json;
			}
			Long errorSetID = ParamUtils.getLongParameter(request, "errorsetid", 0);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			List<Long> itemIDs = errorModelDao.getErrorSetDetailsByErrorSetID(configDBModel, errorSetID);
			List<Long> errortypes = new ArrayList<Long>();
			if (itemIDs != null && !itemIDs.isEmpty()) {
				List<ItemConfigModel> itemConfigs = errorModelDao.selectErrorTypesByIDs(configDBModel, itemIDs);
				for (ItemConfigModel itemConfig : itemConfigs) {
					errortypes.add(itemConfig.getErrortype());
				}
			}
			ErrorModel record = new ErrorModel();
			record.setBatchid(batchID);
			ProcessConfigModel _config = processConfigModelDao.selectByPrimaryKey(16);
			ConfigDBModel _configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(_config.getDefaultValue()));

			List<ErrorAndErrorRelatedModel> errorAndRelateds = errorModelDao.selectErrorAndErrorRelateds(_configDBModel, record, errortypes);
			if (errorAndRelateds != null && !errorAndRelateds.isEmpty()) {
				ProcessConfigModel __config = processConfigModelDao.selectByPrimaryKey(20);
				ConfigDBModel __configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(__config.getDefaultValue()));
				ret = errorModelDao.exportErrors(__configDBModel, errorAndRelateds);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		json.addObject("ret", ret);
		logger.debug("ErrorsManageCtrl-exportErrors end.");
		return json;
	}

	@RequestMapping(params = "atn=exporterrors2excel")
	public void exportErrors2Excel(Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		logger.debug("ErrorsManageCtrl-exportErrors2Excel start.");
		OutputStream out = null;
		HSSFWorkbook workBook = null;
		try {
			Long batchID = ParamUtils.getLongParameter(request, "batchid", 0);
			if (batchID == null || batchID == 0) {
				return;
			}
			Long errorSetID = ParamUtils.getLongParameter(request, "errorsetid", 0);
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			List<Long> itemIDs = errorModelDao.getErrorSetDetailsByErrorSetID(configDBModel, errorSetID);
			List<Long> errortypes = new ArrayList<Long>();
			if (itemIDs != null && !itemIDs.isEmpty()) {
				List<ItemConfigModel> itemConfigs = errorModelDao.selectErrorTypesByIDs(configDBModel, itemIDs);
				for (ItemConfigModel itemConfig : itemConfigs) {
					errortypes.add(itemConfig.getErrortype());
				}
			}
			ErrorModel record = new ErrorModel();
			record.setBatchid(batchID);
			ProcessConfigModel _config = processConfigModelDao.selectByPrimaryKey(16);
			ConfigDBModel _configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(_config.getDefaultValue()));
			List<ErrorModel> errors = errorModelDao.selectErrors(_configDBModel, record, -1, -1, errortypes);
			if (errors != null && !errors.isEmpty()) {
				workBook = new HSSFWorkbook();
				HSSFSheet sheet = workBook.createSheet();

				sheet.createFreezePane(0, 1, 0, 1);// 冻结第一行
				// 第一行的样式
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

				int rowNo = 0;
				Row row0 = sheet.createRow(rowNo++);
				Field[] fields = ErrorModel.class.getDeclaredFields();
				for (Integer i = 0; i < fields.length; i++) {
					String varName = fields[i].getName();
					Cell cell0 = row0.createCell(i);
					cell0.setCellStyle(style0);
					cell0.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell0.setCellValue(varName);
				}

				for (; rowNo <= errors.size(); rowNo++) {
					ErrorModel error = errors.get(rowNo - 1);
					Row row = sheet.createRow(rowNo);
					fields = error.getClass().getDeclaredFields();
					for (Integer columnNo = 0; columnNo < fields.length; columnNo++) {
						Field field = fields[columnNo];
						field.setAccessible(true);
						Cell cell = row.createCell(columnNo);
						String cellType = field.getType().getName();
						switch (cellType) {
						case "java.lang.Long":
						case "java.lang.Integer":
							cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							cell.setCellValue(Long.valueOf(field.get(error).toString()));
							break;
						case "java.lang.String":
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellValue(field.get(error).toString());
							break;
						default:
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellValue(field.get(error).toString());
							break;
						}
					}
				}

				response.setContentType("application/vnd.ms-excel");
				String fileName = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".xls";
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
					logger.error(e.getMessage(), e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

		logger.debug("ErrorsManageCtrl-exportErrors2Excel end.");
	}

}

package com.emg.poiwebeditor.ctrl;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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

import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.common.ProcessConfigEnum;
import com.emg.poiwebeditor.common.ProcessType;
import com.emg.poiwebeditor.common.ResultModel;
import com.emg.poiwebeditor.dao.process.ConfigDBModelDao;
import com.emg.poiwebeditor.dao.task.ErrorModelDao;
import com.emg.poiwebeditor.pojo.ConfigDBModel;
import com.emg.poiwebeditor.pojo.ErrorlistModel;
import com.emg.poiwebeditor.pojo.ProcessConfigModel;
import com.emg.poiwebeditor.service.ProcessConfigModelService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/errorlistexport.web")
public class ErrorlistExportCtrl extends BaseCtrl {
	private static final Logger logger = LoggerFactory.getLogger(WorkTasksCtrl.class);

	@Autowired
	private ProcessConfigModelService processConfigModelService;
	@Autowired
	private ConfigDBModelDao configDBModelDao;
	@Autowired
	private ErrorModelDao errorModelDao;

	private static final String[] excelColumns = { "批次ID", "QID", "错误类型", "错误备注", "更新时间", "数量" };
	private static final Integer[] excelColumnWidth = { 7000, 7000, 4000, 14000, 7000, 3000 };

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) { 
		logger.debug("ErrorlistExportCtrl-openLader start.");

		return "errorlistexport";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView pages(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("WorkTasksCtrl-pages start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			String filter = ParamUtils.getParameter(request, "filter", "");

			Map<String, Object> map = new HashMap<String, Object>();
			if (filter.length() > 0) {
				Map<String, Object> filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "batchid":
						map.put("batchid", filterPara.get(key).toString());
						break;
					case "qid":
						map.put("qid", filterPara.get(key).toString());
						break;
					case "errortype":
						map.put("errortype", filterPara.get(key).toString());
						break;
					case "errorremark":
						map.put("errorremark", "%" + filterPara.get(key).toString() + "%"); 
						break;
					case "updatetime1":
						map.put("updatetime1", filterPara.get(key).toString()); 
						break;
					case "updatetime2":
						map.put("updatetime2", filterPara.get(key).toString()); 
						break;
					default:
						break;
					}
				}
			}
			
			if (limit.compareTo(0) > 0)
				map.put("limit", limit);
			if (offset.compareTo(0) > 0)
				map.put("offset", offset);

			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.CUOWUKU,
					ProcessType.COUNTRY);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			HashSet<Long> selectbatchids = new HashSet<Long>();
			List<ErrorlistModel> errorList = new ArrayList<ErrorlistModel>();
			int count = 0;
			if(map.get("batchid") != null && !String.valueOf(map.get("batchid")).equals("")) {
				for (int i = 0; i < String.valueOf(map.get("batchid")).split(",").length; i++) {
					String batchid = String.valueOf(map.get("batchid")).split(",")[i];
					selectbatchids.add(Long.parseLong(batchid));
				}
				errorList = errorModelDao.selectErrorListInfos(configDBModel, selectbatchids, map);
				count = errorModelDao.selectCountErrorInfos(configDBModel, selectbatchids, map);
			}
			//add by lianhr begin 2019/03/05
			
			//add by lianhr end
			

			json.addObject("rows", errorList);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("WorkTasksCtrl-pages end.");
		return json;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView errorlistexport(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("ErrorListExportCtrl-getInfo start.");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		String strbatchids = ParamUtils.getParameter(request, "batchid", "");
		
		String strqid = ParamUtils.getParameter(request, "qid", "");
		String strerrortype = ParamUtils.getParameter(request, "errortype", "");
		String strerrorremark = ParamUtils.getParameter(request, "errorremark", "");
		String strupdatetime1 = ParamUtils.getParameter(request, "updatetime11", "");
		String strupdatetime2 = ParamUtils.getParameter(request, "updatetime22", "");
		OutputStream out = null;
		HSSFWorkbook workBook = null;
		ResultModel result = new ResultModel();
		if(strbatchids.equals("")) {
			json.addAllObjects(result);
			return json;
		}
		try {
			String[] batchids = strbatchids.split(",");
			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.CUOWUKU,
					ProcessType.COUNTRY);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			HashSet<Long> selectbatchids = new HashSet<Long>();
			for (int i = 0; i < batchids.length; i++) {
				String batchid = batchids[i];
				selectbatchids.add(Long.parseLong(batchid));
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("qid", strqid);
			map.put("errortype", strerrortype);
			map.put("errorremark", strerrorremark);
			map.put("updatetime1", strupdatetime1);
			map.put("updatetime2", strupdatetime2);
			List<ErrorlistModel> errorList = errorModelDao.selectErrorListInfos(configDBModel, selectbatchids, map);
			// 输出Excel
			logger.debug("START");
			if (!errorList.isEmpty()) {
				workBook = new HSSFWorkbook();
				HSSFSheet sheet = workBook.createSheet();

				sheet.createFreezePane(0, 1, 0, 1);// 冻结第一行

				int rowNo = 0;
				// 第一行
				{
					Row row0 = sheet.createRow(rowNo++);
					HSSFCellStyle style0 = workBook.createCellStyle();
					HSSFFont font0 = workBook.createFont();
					/*font0.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
					style0.setFont(font0);
					style0.setAlignment(CellStyle.ALIGN_CENTER);// 居中
					style0.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);// 背景色
					style0.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);// 背景色
					style0.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
					style0.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
					style0.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
					style0.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
*/
					for (Integer i = 0; i < excelColumns.length; i++) {
						sheet.setColumnWidth(i, excelColumnWidth[i]);

						Cell cell0 = row0.createCell(i);
						cell0.setCellStyle(style0);
						cell0.setCellType(HSSFCell.CELL_TYPE_STRING);
						String column = excelColumns[i];
						cell0.setCellValue(column);
					}
				}

				HSSFCellStyle styleC = workBook.createCellStyle();
				/*styleC.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
				styleC.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
				styleC.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
				styleC.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
				styleC.setAlignment(CellStyle.ALIGN_CENTER);*/

				for (ErrorlistModel poi : errorList) {
					Row row = sheet.createRow(rowNo++);

					Field[] fs = poi.getClass().getDeclaredFields();
					for (Integer i = 0; i < fs.length; i++) {
						Cell cell = row.createCell(i);

						Field f = fs[i];
						f.setAccessible(true);
						if (f.get(poi) == null) {
							continue;
						}
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(f.get(poi).toString());
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

		// result.put("itemInfos", itemInfos);
		json.addAllObjects(result);
		logger.debug("ErrorListExportCtrl-getInfo end.");
		return json;
	}
}

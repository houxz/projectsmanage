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

import org.apache.commons.dbcp.BasicDataSource;
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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.projectsmanage.common.DatabaseType;
import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.dao.process.ConfigDBModelDao;
import com.emg.projectsmanage.dao.process.ProcessConfigModelDao;
import com.emg.projectsmanage.pojo.ConfigDBModel;
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
			List<String> batchids = getErrorBatchids();
			model.addAttribute("batchids", batchids);

			List<ErrorSetModel> errorSets = getErrorSets();
			model.addAttribute("errorSets", errorSets);

			return "errorsmanage";
		} catch (Exception e) {
			e.printStackTrace();
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
			String _filter = ParamUtils.getParameter(request, "filter", "");
			String filter = new String(_filter.getBytes("iso-8859-1"), "utf-8");

			Long batchID = ParamUtils.getLongParameter(request, "batchid", 0);
			if (batchID == null || batchID == 0) {
				json.addObject("result", 0);
				json.addObject("option", "批次信息有误");
				return json;
			}
			Long errorSetID = ParamUtils.getLongParameter(request, "errorsetid", 0);
			List<Long> itemIDs = getErrorSetDetailsByErrorSetID(errorSetID);
			List<Long> errortypes = new ArrayList<Long>();
			if (itemIDs != null && !itemIDs.isEmpty()) {
				List<ItemConfigModel> itemConfigs = selectErrorTypesByIDs(itemIDs);
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
						logger.debug("未处理的筛选项：" + key);
						break;
					}
				}
			}

			List<ErrorModel> rows = selectErrors(record, limit, offset, errortypes);
			Integer count = countErrors(record, errortypes);

			json.addObject("rows", rows);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}

		logger.debug("ErrorsManageCtrl-pages end.");
		return json;
	}

	@RequestMapping(params = "atn=exporterrors")
	public void exportErrors(Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response) {
		logger.debug("ErrorsManageCtrl-exportErrors start.");
		OutputStream out = null;
		HSSFWorkbook workBook = null;
		try {
			Long batchID = ParamUtils.getLongParameter(request, "batchid", 0);
			if (batchID == null || batchID == 0) {
				return;
			}
			Long errorSetID = ParamUtils.getLongParameter(request, "errorsetid", 0);
			List<Long> itemIDs = getErrorSetDetailsByErrorSetID(errorSetID);
			List<Long> errortypes = new ArrayList<Long>();
			if (itemIDs != null && !itemIDs.isEmpty()) {
				List<ItemConfigModel> itemConfigs = selectErrorTypesByIDs(itemIDs);
				for (ItemConfigModel itemConfig : itemConfigs) {
					errortypes.add(itemConfig.getErrortype());
				}
			}
			ErrorModel record = new ErrorModel();
			record.setBatchid(batchID);
			List<ErrorModel> errors = selectErrors(record, -1, -1, errortypes);
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
			e.printStackTrace();
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

		logger.debug("ErrorsManageCtrl-exportErrors end.");
	}

	private BasicDataSource getDataSource(ConfigDBModel configDBModel) {
		BasicDataSource dataSource = new BasicDataSource();
		Integer dbtype = configDBModel.getDbtype();
		if (dbtype.equals(DatabaseType.MYSQL.getValue())) {
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		} else if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
			dataSource.setDriverClassName("org.postgresql.Driver");
		} else {
			return null;
		}
		dataSource.setUrl(getUrl(configDBModel));
		dataSource.setUsername(configDBModel.getUser());
		dataSource.setPassword(configDBModel.getPassword());
		return dataSource;
	}

	private String getUrl(ConfigDBModel configDBModel) {
		StringBuffer url = new StringBuffer();
		try {
			Integer dbtype = configDBModel.getDbtype();
			if (dbtype.equals(DatabaseType.MYSQL.getValue())) {
				url.append("jdbc:mysql://");
			} else if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				url.append("jdbc:postgresql://");
			} else {
				return null;
			}
			url.append(configDBModel.getIp());
			url.append(":");
			url.append(configDBModel.getPort());
			url.append("/");
			url.append(configDBModel.getDbname());
			url.append("?characterEncoding=UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return new String();
		}
		return url.toString();
	}

	private List<String> getErrorBatchids() {
		List<String> batchids = new ArrayList<String>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(16);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT DISTINCT batchid ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_error ");

			BasicDataSource dataSource = getDataSource(configDBModel);
			batchids = new JdbcTemplate(dataSource).queryForList(sql.toString(), String.class);
		} catch (Exception e) {
			e.printStackTrace();
			batchids = new ArrayList<String>();
		}
		return batchids;
	}

	private List<ErrorSetModel> getErrorSets() {
		List<ErrorSetModel> errorSets = new ArrayList<ErrorSetModel>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorset ");

			BasicDataSource dataSource = getDataSource(configDBModel);
			errorSets = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ErrorSetModel>(ErrorSetModel.class));

		} catch (Exception e) {
			e.printStackTrace();
			errorSets = new ArrayList<ErrorSetModel>();
		}
		return errorSets;
	}

	private List<Long> getErrorSetDetailsByErrorSetID(Long errorSetID) {
		List<Long> items = new ArrayList<Long>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT \"itemid\" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_errorsetdetail ");
			sql.append(" WHERE \"itemsetid\" = " + errorSetID);

			BasicDataSource dataSource = getDataSource(configDBModel);
			items = new JdbcTemplate(dataSource).queryForList(sql.toString(), Long.class);
		} catch (Exception e) {
			items = new ArrayList<Long>();
		}
		return items;
	}

	private List<ItemConfigModel> selectErrorTypesByIDs(List<Long> itemIDs) {
		List<ItemConfigModel> itemConfigs = new ArrayList<ItemConfigModel>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(2);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_itemconfig ");
			sql.append(" WHERE \"id\" IN ( ");
			for (Long itemID : itemIDs) {
				sql.append(itemID + ",");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");

			BasicDataSource dataSource = getDataSource(configDBModel);
			itemConfigs = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ItemConfigModel>(ItemConfigModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			itemConfigs = new ArrayList<ItemConfigModel>();
		}
		return itemConfigs;
	}

	private List<ErrorModel> selectErrors(ErrorModel record, Integer limit, Integer offset, List<Long> errortypes) {
		List<ErrorModel> errors = new ArrayList<ErrorModel>();
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(16);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_error ");
			sql.append(" WHERE 1=1 ");
			sql.append(" AND batchid =  " + record.getBatchid());
			if (errortypes != null && !errortypes.isEmpty()) {
				sql.append(" AND errortype IN ( ");
				for (Long errortype : errortypes) {
					sql.append(errortype + ",");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(" ) ");
			}
			sql.append(" ORDER BY id ");
			if (limit.compareTo(0) > 0) {
				sql.append(" LIMIT " + limit);
			}
			if (offset.compareTo(0) > 0) {
				sql.append(" OFFSET " + offset);
			}

			BasicDataSource dataSource = getDataSource(configDBModel);
			errors = new JdbcTemplate(dataSource).query(sql.toString(), new BeanPropertyRowMapper<ErrorModel>(ErrorModel.class));
		} catch (Exception e) {
			e.printStackTrace();
			errors = new ArrayList<ErrorModel>();
		}
		return errors;
	}

	private Integer countErrors(ErrorModel record, List<Long> errortypes) {
		Integer ret = -1;
		try {
			ProcessConfigModel config = processConfigModelDao.selectByPrimaryKey(16);
			ConfigDBModel configDBModel = configDBModelDao.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));
			Integer dbtype = configDBModel.getDbtype();

			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT COUNT(1) ");
			sql.append(" FROM ");
			if (dbtype.equals(DatabaseType.POSTGRESQL.getValue())) {
				sql.append(configDBModel.getDbschema()).append(".");
			}
			sql.append("tb_error ");
			sql.append(" WHERE 1=1 ");
			sql.append(" AND batchid =  " + record.getBatchid());
			if (errortypes != null && !errortypes.isEmpty()) {
				sql.append(" AND errortype IN ( ");
				for (Long errortype : errortypes) {
					sql.append(errortype + ",");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(" ) ");
			}

			BasicDataSource dataSource = getDataSource(configDBModel);
			ret = new JdbcTemplate(dataSource).queryForObject(sql.toString(), null, Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
			ret = -1;
		}
		return ret;
	}

}
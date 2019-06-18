package com.emg.poiwebeditor.ctrl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.common.ProcessConfigEnum;
import com.emg.poiwebeditor.common.ProcessType;
import com.emg.poiwebeditor.dao.process.ConfigDBModelDao;
import com.emg.poiwebeditor.dao.task.DatasetModelDao;
import com.emg.poiwebeditor.pojo.ConfigDBModel;
import com.emg.poiwebeditor.pojo.DatasetModel;
import com.emg.poiwebeditor.pojo.EmployeeModel;
import com.emg.poiwebeditor.pojo.KeywordModel;
import com.emg.poiwebeditor.pojo.ProcessConfigModel;
import com.emg.poiwebeditor.service.EmapgoAccountService;
import com.emg.poiwebeditor.service.ProcessConfigModelService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/fielddatamanage.web")
public class FieldDataManageCtrl extends BaseCtrl {

	private static final Logger logger = LoggerFactory.getLogger(FieldDataManageCtrl.class);

	@Autowired
	private ProcessConfigModelService processConfigModelService;

	@Autowired
	private ConfigDBModelDao configDBModelDao;

	@Autowired
	EmapgoAccountService emapgoAccountService;

	//private DatasetModelDao datasetModelDao = new DatasetModelDao();
	@Autowired
	private DatasetModelDao datasetModelDao;
	
	private Integer    uploadcount = 0;
	private Integer    uploadtotall =  100;

	@RequestMapping(method = RequestMethod.GET)
	public String openLander() {
		logger.debug("FieldDataManageCtrl::openLander()");

		return "fielddatamanage";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(params = "atn=pages")
	public ModelAndView getFieldDataInfoList(Model model, HttpServletRequest request, HttpSession session) {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		uploadcount = 0;
		uploadtotall =100;
		try {
			Integer limit = ParamUtils.getIntParameter(request, "limit", 10);
			Integer offset = ParamUtils.getIntParameter(request, "offset", 0);
			// {"name":"四方检索数据"}
			Map<String, Object> filterPara = null;
			String filter = ParamUtils.getParameter(request, "filter", "");

			// 用于过滤
			DatasetModel record = new DatasetModel();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						record.setId(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "name":
						record.setName(filterPara.get(key).toString());
						break;
					case "states":
						String v = filterPara.get(key).toString();
						if (v.equals("上传中")) {
							record.setState(1);
							record.setProcess(1);
						} else if (v.equals("上传异常")) {
							record.setState(2);
							record.setProcess(1);
						} else if (v.equals("未处理")) {
							record.setState(3);
							record.setProcess(1);
						} else if (v.equals("处理中")) {
							record.setState(1);
							record.setProcess(2);
						} else if (v.equals("处理异常")) {
							record.setState(2);
							record.setProcess(2);
						} else if (v.equals("处理完成")) {
							record.setState(3);
							record.setProcess(2);
						} else if (v.equals("创建任务异常")) {
							record.setState(2);
							record.setProcess(3);
						} else if (v.equals("创建任务完成")) {
							record.setState(3);
							record.setProcess(3);
						}
						break;
					}
				}
			}

			ProcessType processType = ProcessType.POIPOLYMERIZE;
			ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZILIAOKU,
					processType);
			ConfigDBModel configDBModel = configDBModelDao
					.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

			List<Integer> datatypelist = new ArrayList<Integer>();
			datatypelist.add(36);
			datatypelist.add(37);// 手动上传的资料类型

			List<DatasetModel> list = datasetModelDao.selectDatasets(configDBModel, datatypelist, record, limit,
					offset);
			int count = datasetModelDao.countErrorSets(configDBModel, datatypelist, record, limit, offset);

			json.addObject("rows", list);
			json.addObject("total", count);
			json.addObject("result", 1);

		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}

		return json;
	}

	@RequestMapping(params = "atn=springUpload")
	public ModelAndView springUpload(HttpServletRequest request, HttpSession session)
			throws IllegalStateException, IOException {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());

		uploadtotall =100;
		uploadcount = 1;
		String account = getLoginAccount(session);
		EmployeeModel record = new EmployeeModel();
		record.setUsername(account);
		Integer userid = 0;
		String realname = "";

		EmployeeModel user = emapgoAccountService.getOneEmployeeWithCache(record);
		if (user != null) {
			userid = user.getId();
			realname = user.getRealname();
		}

		long startTime = System.currentTimeMillis();
		// 将当前上下文初始化给 commonsmutipartresolver 多部分解器
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 检查form中是否有enctype='multipart/form-data'
		if (multipartResolver.isMultipart(request)) {
			// 将request变成多部分request
			try {
				MultipartHttpServletRequest multiRequest = multipartResolver.resolveMultipart(request);

				Boolean ischeck = CheckField(multiRequest ,json);
				if( ischeck == false) {
					uploadcount = 0;
					json.addObject("result",0);
					return json;
				}
				
				ProcessType processType = ProcessType.POIPOLYMERIZE;
				ProcessConfigModel config = processConfigModelService.selectByPrimaryKey(ProcessConfigEnum.ZILIAOKU,
						processType);
				ConfigDBModel configDBModel = configDBModelDao
						.selectByPrimaryKey(Integer.valueOf(config.getDefaultValue()));

				Boolean bdatasetok = false;
				// 获取multiRequest中所有的文件名
				Iterator<String> iter = multiRequest.getFileNames();
				Long batchid = 0L;
				if (iter.hasNext()) {
					// 申请批次号
					// 工具识别码 SVN号 400
					// code
					batchid = datasetModelDao.getBatchid();
				}
				Long bid = datasetModelDao.InsertBatch(configDBModel, batchid, userid, realname);
				if (bid > 0) {
					while (iter.hasNext()) {
						// 一次遍历所有文件
						MultipartFile file = multiRequest.getFile(iter.next().toString());
						if (file != null) {
							InputStream  input = file.getInputStream();
							DatasetModel dataset = new DatasetModel();
							String filename = file.getOriginalFilename();
							int indexc = filename.indexOf(".");
							String name = filename.substring(0, indexc);
							String ext = filename.substring(indexc+1);
							if( ext.compareToIgnoreCase("xls") !=0 ) {
								uploadcount = 0;
								json.addObject("err","只支持xls格式");
								json.addObject("result", 0);
								return json;
							}
								
							dataset.setName(name);
							dataset.setPath(file.getOriginalFilename());// 浏览器做了安全设置,js页无法获取上传路径
							// 一个文件一个dataset
							dataset.setDatatype(37);
							dataset.setState(1);
							dataset.setProcess(1);// 上传中
							dataset.setBatchid(batchid.toString());
							// 更新资料状态 任务创建完成
							Long datasetid = datasetModelDao.InsertDataset(configDBModel);
							boolean b = datasetid.equals(-1L);
							if (datasetid.equals(-1L))
								continue;
							dataset.setId(datasetid);
							dataset.setUsername(realname);
							dataset.setRoleid(userid);
							Double xmin = 1000d;
							Double ymin = 1000d;
							Double xmax = 0d;
							Double ymax = 0d;
							Boolean bInsertError = false;// 插入数据库过程是否有问题
							List<KeywordModel> kmodellist = new ArrayList<KeywordModel>();
							HSSFWorkbook workbook = new HSSFWorkbook(input);
							int sheetnum = workbook.getNumberOfSheets();
							for (int index = 0; index < sheetnum; index++) {
								HSSFSheet sheet = workbook.getSheetAt(index);
								int firstrowindex = sheet.getFirstRowNum();// 默认第一行为表头
								int lastrowindex = sheet.getLastRowNum();// 从第二行开始为正文数据
								if (lastrowindex <= 0)
									continue;
								uploadtotall = lastrowindex ; 
								HSSFRow fields = sheet.getRow(0);
								for (int indexrow = 1; indexrow <= lastrowindex; indexrow++) {
									uploadcount = indexrow;
									HSSFRow row = sheet.getRow(indexrow);
									KeywordModel kmodel = changerowtoKeywordModel(row, fields);
									if (kmodel != null && kmodel.getId() != null) {
										kmodel.setDatasetId(datasetid);
										kmodel.setSrcType(110);// 预定义都写这个值
										
										Boolean bInsert = datasetModelDao.Insertkeyword(configDBModel, kmodel);
										if (!bInsert)
											bInsertError = true;
										kmodellist.add(kmodel);
										
										if(kmodel.getGeo() != null) {
											//计算整个dataset的envelope
											String sgeo = kmodel.getGeo();
											int indexzk = sgeo.indexOf('(');
											int indexyk = sgeo.indexOf(')');
											int indexkg = sgeo.indexOf(' ');
											String sx = sgeo.substring(indexzk+1, indexkg);
											String sy = sgeo.substring(indexkg,indexyk);
											Double x = Double.parseDouble(sx);
											Double y = Double.parseDouble(sy);
											if( x < xmin)
												xmin = x;
											if( x > xmax)
												xmax = x;
											if( y<ymin)
												ymin = y;
											if( y>ymax)
												ymax=y;
										}
										
									}
								}
							}

							dataset.setRecordcount(kmodellist.size());
							if (bInsertError) {
								dataset.setState(2);
								dataset.setProcess(1);// 上传异常
							} else {
								dataset.setState(3);
								dataset.setProcess(1);// 上传完成
							}
								
							//设置成默认值
							dataset.setReason(0);
							dataset.setMode(0);
							dataset.setArea_code(0);
							dataset.setCity_code(0);
							dataset.setDatasource(110);//预定义的
							if( xmax>0 && ymax>0) {
								//POLYGON((132.972469618056 49.1760601128472,132.972469618056 18.2250998263889,82.0648600260417 18.2250998263889,82.0648600260417 49.1760601128472,132.972469618056 49.1760601128472))
								StringBuilder envelope = new StringBuilder();
								envelope.append("POLYGON((");
								envelope.append(xmin.toString() +" " + ymin.toString() + ",");
								envelope.append(xmax.toString() +" " + ymin.toString() + ",");
								envelope.append(xmax.toString() +" " + ymax.toString() + ",");
								envelope.append(xmin.toString() +" " + ymax.toString() + ",");
								envelope.append(xmin.toString() +" " + ymin.toString());
								envelope.append("))");
								
								
								dataset.setEnvelope(envelope);
							}
							Boolean bupdate = datasetModelDao.updateDataset(configDBModel, dataset);

						}
					} //
					Boolean bbatchok = datasetModelDao.updateBatch(configDBModel, bid);
					if (bbatchok)
						json.addObject("result", 1);
					else
						json.addObject("result", 0);
				} // if( bid >0)
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
			}
		}
		long endTime = System.currentTimeMillis();
		logger.debug("上传文件用时:" + String.valueOf(endTime - startTime) + "ms");
		return json;
	}

	@RequestMapping(params = "atn=getprogress")
	public ModelAndView af(HttpServletRequest request, HttpSession session) {
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		json.addObject("result", 1);
		json.addObject("count",uploadcount);
		json.addObject("totall",uploadtotall);
		return json;
	}
	
	/* 检查表头是否有出入
	 * 将
	 */
	public Boolean CheckField(MultipartHttpServletRequest multiRequest , ModelAndView json) {
		Iterator<String> iter = multiRequest.getFileNames();
		try {
		while (iter.hasNext()) {
		// 一次遍历所有文件
		MultipartFile file = multiRequest.getFile(iter.next().toString());
		if (file != null) {
			InputStream  input = file.getInputStream();
			String filename = file.getOriginalFilename();
			int indexc = filename.indexOf(".");
			String name = filename.substring(0, indexc);
			String ext = filename.substring(indexc+1);
			if( ext.compareToIgnoreCase("xls") !=0 ) {
				json.addObject("err","只支持xls格式");
				return false;
			}			
			HSSFWorkbook workbook = new HSSFWorkbook(input);
			int sheetnum = workbook.getNumberOfSheets();
			for (int index = 0; index < sheetnum; index++) {
				HSSFSheet sheet = workbook.getSheetAt(index);
				HSSFRow fields = sheet.getRow(0);
				if (fields == null)
					continue;
				int firstcelindex = fields.getFirstCellNum();
				int lastcelindex = fields.getLastCellNum();

				for (int indexcel = firstcelindex; indexcel < lastcelindex; indexcel++) {
					HSSFCell field = fields.getCell(indexcel);
					String strfieldname = field.getStringCellValue();

					if (strfieldname.equals("序号")) {
					} else if (strfieldname.equals("省份")) {
					} else if (strfieldname.equals("城市")) {
					} else if (strfieldname.equals("区县")) {
					} else if (strfieldname.equals("名称")) {
					} else if (strfieldname.equals("地址")) {
					} else if (strfieldname.equals("电话")) {
					} else if (strfieldname.equals("相关描述")) {
					} else if (strfieldname.equals("POI类别")) {
					} else if (strfieldname.equals("POI系列")) {
					} else if (strfieldname.equals("经纬度")) {
					} else if (strfieldname.equals("关联照片")) {
					} else if (strfieldname.equals("数据来源")) {
					} else if (strfieldname.equals("数据源类型")) {
					} else if (strfieldname.equals("数据源ID")) {
					} else if (strfieldname.equals("备注")) {
					} else if (strfieldname.equals("datasetid")) {
					} else if (strfieldname.equals("检索方式")) {
					} else if (strfieldname.equals("周边检索距离")) {
					}else {
						json.addObject("err","出现定义外表字段");
						return false;
					}
				}
						
			}
		}//if (file != null)
		}//while (iter.hasNext()) {
		}catch(Exception e) {
			return false;
		}
		
		return true;
	}
	
	/*
	 * 将excel一行转换成一个model
	 */
	public KeywordModel changerowtoKeywordModel(HSSFRow row, HSSFRow fields) {
		if (row == null)
			return null;
		KeywordModel kmodel = new KeywordModel();

		int firstcelindex = row.getFirstCellNum();
		int lastcelindex = row.getLastCellNum();

		for (int indexcel = firstcelindex; indexcel <= lastcelindex; indexcel++) {
			HSSFCell field = fields.getCell(indexcel);
			if(field==null)
				continue;
			String strfieldname = field.getStringCellValue();

			HSSFCell cell = row.getCell(indexcel);
			if (cell == null)// 单元格没有内容
				continue;
			double dvalue = 0;
			String svalue = "";
			int ictype = cell.getCellType();
			if( ictype ==HSSFCell.CELL_TYPE_STRING ) {
				svalue = cell.getStringCellValue();
		
			}else if(ictype == HSSFCell.CELL_TYPE_NUMERIC) {
				dvalue = cell.getNumericCellValue();
				svalue = String.valueOf(dvalue);
			}else {
				continue;
			}
			
			if (strfieldname.equals("序号")) {
				Long l1 = Double.valueOf(svalue).longValue();
				kmodel.setId(l1);// 默认必须有
			} else if (strfieldname.equals("省份")) {
				kmodel.setProvince(svalue);
			} else if (strfieldname.equals("城市")) {
				kmodel.setCity(svalue);
			} else if (strfieldname.equals("区县")) {
				kmodel.setDistrict(svalue);
			} else if (strfieldname.equals("名称")) {
				kmodel.setName(svalue);
			} else if (strfieldname.equals("地址")) {
				kmodel.setAddress(svalue);
			} else if (strfieldname.equals("电话")) {
				kmodel.setTelephone(svalue);
			} else if (strfieldname.equals("相关描述")) {
				kmodel.setDesc(svalue);
			} else if (strfieldname.equals("POI类别")) {
				kmodel.setPoiType(svalue);
			} else if (strfieldname.equals("POI系列")) {

			} else if (strfieldname.equals("经纬度")) {
				//POINT(114.448479817708 36.6204405381944)
				if( svalue != null) {
					String sgeo = svalue.replace(',', ' ');
					kmodel.setGeo("POINT("+sgeo+")");
				}
				
			} else if (strfieldname.equals("关联照片")) {

			} else if (strfieldname.equals("数据来源")) {

			} else if (strfieldname.equals("数据源类型")) {
				//20190611需求要求写死110 不读此字段了  kmodel.setSrcType(Integer.valueOf(svalue));
			} else if (strfieldname.equals("数据源ID")) {
				kmodel.setSrcInnerId(svalue);
			} else if (strfieldname.equals("备注")) {
				kmodel.setRemark(svalue);
			} else if (strfieldname.equals("datasetid")) {
				// 154.0
				kmodel.setDatasetId(Double.valueOf(svalue).longValue());// 这个值不应该填写吧？
			} else if (strfieldname.equals("检索方式")) {
				// 0.0
				kmodel.setQueryType(Double.valueOf(svalue).intValue());
			} else if (strfieldname.equals("周边检索距离")) {
				kmodel.setDistance(Double.valueOf(svalue).intValue());
			}

		}

		return kmodel;
	}
}

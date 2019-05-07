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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.emg.poiwebeditor.common.ItemSetEnable;
import com.emg.poiwebeditor.common.ItemSetSysType;
import com.emg.poiwebeditor.common.ItemSetType;
import com.emg.poiwebeditor.common.ItemSetUnit;
import com.emg.poiwebeditor.common.LayerElement;
import com.emg.poiwebeditor.common.ParamUtils;
import com.emg.poiwebeditor.dao.pepro.qctask.ItemInfoModelDao;
import com.emg.poiwebeditor.pojo.qctask.ItemInfoModel;
import com.emg.poiwebeditor.pojo.qctask.ItemInfoModelExample;
import com.emg.poiwebeditor.pojo.qctask.ItemInfoModelExample.Criteria;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/iteminfo.web")
public class ItemInfoCtrl extends BaseCtrl {

	@Autowired
	ItemInfoModelDao itemInfoModelDao;

	private static final Logger logger = LoggerFactory.getLogger(ItemInfoCtrl.class);

	@RequestMapping()
	public String openLader(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("START");

		model.addAttribute("itemsetEnables", ItemSetEnable.toJsonStr());
		model.addAttribute("itemsetSysTypes", ItemSetSysType.toJsonStr());
		model.addAttribute("itemsetTypes", ItemSetType.toJsonStr());
		model.addAttribute("itemsetUnits", ItemSetUnit.toJsonStr());
		model.addAttribute("layerElements", LayerElement.layers());

		return "iteminfomanage";
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
			ItemInfoModelExample example = new ItemInfoModelExample();
			Criteria criteria = example.or();
			if (filter.length() > 0) {
				filterPara = (Map<String, Object>) JSONObject.fromObject(filter);
				for (String key : filterPara.keySet()) {
					switch (key) {
					case "id":
						criteria.andIdEqualTo(Long.valueOf(filterPara.get(key).toString()));
						break;
					case "name":
						criteria.andNameLike(
								String.format("%s%s%s", new String("%"), filterPara.get(key), new String("%")));
						break;
					case "layername":
						criteria.andLayernameLike(
								String.format("%s%s%s", new String("%"), filterPara.get(key), new String("%")));
						break;
					case "enable":
						criteria.andEnableEqualTo(Short.valueOf(filterPara.get(key).toString()));
						break;
					case "unit":
						criteria.andUnitEqualTo(Short.valueOf(filterPara.get(key).toString()));
						break;
					case "type":
						criteria.andTypeEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "systype":
						criteria.andSystypeEqualTo(Integer.valueOf(filterPara.get(key).toString()));
						break;
					case "referdata":
						criteria.andReferdataLike(
								String.format("%s%s%s", new String("%"), filterPara.get(key), new String("%")));
						break;
					case "module":
						criteria.andModuleLike(
								String.format("%s%s%s", new String("%"), filterPara.get(key), new String("%")));
						break;
					default:
						logger.error("未处理的筛选项：" + key);
						break;
					}
				}
			}

			example.setLimit(limit);
			example.setOffset(offset);
			List<ItemInfoModel> rows = itemInfoModelDao.selectByExample(example);
			Long count = itemInfoModelDao.countByExample(example);

			json.addObject("rows", rows);
			json.addObject("total", count);
			json.addObject("result", 1);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("END");
		return json;
	}

	@RequestMapping(params = "atn=submititeminfo", method = RequestMethod.POST)
	public ModelAndView submitItemInfo(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "id", required = true) Long id,
			@RequestParam(value = "oid", required = true) String oid,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "layername", required = false, defaultValue = "") String layername,
			@RequestParam(value = "enable", required = false, defaultValue = "1") Short enable,
			@RequestParam(value = "unit", required = false, defaultValue = "0") Short unit,
			@RequestParam(value = "type", required = false, defaultValue = "0") Integer type,
			@RequestParam(value = "systype", required = false, defaultValue = "86") Integer systype,
			@RequestParam(value = "referdata", required = false, defaultValue = "") String referdata,
			@RequestParam(value = "module", required = false, defaultValue = "") String module) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			if (id.compareTo(0L) > 0) {
				ItemInfoModel itemInfo = new ItemInfoModel();
				itemInfo.setId(id);
				itemInfo.setOid(oid);
				itemInfo.setName(name);
				itemInfo.setLayername(layername);
				itemInfo.setEnable(enable);
				itemInfo.setUnit(unit);
				itemInfo.setType(type);
				itemInfo.setSystype(systype);
				itemInfo.setReferdata(referdata);
				itemInfo.setModule(module);

				Integer ret = itemInfoModelDao.updateByPrimaryKeySelective(itemInfo);
				if (ret.compareTo(0) > 0) {
					json.addObject("result", true);
				} else {
					json.addObject("result", false);
				}
			} else {
				ItemInfoModel itemInfo = new ItemInfoModel();
				itemInfo.setOid(oid);
				itemInfo.setName(name);
				itemInfo.setLayername(layername);
				itemInfo.setEnable(enable);
				itemInfo.setUnit(unit);
				itemInfo.setType(type);
				itemInfo.setSystype(systype);
				itemInfo.setReferdata(referdata);
				itemInfo.setModule(module);

				Integer ret = itemInfoModelDao.insertSelective(itemInfo);
				if (ret.compareTo(0) > 0) {
					json.addObject("result", true);
				} else {
					json.addObject("result", false);
				}
			}
		} catch (Exception e) {
			json.addObject("result", false);
			logger.error(e.getMessage(), e);
		}
		logger.debug("END");
		return json;
	}

	@RequestMapping(params = "atn=getiteminfo", method = RequestMethod.POST)
	public ModelAndView getItemInfo(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "id", required = true) Long id) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			ItemInfoModel itemInfo = itemInfoModelDao.selectByPrimaryKey(id);
			json.addObject("iteminfo", itemInfo);
			json.addObject("result", 1);
		} catch (Exception e) {
			json.addObject("result", 0);
			logger.error(e.getMessage(), e);
		}
		logger.debug("END");
		return json;
	}

	@RequestMapping(params = "atn=deleteiteminfo", method = RequestMethod.POST)
	public ModelAndView deleteItemInfo(Model model, HttpServletRequest request, HttpSession session,
			@RequestParam(value = "id", required = true) Long id) {
		logger.debug("START");
		ModelAndView json = new ModelAndView(new MappingJackson2JsonView());
		try {
			Integer ret = itemInfoModelDao.deleteByPrimaryKey(id);
			if (ret.compareTo(0) > 0) {
				json.addObject("result", true);
			} else {
				json.addObject("result", false);
			}
		} catch (Exception e) {
			json.addObject("result", false);
			logger.error(e.getMessage(), e);
		}
		logger.debug("END");
		return json;
	}
}

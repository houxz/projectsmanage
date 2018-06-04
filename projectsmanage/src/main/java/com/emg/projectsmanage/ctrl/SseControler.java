package com.emg.projectsmanage.ctrl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.emg.projectsmanage.common.ParamUtils;
import com.emg.projectsmanage.dao.process.ProcessModelDao;
import com.emg.projectsmanage.pojo.ProcessModel;
import com.emg.projectsmanage.pojo.ProcessModelExample;
import com.emg.projectsmanage.pojo.ProcessModelExample.Criteria;

@Controller
public class SseControler {
	private static final Logger logger = LoggerFactory.getLogger(SseControler.class);
	
	@Autowired
	private ProcessModelDao processModelDao;

	@RequestMapping(value = "/sse.web", params = "action=refreshprogress", produces = "text/event-stream;charset=utf-8")
	public @ResponseBody
	String refreshProgress(Model model, HttpServletRequest request, HttpSession session) {
		logger.debug("SseControler-refreshProgress start.");
		String ret = new String();
		try {
			String ids = ParamUtils.getParameter(request, "ids");
			if(ids != null && !ids.isEmpty()) {
				ProcessModelExample example = new ProcessModelExample();
				Criteria criteria = example.or();
				List<Long> values = new ArrayList<Long>();
				for(String id : ids.split(",")) {
					values.add(Long.valueOf(id));
				}
				criteria.andIdIn(values );
				List<ProcessModel> processModels = processModelDao.selectByExample(example);
				if(processModels.size() > 0) {
					JSONArray array = new JSONArray();
			        for(ProcessModel processModel : processModels) {
			        	JSONObject jsonObject = new JSONObject();
			        	Long id = processModel.getId();
			        	String progress = processModel.getProgress();
			        	jsonObject.put("id", id);
			        	jsonObject.put("progress", progress);
			        	array.add(jsonObject);
			        }
			        ret = array.toString();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			ret = new String();
		}
		logger.debug("SseControler-refreshProgress end.");
		return "data:" + ret + "\n\n";
	}
}

package com.emg.projectsmanage.ctrl;

import java.io.BufferedOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import me.chyxion.xls.TableToXls;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.emg.projectsmanage.common.ParamUtils;

@Controller
@RequestMapping("/downloadexcel.web")
public class DownloadExcelCtrl {

	@RequestMapping
	public void download(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("application/x-excel");
		request.setCharacterEncoding("UTF-8");
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String fileName = sf.format(new Date()) + ".xls";
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.setCharacterEncoding("UTF-8");
		BufferedOutputStream out = null;
		try {
			String html = ParamUtils.getParameter(request, "tablehtml");
			out = new BufferedOutputStream(response.getOutputStream());
			TableToXls.process(html, out);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
}

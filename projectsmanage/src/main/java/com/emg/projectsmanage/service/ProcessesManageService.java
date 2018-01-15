package com.emg.projectsmanage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emg.projectsmanage.dao.process.ProcessModelDao;

@Service
public class ProcessesManageService {

	@Autowired
	private ProcessModelDao processModelDao;

}

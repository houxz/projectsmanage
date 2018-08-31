package com.emg.projectsmanage.dao.emapgoaccount;

import com.emg.projectsmanage.pojo.AuthorityModel;

public interface AuthorityModelDao {
	
	AuthorityModel getAuthorityByUsername(String username);
	
}
package com.emg.poiwebeditor.dao.emapgoaccount;

import com.emg.poiwebeditor.pojo.AuthorityModel;

public interface AuthorityModelDao {
	
	AuthorityModel getAuthorityByUsername(String username);
	
}
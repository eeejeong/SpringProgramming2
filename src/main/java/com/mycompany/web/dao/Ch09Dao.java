package com.mycompany.web.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Ch09Dao {
private static final Logger logger = LoggerFactory.getLogger(Ch09Dao.class);
	
	public Ch09Dao() {
		logger.debug("Ch09Dao 객체가 만들어졌다!");
	}
	
	public void insert() {
		logger.debug("실행");
	}

}

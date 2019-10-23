package com.mycompany.web.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Ch09Dao2 {
private static final Logger logger = LoggerFactory.getLogger(Ch09Dao2.class);
	
	public Ch09Dao2() {
		logger.debug("Ch09Dao2 객체가 만들어졌다!");
	}
	
	public void insert2() {
		logger.debug("실행");
	}

}

package com.mycompany.web.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// 스프링은 @Component가 붙어있는 클래스는 전부 관리 객체로 생성 
@Component("ch09Dao3")
public class Ch09Dao3 {
private static final Logger logger = LoggerFactory.getLogger(Ch09Dao3.class);
	
	public Ch09Dao3() {
		logger.debug("Ch09Dao3 객체가 만들어졌다!");
	}
	
	public void insert3() {
		logger.debug("실행");
	}

}

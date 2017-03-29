package com.elementwin.bs.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.bs.service.PresaleActivityService;
import com.elementwin.bs.service.mapper.PresaleActivityMapper;

/***
 * 销售政策相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-23
 * Copyright 2016 www.Dibo.ltd
 */
@Service("presaleActivityService")
@Transactional
public class PresaleActivityServiceImpl extends BaseServiceImpl implements PresaleActivityService{
	private static Logger logger = Logger.getLogger(PresaleActivityServiceImpl.class);
	
	@Autowired
	PresaleActivityMapper presaleActivityMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return presaleActivityMapper;
	}
}
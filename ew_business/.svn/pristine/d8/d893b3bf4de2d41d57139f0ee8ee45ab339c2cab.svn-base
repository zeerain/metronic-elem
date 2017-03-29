package com.elementwin.bs.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elementwin.bs.service.PresaleImportDataService;
import com.elementwin.bs.service.mapper.PresaleImportDataMapper;

import dibo.framework.service.impl.BaseServiceImpl;
import dibo.framework.service.mapper.BaseMapper;

/***
 * 数据导入相关操作Service
 * @author Yaojf@dibo.ltd
 * @version v1.0, 2017-3-6
 * Copyright 2016 www.Dibo.ltd
 */
@Service("presaleImportDataService")
@Transactional
public class PresaleImportDataServiceImpl extends BaseServiceImpl implements PresaleImportDataService{
private static Logger logger = Logger.getLogger(PresaleImportDataServiceImpl.class);
	
	@Autowired
	PresaleImportDataMapper presaleImportDataMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return presaleImportDataMapper;
	}
}
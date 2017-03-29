package com.elementwin.bs.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.bs.service.RevisitRecordService;
import com.elementwin.bs.service.mapper.RevisitRecordMapper;

/***
 * Copyright 2016 www.Dibo.ltd
 * 回访记录相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/09/13
 */
@Service("revisitRecordService")
@Transactional
public class RevisitRecordServiceImpl extends BaseServiceImpl implements RevisitRecordService {
	private static Logger logger = Logger.getLogger(RevisitRecordServiceImpl.class);
	
	@Autowired
	RevisitRecordMapper revisitRecordMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return revisitRecordMapper;
	}
	
}
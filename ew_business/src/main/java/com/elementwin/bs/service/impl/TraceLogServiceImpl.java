package com.elementwin.bs.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.bs.service.TraceLogService;
import com.elementwin.bs.service.mapper.TraceLogMapper;

/***
 * Copyright 2016 www.Dibo.ltd
 * 操作日志相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/09/13
 */
@Service("traceLogService")
@Transactional
public class TraceLogServiceImpl extends BaseServiceImpl implements TraceLogService {
	private static Logger logger = Logger.getLogger(TraceLogServiceImpl.class);
	
	@Autowired
	TraceLogMapper traceLogMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return traceLogMapper;
	}
	
}
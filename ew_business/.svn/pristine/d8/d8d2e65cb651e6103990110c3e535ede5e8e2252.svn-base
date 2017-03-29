package com.elementwin.bs.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.bs.service.RevisitStatisticalService;
import com.elementwin.bs.service.mapper.RevisitStatisticalMapper;

/***
 * Copyright 2016 www.Dibo.ltd
 * 回访统计相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/09/13
 */
@Service("revisitStatisticalService")
@Transactional
public class RevisitStatisticalServiceImpl extends BaseServiceImpl implements RevisitStatisticalService {
	private static Logger logger = Logger.getLogger(RevisitStatisticalServiceImpl.class);
	
	@Autowired
	RevisitStatisticalMapper revisitStatisticalMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return revisitStatisticalMapper;
	}
	
}
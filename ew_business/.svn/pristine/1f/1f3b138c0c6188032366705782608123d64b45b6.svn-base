package com.elementwin.bs.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.bs.service.OrgServiceService;
import com.elementwin.bs.service.mapper.OrgServiceMapper;

/***
 * Copyright 2016 www.Dibo.ltd
 * 企业服务相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
@Service("orgServiceService")
@Transactional
public class OrgServiceServiceImpl extends BaseServiceImpl implements OrgServiceService {
	private static Logger logger = Logger.getLogger(OrgServiceServiceImpl.class);
	
	@Autowired
	OrgServiceMapper orgServiceMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return orgServiceMapper;
	}
	
}
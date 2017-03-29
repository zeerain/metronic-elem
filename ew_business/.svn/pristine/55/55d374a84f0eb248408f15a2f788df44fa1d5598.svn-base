package com.elementwin.bs.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.bs.service.RevisitWorkOrderService;
import com.elementwin.bs.service.mapper.RevisitWorkOrderMapper;
import com.elementwin.bs.model.RevisitWorkOrder;
import com.elementwin.bs.model.RevisitWorkOrderOpinion;

/***
 * Copyright 2016 www.Dibo.ltd
 * 工单相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/09/13
 */
@Service("revisitWorkOrderService")
@Transactional
public class RevisitWorkOrderServiceImpl extends BaseServiceImpl implements RevisitWorkOrderService {
	private static Logger logger = Logger.getLogger(RevisitWorkOrderServiceImpl.class);
	
	@Autowired
	RevisitWorkOrderMapper revisitWorkOrderMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return revisitWorkOrderMapper;
	}
	
	@Override
	public RevisitWorkOrder getWorkOrderWithOptions(Long workOrderId)
			throws Exception {
		return revisitWorkOrderMapper.getWorkOrderWithOptions(workOrderId);
	}
	
	@Override
	@Transactional
	public boolean createWorkOrderOpinion(RevisitWorkOrderOpinion model)
			throws Exception {
		return revisitWorkOrderMapper.createWorkOrderOpinion(model);
	}
	
}
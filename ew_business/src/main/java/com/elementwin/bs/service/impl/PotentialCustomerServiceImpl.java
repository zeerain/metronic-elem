package com.elementwin.bs.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elementwin.bs.model.PotentialCustomer;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.mapper.PotentialCustomerMapper;

import dibo.framework.service.impl.BaseServiceImpl;
import dibo.framework.service.mapper.BaseMapper;

/***
 * 潜客相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-24
 * Copyright 2016 www.Dibo.ltd
 */
@Service("potentialCustomerService")
@Transactional
public class PotentialCustomerServiceImpl extends BaseServiceImpl implements PotentialCustomerService{
	private static Logger logger = Logger.getLogger(PotentialCustomerServiceImpl.class);
	
	@Autowired
	PotentialCustomerMapper potentialCustomerMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return potentialCustomerMapper;
	}

	@Override
	public List<PotentialCustomer> getSimpleModelList(
			Map<String, Object> criteria) throws Exception {
		return potentialCustomerMapper.getSimpleModelList(criteria);
	}
	
	@Override
	public boolean updateFirstSpell(PotentialCustomer potentialCustomer){
		return potentialCustomerMapper.updateFirstSpell(potentialCustomer) > 0;
	}

}
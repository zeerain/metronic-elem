package com.elementwin.bs.service;

import java.util.List;
import java.util.Map;

import com.elementwin.bs.model.PotentialCustomer;

import dibo.framework.service.BaseService;

/***
 * 潜客相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-24
 * Copyright 2016 www.Dibo.ltd
 */
public interface PotentialCustomerService extends BaseService{

	/***
	 * 获取简单的潜客model列表，用于统计分析
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	List<PotentialCustomer> getSimpleModelList(Map<String, Object> criteria) throws Exception;

	public boolean updateFirstSpell(PotentialCustomer potentialCustomer);
}
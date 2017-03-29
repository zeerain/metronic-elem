package com.elementwin.bs.service.mapper;

import java.util.List;
import java.util.Map;

import dibo.framework.service.mapper.BaseMapper;

import com.elementwin.bs.model.PotentialCustomer;

/***
 * 潜客相关Mapper
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-24
 * Copyright 2016 www.Dibo.ltd
 */
public interface PotentialCustomerMapper extends BaseMapper{

	List<PotentialCustomer> getSimpleModelList(Map<String, Object> criteria) throws Exception;
	
	public int updateFirstSpell(PotentialCustomer potentialCustomer);

	int getStatisticValue(Map<String, Object> criteria) throws Exception;
}
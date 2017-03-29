package com.elementwin.bs.service.mapper;

import java.util.List;

import com.elementwin.common.model.Organization;

/***
 * Copyright 2016 www.Dibo.ltd
 * 客户相关Mapper
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
public interface OrganizationMapper extends dibo.framework.service.mapper.BaseMapper{

	Long getOrgIdByAppId(String appId) throws Exception;

	List<Organization> getSubCompanyList(Long orgId) throws Exception;
	
}
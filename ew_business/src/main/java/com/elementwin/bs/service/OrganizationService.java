package com.elementwin.bs.service;

import java.util.List;

import com.elementwin.bs.model.OrgUser;
import com.elementwin.common.model.MetadataItem;
import com.elementwin.common.model.Organization;

/***
 * Copyright 2016 www.Dibo.ltd
 * 客户相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
public interface OrganizationService extends dibo.framework.service.BaseService{
	
	public List<MetadataItem> getAvailableServices(Long orgId) throws Exception;

	public Long getOrgIdByAppId(String agentId) throws Exception;

	public List<Organization> getSubCompanyList(OrgUser orgUser) throws Exception;
	
}
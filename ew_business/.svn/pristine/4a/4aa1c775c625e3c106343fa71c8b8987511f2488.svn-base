package com.elementwin.bs.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.bs.model.OrgUser;
import com.elementwin.common.model.Organization;
import com.elementwin.common.model.MetadataItem;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.bs.service.mapper.MetadataItemMapper;
import com.elementwin.bs.service.mapper.OrganizationMapper;

/***
 * Copyright 2016 www.Dibo.ltd
 * 客户相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
@Service("organizationService")
@Transactional
public class OrganizationServiceImpl extends BaseServiceImpl implements OrganizationService {
	private static Logger logger = Logger.getLogger(OrganizationServiceImpl.class);
	
	@Autowired
	OrganizationMapper organizationMapper;
	
	@Autowired
	MetadataItemMapper metadataItemMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return organizationMapper;
	}

	@Override
	public List<MetadataItem> getAvailableServices(Long orgId) throws Exception {
		return metadataItemMapper.getAvailableServicesItems(orgId);
	}

	@Override
	public Long getOrgIdByAppId(String agentId) throws Exception {
		return organizationMapper.getOrgIdByAppId(agentId);
	}

	@Override
	public List<Organization> getSubCompanyList(OrgUser orgUser)
			throws Exception {
		if(!orgUser.isGroupUser()){
			return null;
		}
		return organizationMapper.getSubCompanyList(orgUser.getOrgId());
	}
	
}
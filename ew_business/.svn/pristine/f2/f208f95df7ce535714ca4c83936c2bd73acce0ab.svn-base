package com.elementwin.bs.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.common.model.PresaleImportData;
import com.elementwin.bs.model.TaskAssignRule;
import com.elementwin.bs.service.PresaleImportService;
import com.elementwin.bs.service.mapper.PresaleImportMapper;
import com.elementwin.bs.utils.AppHelper;

/***
 * 数据导入相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-15
 * Copyright 2016 www.Dibo.ltd
 */
@Service("presaleImportService")
@Transactional
public class PresaleImportServiceImpl extends BaseServiceImpl implements PresaleImportService{
	private static Logger logger = Logger.getLogger(PresaleImportServiceImpl.class);
	
	@Autowired
	PresaleImportMapper presaleImportMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return presaleImportMapper;
	}

	@Override
	public List<PresaleImportData> getImportDataList(
			Map<String, Object> criteria) throws Exception {
		criteria = appendOrgIdCriteria(criteria);
		return presaleImportMapper.getImportDataList(criteria);
	}

	@Override
	public List<String> findDuplicateData(List<PresaleImportData> modelList) throws Exception {
		if(modelList == null || modelList.isEmpty()){
			return null;
		}
		//TODO 需要优化
		List<String> indexList = new ArrayList<String>();
		List<String> importIndexList = new ArrayList<String>();
		Set<String> phoneSet = new HashSet<String>();
		for(PresaleImportData data : modelList){
			if(presaleImportMapper.findDuplicateData(data) > 0){
				indexList.add(String.valueOf(data.getRowIndex()));
			}
			if(phoneSet.contains(data.getCustomerPhone())){
				importIndexList.add(String.valueOf(data.getRowIndex()));				
			}
			else{
				phoneSet.add(data.getCustomerPhone());
			}
		}
		phoneSet = null;
		if(indexList.isEmpty() && !importIndexList.isEmpty()){
			return importIndexList;
		}
		return indexList;
	}

	@Override
	@Transactional
	public int batchCreateData(List<PresaleImportData> modelList)
			throws Exception {
		if(modelList == null || modelList.isEmpty()){
			return 0;			
		}
		return presaleImportMapper.batchCreateData(modelList);
	}
	
	private Map<String, Object> appendOrgIdCriteria(Map<String, Object> criteria){
		if(criteria == null){
			criteria = new HashMap<String, Object>();
		}
		criteria.put("org_id", AppHelper.getCurrentUser().getOrgId());
		return criteria;
	}

	@Override
	public List<TaskAssignRule> getMatchedAssignRules(Long serviceId, Long orgId)
			throws Exception {
		return presaleImportMapper.getMatchedAssignRules(serviceId, orgId);
	}
}
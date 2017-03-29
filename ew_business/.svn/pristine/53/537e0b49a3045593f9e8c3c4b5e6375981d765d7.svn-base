package com.elementwin.bs.service;

import java.util.List;
import java.util.Map;

import com.elementwin.bs.model.TaskAssignRule;
import com.elementwin.common.model.PresaleImportData;

import dibo.framework.service.BaseService;

/***
 * 数据导入相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-15
 * Copyright 2016 www.Dibo.ltd
 */
public interface PresaleImportService extends BaseService{

	List<PresaleImportData> getImportDataList(Map<String, Object> criteria) throws Exception;

	List<String> findDuplicateData(List<PresaleImportData> modelList) throws Exception;

	int batchCreateData(List<PresaleImportData> modelList) throws Exception;
	
	List<TaskAssignRule> getMatchedAssignRules(Long serviceId, Long orgId) throws Exception;
	
}
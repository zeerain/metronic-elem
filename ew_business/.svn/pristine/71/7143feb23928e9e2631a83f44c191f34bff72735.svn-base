package com.elementwin.bs.service.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.elementwin.bs.model.TaskAssignRule;
import com.elementwin.common.model.PresaleImportData;

import dibo.framework.service.mapper.BaseMapper;

/***
 * 数据导入相关Mapper
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-15
 * Copyright 2016 www.Dibo.ltd
 */
public interface PresaleImportMapper extends BaseMapper{

	List<PresaleImportData> getImportDataList(Map<String, Object> criteria) throws Exception;

	int findDuplicateData(PresaleImportData data) throws Exception;

	int batchCreateData(List<PresaleImportData> modelList) throws Exception;

	List<TaskAssignRule> getMatchedAssignRules(@Param("serviceId")Long serviceId, @Param("orgId")Long orgId) throws Exception;
	
}
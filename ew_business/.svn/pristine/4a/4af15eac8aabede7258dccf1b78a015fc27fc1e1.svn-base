package com.elementwin.bs.service.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import dibo.framework.service.mapper.BaseMapper;

import com.elementwin.bs.model.PresaleTask;
import com.elementwin.bs.model.PresaleRecord;
import com.elementwin.bs.model.PresaleCall;

/***
 * 售前任务相关Mapper
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-18
 * Copyright 2016 www.Dibo.ltd
 */
public interface PresaleTaskMapper extends BaseMapper{
	
	List<PresaleRecord> getRelatedPresaleRecordList(@Param("modelList")List<PresaleTask> modelList, @Param("isAsc")boolean isAsc) throws Exception;
	
	List<PresaleCall> getRelatedPresaleCallList(@Param("modelList")List<PresaleRecord> modelList) throws Exception;

	int createRecord(PresaleRecord record) throws Exception;

	List<PresaleTask> getUnsavedCustomerList(Long orgId) throws Exception;

	int updateTaskCustomer(PresaleTask task) throws Exception;
	
	// 获取all model列表
	public List<PresaleTask> getAllList(Map<String, Object> criteria) throws Exception;
	
	void linkFeedbackRecord(PresaleRecord record) throws Exception;

	Long getLatestCallRecordId(Long taskId) throws Exception;

	void linkFeedbackRecord(@Param("callRecordId")Long callRecordId, @Param("refererId")Long refererId);
	
	public List<PresaleRecord> getRecordModelList(Map<String, Object> criteria) throws Exception;

	int getStatisticValue(Map<String, Object> criteria) throws Exception;

	int updateTaskStatus(PresaleTask task) throws Exception;

	PresaleRecord getRelatedExpireRecord(Long taskId) throws Exception;

	int getMatchedExpireCount(Map<String, Object> criteria) throws Exception;
	
}
package com.elementwin.bs.service;

import java.util.List;
import java.util.Map;

import com.elementwin.api.config.Cons;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.PresaleRecord;
import com.elementwin.bs.model.PresaleTask;

import dibo.framework.service.BaseService;

/***
 * 售前任务相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-18
 * Copyright 2016 www.Dibo.ltd
 */
public interface PresaleTaskService extends BaseService{

	/***
	 * 设置Model的关联对象-PresaleRecord
	 */
	public void appendPresaleRecordList(PresaleTask model) throws Exception;
	
	/***
	 * 批量设置Model的关联对象-PresaleRecord
	 */
	public void appendPresaleRecordList(List<PresaleTask> modelList) throws Exception;

	/**
	 * 批量设置model的关联对象 - Customer
	 * @param modelList
	 * @throws Exception
	 */
	public void appendCustomer2Task(List<PresaleTask> modelList) throws Exception;
	
	/***
	 * 创建Record
	 * @param record
	 * @return
	 * @throws Exception
	 */
	public boolean createRecord(PresaleRecord record) throws Exception;

	/***
	 * 获取未保存的潜客记录
	 * @param orgId
	 * @return
	 * @throws Exception
	 */
	public List<PresaleTask> getUnsavedCustomerList(Long orgId) throws Exception;

	/***
	 * 获取所有的潜客记录
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	public List<PresaleTask> getAllModelList(Map<String, Object> criteria) throws Exception;
	
	/***
	 * 更新潜客id
	 * @param task
	 * @throws Exception
	 */
	public void updateTaskCustomer(PresaleTask task) throws Exception;

	/***
	 * 链接最新的拨打记录所关联的反馈记录
	 * @param record
	 * @throws Exception
	 */
	public void linkFeedbackRecord(PresaleRecord record) throws Exception;
	
	/**
	 * 获取记录
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	public List<PresaleRecord> getRecordModelList(Map<String, Object> criteria) throws Exception;
	
	/***
	 * 获取全维度数据的task（关联customer，record，call，audio）
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	public List<PresaleTask> getFatTaskList(Map<String, Object> criteria, int pageIndex) throws Exception;

	/***
	 * 获取统计值
	 * @param criteria
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public int[] getStatisticValue(Map<String, Object> criteria, OrgUser user, Cons.STATISTIC_TYPE type) throws Exception;
	
	/***
	 * 更新task状态
	 * @param task
	 * @return
	 * @throws Exception
	 */
	public boolean updateTaskStatus(PresaleTask task) throws Exception;

	/***
	 * 获取设置了超期的record
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public PresaleRecord getRelatedExpireRecord(Long taskId) throws Exception;
}
package com.elementwin.bs.service;

import java.util.List;
import java.util.Map;

import com.elementwin.bs.model.OrgServiceQuestion;
import com.elementwin.bs.model.RevisitAnswer;
import com.elementwin.bs.model.RevisitRecord;
import com.elementwin.bs.model.RevisitTask;
import com.elementwin.bs.model.RevisitWorkOrder;
import com.elementwin.bs.model.RevisitWorkOrderOpinion;
import com.elementwin.common.model.QuestionItem;

/***
 * Copyright 2016 www.Dibo.ltd
 * 回访任务相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/09/13
 */
public interface RevisitTaskService extends dibo.framework.service.BaseService{

	/***
	 * 获取任务详细内容
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public RevisitTask getRevisitTaskDetail(Long taskId) throws Exception;
	
	/**
	 * 获取回访记录相关联的答案
	 * @param recordId
	 * @return
	 * @throws Exception
	 */
	public List<RevisitAnswer> getRevisitAnswers(Long taskId) throws Exception;
	
	/**
	 * 工单记录数
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	public int getListWithWorkOrderCount(Map<String, Object> criteria) throws Exception;
	
	/***
	 * 工单记录
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	public List<RevisitTask> getListWithWorkOrder(Map<String, Object> criteria, int... pageIndex) throws Exception;
	
	/***
	 * 待工单的任务记录
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	public List<RevisitTask> getListWithWorkOrderAndDetail(Map<String, Object> criteria) throws Exception;

	/***
	 * 重新生成回访任务task
	 * @param taskId
	 * @throws Exception
	 */
	public void cloneNewTask(Long taskId) throws Exception;
	
	/**
	 * 查询出与任务集合相关的所有回访答案
	 * @param taskIds
	 * @throws Exception
	 */
	public List<RevisitAnswer> getAnswersInTasks(List<Long> taskIds) throws Exception;
	
	/**
	 * 查询出与任务集合相关的所有回访记录
	 * @param taskIds
	 * @return
	 * @throws Exception
	 */
	public List<RevisitRecord> getRecordsInTasks(List<Long> taskIds) throws Exception;
	
	/**
	 * 查询出与任务集合相关的所有工单记录
	 * @param workOrderIds
	 * @return
	 * @throws Exception
	 */
	public List<RevisitWorkOrder> getWorkOrders(List<Long> workOrderIds) throws Exception;
	
	/**
	 * 查询出当前回访话术问题
	 * @param questionId
	 * @return
	 * @throws Exception
	 */
	public List<QuestionItem> getQuestionItems(Long questionId) throws Exception;
	
	/***
	 * 查询客户服务对应的话术模板
	 * @param orgId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public List<OrgServiceQuestion> getServiceQuestionList(Long orgId, Long serviceId) throws Exception;
	
	/***
	 * 工单记录
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	public List<RevisitTask> getWorkOrderTask(Map<String, Object> criteria) throws Exception;
	
	/**
	 * 查询出与任务集合相关的所有工单记录的opinion
	 * @param workOrderIds
	 * @return
	 * @throws Exception
	 */
	public List<RevisitWorkOrderOpinion> getWorkOrderOpinion(List<Long> workOrderIds) throws Exception;

	/***
	 * 附加工单处理项
	 * @param allWorkOrders
	 * @throws Exception
	 */
	public void appendWorkOrderOpinions(List<RevisitWorkOrder> allWorkOrders) throws Exception;
	
}
package com.elementwin.bs.service.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.elementwin.bs.model.RevisitAnswer;
import com.elementwin.bs.model.RevisitRecord;
import com.elementwin.bs.model.RevisitTask;
import com.elementwin.bs.model.RevisitWorkOrder;
import com.elementwin.bs.model.OrgServiceQuestion;
import com.elementwin.bs.model.RevisitWorkOrderOpinion;
import com.elementwin.common.model.QuestionItem;

/***
 * Copyright 2016 www.Dibo.ltd
 * 回访任务相关Mapper
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/09/13
 */
public interface RevisitTaskMapper extends dibo.framework.service.mapper.BaseMapper{
	
	RevisitTask getTaskDetail(Long taskId) throws Exception;

	List<RevisitAnswer> getTaskAnswers(Long taskId) throws Exception;

	int getListWithWorkOrderCount(Map<String, Object> criteria) throws Exception;

	List<RevisitTask> getListWithWorkOrder(Map<String, Object> criteria) throws Exception;
	
	List<RevisitTask> getListWithWorkOrderAndDetail(Map<String, Object> criteria) throws Exception;
	
	List<RevisitAnswer> getAnswersInTasks(@Param("taskIds") List<Long> taskIds) throws Exception;
	
	List<RevisitRecord> getRecordsInTasks(@Param("taskIds") List<Long> taskIds) throws Exception;
	
	List<QuestionItem> getQuestionItems(Long questionId) throws Exception;
	
	List<RevisitWorkOrder> getWorkOrders(@Param("workOrderIds") List<Long> workOrderIds) throws Exception;
	
	public List<OrgServiceQuestion> getServiceQuestionList(@Param("orgId") Long orgId, @Param("serviceId")Long serviceId) throws Exception;
	
	List<RevisitTask> getWorkOrderTask(Map<String, Object> criteria) throws Exception;
	List<RevisitWorkOrderOpinion> getWorkOrderOpinions(@Param("workOrderIds") List<Long> workOrderIds) throws Exception;
	
}
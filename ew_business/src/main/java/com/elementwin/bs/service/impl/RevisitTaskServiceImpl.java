package com.elementwin.bs.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;
import dibo.framework.utils.V;

import com.elementwin.bs.model.OrgServiceQuestion;
import com.elementwin.bs.model.RevisitAnswer;
import com.elementwin.bs.model.RevisitRecord;
import com.elementwin.bs.model.RevisitTask;
import com.elementwin.bs.model.RevisitWorkOrder;
import com.elementwin.bs.model.RevisitWorkOrderOpinion;
import com.elementwin.bs.service.RevisitTaskService;
import com.elementwin.bs.service.mapper.RevisitTaskMapper;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.common.model.QuestionItem;
import com.elementwin.bs.config.Cons;

/***
 * Copyright 2016 www.Dibo.ltd
 * 回访任务相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/09/13
 */
@Service("revisitTaskService")
@Transactional
public class RevisitTaskServiceImpl extends BaseServiceImpl implements RevisitTaskService {
	private static Logger logger = Logger.getLogger(RevisitTaskServiceImpl.class);
	
	@Autowired
	RevisitTaskMapper revisitTaskMapper;

	@Override
	protected BaseMapper getMapper() {
		return revisitTaskMapper;
	}

	@Override
	public RevisitTask getRevisitTaskDetail(Long taskId) throws Exception {
		return revisitTaskMapper.getTaskDetail(taskId);
	}
	
	@Override
	public List<RevisitAnswer> getRevisitAnswers(Long taskId)
			throws Exception {
		return revisitTaskMapper.getTaskAnswers(taskId);
	}

	@Override
	public int getListWithWorkOrderCount(Map<String, Object> criteria)
			throws Exception {
		return revisitTaskMapper.getListWithWorkOrderCount(criteria);
	}

	@Override
	public List<RevisitTask> getListWithWorkOrder(Map<String, Object> criteria, int... pageIndex)
			throws Exception {
		if(pageIndex != null && pageIndex.length > 0){
			int pageSize = pageIndex.length > 1? pageIndex[1] : Cons.PAGE_SIZE;
			criteria.put("begin", pageSize * (pageIndex[0] - 1));
			criteria.put("pageSize", pageSize);		
		}
		return revisitTaskMapper.getListWithWorkOrder(criteria);
	}
	
	@Override
	public List<RevisitTask> getWorkOrderTask(Map<String, Object> criteria)
			throws Exception {
		return revisitTaskMapper.getWorkOrderTask(criteria);
	}
	
	@Override
	@Transactional
	public void cloneNewTask(Long taskId) throws Exception {
		// 将原task置为inactive
		RevisitTask oldTask = (RevisitTask) revisitTaskMapper.get(taskId);
		if(oldTask == null){
			logger.warn("无缝获取RevisitTask: id="+taskId);
			return;
		}
		
		oldTask.setActive(false);
		revisitTaskMapper.update(oldTask);
		
		// 克隆task
		RevisitTask newTask = oldTask;
		newTask.reset();
		// 设置上次回访人
		newTask.setPreviousOwnerId(oldTask.getOwnerId());
		newTask.setRefererId(taskId);

		newTask.setActive(true);
		// 创建新的task
		revisitTaskMapper.create(newTask);
	}

	@Override
	public List<RevisitTask> getListWithWorkOrderAndDetail(
			Map<String, Object> criteria) throws Exception {
		return revisitTaskMapper.getListWithWorkOrderAndDetail(criteria);
	}
	
	@Override
	public List<RevisitAnswer> getAnswersInTasks(List<Long> taskIds) throws Exception{
		if(taskIds == null || taskIds.isEmpty()){
			return null;
		}
		return revisitTaskMapper.getAnswersInTasks(taskIds);
	}
	
	@Override
	public List<RevisitRecord> getRecordsInTasks(List<Long> taskIds) throws Exception{
		if(taskIds == null || taskIds.isEmpty()){
			return null;
		}
		return revisitTaskMapper.getRecordsInTasks(taskIds);
	}
	@Override
	public List<RevisitWorkOrder> getWorkOrders(List<Long> workOrderIds) throws Exception{
		if(workOrderIds == null || workOrderIds.isEmpty()){
			return null;
		}
		return revisitTaskMapper.getWorkOrders(workOrderIds);
	}	
	
	@Override
	public List<QuestionItem> getQuestionItems(Long questionId) throws Exception{
		return revisitTaskMapper.getQuestionItems(questionId);
	}
	
	@Override
	public List<OrgServiceQuestion> getServiceQuestionList(Long orgId, Long serviceId) throws Exception{
		return revisitTaskMapper.getServiceQuestionList(orgId, serviceId);		
	}
	
	@Override
	public List<RevisitWorkOrderOpinion> getWorkOrderOpinion(List<Long> workOrderIds) throws Exception{
		if(workOrderIds == null || workOrderIds.isEmpty()){
			return null;
		}
		return revisitTaskMapper.getWorkOrderOpinions(workOrderIds);
	}

	@Override
	public void appendWorkOrderOpinions(List<RevisitWorkOrder> allWorkOrders)
			throws Exception {
		if(V.isEmpty(allWorkOrders)){
			return;
		}
		List<Long> workOrderIds = new ArrayList<Long>();
		for(RevisitWorkOrder wo : allWorkOrders){
			workOrderIds.add(wo.getId());
		}
		//查询所有在task集合中的所有工单的详细信息
		List<RevisitWorkOrderOpinion> allWorkOrderOpinions = getWorkOrderOpinion(workOrderIds);
		if(V.isEmpty(allWorkOrderOpinions)){
			return;
		}
		// 组装
		Map<Long, List<RevisitWorkOrderOpinion>> allWorkOrderOpinionMap = new HashMap<Long, List<RevisitWorkOrderOpinion>>();
		for(RevisitWorkOrderOpinion o : allWorkOrderOpinions){
			List<RevisitWorkOrderOpinion> list = allWorkOrderOpinionMap.get(o.getWorkOrderId());
			if(list == null){
				list = new ArrayList<RevisitWorkOrderOpinion>();
				allWorkOrderOpinionMap.put(o.getWorkOrderId(), list);
			}
			list.add(o);
		}
		for(RevisitWorkOrder wo : allWorkOrders){
			wo.setOpinionList(allWorkOrderOpinionMap.get(wo.getId()));
		}
		
		allWorkOrderOpinions = null;
		allWorkOrderOpinionMap = null;
	}
}
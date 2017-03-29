package com.elementwin.bs.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elementwin.api.config.Cons;
import com.elementwin.api.config.Cons.STATISTIC_TYPE;
import com.elementwin.bs.model.AudioMsg;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.PotentialCustomer;
import com.elementwin.bs.model.PresaleCall;
import com.elementwin.bs.model.PresaleRecord;
import com.elementwin.bs.model.PresaleTask;
import com.elementwin.bs.service.PresaleTaskService;
import com.elementwin.bs.service.mapper.AudioMsgMapper;
import com.elementwin.bs.service.mapper.PotentialCustomerMapper;
import com.elementwin.bs.service.mapper.PresaleTaskMapper;

import dibo.framework.service.impl.BaseServiceImpl;
import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.utils.V;

/***
 * 售前任务相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-18
 * Copyright 2016 www.Dibo.ltd
 */
@Service("presaleTaskService")
@Transactional
public class PresaleTaskServiceImpl extends BaseServiceImpl implements PresaleTaskService{
	private static Logger logger = Logger.getLogger(PresaleTaskServiceImpl.class);
	
	@Autowired
	PresaleTaskMapper presaleTaskMapper;
	
	@Autowired
	PotentialCustomerMapper potentialCustomerMapper;
	
	@Autowired
	AudioMsgMapper audioMsgMapper;
	
	@Override
	public void appendPresaleRecordList(PresaleTask model) throws Exception{
		List<PresaleTask> modelList = new ArrayList<PresaleTask>();
		modelList.add(model);
		
		List<PresaleRecord> relatedObj = presaleTaskMapper.getRelatedPresaleRecordList(modelList, false);
		
		// 附加通话数据到执行记录对象
		appendPresaleCallList(relatedObj);			
		// 附加录音数据到执行记录对象
		appendAudioMsgList(relatedObj);
					
		model.setPresaleRecordList(relatedObj);
		
		modelList = null;
	}
	
	@Override
	public void appendPresaleRecordList(List<PresaleTask> modelList) throws Exception{
		if(modelList == null || modelList.isEmpty()){
			return;
		}
		List<PresaleRecord> relatedObj = presaleTaskMapper.getRelatedPresaleRecordList(modelList, true);
		if(relatedObj != null && !relatedObj.isEmpty()){
			// 附加通话数据到执行记录对象
			appendPresaleCallList(relatedObj);
			// 附加录音数据到执行记录对象
			appendAudioMsgList(relatedObj);
			
			Map<Long, PresaleTask> map = new HashMap<Long, PresaleTask>();
			for(PresaleTask model : modelList){
				map.put(model.getId(), model);
			}
			for(PresaleRecord obj : relatedObj){
				PresaleTask model = map.get(obj.getTaskId());
				if(model != null){
					List<PresaleRecord> relList = model.getPresaleRecordList();
					if(relList == null){
						relList = new ArrayList<PresaleRecord>();
						model.setPresaleRecordList(relList);
					}
					relList.add(obj);
				}
			}
			map = null;
			relatedObj = null;
		}
	}

	/***
	 * 追加通话信息到任务执行记录
	 * @param modelList
	 * @throws Exception
	 */
	private void appendPresaleCallList(List<PresaleRecord> modelList) throws Exception{
		if(modelList == null || modelList.isEmpty()){
			return;
		}
		List<PresaleCall> relatedObj = presaleTaskMapper.getRelatedPresaleCallList(modelList);
		if(relatedObj != null && !relatedObj.isEmpty()){
			Map<Long, PresaleRecord> map = new HashMap<Long, PresaleRecord>();
			for(PresaleRecord model : modelList){
				map.put(model.getId(), model);
			}
			for(PresaleCall obj : relatedObj){
				PresaleRecord model = map.get(obj.getRecordId());
				if(model != null){
					List<PresaleCall> relList = model.getPresaleCallList();
					if(relList == null){
						relList = new ArrayList<PresaleCall>();
						model.setPresaleCallList(relList);
					}
					relList.add(obj);
				}
			}
			map = null;
		}
	}
	
	@Override
	@Transactional
	public boolean createRecord(PresaleRecord record) throws Exception {
		return presaleTaskMapper.createRecord(record) > 0;
	}

	@Override
	public List<PresaleTask> getUnsavedCustomerList(Long orgId)
			throws Exception {
		return presaleTaskMapper.getUnsavedCustomerList(orgId);
	}

	@Override
	@Transactional
	public void updateTaskCustomer(PresaleTask task) throws Exception {
		presaleTaskMapper.updateTaskCustomer(task);
	}
	
	@Override
	protected BaseMapper getMapper() {
		return presaleTaskMapper;
	}
	
	/***
	 * 获取所有的潜客记录
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<PresaleTask> getAllModelList(Map<String, Object> criteria) throws Exception {
		if(criteria == null){
			criteria = new HashMap<String, Object>();
		}
		return presaleTaskMapper.getAllList(criteria);
	}

	@Override
	public void linkFeedbackRecord(PresaleRecord record) throws Exception {
		Long latestCallRecordId = presaleTaskMapper.getLatestCallRecordId(record.getTaskId());
		if(latestCallRecordId != null){
			Long refererId = record.getId();
			presaleTaskMapper.linkFeedbackRecord(latestCallRecordId, refererId);			
		}
	}
	
	@Override
	public List<PresaleRecord> getRecordModelList(Map<String, Object> criteria) throws Exception {
		if(criteria == null){
			criteria = new HashMap<String, Object>();
		}
		return presaleTaskMapper.getRecordModelList(criteria);
	}

	@Override
	public List<PresaleTask> getFatTaskList(Map<String, Object> criteria, int pageIndex)
			throws Exception {
		// 加载当前页
		List<PresaleTask> modelList = (List<PresaleTask>) getModelList(criteria, pageIndex, Cons.WECHAT_PAGE_SIZE);
		// 关联record记录及call记录
		appendPresaleRecordList(modelList);
		// 将潜客信息关联至task
		appendCustomer2Task(modelList);
		
		return modelList;
	}

	/***
	 * 追加录音鑫鑫到任务执行记录
	 * @param modelList
	 * @throws Exception
	 */
	private void appendAudioMsgList(List<PresaleRecord> modelList) throws Exception{
		if(modelList == null || modelList.isEmpty()){
			return;
		}
		List<AudioMsg> relatedObj = audioMsgMapper.getRecordRelatedAudioMsgList(modelList);
		if(relatedObj != null && !relatedObj.isEmpty()){
			Map<Long, PresaleRecord> map = new HashMap<Long, PresaleRecord>();
			for(PresaleRecord model : modelList){
				map.put(model.getId(), model);
			}
			for(AudioMsg obj : relatedObj){
				PresaleRecord model = map.get(obj.getRelRecordId());
				if(model != null){
					List<AudioMsg> relList = model.getAudioMsgList();
					if(relList == null){
						relList = new ArrayList<AudioMsg>();
						model.setAudioMsgList(relList);
					}
					relList.add(obj);
				}
			}
			map = null;
		}
	}
	
	/**
	 * 附加潜客信息到task
	 * @param taskList
	 */
	public void appendCustomer2Task(List<PresaleTask> modelList) throws Exception{
		if(V.isEmpty(modelList)){
			return;
		}
		Map<Long, PresaleTask> map = new HashMap<Long, PresaleTask>();
		// modelList
		for(PresaleTask model : modelList){
			if(model.getCustomerId() != null){
				map.put(model.getCustomerId(), model);					
			}
		}
		Map<String, Object> criteria = new HashMap<String, Object>();
		Set set = map.keySet();
		if (set != null && set.size() > 0){
			criteria.put("idIn", map.keySet());

			List<PotentialCustomer> customerList = (List<PotentialCustomer>) potentialCustomerMapper.getList(criteria);
			if(customerList != null && !customerList.isEmpty()){
				for(PotentialCustomer obj : customerList){
					PresaleTask model = map.get(obj.getId());
					if(model != null){
						model.setCustomer(obj);					
					}
				}
				map = null;
				customerList = null;
			}
		}
	}

	@Override
	public int[] getStatisticValue(Map<String, Object> criteria, OrgUser user,
			STATISTIC_TYPE type) throws Exception {
		if(criteria == null){
			criteria = new HashMap<String, Object>();
		}
		// 统计潜客数量
		if(type.equals(Cons.STATISTIC_TYPE.CUSTOMER_COUNT)){
			criteria.put("from_org_id", user.getOrgId());
			int orgCount = potentialCustomerMapper.getStatisticValue(criteria);
			
			criteria.put("from_sa_id", user.getId());
			int userCount = potentialCustomerMapper.getStatisticValue(criteria);
			
			return new int[]{userCount, orgCount};
		}
		
		// 到店接待数量 ， 成交数量
		criteria.put("org_id", user.getOrgId());
		// 统计按时响应率
		if(type.equals(STATISTIC_TYPE.ON_TIME_RATE)){
			// 单位所有按时响应数量
			criteria.put("expired_flag", PresaleRecord.EXPIRED_FLAG_UNEXPIRED);
			int unexpiredCount = presaleTaskMapper.getMatchedExpireCount(criteria);
			// 单位所有应响应数量
			criteria.put("expired_flag", PresaleRecord.EXPIRED_FLAG_SET);
			criteria.put("allRequired", true);
			int requiredCount = presaleTaskMapper.getMatchedExpireCount(criteria);
			
			// 我的应响应数量
			criteria.put("salesperson_id", user.getId());
			int myRequiredCount = presaleTaskMapper.getMatchedExpireCount(criteria);
			
			// 我的按时响应数量
			criteria.remove("allRequired");
			criteria.put("expired_flag", PresaleRecord.EXPIRED_FLAG_UNEXPIRED);
			int myUnexpiredCount = presaleTaskMapper.getMatchedExpireCount(criteria);
			
			int userCount = myRequiredCount == 0? 100 : (myUnexpiredCount*100/myRequiredCount);
			int orgCount = requiredCount == 0? 100 : (unexpiredCount*100/requiredCount);
			
			return new int[]{userCount, orgCount};
		}
		
		// 接待次数
		if(type.equals(STATISTIC_TYPE.SERVICE_COUNT)){
			criteria.put("customerTagsIn", Cons.MATCH_KEYWORDS_SERVICE);
		}
		// 成交次数
		else if(type.equals(STATISTIC_TYPE.DEAL_COUNT)){
			criteria.put("customerTagsIn", Cons.MATCH_KEYWORDS_DEAL);			
		}
		
		int orgCount = presaleTaskMapper.getStatisticValue(criteria);
		
		criteria.put("salesperson_id", user.getId());
		int userCount = presaleTaskMapper.getStatisticValue(criteria);
		
		return new int[]{userCount, orgCount};
	}

	@Override
	@Transactional
	public boolean updateTaskStatus(PresaleTask task) throws Exception {
		return presaleTaskMapper.updateTaskStatus(task) > 0;
	}

	@Override
	public PresaleRecord getRelatedExpireRecord(Long taskId) throws Exception {
		return presaleTaskMapper.getRelatedExpireRecord(taskId);
	}
}
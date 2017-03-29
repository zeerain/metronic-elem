package com.elementwin.bs.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elementwin.api.config.Cons;
import com.elementwin.bs.model.PresaleTask;
import com.elementwin.bs.model.TaskAssignRule;
import com.elementwin.bs.service.PresaleImportService;
import com.elementwin.common.model.PresaleImportData;

/***
 * 
 * Task相关工具类
 * @author Mazc@dibo.ltd
 * @version 2017年3月5日
 * Copyright 2017 www.dibo.ltd
 */
public class TaskHelper {
	private static Logger logger = Logger.getLogger(TaskHelper.class); 
	
	/***
	 * 分配任务
	 * @param modelList
	 */
	public static void assignTask(PresaleImportService presaleImportService, PresaleTask task, Long orgId){
		List<PresaleTask> taskList = new ArrayList<PresaleTask>();
		taskList.add(task);
		assignTasks(presaleImportService, taskList, orgId);
	}
	/***
	 * 分配任务
	 * @param modelList
	 */
	public static void assignTasks(PresaleImportService presaleImportService, List<? extends PresaleImportData> taskList, Long orgId){
		// 系统自动分配任务
		List<TaskAssignRule> rules = null;
		try {
			rules = presaleImportService.getMatchedAssignRules(Cons.SERVICE_PRESALE_ID, orgId);
		}
		catch (Exception e) {
			logger.error("获取任务分配规则异常:", e);
		}
		if(rules != null && !rules.isEmpty()){
			Map<Long, Long> orgUserId2CcUserIdMap = new HashMap<Long, Long>();
			for(TaskAssignRule rule : rules){
				Long key = rule.getOrgUserId()!=null? rule.getOrgUserId() : 0L;
				orgUserId2CcUserIdMap.put(key, rule.getCcUserId());
			}
			for(PresaleImportData task : taskList){
				Long userId = task.getSalespersonId();
				Long ccUserId = orgUserId2CcUserIdMap.get(userId);
				if(ccUserId == null){
					ccUserId = orgUserId2CcUserIdMap.get(0L);
				}
				if(ccUserId != null){
					task.setCurrentOwnerId(ccUserId);
					task.setAssignBy(0L);
					// set task assigned
					task.setStatus(Cons.PRESALE_STATUS.ASSIGNED.name());
				}
				else{
					logger.warn("未获取到与售前导入数据"+task.getId()+"匹配的任务分配规则！");
				}				
			}
		}
		else{
			logger.warn("未获取到任何匹配的任务分配规则！");
		}
	}
}

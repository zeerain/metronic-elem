package com.elementwin.bs.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;

import com.elementwin.api.config.Cons;
import com.elementwin.api.utils.ChartHelper;
import com.elementwin.api.utils.DateUtils;
import com.elementwin.bs.cache.MetadataCache;
import com.elementwin.bs.model.PotentialCustomer;
import com.elementwin.bs.model.PresaleRecord;
import com.elementwin.bs.model.PresaleTask;
import com.elementwin.bs.model.RevisitStatistical;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.RevisitImportService;
import com.elementwin.bs.service.RevisitStatisticalService;
import com.elementwin.bs.service.PresaleTaskService;
import com.elementwin.common.model.RevisitImportData;

import dibo.framework.utils.S;
import dibo.framework.utils.V;

/***
 * 查询统计helper类
 * @author Mazc@dibo.ltd
 *
 */
public class StatisticHelper {
	
	private static int DEFAULT_DAYS = Integer.parseInt(MetadataCache.getSystemProperty("revisit.export.default.days"));
	
	/***
	 * 构建统计数据
	 * @param request
	 * @param modelMap
	 */
	public static void buildStatisticData(HttpServletRequest request, ModelMap modelMap, 
			RevisitStatisticalService revisitStatisticalService, RevisitImportService revisitImportService) throws Exception{
		// 添加查询条件
		Map<String, Object> criteria = (Map<String, Object>) modelMap.get("criteria");
		String beginDateStr = request.getParameter("beginDate");
		String endDateStr = request.getParameter("endDate");
		if(StringUtils.isBlank(beginDateStr)){
			Date today = new Date();
			Date beginDate = org.apache.commons.lang3.time.DateUtils.addDays(today, -DEFAULT_DAYS);
			
			beginDateStr = DateUtils.getDate(beginDate);
			endDateStr = DateUtils.getDate(today);
		}
		criteria.put("beginDate", beginDateStr);
		criteria.put("endDate", endDateStr);
		
		// 显示统计图表
		Map<String, Integer> chart1Map = new HashMap<String, Integer>();
		Map<String, Integer> chart2Map = new HashMap<String, Integer>();
		Map<String, Integer[]> chart3Map = new HashMap<String, Integer[]>();
		List<String> dateOrder = new ArrayList<String>();

		// 统计服务次数
		List<RevisitImportData> importDataList = revisitImportService.getSimpleImportDataList(criteria);
		if(importDataList != null && !importDataList.isEmpty()){
			chart1Map.put("服务次数", importDataList.size()); //TODO 查询服务次数
			for(RevisitImportData data : importDataList){
				
				increaseMapValue(chart2Map, data.getServiceEndDate());
				
				increaseMapValue(chart3Map, data.getCustomerManager(), 0);
				
				if(!dateOrder.contains(data.getServiceEndDate())){
					dateOrder.add(data.getServiceEndDate());
				}
			}
		}

		// 第二类统计
		Map<String, Integer> chart4_1Map = new HashMap<String, Integer>();
		Map<String, Integer> chart4_2Map = new HashMap<String, Integer>();
		
		Map<String, Integer> chart5Map = new HashMap<String, Integer>();
		Map<String, Integer[]> chart6Map = new HashMap<String, Integer[]>();
		
		// 第三类统计
		Map<String, Integer> chart7Map = new HashMap<String, Integer>();
		Map<String, Long[]> chart8Map = new HashMap<String, Long[]>();

		List<RevisitStatistical> modelList = (List<RevisitStatistical>) revisitStatisticalService.getModelList(criteria);
		if(modelList != null && !modelList.isEmpty()){
			chart1Map.put("回访次数", modelList.size());
			int doneCount = 0, workorderCount = 0, stopedCount = 0;
			for(RevisitStatistical model : modelList){
				if(Cons.REVISIT_STATUS.DONE.name().equalsIgnoreCase(model.getStatus())){
					doneCount++;
				}
				else{
					// 判断是否是终止的
					//TODO 161226 过渡阶段，兼容老数据，之后可废弃第二条件
					String stoped = AppHelper.getValueFromMapString(model.getStatusComment(), "schedule");
					if(Cons.REVISIT_STATUS.STOP.name().equalsIgnoreCase(model.getStatus()) || "终止".equals(stoped)){
						stopedCount++;
						
						String failReason = AppHelper.getValueFromMapString(model.getStatusComment(), "failReason");
						if(StringUtils.isNotBlank(failReason)){
							increaseMapValue(chart4_2Map, failReason);
						}
					}
				}
				if(model.getWorkOrderId() != null && model.getWorkOrderId().longValue() > 0){
					workorderCount++;
					
					// 工单数量-按用户
					increaseMapValue(chart3Map, model.getOrgUserName(), 1);

					// 工单数量-按日期
					increaseMapValue(chart6Map, model.getServiceDate(), 1);
					
					// 统计工单类别
					increaseMapValue(chart7Map, model.getWorkOrderCategory());

					// 统计工单处理时长
					Long[] durations = chart8Map.get(model.getOrgUserName());
					if(durations == null){
						durations = new Long[]{0L, 0L};
					}
					durations[0] = durations[0] + model.getWorkOrderDuration();
					durations[1] = durations[1] + 1;
					chart8Map.put(model.getOrgUserName(), durations);
				}
				
				//满分数， 创建工单数，及正常数
				String key5 = "正常数";
				if(model.getAnswerScore() >= model.getTotalScore()){
					key5 = "满分数";
					
					// 满分次数-按日期
					increaseMapValue(chart6Map, model.getServiceDate(), 0);
				}
				increaseMapValue(chart5Map, key5);
			}
			chart1Map.put("回访完成数", doneCount);
			chart1Map.put("工单数", workorderCount);
			
			chart4_1Map.put("完成数", doneCount);
			chart4_1Map.put("终止数", stopedCount);

			chart5Map.put("工单数", workorderCount);
		}
		
		modelMap.addAttribute("chart1Map", chart1Map);
		modelMap.addAttribute("chart2Map", chart2Map);
		modelMap.addAttribute("chart3Map", chart3Map);
		modelMap.addAttribute("dateOrder", dateOrder);
		
		modelMap.addAttribute("chart4_1Map", chart4_1Map);
		modelMap.addAttribute("chart4_2Map", chart4_2Map);
		modelMap.addAttribute("chart5Map", chart5Map);
		modelMap.addAttribute("chart6Map", chart6Map);

		modelMap.addAttribute("chart7Map", chart7Map);
		modelMap.addAttribute("chart8Map", chart8Map);

		modelMap.addAttribute("criteria", criteria);

		modelList = null;
		importDataList = null;
	}
	
	/***
	 * 构建统计数据
	 * @param request
	 * @param modelMap
	 */
	public static void buildPresaleStatisticData(HttpServletRequest request, ModelMap modelMap, PotentialCustomerService potentialCustomerService, PresaleTaskService presaleTaskService) throws Exception{
		// 添加查询条件
		Map<String, Object> criteria = (Map<String, Object>) modelMap.get("criteria");
		String beginDateStr = request.getParameter("beginDate");
		String endDateStr = request.getParameter("endDate");
		if(StringUtils.isBlank(beginDateStr)){
			Date today = new Date();
			Date beginDate = org.apache.commons.lang3.time.DateUtils.addDays(today, -DEFAULT_DAYS);
			
			beginDateStr = DateUtils.getDate(beginDate);
			endDateStr = DateUtils.getDate(today);
		}
		criteria.put("beginDate", beginDateStr);
		criteria.put("endDate", endDateStr);
		
		// 显示统计图表
		Map<String, Integer> chart1Map = new HashMap<String, Integer>();
		Map<String, Map<String, Integer>> chart3Map = new HashMap<String, Map<String, Integer>>();
		
		Map<String, Integer> chart2Map = new HashMap<String, Integer>();
		Map<String, Integer> chart4Map = new HashMap<String, Integer>();
		
		// 统计
		int taskCount = 0;
		int i = 0;
		String taskIds = null;
		List<PotentialCustomer> modelList = potentialCustomerService.getSimpleModelList(criteria);
		if(V.isNotEmpty(modelList)){
			taskCount = modelList.size();
			for(PotentialCustomer model : modelList){
				
				String category = V.isNotEmpty(model.getCategory())? model.getCategory() : "其他";
				increaseMapValue(chart1Map, category);
				
				if(V.isNotEmpty(model.getSalespersonName())){
					// 分销售顾问统计
					Map<String, Integer> map = chart3Map.get(model.getSalespersonName());
					if(map == null){
						map = new HashMap<String, Integer>();
						chart3Map.put(model.getSalespersonName(), map);
					}
					increaseMapValue(map, category);
				}
				
				if (i == 0) {
					taskIds = ""+model.getTaskId();
				} else {
					taskIds += ","+model.getTaskId();
				}
				i ++;
			}
			
			// 获取record记录
			Map<String, Object> criteriaTask = new HashMap<String, Object>();
			criteriaTask.put("taskIds", taskIds.split(","));
			List<PresaleRecord> modelRecordList = presaleTaskService.getRecordModelList(criteriaTask);
			if(V.isNotEmpty(modelRecordList)){
				for(PresaleRecord model : modelRecordList){
					// 已通知销售 计数
					if ("CALL".equals(model.getType()) && "CcUser".equals(model.getCreateByType())) {
						if (Cons.PRESALE_STATUS.NOTIFY_SA.toString().equals(model.getStatus())) {
							increaseMapValue(chart2Map, "通知销售次数");
							
							if (model.getRefererId() == null) { // 已通知未回复 计数
								increaseMapValue(chart2Map, "已通知未回复次数");
							}
						}
					}
					// 销售回复 计数
					if ("SA_UPDATE".equals(model.getType())) {
						increaseMapValue(chart2Map, "销售回复次数");
					}
					// 潜客标签 计数
					if(V.isNotEmpty(model.getCustomerTags())){
						increaseMapValue(chart4Map, model.getCustomerTags());					
					}
				}
			}
			
		}
		modelMap.addAttribute("taskCount", taskCount);
		modelMap.addAttribute("chart1Map", chart1Map);
		modelMap.addAttribute("chart3Map", chart3Map);
		
		modelMap.addAttribute("chart2Map", chart2Map);
		modelMap.addAttribute("chart4Map", chart4Map);

		modelMap.addAttribute("criteria", criteria);
	}

	/***
	 * 增长map的值
	 * @param map
	 * @param key
	 */
	private static void increaseMapValue(Map<String, Integer> map, String key){
		Integer temp = map.get(key);
		if(temp == null){
			temp = 0;
		}
		map.put(key, ++temp);
	}
	
	/**
	 * 增长map的值
	 * @param map
	 * @param key
	 * @param index
	 */
	private static void increaseMapValue(Map<String, Integer[]> map, String key, int index){
		Integer[] counts = map.get(key);
		if(counts == null){
			counts = new Integer[]{0, 0};
		}
		counts[index] = counts[index]+1;
		map.put(key, counts);
	}
	
}

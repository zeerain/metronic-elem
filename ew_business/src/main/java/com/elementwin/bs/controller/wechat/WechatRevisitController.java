package com.elementwin.bs.controller.wechat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.elementwin.api.config.Cons;
import com.elementwin.bs.utils.StatisticHelper;
import com.elementwin.common.model.MetadataItem;
import com.elementwin.bs.model.RevisitAnswer;
import com.elementwin.bs.model.RevisitRecord;
import com.elementwin.bs.model.RevisitTask;
import com.elementwin.bs.model.RevisitWorkOrder;
import com.elementwin.bs.model.RevisitWorkOrderOpinion;
import com.elementwin.bs.service.MetadataItemService;
import com.elementwin.bs.service.RevisitImportService;
import com.elementwin.bs.service.RevisitStatisticalService;
import com.elementwin.bs.service.RevisitTaskService;
import com.elementwin.bs.utils.AppHelper;

import dibo.framework.model.BaseModel;
import dibo.framework.service.BaseService;

/***
 * Dibo 微信售后管理controller 
 * @author Mazc@dibo.ltd
 * @version 2016年12月28日
 * Copyright 2016 www.dibo.ltd
 */
@Controller
@RequestMapping("/wechat/group/revisit")
public class WechatRevisitController extends BaseWechatController {
	private static Logger logger = Logger.getLogger(WechatRevisitController.class);
	
	@Autowired
	protected RevisitTaskService revisitTaskService;
	
	@Autowired
	protected MetadataItemService metadataItemService;
	
	@Autowired
	protected RevisitStatisticalService revisitStatisticalService;
	
	@Autowired
	protected RevisitImportService revisitImportService;
	
	protected BaseService getService(){
		return revisitTaskService;
	}
	
	@Override
	protected String getViewPrefix() {
		return "wechat/group/revisit/";
	}
	
	/***
	 * 显示列表首页面
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	protected String list(HttpServletRequest request, ModelMap modelMap) throws Exception {		
		// 加载第一页
		listPaging(1, request, modelMap);
		return super.view(request, modelMap, "list");
	}
	
	/**
	 * 显示首列表-分页
	 * @param pageIndex 分页
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/list/{pageIndex}", method = RequestMethod.GET)
	protected String listPaging(@PathVariable("pageIndex")int pageIndex, HttpServletRequest request, ModelMap modelMap) throws Exception{
		checkUser(request);
		// 获取当前单位id，默认取org列表第一个
		Long orgId = getCurrentOrgId(request, modelMap);

		// 封装并自动生成
		Map<String, Object> criteria = super.buildQueryCriteria(request, pageIndex);
		criteria.put("org_id", orgId);
		modelMap.addAttribute("criteria", criteria);
		
		// 获取记录总数，用于前端显示分页
		int totalCount = getService().getModelListCount(criteria);
		// 构建分页信息
		modelMap.addAttribute("pagination", super.buildPagination("/wechat/revisit/list/", criteria, totalCount, pageIndex));
		
		//获取总页数
		if (pageIndex == 1 ){
			int totalPages = (int)Math.ceil((float)totalCount/Cons.WECHAT_PAGE_SIZE);
			modelMap.addAttribute("totalPages", totalPages);
		}
		
		// 加载当前页
		List<? extends BaseModel> modelList = getService().getModelList(criteria, pageIndex, Cons.WECHAT_PAGE_SIZE);
		modelMap.addAttribute("modelList", modelList);
		
		if (pageIndex > 1) {
			return super.view(request, modelMap, "list_paging");
		}
		
		return super.view(request, modelMap, "list_page");
	}
	
	/***
	 * 显示查看详细页面
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	protected String viewPage(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap) throws Exception {  
		checkUser(request);
		
		BaseModel model = getService().getModel(id);
		modelMap.put("model", model);
		
		return super.view(request, modelMap, "detail");
    }
	
	/***
	 * 显示列表首页面
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/woList/{status}", method = RequestMethod.GET)
	protected String woList(@PathVariable("status")String status, HttpServletRequest request, ModelMap modelMap) throws Exception {		
		// 加载第一页
		woListPaging(status, 1, request, modelMap);
		return super.view(request, modelMap, "wo_list");
	}
	
	/**
	 * 显示首列表-分页
	 * @param pageIndex 分页
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/woList/{status}/{pageIndex}", method = RequestMethod.GET)
	protected String woListPaging(@PathVariable("status")String status, @PathVariable("pageIndex")int pageIndex, HttpServletRequest request, ModelMap modelMap) throws Exception{
		// 检查用户登录信息
		checkUser(request);
		// 获取当前单位id，默认取org列表第一个
		Long orgId = getCurrentOrgId(request, modelMap);
		
		// 封装并自动生成
		Map<String, Object> criteria = super.buildQueryCriteria(request, pageIndex);
		criteria.put("org_id", orgId);
		if("PENDING".equalsIgnoreCase(status)){
			criteria.put("workorderStatusIn", new String[]{Cons.WORK_ORDER_STATUS.PENDING.name()});			
		}
		else{
			criteria.put("workorderStatusIn", new String[]{Cons.WORK_ORDER_STATUS.SOLVED.name(), Cons.WORK_ORDER_STATUS.AUDIT.name()});
		}
		// 获取记录总数，用于前端显示分页
		int totalCount = revisitTaskService.getListWithWorkOrderCount(criteria);
		// 构建分页信息
		modelMap.addAttribute("pagination", super.buildPagination("/wechat/revisit/woList/"+status+"/", criteria, totalCount, pageIndex));
		modelMap.addAttribute("criteria", criteria);
		
		//获取总页数
		if (pageIndex == 1 ){
			int totalPages = (int)Math.ceil((float)totalCount/Cons.WECHAT_PAGE_SIZE);
			modelMap.addAttribute("totalPages", totalPages);
		}
		
		// 加载当前页
		List<RevisitTask> modelList = (List<RevisitTask>) revisitTaskService.getListWithWorkOrder(criteria, pageIndex, Cons.WECHAT_PAGE_SIZE);
		
		modelList = enhanceTaskList(modelList);
		
		modelMap.put("modelList", modelList);
		
		List<MetadataItem> categoryList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.WO_CATEGORY);
		modelMap.put("categoryList", categoryList);

		modelMap.put("status", status.toUpperCase());
		
		if (pageIndex > 1) {
			return super.view(request, modelMap, "wo_list_paging");
		}
		
		return super.view(request, modelMap, "wo_list_page");
	}
	
	/***
	 * 扩展task的附加属性
	 * @param modelList
	 * @return
	 */
	private List<RevisitTask> enhanceTaskList(List<RevisitTask> modelList) throws Exception{
		//编辑taskId集合
		List<Long> taskIds = new ArrayList<Long>();
		//编辑workOrderIds集合
		List<Long> workOrderIds = new ArrayList<Long>();
		if(modelList != null && !modelList.isEmpty()){
			for(RevisitTask task : modelList){
				taskIds.add(task.getId());
				workOrderIds.add(task.getWorkOrderId());
			}
			//查询所有在taskId集合中的答案记录
			List<RevisitAnswer> allAnswers = revisitTaskService.getAnswersInTasks(taskIds);
			Map<Long, List<BaseModel>> allAnswerMap = AppHelper.list2map(allAnswers);
			
			//查询所有在taskId集合中的record记录
			List<RevisitRecord> allRecords = revisitTaskService.getRecordsInTasks(taskIds);
			Map<Long, List<BaseModel>> allRecordMap = AppHelper.list2map(allRecords);
			
			//查询所有在task集合中的所有工单记录
			List<RevisitWorkOrder> allWorkOrders = revisitTaskService.getWorkOrders(workOrderIds);
			Map<Long, List<BaseModel>> allWorkOrderMap = AppHelper.list2map(allWorkOrders);
			
			//查询所有在task集合中的所有工单的详细信息
			List<RevisitWorkOrderOpinion> allWorkOrderOpinions = revisitTaskService.getWorkOrderOpinion(workOrderIds);
			Map<Long, List<BaseModel>> allWorkOrderOpinionMap = AppHelper.list2map(allWorkOrderOpinions);
			
			//拼接结果，生成task列表
			for(RevisitTask task : modelList){
				List<RevisitAnswer> revisitAnswers = new ArrayList<RevisitAnswer>();
				List<RevisitRecord> revisitRecords = new ArrayList<RevisitRecord>();
				List<RevisitWorkOrderOpinion> revisitWorkOrderOpinions = new ArrayList<RevisitWorkOrderOpinion>();
				
				//拼接答案记录
				if(allAnswerMap != null){
					List<BaseModel> answers = (List<BaseModel>) allAnswerMap.get(task.getId());
					if(answers != null && !answers.isEmpty()) {
						for(BaseModel answer : answers){
							revisitAnswers.add((RevisitAnswer) answer);
						}
						task.setAnswerList(revisitAnswers);
					}					
				}
				
				//拼接record记录
				if(allRecordMap != null){
					List<BaseModel> records = (List<BaseModel>) allRecordMap.get(task.getId());
					if(records != null && !records.isEmpty()) {
						for(BaseModel record : records){
							revisitRecords.add((RevisitRecord) record);
						}
						task.setRecordList(revisitRecords);
					}					
				}
				
				//拼接工单记录
				if(allWorkOrderMap != null){
					List<BaseModel> workOrders = allWorkOrderMap.get(task.getWorkOrderId());
					if(workOrders != null && !workOrders.isEmpty()){
						RevisitWorkOrder workOrder = (RevisitWorkOrder) workOrders.get(0);
						if (allWorkOrderOpinionMap != null && !allWorkOrderOpinionMap.isEmpty()) {
							//工单拼接详细信息
							List<BaseModel> workOrderOpinions = allWorkOrderOpinionMap.get(task.getWorkOrderId());
							if (workOrderOpinions != null && !workOrderOpinions.isEmpty()) {
								for(BaseModel workOrderOpinion : workOrderOpinions) {
									revisitWorkOrderOpinions.add((RevisitWorkOrderOpinion) workOrderOpinion);
								}
								workOrder.setOpinionList(revisitWorkOrderOpinions);
							}
						}
						task.setWorkOrder(workOrder);
					}					
				}
				
			}
		}
		
		return modelList;
	}

	/***
	 * 查看统计报告
	 * @return
	 */
	@RequestMapping(value = "/report", method = RequestMethod.GET)
	protected String reportPage(HttpServletRequest request, ModelMap modelMap) throws Exception {  
		checkUser(request);

		// 统计当前单位
		Long orgId = getCurrentOrgId(request, modelMap);
		
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", orgId);
		modelMap.addAttribute("criteria", criteria);
		// 构建统计数据
		StatisticHelper.buildStatisticData(request, modelMap, revisitStatisticalService, revisitImportService);
		
		return super.view(request, modelMap, "report");
	}
	
}

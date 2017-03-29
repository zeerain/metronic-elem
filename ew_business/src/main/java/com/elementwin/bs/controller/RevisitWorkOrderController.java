package com.elementwin.bs.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dibo.framework.service.BaseService;

import com.elementwin.api.config.Cons;
import com.elementwin.common.model.MetadataItem;
import com.elementwin.bs.model.RevisitTask;
import com.elementwin.bs.model.RevisitWorkOrder;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.RevisitWorkOrderOpinion;
import com.elementwin.bs.service.CcUserService;
import com.elementwin.bs.service.MetadataItemService;
import com.elementwin.bs.service.RevisitTaskService;
import com.elementwin.bs.service.RevisitWorkOrderService;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.bs.model.CcUser;

/***
 * Copyright 2016 www.Dibo.ltd
 * 工单相关操作Controller
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/09/13
 */
@Controller
@RequestMapping("/revisitWorkOrder")
public class RevisitWorkOrderController extends RevisitRecordController {
	private static Logger logger = Logger.getLogger(RevisitWorkOrderController.class);

	@Autowired
	private RevisitTaskService revisitTaskService;
	
	@Autowired
	private RevisitWorkOrderService revisitWorkOrderService;
	
	@Autowired
	private OrgUserService orgUserService;
	
	@Autowired
	private MetadataItemService metadataItemService;
	
	@Autowired
	private CcUserService ccUserService;
	
	// 工单类别
	private List<MetadataItem> woCategoryList = null;
	
	@Override
	protected String getViewPrefix() {
		return "revisitWorkOrder";
	}
	
	/***
	 * 根路径/
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String root(HttpServletRequest request, ModelMap modelMap) throws Exception {		
		woCategoryList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.WO_CATEGORY);
		return index(request, modelMap);
	}
	
	/***
	 * 显示首页面
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(HttpServletRequest request, ModelMap modelMap) throws Exception {		
		// 加载第一页
		return indexPaging(1, request, modelMap);
	}
	
	/**
	 * 显示首页-分页
	 * @param pageIndex 分页
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value="/index/{pageIndex}", method=RequestMethod.GET)
	public String indexPaging(@PathVariable("pageIndex")int pageIndex, HttpServletRequest request, ModelMap modelMap) throws Exception{
		// 添加单位id
		super.buildCriteriaWithOrgId(modelMap);
		// 封装并自动生成
		Map<String, Object> criteria = super.buildQueryCriteria(request, pageIndex);
		// 是否已经存在附加条件
		if(modelMap.get("criteria") != null){
			Map<String, Object> additionalCriteria = (Map<String, Object>) modelMap.get("criteria");
			if(additionalCriteria != null && !additionalCriteria.isEmpty()){
				for(Map.Entry<String, Object> entry : additionalCriteria.entrySet()){
					criteria.put(entry.getKey(), entry.getValue());
				}
			}
		}
		
		modelMap.addAttribute("criteria", criteria);
		
		// 获取记录总数，用于前端显示分页
		int totalCount = revisitTaskService.getListWithWorkOrderCount(criteria);
		
		// 构建分页信息
		modelMap.addAttribute("pagination", AppHelper.buildPagination(getViewPrefix()+"/index", criteria, totalCount, pageIndex));
		
		// 加载当前页
		List<RevisitTask> modelList = revisitTaskService.getListWithWorkOrder(criteria, pageIndex);
		modelMap.addAttribute("modelList", modelList);

		if(woCategoryList == null){
			woCategoryList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.WO_CATEGORY);			
		}
		modelMap.put("categoryList", woCategoryList);
		
		List<CcUser> userList = (List<CcUser>) ccUserService.getModelList(null);
		modelMap.addAttribute("ccUserList", userList);
		
		return view(request, modelMap, "index");
	}
	
	/***
	 * 显示查看详细页面
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String viewPage(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap) throws Exception {  
		return super.viewPage(id, request, modelMap);
    }
	
	/***
	 * 更新的后台处理
	 * @param revisitWorkOrder
	 * @param result
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	public String update(@PathVariable("id")Long id, @Valid RevisitWorkOrder model, BindingResult result, HttpServletRequest request, ModelMap modelMap) throws Exception{
		String urlContext = request.getParameter("urlContext");
		if(StringUtils.isBlank(request.getParameter("urlContext"))){
			urlContext = "revisitWorkOrder";
		}
		
		String taskIdStr = request.getParameter("taskId");
		RevisitWorkOrder oldModel = (RevisitWorkOrder) revisitWorkOrderService.getModel(id);
		// Model属性值验证结果
		if(result.hasErrors()) {
			AppHelper.bindErrors(modelMap, result);
            return "redirect:/"+urlContext+"/view/"+taskIdStr;  
        }
		// 追加处理意见
		String category = request.getParameter("category");
		String opinionStr = request.getParameter("opinion");
		boolean isRevisitAgain = "true".equalsIgnoreCase(request.getParameter("revisitAgain"));

		OrgUser user = AppHelper.getCurrentUser();
		RevisitWorkOrderOpinion opinion = new RevisitWorkOrderOpinion();
		opinion.setWorkOrderId(id);
		opinion.setCategory(category);
		opinion.setOpinion(opinionStr);
		opinion.setStatus(model.getStatus());
		opinion.setRevisitAgain(isRevisitAgain);
		opinion.setUserType(OrgUser.class.getSimpleName());
		opinion.setCreateBy(user.getId());
		opinion.setCreateByName(user.getRealname());
		// 创建工单处理意见
		revisitWorkOrderService.createWorkOrderOpinion(opinion);
		
		// 审核的提交结果，直接记录最终结果
		if(oldModel.getStatus().equalsIgnoreCase(Cons.WORK_ORDER_STATUS.AUDIT.name())){ // 审核的最终结果
			oldModel.setConfirmedCategory(category);
			oldModel.setStatus(Cons.WORK_ORDER_STATUS.SOLVED.name());
		}
		// 已解决
		else if(Cons.WORK_ORDER_STATUS.SOLVED.name().equalsIgnoreCase(model.getStatus())){
			oldModel.setCloseTime(new Date());
			if(oldModel.getCreateTime() != null){
				// 计算工单处理时长
				oldModel.setWorkOrderDuration((oldModel.getCloseTime().getTime() - oldModel.getCreateTime().getTime())/1000);				
			}
			if(oldModel.getCategory().equalsIgnoreCase(category)){	// 客户经理指定的类别跟系统一致时			
				oldModel.setConfirmedCategory(category);			
				oldModel.setStatus(Cons.WORK_ORDER_STATUS.SOLVED.name());
			}
			else{ // 客户经理指定的类型不一致时
				oldModel.setConfirmedCategory(null);			
				oldModel.setStatus(Cons.WORK_ORDER_STATUS.AUDIT.name());
				
				OrgUser leader = orgUserService.getManagerByEmployeeName(oldModel.getOrgId(), oldModel.getCustomerManager());
				if(leader != null){
					oldModel.setAuditorId(leader.getId());					
				}
				else{
					logger.warn("无法获取客户经理领导！" + oldModel.getOrgName()+"-"+oldModel.getCustomerManager());
				}
			}
		}
        // 执行保存操作
        boolean success = revisitWorkOrderService.updateModel(oldModel);
        
        // 再次回访
        if(Cons.WORK_ORDER_STATUS.SOLVED.name().equalsIgnoreCase(model.getStatus())){
        	Long taskId = Long.parseLong(taskIdStr);
        	if(isRevisitAgain && StringUtils.isNotBlank(taskIdStr)){
        		// 重新生成task
        		revisitTaskService.cloneNewTask(taskId);
        		
        		// 异步更新当前任务统计为失效 
        		asyncWorker.updateStatistical2Inactive(taskId);
        	}
        	else{
        		// 异步更新当前任务统计-工单类别
        		asyncWorker.updateStatisticalData(taskId, oldModel);
        	}
        }
        
        // 绑定执行结果
        String msg = success?"处理工单操作成功！":"处理工单操作失败！";
        addResultMsg(request, success, msg);
        
        // 记录操作日志
		asyncWorker.saveTraceLog(user, Cons.OPERATION.UPDATE, oldModel, "处理工单: "+oldModel.getId());
		
		return "redirect:/"+urlContext+"/";
	}
	
	@Override
	protected BaseService getService() {
		return revisitTaskService;
	}
}
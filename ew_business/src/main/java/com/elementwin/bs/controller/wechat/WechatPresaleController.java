package com.elementwin.bs.controller.wechat;

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
import com.elementwin.bs.model.PotentialCustomer;
import com.elementwin.bs.model.PresaleTask;
import com.elementwin.bs.utils.StatisticHelper;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.PresaleTaskService;

import dibo.framework.service.BaseService;
import dibo.framework.utils.V;

/***
 * Dibo 微信售后管理controller 
 * @author Mazc@dibo.ltd
 * @version 2016年12月28日
 * Copyright 2016 www.dibo.ltd
 */
@Controller
@RequestMapping("/wechat/group/presale")
public class WechatPresaleController extends BaseWechatController {
	private static Logger logger = Logger.getLogger(WechatPresaleController.class);
	
	@Autowired
	protected PresaleTaskService presaleTaskService;
	
	@Autowired
	protected PotentialCustomerService potentialCustomerService;
	
	protected BaseService getService(){
		return presaleTaskService;
	}
	
	@Override
	protected String getViewPrefix() {
		return "wechat/group/presale/";
	}
	
	/**
	 * 微信企业号--潜客任务
	 * @param wechat
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list/{status}", method = RequestMethod.GET)
	public String presaleTask(@PathVariable("status")String status, HttpServletRequest request, ModelMap modelMap) throws Exception {
		// 加载第一页
		listPaging(status, 1, request, modelMap);
		return super.view(request, modelMap, "list");
	}
	

	/**
	 * 显示首列表-分页
	 * @param pageIndex 分页
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/list/{status}/{pageIndex}", method = RequestMethod.GET)
	protected String listPaging(@PathVariable("status")String status, @PathVariable("pageIndex")int pageIndex, HttpServletRequest request, ModelMap modelMap) throws Exception{
		checkUser(request);
		// 获取当前单位id，默认取org列表第一个
		Long orgId = getCurrentOrgId(request, modelMap);
		
		// 封装并自动生成
		Map<String, Object> criteria = super.buildQueryCriteria(request, pageIndex);
		criteria.put("org_id", orgId);
		
		if("CONTINUES".equalsIgnoreCase(status)){
			criteria.put("statusNotIn", new String[]{Cons.PRESALE_STATUS.DONE.name(), Cons.PRESALE_STATUS.STOP.name()});
		}
		else if("DONES".equalsIgnoreCase(status)){
			criteria.put("statusIn", new String[]{Cons.PRESALE_STATUS.DONE.name(), Cons.PRESALE_STATUS.STOP.name()});			
		}
		else{
			criteria.put("status", status.toUpperCase());			
		}
		
		modelMap.addAttribute("criteria", criteria);
		
		// 根据选择的条件搜索
		String customerName = request.getParameter("customer_name");
		String category = request.getParameter("category");
		boolean isSaFeedback = "true".equalsIgnoreCase(request.getParameter("saFeedback"));
		if (V.isNotEmpty(customerName)) {
			criteria.put("customer_name", customerName);
		}
		if (V.isNotEmpty(category)) {
			criteria.put("category", category);
		}
		if (isSaFeedback) {
			criteria.put("status", Cons.PRESALE_STATUS.NOTIFY_SA.toString());
		}
		
		// 获取记录总数，用于前端显示分页
		int totalCount = presaleTaskService.getModelListCount(criteria);
		// 构建分页信息
		modelMap.addAttribute("pagination", super.buildPagination("/wechat/revisit/list/"+status, criteria, totalCount, pageIndex));
		
		//获取总页数
		if (pageIndex == 1 ){
			int totalPages = (int)Math.ceil((float)totalCount/Cons.WECHAT_PAGE_SIZE);
			modelMap.addAttribute("totalPages", totalPages);
		}
		
		// 加载当前页
		List<PresaleTask> tasks = (List<PresaleTask>) presaleTaskService.getModelList(criteria, pageIndex, Cons.WECHAT_PAGE_SIZE);
		// 整合最后一条回访记录的内容
		presaleTaskService.appendPresaleRecordList(tasks);
		modelMap.addAttribute("modelList", tasks);
		
		modelMap.addAttribute("status", status.toUpperCase());
		
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
	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	protected String viewPage(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap) throws Exception {  
		checkUser(request);
		
		// 获取model，追加关联record
		PresaleTask task = (PresaleTask) presaleTaskService.getModel(id);
		
		presaleTaskService.appendPresaleRecordList(task);			
		modelMap.addAttribute("model", task);
		
		PotentialCustomer customer = (PotentialCustomer) potentialCustomerService.getModel(task.getCustomerId());
		modelMap.addAttribute("customer", customer);
		
		modelMap.addAttribute("canEdit", false);
		
		return super.view(request, modelMap, "view");
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
		criteria.put("from_org_id", orgId);
		modelMap.addAttribute("criteria", criteria);
		// 构建统计数据
		StatisticHelper.buildPresaleStatisticData(request, modelMap, potentialCustomerService, presaleTaskService);
				
		return super.view(request, modelMap, "report");
	}
	
}

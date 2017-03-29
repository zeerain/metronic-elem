package com.elementwin.bs.controller;

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

import dibo.framework.service.BaseService;

import com.elementwin.api.config.Cons;
import com.elementwin.common.model.MetadataItem;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.RevisitTask;
import com.elementwin.bs.model.RevisitWorkOrder;
import com.elementwin.bs.service.CcUserService;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.service.MetadataItemService;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.bs.service.RevisitTaskService;
import com.elementwin.bs.service.RevisitWorkOrderService;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.bs.model.CcUser;

/***
 * Copyright 2016 www.Dibo.ltd
 * 回访记录相关操作Controller
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/09/13
 */
@Controller
@RequestMapping("/revisitRecord")
public class RevisitRecordController extends BaseCRUDController {
	private static Logger logger = Logger.getLogger(RevisitRecordController.class);

	@Autowired
	private RevisitTaskService revisitTaskService;
	
	@Autowired
	private RevisitWorkOrderService revisitWorkOrderService;

	@Autowired
	private OrgUserService orgUserService;
	
	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private CcUserService ccUserService;
	
	@Autowired
	private MetadataItemService metadataItemService;
	
	// 工单类别
	private List<MetadataItem> woCategoryList = null;
	
	@Override
	protected String getViewPrefix() {
		return "revisitRecord";
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
		// 添加当前参数
		OrgUser user = AppHelper.getCurrentUser();
		AppHelper.bindSubCompany(request, organizationService, user, modelMap);
		if(!user.isGroupUser() && !user.isManager()){
			Map<String, Object> criteria = (Map<String, Object>) modelMap.get("criteria");
			criteria.put("salesperson_id", user.getId());			
			modelMap.addAttribute("criteria", criteria);
		}
		
		List<CcUser> userList = (List<CcUser>) ccUserService.getModelList(null);
		modelMap.addAttribute("ccUserList", userList);
		
		if(woCategoryList == null){
			woCategoryList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.WO_CATEGORY);			
		}
		modelMap.put("categoryList", woCategoryList);
		
		return super.indexPaging(pageIndex, request, modelMap);
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
		OrgUser user = AppHelper.getCurrentUser();
		
		RevisitTask model = (RevisitTask) revisitTaskService.getRevisitTaskDetail(id);
		if(model != null && !model.getOrgId().equals(user.getOrgId())){
			AppHelper.checkPermission(request, organizationService, user, model.getOrgId());
		}

		// 工单
		if(model != null && model.getWorkOrderId() != null){
			RevisitWorkOrder workOrder = revisitWorkOrderService.getWorkOrderWithOptions(model.getWorkOrderId());
			model.setWorkOrder(workOrder);			
			
			List<MetadataItem> categoryList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.WO_CATEGORY);
			modelMap.put("categoryList", categoryList);
		}
		modelMap.put("model", model);
		
		modelMap.put("urlContext", getViewPrefix());
		
		return super.viewPage(id, request, modelMap);
    }
	
	@Override
	protected BaseService getService() {
		return revisitTaskService;
	}
}
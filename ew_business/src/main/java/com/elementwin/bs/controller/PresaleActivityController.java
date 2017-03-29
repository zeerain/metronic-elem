package com.elementwin.bs.controller;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.ResponseBody;

import dibo.framework.model.BaseModel;
import dibo.framework.service.BaseService;

import com.elementwin.api.utils.DateUtils;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.PresaleActivity;
import com.elementwin.bs.service.PresaleActivityService;
import com.elementwin.bs.utils.AppHelper;

/***
 * 销售政策相关操作Controller
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-23
 * Copyright 2016 www.Dibo.ltd
 */
@Controller
@RequestMapping("/presaleActivity")
public class PresaleActivityController extends BaseCRUDController {
	private static Logger logger = Logger.getLogger(PresaleActivityController.class);
	
	@Autowired
	private PresaleActivityService presaleActivityService;
	
	@Override
	protected String getViewPrefix() {
		return "presaleActivity";
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
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", AppHelper.getCurrentUser().getOrgId());
		modelMap.addAttribute("criteria", criteria);
		
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
		return super.viewPage(id, request, modelMap);
    }
	
	/***
	 * 显示创建页面
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createPage(HttpServletRequest request, ModelMap modelMap) throws Exception {  
		return super.createPage(request, modelMap);
    }
	
	/***
	 * 创建的后台处理
	 * @param presaleActivity
	 * @param result
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@Valid PresaleActivity model, BindingResult result, HttpServletRequest request, ModelMap modelMap) throws Exception {  
		actionBeforeValidate(request, model, modelMap);
		return super.create(model, result, request, modelMap);
    }
	
	/***
	 * 显示更新页面
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String updatePage(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap) throws Exception {  
		return super.updatePage(id, request, modelMap);
    }
	
	/***
	 * 更新的后台处理
	 * @param presaleActivity
	 * @param result
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	public String update(@PathVariable("id")Long id, @Valid PresaleActivity model, BindingResult result, HttpServletRequest request, ModelMap modelMap) throws Exception{
		return super.update(id, model, result, request, modelMap);
	}
	
	/***
	 * 删除的后台处理
	 * @param presaleActivity
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> delete(@PathVariable("id")Long id, HttpServletRequest request) throws Exception{
		return super.delete(id, request);
	}
	
	@Override
	protected BaseService getService() {
		return presaleActivityService;
	}

	@Override
	protected boolean canDelete(BaseModel model) {
		Long currentUserId = AppHelper.getCurrentUserId();
		if(currentUserId != null && currentUserId.equals(model.getCreateBy())){
			return true;
		}
		return false;
	}
	
	// 创建
	private void actionBeforeValidate(HttpServletRequest request, BaseModel model, ModelMap modelMap) throws Exception{
		OrgUser user = AppHelper.getCurrentUser();
		PresaleActivity activity = (PresaleActivity)model;
		activity.setOrgId(user.getOrgId());
		activity.setCreateBy(user.getId());
		
		if(StringUtils.isNotBlank(request.getParameter("beginDate"))){
			activity.setBeginTime(DateUtils.datetimeString2Date(request.getParameter("beginDate") + " 00:00:00"));
		}
		if(StringUtils.isNotBlank(request.getParameter("endDate"))){
			activity.setEndTime(DateUtils.datetimeString2Date(request.getParameter("endDate") + " 23:59:59"));			
		}
	}
	// 更新
	@Override
	protected void beforeUpdate(HttpServletRequest request, BaseModel model, ModelMap modelMap) throws Exception{
		PresaleActivity newModel = ((PresaleActivity)model);
		PresaleActivity oldModel = (PresaleActivity)presaleActivityService.getModel(model.getId());
		String amountStr = request.getParameter("amount");
		if(StringUtils.isNotBlank(amountStr)){
			// 更新剩余数量
			int newAmount = Integer.parseInt(amountStr);
			int remaining = oldModel.getRemaining() + (newAmount - oldModel.getAmount().intValue());
			if(remaining < 0){
				remaining = 0;
			}
			newModel.setRemaining(remaining);
		}
		if(StringUtils.isNotBlank(request.getParameter("beginDate"))){
			newModel.setBeginTime(DateUtils.datetimeString2Date(request.getParameter("beginDate") + " 00:00:00"));
		}
		if(StringUtils.isNotBlank(request.getParameter("endDate"))){
			newModel.setEndTime(DateUtils.datetimeString2Date(request.getParameter("endDate") + " 23:59:59"));			
		}
	}
}
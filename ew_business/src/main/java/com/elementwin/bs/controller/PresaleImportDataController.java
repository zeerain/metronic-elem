package com.elementwin.bs.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.elementwin.api.config.Cons;
import com.elementwin.api.utils.DateUtils;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.PresaleImport;
import com.elementwin.bs.service.FileService;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.PresaleImportDataService;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.common.model.PresaleImportData;

import dibo.framework.service.BaseService;
import dibo.framework.utils.V;

/***
 * 数据导入相关操作Controller
 * @author Yaojf@dibo.ltd
 * @version v1.0, 2017-3-6
 * Copyright 2016 www.Dibo.ltd
 */
@Controller
@RequestMapping("/presaleImportData")
public class PresaleImportDataController extends BaseCRUDController {
	private static Logger logger = Logger.getLogger(PresaleImportDataController.class);
	
	@Autowired
	private PresaleImportDataService presaleImportDataService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private PotentialCustomerService potentialCustomerService;
	
	@Autowired
	private PresaleImportController presaleImportController;
	
	@Override
	protected String getViewPrefix() {
		return "presaleImportData";
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
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", user.getOrgId());
		criteria.put("create_by", user.getId());
		criteria.put("register_date", DateUtils.getDate(new Date()));
		
		modelMap.addAttribute("criteria", criteria);
		
		modelMap.put("customerLevelList", Cons.getCustomerLevelList());

		return super.indexPaging(pageIndex, request, modelMap);
	}
	
	/***
	 * 创建的后台处理
	 * @param PresaleImport
	 * @param result
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(HttpServletRequest request, ModelMap modelMap) throws Exception {  
		OrgUser user = AppHelper.getCurrentUser();
		
		PresaleImport presaleImport = new PresaleImport();
		presaleImport.setOrgId(user.getOrgId());
		presaleImport.setId(0L);
		
		// 上传数据
		List<PresaleImportData> modelList = new ArrayList<PresaleImportData>();
		
		PresaleImportData model = new PresaleImportData();
    	model.setRowIndex(1);
    	model.setSalespersonName(request.getParameter("salespersonName"));
    	model.setCustomerName(request.getParameter("customerName"));
    	model.setCustomerPhone(request.getParameter("customerPhone"));
    	model.setCustomerLevel(request.getParameter("customerLevel"));
    	model.setIntentCarModel(request.getParameter("intentCarModel"));
    	model.setRegisterDate(DateUtils.getDate(new Date()));
    	model.setComment(request.getParameter("comment"));	        		
    	model.setNextHandleTime(DateUtils.datetimeString2Date(DateUtils.getDate(new Date()) + " 23:59"));	        	
    	modelList.add(model);
    	
		String redirectUrl = presaleImportController.validateAndSaveData(presaleImport, modelList, request, modelMap, false, true);
		if(V.isNotEmpty(redirectUrl)){
			return redirectUrl;
		}
		
		return "redirect:/presaleImportData/index";
    }
	
	@Override
	protected BaseService getService() {
		return presaleImportDataService;
	}
}
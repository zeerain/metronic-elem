package com.elementwin.bs.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dibo.framework.service.BaseService;
import com.elementwin.api.config.Cons;
import com.elementwin.common.model.MetadataItem;
import com.elementwin.bs.model.OrgService;
import com.elementwin.bs.service.MetadataItemService;
import com.elementwin.bs.service.OrgServiceService;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.bs.utils.AppHelper;

/***
 * Copyright 2016 www.Dibo.ltd
 * 客户相关操作Controller
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
@Controller
@RequestMapping("/organization")
public class OrganizationController extends BaseCRUDController {
	private static Logger logger = Logger.getLogger(OrganizationController.class);
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private OrgServiceService orgServiceService;
	
	@Autowired
	private MetadataItemService metadataItemService;
	
	@Override
	protected String getViewPrefix() {
		return "organization";
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
	 * 显示查看详细页面
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/view", method = RequestMethod.GET)
    public String viewPage(HttpServletRequest request, ModelMap modelMap) throws Exception {  
		Long orgId = AppHelper.getCurrentUser().getOrgId();
		
		List<MetadataItem> services = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.SERVICE);
		modelMap.put("services", services);
		
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", orgId);
		List<OrgService> orgServices = (List<OrgService>) orgServiceService.getModelList(criteria);
		modelMap.put("orgServices", orgServices);
		
		return super.viewPage(orgId, request, modelMap);
    }
	
	@Override
	protected BaseService getService() {
		return organizationService;
	}
}
package com.elementwin.bs.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.PresaleTaskService;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.bs.utils.StatisticHelper;

/***
 * Copyright 2016 www.Dibo.ltd
 * 回访统计相关操作Controller
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/09/13
 */
@Controller
@RequestMapping("/presaleStatistical")
public class PresaleStatisticalController extends BaseController {
	private static Logger logger = Logger.getLogger(PresaleStatisticalController.class);
	
	@Autowired
	private PotentialCustomerService potentialCustomerService;
	
	@Autowired
	private PresaleTaskService presaleTaskService;

	@Autowired
	private OrganizationService organizationService;

	@Override
	protected String getViewPrefix() {
		return "presaleStatistical";
	}
	
	/***
	 * 根路径/
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(HttpServletRequest request, ModelMap modelMap) throws Exception {	
		OrgUser orgUser = AppHelper.getCurrentUser();
		
		AppHelper.bindSubCompany(request, organizationService, orgUser, modelMap);
		
		Map<String, Object> criteria = (Map<String, Object>) modelMap.get("criteria");
		Long orgId = (Long) criteria.get("org_id");
		criteria.put("from_org_id", orgId);
		if(!orgUser.isGroupUser() && !orgUser.isManager()){
			criteria.put("from_sa_id", orgUser.getId());
		}
		modelMap.addAttribute("criteria", criteria);
		
		// 构建统计数据
		StatisticHelper.buildPresaleStatisticData(request, modelMap, potentialCustomerService, presaleTaskService);
		
		return view(request, modelMap, "index");
	}
	
}
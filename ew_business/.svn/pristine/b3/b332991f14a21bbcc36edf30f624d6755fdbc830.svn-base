package com.elementwin.bs.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.bs.service.RevisitImportService;
import com.elementwin.bs.service.RevisitStatisticalService;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.bs.utils.StatisticHelper;

/***
 * Copyright 2016 www.Dibo.ltd
 * 回访统计相关操作Controller
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/09/13
 */
@Controller
@RequestMapping("/revisitStatistical")
public class RevisitStatisticalController extends BaseController {
	private static Logger logger = Logger.getLogger(RevisitStatisticalController.class);
	
	@Autowired
	private RevisitStatisticalService revisitStatisticalService;
	
	@Autowired
	private RevisitImportService revisitImportService;

	@Autowired
	private OrganizationService organizationService;

	@Override
	protected String getViewPrefix() {
		return "revisitStatistical";
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
		
		StatisticHelper.buildStatisticData(request, modelMap, revisitStatisticalService, revisitImportService);
		
		return view(request, modelMap, "index");
	}
	
}
package com.elementwin.bs.controller.wx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import com.elementwin.api.config.Cons;
import com.elementwin.api.utils.WeChatHelper;
import com.elementwin.common.model.Organization;
import com.elementwin.bs.model.AudioMsg;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.common.model.WechatException;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.bs.controller.BaseController;

import dibo.framework.utils.V;

/****
 * 微信端改版
 * @author Mazc@dibo.ltd
 * @version 2016年12月28日
 * Copyright 2016 www.dibo.ltd
 */
@Controller
public class BaseWxController extends BaseController{
	private static Logger logger = Logger.getLogger(BaseWxController.class);
	
	@Autowired
	protected OrgUserService orgUserService;
	
	@Autowired
	protected OrganizationService organizationService;

	@Override
	protected String getViewPrefix() {
		return "weixin";
	}
	
	/**
	 * 构建分页参数
	 * @param modelMap
	 * @param criteria
	 * @param totalCount
	 * @param pageIndex
	 */
	protected void addPagingAttribute(ModelMap modelMap, Map<String, Object> criteria, int totalCount, int pageIndex){
		modelMap.addAttribute("pagination", super.buildPagination(getViewPrefix() + "/list/", criteria, totalCount, pageIndex));
		//更新总页数
		int totalPages = (int)Math.ceil((float)totalCount/Cons.WECHAT_PAGE_SIZE);
		modelMap.addAttribute("totalPages", totalPages);
	}
	
	/***
	 * 保存录音文件
	 * @param audioMsg
	 * @return
	 */
	protected boolean createAudioMsg(AudioMsg audioMsg){
		return false;
	}
	
	/**
	 * 是否为有效用户
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected boolean checkUser(HttpServletRequest request) throws Exception{
		OrgUser user = getCurrentUser(request);
		if(user == null){
			logger.warn("未获取当前用户信息！");
			throw new WechatException("未获取到当前微信用户信息！");
		}
		
		return user != null;
	}
	
	/***
	 * 获取当前用户
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected OrgUser getCurrentUser(HttpServletRequest request) throws Exception{
		HttpSession session = request.getSession(false);
		if(session != null && session.getAttribute("user") != null){
			return (OrgUser) session.getAttribute("user");
		}
		
		// 获取UserID
		String userID = getWechatUserId(request); // 账号
		// 获取User
		OrgUser user = orgUserService.getUserByWeChat(userID);
		if (user == null) {
			throw new WechatException("您暂时无法使用该应用，请检查系统中的账号设置！");
		}
		session.setAttribute("user", user);
		
		//TODO 判断是否有访问权限，暂时允许全部user访问
		
		return user;
	}
	
	/***
	 * 获取当前的OrgId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected Long getCurrentOrgId(HttpServletRequest request, ModelMap modelMap) throws Exception{
		List<Organization> orgList = (List<Organization>) organizationService.getModelList(null);
		// 添加当前参数
		OrgUser user = getCurrentUser(request);
		AppHelper.bindSubCompany(request, organizationService, user, modelMap);
		modelMap.addAttribute("orgList", modelMap.get("subCompanyList"));
		
		// 获取Session
		HttpSession session = request.getSession(true);
		// 从request获取org_id
		if(request.getParameter("org_id") != null){
			Long org_id = Long.parseLong(request.getParameter("org_id"));
			session.setAttribute("org_id", org_id);
			
			return org_id;
		}
		// 从session获取orgid
		else if(session.getAttribute("org_id") != null) {
			return (Long) session.getAttribute("org_id");
		}
		else if(V.isNotEmpty(orgList)){
			return orgList.get(0).getId();
		}
		return null;
	}
	
	/***
	 * 获取微信UserID
	 * @param request
	 * @param agentId
	 * @return
	 * @throws Exception
	 */
	private String getWechatUserId(HttpServletRequest request, String... agentIdParam) throws Exception{
		// 获取微信企业号内员工微信号
		String userID = null; // 账号
		// 获取Session
		HttpSession session = request.getSession(true);
		logger.debug("session=" + session.getId());
		if(session.getAttribute("userID") != null) {
			userID = (String) session.getAttribute("userID");
			logger.info("[WeChat]获取session中的userID: " + userID);
		}
		// 获取UserId
		if(V.isEmpty(userID)){
			String code = request.getParameter("code");
			if(StringUtils.isBlank(code)){
				logger.error("[WeChat]获取企业号code失败");
				throw new WechatException("获取企业号code失败！");
			}
			// 
			logger.info("[WeChat]获取企业号code: " + code);
			if(!"authdeny".equals(code)) {
				String agentId = null;
				if(agentIdParam != null){
					agentId = agentIdParam[0];
				}
				else{
					agentId = (String) session.getAttribute("agentId");					
				}
		    	userID = WeChatHelper.getUserID(code, agentId);
		        logger.info("[WeChat]保存到session中的userID: " + userID);
		    }
		    if(StringUtils.isBlank(userID)) {
		    	throw new WechatException("获取微信用户userID失败！");
		    }
		    session.setAttribute("userID", userID);
		}
		return userID;
	}

	/***
	 * 获取有效用户数量
	 * @param user
	 * @return
	 * @throws Exception
	 */
	protected int getActiveOrgUserCount(OrgUser user) throws Exception{
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", user.getOrgId());
		criteria.put("enabled", true);
		int count = orgUserService.getModelListCount(criteria);
		
		return count;
	}
}
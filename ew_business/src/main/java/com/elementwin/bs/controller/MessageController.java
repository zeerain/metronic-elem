package com.elementwin.bs.controller;

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
import com.elementwin.bs.security.PermissionException;
import dibo.framework.model.BaseModel;
import com.elementwin.bs.model.Notification;
import com.elementwin.bs.model.OrgUser;
import dibo.framework.service.BaseService;
import com.elementwin.bs.service.NotificationService;
import com.elementwin.bs.utils.AppHelper;

/***
 * Copyright 2016 www.Dibo.ltd
 * 消息通知相关操作Controller
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/30
 */
@Controller
@RequestMapping("/message")
public class MessageController extends BaseCRUDController {
	private static Logger logger = Logger.getLogger(MessageController.class);
	
	@Autowired
	private NotificationService notificationService;
	
	@Override
	protected String getViewPrefix() {
		return "message";
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
		Map<String, Object> criteria = super.buildQueryCriteria(request, pageIndex);
		
		OrgUser user = AppHelper.getCurrentUser();
		criteria.put("userType", OrgUser.class.getSimpleName());
		criteria.put("userId", user.getId());
		criteria.put("scope", Cons.SYSTEMS[1]);
		criteria.put("org_id", user.getOrgId());
		criteria.put("org_role_id", user.getOrgRoleItemId());
		
		modelMap.addAttribute("criteria", criteria);
		
		// 获取记录总数，用于前端显示分页
		int totalCount = getService().getModelListCount(criteria);
		
		// 加载当前页
		List<? extends BaseModel> modelList = getService().getModelList(criteria, pageIndex);
		modelMap.addAttribute("modelList", modelList);
		
		List<Notification> unreadModelList = notificationService.getUnreadNotificationList(criteria);
		modelMap.addAttribute("unreadModelList", unreadModelList);
		
		// 构建分页信息
		criteria.clear();
		modelMap.addAttribute("pagination", AppHelper.buildPagination(getViewPrefix()+"/index", criteria, totalCount, pageIndex));
		
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
		OrgUser user = AppHelper.getCurrentUser();
		Notification notofication = (Notification) notificationService.getModel(id);
		if(!notificationService.isUserCanRead(user, notofication)){
			logger.warn("用户试图访问非授权的页面:" + request.getRequestURI());
			throw new PermissionException("用户试图访问非授权的页面:" + request.getRequestURI());
		}
		// 
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("userType", OrgUser.class.getSimpleName());
		criteria.put("userId", user.getId());
		criteria.put("scope", Cons.SYSTEMS[1]);
		criteria.put("org_id", user.getOrgId());
		criteria.put("org_role_id", user.getOrgRoleItemId());
		List<Notification> notifications = notificationService.getUnreadNotificationList(criteria);
		boolean isUnread = false;
		if(notifications != null){
			for(Notification n : notifications){
				if(n.getId().equals(id)){
					isUnread = true;
					break;
				}
			}
		}
		if(isUnread){
			notificationService.createNotificationRead(OrgUser.class.getSimpleName(), user.getId(), id);
		}
		modelMap.put("isUnread", isUnread);
		
		
		return super.viewPage(id, request, modelMap);
    }
	
	@Override
	protected BaseService getService() {
		return notificationService;
	}
}
package com.elementwin.bs.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.bs.model.Notification;
import com.elementwin.bs.service.NotificationService;

import com.elementwin.bs.service.FileService;

/***
 * Controller的父类
 * @author Mazc@dibo.ltd
 */
@Controller
public abstract class BaseController extends dibo.framework.controller.BaseController{
	private static Logger logger = Logger.getLogger(BaseController.class);
	
	@Autowired
	private FileService fileService;

	@Autowired
	private NotificationService notificationService;
	
	/***
	 * 获取view视图页面的path前缀（文件夹地址）
	 * @return
	 */
	protected abstract String getViewPrefix();

	/***
	 * view路径+文件名
	 * @param fileName
	 * @return
	 */
	public String view(HttpServletRequest request, ModelMap model, String fileName){
		// 更新缓存
		HttpSession session = request.getSession(false);
		if(session != null){
			OrgUser user = AppHelper.getCurrentUser();
			if(user != null){
				if(session.getAttribute("user") == null){
					session.setAttribute("user", user);					
				}
				// 更新未读消息
				Map<String, Object> criteria = new HashMap<String, Object>();
				criteria.put("userType", OrgUser.class.getSimpleName());
				criteria.put("userId", user.getId());				
				OrgUser sessionUser = (OrgUser) session.getAttribute("user");
				try {
					List<Notification> notifications = notificationService.getUnreadNotificationList(criteria);
					sessionUser.setNotifications(notifications);
					session.setAttribute("user", sessionUser);			
				}
				catch (Exception e) {
					logger.error("更新消息通知异常:", e);
				}
			}
		}
		// 绑定操作结果
		return super.view(request, model, fileName);
	}
	
	/***
	 * 添加OrgId参数到criteria
	 * @param modelMap
	 */
	public void buildCriteriaWithOrgId(ModelMap modelMap){
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", AppHelper.getCurrentUser().getOrgId());
		modelMap.addAttribute("criteria", criteria);
	}

	/***
	 * frontendview路径+文件名 (微信端访问)
	 * @param fileName
	 * @return
	 */
	protected String wechatView(HttpServletRequest request, ModelMap model, String fileName){
		// 更新缓存
		HttpSession session = request.getSession(false);
		if(session != null && session.getAttribute("user") == null){
			OrgUser user = AppHelper.getCurrentUser();
			if(user != null){
				session.setAttribute("user", user);
			}
		}
		// 绑定操作结果
		return super.view(request, model, fileName);
	}
	
	/***
	 * 添加OrgId参数到criteria(微信端)
	 * @param modelMap
	 */
	public void buildCriteriaWithOrgIdWeChat(HttpServletRequest request, ModelMap modelMap){
		OrgUser orgUser = null;
		Map<String, Object> criteria = new HashMap<String, Object>();
		
		HttpSession session = request.getSession(false);
		if(session != null){
			if (session.getAttribute("orgUser") != null) {
				orgUser = (OrgUser) session.getAttribute("orgUser");
				criteria.put("org_id", orgUser.getOrgId());
				criteria.put("user_id", orgUser.getId());
				criteria.put("customer_manager", orgUser.getRealname());
			}
		}
		
		modelMap.addAttribute("criteria", criteria);
	}

}
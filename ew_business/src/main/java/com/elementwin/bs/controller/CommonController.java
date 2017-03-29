package com.elementwin.bs.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.elementwin.bs.config.Cons;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.security.PermissionException;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.service.PresaleTaskService;
import com.elementwin.bs.utils.AppHelper;

import dibo.framework.utils.V;

/***
 * 基础功能Controller
 * @author Mazc@dibo.ltd
 */
@Controller("commonController")
public class CommonController extends BaseController{
	private static Logger logger = Logger.getLogger(CommonController.class);
	
	@Autowired
	public OrgUserService orgUserService;
	
	@Autowired
	public PresaleTaskService presaleTaskService;
	
	@Override
	protected String getViewPrefix(){
		return "common";
	}
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String welcome(HttpServletRequest request, ModelMap model) throws Exception{
		OrgUser user = AppHelper.getCurrentUser();
		if(user == null){
			return "redirect:/login";
		}
		
		//user放入Session
		HttpSession session = request.getSession(false);
		if(session != null){
			String service = request.getParameter("svc");
			if(StringUtils.isNotBlank(service)){
				user.switchServiceMenus(Long.parseLong(service));
				session.setAttribute("user", user);
			}
		}
		return view(request, model, "dashboard");
	}

	/**
	 * 显示登录注册页面
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String manageLogin(HttpServletRequest request, ModelMap model) throws Exception{
		
		beforeLoginAction(request, model);

		return view(request, model, "login");
	}
	
	/***
	 * 登录之前的验证
	 * @param request
	 * @param model
	 * @throws Exception
	 */
	private void beforeLoginAction(HttpServletRequest request, ModelMap model) throws Exception{
		// 当前用户登录的状态
		HttpSession session = request.getSession(true);
		if(session != null){
			Integer count = (Integer)session.getAttribute("loginFailed");
			if(count != null){
				model.addAttribute("loginFailed", count);
				//model.addAttribute("captcha", AppHelper.getCachedCaptcha());
				if(count.intValue() >= Cons.MAX_LOGIN_FAILED_TIMES * 2){					
					logger.warn("登录失败次数超出最大限制次数!");
					model.addAttribute("attemptExceed", true);
				}
			}
		}
		
		// 记录之前访问的页面
		String referer = request.getHeader("Referer");
		if(referer != null && UrlUtils.isValidRedirectUrl(referer)){
			String requestURL = request.getRequestURL().toString();
			String requestURI = request.getRequestURI().toString();
			String urlPrefix = requestURL.replace(requestURI, "")+request.getContextPath();
			if(referer.startsWith(urlPrefix) && !referer.contains(Cons.URL_LOGIN)){
				model.addAttribute("referer", referer.substring(urlPrefix.length()));
			}
		}
	}
	
	/**
	 * 系统验证码
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/vc", method=RequestMethod.GET)
	public void verificationCode(HttpServletRequest request, HttpServletResponse response){
		String token = "123456"; //TODO generate token
	    HttpSession session = request.getSession(false);
	    if(session != null){
	    	session.setAttribute("vc", token);
	    }
	}
	
	/**
	 * 校验系统验证码
	 * @param request
	 * @param response
	 */
	/*
	@RequestMapping(value="/check/vc", method=RequestMethod.POST)
	@ResponseBody
	public boolean checkVcode(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession(false);
		boolean result = session.getAttribute("vc") == null || ((String) session.getAttribute("vc")).equalsIgnoreCase(request.getParameter("vc"));
		
		return result;
	}*/
	
	/***
	 * Keep Alive action
	 */
	@ResponseBody
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public String ping(HttpServletRequest request, ModelMap modelMap) throws Exception {
		return null;
	}
	
	/**
	 * 错误信息页面
	 * @return
	 */
	@RequestMapping(value="/error", method=RequestMethod.GET)
	public String error(HttpServletRequest request, ModelMap model){
		String errorMsg = (String) request.getAttribute("javax.servlet.error.message");
		Object exception = request.getAttribute("javax.servlet.error.exception");
		String requestUrl = (String) request.getAttribute("javax.servlet.error.request_uri");
		StringBuilder sb = new StringBuilder();
		sb.append("Error occured : ").append("status_code=").append(request.getAttribute("javax.servlet.error.status_code"))
		.append(";message=").append(errorMsg)
        .append(";exception=").append(exception)
		.append(";request_uri=").append(requestUrl);
		
		logger.warn(sb.toString());
		// 如果是来自微信的，跳转到微信异常页面
		String referer = request.getHeader("referer");
		if((V.isNotEmpty(referer) && referer.contains("/wechat/")) || requestUrl.contains("/wechat/")){
			model.addAttribute("errorMsg", errorMsg);
			return "wechat/common/error";
		}
		
		if(exception instanceof PermissionException){
			return view(request, model, "403");			
		}
		return view(request, model, "error");			
	}

}
package com.elementwin.bs.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.elementwin.common.model.WechatException;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.security.PermissionException;
import com.elementwin.bs.utils.AppHelper;

@ControllerAdvice
public class DefaultExceptionController {
	private static Logger logger = Logger.getLogger(DefaultExceptionController.class);
	
	/*
	@ExceptionHandler(Exception.class)
    public String handleException(HttpServletRequest request, Exception ex) {
		logger.error("发生异常:", ex);
	}*/
	
	@ExceptionHandler(Exception.class)
	public String handleException(HttpServletRequest request, Exception ex) {
		// 记录日志
		logger.error("发生异常:", ex);
		
		// 如果是来自微信的，跳转到微信异常页面
		String requestURI = request.getRequestURI().replace(request.getContextPath(), "");
		if(requestURI.startsWith("/wechat") || requestURI.startsWith("/weixin")){
			return handleWechatException(request, new WechatException(ex));
		}
		
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if(statusCode != null){
			String referer = request.getHeader("referer");
			logger.debug("ERROR "+statusCode+": FROM URL:" + referer + " => " + request.getRequestURL().toString());
		}
		if(ex instanceof PermissionException){
			return viewPage(request, "common/403");			
		}
		return viewPage(request, "common/error");	
	}
	
	/**
	 * 默认微信端异常处理
	 * @param request
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(WechatException.class)
    public String handleWechatException(HttpServletRequest request, WechatException ex) {
		logger.error("微信端发生异常:", ex);
		
		String requestUrl = (String) request.getAttribute("javax.servlet.error.request_uri");
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		Object exception = request.getAttribute("javax.servlet.error.exception");
		
		StringBuilder sb = new StringBuilder();
		sb.append("request_url: [").append(requestUrl).append("] Error occured : ").append("status_code=").append(statusCode)
		.append(";message=").append(request.getAttribute("javax.servlet.error.message")).append(";exception=").append(exception);
		logger.warn(sb.toString());
		
		// 将报错信息填充至错误页面
		request.setAttribute("errMsg", "您访问的页面不存在或发生错误！");
		
		return viewPage(request, "wechat/common/error");
	}
	
	/**
	 * 页面view
	 * @param request
	 * @param view
	 * @return
	 */
	private String viewPage(HttpServletRequest request, String view){
		// 更新缓存
		HttpSession session = request.getSession(false);
		if(session != null){
			OrgUser user = AppHelper.getCurrentUser();
			if(user != null){
				if(session.getAttribute("user") == null){
					session.setAttribute("user", user);					
				}
			}
		}
		// 绑定操作结果
		return view;
	}
}
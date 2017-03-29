package com.elementwin.bs.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.elementwin.api.config.SystemConfig;
import com.elementwin.bs.config.Cons;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.common.model.WechatException;

public class SecurityInterceptor implements HandlerInterceptor {
	Logger logger = Logger.getLogger(SecurityInterceptor.class);

	private String[] WECHAT_URLS = SystemConfig.getWechatUrls();
	
	private String[] ignoreUrls = new String[]{
		Cons.URL_LOGIN, Cons.URL_CHANGEPWD, Cons.URL_PROFILE, Cons.URL_MESSAGE, Cons.URL_FILE
	};
	
	// debug
	private Long times = 0L; 
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		if(logger.isDebugEnabled()){
			times = (new Date()).getTime();			
		}
		logger.info("referer=" + request.getHeader("referer"));
		
		// 判断是否有权限访问URL
		String requestURI = request.getRequestURI().replace(request.getContextPath(), "");
		
		// 判断是否合法来源 (微信链接只允许微信浏览器访问)
		boolean accessWechat = false;
		if(WECHAT_URLS != null && WECHAT_URLS.length > 0){ 
			for(String url : WECHAT_URLS){
				if(requestURI.startsWith(url)){
					if(!AppHelper.isFromWechat(request)){
						throw new WechatException("请在微信中打开此链接！");
					}
					accessWechat = true;
				}
			}
		}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler, ModelAndView mv) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception exception)
					throws Exception {
		// TODO 记录日志
		if(logger.isDebugEnabled()){
			long t = (new Date()).getTime() - times;
			logger.info("Request URL : " + request.getRequestURL() + " takes  [" +  t + "] ms");
			if(t > 3000){
				logger.warn("Request URL : " + request.getRequestURL() + " need to be optimized.");
			}
		}
	}
	
}

package com.elementwin.bs.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.elementwin.api.utils.ContextHelper;
import com.elementwin.bs.config.Cons;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.service.OrgUserService;

import dibo.framework.utils.V;

/****
 * 登录失败处理类
 * @author Mazc@dibo.ltd
 */
public class AuthenticationFailureHandler extends
		SimpleUrlAuthenticationFailureHandler {
	private static Logger logger = Logger.getLogger(AuthenticationFailureHandler.class);
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
		
		// 尝试失败次数+1
		HttpSession session = request.getSession(false);
		if(session != null){
			Integer count = (Integer)session.getAttribute("loginFailed");
			if(count == null){
				count = 0;
			}
			session.setAttribute("loginFailed", ++count);
			
			logger.warn("用户登录失败，用户名或密码错误！"+count);
		}
		// 加载数据
		String username = request.getParameter("username");
		if(V.isNotEmpty(username)){
			OrgUserService orgUserService = (OrgUserService) ContextHelper.getBean("orgUserService");
			if(orgUserService != null){
				OrgUser user = null;
				try {
					user = orgUserService.getUser(username);
				}
				catch (Exception e) {
					logger.error("获取用户认证信息失败：", e);
				}
				// 用户存在，失败次数+1
				if(user != null){
					if(!user.isEnabled()){
						session.setAttribute("locked", "true");
					}
					else{
						session.removeAttribute("locked");
						
						int failedCount = user.getFailedLogin() + 1;
						boolean isSameDay = true;
						if(user.getLastLoginTime() != null){
							isSameDay = DateUtils.isSameDay(user.getLastLoginTime(), new Date());
						}
						if(!isSameDay){
							failedCount = 1;
						}
						user.setFailedLogin(failedCount);
						if(failedCount > Cons.MAX_LOGIN_FAILED_TIMES){
							user.setEnabled(false);
						}
						user.setLastLoginIp(request.getRemoteAddr());
						orgUserService.updateLoginInfo(user);						
					}
				}
			}
		}
		super.onAuthenticationFailure(request, response, exception);
	}
}

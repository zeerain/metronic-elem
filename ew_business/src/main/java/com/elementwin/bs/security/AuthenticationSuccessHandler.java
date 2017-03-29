package com.elementwin.bs.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.elementwin.api.utils.ContextHelper;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.utils.AppHelper;

/***
 * 登录成功处理类
 * @author Mazc@dibo.ltd
 */
public class AuthenticationSuccessHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {
	private static Logger logger = Logger.getLogger(AuthenticationSuccessHandler.class);
	
	public String targetUrlParameter = "target-url";
	
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
		// 从缓存中得到当前用户信息，将必要信息缓存到Session
		HttpSession session = request.getSession(false);
		if(session != null){
			session.removeAttribute("loginFailed");
			OrgUser user = AppHelper.getCurrentUser();
			if(user != null){
				if(user.isEnabled()){
					session.removeAttribute("locked");
				}
				// 需要缓存到Session中的值
				session.setAttribute("user", user);
				logger.info("用户"+user.getUsername()+"登录成功！");
				// 重置登录失败信息
				OrgUserService orgUserService = (OrgUserService) ContextHelper.getBean("orgUserService");
				if(orgUserService != null){
					user.setFailedLogin(0);
					user.setLastLoginIp(request.getRemoteAddr());
					orgUserService.updateLoginInfo(user);
				}
			}
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
    }
	
}
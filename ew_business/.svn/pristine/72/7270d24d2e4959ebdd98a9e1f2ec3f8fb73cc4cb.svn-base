package com.elementwin.bs.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.web.DefaultRedirectStrategy;

import com.elementwin.bs.config.Cons;

/***
 * 安全的RedirectStrategy，判断是否需要跳转到登录前的页面
 * @author Mazc@dibo.ltd
 */
public class AuthSuccessRedirectStrategy extends DefaultRedirectStrategy {

	@Override
	public void sendRedirect(HttpServletRequest request, HttpServletResponse response,
			String url) throws IOException {
		HttpSession session = request.getSession(false);
		if(session != null && session.getAttribute("vc") != null){
			// 验证码未验证通过
			//super.sendRedirect(request, response, "/login?error=vc");
			//return;
		}
		if(url != null && url.contains(Cons.URL_LOGIN)){
			System.out.println("LOGIN_WARN ***: redirect to login page again.");
		}
		// 跳转
		super.sendRedirect(request, response, url);			
	}
}
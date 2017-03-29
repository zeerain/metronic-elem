package com.elementwin.bs.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.elementwin.bs.security.AuthSuccessRedirectStrategy;
import com.elementwin.bs.security.AuthenticationFailureHandler;
import com.elementwin.bs.security.AuthenticationSuccessHandler;
import com.elementwin.bs.security.UserDetailsServiceImpl;

/****
 * Spring Security配置文件
 * @author Mazc@dibo.ltd
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
	// 登录成功后是否跳转到之前的referer URL
	private boolean enableRedirect2RefererStrategy = true;
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	UserDetailsServiceImpl userDetailsService;
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		//用户认证实现类 
		auth.userDetailsService(userDetailsService)
			.passwordEncoder(new Md5PasswordEncoder());
	}

	@Override  
    public void configure(WebSecurity web) throws Exception {  
        web.ignoring().antMatchers("/res/**", "/img/**");
    }
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		// 登录成功handler
		AuthenticationSuccessHandler successHandler = new AuthenticationSuccessHandler();
		successHandler.setTargetUrlParameter("target-url");
		successHandler.setDefaultTargetUrl(Cons.URL_WELCOME);
		// 是否跳转到登录前的页面
		if(enableRedirect2RefererStrategy){
			successHandler.setRedirectStrategy(new AuthSuccessRedirectStrategy());			
		}

		AuthenticationFailureHandler failureHandler = new AuthenticationFailureHandler();
		failureHandler.setAllowSessionCreation(false);
		failureHandler.setDefaultFailureUrl(Cons.URL_LOGIN+"?error=auth");
		
		http
            .authorizeRequests()
            	.antMatchers(Cons.URL_LOGIN, "/vc", "/error", "/wechat/**", "/weixin/**").anonymous()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage(Cons.URL_LOGIN).loginProcessingUrl("/dologon").defaultSuccessUrl(Cons.URL_WELCOME)
                .successHandler(successHandler).failureHandler(failureHandler)
                .and()
            .logout().logoutUrl("/logout").logoutSuccessUrl(Cons.URL_LOGIN).invalidateHttpSession(true)
                .and()
            .sessionManagement().sessionAuthenticationErrorUrl(Cons.URL_LOGIN+"?error=expired").maximumSessions(1).expiredUrl(Cons.URL_LOGIN+"?error=expired");
		
		http.rememberMe().rememberMeParameter("remember_me").userDetailsService(userDetailsService)
		.tokenRepository(persistentTokenRepository()).tokenValiditySeconds(1209600);
		
		//默认禁用CSRF防范
		http.csrf().disable();
	}
	
	// Remember me 实现类
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setDataSource(dataSource);
		return db;
	}
	
}
package com.elementwin.bs.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.elementwin.bs.model.OrgUser;
import com.elementwin.common.model.Organization;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.common.model.LogonUser;
import com.elementwin.bs.model.Notification;
import com.elementwin.bs.service.NotificationService;

/***
 * Spring Security安全验证的实现
 * @author Mazc@dibo.ltd
 */
@Component("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
	private static Logger logger = Logger.getLogger(UserDetailsServiceImpl.class);
	
	@Autowired
	private OrgUserService orgUserService;
	
	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private NotificationService notificationService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		OrgUser user = null;
		try {
			user = orgUserService.getUser(username);
		}
		catch (Exception e) {
			logger.error("获取用户认证信息失败：", e);
		}
		// 用户不存在
		if(user == null){
			String message = "不存在的用户:"+username;
			logger.warn(message);
			throw new UsernameNotFoundException(message);			
		}
		if(!user.isEnabled()){			
			String message = "您的账号已锁定，请联系管理员解锁！";
			logger.warn(message);
			throw new UsernameNotFoundException(message);			
		}
		
		try{
			Organization org = (Organization) organizationService.getModel(user.getOrgId());
			user.setOrganization(org);			
		}
		catch(Exception e){
			logger.error("获取单位信息异常:", e);
		}
		
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		if(user.getRole() != null){
			authorities.add(new SimpleGrantedAuthority(user.getRole().getRole()));
			// 设置用户可访问的菜单
			List<Long> roleIdList = new ArrayList<Long>();
			roleIdList.add(user.getRole().getId());
			// 设置客户可访问的服务
			try{
				orgUserService.initAuthedMenus(user);
			}
			catch(Exception e){
				logger.error("获取可访问菜单失败！", e);
			}
			
			// 设置用户未读的消息通知
			Map<String, Object> criteria = new HashMap<String, Object>();
			criteria.put("userType", OrgUser.class.getSimpleName());
			criteria.put("userId", user.getId());
			criteria.put("org_id", user.getOrgId());
			criteria.put("org_role_id", user.getOrgRoleItemId());
			try {
				List<Notification> notifications = notificationService.getUnreadNotificationList(criteria);
				user.setNotifications(notifications);
			}
			catch (Exception e) {
				logger.error("获取用户未读消息失败:", e);
			}
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("取到用户认证信息: " + user.toString());
		}
		
		return new LogonUser(user, authorities);
	}
}
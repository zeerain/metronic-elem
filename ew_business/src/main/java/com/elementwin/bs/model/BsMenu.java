package com.elementwin.bs.model;

import com.elementwin.common.model.BaseMenu;

/***
 * Menu-菜单
 * @author Mazc@dibo.ltd
 */
public class BsMenu extends BaseMenu{  

	private static final long serialVersionUID = 202L;

	// 菜单所属服务ID
	private Long serviceId;
	// 授权角色
	private String authRole;
	
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public String getAuthRole() {
		return authRole;
	}
	public void setAuthRole(String authRole) {
		this.authRole = authRole;
	}
	
}
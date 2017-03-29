package com.elementwin.bs.service;

/***
 * 角色权限相关操作Service
 * @author Mazc@dibo.ltd
 */
public interface RoleMenuService extends dibo.framework.service.BaseService{

	/***
	 * 删除某角色下的菜单权限
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean deleteRoleMenus(Long roleId) throws Exception;

}
package com.elementwin.bs.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.bs.service.RoleMenuService;
import com.elementwin.bs.service.mapper.RoleMenuMapper;

/***
 * 角色权限相关操作Service实现类
 * @author Mazc@dibo.ltd
 */
@Service("roleMenuService")
@Transactional
public class RoleMenuServiceImpl extends BaseServiceImpl implements RoleMenuService {
	private static Logger logger = Logger.getLogger(RoleMenuServiceImpl.class);
	
	@Autowired
	RoleMenuMapper roleMenuMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return roleMenuMapper;
	}
	
	@Override
	@Transactional
	public boolean deleteRoleMenus(Long roleId) throws Exception {
		return roleMenuMapper.deleteRoleMenus(roleId) > 0;
	}

}
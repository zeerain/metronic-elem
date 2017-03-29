package com.elementwin.bs.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.bs.service.RoleService;
import com.elementwin.bs.service.mapper.RoleMapper;

/***
 * 角色相关操作Service实现类
 * @author Mazc@dibo.ltd
 */
@Service("roleService")
@Transactional
public class RoleServiceImpl extends BaseServiceImpl implements RoleService {
	private static Logger logger = Logger.getLogger(RoleServiceImpl.class);
	
	@Autowired
	RoleMapper roleMapper;

	@Override
	protected BaseMapper getMapper() {
		return roleMapper;
	}
	
}
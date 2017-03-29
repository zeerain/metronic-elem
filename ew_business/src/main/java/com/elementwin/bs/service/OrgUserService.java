package com.elementwin.bs.service;

import java.util.List;
import java.util.Map;

import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.Role;

/***
 * Copyright 2016 www.Dibo.ltd
 * 企业员工相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
public interface OrgUserService extends dibo.framework.service.BaseService{
	
	/***
	 * 根据用户名查找用户
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public OrgUser getUser(String username) throws Exception;
	
	/**
	 * 修改密码
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 * @throws Exception
	 */
	public boolean updateUserPwd(Long userId, String oldPassword, String newPassword) throws Exception;

	/***
	 * 加载所有角色
	 * @return
	 * @throws Exception
	 */
	public List<Role> getAllRoles() throws Exception;
	
	/**
	 * 登录成功后更新log
	 * @param userId
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	public boolean updateLoginInfo(OrgUser user);

	/***
	 * 获取这个单位下的初始菜单：第一个服务对应的菜单
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public void initAuthedMenus(OrgUser user) throws Exception;

	/***
	 * 判断用户是否重复，是否可创建
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean isDuplicateUser(OrgUser user) throws Exception;
	
	/***
	 * 根据微信号查找用户
	 * @param wechat
	 * @return
	 * @throws Exception
	 */
	public OrgUser getUserByWeChat(String wechat) throws Exception;

	/***
	 * 获取员工上级
	 * @param orgId
	 * @param employeeName
	 * @return
	 * @throws Exception
	 */
	public OrgUser getManagerByEmployeeName(Long orgId, String employeeName) throws Exception;
	
	/***
	 * 根据员工号获取员工
	 * @param orgId
	 * @param managerNo
	 * @return
	 * @throws Exception
	 */
	public OrgUser getUserByEmployeeNo(Long orgId, String managerNo) throws Exception;
	
	/**
	 * 查询单位下用户姓名于对应的Id
	 * @param orgId
	 * @param realnames
	 * @return
	 * @throws Exception
	 */
	public Map<String, Long> getUserName2IdMap(Long orgId, List<String> realnames) throws Exception;
	
}
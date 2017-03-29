package com.elementwin.bs.service.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.Role;

/***
 * Copyright 2016 www.Dibo.ltd
 * 企业员工相关Mapper
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
public interface OrgUserMapper extends dibo.framework.service.mapper.BaseMapper{
	// 自定义接口
	public OrgUser getUserByUsername(String username) throws Exception;
	
	public OrgUser getUserByWeChat(String wechat) throws Exception;

	public int updateUserPwd(@Param("userId")Long userId, @Param("oldPassword")String oldPassword, @Param("newPassword")String newPassword) throws Exception;

	public List<Role> getAllRoles() throws Exception;

	public int updateLoginInfo(OrgUser user);

	public int findAnotherUser(OrgUser user) throws Exception;
	
	public OrgUser getManagerByEmployeeName(@Param("orgId")Long orgId, @Param("employeeName")String employeeName) throws Exception;
	
	public OrgUser getUserByEmployeeNo(@Param("orgId")Long orgId, @Param("employeeNo")String employeeNo) throws Exception;
	
	public List<OrgUser> getUsersByNames(@Param("orgId")Long orgId, @Param("nameList")List<String> nameList) throws Exception;
	
}
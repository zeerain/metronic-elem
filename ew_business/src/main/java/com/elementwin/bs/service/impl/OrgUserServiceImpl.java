package com.elementwin.bs.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.model.BaseModel;
import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.bs.config.Cons;
import com.elementwin.common.model.MetadataItem;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.Role;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.service.mapper.BsMenuMapper;
import com.elementwin.bs.service.mapper.MetadataItemMapper;
import com.elementwin.bs.service.mapper.OrgUserMapper;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.common.model.BaseMenu;

/***
 * Copyright 2016 www.Dibo.ltd
 * 企业员工相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
@Service("orgUserService")
@Transactional
public class OrgUserServiceImpl extends BaseServiceImpl implements OrgUserService {
	private static Logger logger = Logger.getLogger(OrgUserServiceImpl.class);
	
	@Autowired
	OrgUserMapper orgUserMapper;
	
	@Autowired
	MetadataItemMapper metadataItemMapper;
	
	@Autowired
	BsMenuMapper bsMenuMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return orgUserMapper;
	}
	
	@Override
	public int getModelListCount(Map<String, Object> criteria) throws Exception {
		if(criteria == null){
			criteria = new HashMap<String, Object>();
		}
		if(!criteria.containsKey("org_id")){
			criteria.put("org_id", AppHelper.getCurrentUser().getOrgId());			
		}
		return getMapper().getListCount(criteria);
	}

	@Override
	public List<? extends BaseModel> getModelList(
			Map<String, Object> criteria, int... page) throws Exception {
		if(criteria == null){
			criteria = new HashMap<String, Object>();
		}
		criteria.put("org_id", AppHelper.getCurrentUser().getOrgId());
		if(page != null && page.length > 0){
			criteria.put("begin", Cons.begin(page[0]));
			criteria.put("pageSize", Cons.PAGE_SIZE);			
		}
		return getMapper().getList(criteria);
	}

	@Override
	public List<? extends BaseModel> getLimitModelList(
			Map<String, Object> criteria, int count) throws Exception {
		if(criteria == null){
			criteria = new HashMap<String, Object>();
		}
		criteria.put("org_id", AppHelper.getCurrentUser().getOrgId());
		if(count > 0){
			criteria.remove("begin");
			criteria.remove("pageSize");
			criteria.put("limitCount", count);
		}
		return getMapper().getList(criteria);
	}
	
	@Override
	public OrgUser getUser(String username) throws Exception {
		return orgUserMapper.getUserByUsername(username);			
	}

	@Override
	@Transactional
	public boolean createModel(BaseModel model) throws Exception {
		OrgUser user = (OrgUser) model;
		user.setPassword(AppHelper.md5Encode(user.getPassword()));
		return orgUserMapper.create(user) > 0;
	}

	@Override
	@Transactional
	public boolean updateModel(BaseModel model) throws Exception {
		// 是否更新密码
		OrgUser baseUser = (OrgUser)model;
		if(baseUser.getPassword() != null && baseUser.getPassword().length() < 32){
			baseUser.setPassword(AppHelper.md5Encode(baseUser.getPassword()));
		}
		return orgUserMapper.update(baseUser) > 0;			
	}
	
	@Override
	@Transactional
	public boolean updateUserPwd(Long userId, String oldPassword, String newPassword) throws Exception{
		boolean result = orgUserMapper.updateUserPwd(userId, AppHelper.md5Encode(oldPassword), AppHelper.md5Encode(newPassword)) > 0;
		
		if(logger.isDebugEnabled()){
			logger.debug("updateUserPwd result is : " + result);
		}
		
		return result;
	}

	@Override
	public List<Role> getAllRoles() throws Exception {
		return orgUserMapper.getAllRoles();
	}

	@Override
	@Transactional
	public boolean updateLoginInfo(OrgUser user){
		return orgUserMapper.updateLoginInfo(user) > 0;
	}

	@Override
	public void initAuthedMenus(OrgUser user) throws Exception {
		List<MetadataItem> availableServices = metadataItemMapper.getAvailableServicesItems(user.getOrgId());
		List<Long> serviceIds = new ArrayList<Long>();
		serviceIds.add(0L);
		if(availableServices != null){
			for(MetadataItem item : availableServices){
				serviceIds.add(item.getId());
			}
			logger.info("availableServices="+availableServices.toString());
		}
		else{
			logger.warn("availableServices is null");			
		}
		
		List<String> authRoles = new ArrayList<String>();
		if(user.isAdmin()){
			authRoles.add("ADMIN");			
		}
		// 判断是否为集团用户，集团用户添加GROUP，其他用户ORGUSER
		if(user.isGroupUser()){
			authRoles.add("GROUP");			
		}
		else{
			authRoles.add("ORGUSER");			
		}
		authRoles.add("ALL");			
		List<BaseMenu> allMenus = bsMenuMapper.getServiceRelatedMenus(serviceIds, authRoles);
		
		user.setAuthServiceAndMenus(availableServices, allMenus);
	}

	@Override
	public boolean isDuplicateUser(OrgUser user) throws Exception {
		return orgUserMapper.findAnotherUser(user) > 0;
	}
	
	@Override
	public OrgUser getUserByWeChat(String wechat) throws Exception {
		return orgUserMapper.getUserByWeChat(wechat);			
	}
	
	@Override
	public OrgUser getManagerByEmployeeName(Long orgId, String employeeName)
			throws Exception {
		return orgUserMapper.getManagerByEmployeeName(orgId, employeeName);
	}

	@Override
	public OrgUser getUserByEmployeeNo(Long orgId, String managerNo)
			throws Exception {
		return orgUserMapper.getUserByEmployeeNo(orgId, managerNo);
	}

	@Override
	public Map<String, Long> getUserName2IdMap(Long orgId,
			List<String> realnames) throws Exception {
		Map<String, Long> map = new HashMap<String, Long>();
		List<OrgUser> users = orgUserMapper.getUsersByNames(orgId, realnames);
		if(users != null && !users.isEmpty()){
			for(OrgUser user : users){
				map.put(user.getRealname(), user.getId());
			}
		}
		return map;
	}

}
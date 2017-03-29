package com.elementwin.bs.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;

import com.elementwin.bs.config.Cons;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.common.model.Organization;
import com.elementwin.bs.security.PermissionException;
import com.elementwin.bs.service.OrganizationService;

import dibo.framework.model.BaseModel;
import dibo.framework.utils.V;

import com.elementwin.common.model.BaseUser;
import com.elementwin.common.model.LogonUser;

/***
 * 应用通用帮助类
 * @author Mazc@dibo.ltd
 */
public class AppHelper extends com.elementwin.api.utils.AppHelper{
	private static Logger logger = Logger.getLogger(AppHelper.class);
	
	/**
	 * 得到当前登录的用户名
	 * @return 
	 */
	public static OrgUser getCurrentUser(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth != null){
			Object principal = auth.getPrincipal();
			if(principal != null && principal instanceof UserDetails){
				BaseUser base =((LogonUser)principal).getUser();
				if(base != null){
					return (OrgUser) base;
				}
			}
		}
		return null;
	}

	/**
	 * 得到当前登录的用户id
	 * @return 
	 */
	public static Long getCurrentUserId(){
		OrgUser user = getCurrentUser();
		if(user != null){
			return user.getId();
		}
		if(logger.isDebugEnabled()){
			logger.warn("无法获取当前用户Id!");
		}
		return null;
	}
	
	/***
	 * 当前用户是否是管理员
	 * @return
	 */
	public static boolean isAdmin(){
		OrgUser user = getCurrentUser();
		return user!=null && user.getRoleId().equals(Cons.ROLE_ADMIN);
	}
	
	/***
	 * 构建分页信息
	 * @param totalCount
	 * @param totalCount2
	 * @param pageIndex
	 * @return
	 */
	public static Map<String, Object> buildPagination(String baseUrl, Map<String, Object> criteria, int totalCount, int currentPage) {
		return buildPagination(baseUrl, criteria, totalCount, currentPage, Cons.PAGE_SIZE);
	}
	
	/***
	 * 构建树形结构Model //TODO 递归实现3级
	 * @param allMenus
	 * @return
	 */
	public static List<? extends BaseModel> buildTreeStructureModels(List<? extends BaseModel> allModels){
		if(allModels == null || allModels.size() == 0){
			return null;
		}
		List<BaseModel> level1Models = new ArrayList<BaseModel>();
		Map<Long, List<BaseModel>> level2Map  = new HashMap<Long, List<BaseModel>>();
		Map<Long, List<BaseModel>> level3Map = new HashMap<Long, List<BaseModel>>();
		for(BaseModel m : allModels){
			if(m == null){
				logger.warn("检测到无效的model对象.");
				continue;
			}
			if(m.getParentId().equals(0L)){
				level1Models.add(m);
			}
			else if(m.getParentId() > 0 && m.getParentId() < 1000L){
				List<BaseModel> list = level2Map.get(m.getParentId());
				if(list == null){
					list = new ArrayList<BaseModel>();
				}
				list.add(m);
				level2Map.put(m.getParentId(), list);
			}
			else{
				List<BaseModel> list = level3Map.get(m.getParentId());
				if(list == null){
					list = new ArrayList<BaseModel>();
				}
				list.add(m);
				level3Map.put(m.getParentId(), list);
			}
		}
		if(!level2Map.isEmpty()){
			for(List<BaseModel> list : level2Map.values()){
				for(BaseModel model : list){
					model.setChildren(level3Map.get(model.getId()));
				}
			}
		}
		for(BaseModel m : level1Models){
			m.setChildren(level2Map.get(m.getId()));
		}
		
		return level1Models;
	}
	
	/***
	 * 检查是否有权限访问某单位的数据
	 * @param request
	 * @param organizationService
	 * @param user
	 * @param orgId
	 * @throws Exception
	 */
	public static void checkPermission(HttpServletRequest request, OrganizationService organizationService, OrgUser user, Long orgId) throws Exception{
		boolean hasPermission = false;
		List<Organization> subCompanyList = organizationService.getSubCompanyList(user);
		if(V.isNotEmpty(subCompanyList)){
			for(Organization org : subCompanyList){
				if(orgId.equals(org.getId())){
					hasPermission = true;
					break;
				}
			}
		}
		if(!hasPermission){
			String error = "用户["+user.getUsername()+"]试图访问非授权的页面:" + request.getRequestURI();
			logger.warn(error);			
			throw new PermissionException("您没有权限查看该数据！");
		}
	}
	
	/***
	 * 绑定子单位信息
	 * @param request
	 * @param organizationService
	 * @param orgUser
	 * @param modelMap
	 * @throws Exception
	 */
	public static void bindSubCompany(HttpServletRequest request, OrganizationService organizationService, OrgUser orgUser, ModelMap modelMap) throws Exception{
		Long orgId = orgUser.getOrgId();
		List<Organization> subCompanyList = null;
		if(orgUser.isGroupUser()){
			subCompanyList = organizationService.getSubCompanyList(orgUser);
			if(V.isNotEmpty(subCompanyList)){
				// 如果是集团用户，将单位设置为第一个子单位
				orgId = subCompanyList.get(0).getId();
				// 如果请求切换单位
				String orgIdStr = request.getParameter("org_id");
				if(V.isNotEmpty(orgIdStr) && !orgIdStr.equals(String.valueOf(orgUser.getOrgId()))){
					boolean hasPermission = false;
					for(Organization org : subCompanyList){
						if(orgIdStr.equals(String.valueOf(org.getId()))){
							orgId = Long.parseLong(orgIdStr);
							hasPermission = true;
							break;
						}
					}
					if(!hasPermission){
						throw new PermissionException("您没有权限查看该数据！");
					}
				}				 
			}
		}
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", orgId);
		modelMap.addAttribute("criteria", criteria);
		modelMap.addAttribute("subCompanyList", subCompanyList);
	}
	
	/***
	 * 获取可访问的单位列表
	 * @param request
	 * @param organizationService
	 * @param orgUser
	 * @param modelMap
	 * @throws Exception
	 */
	public static List<Organization> getAccessableOrgList(HttpServletRequest request, OrganizationService organizationService, OrgUser orgUser) throws Exception{
		List<Organization> orgList = null;
		if(orgUser.isGroupUser()){
			orgList = organizationService.getSubCompanyList(orgUser);
		}
		else{
			Organization org = (Organization) organizationService.getModel(orgUser.getOrgId());
			if(org != null){
				orgList = new ArrayList<Organization>();
				orgList.add(org);
			}
		}
		return orgList;
	}
	
}
package com.elementwin.bs.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.elementwin.api.config.Cons;
import com.elementwin.common.model.MetadataItem;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.common.model.Organization;
import com.elementwin.bs.service.MetadataItemService;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.bs.utils.AppHelper;
import dibo.framework.model.BaseModel;
import dibo.framework.service.BaseService;

/***
 * Copyright 2016 www.Dibo.ltd
 * 企业员工相关操作Controller
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
@Controller
@RequestMapping("/orgUser")
public class OrgUserController extends BaseCRUDController {
	private static Logger logger = Logger.getLogger(OrgUserController.class);
	
	@Autowired
	private OrgUserService orgUserService;
	
	@Autowired
	private MetadataItemService metadataItemService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Override
	protected String getViewPrefix() {
		return "orgUser";
	}
	
	/***
	 * 根路径/
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String root(HttpServletRequest request, ModelMap modelMap) throws Exception {		
		return index(request, modelMap);
	}
	
	/***
	 * 显示首页面
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(HttpServletRequest request, ModelMap modelMap) throws Exception {		
		// 加载第一页
		return indexPaging(1, request, modelMap);
	}
	
	/**
	 * 显示首页-分页
	 * @param pageIndex 分页
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value="/index/{pageIndex}", method=RequestMethod.GET)
	public String indexPaging(@PathVariable("pageIndex")int pageIndex, HttpServletRequest request, ModelMap modelMap) throws Exception{
		List<MetadataItem> orgRoleList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.ORGUSER_ROLE);
		modelMap.put("orgRoleList", orgRoleList);
		
		List<MetadataItem> statusList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.USER_STATUS);
		modelMap.put("statusList", statusList);
		
		OrgUser me = AppHelper.getCurrentUser();
		Organization org = (Organization) organizationService.getModel(me.getOrgId());
		modelMap.put("org", org);
		
		return super.indexPaging(pageIndex, request, modelMap);
	}
	
	/***
	 * 显示查看详细页面
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String viewPage(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap) throws Exception {  
		return super.viewPage(id, request, modelMap);
    }
	
	/***
	 * 显示创建页面
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createPage(HttpServletRequest request, ModelMap modelMap) throws Exception {  
		List<MetadataItem> orgRoleList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.ORGUSER_ROLE);
		modelMap.put("orgRoleList", orgRoleList);
		
		List<MetadataItem> statusList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.USER_STATUS);
		modelMap.put("statusList", statusList);

		OrgUser user = AppHelper.getCurrentUser();
		modelMap.put("orgId", user.getOrgId());

		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", user.getOrgId());
		criteria.put("active", 1);
		List<OrgUser> users = (List<OrgUser>) orgUserService.getModelList(criteria);
		modelMap.put("allUsers", users);
		
		return super.createPage(request, modelMap);
    }
	
	/***
	 * 创建的后台处理
	 * @param orgUser
	 * @param result
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@Valid OrgUser model, BindingResult result, HttpServletRequest request, ModelMap modelMap) throws Exception {  
		OrgUser user = AppHelper.getCurrentUser();
		model.setOrgId(user.getOrgId());
		return super.create(model, result, request, modelMap);
    }
	
	/***
	 * 显示更新页面
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String updatePage(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap) throws Exception {  
		List<MetadataItem> orgRoleList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.ORGUSER_ROLE);
		modelMap.put("orgRoleList", orgRoleList);
		
		List<MetadataItem> statusList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.USER_STATUS);
		modelMap.put("statusList", statusList);
		
		Map<String, Object> criteria = new HashMap<String, Object>();
		OrgUser user = AppHelper.getCurrentUser();
		criteria.put("org_id", user.getOrgId());
		criteria.put("active", 1);
		List<OrgUser> users = (List<OrgUser>) orgUserService.getModelList(criteria);
		modelMap.put("allUsers", users);
		
		return super.updatePage(id, request, modelMap);
    }
	
	/***
	 * 更新的后台处理
	 * @param orgUser
	 * @param result
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	public String update(@PathVariable("id")Long id, @Valid OrgUser model, BindingResult result, HttpServletRequest request, ModelMap modelMap) throws Exception{
		OrgUser user = AppHelper.getCurrentUser();
		model.setOrgId(user.getOrgId());
		return super.update(id, model, result, request, modelMap);
	}
	
	/***
	 * 删除的后台处理
	 * @param orgUser
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> delete(@PathVariable("id")Long id, HttpServletRequest request) throws Exception{
		return super.delete(id, request);
	}
	
	/***
	 * 显示修改密码页面
	 * @param request
	 * @param modelMap
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profilePage(HttpServletRequest request, ModelMap modelMap) throws Exception {  
		modelMap.put("model", orgUserService.getModel(AppHelper.getCurrentUserId()));
		
		List<MetadataItem> orgRoleList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.ORGUSER_ROLE);
		modelMap.put("orgRoleList", orgRoleList);
		
		List<MetadataItem> statusList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.USER_STATUS);
		modelMap.put("statusList", statusList);
		
		return view(request, modelMap, "profile");
    }
	
	/***
	 * 显示修改密码页面
	 * @param request
	 * @param modelMap
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value = "/profile", method = RequestMethod.POST)
    public String profile(@Valid OrgUser model, BindingResult result, HttpServletRequest request, ModelMap modelMap) throws Exception {  
		
		OrgUser user = AppHelper.getCurrentUser();
		model.setId(user.getId());
		model.setOrgId(user.getOrgId());
		model.setPassword(user.getPassword());
		model.setOrgRole(user.getOrgRole());
		model.setEnabled(user.isEnabled());
		
		// Model属性值验证结果
		if(result.hasErrors()) {
			AppHelper.bindErrors(modelMap, result);
			modelMap.addAttribute("model", model);
            return profilePage(request, modelMap);  
        }
		
		// 保存头像
		String imgAccessUrl = super.saveFiles(request, model);
		if(StringUtils.isNotBlank(imgAccessUrl)){
			model.setAvatar(imgAccessUrl);
			if(logger.isDebugEnabled()){
				logger.info("头像保存成功，Url="+imgAccessUrl);
			}
		}
		else{
			OrgUser oldModel = (OrgUser) orgUserService.getModel(model.getId());
			user.setAvatar(oldModel.getAvatar());
		}
		
        // 执行保存操作
        boolean success = getService().updateModel(model);
        
        // 绑定执行结果
        String msg = success?"更新操作成功！":"更新操作失败！";
        addResultMsg(request, success, msg);
        
        // 绑定执行结果
        return "redirect:/"+getViewPrefix()+"/profile";
    }
	
	
	/***
	 * 显示修改密码页面
	 * @param request
	 * @param modelMap
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(value = "/changepwd", method = RequestMethod.GET)
    public String updateUserPwd(HttpServletRequest request, ModelMap modelMap) throws Exception {  
		OrgUser user = AppHelper.getCurrentUser();
		modelMap.put("model", user);
		return view(request, modelMap, "changePwd");
    }
	
	/**
	 * 修改密码的后台处理
	 * @param request
	 * @param result
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/changepwd", method=RequestMethod.POST)
	public String updateUserPwdAction(HttpServletRequest request, ModelMap modelMap) throws Exception{
		String oldPassword = request.getParameter("oldPassword");
		String newPassword = request.getParameter("newPassword");
		String newPassword2 = request.getParameter("newPassword2");
		
		if(!isValidPwd(oldPassword) || !isValidPwd(newPassword) || !isValidPwd(newPassword2)){
			AppHelper.bindError(modelMap, "密码长度不符合要求，请设置6-16位的字母数字组合作为密码！");
            return view(request, modelMap, "changePwd");
		}
		if(!newPassword.equals(newPassword2)){
			AppHelper.bindError(modelMap, "两次输入的新密码不一样，请重新设置！");
            return view(request, modelMap, "changePwd");
		}
		
		// 更新密码
		OrgUser user = AppHelper.getCurrentUser();
		Long userId = user.getId();
		boolean success = orgUserService.updateUserPwd(userId, oldPassword, newPassword);
		if(!success){
			AppHelper.bindError(modelMap, "更新密码失败，请检查旧密码输入是否正确！");
            return view(request, modelMap, "changePwd");
		}
		else{
			logger.info("用户:"+userId+" 更新了密码！");
			// 记录操作日志
			asyncWorker.saveTraceLog(user, Cons.OPERATION.UPDATE, user, "修改密码");
		}
		String msg = "密码修改成功，请牢记新密码！";
		addResultMsg(request, success, msg);
		
		return "redirect:/orgUser/changepwd";
	}
	
	private boolean isValidPwd(String password){
		return password != null && password.length() >= 6 && password.length() <= 16;
	}
	
	@Override
	protected BaseService getService() {
		return orgUserService;
	}

	@Override
	protected boolean canDelete(BaseModel model) {
		OrgUser me = AppHelper.getCurrentUser();
		OrgUser user = (OrgUser)model;
		if(me.isAdmin() && !user.isAdmin()){ //只有管理员可删除非管理员数据
			return true;
		}
		return false;
	}
	
	// 创建之前的处理
	@Override
	protected void beforeCreate(HttpServletRequest request, BaseModel model, ModelMap modelMap) throws Exception{
		OrgUser loginUser = AppHelper.getCurrentUser();
		OrgUser user = (OrgUser)model;
		if(orgUserService.isDuplicateUser(user)){
			AppHelper.bindError(modelMap, "员工编号不可重复，该员工编号已存在，请检查！");
			return;
		}
		else{
			OrgUser u = orgUserService.getUser(user.getUsername());
			if(u != null){
				// 新建，更新
				if(model.getId() == null || !u.getId().equals(model.getId())){
					AppHelper.bindError(modelMap, "用户名[ "+user.getUsername()+" ]已存在，请检查！");
					return;
				}
			}
			if(StringUtils.isNotBlank(user.getManagerNo()) && !user.getEmployeeNo().equals(user.getManagerNo())){
				OrgUser leader = orgUserService.getUserByEmployeeNo(user.getOrgId(), user.getManagerNo());
				if(leader == null){
					AppHelper.bindError(modelMap, "员工经理不存在，请检查员工经理编号！");
					return;
				}
			}
			else{
				user.setManagerNo(user.getEmployeeNo());
			}
			user.setCreateBy(loginUser.getId());
		}
		user.setOrgId(loginUser.getOrgId());
		
		// 保存头像
		String imgAccessUrl = super.saveFiles(request, model);
		if(StringUtils.isNotBlank(imgAccessUrl)){
			user.setAvatar(imgAccessUrl);
			if(logger.isDebugEnabled()){
				logger.info("头像保存成功，Url="+imgAccessUrl);
			}
		}
		else if(!model.isNew()){
			OrgUser oldModel = (OrgUser) orgUserService.getModel(model.getId());
			user.setAvatar(oldModel.getAvatar());
		}
	}
	
	// 更新
	@Override
	protected void beforeUpdate(HttpServletRequest request, BaseModel model, ModelMap modelMap) throws Exception{
		beforeCreate(request, model, modelMap);		
	}
	
}
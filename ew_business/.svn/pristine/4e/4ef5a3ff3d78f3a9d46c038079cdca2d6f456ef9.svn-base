package com.elementwin.bs.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.elementwin.api.config.Cons;
import com.elementwin.bs.cache.MetadataCache;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.common.model.BaseMenu;
import com.elementwin.common.model.BaseUser;
import com.elementwin.common.model.MetadataItem;
import com.elementwin.common.model.Organization;
import com.elementwin.common.model.Role;

import dibo.framework.utils.V;

/***
 * Copyright 2016 www.Dibo.ltd
 * OrgUser-企业员工
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
public class OrgUser extends BaseUser{  
	
	private static final long serialVersionUID = 205L;
	
    @NotNull(message = "角色不能为空！")
    private Long roleId = Cons.ROLE_ORGUSER; // 员工角色

    @NotNull(message = "客户ID不能为空！")
    private Long orgId; // 客户ID
	
    @NotNull(message = "员工角色不能为空！")
    private Long orgRoleItemId; //员工在单位的角色
    
    private String orgRole; // 单位的角色层
    
    @Length(max = 50, message = "员工编号长度超出了最大限制！")  
    private String employeeNo; // 员工编号
	
    @NotNull(message = "经理员工号不能为空！")
    @Length(max = 10, message = "经理员工号长度超出了最大限制！")  
    private String managerNo = "0"; // 经理员工号
	
    @Length(max = 50, message = "微信号长度超出了最大限制！")  
    private String wechat; // 微信号
	
    @Length(max = 255, message = "备注长度超出了最大限制！")  
    private String comment; // 备注
	
    @NotNull(message = "员工状态不能为空！")
    private Long statusItemId; // 员工状态

    @Length(max = 20, message = "助理电话长度超出了最大限制！")  
    private String assistantPhone; // 助理电话
    
    private String orgRoleName; // 员工头衔
    private String orgName; // 公司名字
    private String orgAddress; // 公司地址
	private String orgTelphone; // 公司电话
    private String statusName; // 员工状态
	
    private Organization organization; // 公司
    
    // 当前客户对应的服务
    private Long currentServiceId = null;
    private List<MetadataItem> availableServices = null;
    private Map<Long, List<BaseMenu>> service2MenusMap = null;
    
    public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
    public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
    public String getEmployeeNo() {
		return employeeNo;
	}
	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}
    public String getManagerNo() {
		return managerNo;
	}
	public void setManagerNo(String managerNo) {
		this.managerNo = managerNo;
	}
    public String getWechat() {
		return wechat;
	}
	public void setWechat(String wechat) {
		this.wechat = wechat;
	}
    public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
    public Long getOrgRoleItemId() {
		return orgRoleItemId;
	}
	public void setOrgRoleItemId(Long orgRoleItemId) {
		this.orgRoleItemId = orgRoleItemId;
	}
	public Long getStatusItemId() {
		return statusItemId;
	}
	public void setStatusItemId(Long statusItemId) {
		this.statusItemId = statusItemId;
	}
	public boolean isAdmin() {
		return V.isNotEmpty(orgRole) && orgRole.equalsIgnoreCase(Cons.ORG_ROLE.ADMIN.name());
	}
	public String getOrgRoleName() {
		return orgRoleName;
	}
	public void setOrgRoleName(String orgRoleName) {
		this.orgRoleName = orgRoleName;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgAddress() {
		return orgAddress;
	}
	public void setOrgAddress(String orgAddress) {
		this.orgAddress = orgAddress;
	}
	public String getOrgTelphone() {
		return orgTelphone;
	}
	public void setOrgTelphone(String orgTelphone) {
		this.orgTelphone = orgTelphone;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	public String getAssistantPhone() {
		return assistantPhone;
	}
	public void setAssistantPhone(String assistantPhone) {
		this.assistantPhone = assistantPhone;
	}
	
	public Long getCurrentServiceId() {
		return currentServiceId;
	}
	public void setCurrentServiceId(Long currentServiceId) {
		this.currentServiceId = currentServiceId;
	}
	public String getOrgRole() {
		return orgRole;
	}
	public void setOrgRole(String orgRole) {
		this.orgRole = orgRole;
	}
	public String getOrgRoleLabel() {
		return Cons.ORG_ROLE_LABELS.get(orgRole);
	}
	public boolean isManager() {
		return V.isNotEmpty(orgRole) && orgRole.equalsIgnoreCase(Cons.ORG_ROLE.MANAGER.name());
	}
	
	public boolean isGroupUser() {
		return this.getOrganization() != null && this.getOrganization().isGroup();
	}
	
	@Override
	public Role getRole() {
		return MetadataCache.getRole(getRoleId());
	}

	public List<MetadataItem> getAvailableServices() {
		return availableServices;
	}
	public Map<Long, List<BaseMenu>> getService2MenusMap() {
		return service2MenusMap;
	}
	
	/***
	 * 登录后初始化
	 * @param availableServices
	 * @param allMenus
	 */
	public void setAuthServiceAndMenus(List<MetadataItem> availableServices, List<BaseMenu> allMenus) {
		this.availableServices = availableServices;
		if(this.isGroupUser()){
			List<BaseMenu> tree = (List<BaseMenu>) AppHelper.buildTreeStructureModels(allMenus);
			this.setMenus(tree);
			setCurrentServiceId(0L);
			return;
		}
		
		Map<Long, List<BaseMenu>> tempMap = new HashMap<Long, List<BaseMenu>>();
		if(availableServices != null && !availableServices.isEmpty()){
			if(allMenus != null){
				for(BaseMenu menu : allMenus){
					BsMenu bsMenu = (BsMenu)menu;
					List<BaseMenu> menus = tempMap.get(bsMenu.getServiceId());
					if(menus == null){
						menus = new ArrayList<BaseMenu>();
						tempMap.put(bsMenu.getServiceId(), menus);
					}
					menus.add(menu);
				}
				if(!tempMap.isEmpty()){
					this.service2MenusMap = new HashMap<Long, List<BaseMenu>>();
					for(Map.Entry<Long, List<BaseMenu>> entry : tempMap.entrySet()){
						List<BaseMenu> list = entry.getValue();
						List<BaseMenu> tree = (List<BaseMenu>) AppHelper.buildTreeStructureModels(list);
						this.service2MenusMap.put(entry.getKey(), tree);
					}
					tempMap = null;					
				}
				// 设置初始化菜单
				switchServiceMenus(availableServices.get(0).getId());						
			}
		}
	}
	
	/***
	 * 切换服务菜单
	 * @param serviceId
	 */
	public void switchServiceMenus(Long serviceId){
		// 设置初始化菜单
		List<BaseMenu> svcMenus = this.service2MenusMap.get(serviceId);
		if(svcMenus == null){
			svcMenus = this.service2MenusMap.get(0L);
		}
		else{
			List<BaseMenu> globalMenus = this.service2MenusMap.get(0L);
			if(globalMenus != null){
				for(BaseMenu m : globalMenus){
					if(!svcMenus.contains(m)){
						svcMenus.add(m);						
					}
				}
			}
		}
		this.setMenus(svcMenus);
		setCurrentServiceId(serviceId);
	}
}
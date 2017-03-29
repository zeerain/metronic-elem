package com.elementwin.bs.model;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.elementwin.api.config.Cons;
import com.elementwin.bs.cache.MetadataCache;
import com.elementwin.common.model.Role;

/***
 * User-用户
 * @author Mazc@dibo.ltd
 */
public class CcUser extends com.elementwin.common.model.BaseUser{

	private static final long serialVersionUID = 6481034648513944325L;

	@NotNull(message = "角色不能为空！")
    private Long roleId = Cons.ROLE_CCUSER; // 角色
	
    private Long teamId; // 团队
	
    private Long teamRoleId; // 团队角色
	
    private Long managerId; // 上级ID
	
    @Length(max = 255, message = "头像长度超出了最大限制！")  
    private String avatar; // 头像
	
    @Length(max = 50, message = "长度超出了最大限制！")  
    private String extnumber; // 
	
    @Length(max = 255, message = "微信号长度超出了最大限制！")  
    private String wechat; // 微信号
	
    @Length(max = 255, message = "等级长度超出了最大限制！")  
    private String level; // 等级
	
    private Long statusId; // 用户状态
	
    private String managerName;
	
    public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
    public Long getTeamId() {
		return teamId;
	}
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}
    public Long getTeamRoleId() {
		return teamRoleId;
	}
	public void setTeamRoleId(Long teamRoleId) {
		this.teamRoleId = teamRoleId;
	}
    public Long getManagerId() {
		return managerId;
	}
	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}
    public String getAvatar() {
		return avatar;
	}
    
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
    public String getExtnumber() {
		return extnumber;
	}
	public void setExtnumber(String extnumber) {
		this.extnumber = extnumber;
	}
    public String getWechat() {
		return wechat;
	}
	public void setWechat(String wechat) {
		this.wechat = wechat;
	}
    public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
    public Long getStatusId() {
		return statusId;
	}
	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}
	
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	/***
	 * 扩展属性
	 * @return
	 */
	public String getAvatarUrl() {
    	if(avatar == null){
    		return Cons.DEFAULT_AVATAR_CC;
    	}
    	return avatar;
    }

	@Override
	public Role getRole() {
		return MetadataCache.getRole(roleId);
	}
	
	/***
	 * 是否是管理角色
	 * @return
	 */
	public boolean isTeamManager(){
		return Cons.CCUSER_TEAMROLE_MANAGER.equals(this.teamRoleId.longValue());
	}
}
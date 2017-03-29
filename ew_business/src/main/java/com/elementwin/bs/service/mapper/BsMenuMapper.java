package com.elementwin.bs.service.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.elementwin.bs.model.BsMenu;

import dibo.framework.service.mapper.BaseMapper;

import com.elementwin.common.model.BaseMenu;

/***
 * Menu-菜单相关Mapper
 * @author Mazc@dibo.ltd
 */
public interface BsMenuMapper extends BaseMapper{
	
	List<BsMenu> getServiceRelatedMenus(Long serviceId) throws Exception;

	List<BaseMenu> getServiceRelatedMenus(@Param("serviceIds")List<Long> serviceIds, @Param("authRoles")List<String> authRoles) throws Exception;
	
}
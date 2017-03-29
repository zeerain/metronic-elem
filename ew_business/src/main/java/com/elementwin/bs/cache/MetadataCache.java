package com.elementwin.bs.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.elementwin.api.config.SystemConfig;
import com.elementwin.bs.model.BsMenu;
import com.elementwin.bs.model.Role;
import com.elementwin.bs.service.BsMenuService;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.api.utils.ContextHelper;

/***
 * 元数据缓存类
 * @author Mazc@dibo.ltd
 */
@Component
public class MetadataCache implements ApplicationListener<ContextRefreshedEvent> {
	private static Logger logger = Logger.getLogger(MetadataCache.class);
	// 元数据缓存
	// 角色缓存
	private static Map<Long, Role> rolesCacheMap = new ConcurrentHashMap<Long, Role>();
	
	private static List<BsMenu> allMenus = new ArrayList<BsMenu>();
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent evt) {
		// 缓存角色数据
		loadRoles();
		// 加载菜单
		loadMenus();
	}
	
	/***
	 * load全部元数据并缓存
	 */
	private static void loadRoles(){
		// 加载数据
		OrgUserService orgUserService = (OrgUserService) ContextHelper.getBean("orgUserService");
		if(orgUserService != null){
			try {
				List<Role> roles = orgUserService.getAllRoles();
				if(roles != null){
					rolesCacheMap.clear();
					for(Role r : roles){
						rolesCacheMap.put(r.getId(), r);
					}
					logger.info("加载角色数据完成！");
				}
			}
			catch (Exception e) {
				logger.error("加载角色数据出错！", e);
			}			
		}
	}	
	
	/***
	 * load全部菜单并缓存
	 */
	private static void loadMenus(){
		// 加载数据
		BsMenuService bsMenuService = (BsMenuService) ContextHelper.getBean("bsMenuService");
		if(bsMenuService != null){
			try {
				allMenus = (List<BsMenu>) bsMenuService.getModelList(null);
				// build成树形结构
				allMenus = (List<BsMenu>) AppHelper.buildTreeStructureModels(allMenus);
				
				logger.info("加载菜单数据完成！");
			}
			catch (Exception e) {
				logger.error("加载菜单出错！", e);
			}
		}
	}
		
	/**
	 * 获取系统参数
	 * @param key
	 * @return
	 */
	public static String getSystemProperty(String key){
		return SystemConfig.getProperty(key);
	}

	/***
	 * 读取Role
	 * @param id
	 * @return
	 */
	public static Role getRole(Long id){
		if(rolesCacheMap.isEmpty()){
			loadRoles();
		}
		return rolesCacheMap.get(id);
	}
	
	/***
	 * 获取全部菜单
	 * @return
	 */
	public static List<BsMenu> getAllMenus(){
		return allMenus;
	}
	
	public static Object getBean(String beanId){
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		if(wac != null){
			return wac.getBean(beanId);			
		}
		return null;
	}
}
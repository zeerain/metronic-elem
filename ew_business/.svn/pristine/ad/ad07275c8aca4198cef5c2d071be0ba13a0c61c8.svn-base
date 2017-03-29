package com.elementwin.bs.controller.wx;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.elementwin.api.config.Cons;
import com.elementwin.bs.cache.MetadataCache;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.PresaleTaskService;
import com.elementwin.common.model.Organization;
import com.elementwin.common.model.WechatException;
import com.elementwin.api.utils.DateUtils;

import dibo.framework.utils.V;

@Controller
@RequestMapping("/weixin")
public class MainController extends BaseWxController{
	private static Logger logger = Logger.getLogger(MainController.class);
	
	@Autowired
	protected OrgUserService orgUserService;
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	protected PresaleTaskService presaleTaskService;
	
	@Autowired
	protected PotentialCustomerService potentialCustomerService;
	
	/***
	 * 应用首页
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String appHome(HttpServletRequest request, ModelMap modelMap) throws Exception {
		checkUser(request);
		// 获取缓存用户
		OrgUser user = getCurrentUser(request);
		// 设置user的单位信息
		Organization org = (Organization) organizationService.getModel(user.getOrgId());

		Map<String, Integer> countMap = new HashMap<String, Integer>();
		// 统计任务
		fillTaskCount(countMap, user);
		// 统计通讯录
		fillAddressBookCount(countMap, user);
		// 统计排名
		fillRanking(countMap, user);
		
		modelMap.addAttribute("user", user);
		modelMap.addAttribute("org", org);
		modelMap.addAttribute("countMap", countMap);
		
		return super.view(request, modelMap, "home");
	}
	
	/**
	 * 填充任务数量
	 * @param user
	 * @param countMap
	 * @throws Exception
	 */
	private void fillTaskCount(Map<String, Integer> countMap, OrgUser user) throws Exception{
		// 封装并自动生成
		Map<String, Object> criteria = new HashMap<String, Object>();
		// 取当前用户的所属单位
		criteria.put("org_id", user.getOrgId()); 
		criteria.put("salesperson_id", user.getId());
		
		// 获取记录总数，用于前端显示分页
		int all = presaleTaskService.getModelListCount(criteria);
		
		// 是否待响应
		criteria.put("statusIn", new String[]{Cons.PRESALE_STATUS.NOTIFY_SA.name(), Cons.PRESALE_STATUS.CONTINUE.name()});
		// 获取待响应记录总数
		int pending = presaleTaskService.getModelListCount(criteria);
		
		criteria = null;
		
		countMap.put("taskPending", pending);
		countMap.put("taskAll", all);
	}
	
	/***
	 * 填充通讯录数目
	 * @param countMap
	 * @param user
	 * @throws Exception
	 */
	private void fillAddressBookCount(Map<String, Integer> countMap, OrgUser user) throws Exception{
		// 计算通讯录待办和总数
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", user.getOrgId());
        criteria.put("salesperson_id", user.getId());
        criteria.put("handleStatusIn", new String[]{Cons.PRESALE_HANDLE_STATUS.PENDING.name(), Cons.PRESALE_HANDLE_STATUS.UPDATED.name()});
        criteria.put("nextHandleTimeEnd", DateUtils.getDateEnd(new Date()));

        // 获取待办记录总数
        int pendingCount = presaleTaskService.getModelListCount(criteria);
		countMap.put("addressBookPending", pendingCount); // 通讯录待办
		
        // 获取所有潜客列表
		criteria.clear();
		criteria.put("org_id", user.getOrgId());
    	criteria.put("from_sa_id", user.getId());
    	int all = potentialCustomerService.getModelListCount(criteria);
		countMap.put("addressBookAll", all); // 通讯录所有
	}
	
	/***
	 * 计算排名
	 * @param countMap
	 * @param user
	 */
	private void fillRanking(Map<String, Integer> countMap, OrgUser user) throws Exception{
		// TODO 实现排名
	
		//获取排名及统计数据
		//int salesCount = getActiveOrgUserCount(user);
		
		// 初始化起止日期
		String beginDate = null;
		String endDate = null;
		Map<String, Object> criteria = new HashMap<String, Object>();
		if(V.isEmpty(beginDate) && V.isEmpty(endDate)){
			Date today = new Date();
			beginDate = DateUtils.getDate(today, -7); //TODO 更改为配置项
			endDate = DateUtils.getDate(today);
		}
		criteria.put("beginDate", beginDate);
		criteria.put("endDate", endDate);
		
		int[] customerCountArr = presaleTaskService.getStatisticValue(criteria, user, Cons.STATISTIC_TYPE.CUSTOMER_COUNT);
		//customerCountArr[1] = (customerCountArr[1]/salesCount);
		countMap.put("myPerformance", customerCountArr[0]);
		countMap.put("allPerformance", customerCountArr[1]);
	}
	
	/***
	 * Keep Alive action
	 */
	@ResponseBody
	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public String ping(HttpServletRequest request, ModelMap modelMap) throws Exception {
		return null;
	}
	
	/***
	 * 微信端测试入口: PC端先运行此页面注入微信用户
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * 
	 */
	@RequestMapping(value = "/test/{wechat}", method = RequestMethod.GET)
	public String test(@PathVariable("wechat")String wechat, HttpServletRequest request, ModelMap modelMap) throws Exception {
		if(!"true".equalsIgnoreCase(MetadataCache.getSystemProperty("wechat.test.enable"))){
			throw new WechatException("您访问的页面不存在！");
		}
		HttpSession session = request.getSession(true);
		session.setAttribute("userID", wechat);
		
		// 获取User
		OrgUser user = orgUserService.getUserByWeChat(wechat);
		if (user == null) {
			throw new WechatException("您暂时无法使用该应用，请检查系统中的账号设置！");
		}
		session.setAttribute("user", user);
		
		// 元兵演示
		return "redirect:/weixin/home";		
	}
	
}
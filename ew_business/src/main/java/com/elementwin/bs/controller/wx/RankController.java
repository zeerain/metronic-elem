package com.elementwin.bs.controller.wx;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.elementwin.api.config.Cons;
import com.elementwin.api.utils.DateUtils;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.service.PresaleTaskService;

import dibo.framework.utils.V;

@Controller
@RequestMapping("/weixin/rank")
public class RankController extends BaseWxController {
	private static Logger logger = Logger.getLogger(RankController.class);
	
	@Autowired
	private OrgUserService orgUserService;
	
	@Autowired
	private PresaleTaskService presaleTaskService;
	
	@Override
	protected String getViewPrefix() {
		return "weixin/rank";
	}
	
	// 微信企业号--个人名片
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String ecardView(HttpServletRequest request, ModelMap modelMap) throws Exception {
		checkUser(request);
		// 获取当前用户
		OrgUser orgUser = getCurrentUser(request);
		modelMap.addAttribute("model", orgUser);
		
		//获取排名及统计数据
		int salesCount = getActiveOrgUserCount(orgUser);
		
		// 初始化起止日期
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		Map<String, Object> criteria = new HashMap<String, Object>();
		if(V.isEmpty(beginDate) && V.isEmpty(endDate)){
			Date today = new Date();
			beginDate = DateUtils.getDate(today, -7); //TODO 更改为配置项
			endDate = DateUtils.getDate(today);
		}
		criteria.put("beginDate", beginDate);
		criteria.put("endDate", endDate);
		
		int[] svcCountArr = presaleTaskService.getStatisticValue(criteria, orgUser, Cons.STATISTIC_TYPE.SERVICE_COUNT);
		svcCountArr[1] = (svcCountArr[1]/salesCount);
		modelMap.put("svcCount", svcCountArr);
				
		resetCriteria(criteria, beginDate, endDate);		
		int[] customerCountArr = presaleTaskService.getStatisticValue(criteria, orgUser, Cons.STATISTIC_TYPE.CUSTOMER_COUNT);
		customerCountArr[1] = (customerCountArr[1]/salesCount);
		modelMap.put("customerCount", customerCountArr);
		
		resetCriteria(criteria, beginDate, endDate);		
		int[] dealCountArr = presaleTaskService.getStatisticValue(criteria, orgUser, Cons.STATISTIC_TYPE.DEAL_COUNT);
		dealCountArr[1] = (dealCountArr[1]/salesCount);
		modelMap.put("dealCount", dealCountArr);

		resetCriteria(criteria, beginDate, endDate);
		int[] ontimeCountArr = presaleTaskService.getStatisticValue(criteria, orgUser, Cons.STATISTIC_TYPE.ON_TIME_RATE);
		modelMap.put("ontimeRate", ontimeCountArr);
		
		modelMap.put("beginDate", beginDate);
		modelMap.put("endDate", endDate);
		
		return super.view(request, modelMap, "index");
	}
	
	/***
	 * 重置查询条件
	 * @param criteria
	 * @param beginDate
	 * @param endDate
	 */
	private void resetCriteria(Map<String, Object> criteria, String beginDate, String endDate){
		criteria.clear();
		criteria.put("beginDate", beginDate);
		criteria.put("endDate", endDate);
		
	}
}

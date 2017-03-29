package com.elementwin.bs.controller.wx;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.PresaleTask;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.PresaleTaskService;

import dibo.framework.utils.DateUtils;

@Controller
@RequestMapping("/weixin/calendar")
public class CalendarController extends BaseWxController {
	private static Logger logger = Logger.getLogger(CalendarController.class);
	
	@Autowired
	PresaleTaskService presaleTaskService;

	@Autowired
	PotentialCustomerService potentialCustomerService;
	
	@Override
	protected String getViewPrefix() {
		return "weixin/calendar";
	}
	
	/***
	 * 显示列表首页面
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	protected String index(HttpServletRequest request, ModelMap modelMap) throws Exception {		
		Date today = new Date();
		modelMap.addAttribute("today", today);
		
		loadCalendar(DateUtils.getDate(today), request, modelMap);
		
		return super.view(request, modelMap, "index");
	}
	
	@RequestMapping(value = "/load/{date}", method=RequestMethod.GET)
	protected String loadCalendar(@PathVariable("date")String date, HttpServletRequest request, ModelMap modelMap) throws Exception{
		checkUser(request);
		// 获取用户信息
		OrgUser user = getCurrentUser(request);
				
		// 封装并自动生成
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("salesperson_id", user.getId());
		criteria.put("appointed_time", date);
		//criteria.put("statusIn", new String[]{Cons.PRESALE_STATUS.NOTIFY_SA.name()});
		
		// 加载当前页
		List<PresaleTask> modelList = (List<PresaleTask>) presaleTaskService.getModelList(criteria);
		presaleTaskService.appendCustomer2Task(modelList);
		
		modelMap.put("modelList", modelList);
		modelMap.put("weekday", com.elementwin.api.utils.DateUtils.getWeekday());
		
		modelMap.put("currentDate", com.elementwin.api.utils.DateUtils.dateString2Date(date));
		
		
		return super.view(request, modelMap, "list");
	}
	
}
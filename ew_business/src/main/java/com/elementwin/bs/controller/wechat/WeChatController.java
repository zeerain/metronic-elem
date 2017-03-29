package com.elementwin.bs.controller.wechat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

import com.elementwin.api.config.Cons;
import com.elementwin.bs.cache.MetadataCache;
import com.elementwin.bs.controller.BaseController;
import com.elementwin.bs.model.AudioMsg;
import com.elementwin.bs.model.ECard;
import com.elementwin.common.model.MetadataItem;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.common.model.Organization;
import com.elementwin.bs.model.PotentialCustomer;
import com.elementwin.bs.model.PresaleRecord;
import com.elementwin.bs.model.PresaleTask;
import com.elementwin.bs.model.RevisitAnswer;
import com.elementwin.bs.model.RevisitRecord;
import com.elementwin.bs.model.RevisitTask;
import com.elementwin.bs.model.RevisitWorkOrder;
import com.elementwin.bs.model.RevisitWorkOrderOpinion;
import com.elementwin.bs.service.AudioMsgService;
import com.elementwin.bs.service.MetadataItemService;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.PresaleTaskService;
import com.elementwin.bs.service.RevisitImportService;
import com.elementwin.bs.service.RevisitRecordService;
import com.elementwin.bs.service.RevisitStatisticalService;
import com.elementwin.bs.service.RevisitTaskService;
import com.elementwin.bs.service.RevisitWorkOrderService;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.bs.utils.StatisticHelper;
import com.elementwin.api.utils.WeChatHelper;
import com.elementwin.bs.scheduler.AsyncWorker;
import com.elementwin.common.model.WechatException;

import dibo.framework.model.BaseModel;
import dibo.framework.utils.S;
import dibo.framework.utils.V;

/***
 * Copyright 2016 www.Dibo.ltd
 * 微信企业号相关操作Controller
 * @author Yaojf@dibo.ltd
 * @version v1.0, 2016/09/09
 */
@Controller
@RequestMapping("/wechat")
public class WeChatController extends BaseController {
	private static Logger logger = Logger.getLogger(WeChatController.class);
	
	@Autowired
	private OrgUserService orgUserService;
	
	@Autowired
	private RevisitWorkOrderService revisitWorkOrderService;
	
	@Autowired
	private RevisitTaskService revisitTaskService;
	
	@Autowired
	private RevisitRecordService revisitRecordService;
	
	@Autowired
	private MetadataItemService metadataItemService;
	
	@Autowired
	private RevisitStatisticalService revisitStatisticalService;
	
	@Autowired
	private RevisitImportService revisitImportService;
	
	@Autowired
	private PresaleTaskService presaleTaskService;
	
	@Autowired
	private AudioMsgService audioMsgService;

	@Autowired
	private AsyncWorker asyncWorker;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private PotentialCustomerService potentialCustomerService;
	
	@Override
	protected String getViewPrefix() {
		return "wechat";
	}
	
	private ECard eCardModel = null;
	
	// 演示APPID
	private String DEMO_APPID = MetadataCache.getSystemProperty("wechat.demo.appid");
	
	/***
	 * 微信企业号初始化页面：
	 * @param agentId 企业号应用ID，需要在微信中绑定
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/init/{agentId}", method = RequestMethod.GET)
	public String initWechatApp(@PathVariable("agentId")String agentId, HttpServletRequest request, ModelMap modelMap) throws Exception {
		// 获取微信企业号内员工微信号
		String userID = getWechatUserId(request, agentId); // 账号
		
	    // 获取User并缓存
		OrgUser user = orgUserService.getUserByWeChat(userID);
		if (user == null) {
			throw new WechatException("您暂时无法使用该应用，请检查系统中的账号设置！");
		}
		// 设置user的单位信息
		Organization org = (Organization) organizationService.getModel(user.getOrgId());
		user.setOrganization(org);
		
		HttpSession session = request.getSession(false);
		session.setAttribute("user", user);
		session.setAttribute("userID", userID);
		session.setAttribute("agentId", agentId);
		
		// 更新缓存单位id
		updateCachedOrgId(session, agentId);
		
		// 元兵演示
		if(agentId.equals(DEMO_APPID)){
			return "redirect:/weixin/home";
		}
		else if(user.isGroupUser()){
			return "redirect:/wechat/group/home";			
		}
		else{
			return "redirect:/wechat/app";			
		}
	}
	
	@RequestMapping(value = "/app", method = RequestMethod.GET)
	public String openApp(HttpServletRequest request, ModelMap modelMap) throws Exception {
		String userID = ""; // 账号
		
		HttpSession session = request.getSession(false);
		if(session != null){
			if (session.getAttribute("userID") != null) {
				userID = (String) session.getAttribute("userID");
				logger.info("[WeChat]获取session中的userID: " + userID);
			}
		}
		
	    // 获取orgUser
		OrgUser orgUser = orgUserService.getUserByWeChat(userID);
		if (orgUser == null) {
			return "redirect:/wechat/error?errCode=ERR_WECHAT_3";
		}
		session.setAttribute("orgUser", orgUser);
		
		return wechatView(request, modelMap, "index");
	}
	
	// 微信企业号--个人名片
	@RequestMapping(value = "/ecardview", method = RequestMethod.GET)
	public String ecardView(HttpServletRequest request, ModelMap modelMap) throws Exception {
		OrgUser orgUser = getCurrentUser(request);
		if(orgUser == null){
			return "redirect:/wechat/error?errCode=ERR_WECHAT_6";
		}
		
		modelMap.addAttribute("model", orgUser);
		
		// 二维码参数
		modelMap.addAttribute("ecard", getEcardModel());
		
		return wechatView(request, modelMap, "ecardview");
	}
	
	/**
	 * 微信企业号--处理工单
	 * @param wechat
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/revisitWorkOrder", method = RequestMethod.GET)
	public String revisitWorkOrder(HttpServletRequest request, ModelMap modelMap) throws Exception {
		return revisitWorkOrderNavbar(Cons.WORK_ORDER_STATUS.PENDING.name(), request, modelMap);
	}
	
	/**
	 * 微信企业号--处理工单 navbar
	 * @param status
	 * @param request
	 * @param modelMap
	 */
	@RequestMapping(value = "/revisitWorkOrder/{status}", method = RequestMethod.GET)
	public String revisitWorkOrderNavbar(@PathVariable("status")String status, HttpServletRequest request, ModelMap modelMap) throws Exception {
		OrgUser orgUser = getCurrentUser(request);
		if(orgUser == null){
			return "redirect:/wechat/error?errCode=ERR_WECHAT_6";
		}
		Long orgId = getCachedOrgId(request, orgUser);
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", orgId);
		//如果设置了工单代理人，统一交给代理人
		boolean canEdit = false;
		boolean isAgent = false;
		Organization org = (Organization) organizationService.getModel(orgId);
		if(org.hasAgent()){// 如果有代理代理可处理
			if(orgUser.getId().equals(org.getAgent1Id()) || orgUser.getId().equals(org.getAgent2Id())){
				canEdit = true;
				isAgent = true;
			}
			if(!isAgent){// 非代理，销售经理是否可处理取决于设置
				criteria.put("customer_manager", orgUser.getRealname());			
				canEdit = org.isSaEditable();
			}
		}
		else{
			criteria.put("customer_manager", orgUser.getRealname());			
			canEdit = true;
		}
		criteria.put("workorderStatus", status.toUpperCase());
		List<RevisitTask> tasks = (List<RevisitTask>) revisitTaskService.getWorkOrderTask(criteria);
		
		//编辑taskId集合
		List<Long> taskIds = new ArrayList<Long>();
		//编辑workOrderIds集合
		List<Long> workOrderIds = new ArrayList<Long>();
		
		if(tasks != null && !tasks.isEmpty()){
			for(RevisitTask task : tasks){
				taskIds.add(task.getId());
				workOrderIds.add(task.getWorkOrderId());
			}
			//查询所有在taskId集合中的答案记录
			List<RevisitAnswer> allAnswers = revisitTaskService.getAnswersInTasks(taskIds);
			Map<Long, List<BaseModel>> allAnswerMap = AppHelper.list2map(allAnswers);
			
			//查询所有在taskId集合中的record记录
			List<RevisitRecord> allRecords = revisitTaskService.getRecordsInTasks(taskIds);
			Map<Long, List<BaseModel>> allRecordMap = AppHelper.list2map(allRecords);
			
			//查询所有在task集合中的所有工单记录
			List<RevisitWorkOrder> allWorkOrders = revisitTaskService.getWorkOrders(workOrderIds);
			Map<Long, List<BaseModel>> allWorkOrderMap = AppHelper.list2map(allWorkOrders);
			
			//查询所有在task集合中的所有工单的详细信息
			List<RevisitWorkOrderOpinion> allWorkOrderOpinions = revisitTaskService.getWorkOrderOpinion(workOrderIds);
			Map<Long, List<BaseModel>> allWorkOrderOpinionMap = AppHelper.list2map(allWorkOrderOpinions);
			
			//拼接结果，生成task列表
			for(RevisitTask task : tasks){
				List<RevisitAnswer> revisitAnswers = new ArrayList<RevisitAnswer>();
				List<RevisitRecord> revisitRecords = new ArrayList<RevisitRecord>();
				List<RevisitWorkOrderOpinion> revisitWorkOrderOpinions = new ArrayList<RevisitWorkOrderOpinion>();
				
				//拼接答案记录
				List<BaseModel> answers = (List<BaseModel>) allAnswerMap.get(task.getId());
				if(answers != null && !answers.isEmpty()) {
					for(BaseModel answer : answers){
						revisitAnswers.add((RevisitAnswer) answer);
					}
					task.setAnswerList(revisitAnswers);
				}
				
				//拼接record记录
				List<BaseModel> records = (List<BaseModel>) allRecordMap.get(task.getId());
				if(records != null && !records.isEmpty()) {
					for(BaseModel record : records){
						revisitRecords.add((RevisitRecord) record);
					}
					task.setRecordList(revisitRecords);
				}
				
				//拼接工单记录
				List<BaseModel> workOrders = allWorkOrderMap.get(task.getWorkOrderId());
				if(workOrders != null && !workOrders.isEmpty()){
					RevisitWorkOrder workOrder = (RevisitWorkOrder) workOrders.get(0);
					if (allWorkOrderOpinionMap != null && !allWorkOrderOpinionMap.isEmpty()) {
						//工单拼接详细信息
						List<BaseModel> workOrderOpinions = allWorkOrderOpinionMap.get(task.getWorkOrderId());
						if (workOrderOpinions != null && !workOrderOpinions.isEmpty()) {
							for(BaseModel workOrderOpinion : workOrderOpinions) {
								revisitWorkOrderOpinions.add((RevisitWorkOrderOpinion) workOrderOpinion);
							}
							workOrder.setOpinionList(revisitWorkOrderOpinions);
						}
					}
					task.setWorkOrder(workOrder);
				}
			}
		}
		
		modelMap.put("taskList", tasks);
		
		List<MetadataItem> categoryList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.WO_CATEGORY);
		modelMap.put("categoryList", categoryList);

		modelMap.put("currentUser", orgUser);
		
		modelMap.put("status", status.toUpperCase());
		
		modelMap.put("canEdit", canEdit);
		modelMap.put("isAgent", isAgent);
		
		return wechatView(request, modelMap, "/revisitWorkOrder/index");
	}
	
	/***
	 * 更新的后台处理
	 * @param revisitWorkOrder
	 * @param result
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateWorkOrder/{id}", method = RequestMethod.POST)
	public String updateWorkOrder(@PathVariable("id")Long id, @Valid RevisitWorkOrder model, BindingResult result, HttpServletRequest request, ModelMap modelMap) throws Exception {
		OrgUser user = getCurrentUser(request);
		if(user == null){
			return "redirect:/wechat/error?errCode=ERR_WECHAT_6";
		}

		String taskIdStr = request.getParameter("taskId");
		RevisitWorkOrder oldModel = (RevisitWorkOrder) revisitWorkOrderService.getModel(id);
		// 追加处理意见
		String category = request.getParameter("category");
		String opinionStr = request.getParameter("opinion");
		
		boolean isRevisitAgain = "true".equalsIgnoreCase(request.getParameter("revisitAgain"));

		RevisitWorkOrderOpinion opinion = new RevisitWorkOrderOpinion();
		opinion.setWorkOrderId(id);
		opinion.setCategory(category);
		opinion.setOpinion(opinionStr);
		opinion.setStatus(model.getStatus());
		opinion.setRevisitAgain(isRevisitAgain);
		opinion.setUserType(OrgUser.class.getSimpleName());
		opinion.setCreateBy(user.getId());
		opinion.setCreateByName(user.getRealname());
		// 创建工单处理意见
		revisitWorkOrderService.createWorkOrderOpinion(opinion);
		
		// 审核的提交结果，直接记录最终结果
		if(oldModel.getStatus().equalsIgnoreCase(Cons.WORK_ORDER_STATUS.AUDIT.name())){ // 审核的最终结果
			oldModel.setConfirmedCategory(category);
			oldModel.setStatus(Cons.WORK_ORDER_STATUS.SOLVED.name());
		}
		// 已解决
		else if(Cons.WORK_ORDER_STATUS.SOLVED.name().equalsIgnoreCase(model.getStatus())){
			oldModel.setCloseTime(new Date());
			if(oldModel.getCreateTime() != null){
				// 计算工单处理时长
				oldModel.setWorkOrderDuration((oldModel.getCloseTime().getTime() - oldModel.getCreateTime().getTime())/1000);
			}
			if(oldModel.getCategory().equalsIgnoreCase(category)){	// 客户经理指定的类别跟系统一致时			
				oldModel.setConfirmedCategory(category);			
				oldModel.setStatus(Cons.WORK_ORDER_STATUS.SOLVED.name());
			}
			else{ // 客户经理指定的类型不一致时
				oldModel.setConfirmedCategory(null);			
				oldModel.setStatus(Cons.WORK_ORDER_STATUS.AUDIT.name());
				
				OrgUser leader = orgUserService.getManagerByEmployeeName(oldModel.getOrgId(), oldModel.getCustomerManager());
				if(leader != null){
					oldModel.setAuditorId(leader.getId());
					// 微信端给领导推送消息
					String content = "跟进任务："+taskIdStr+"号，类别[ "+oldModel.getCategory()+" ]被 "+oldModel.getCustomerManager()+" 更改为[ "+category+" ]，备注说明:[ "+oldModel.getContent()+" ]， 需要您审核确认。请到元兵车助理企业号中查看处理。";
					if(StringUtils.isNotBlank(leader.getUsername())){
						asyncWorker.sendChatMessageWechat("single", leader.getUsername(), "text", content);
					}
					else{
						logger.warn("平台没有注册领导的微信号，推送消息失败！");
					}
				}
				else{
					logger.warn("无法获取客户经理领导！" + oldModel.getOrgName()+"-"+oldModel.getCustomerManager());
				}
			}
		}
        // 执行保存操作
        boolean success = revisitWorkOrderService.updateModel(oldModel);
        
        // 再次回访
        if(Cons.WORK_ORDER_STATUS.SOLVED.name().equalsIgnoreCase(model.getStatus())){
        	Long taskId = Long.parseLong(taskIdStr);
        	if(isRevisitAgain && StringUtils.isNotBlank(taskIdStr)){
        		// 重新生成task
        		revisitTaskService.cloneNewTask(taskId);
        		
        		// 异步更新当前任务统计为失效 
        		asyncWorker.updateStatistical2Inactive(taskId);
        	}
        	else{
        		// 异步更新当前任务统计-工单类别
        		asyncWorker.updateStatisticalData(taskId, oldModel);
        	}
        }
        
        // 绑定执行结果
        String msg = success?"处理工单操作成功！":"处理工单操作失败！";
        addResultMsg(request, success, msg);
        
        // 记录操作日志
 		asyncWorker.saveTraceLog(user, Cons.OPERATION.UPDATE, oldModel, "处理工单: "+oldModel.getId());
     	
        return "redirect:/wechat/revisitWorkOrder";
	}
	
	/**
	 * 微信企业号--查看报告
	 * @param wechat
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/report", method = RequestMethod.GET)
	public String report(HttpServletRequest request, ModelMap modelMap) throws Exception {		
		// 添加查询条件
		OrgUser user = getCurrentUser(request);
		if(user == null){
			return "redirect:/wechat/error?errCode=ERR_WECHAT_6";
		}
		
		Long orgId = getCachedOrgId(request, user);
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", orgId);
		modelMap.addAttribute("criteria", criteria);
		// 构建统计数据
		StatisticHelper.buildStatisticData(request, modelMap, revisitStatisticalService, revisitImportService);
		
		return wechatView(request, modelMap, "report");
	}
	
	// 微信企业号--报错页面
	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String error(HttpServletRequest request, ModelMap modelMap) throws Exception {
		String errCode = request.getParameter("errCode");
		// Error信息
		String errMsg = "";
		
		if ("ERR_WECHAT_1".equals(errCode)) {
			errMsg = "请在微信客户端打开链接";
		} else if ("ERR_WECHAT_2".equals(errCode)) {
			errMsg = "该功能还未启用";
		} else if ("ERR_WECHAT_3".equals(errCode)) {
			errMsg = "您暂时无法使用该应用，请联系元兵或检查业务系统中的账号设置。";
		} else if ("ERR_WECHAT_4".equals(errCode)) {
			errMsg = "您还没有个人名片";
		} else if ("ERR_WECHAT_5".equals(errCode)) {
			errMsg = "获取企业号信息失败";
		} else if ("ERR_WECHAT_6".equals(errCode)) {
			errMsg = "获取用户信息失败";
		} else {
			errMsg = "页面不存在或已被删除";
		}
		
		modelMap.addAttribute("errMsg", errMsg);
		return wechatView(request, modelMap, "common/error");
	}
	
	// 微信企业号--消息提示页面
	@RequestMapping(value = "/msg", method = RequestMethod.GET)
	public String msg(HttpServletRequest request, ModelMap modelMap) throws Exception {
		String msgCode = request.getParameter("msgCode");
		// 提示信息
		String result = "SUCCESS";
		String msg = "";
		String redirectUrl = "";
		
		if ("MSG_WECHAT_SUCCESS_6".equals(msgCode)) {
			result = "SUCCESS";
			msg = "工单状态更新成功";
			redirectUrl = "revisitWorkOrder/PENDING/1";
		} else if ("MSG_WECHAT_FAIL_6".equals(msgCode)) {
			result = "FAIL";
			msg = "工单状态更新失败";
			redirectUrl = "javascript:;";
		} else if ("MSG_WECHAT_FAIL_7".equals(msgCode)) {
			result = "FAIL";
			msg = "工单信息异常";
			redirectUrl = "javascript:;";
		}
		
		modelMap.addAttribute("result", result);
		modelMap.addAttribute("msg", msg);
		modelMap.addAttribute("redirectUrl", redirectUrl);
		
		return wechatView(request, modelMap, "common/msg");
	}
	
	/***
	 * 获取当前用户
	 * @param request
	 * @return
	 */
	private OrgUser getCurrentUser(HttpServletRequest request){
		OrgUser orgUser = null;
		HttpSession session = request.getSession(false);
		if(session != null){
			if (session.getAttribute("orgUser") != null) {
				orgUser = (OrgUser) session.getAttribute("orgUser");
				logger.info("[WeChat] 获取session中的orgUser: " + orgUser.getUsername());
			}
			else{
				logger.warn("[WeChat] 获取session中的orgUser失败。");				
			}
		}
		return orgUser;
	}
	
	private Long getCachedOrgId(HttpServletRequest request, OrgUser user){
		// 检查单位信息
		HttpSession session = request.getSession(false);
		if(session != null){
			if(session.getAttribute("orgId") == null){
				String agentId = (String) session.getAttribute("agentId");
				updateCachedOrgId(session, agentId);				
			}
			Long cachedOrgId = (Long) session.getAttribute("orgId");
			if(cachedOrgId != null){
				return cachedOrgId;
			}
		}
		return user.getOrgId();
	}
	
	private void updateCachedOrgId(HttpSession session, String agentId){
		// 检查单位信息
		if(session != null){
			try {
				Long fromOrgId = organizationService.getOrgIdByAppId(agentId);
				if(fromOrgId != null){
					session.setAttribute("orgId", fromOrgId);					
				}
			}
			catch (Exception e) {
				logger.error("getOrgIdByAppId error", e);
			}
		}
	}
	
	/**
	 * 微信企业号--潜客任务
	 * @param wechat
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/presaleTask", method = RequestMethod.GET)
	public String presaleTask(HttpServletRequest request, ModelMap modelMap) throws Exception {
		return presaleTaskNavbar("CONTINUES", request, modelMap);
	}
	
	/**
	 * 微信企业号 -- 潜客任务 navbar
	 * @param status
	 * @param request
	 * @param modelMap
	 */
	@RequestMapping(value = "/presaleTask/{status}", method = RequestMethod.GET)
	public String presaleTaskNavbar(@PathVariable("status")String status, HttpServletRequest request, ModelMap modelMap) throws Exception {
		OrgUser orgUser = getCurrentUser(request);
		if(orgUser == null){
			return "redirect:/wechat/error?errCode=ERR_WECHAT_6";
		}
		Long orgId = getCachedOrgId(request, orgUser);
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", orgId);
		if(!orgUser.isManager()){
			criteria.put("salesperson_id", orgUser.getId());
		}
		criteria.put("status", status.toUpperCase());
		
		// 根据选择的条件搜索
		String customerName = request.getParameter("customer_name");
		String category = request.getParameter("category");
		boolean isSaFeedback = "true".equalsIgnoreCase(request.getParameter("saFeedback"));
		
		if (V.isNotEmpty(customerName)) {
			criteria.put("customer_name", customerName);
		}
		if (V.isNotEmpty(category)) {
			criteria.put("category", category);
		}
		if (isSaFeedback) {
			criteria.put("status", Cons.PRESALE_STATUS.NOTIFY_SA.toString());
		}
		
		List<PresaleTask> tasks = (List<PresaleTask>) presaleTaskService.getAllModelList(criteria);
		
		// 整合最后一条回访记录的内容
		presaleTaskService.appendPresaleRecordList(tasks);
		modelMap.addAttribute("modelList", tasks);
		
		modelMap.addAttribute("status", status.toUpperCase());
		
		return wechatView(request, modelMap, "/presaleTask/index");
	}
	
	/***
	 * 微信企业号 -- 潜客任务 查看详细页面
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/presaleTask/view/{id}", method = RequestMethod.GET)
    public String viewPage(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap) throws Exception {
		// 获取model，追加关联record
		PresaleTask task = (PresaleTask) presaleTaskService.getModel(id);
		
		presaleTaskService.appendPresaleRecordList(task);			
		modelMap.addAttribute("model", task);
		
		PotentialCustomer customer = (PotentialCustomer) potentialCustomerService.getModel(task.getCustomerId());
		modelMap.addAttribute("customer", customer);
		
		OrgUser orgUser = getCurrentUser(request);
		modelMap.addAttribute("canEdit", orgUser.getId().equals(task.getSalespersonId()));
		
		if (checkDemoApp(request)) {
			modelMap.addAttribute("DemoApp", true);
		} else {
			modelMap.addAttribute("DemoApp", false);
		}
		
		return wechatView(request, modelMap, "/presaleTask/view");
    }
	
	/***
	 * 微信企业号 -- 潜客任务 更新跟踪信息
	 * @param dataType
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/presaleTask/update/{dataType}/{id}", method = RequestMethod.POST)
    public String update(@PathVariable("dataType")String dataType, @PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap) throws Exception {
		boolean success = false;
		OrgUser orgUser = getCurrentUser(request);
		PresaleTask task = (PresaleTask) presaleTaskService.getModel(id);
		
		if (checkDemoApp(request) && "voice".equals(dataType)) { // demo语音反馈
			// 获取model，追加关联audioMsg
			AudioMsg audioMsg = new AudioMsg();
			audioMsg.setReceiverType("CcUser");
			audioMsg.setReceiverId(task.getCurrentOwnerId());
			audioMsg.setRelObjType("PresaleTask");
			audioMsg.setRelObjId(id);
			audioMsg.setContent(request.getParameter("content"));
//			audioMsg.setAudioUrls(request.getParameter("audioUrls"));
			audioMsg.setFromOrgId(orgUser.getOrgId());
			audioMsg.setCreateBy(orgUser.getId());
			
			// 执行保存操作
	        success = audioMsgService.createModel(audioMsg);
	        
	        if(!success){
				logger.warn("保存audioMsg失败");
			}
	        
	        String audioUrls = request.getParameter("audioUrls");
	        
			if(StringUtils.isNotBlank(audioUrls) && success){
				asyncWorker.downloadAndSaveWechatAudioUrls(audioUrls, audioMsg);
				modelMap.addAttribute("success", true);
			}
			
//	        if(success){
//	        	// 异步创建操作日志
//	        	asyncWorker.saveTraceLog(orgUser, Cons.OPERATION.UPDATE, audioMsg);
//	        }
		} 
		else {
			// 获取model，追加关联record
			PresaleRecord record = new PresaleRecord();
			record.setTaskId(id);
			record.setComment(request.getParameter("comment"));
			record.setStatus(request.getParameter("status"));
			record.setStatusDetail(request.getParameter("statusDetail"));
			record.setType(Cons.PRESALE_RECORD_TYPE.SA_UPDATE.name());
			record.setNotifyType(Cons.NOTIFY_TYPE.CC.name());
			record.setCreateBy(orgUser.getId());
			record.setCreateByName(orgUser.getRealname());
			record.setCreateByType(OrgUser.class.getSimpleName());
			
			// 执行保存操作
	        success = presaleTaskService.createRecord(record);
	        if(success){
	        	// 设置SA反馈所关联的记录
	        	task.setStatus(record.getStatus());
	        	task.setStatusDetail(record.getStatusDetail());
	        	presaleTaskService.updateModel(task);
	        	
	        	// 更新最新拨打记录的refererId指向当前记录
	        	presaleTaskService.linkFeedbackRecord(record);
	        	
	        	// 异步创建操作日志
	        	asyncWorker.saveTraceLog(orgUser, Cons.OPERATION.UPDATE, record);
	        }
		}
		
        // 绑定执行结果
        String msg = success?"补充信息操作成功！":"补充信息操作失败！";
        addResultMsg(request, success, msg);
        
        return "redirect:/wechat/presaleTask/view/"+id;
    }
	
	/**
	 * 初始化Ecard
	 * @return
	 */
	private ECard getEcardModel(){
		// 二维码参数
		if(eCardModel == null){
			eCardModel = new ECard();
			eCardModel.setEcardSize(MetadataCache.getSystemProperty("ecard.size"));
			eCardModel.setEcardFill(MetadataCache.getSystemProperty("ecard.fill"));
			eCardModel.setEcardRadius(MetadataCache.getSystemProperty("ecard.radius"));
			eCardModel.setEcardMode(MetadataCache.getSystemProperty("ecard.mode"));
			eCardModel.setEcardFontcolor(MetadataCache.getSystemProperty("ecard.fontcolor"));
		}
		return eCardModel;
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
			return "redirect:/wechat/error?errCode=ERR_WECHAT_3";
		}
		HttpSession session = request.getSession(true);
		session.setAttribute("userID", wechat);
		
//		session.setAttribute("agentId", "2"); //demo
		
		// 元兵演示
		if(V.isNotEmpty(request.getParameter("demo"))){
			return "redirect:/weixin/home";
		}
		else{
			return "redirect:/wechat/app";			
		}
	}
	
	/**
	 * 演示用app入口
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/demoapp", method = RequestMethod.GET)
	public String openDemoApp(HttpServletRequest request, ModelMap modelMap) throws Exception {
		String userID = ""; // 账号
		HttpSession session = request.getSession(false);
		if(session != null){
			if (session.getAttribute("userID") != null) {
				userID = (String) session.getAttribute("userID");
				logger.info("[WeChat]获取session中的userID: " + userID);
			}
		}
	    // 获取orgUser
		OrgUser orgUser = orgUserService.getUserByWeChat(userID);
		if (orgUser == null) {
			return "redirect:/wechat/error?errCode=ERR_WECHAT_3";
		}
		session.setAttribute("orgUser", orgUser);
		
		return wechatView(request, modelMap, "demo_index");
	}
	
	/**
	 * 微信企业号 -- 潜客任务 navbar
	 * @param status
	 * @param request
	 * @param modelMap
	 */
	@RequestMapping(value = "/myPresaleTask", method = RequestMethod.GET)
	public String myPresaleTask(HttpServletRequest request, ModelMap modelMap) throws Exception {
		OrgUser orgUser = getCurrentUser(request);
		if(orgUser == null){
			return "redirect:/wechat/error?errCode=ERR_WECHAT_6";
		}
		Long orgId = getCachedOrgId(request, orgUser);
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", orgId);
		if(!orgUser.isManager()){
			criteria.put("salesperson_id", orgUser.getId());
		}
		
		// 根据选择的条件搜索
		String customerName = request.getParameter("customer_name");
		String category = request.getParameter("category");
		boolean isSaFeedback = "true".equalsIgnoreCase(request.getParameter("saFeedback"));
		
		if (V.isNotEmpty(customerName)) {
			criteria.put("customer_name", customerName);
		}
		if (V.isNotEmpty(category)) {
			criteria.put("category", category);
		}
		
		List<PresaleTask> tasks = (List<PresaleTask>) presaleTaskService.getAllModelList(criteria);
		
		// 整合最后一条回访记录的内容
		presaleTaskService.appendPresaleRecordList(tasks);
		modelMap.addAttribute("modelList", tasks);
		
		modelMap.addAttribute("saFeedback", isSaFeedback);
		
		return wechatView(request, modelMap, "/presaleTask/index");
	}
	
	/**
	 * 微信企业号--查看潜客报告
	 * @param wechat
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/presaleReport", method = RequestMethod.GET)
	public String presaleReport(HttpServletRequest request, ModelMap modelMap) throws Exception {		
		OrgUser orgUser = getCurrentUser(request);
		if(orgUser == null){
			return "redirect:/wechat/error?errCode=ERR_WECHAT_6";
		}
		Map<String, Object> criteria = new HashMap<String, Object>();
		Long orgId = getCachedOrgId(request, orgUser);
		criteria.put("from_org_id", orgId);
		if(!orgUser.isManager()){
			criteria.put("from_sa_id", orgUser.getId());
		}
		modelMap.addAttribute("criteria", criteria);
		
		// 构建统计数据
		StatisticHelper.buildPresaleStatisticData(request, modelMap, potentialCustomerService, presaleTaskService);
		
		return wechatView(request, modelMap, "presaleReport");
	}
	
	/**
	 * 企业号二维码
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/qrcode", method = RequestMethod.GET)
	public String qrcode(HttpServletRequest request, ModelMap modelMap) throws Exception {
		return wechatView(request, modelMap, "qrcode");
	}
	
	/**
	 * 企业号二维码
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/makeEcard", method = RequestMethod.GET)
	public String makeEcard(HttpServletRequest request, ModelMap modelMap) throws Exception {
		return wechatView(request, modelMap, "makeEcard");
	}
	
	/**
	 * 我的售后记录
	 * @param status
	 * @param request
	 * @param modelMap
	 */
	@RequestMapping(value = "/revisitTaskHistory", method = RequestMethod.GET)
	public String revisitTaskHistory(HttpServletRequest request, ModelMap modelMap) throws Exception {
		OrgUser orgUser = getCurrentUser(request);
		if(orgUser == null){
			return "redirect:/wechat/error?errCode=ERR_WECHAT_6";
		}
		Long orgId = getCachedOrgId(request, orgUser);
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", orgId);
		//如果设置了工单代理人，统一交给代理人
		boolean canEdit = false;
		boolean isAgent = false;
		Organization org = (Organization) organizationService.getModel(orgId);
		if(org.hasAgent()){// 如果有代理代理可处理
			if(orgUser.getId().equals(org.getAgent1Id()) || orgUser.getId().equals(org.getAgent2Id())){
				canEdit = true;
				isAgent = true;
			}
			if(!isAgent){// 非代理，销售经理是否可处理取决于设置
				criteria.put("customer_manager", orgUser.getRealname());			
				canEdit = org.isSaEditable();
			}
		}
		else{
			criteria.put("customer_manager", orgUser.getRealname());			
			canEdit = true;
		}
		//TODO 暂时查询前100条
		List<RevisitTask> tasks = (List<RevisitTask>) revisitTaskService.getLimitModelList(criteria, 100);
		
		//编辑taskId集合
		List<Long> taskIds = new ArrayList<Long>();
		//编辑workOrderIds集合
		List<Long> workOrderIds = new ArrayList<Long>();
		
		if(tasks != null && !tasks.isEmpty()){
			for(RevisitTask task : tasks){
				taskIds.add(task.getId());
				workOrderIds.add(task.getWorkOrderId());
			}
			//查询所有在taskId集合中的答案记录
			List<RevisitAnswer> allAnswers = revisitTaskService.getAnswersInTasks(taskIds);
			Map<Long, List<BaseModel>> allAnswerMap = AppHelper.list2map(allAnswers);
			
			//查询所有在taskId集合中的record记录
			List<RevisitRecord> allRecords = revisitTaskService.getRecordsInTasks(taskIds);
			Map<Long, List<BaseModel>> allRecordMap = AppHelper.list2map(allRecords);
			
			//查询所有在task集合中的所有工单记录
			List<RevisitWorkOrder> allWorkOrders = revisitTaskService.getWorkOrders(workOrderIds);
			Map<Long, List<BaseModel>> allWorkOrderMap = AppHelper.list2map(allWorkOrders);
			
			//查询所有在task集合中的所有工单的详细信息
			List<RevisitWorkOrderOpinion> allWorkOrderOpinions = revisitTaskService.getWorkOrderOpinion(workOrderIds);
			Map<Long, List<BaseModel>> allWorkOrderOpinionMap = AppHelper.list2map(allWorkOrderOpinions);
			
			//拼接结果，生成task列表
			for(RevisitTask task : tasks){
				List<RevisitAnswer> revisitAnswers = new ArrayList<RevisitAnswer>();
				List<RevisitRecord> revisitRecords = new ArrayList<RevisitRecord>();
				List<RevisitWorkOrderOpinion> revisitWorkOrderOpinions = new ArrayList<RevisitWorkOrderOpinion>();
				
				//拼接答案记录
				List<BaseModel> answers = (List<BaseModel>) allAnswerMap.get(task.getId());
				if(answers != null && !answers.isEmpty()) {
					for(BaseModel answer : answers){
						revisitAnswers.add((RevisitAnswer) answer);
					}
					task.setAnswerList(revisitAnswers);
				}
				
				//拼接record记录
				List<BaseModel> records = (List<BaseModel>) allRecordMap.get(task.getId());
				if(records != null && !records.isEmpty()) {
					for(BaseModel record : records){
						revisitRecords.add((RevisitRecord) record);
					}
					task.setRecordList(revisitRecords);
				}
				
				//拼接工单记录
				List<BaseModel> workOrders = allWorkOrderMap.get(task.getWorkOrderId());
				if(workOrders != null && !workOrders.isEmpty()){
					RevisitWorkOrder workOrder = (RevisitWorkOrder) workOrders.get(0);
					if (allWorkOrderOpinionMap != null && !allWorkOrderOpinionMap.isEmpty()) {
						//工单拼接详细信息
						List<BaseModel> workOrderOpinions = allWorkOrderOpinionMap.get(task.getWorkOrderId());
						if (workOrderOpinions != null && !workOrderOpinions.isEmpty()) {
							for(BaseModel workOrderOpinion : workOrderOpinions) {
								revisitWorkOrderOpinions.add((RevisitWorkOrderOpinion) workOrderOpinion);
							}
							workOrder.setOpinionList(revisitWorkOrderOpinions);
						}
					}
					task.setWorkOrder(workOrder);
				}
			}
		}
		
		modelMap.put("taskList", tasks);
		
		List<MetadataItem> categoryList = metadataItemService.getMetadataItems(Cons.METADATA_TYPE.WO_CATEGORY);
		modelMap.put("categoryList", categoryList);

		modelMap.put("currentUser", orgUser);
		
		modelMap.put("canEdit", canEdit);
		modelMap.put("isAgent", isAgent);
		
		return wechatView(request, modelMap, "/revisit/index");
	}
	
	/***
	 * 获取微信UserID
	 * @param request
	 * @param agentId
	 * @return
	 * @throws Exception
	 */
	private String getWechatUserId(HttpServletRequest request, String... agentIdParam) throws Exception{
		// 获取微信企业号内员工微信号
		String userID = null; // 账号
		// 获取Session
		HttpSession session = request.getSession(true);
		if(session.getAttribute("userID") != null) {
			userID = (String) session.getAttribute("userID");
			logger.info("[WeChat]获取session中的userID: " + userID);
		}
		// 获取UserId
		if(V.isEmpty(userID)){
			String code = request.getParameter("code");
			if(StringUtils.isBlank(code)){
				logger.error("[WeChat]获取企业号code失败");
				throw new WechatException("获取企业号code失败！");
			}
			// 
			logger.info("[WeChat]获取企业号code: " + code);
			if(!"authdeny".equals(code)) {
				String agentId = null;
				if(agentIdParam != null){
					agentId = agentIdParam[0];
				}
				else{
					agentId = (String) session.getAttribute("agentId");					
				}
		    	userID = WeChatHelper.getUserID(code, agentId);
		        logger.info("[WeChat]保存到session中的userID: " + userID);
		    }
		    if(StringUtils.isBlank(userID)) {
		    	throw new WechatException("获取微信用户userID失败！");
		    }
		    session.setAttribute("userID", userID);
		}
		return userID;
	}
	
	private boolean checkDemoApp(HttpServletRequest request) throws Exception{
		String agentId = "";
		HttpSession session = request.getSession(false);
		if(session != null){
			if (session.getAttribute("agentId") != null) {
				agentId = (String) session.getAttribute("agentId");
				logger.info("[WeChat]获取session中的agentId: " + agentId);
			}
		}
		
		if (agentId.equals(DEMO_APPID)) {
			return true;
		}
		return false;
	}
	
}
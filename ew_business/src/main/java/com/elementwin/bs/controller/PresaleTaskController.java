package com.elementwin.bs.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dibo.framework.service.BaseService;

import com.elementwin.api.config.Cons;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.PresaleRecord;
import com.elementwin.bs.model.PresaleTask;
import com.elementwin.bs.security.PermissionException;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.PresaleTaskService;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.bs.model.PotentialCustomer;

/***
 * 潜客数据相关操作Controller
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-21
 * Copyright 2016 www.Dibo.ltd
 */
@Controller
@RequestMapping("/presaleTask")
public class PresaleTaskController extends BaseCRUDController {
	private static Logger logger = Logger.getLogger(PresaleTaskController.class);
	
	@Autowired
	private PresaleTaskService presaleTaskService;
	
	@Autowired
	private PotentialCustomerService potentialCustomerService;
	
	@Autowired
	private OrganizationService organizationService;

	@Override
	protected String getViewPrefix() {
		return "presaleTask";
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
		// 添加当前参数
		OrgUser user = AppHelper.getCurrentUser();
		AppHelper.bindSubCompany(request, organizationService, user, modelMap);
		if(!user.isGroupUser() && !user.isManager()){
			Map<String, Object> criteria = (Map<String, Object>) modelMap.get("criteria");
			criteria.put("salesperson_id", user.getId());			
			modelMap.addAttribute("criteria", criteria);
		}
		
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
		OrgUser user = AppHelper.getCurrentUser();
		// 获取model，追加关联record
		PresaleTask task = (PresaleTask) presaleTaskService.getModel(id);
		// 检查权限
		boolean ownThisTask = user.getId().equals(task.getSalespersonId());
		modelMap.addAttribute("canEdit", ownThisTask);
		
		boolean hasPermission = false;
		// 不同单位，判断是否为集团用户
		if(!task.getOrgId().equals(user.getOrgId())){
			AppHelper.checkPermission(request, organizationService, user, task.getOrgId());
			hasPermission = true;
		}
		// 同一单位判断是否为管理或自己的任务
		else if((user.isManager() || ownThisTask)){
			hasPermission = true;
		}
		if(hasPermission){
			presaleTaskService.appendPresaleRecordList(task);
			modelMap.addAttribute("model", task);
			
			PotentialCustomer customer = (PotentialCustomer) potentialCustomerService.getModel(task.getCustomerId());
			modelMap.addAttribute("customer", customer);
			
			return super.viewPage(id, request, modelMap);
		}
		else{
			throw new PermissionException("您对该数据的访问权限校验未通过，暂时无权访问该数据！");
		}
    }
	
	/***
	 * 更新跟踪信息
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public String update(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap) throws Exception {
		OrgUser user = AppHelper.getCurrentUser();
		// 获取model，追加关联record
		PresaleRecord record = new PresaleRecord();
		record.setTaskId(id);
		record.setComment(request.getParameter("comment"));
		record.setStatus(request.getParameter("status"));
		record.setStatusDetail(request.getParameter("statusDetail"));
		record.setType(Cons.PRESALE_RECORD_TYPE.SA_UPDATE.name());
		record.setNotifyType(Cons.NOTIFY_TYPE.CC.name());
		record.setCreateBy(user.getId());
		record.setCreateByName(user.getRealname());
		record.setCreateByType(OrgUser.class.getSimpleName());

		// 执行保存操作
        boolean success = presaleTaskService.createRecord(record);
        if(success){
        	// 设置SA反馈所关联的记录
        	PresaleTask task = (PresaleTask) presaleTaskService.getModel(id);
        	task.setStatus(record.getStatus());
        	task.setStatusDetail(record.getStatusDetail());
        	presaleTaskService.updateModel(task);
        	
        	// 更新最新拨打记录的refererId指向当前记录
        	presaleTaskService.linkFeedbackRecord(record);
        	
        	// 异步创建操作日志
        	asyncWorker.saveTraceLog(user, Cons.OPERATION.UPDATE, record);
        }
        
        // 绑定执行结果
        String msg = success?"补充信息操作成功！":"补充信息操作失败！";
        addResultMsg(request, success, msg);
        
        // 绑定执行结果
        return "redirect:/"+getViewPrefix()+"/view/"+id;
    }
	
	@Override
	protected BaseService getService() {
		return presaleTaskService;
	}
}
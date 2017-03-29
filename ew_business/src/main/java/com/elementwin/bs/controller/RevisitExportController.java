package com.elementwin.bs.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.elementwin.api.config.Cons;
import com.elementwin.api.utils.DateUtils;
import com.elementwin.bs.cache.MetadataCache;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.RevisitAnswer;
import com.elementwin.bs.model.RevisitRecord;
import com.elementwin.bs.model.RevisitTask;
import com.elementwin.bs.model.RevisitWorkOrder;
import com.elementwin.common.model.QuestionItem;
import com.elementwin.bs.service.RevisitTaskService;
import com.elementwin.bs.utils.AppHelper;

import dibo.commons.file.FileHelper;
import dibo.commons.file.excel.ExcelWriter;
import dibo.framework.model.BaseModel;
import dibo.framework.service.BaseService;

/***
 * Copyright 2016 www.Dibo.ltd
 * 回访记录相关操作Controller
 * @author Yangz@dibo.ltd
 * @version v1.0, 2016/09/13
 */
@Controller
@RequestMapping("/revisitExport")
public class RevisitExportController extends BaseCRUDController {
	private static Logger logger = Logger.getLogger(RevisitExportController.class);

	@Autowired
	private RevisitTaskService revisitTaskService;
	
	private static int DEFAULT_DAYS = Integer.parseInt(MetadataCache.getSystemProperty("revisit.export.default.days"));
	
	@Override
	protected String getViewPrefix() {
		return "revisitExport";
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
		Map<String, Object> criteria = new HashMap<String, Object>();
		buildCriteriaDate(criteria, request, modelMap);
		
		modelMap.addAttribute("criteria", criteria);
		
		return view(request, modelMap, "index");
	}
	
	/**
	 * 显示首页-分页
	 * @param pageIndex 分页
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/index/{pageIndex}", method=RequestMethod.GET)
	public String indexPaging(@PathVariable("pageIndex")int pageIndex, HttpServletRequest request, ModelMap modelMap) throws Exception{
		// 添加单位id
		super.buildCriteriaWithOrgId(modelMap);
		Map<String, Object> criteria = (Map<String, Object>) modelMap.get("criteria");
		buildCriteriaDate(criteria, request, modelMap);
		
		modelMap.addAttribute("criteria", criteria);
		return super.indexPaging(pageIndex, request, modelMap);
	}
	
	/**
	 * 导出数据
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/export", method = RequestMethod.GET)
	public String export(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception{
		OrgUser user = AppHelper.getCurrentUser();
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", user.getOrgId());
		buildCriteriaWithFilter(criteria, request, modelMap);
		
		List<RevisitTask> tasks = (List<RevisitTask>) revisitTaskService.getModelList(criteria);
		if(tasks == null || tasks.isEmpty()){
			addResultMsg(request, false, "没有符合您查询条件的结果");
			return view(request, modelMap, "index");
		}
		//编辑taskId集合
		List<Long> taskIds = new ArrayList<Long>();
		//编辑workOrderIds集合
		List<Long> workOrderIds = new ArrayList<Long>();
		
		// 支持多Sheet模式
		Map<Long, List<RevisitTask>> qMap = new HashMap<Long, List<RevisitTask>>();
		// 遍历task
		for(RevisitTask task : tasks){
			taskIds.add(task.getId());
			if(task.getWorkOrderId() != null && task.getWorkOrderId() > 0L){
				workOrderIds.add(task.getWorkOrderId());
			}			
			// 遍历Map 归类task 
			Long key = task.getQuestionId();
			List<RevisitTask> list = qMap.get(key);
			if(list == null){
				list = new ArrayList<RevisitTask>();
				qMap.put(key, list);
			}
			list.add(task);
		}
		
		//查询所有在taskId集合中的答案记录
		List<RevisitAnswer> allAnswers = revisitTaskService.getAnswersInTasks(taskIds);
		Map<Long, List<BaseModel>> allAnswerMap = AppHelper.list2map(allAnswers);
		
		//查询所有在taskId集合中的record记录
		List<RevisitRecord> allRecords = revisitTaskService.getRecordsInTasks(taskIds);
		Map<Long, List<BaseModel>> allRecordMap = AppHelper.list2map(allRecords);
		
		//查询所有在task集合中的所有工单记录
		List<RevisitWorkOrder> allWorkOrders = revisitTaskService.getWorkOrders(workOrderIds);
		//附加工单处理项
		revisitTaskService.appendWorkOrderOpinions(allWorkOrders);
		
		Map<Long, List<BaseModel>> allWorkOrderMap = AppHelper.list2map(allWorkOrders);
				
		// 写入excel
		String outFileName = "回访记录_" + DateUtils.getDate(new Date()) + ".xls";
		ExcelWriter writer = new ExcelWriter(outFileName);
		for(Map.Entry<Long, List<RevisitTask>> entry : qMap.entrySet()){
			List<QuestionItem> qItems = revisitTaskService.getQuestionItems(entry.getKey());
			List<String> headers = getExportHeader(qItems);
			//获取表格所有行数据
			List<String> rows = getExportData(entry.getValue(), allAnswerMap, allRecordMap, allWorkOrderMap, qItems.size());
			// 添加sheet
			writer.addSheet(headers, rows);
		}
		// 生成
		String path = writer.generate();
		// 下载
		if(StringUtils.isNotBlank(path)){
			FileHelper.downloadFile(path, request, response);			
		}
		
		// 记录操作日志
		asyncWorker.saveTraceLog(user, Cons.OPERATION.EXPORT, tasks.get(0), "导出回访记录:"+criteria.toString());
		
		//释放所有大变量内存空间，优化性能
		tasks = null;
		taskIds = null;
		workOrderIds = null;
		allAnswers = null;
		allRecords = null;
		allWorkOrderMap = null;
		criteria = null;
		allAnswerMap = null;
		allRecordMap = null;
		qMap = null;
    	
		return null;
	}
	
	/**
	 * 查询数据
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/search", method = RequestMethod.GET)
	public String search(HttpServletRequest request, ModelMap modelMap) throws Exception{
		// 添加单位id
		super.buildCriteriaWithOrgId(modelMap);
		Map<String, Object> criteria = (Map<String, Object>) modelMap.get("criteria");
		buildCriteriaWithFilter(criteria, request, modelMap);
		buildCriteriaDate(criteria, request, modelMap);
		
		modelMap.addAttribute("criteria", criteria);
		
		return super.indexPaging(1, request, modelMap);
	}
	
	/**
	 * 初始化开始结束日期的查询条件
	 * @param criteria
	 * @param request
	 * @param modelMap
	 * @throws Exception
	 */
	private void buildCriteriaDate(Map<String, Object> criteria, HttpServletRequest request, ModelMap modelMap) throws Exception{
		setCriteriaWithBegin(criteria, request, "serviceBeginDate");
		setCriteriaWithBegin(criteria, request, "revisitBeginDate");
		
		setCriteriaWithEnd(criteria, request, "serviceEndDate");
		setCriteriaWithEnd(criteria, request, "revisitEndDate");
	}
	
	/**
	 * 设置开始日期的查询条件
	 * @param criteria
	 * @param request
	 * @param name
	 */
	private void setCriteriaWithBegin(Map<String, Object> criteria, HttpServletRequest request, String name){
		String value = request.getParameter(name);
		if(StringUtils.isBlank(value)){
			Date today = new Date();
			Date beginDate = org.apache.commons.lang3.time.DateUtils.addDays(today, -DEFAULT_DAYS);
			String beginDateStr = DateUtils.getDate(beginDate);
			criteria.put(name, beginDateStr);
		}
	}
	
	/**
	 * 设置结束日期的查询条件
	 * @param criteria
	 * @param request
	 * @param name
	 */
	private void setCriteriaWithEnd(Map<String, Object> criteria, HttpServletRequest request, String name){
		String value = request.getParameter(name);
		if(StringUtils.isBlank(value)){
			Date today = new Date();
			String endDateStr  = DateUtils.getDate(today);
			criteria.put(name, endDateStr );
		}
	}
	
	/**
	 * 创建筛选条件
	 * @param criteria
	 * @param request
	 * @param modelMap
	 * @throws Exception
	 */
	private void buildCriteriaWithFilter(Map<String, Object> criteria, HttpServletRequest request, ModelMap modelMap) throws Exception {
		analysisCriteria(criteria, request, "serviceBeginDate");
		analysisCriteria(criteria, request, "serviceEndDate");
		analysisCriteria(criteria, request, "revisitBeginDate");
		analysisCriteria(criteria, request, "revisitEndDate");
		analysisCriteria(criteria, request, "customer_manager");
		analysisCriteria(criteria, request, "service_type");
		modelMap.put("criteria", criteria);
	}
	
	/**
	 * 分析筛选数据是否合法
	 * @param criteria
	 * @param request
	 * @param name
	 * @throws Exception
	 */
	private void analysisCriteria(Map<String, Object> criteria, HttpServletRequest request, String name) throws Exception{
		String value = request.getParameter(name);
		if(StringUtils.isNotBlank(value)){
			criteria.put(name, value);
		}
	}
	
	private List<String> getExportData(List<RevisitTask> tasks, Map<Long, List<BaseModel>> allAnswerMap, 
			Map<Long, List<BaseModel>> allRecordMap, Map<Long, List<BaseModel>> allWorkOrderMap, int questionItemCount) throws Exception{
		List<String> taskStrs = new ArrayList<String>();
		for(RevisitTask task : tasks){
			//处理答案相关
			List<BaseModel> answers = allAnswerMap != null? allAnswerMap.get(task.getId()) : null;

			int beginIdx = 21;
			int fluidLength = beginIdx + (questionItemCount*2);
			//按excelHelper中相关次序写入相应字段
			String [] records = new String[fluidLength + 8]; //不固定的问题列后面增加8列
			records[0] = task.getId().toString();
			records[1] = task.getServiceEndDate();
			records[2] = task.getIsDealerStore();
			records[3] = task.getCarFrameNo();
			records[4] = task.getCarLicenseNo();
			records[5] = task.getCarBrand();
			records[6] = task.getCarModelType();
			records[7] = task.getCustomerManager();
			records[8] = task.getCarDriver();
			records[9] = task.getCarDriverPhone();
			records[10] = task.getCarOwner();
			records[11] = task.getAddress();
			records[12] = task.getServiceType();
			records[13] = task.getMileage().toString();
			records[14] = task.getIsMember();
			records[15] = task.getIsOutInsure();
			
			records[16] = DateUtils.getDateTime(task.getUpdateTime());
			
			//判定是否接通
			List<BaseModel> rcs = (List<BaseModel>) allRecordMap.get(task.getId());
			String isCall = "否";
			if(rcs != null && !rcs.isEmpty()){
				for(BaseModel rc : rcs){
					if(((RevisitRecord) rc).getCallDuration() > 0){
						isCall = "是";
						break;
					}
				}
			}
		
			records[17] = isCall;
			records[18] = task.getOwnerName() != null? task.getOwnerName() : "-";
			
			//写入回访结果
			records[19] = task.getStatusName();
			
			//写入备注
			String comment = "";
			if(rcs != null && !rcs.isEmpty()){
				for(BaseModel rc : rcs){
					RevisitRecord record = (RevisitRecord) rc;
					comment += DateUtils.getDateTime(record.getCreateTime()) + " - " + record.getCcuserName() + " - " + record.getStatusName() + "; ";
				}
			}
			records[20] = comment;

			//写入答案
			if(answers != null && !answers.isEmpty()){
				String[] answerArray = new String[questionItemCount];
				String[] scoreArray = new String[questionItemCount];
				if(answers.size() != questionItemCount){
					logger.warn("导出答案与问题项数量不匹配！taskId="+task.getId()+", questionItemCount="+questionItemCount+", answerSize="+answers.size());
				}
				for(int i=0; i<answers.size(); i++){
					RevisitAnswer answer = (RevisitAnswer) answers.get(i);
					//对checkbox多选的用分号分隔
					String rs = StringUtils.replaceOnce(answer.getAnswer(), "_", ",");
					if(i >= questionItemCount){
						break;
					}
					answerArray[i] = rs;
					scoreArray[i] = answer.getScore()>0 ? String.valueOf(answer.getScore()) : "0";
				}
				for(int i=0; i<questionItemCount; i++){
					records[beginIdx++] = answerArray[i];
					records[beginIdx++] = scoreArray[i];
				}
			}
			String category = "-", content = "-";
			
			//update2016_12_30
			RevisitWorkOrder workOrder = null;
			//end_update
			
			if(allWorkOrderMap != null && !allWorkOrderMap.isEmpty()){
				List<BaseModel> workOrders = allWorkOrderMap.get(task.getWorkOrderId());
				if(workOrders != null && !workOrders.isEmpty()){
					workOrder = (RevisitWorkOrder) workOrders.get(0);
					category = workOrder.getCategory();
					content = workOrder.getContent();
				}
			}
			// 添加工单类型及描述
			records[fluidLength] = String.valueOf(task.getTotalScore());
			records[fluidLength+1] = category;
			records[fluidLength+2] = content;
			
			//update2016_12_30
			if(workOrder != null){
				records[fluidLength+3] = workOrder.getStatusTrace(); //客述变更情况
				records[fluidLength+4] = workOrder.getProgressTrace(); //解决过程
				//跟进问题创建时间
				records[fluidLength + 5] = DateUtils.getDateTime(workOrder.getCreateTime());
				records[fluidLength + 6] = workOrder.getCloseTime() != null? DateUtils.getDateTime(workOrder.getCloseTime()) : "-"; //关闭时间
				records[fluidLength + 7] = workOrder.getCloseTime() != null? workOrder.getCloseByName() : "-"; //关闭人
			}
			//end_update
			taskStrs.add(StringUtils.join(records, Cons.SPLIT_FLAG));
		}
		return taskStrs;
	}
	
	/**
	 * 获得导出头部标题
	 * @param questionItems
	 * @return
	 * @throws Exception
	 */
	private List<String> getExportHeader(List<QuestionItem> questionItems) throws Exception{
		List<String> headers = new ArrayList<String>();
		headers.add("ID");
		headers.add("交车日期");
		headers.add("原店");
		headers.add("车架号");
		headers.add("车牌号");
		headers.add("车辆品牌");
		headers.add("车型");
		headers.add("客户经理");
		headers.add("送修人");
		headers.add("送修人手机");
		headers.add("车主姓名");
		headers.add("现有住址");
		headers.add("维修类型");
		headers.add("到店里程数");
		//-----
		headers.add("是否会员");
		headers.add("是否过保");
		//-----
		headers.add("回访日期");
		headers.add("是否接通");
		//-----
		headers.add("回访员");		
		headers.add("回访结果");
		headers.add("备注");
		int i = 0;
	    for(i=0; i<questionItems.size(); i++){
	    	String title = questionItems.get(i).getTitle();
	    	if(title == null){
	    		title = "-";
	    	}
	    	headers.add(title);
	    	headers.add("得分");
	    }
	    //------
	    headers.add("总得分");
	    
	    headers.add("客述细分");
	    headers.add("跟进任务描述");
	    
	
	    //update2016_12_30
	    headers.add("客述变更情况");
	    headers.add("解决过程");
	    headers.add("跟进问题创建时间");
	    headers.add("关闭时间");
	    headers.add("关闭人");
	    //end_update
		
		return headers;
	}
	
	@Override
	protected BaseService getService() {
		return revisitTaskService;
	}

	@Override
	protected boolean canDelete(BaseModel model) {
		return false;
	}
}
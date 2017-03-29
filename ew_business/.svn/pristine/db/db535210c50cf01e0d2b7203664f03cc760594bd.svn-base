package com.elementwin.bs.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.elementwin.api.config.Cons;
import com.elementwin.api.utils.DateUtils;
import com.elementwin.api.utils.ExcelHelper;
import com.elementwin.bs.model.File;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.PotentialCustomer;
import com.elementwin.bs.model.PresaleImport;
import com.elementwin.bs.security.PermissionException;
import com.elementwin.bs.service.FileService;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.PresaleImportService;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.bs.utils.TaskHelper;
import com.elementwin.api.utils.SpellHelper;
import com.elementwin.common.model.PresaleImportData;

import dibo.framework.model.BaseModel;
import dibo.framework.service.BaseService;
import dibo.framework.utils.V;

/***
 * 数据导入相关操作Controller
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-15
 * Copyright 2016 www.Dibo.ltd
 */
@Controller
@RequestMapping("/presaleImport")
public class PresaleImportController extends BaseCRUDController {
	private static Logger logger = Logger.getLogger(PresaleImportController.class);
	
	@Autowired
	private PresaleImportService presaleImportService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private OrgUserService orgUserService;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private PotentialCustomerService potentialCustomerService;
	
	@Override
	protected String getViewPrefix() {
		return "presaleImport";
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
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", user.getOrgId());
		criteria.put("create_by", user.getId());		
		
		String newHandleTimeStr = DateUtils.getDate(new Date());
		criteria.put("newHandleTime", newHandleTimeStr);
		
		modelMap.addAttribute("criteria", criteria);

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
		// 检查权限
		OrgUser user = AppHelper.getCurrentUser();
		PresaleImport importObj = (PresaleImport) presaleImportService.getModel(id);
		if(importObj.getOrgId().equals(user.getOrgId()) && user.getId().equals(importObj.getCreateBy())){
			// 获取data models
			Map<String, Object> criteria = new HashMap<String, Object>();
			criteria.put("import_id", id);
			
			List<? extends PresaleImportData> dataModels = presaleImportService.getImportDataList(criteria);
			modelMap.addAttribute("modelList", dataModels);
			
			
			return super.viewPage(id, request, modelMap);			
		}
		else{
			throw new PermissionException("您没有权限查看该数据！");
		}
    }
	
	
	/**
	 * 设置请求协助
	 * @param request
	 * @param modelMap
	 * @return index
	 * @throws Exception
	 */
	@RequestMapping(value = "/callHelp/{id}", method = RequestMethod.GET)
	public String callHelp(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap) throws Exception{
		PresaleImport model = (PresaleImport) presaleImportService.getModel(id);
		model.setRequestHelp(true);
		boolean success = presaleImportService.updateModel(model);
		String msg = success ? "请求协助成功" : "请求协助失败";
		addResultMsg(request, success, msg);
		return indexPaging(1, request, modelMap);
	}
	
	
	/***
	 * 创建的后台处理
	 * @param PresaleImport
	 * @param result
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(HttpServletRequest request, ModelMap modelMap) throws Exception {  
		OrgUser user = AppHelper.getCurrentUser();
		PresaleImport model = new PresaleImport();
		// 保存Excel
		String uuid = saveExcelFile(request, model);
		if(StringUtils.isBlank(uuid)){
			AppHelper.bindError(modelMap, "保存数据文件失败，请检查您上传的文件！");
			return index(request, modelMap);
		}
		
		File fileModel = fileService.getFileByUuid(uuid);
		if(fileModel == null){			
			AppHelper.bindError(modelMap, "无法获取到上传的数据文件！");
			return index(request, modelMap);
		}
		
		// 创建上传记录
		PresaleImport presaleImport = null;
		String modelId = request.getParameter("modelId");
		boolean isRepair = StringUtils.isNotBlank(modelId); 
		if(isRepair){
			presaleImport = (PresaleImport) presaleImportService.getModel(Long.parseLong(modelId));
		}
		else{
			presaleImport = new PresaleImport();
		}
		presaleImport.setFileUuid(fileModel.getUuid());
		presaleImport.setFileName(fileModel.getName());			
		presaleImport.setOrgId(user.getOrgId());
		presaleImport.setCreateBy(user.getId());
		String comment = request.getParameter("comment");
		if(StringUtils.isNotBlank(comment)){	
			presaleImport.setComment(comment);			
		}
		String newHandleTime = request.getParameter("newHandleTime");
		// 非预览模式  直接保存
		if(!isRepair){
			presaleImportService.createModel(presaleImport);			
		}
		
		try{
			List<PresaleImportData> modelList = new ArrayList<PresaleImportData>();
			MultipartFile file = getUploadedFile(request);
			if(file != null){
				// 解析Excel返回封装好的结果
				List<String> invalidCells = ExcelHelper.parseExcel2PresaleData(file, modelList, newHandleTime);
				if(V.isNotEmpty(invalidCells)){
					modelMap.addAttribute("resultInfo", invalidCells.toString());
				}
				else{
					String redirectUrl = validateAndSaveData(presaleImport, modelList, request, modelMap, false, false);
					if(V.isNotEmpty(redirectUrl)){
						return redirectUrl;
					}
					// 记录操作日志
					asyncWorker.saveTraceLog(user, isRepair?Cons.OPERATION.UPDATE : Cons.OPERATION.CREATE, presaleImport, "导入潜客数据: "+presaleImport.getCount()+"条");
					
					return "redirect:/presaleImport/index";
				}
			}
		}
		catch(Exception e){
			modelMap.addAttribute("resultInfo", "解析数据出错，原因为：" + e.getMessage());
			logger.error("处理Excel文件异常:", e);
		}
		// 返回处理结果
        String errorinfo = (String)modelMap.get("resultInfo");
        if(StringUtils.isNotBlank(errorinfo)){
        	presaleImport.setFailReason(errorinfo);
        	if(!presaleImport.getStatus().equals(Cons.UPLOAD_STATUS_DUPLICATE)){
            	presaleImport.setStatus(Cons.UPLOAD_STATUS_FAIL);
        	}
        	presaleImportService.updateModel(presaleImport);
        	
        	addResultMsg(request, false, errorinfo);
        }
        // 清空缓存
        HttpSession session = request.getSession(false);
		if(session != null){
			session.removeAttribute("presaleImport");
			session.removeAttribute("presaleImportData");			
		}
		
        return "redirect:/presaleImport/index";
    }
	
	/***
	 * 检查数据是否合法
	 * @param presaleImport
	 * @param modelList
	 * @param request
	 * @param modelMap
	 * @return
	 */
	public String validateAndSaveData(PresaleImport presaleImport, List<PresaleImportData> modelList, HttpServletRequest request, ModelMap modelMap, boolean isPreview, boolean isFocusImport)
		throws Exception{
		if(V.isEmpty(modelList)){
			return null;
		}
		/*判定是否重复导入*/
		OrgUser user = AppHelper.getCurrentUser();
		
		List<Integer> invalidRowIndex = new ArrayList<Integer>();
		List<String> realnameList = new ArrayList<String>();
		for (PresaleImportData row : modelList){
			row.setOrgId(presaleImport.getOrgId());
			row.setImportId(presaleImport.getId());
			row.setCreateBy(user.getId());
			// 添加所有销售员
			if(!realnameList.contains(row.getSalespersonName())){
				realnameList.add(row.getSalespersonName());
			}
			if(!Cons.getCustomerCategoryList().contains(row.getCustomerCategory())){
				invalidRowIndex.add(row.getRowIndex());
			}
		}
		/* 检查潜客类别是否合法
		if(invalidRowIndex != null && !invalidRowIndex.isEmpty()){
			String msg = "序号为[ " + StringUtils.join(invalidRowIndex, ",") + "]的潜客类别数据有误，请修改正确后重试！";
			modelMap.addAttribute("resultInfo", msg);
			
			presaleImport.setFailReason(msg);
			presaleImport.setStatus(Cons.UPLOAD_STATUS_FAIL);
			presaleImport.setCount(0);
			
			addResultMsg(request, false, msg);
			
			if(isPreview){
				modelMap.addAttribute("modelList", modelList);
			}
			else{
				presaleImportService.updateModel(presaleImport);
				return "redirect:/presaleImport/index";				
			}
		}*/
		// 检查是否所有销售员都存在？
		Map<String, Long> userMap = orgUserService.getUserName2IdMap(presaleImport.getOrgId(), realnameList);
		List<String> invalidNames = new ArrayList<String>();
		for (PresaleImportData row : modelList){
			Long userId = userMap.get(row.getSalespersonName());
			if(userId != null){
				row.setSalespersonId(userId);
			}
			else{
				invalidNames.add(row.getSalespersonName());
			}
		}
		if(invalidNames.size() > 0){
			String msg = "销售顾问 [ " + StringUtils.join(invalidNames, ",") + "] 不存在，请检查！";
			modelMap.addAttribute("resultInfo", msg);
			
			presaleImport.setFailReason(msg);
			presaleImport.setStatus(Cons.UPLOAD_STATUS_FAIL);
			presaleImport.setCount(0);
			
			addResultMsg(request, false, msg);
			if (!isFocusImport) {
				if(isPreview){
					modelMap.addAttribute("modelList", modelList);
				}
				else{
					presaleImportService.updateModel(presaleImport);
					return "redirect:/presaleImport/index";
				}
			}
			else {
				return "redirect:/presaleImportData/index";
			}
		}
		
		// 判断是否重复
		List<String> rowIndex = presaleImportService.findDuplicateData(modelList);
		if(rowIndex != null && !rowIndex.isEmpty()){
			String msg = "序号为[ " + StringUtils.join(rowIndex, ",") + "]的数据在系统中已存在(或与导入记录重复)";
			if (!isFocusImport) {
				msg = msg + "，请去除重复数据后重试！";
			}
			modelMap.addAttribute("resultInfo", msg);
			
			presaleImport.setFailReason(msg);
			presaleImport.setStatus(Cons.UPLOAD_STATUS_DUPLICATE);
			presaleImport.setCount(0);
			
			addResultMsg(request, false, msg);
			if(isPreview){
				modelMap.addAttribute("modelList", modelList);
			}
			else{
				if (!isFocusImport) {
					presaleImportService.updateModel(presaleImport);
					return "redirect:/presaleImport/index";
				}
				else {
					return "redirect:/presaleImportData/index";
				}
			}
		}
		else{
			if(isPreview){
				// 预览成功，为下一步保存做准备
				HttpSession session = request.getSession(false);
				if(session != null){
					session.setAttribute("presaleImport", presaleImport);
					session.setAttribute("presaleImportData", modelList);
				}
				addResultMsg(request, true, "尝试解析数据文件成功，共有 "+modelList.size()+" 条数据可导入！");
				modelMap.addAttribute("modelList", modelList);
			}
			else{
				// 分配任务
				TaskHelper.assignTasks(presaleImportService, modelList, presaleImport.getOrgId());
				// 保存
				boolean isSucess = presaleImportService.batchCreateData(modelList) > 0;
				if(!isSucess){
					presaleImport.setFailReason("保存导入数据失败！");
					presaleImport.setStatus(Cons.UPLOAD_STATUS_FAIL);
					presaleImport.setCount(0);
					
					modelMap.addAttribute("resultInfo", "保存导入数据失败！");
				}
				else{
					presaleImport.setStatus(Cons.UPLOAD_STATUS_SUCCESS);
					presaleImport.setCount(modelList.size());
					
					// 调用异步创建潜客信息
					asyncWorker.batchCreatePotentialCustomers(presaleImport.getOrgId());
				}
				
				String msg = isSucess ? "数据导入成功！ 共成功导入 "+modelList.size()+"条记录！" : "保存数据失败";
				addResultMsg(request, isSucess, msg);						
				
				if (!isFocusImport) {
					presaleImportService.updateModel(presaleImport);		
				}
			}			
		}
		return null;
	}
	
	/***
	 * 显示更新页面
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/repair/{id}", method = RequestMethod.GET)
    public String repairPage(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap) throws Exception {  
		PresaleImport revisitImport = (PresaleImport) presaleImportService.getModel(id);
		modelMap.addAttribute("model", revisitImport);
		modelMap.put("isRepair", true);
		
		return view(request, modelMap, "repair");
    }
	
	/***
	 * 预览数据页面
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/preview", method = RequestMethod.POST)
	public String preview(HttpServletRequest request, ModelMap modelMap) throws Exception{
		OrgUser user = AppHelper.getCurrentUser();
    	PresaleImport model = new PresaleImport();
		// 保存Excel
		String uuid = saveExcelFile(request, model);
		if(StringUtils.isBlank(uuid)){
			AppHelper.bindError(modelMap, "保存数据文件失败，请检查您上传的文件！");
			return index(request, modelMap);
		}
		
		File fileModel = fileService.getFileByUuid(uuid);
		if(fileModel == null){			
			AppHelper.bindError(modelMap, "无法获取到上传的数据文件！");
			return index(request, modelMap);
		}
		// 临时创建上传记录model
		PresaleImport presaleImport = null;
		String modelId = request.getParameter("modelId");
		boolean isRepair = StringUtils.isNotBlank(modelId); 
		if(isRepair){
			presaleImport = (PresaleImport) presaleImportService.getModel(Long.parseLong(modelId));
		}
		else{
			presaleImport = new PresaleImport();
		}
		presaleImport.setFileUuid(fileModel.getUuid());
		presaleImport.setFileName(fileModel.getName());			
		presaleImport.setOrgId(user.getOrgId());
		presaleImport.setCreateBy(user.getId());
		// 非预览模式  直接保存
		String comment = request.getParameter("comment");
		if(StringUtils.isNotBlank(comment)){
			presaleImport.setComment(comment);			
		}
		String newHandleTime = request.getParameter("newHandleTime");
		
		try{
			MultipartFile file = getUploadedFile(request);
			if(file != null){
				List<PresaleImportData> modelList = new ArrayList<PresaleImportData>();
				// 解析Excel返回封装好的结果 TODO: 从本地文件中读取
				List<String> invalidCells = ExcelHelper.parseExcel2PresaleData(file, modelList, newHandleTime);
				if(V.isNotEmpty(invalidCells)){
					modelMap.addAttribute("resultInfo", invalidCells.toString());
				}
				else{
					validateAndSaveData(presaleImport, modelList, request, modelMap, true, false);
				}
			}
		}
		catch(Exception e){
			modelMap.addAttribute("resultInfo", "解析数据出错，原因为：" + e.getMessage());
			logger.error("处理Excel文件异常:", e);
		}
		
		// 返回处理结果
        String errorinfo = (String)modelMap.get("resultInfo");
        boolean canImport = V.isEmpty(errorinfo);
        modelMap.addAttribute("canImport", canImport);
        if(!canImport){
        	presaleImport.setFailReason(errorinfo);
        	presaleImport.setStatus(Cons.UPLOAD_STATUS_FAIL);
        	addResultMsg(request, false, errorinfo);
        }
        
		return view(request, modelMap, "preview");
	}
	
	/***
	 * 预览成功的保存页面
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/previewSave", method = RequestMethod.POST)
	public String previewSave(HttpServletRequest request, ModelMap modelMap) throws Exception{
		HttpSession session = request.getSession(false);
		if(session != null){
			PresaleImport presaleImport = (PresaleImport) session.getAttribute("presaleImport");
			List<PresaleImportData> models = (List<PresaleImportData>) session.getAttribute("presaleImportData");
			if(presaleImport == null || models == null){
				addResultMsg(request, false, "无法获取当前上传的数据文件！");
				return "redirect:/presaleImport/index";
			}
			
			OrgUser user = AppHelper.getCurrentUser();
			presaleImport.setCreateBy(user.getId());
			presaleImport.setOrgId(user.getOrgId());				
			if(presaleImport.getId() == null){
				presaleImportService.createModel(presaleImport);
				// 设置字表关联
				for(PresaleImportData m : models){
					m.setImportId(presaleImport.getId());
				}
			}
			// 分配任务
			TaskHelper.assignTasks(presaleImportService, models, presaleImport.getOrgId());
			// 保存
			boolean isSucess = presaleImportService.batchCreateData(models) > 0;
			if(!isSucess){
				presaleImport.setFailReason("保存导入数据失败！");
				presaleImport.setStatus(Cons.UPLOAD_STATUS_FAIL);
				presaleImport.setCount(0);
			}
			else{
				presaleImport.setStatus(Cons.UPLOAD_STATUS_SUCCESS);
				presaleImport.setCount(models.size());
				
				// 调用异步创建潜客信息
				asyncWorker.batchCreatePotentialCustomers(presaleImport.getOrgId());
			}
			presaleImport.setCreateBy(user.getId());
			presaleImportService.updateModel(presaleImport);
			
			String msg = isSucess ? "数据导入成功！ 共成功导入 "+models.size()+"条记录！" : "保存数据失败";
			if(!isSucess){
			}
			
			addResultMsg(request, isSucess, msg);
			
			session.removeAttribute("presaleImport");
			session.removeAttribute("presaleImportData");
		}
		else{
			addResultMsg(request, false, "未获取到预处理的数据！");			
		}
		
		return "redirect:/presaleImport/index";
	}
	
	// 读取表格数据之前的处理
	private String saveExcelFile(HttpServletRequest request, BaseModel model) throws Exception{
		// 保存Excel
		String fileAccessUrl = super.saveFiles(request, model);
		if(StringUtils.isNotBlank(fileAccessUrl)){
			return fileAccessUrl.substring(fileAccessUrl.lastIndexOf("/")+1);
		}
		return null;
	}
	
	/***
	 * 从request获取到上传的第一个文件
	 * @param request
	 * @return
	 */
	private MultipartFile getUploadedFile(HttpServletRequest request){
		List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles("attachedFiles");
    	if(V.isNotEmpty(files)){
    		for(MultipartFile f : files){
    			if(f != null && f.getOriginalFilename() != null){
    				return f;
    			}
    		}
    	}
    	return null;
	}
	
	@Override
	protected BaseService getService() {
		return presaleImportService;
	}
	
	/**
	 * 批量更新名字首字母
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateFirstSpell", method = RequestMethod.GET)
    public String updateFirstSpell(HttpServletRequest request, ModelMap modelMap) throws Exception {  
		// 获取所有潜客列表
    	Map<String, Object> criteria = super.buildQueryCriteria(request, 0);
		List<PotentialCustomer> potentialCustomers = (List<PotentialCustomer>) potentialCustomerService.getModelList(criteria);
		
		if(potentialCustomers != null && !potentialCustomers.isEmpty()){
			for(PotentialCustomer potentialCustomer : potentialCustomers){
				String firstSpell = SpellHelper.getFirstSpell(potentialCustomer.getName(), SpellHelper.TYPE.SINGLE, 1);
				potentialCustomer.setFirstSpell(firstSpell.toUpperCase());
				// 更新潜客姓名首字母
				potentialCustomerService.updateFirstSpell(potentialCustomer);
			}
		}
		
		return "redirect:/presaleImport/index";
    }
}
package com.elementwin.bs.controller;

import java.util.ArrayList;
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

import dibo.framework.model.BaseModel;
import dibo.framework.service.BaseService;
import dibo.framework.utils.V;

import com.elementwin.bs.model.RevisitImport;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.api.config.Cons;
import com.elementwin.api.utils.ExcelHelper;
import com.elementwin.bs.model.File;
import com.elementwin.bs.utils.AppHelper;
import com.elementwin.common.model.RevisitImportData;
import com.elementwin.bs.service.FileService;
import com.elementwin.bs.service.OrgUserService;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.bs.service.RevisitImportService;

/***
 * Copyright 2016 www.Dibo.ltd
 * 回访数据上传相关操作Controller
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/31
 */
@Controller
@RequestMapping("/revisitImport")
public class RevisitImportController extends BaseCRUDController {
	private static Logger logger = Logger.getLogger(RevisitImportController.class);
	
	@Autowired
	private RevisitImportService revisitImportService;
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private OrgUserService orgUserService;
	
	@Autowired
	private FileService fileService;
	
	@Override
	protected String getViewPrefix() {
		return "revisitImport";
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
	 * 设置请求协助
	 * @param request
	 * @param modelMap
	 * @return index
	 * @throws Exception
	 */
	@RequestMapping(value = "/callHelp/{id}", method = RequestMethod.GET)
	public String callHelp(@PathVariable("id")Long id, HttpServletRequest request, ModelMap modelMap) throws Exception{
		RevisitImport model = (RevisitImport) revisitImportService.getModel(id);
		model.setRequestHelp(true);
		boolean success = revisitImportService.updateModel(model);
		String msg = success ? "请求协助成功" : "请求协助失败";
		addResultMsg(request, success, msg);
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
		// 获取data models
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("data_import_id", id);
		
		List<RevisitImportData> dataModels = (List<RevisitImportData>) revisitImportService.getDataList(criteria);
		modelMap.addAttribute("modelList", dataModels);
		
		return super.viewPage(id, request, modelMap);
    }
	
	/***
	 * 创建的后台处理
	 * @param revisitImport
	 * @param result
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(HttpServletRequest request, ModelMap modelMap) throws Exception {  
		OrgUser user = AppHelper.getCurrentUser();
    	RevisitImport model = new RevisitImport();
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
		RevisitImport revisitImport = null;
		String modelId = request.getParameter("modelId");
		boolean isRepair = StringUtils.isNotBlank(modelId); 
		if(isRepair){
			revisitImport = (RevisitImport) revisitImportService.getModel(Long.parseLong(modelId));
		}
		else{
			revisitImport = new RevisitImport();
		}
		revisitImport.setFileUuid(fileModel.getUuid());
		revisitImport.setFileName(fileModel.getName());			
		revisitImport.setOrgId(user.getOrgId());
		revisitImport.setCreateBy(user.getId());
		String comment = request.getParameter("comment");
		if(StringUtils.isNotBlank(comment)){	
			revisitImport.setComment(comment);			
		}
		// 非预览模式  直接保存
		if(!isRepair){
			revisitImportService.createModel(revisitImport);			
		}
		
		try{
			List<RevisitImportData> modelList = new ArrayList<RevisitImportData>();
			MultipartFile file = getUploadedFile(request);
			if(file != null){
				// 解析Excel返回封装好的结果 TODO: 从本地文件中读取
				List<String> invalidCells = ExcelHelper.parseExcel2RevisitData(file, modelList);
				if(V.isNotEmpty(invalidCells)){
					modelMap.addAttribute("resultInfo", invalidCells.toString());
	            	revisitImport.setStatus(Cons.UPLOAD_STATUS_FAIL);
				}
				else{
					/*判定是否重复导入*/	
					if(modelList != null && !modelList.isEmpty()){
						List<String> realnameList = new ArrayList<String>();
						for (RevisitImportData row : modelList){
							row.setOrgId(revisitImport.getOrgId());
							row.setDataImportId(revisitImport.getId());
							// 添加所有销售
							if(!realnameList.contains(row.getCustomerManager())){
								realnameList.add(row.getCustomerManager());
							}
						}
						// 检查是否所有销售员都存在？
						Map<String, Long> userMap = orgUserService.getUserName2IdMap(revisitImport.getOrgId(), realnameList);
						List<String> invalidNames = new ArrayList<String>();
						for (RevisitImportData row : modelList){
							Long userId = userMap.get(row.getCustomerManager());
							if(userId == null){
								invalidNames.add(row.getCustomerManager());
							}
						}
						if(invalidNames.size() > 0){
							String msg = "客户经理 [ " + StringUtils.join(invalidNames, ",") + "] 不存在，请检查！";
							modelMap.addAttribute("resultInfo", msg);
							modelMap.addAttribute("resultInfo", msg);
							
							revisitImport.setFailReason(msg);
							revisitImport.setStatus(Cons.UPLOAD_STATUS_FAIL);
							revisitImport.setCount(0);
							
							revisitImportService.updateModel(revisitImport);

							addResultMsg(request, false, msg);
							return "redirect:/revisitImport/index";
						}
						else{
							// 判断是否重复
							List<String> rowIndex = revisitImportService.findDuplicateData(modelList);
							if(rowIndex != null && !rowIndex.isEmpty()){
								String msg = "序号为[ " + StringUtils.join(rowIndex, ",") + "]的数据与系统中已上传数据重复，请去除重复数据后重试！";
								modelMap.addAttribute("resultInfo", msg);
								
								revisitImport.setFailReason(msg);
								revisitImport.setStatus(Cons.UPLOAD_STATUS_DUPLICATE);
								revisitImport.setCount(0);
								
								revisitImportService.updateModel(revisitImport);
								
								addResultMsg(request, false, msg);
								return "redirect:/revisitImport/index";
							}
							else{
								// 保存
								boolean isSucess = revisitImportService.batchCreateData(modelList) > 0;
								if(!isSucess){
									revisitImport.setFailReason("保存导入数据失败！");
									revisitImport.setStatus(Cons.UPLOAD_STATUS_FAIL);
									revisitImport.setCount(0);
									
									modelMap.addAttribute("resultInfo", "保存导入数据失败！");
								}
								else{
									revisitImport.setStatus(Cons.UPLOAD_STATUS_SUCCESS);
									revisitImport.setCount(modelList.size());
								}
								
								String msg = isSucess ? "数据导入成功！ 共成功导入 "+modelList.size()+"条记录！" : "保存数据失败";
								addResultMsg(request, isSucess, msg);						
								
								revisitImportService.updateModel(revisitImport);	
							}
						}
					}
					
					// 记录操作日志
					asyncWorker.saveTraceLog(user, isRepair?Cons.OPERATION.UPDATE : Cons.OPERATION.CREATE, revisitImport, "导入数据: "+revisitImport.getCount()+"条");
					
					return "redirect:/revisitImport/index";
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
        	revisitImport.setFailReason(errorinfo);
        	if(!revisitImport.getStatus().equals(Cons.UPLOAD_STATUS_DUPLICATE)){
            	revisitImport.setStatus(Cons.UPLOAD_STATUS_FAIL);
        	}
			revisitImportService.updateModel(revisitImport);
        	
        	addResultMsg(request, false, errorinfo);
        }
        // 清空缓存
        HttpSession session = request.getSession(false);
		if(session != null){
			session.removeAttribute("revisitImport");
			session.removeAttribute("revisitImportData");			
		}
		
        return "redirect:/revisitImport/index";
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
		RevisitImport revisitImport = (RevisitImport) revisitImportService.getModel(id);
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
    	RevisitImportData model = new RevisitImportData();
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
		RevisitImport revisitImport = null;
		String modelId = request.getParameter("modelId");
		boolean isRepair = StringUtils.isNotBlank(modelId); 
		if(isRepair){
			revisitImport = (RevisitImport) revisitImportService.getModel(Long.parseLong(modelId));
		}
		else{
			revisitImport = new RevisitImport();
		}
		revisitImport.setFileUuid(fileModel.getUuid());
		revisitImport.setFileName(fileModel.getName());			
		revisitImport.setOrgId(user.getOrgId());
		revisitImport.setCreateBy(user.getId());
		// 非预览模式  直接保存
		String comment = request.getParameter("comment");
		if(StringUtils.isNotBlank(comment)){	
			revisitImport.setComment(comment);			
		}
		
		boolean canImport = true;	
		try{
			MultipartFile file = getUploadedFile(request);
			if(file != null){
				List<RevisitImportData> modelList = new ArrayList<RevisitImportData>();
				// 解析Excel返回封装好的结果 TODO: 从本地文件中读取
				List<String> invalidCells = ExcelHelper.parseExcel2RevisitData(file, modelList);
				if(V.isNotEmpty(invalidCells)){
					canImport = false;
					modelMap.addAttribute("resultInfo", invalidCells.toString());
				}
				else{
					/*判定是否重复导入*/	
					if(modelList != null && !modelList.isEmpty()){
						List<String> realnameList = new ArrayList<String>();
						for (RevisitImportData row : modelList){
							row.setOrgId(revisitImport.getOrgId());
							row.setDataImportId(revisitImport.getId());
							// 添加所有销售
							if(!realnameList.contains(row.getCustomerManager())){
								realnameList.add(row.getCustomerManager());
							}
						}
						// 检查是否所有销售员都存在？
						Map<String, Long> userMap = orgUserService.getUserName2IdMap(revisitImport.getOrgId(), realnameList);
						List<String> invalidNames = new ArrayList<String>();
						for (RevisitImportData row : modelList){
							Long userId = userMap.get(row.getCustomerManager());
							if(userId == null){
								invalidNames.add(row.getCustomerManager());
							}
						}
						if(invalidNames.size() > 0){
							canImport = false;
							String msg = "客户经理 [ " + StringUtils.join(invalidNames, ",") + "] 不存在，请检查！";
							modelMap.addAttribute("resultInfo", msg);
							
							revisitImport.setFailReason(msg);
							revisitImport.setStatus(Cons.UPLOAD_STATUS_FAIL);
							revisitImport.setCount(0);

							addResultMsg(request, false, msg);
							modelMap.addAttribute("modelList", modelList);
						}
						else{
							// 判断是否重复
							List<String> rowIndex = revisitImportService.findDuplicateData(modelList);
							if(rowIndex != null && !rowIndex.isEmpty()){
								canImport = false;
								String msg = "序号为[" + StringUtils.join(rowIndex, ",") + "]的数据与系统中已上传数据重复，请去除重复数据后重试！";
								modelMap.addAttribute("resultInfo", msg);
								
								revisitImport.setFailReason(msg);
								revisitImport.setStatus(Cons.UPLOAD_STATUS_DUPLICATE);
								revisitImport.setCount(0);
								
								addResultMsg(request, false, msg);
								modelMap.addAttribute("modelList", modelList);
							}
							else{
								// 预览成功，为下一步保存做准备
								HttpSession session = request.getSession(false);
								if(session != null){
									session.setAttribute("revisitImport", revisitImport);
									session.setAttribute("revisitImportData", modelList);
								}
								addResultMsg(request, true, "尝试解析数据文件成功，共有 "+modelList.size()+" 条数据可导入！");
								modelMap.addAttribute("modelList", modelList);						
							}							
						}
					}
				}
			}
		}
		catch(Exception e){
			canImport = false;
			modelMap.addAttribute("resultInfo", "解析数据出错，原因为：" + e.getMessage());
			logger.error("处理Excel文件异常:", e);
		}
		
		// 返回处理结果
		modelMap.addAttribute("canImport", canImport);
        String errorinfo = (String)modelMap.get("resultInfo");
        if(StringUtils.isNotBlank(errorinfo)){
        	revisitImport.setFailReason(errorinfo);
        	revisitImport.setStatus(Cons.UPLOAD_STATUS_FAIL);
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
			RevisitImport revisitImport = (RevisitImport) session.getAttribute("revisitImport");
			List<RevisitImportData> models = (List<RevisitImportData>) session.getAttribute("revisitImportData");
			if(revisitImport == null || models == null){
				addResultMsg(request, false, "无法获取当前上传的数据文件！");
				return "redirect:/revisitImport/index";
			}
			
			OrgUser user = AppHelper.getCurrentUser();
			revisitImport.setCreateBy(user.getId());
			revisitImport.setOrgId(user.getOrgId());				
			if(revisitImport.getId() == null){
				revisitImportService.createModel(revisitImport);
				// 设置字表关联
				for(RevisitImportData m : models){
					m.setDataImportId(revisitImport.getId());
				}
			}
			// 保存
			boolean isSucess = revisitImportService.batchCreateData(models) > 0;
			if(!isSucess){
				revisitImport.setFailReason("保存导入数据失败！");
				revisitImport.setStatus(Cons.UPLOAD_STATUS_FAIL);
				revisitImport.setCount(0);
			}
			else{
				revisitImport.setStatus(Cons.UPLOAD_STATUS_SUCCESS);
				revisitImport.setCount(models.size());
			}
			revisitImport.setCreateBy(user.getId());
			revisitImportService.updateModel(revisitImport);
			
			String msg = isSucess ? "数据导入成功！ 共成功导入 "+models.size()+"条记录！" : "保存数据失败";
			if(!isSucess){
			}
			
			addResultMsg(request, isSucess, msg);
			
			session.removeAttribute("revisitImport");
			session.removeAttribute("revisitImportData");
		}
		else{
			addResultMsg(request, false, "未获取到预处理的数据！");			
		}
		
		return "redirect:/revisitImport/index";
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
		return revisitImportService;
	}

	@Override
	protected boolean canDelete(BaseModel model) {
		return false;
	}

}
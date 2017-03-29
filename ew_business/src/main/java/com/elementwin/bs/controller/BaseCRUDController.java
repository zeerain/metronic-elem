package com.elementwin.bs.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import dibo.commons.file.FileHelper;
import dibo.framework.model.BaseModel;

import com.elementwin.api.config.Cons;
import com.elementwin.bs.scheduler.AsyncWorker;
import com.elementwin.bs.model.File;
import com.elementwin.bs.model.Notification;
import com.elementwin.bs.model.OrgUser;

import dibo.framework.service.BaseService;
import dibo.framework.utils.V;

import com.elementwin.bs.utils.AppHelper;
import com.elementwin.bs.service.FileService;
import com.elementwin.bs.service.NotificationService;

/***
 * 增删改查通用管理功能-父类
 * @author Mazc@dibo.ltd
 *
 */
public abstract class BaseCRUDController extends dibo.framework.controller.BaseCRUDController {
	private static Logger logger = Logger.getLogger(BaseCRUDController.class);
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private NotificationService notificationService;

	@Autowired
	AsyncWorker asyncWorker;
	
	/***
	 * view路径+文件名
	 * @param fileName
	 * @return
	 */
	@Override
	public String view(HttpServletRequest request, ModelMap model, String fileName){
		// 更新缓存
		HttpSession session = request.getSession(false);
		if(session != null){
			OrgUser user = AppHelper.getCurrentUser();
			if(user != null){
				if(session.getAttribute("user") == null){
					session.setAttribute("user", user);					
				}
			}
		}
		// 绑定操作结果
		return super.view(request, model, fileName);
	}
	
	/***
	 * 添加OrgId参数到criteria
	 * @param modelMap
	 */
	public void buildCriteriaWithOrgId(ModelMap modelMap){
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("org_id", AppHelper.getCurrentUser().getOrgId());
		modelMap.addAttribute("criteria", criteria);
	}

	/****
	 * 保存上传文件
	 * @param request
	 * @param type
	 * @param objId
	 * @return
	 * @throws Exception
	 */
	protected String saveFiles(HttpServletRequest request, BaseModel model) throws Exception{
		String link = null;
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if(isMultipart){
        	List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles("attachedFiles");
        	if(V.isNotEmpty(files)){
        		for(MultipartFile file : files){
        			String fileName = file.getOriginalFilename();
        			if(V.isNotEmpty(fileName)){
        				String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        				String ext = fileName.substring(fileName.lastIndexOf(".")+1);
        				String newFileName = uuid+"."+ext;
        				if(!FileHelper.isValidFileExt(ext)){
        					logger.info("非法的附件类型: "+fileName);  
        					continue;
        				}
        				if(FileHelper.isImage(ext)){
        					link = FileHelper.saveImage(file, newFileName);        					
        				}
        				else{
        					String path = FileHelper.saveFile(file, newFileName);
        					
        					File fileObj = new File();
        					fileObj.setUuid(uuid);
        					fileObj.setName(fileName);
        					fileObj.setRelObjType(model.getClass().getSimpleName());
        					fileObj.setRelObjId(model.getId());
        					fileObj.setFileType(ext);
        					fileObj.setName(fileName);
        					fileObj.setPath(path);
        					
        					link = "/file/download/"+uuid;
        					fileObj.setLink(link);
        					fileObj.setSize(file.getSize());
        					
        					fileService.createModel(fileObj);
        					logger.info("成功保存附件: "+fileObj.toString());        					
        				}
        			}
        		}
        	}
        }
        
        return link;
	}
	
	// 获取service实例
	protected abstract BaseService getService();
	
	// 抽象方法
	// 删除权限
	@Override
	protected boolean canDelete(BaseModel model){
		return false;
	}
	// 创建
	@Override
	protected void beforeCreate(HttpServletRequest request, BaseModel model, ModelMap modelMap) throws Exception{	
	}
	@Override
	protected String afterCreated(HttpServletRequest request, BaseModel model, ModelMap modelMap) throws Exception{
		// 异步创建操作日志
    	OrgUser user = AppHelper.getCurrentUser();
    	asyncWorker.saveTraceLog(user, Cons.OPERATION.CREATE, model);
		return null;
	}
	// 更新
	@Override
	protected void beforeUpdate(HttpServletRequest request, BaseModel model, ModelMap modelMap) throws Exception{
	}
	@Override
	protected String afterUpdated(HttpServletRequest request, BaseModel model, ModelMap modelMap) throws Exception{		
		// 异步创建操作日志
		OrgUser user = AppHelper.getCurrentUser();
		asyncWorker.saveTraceLog(user, Cons.OPERATION.UPDATE, model);
		return null;
	}
	// 删除后
	@Override
	protected void afterDeleted(HttpServletRequest request, BaseModel model) {
		// 异步创建操作日志
		OrgUser user = AppHelper.getCurrentUser();
		asyncWorker.saveTraceLog(user, Cons.OPERATION.DELETE, model);		
	}
}
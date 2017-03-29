package com.elementwin.bs.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.elementwin.bs.model.AudioPack;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.common.model.Organization;
import com.elementwin.bs.scheduler.AsyncWorker;
import com.elementwin.bs.security.PermissionException;
import com.elementwin.bs.service.AudioPackService;
import com.elementwin.bs.service.OrgServiceService;
import com.elementwin.bs.service.OrganizationService;
import com.elementwin.bs.utils.AppHelper;

import dibo.commons.file.FileHelper;
import dibo.framework.utils.V;

/***
 * 文件相关操作Controller
 * @author Mazc@dibo.ltd
 */
@Controller
@RequestMapping("/audioDownload")
public class AudioDownloadController extends BaseController {
	private static Logger logger = Logger.getLogger(AudioDownloadController.class);
	
	@Autowired
	private OrganizationService organizationService;
	
	@Autowired
	private OrgServiceService orgServiceService;
	
	@Autowired
	private AudioPackService audioPackService;
	
	@Autowired
	private AsyncWorker asyncWorker;
	
	@Override
	protected String getViewPrefix() {
		return "audioDownload";
	}
	
	@RequestMapping(value = "/{type}", method = RequestMethod.GET)
	public String indexPage(@PathVariable("type")String type, HttpServletRequest request, ModelMap modelMap) throws Exception {
		OrgUser user = AppHelper.getCurrentUser();
		if(user != null && (user.isAdmin() || user.isGroupUser())){
			// 管理员和集团用户可下载
		}
		else{
			throw new PermissionException("您无权访问该页面");			
		}
		// 获取可访问的单位列表
		List<Organization> modelList = AppHelper.getAccessableOrgList(request, organizationService, user);
		modelMap.put("modelList", modelList);
		
		initPreviousMonth(modelMap);
		initPreviousDays(modelMap);
		String month = request.getParameter("month");
		Date now = new Date();
		if(V.isEmpty(month)){
			// 默认为上个月
			month = com.elementwin.api.utils.DateUtils.getYearMonth(DateUtils.addMonths(now, -1));
		}
		
		// 月度的打包
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("date_range", month);
		criteria.put("orgIdIn", modelList);
		
		List<AudioPack> packList = (List<AudioPack>) audioPackService.getModelList(criteria);
		Map<String, Long> monthPackMap = new HashMap<String, Long>();
		if(V.isNotEmpty(packList)){
			for(AudioPack pack : packList){
				monthPackMap.put(String.valueOf(pack.getOrgId()), pack.getId());				
			}
			packList = null;
		}
		modelMap.put("monthPackMap", monthPackMap);
		
		// 某天的打包
		String day = request.getParameter("day");
		if(V.isEmpty(day)){
			// 默认为昨天
			day = com.elementwin.api.utils.DateUtils.getDate(now, -1);
		}
		criteria.put("date_range", day);
		criteria.put("orgIdIn", modelList);
		packList = (List<AudioPack>) audioPackService.getModelList(criteria);
		Map<String, Long> dayPackMap = new HashMap<String, Long>();
		if(V.isNotEmpty(packList)){
			for(AudioPack pack : packList){
				dayPackMap.put(String.valueOf(pack.getOrgId()), pack.getId());				
			}
			packList = null;
		}
		modelMap.put("dayPackMap", dayPackMap);
		
		modelMap.put("type", type);
		modelMap.put("month", month);
		modelMap.put("day", day);
		
		return super.view(request, modelMap, "index");
	}
	
	/***
	 * 初始化之前的月份
	 * @param modelMap
	 */
	private void initPreviousMonth(ModelMap modelMap){
		List<String> result = new ArrayList<String>();
		for(int i=-1; i>=-6; i--){
			String range = com.elementwin.api.utils.DateUtils.getYearMonth(DateUtils.addMonths(new Date(), i));
			result.add(range);
		}
		
		modelMap.addAttribute("monthOptions", result);
	}

	/***
	 * 初始化之前的月份
	 * @param modelMap
	 */
	private void initPreviousDays(ModelMap modelMap){
		List<String> result = new ArrayList<String>();
		String month = com.elementwin.api.utils.DateUtils.getYearMonth(new Date());
		int day = com.elementwin.api.utils.DateUtils.getDay();
		for(int i=day; i>0; i--){
			String d = ((i < 10)? "0":"") + i;
			result.add(month + "-" + d);
		}
		
		modelMap.addAttribute("dayOptions", result);
	}
	
	/***
	 * 下载文件
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/download/{orgId}/{packid}", method = RequestMethod.GET)
    public String viewPage(@PathVariable("orgId")Long orgId, @PathVariable("packid")Long packid, HttpServletRequest request, HttpServletResponse response) throws Exception {  
		OrgUser user = AppHelper.getCurrentUser();
		if(user != null && (user.isAdmin() || user.isGroupUser())){
			// 管理员和集团用户可下载
		}
		else{
			throw new PermissionException("您无权访问该页面");			
		}
		// 获取可访问的单位列表
		List<Organization> orgList = AppHelper.getAccessableOrgList(request, organizationService, user);
		boolean hasPermission = false;
		if(V.isNotEmpty(orgList)){
			for(Organization org : orgList){
				if(orgId.equals(org.getId())){
					hasPermission = true;
				}
			}
		}
		if(!hasPermission){
			throw new PermissionException("您无权访问该页面");				
		}
		
		response.setContentType("text/html;charset=utf-8");  
        request.setCharacterEncoding("UTF-8");  
        // 获取打包对象
        AudioPack pack = (AudioPack) audioPackService.getModel(packid);
        if(pack != null){
        	Organization org = (Organization) organizationService.getModel(orgId);
        	String downloadPath = FileHelper.getFullFilePath(pack.getFilePath());
        	java.io.BufferedInputStream bis = null;
        	java.io.BufferedOutputStream bos = null;
        	String name = org.getShortName() + "_" + pack.getDateRange() + "." + FileHelper.getFileExtByName(downloadPath);
        	try{
        		String fileName = new String(name.getBytes("utf-8"), "ISO8859-1");
        		long fileLength = new java.io.File(downloadPath).length();
        		response.setContentType(pack.getContextType());
        		response.setHeader("Content-disposition", "attachment; filename="+ fileName);  
        		response.setHeader("Content-Length", String.valueOf(fileLength));
        		bis = new BufferedInputStream(new FileInputStream(downloadPath));  
        		bos = new BufferedOutputStream(response.getOutputStream());
        		byte[] buff = new byte[2048];
        		int bytesRead;
        		while(-1 != (bytesRead = bis.read(buff, 0, buff.length))) {  
        			bos.write(buff, 0, bytesRead);  
        		}
        	}
        	catch (Exception e) {  
        		logger.error("下载录音zip文件失败:"+packid, e);
        	}
        	finally {
        		if (bis != null)  
        			bis.close();  
        		if (bos != null)  
        			bos.close();  
        	}  
        }
        return null;
    }

}
package com.elementwin.bs.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.elementwin.bs.model.File;
import com.elementwin.bs.scheduler.AsyncWorker;
import com.elementwin.bs.service.FileService;

import dibo.commons.file.FileHelper;

/***
 * 文件相关操作Controller
 * @author Mazc@dibo.ltd
 */
@Controller
@RequestMapping("/file")
public class FileController extends BaseController {
	private static Logger logger = Logger.getLogger(FileController.class);
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private AsyncWorker asyncWorker;
	
	/***
	 * 下载文件
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/download/{uuid}", method = RequestMethod.GET)
    public String viewPage(@PathVariable("uuid")String uuid, HttpServletRequest request, HttpServletResponse response) throws Exception {  
		response.setContentType("text/html;charset=utf-8");  
        request.setCharacterEncoding("UTF-8");  
        if(uuid.contains(".")){
        	uuid = uuid.substring(0, uuid.indexOf("."));
        }
        File fileModel = fileService.getFileByUuid(uuid);
        if(fileModel != null){
        	String downloadPath = FileHelper.getFullFilePath(fileModel.getPath());
        	java.io.BufferedInputStream bis = null;
        	java.io.BufferedOutputStream bos = null;
        	try{
        		String fileName = new String(fileModel.getName().getBytes("utf-8"), "ISO8859-1");
        		long fileLength = new java.io.File(downloadPath).length();  
        		if(fileModel.getContextType() != null){
        			response.setContentType(fileModel.getContextType());         			
        		}
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
        		logger.error("下载文件失败:"+uuid, e);
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

	@Override
	protected String getViewPrefix() {
		return null;
	}
	
	/***
	 * 下载文件
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/download/test", method = RequestMethod.GET)
    public String download(HttpServletRequest request, HttpServletResponse response) throws Exception {  
		java.io.BufferedInputStream bis = null;
		java.io.BufferedOutputStream bos = null;
		String downloadPath = "/www/server/tomcat/webapps/upload/file/1612/test_2016-12-21.xls"; //"D:\\FTPResources\\Shangh_YuanBing\\logs\\回访记录_2016-12-21.xls";
    	try{
    		response.setContentType("text/html;charset=utf-8");  
    		request.setCharacterEncoding("UTF-8");  
    		String fileName = "test_2016-12-21.xls";
    		long fileLength = new java.io.File(downloadPath).length();  
    		response.setContentType("application/x-msdownload;");         			
    		response.setHeader("Content-disposition", "attachment; filename="+ fileName);  
    		response.setHeader("Content-Length", String.valueOf(fileLength));
    		bis = new BufferedInputStream(new FileInputStream(downloadPath));  
    		bos = new BufferedOutputStream(response.getOutputStream());
    		int size = 2048;
    		if(request.getParameter("size") != null){
    			size = Integer.parseInt(request.getParameter("size"));
    		}
    		byte[] buff = new byte[size];
    		int bytesRead;
    		while(-1 != (bytesRead = bis.read(buff, 0, buff.length))) {  
    			bos.write(buff, 0, bytesRead);  
    		}
    	}
    	catch (Exception e) {  
    		logger.error("下载文件失败:"+downloadPath, e);
    	}
    	finally {
    		if (bis != null)  
    			bis.close();  
    		if (bos != null)  
    			bos.close();  
    	}
        return null;
    }

}
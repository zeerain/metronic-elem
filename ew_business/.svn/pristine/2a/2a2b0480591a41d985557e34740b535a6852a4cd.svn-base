package com.elementwin.bs.controller.wechat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.elementwin.api.config.SystemConfig;
import com.elementwin.bs.model.AudioMsg;
import com.elementwin.bs.model.CcUser;
import com.elementwin.bs.scheduler.AsyncWorker;
import com.elementwin.bs.service.AudioMsgService;
import com.fasterxml.jackson.databind.ObjectMapper;

import dibo.commons.file.FileHelper;




/***
 * Dibo 微信售后管理controller 
 * @author Yangz@dibo.ltd
 * @version 2016年12月28日
 * Copyright 2016 www.dibo.ltd
 */
@Controller
@RequestMapping("wechat/group/voice")
public class WechatVoiceController extends BaseWechatController {
	 private static Logger logger = Logger.getLogger(WechatVoiceController.class);

	 private static final String CORPID=SystemConfig.getProperty("wechat.corpID");  
	 private static final String CORPSECRET=SystemConfig.getProperty("wechat.secret");  
	
	 @Autowired
	 private AsyncWorker asyncWorker;
	 
	 @Autowired
	 private AudioMsgService audioMsgService;
	 
	@Override
	protected String getViewPrefix() {
		return "wechat/group/voice";
	}
	
	/**
	 * 售后录音表单页面
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String voice(HttpServletRequest request, ModelMap modelMap) throws Exception{
		
		modelMap.addAttribute("orgId", getCurrentOrgId(request, modelMap));
		modelMap.addAttribute("userId", getCurrentUser(request).getId());
		
		return view(request, modelMap, "index");
	}
	
	@RequestMapping(value="/view", method=RequestMethod.GET)
	public String view(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception{
		HttpSession session = request.getSession(false);
		if(session != null && StringUtils.isNotBlank((String) session.getAttribute("serverId"))){
			String token = getAccessToken(request, response);
			String filePath = downloadVoice(token, (String) session.getAttribute("serverId"));
			if(filePath.endsWith("mp3")){
				modelMap.addAttribute("filePath", filePath);
				modelMap.addAttribute("text", session.getAttribute("text"));
				return view(request, modelMap, "view");
			}
		}
		return "redirect: /wechat/group/voice/index";
	}
	
	
	/**
	 * 保存语音消息记录
	 * @param model
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="index", method=RequestMethod.POST)
	public String index(@ModelAttribute AudioMsg model, HttpServletRequest request, ModelMap modelMap) throws Exception{
		String audioUrls = model.getAudioUrls();
		
		model.setSystem("CC");
		model.setReceiverType(CcUser.class.getSimpleName());
		
		
		String receiver = SystemConfig.getProperty("wechat.audio.receiver");
		if(StringUtils.isNotBlank(receiver)){
			try{
				Long receiverId = Long.valueOf(receiver);
				model.setReceiverId(receiverId);
			}catch(Exception e){
				logger.error("售后微信录音获取接收坐席id出错");
			}
		}
		model.setRelObjType("RevisitTask");
		
		boolean success = audioMsgService.createModel(model);
		if(!success){
			logger.warn("保存audioMsg失败");
		}
		if(StringUtils.isNotBlank(audioUrls) && success){
			asyncWorker.downloadAndSaveWechatAudioUrls(audioUrls, model);
			modelMap.addAttribute("success", true);
		}
		return voice(request, modelMap);
	}
	
	
	public String downloadVoice(String token, String serverId) throws Exception{
		String remoteUrl = "https://qyapi.weixin.qq.com/cgi-bin/media/get?access_token=" + token + "&media_id=" + serverId;
		logger.info("下载音频链接为：" + remoteUrl);
		String fileName = UUID.randomUUID() + ".mp3";
		String accessPath = FileHelper.getImageStoragePath(fileName);
		String fileStorageDirectory = FileHelper.getFileStorageDirectory();
		boolean success = FileHelper.downloadRemoteFile(remoteUrl, fileStorageDirectory + accessPath);
		if(!success){
			logger.error("下载微信录音：" + serverId + "失败");
			return serverId;
		}else{
			logger.info("下载微信录音成功:" + accessPath);
		}
		return accessPath;
	}
	
	@ResponseBody
	@RequestMapping(value="/saveVoice", method=RequestMethod.POST)
	public Map<String, Object> saveVoice(HttpServletRequest request, ModelMap modelMap) throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
		String serverId = request.getParameter("serverId");
		if(StringUtils.isNotBlank(serverId)){
			HttpSession session = request.getSession(true);
			session.setAttribute("serverId", serverId);
			session.setAttribute("text", request.getParameter("text"));
			result.put("status", 1);
			result.put("detail", "保存录音成功");
			return result;
		}
		result.put("status", -1);
		result.put("detail", "保存录音失败");
		return result;
	}
	
    public String getAccessToken(HttpServletRequest request, HttpServletResponse response) throws Exception {  
        String urlStr = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+CORPID+"&corpsecret="+CORPSECRET;    
        String resultStr = processUrl(response, urlStr);    
        ObjectMapper mapper = new ObjectMapper();
        try{
        	Map map = mapper.readValue(resultStr, Map.class);
        	if(map != null && map.get("access_token") != null){
        		return (String) map.get("access_token");
        	}else{
        		logger.warn("获取access_token失败");
        	}
        }catch(Exception e){
        	logger.error("解析返回数据出错", e);
        }
        return "";
    }  
	
    public String getTsapiTicket(HttpServletRequest request, HttpServletResponse response, String access_token) throws Exception { 
        String urlStr = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token="+access_token;    
        String resultStr = processUrl(response, urlStr);    
        //处理返回结果字符串
        ObjectMapper mapper = new ObjectMapper();
        try{
        	Map map = mapper.readValue(resultStr, Map.class);
        	if(map != null && map.get("ticket") != null && map.get("errcode") != null){
        		Integer errcode = (Integer) map.get("errcode");
        		if(errcode != 0){
        			logger.warn("获取ticket失败");
        			return "";
        		}
        		return (String) map.get("ticket");
        	}else{
        		logger.warn("获取ticket失败");
        	}
        }catch(Exception e){
        	logger.error("解析返回数据出错", e);
        }
        return "";
    }  
    
    public Map<String, String> getJsSdkParam(HttpServletRequest request, HttpServletResponse response, String url) throws Exception{
    	Map<String, String> result = new HashMap<String, String>();
    	
    	String appId = CORPID;
    	
    	String accessToken = getAccessToken(request, response);
    	String apiTicket = getTsapiTicket(request, response, accessToken);
    	String nonceStr = UUID.randomUUID().toString();
    	String tsm = Long.toString(System.currentTimeMillis());
    	
    	String signature = getJsSdkSign(nonceStr, apiTicket, tsm, url);
    	
    	result.put("appId", appId);
    	result.put("timestamp", tsm);
    	result.put("nonceStr", nonceStr);
    	result.put("signature", signature);
    	
    	return result;
    }
   
	private String processUrl(HttpServletResponse response, String urlStr) {  
        URL url;  
        try {  
            url = new URL(urlStr);  
            URLConnection URLconnection = url.openConnection();    
            HttpURLConnection httpConnection = (HttpURLConnection)URLconnection;    
            int responseCode = httpConnection.getResponseCode();    
            if (responseCode == HttpURLConnection.HTTP_OK) {    
                InputStream urlStream = httpConnection.getInputStream();    
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlStream));    
                String sCurrentLine = "";    
                String sTotalString = "";    
                while ((sCurrentLine = bufferedReader.readLine()) != null) {    
                    sTotalString += sCurrentLine;    
                }    
                return sTotalString;
            }else{  
            	logger.error("调起js-sdk过程中processUrl失败");
            	
            }  
        } catch (Exception e) {  
        	logger.error("调起js-sdk过程中processUrl失败", e);
        }  
        return "";
    }  
	
	
	/**
	 * 获得加密后得签名
	 * @param noncestr
	 * @param tsapiTicket
	 * @param timestamp
	 * @param url
	 * @return
	 */
    public static String getJsSdkSign(String noncestr,String tsapiTicket,String timestamp,String url){  
        String content="jsapi_ticket="+tsapiTicket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+url;  
        String ciphertext=getSha1(content);  
        
        return ciphertext;  
    }  
    
    /**
     * 进行sha1加密
     * @param str
     * @return
     */
    public static String getSha1(String str){
        if(str==null||str.length()==0){
            return null;
        }
        char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9',
                'a','b','c','d','e','f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j*2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];      
            }
            return new String(buf);
        }
        catch (Exception e) {
            return null;
        }
    }
	
	    
	
	
	
}

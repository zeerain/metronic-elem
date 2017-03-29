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

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.elementwin.api.config.SystemConfig;
import com.elementwin.api.utils.WeChatHelper;
import com.elementwin.api.utils.WechatAudioHelper;
import com.elementwin.bs.controller.BaseController;
import com.elementwin.bs.model.AudioMsg;
import com.fasterxml.jackson.databind.ObjectMapper;

import dibo.framework.utils.V;

/***
 * Dibo 微信jssdk后台接口controller 
 * @author Yangz@dibo.ltd
 * @version 2016年12月28日
 * Copyright 2016 www.dibo.ltd
 */
@Controller
@RequestMapping("wechat/jsSdk")
public class WechatJsSdkController extends BaseController {
	 private static Logger logger = Logger.getLogger(WechatJsSdkController.class);

	 private static final String CORPID=SystemConfig.getProperty("wechat.corpID");  
	 private static final String CORPSECRET=SystemConfig.getProperty("wechat.secret");  
	
	@Override
	protected String getViewPrefix() {
		return "wechat/jsSdk";
	}
	
	@ResponseBody
	@RequestMapping(value="/getJsSdk", method=RequestMethod.POST)
    public Map<String, String> getJsSdk(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception{
    	Map<String, String> result = new HashMap<String, String>();
    	
    	String appId = CORPID;
    	
    	String url = request.getParameter("url");
    	String apiTicket = WeChatHelper.getJsapiTicket(request, response);
    	String nonceStr = UUID.randomUUID().toString();
    	String tsm = Long.toString(System.currentTimeMillis());
    	
    	String signature = getJsSdkSign(nonceStr, apiTicket, tsm, url);
    	
    	result.put("appId", appId);
    	result.put("timestamp", tsm);
    	result.put("nonceStr", nonceStr);
    	result.put("signature", signature);
    	
    	return result;
    }
	
	@ResponseBody
	@RequestMapping(value="/saveWechatVoice", method=RequestMethod.POST)
	public Map<String, String> saveWechatVoice(@ModelAttribute AudioMsg audioMsg, HttpServletRequest request, ModelMap modelMap) throws Exception{
		Map<String, String> result = new HashMap<String, String>();
		
		String token = WeChatHelper.getAccessToken("APPTOKEN");
		String serverId = audioMsg.getAudioUrls();
		
		result.put("audioUrls", "");
		if (V.isNotEmpty(serverId)){
			if (V.isNotEmpty(token)){
				String audioUrls = WechatAudioHelper.downloadVoice(audioMsg, token, serverId);
				if(V.isNotEmpty(audioUrls) && (audioUrls.endsWith("mp3") || audioUrls.endsWith("amr"))){
					result.put("status", "1");
					result.put("audioUrls", audioUrls);
					result.put("detail", "获取路径成功");
				}
				else{
					result.put("status", "-3");
					result.put("detail", "下载录音文件错误");
				}
			} else{
				result.put("status", "-2");
				result.put("detail", "获取token错误");
			}
		} else{
			result.put("status", "-1");
			result.put("detail", "提交参数错误");
		}
		return result;
	}
   
	/**
	 * 获取accessToken
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
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
	
    /**
     * 获取apiTicket
     * @param request
     * @param response
     * @param access_token
     * @return
     * @throws Exception
     */
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

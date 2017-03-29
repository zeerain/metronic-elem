package com.elementwin.bs.scheduler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.elementwin.api.utils.SpellHelper;
import com.elementwin.api.utils.WeChatHelper;
import com.elementwin.api.utils.WechatAudioHelper;
import com.elementwin.bs.cache.MetadataCache;
import com.elementwin.bs.config.Cons;
import com.elementwin.bs.model.AudioMsg;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.PotentialCustomer;
import com.elementwin.bs.model.PresaleTask;
import com.elementwin.bs.model.RevisitStatistical;
import com.elementwin.bs.model.RevisitWorkOrder;
import com.elementwin.bs.model.TraceLog;
import com.elementwin.bs.service.AudioMsgService;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.PresaleTaskService;
import com.elementwin.bs.service.RevisitStatisticalService;
import com.elementwin.bs.service.TraceLogService;

import dibo.framework.model.BaseModel;

/***
 * 异步操作类
 * @author Mazc@dibo.ltd
 */
@Async
@Component("asyncWorker")
public class AsyncWorker {
	private static Logger logger = Logger.getLogger(AsyncWorker.class);
	
	@Autowired
	private RevisitStatisticalService revisitStatisticalService;
	
	@Autowired
	private PotentialCustomerService potentialCustomerService;
	
	@Autowired
	private PresaleTaskService presaleTaskService;
	
	@Autowired
	private TraceLogService traceLogService;
	
	@Autowired
	private AudioMsgService audioMsgService;
	
	// 微信相关参数
	private static String WECHAT_CHAT_SENDER = null;

	/***
	 * 创建统计数据
	 * @param task
	 * @throws Exception
	 */
	public void updateStatisticalData(Long taskId, RevisitWorkOrder workOrder) throws Exception{
		// create or update model
		RevisitStatistical model = (RevisitStatistical) revisitStatisticalService.getModel(taskId);		
		if(model != null){
			model.setWorkOrderId(workOrder.getId());
			if(workOrder.getConfirmedCategory() != null){
				model.setWorkOrderCategory(workOrder.getConfirmedCategory());			
			}
			if(workOrder.getWorkOrderDuration() != null && workOrder.getWorkOrderDuration().longValue() > 0){
				model.setWorkOrderDuration(workOrder.getWorkOrderDuration());
			}
			revisitStatisticalService.updateModel(model);			
		}
	}
	
	/***
	 * 重新回访的统计，将原回访置为失效
	 * @param taskId
	 * @throws Exception
	 */
	public void updateStatistical2Inactive(Long taskId) throws Exception{
		RevisitStatistical model = (RevisitStatistical) revisitStatisticalService.getModel(taskId);
		if(model != null){
			model.setActive(false);
			revisitStatisticalService.updateModel(model);			
		}
	}
	
	/**
	 * 下载微信录音并更新数据库
	 * @param audioUrls
	 * @param model
	 * @throws Exception
	 */
	public void downloadAndSaveWechatAudioUrls(String audioUrls, AudioMsg model) throws Exception{
		String token = WeChatHelper.getAccessToken("APPTOKEN");
		if(!audioUrls.endsWith("mp3")){
			String audioUrl = WechatAudioHelper.downloadVoice(model, token, audioUrls);
			if(!audioUrl.endsWith("mp3") && !audioUrl.endsWith("amr")){
				logger.warn("微信语音#"+model.getId()+"下载失败 !");
				return;
			}
			model.setAudioUrls(audioUrl);
			audioMsgService.updateModel(model);
		}
	}
	
	/****
	 * HTTP下载文件
	 * @param fileUrl
	 * @param targetFilePath
	 * @return
	 */
	public boolean downloadRemoteFile(String fileUrl, String targetFilePath) {  
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(fileUrl);  
		try {
            HttpResponse httpResponse = httpClient.execute(httpGet);  
            HttpEntity entity = httpResponse.getEntity();  
            long length = entity.getContentLength();  
            if(length <= 0){
                logger.warn("下载文件不存在！");  
                return false;
            }
            logger.info("微信语音下载的格式为：" + httpResponse);
            FileUtils.copyInputStreamToFile(entity.getContent(), new File(targetFilePath));
            return true;
        }
        catch (MalformedURLException e) {
        	logger.error("下载文件URL异常(url=" + fileUrl + "): ", e);
            return false;
        }
        catch (IOException e) {
        	logger.error("下载文件IO异常(url=" + fileUrl + "): ", e);
            return false;
        }
		finally{
			try {
				httpClient.close();
			}
			catch (IOException e) {
				logger.error("关闭httpClient下载连接异常:", e);
			}
		}
    }
	
	/***
	 * 保存操作日志
	 * @param operation
	 * @param model
	 */
	public void saveTraceLog(OrgUser user, Cons.OPERATION operation, BaseModel model, String...comment){
		if(model instanceof TraceLog){
			return;
		}
		TraceLog traceLog = new TraceLog();
		if(user != null){
			traceLog.setUserId(user.getId());
			traceLog.setUserType(OrgUser.class.getSimpleName());
		}
		traceLog.setOperation(operation.name());
		traceLog.setRelObjId(model.getId());
		traceLog.setRelObjType(model.getClass().getSimpleName());
		String allData = model.toString();
		traceLog.setRelObjData(allData.substring(allData.indexOf("[")+1, allData.lastIndexOf("]")));
		if(comment != null && comment.length > 0){
			traceLog.setComment(comment[0]);
		}
		try {
			traceLogService.createModel(traceLog);
		}
		catch (Exception e) {
			logger.error("保存操作日志错误", e);
		}
	}
	
	/**
	 * 调用微信接口发送会话消息
	 * @param type
	 * @param id
	 * @param sender
	 * @param msgtype
	 * @param content
	 * @throws Exception
	 */
	public void sendChatMessageWechat(String type, String id, String msgtype, String content) throws Exception {
		if(StringUtils.isBlank(id)){
			return ;
		}
		
		initWechatKeys();
		
		boolean success = WeChatHelper.sendChatMessage(type, id, WECHAT_CHAT_SENDER, msgtype, content);
		if(success){
			logger.info("微信推送会话消息成功");
		}else{
			logger.error("微信推送会话消息失败");
		}
		
	}
	
	public void sendLinkChatMessageWechat(String type, String id, String msgtype, String content, String mediaId, String title, String url) throws Exception {
		if(StringUtils.isBlank(id)){
			return ;
		}
		
		initWechatKeys();
		boolean success = WeChatHelper.sendLinkChatMessage(type, id, WECHAT_CHAT_SENDER, msgtype, content, mediaId, title, url);
		if(success){
			logger.info("微信推送会话消息成功");
		}else{
			logger.error("微信推送会话消息失败");
		}
		
	}
	
	/***
	 * 批量创建潜客信息
	 * @throws Exception
	 */
	public void batchCreatePotentialCustomers(Long orgId) throws Exception{
		List<PresaleTask> newTaskList = presaleTaskService.getUnsavedCustomerList(orgId);
		if(newTaskList != null && !newTaskList.isEmpty()){
			for(PresaleTask task : newTaskList){
				PotentialCustomer c = new PotentialCustomer();
				c.setFromOrgId(orgId);
				c.setFromSaId(task.getSalespersonId());
				c.setName(task.getCustomerName());
				c.setPhone(task.getCustomerPhone());
				c.setIntentCarModel(task.getIntentCarModel());
				c.setRegisterDate(task.getRegisterDate());

				String firstSpell = SpellHelper.getFirstSpell(c.getName(), SpellHelper.TYPE.SINGLE, 1);
				c.setFirstSpell(firstSpell.toUpperCase());
				// 保存
				potentialCustomerService.createModel(c);
				// 更新task
				if(c.getId() != null){
					task.setCustomerId(c.getId());
					presaleTaskService.updateTaskCustomer(task);
					if(logger.isDebugEnabled()){
						logger.info("task:"+task.getId()+" 的潜客信息创建完成！");
					}
				}
			}
		}
	}
	
	/***
	 * 初始化Wechat参数
	 */
	private void initWechatKeys(){
		if(WECHAT_CHAT_SENDER == null){
			WECHAT_CHAT_SENDER = MetadataCache.getSystemProperty("wechat.chat.sender");
		}
	}
}

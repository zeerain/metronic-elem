package com.elementwin.bs.controller.wx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.elementwin.api.config.Cons;
import com.elementwin.bs.model.AudioMsg;
import com.elementwin.bs.model.CcUser;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.PotentialCustomer;
import com.elementwin.bs.model.PresaleRecord;
import com.elementwin.bs.model.PresaleTask;
import com.elementwin.bs.scheduler.AsyncWorker;
import com.elementwin.bs.service.AudioMsgService;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.PresaleTaskService;

@Controller
@RequestMapping("/weixin/task")
public class TaskController extends BaseWxController{
	private static Logger logger = Logger.getLogger(TaskController.class);
	
	@Autowired
	PresaleTaskService presaleTaskService;

	@Autowired
	PotentialCustomerService potentialCustomerService;
	
	@Autowired
	AudioMsgService audioMsgService;
	
	@Autowired
	AsyncWorker asyncWorker;
	
	@Override
	protected String getViewPrefix() {
		return "weixin/task";
	}
	
	/***
	 * 显示列表首页面
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	protected String list(HttpServletRequest request, ModelMap modelMap) throws Exception {		
		// 加载第一页
		listPaging(1, "", request, modelMap);
		return super.view(request, modelMap, "list");
	}
	
	/**
	 * 显示首列表-分页
	 * @param pageIndex 分页
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value = "/list/{pageIndex}/{lastFocusDate}", method = RequestMethod.GET)
	protected String listPaging(@PathVariable("pageIndex")int pageIndex, @PathVariable("lastFocusDate")String lastFocusDate, HttpServletRequest request, ModelMap modelMap) throws Exception{
		checkUser(request);
		// 获取用户信息
		OrgUser user = getCurrentUser(request);
		
		// 封装并自动生成
		Map<String, Object> criteria = super.buildQueryCriteria(request, pageIndex);
		criteria.put("org_id", user.getOrgId()); 
		criteria.put("salesperson_id", user.getId());
		criteria.put("statusIn", new String[]{Cons.PRESALE_STATUS.NOTIFY_SA.name(), Cons.PRESALE_STATUS.CONTINUE.name()});
		
		modelMap.addAttribute("criteria", criteria);
		
		// 获取记录总数，用于前端显示分页
		int totalCount = presaleTaskService.getModelListCount(criteria);
		// 添加分页信息
		super.addPagingAttribute(modelMap, criteria, totalCount, pageIndex);
		
		// 加载当前页
		List<PresaleTask> modelList = (List<PresaleTask>) presaleTaskService.getFatTaskList(criteria, pageIndex);
		modelMap.addAttribute("modelList", modelList);
		
		modelMap.addAttribute("lastFocusDate", lastFocusDate);
		
		return super.view(request, modelMap, "list_paging");
	}
	
	/***
	 * 发送录音
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/send/{taskId}", method = RequestMethod.POST)
	@ResponseBody
	protected Map<String, Object> sendAudio(@PathVariable("taskId")Long taskId, @ModelAttribute AudioMsg model, HttpServletRequest request, ModelMap modelMap) throws Exception {
		// 获取用户信息
		OrgUser user = getCurrentUser(request);
		Map<String, Object> result = new HashMap<String, Object>();
		
		// 创建record
		// 查找是否有响应期限的record
		PresaleRecord exipreRecord = presaleTaskService.getRelatedExpireRecord(taskId);
		PresaleRecord record = createTaskRecord(taskId, model, user, exipreRecord);
		if(record != null){
			PresaleTask task = (PresaleTask)presaleTaskService.getModel(taskId);
			// 创建audio
			model.setSystem(Cons.SYSTEM.BS.name());
			model.setReceiverType(CcUser.class.getSimpleName());
			model.setReceiverId(task.getCurrentOwnerId());
			model.setRelObjType(PresaleTask.class.getSimpleName());
			model.setRelObjId(task.getId());
			model.setRelRecordId(record.getId());
			model.setFromOrgId(task.getOrgId());
			model.setCreateBy(user.getId());
	        
			// 音频消息
			audioMsgService.createModel(model);
			
			// 异步下载录音文件
			asyncWorker.downloadAndSaveWechatAudioUrls(model.getAudioUrls(), model);
			
			// 更新model
			task.setStatus(record.getStatus());
			presaleTaskService.updateModel(task);
			
			result.put("success", true);			
		}
		else{
			result.put("error", "任务记录创建失败！");			
		}
		
		return result;
	}
	
	/***
	 * 显示查看详细页面
	 * @param id
	 * @param request
	 * @param modelMap
	 * @return view
	 * @throws Exception
	 */
	@RequestMapping(value="/view/{taskId}")
	protected String viewPage(@PathVariable("taskId")Long taskId, HttpServletRequest request, ModelMap modelMap) throws Exception {  
		checkUser(request);
		
		// 获取model，追加关联record
		PresaleTask task = (PresaleTask) presaleTaskService.getModel(taskId);
		
		presaleTaskService.appendPresaleRecordList(task);			
		
		PotentialCustomer customer = (PotentialCustomer) potentialCustomerService.getModel(task.getCustomerId());

		task.setCustomer(customer);
		modelMap.addAttribute("model", task);
		modelMap.addAttribute("canEdit", false);
		
		return view(request, modelMap, "view");
    }
	
	/***
	 * 创建task记录
	 * @param taskId
	 * @param model
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private PresaleRecord createTaskRecord(Long taskId, AudioMsg model, OrgUser user, PresaleRecord exipreRecord) throws Exception {
		//TODO 根据serverId下载录音
		PresaleRecord record = new PresaleRecord();
		record.setTaskId(taskId);
		record.setType(Cons.PRESALE_RECORD_TYPE.SA_AUDIO.name());
		record.setComment(model.getContent());
		//record.setNotify(true);
		record.setNotifyType(Cons.NOTIFY_TYPE.CC.name());
		record.setCreateByName(user.getRealname());
		record.setCreateBy(user.getId());
		record.setCreateByType(OrgUser.class.getSimpleName());
		record.setStatus(Cons.PRESALE_STATUS.SA_FEEDBACK.name());
        
		if(exipreRecord != null){
			long currentTime = System.currentTimeMillis();
			long expireTime = exipreRecord.getExpireTime().getTime();
			if(currentTime <= expireTime){
				record.setExpiredFlag(PresaleRecord.EXPIRED_FLAG_UNEXPIRED);
			}
			else{
				record.setExpiredFlag(PresaleRecord.EXPIRED_FLAG_EXPIRED);
				//TODO 设置严重超期
				//record.setExpiredFlag(PresaleRecord.EXPIRED_FLAG_EXPIRED_II);
			}
		}
		boolean success = presaleTaskService.createRecord(record);
		return success? record : null;
	}	
	
}

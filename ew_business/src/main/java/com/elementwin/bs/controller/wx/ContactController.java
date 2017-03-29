package com.elementwin.bs.controller.wx;

import com.elementwin.api.config.Cons;
import com.elementwin.bs.model.AudioMsg;
import com.elementwin.bs.model.CcUser;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.PotentialCustomer;
import com.elementwin.bs.model.PresaleRecord;
import com.elementwin.bs.model.PresaleTask;
import com.elementwin.bs.scheduler.AsyncWorker;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.AudioMsgService;
import com.elementwin.bs.service.PresaleTaskService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/****
 * 通讯录模块的controller
 * @author Yangs@dibo.ltd
 * @version 2017年2月19日
 * Copyright 2016 www.dibo.ltd
 */
@Controller
@RequestMapping("/weixin/contact")
public class ContactController extends BaseWxController {
	private static Logger logger = Logger.getLogger(ContactController.class);
	
    @Autowired
    private PresaleTaskService presaleTaskService;
    
    @Autowired
    private PotentialCustomerService potentialCustomerService;
    
    @Autowired
    private AudioMsgService audioMsgService;
    
    @Autowired
    private AsyncWorker asyncWorker;

    @Override
    protected String getViewPrefix() {
        return "weixin/contact";
    }

    @RequestMapping(value = "/todotask/list", method = RequestMethod.GET)
    public String todotaskList(HttpServletRequest request, ModelMap modelMap) throws Exception {
        // 加载第一页
        listPaging(1, "", request, modelMap);
        return super.view(request, modelMap, "list");
    }

    @RequestMapping(value = "/todotask/list/{pageIndex}/{lastFocusDate}", method = RequestMethod.GET)
    public String listPaging(@PathVariable("pageIndex")int pageIndex, @PathVariable("lastFocusDate")String lastFocusDate, HttpServletRequest request, ModelMap modelMap) throws Exception{
        checkUser(request);
        // 获取用户信息
        OrgUser user = getCurrentUser(request);

        // 封装并自动生成
        Map<String, Object> criteria = super.buildQueryCriteria(request, pageIndex);
        criteria.put("org_id", user.getOrgId());
        criteria.put("salesperson_id", user.getId());
        criteria.put("handleStatusIn", new String[]{Cons.PRESALE_HANDLE_STATUS.PENDING.name(), Cons.PRESALE_HANDLE_STATUS.UPDATED.name()});
        criteria.put("nextHandleTimeEnd", com.elementwin.api.utils.DateUtils.getDateEnd(new Date()));

        modelMap.addAttribute("criteria", criteria);

        // 获取记录总数，用于前端显示分页
        int totalCount = presaleTaskService.getModelListCount(criteria);
        // 添加分页信息
        super.addPagingAttribute(modelMap, criteria, totalCount, pageIndex);
        
        // 加载当前页
        List<PresaleTask> modelList = (List<PresaleTask>) presaleTaskService.getModelList(criteria, pageIndex, Cons.WECHAT_PAGE_SIZE);
        presaleTaskService.appendCustomer2Task(modelList);
        modelMap.addAttribute("modelList", modelList);
        
        modelMap.addAttribute("lastFocusDate", lastFocusDate);

        return super.view(request, modelMap, "list_paging");
    }

    @ResponseBody
    @RequestMapping(value = "/todotask/sendAudioUrl/{taskId}", method = RequestMethod.POST)
    public Map<String, Object> sendAudioUrl(@ModelAttribute AudioMsg model, @PathVariable("taskId") Long taskId, HttpServletRequest request) throws Exception {
        OrgUser user = getCurrentUser(request);
        Map<String, Object> result = new HashMap<String, Object>();

        // 把audioUrl插入presale_record
        PresaleRecord record = createTaskRecord(taskId, user);
        PresaleTask task = (PresaleTask)presaleTaskService.getModel(taskId);

        if(record != null){
            // 把audioUrl插入audio_msg
            createAudioMsg(task, record.getId(), model, user);
            // 更新presale_task的状态
            task.setHandleStatus(Cons.PRESALE_HANDLE_STATUS.UPDATED.name());
            presaleTaskService.updateModel(task);
            result.put("success", true);
        } else {
            result.put("error", "任务记录创建失败！");
        }
        return result;
    }

    private void createAudioMsg(PresaleTask task, Long recordId, AudioMsg model, OrgUser user) throws Exception {
        AudioMsg audioMsg = new AudioMsg();
        audioMsg.setSystem(Cons.SYSTEMS[2]);
        audioMsg.setReceiverType(CcUser.class.getSimpleName());
        audioMsg.setReceiverId(task.getCurrentOwnerId());
        audioMsg.setRelObjType(PresaleTask.class.getSimpleName());
        audioMsg.setRelObjId(task.getId());
        audioMsg.setFromOrgId(task.getOrgId());
        
        audioMsg.setRelRecordId(recordId);
        audioMsg.setAudioUrls(model.getAudioUrls());
        audioMsg.setContent(model.getContent());
        audioMsg.setAudioDuration(model.getAudioDuration());
        audioMsg.setCreateBy(user.getId());
        audioMsgService.createModel(audioMsg);
		// 异步下载录音文件
		asyncWorker.downloadAndSaveWechatAudioUrls(audioMsg.getAudioUrls(), audioMsg);
    }

    private PresaleRecord createTaskRecord(Long taskId, OrgUser user) throws Exception {
        PresaleRecord record = new PresaleRecord();
        record.setTaskId(taskId);
        record.setType(Cons.PRESALE_RECORD_TYPE.SA_AUDIO.name());
        record.setNotifyType(Cons.NOTIFY_TYPE.CC.name());
        record.setCreateByName(user.getRealname());
        record.setCreateBy(user.getId());
        record.setCreateByType(OrgUser.class.getSimpleName());

        boolean success = presaleTaskService.createRecord(record);
        return success? record : null;
    }
    
    /**
     * 通讯录列表
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addressbook", method = RequestMethod.GET)
    public String getAddressBook(HttpServletRequest request, ModelMap modelMap) throws Exception {
    	checkUser(request);
        // 获取用户信息
        OrgUser user = getCurrentUser(request);
        
        // 获取所有潜客列表
    	Map<String, Object> criteria = super.buildQueryCriteria(request, 0);
    	criteria.put("from_org_id", user.getOrgId());
    	criteria.put("from_sa_id", user.getId());
    	criteria.put("firstSpellSort", "isAsc");
    	List<PotentialCustomer> modelList = (List<PotentialCustomer>) potentialCustomerService.getSimpleModelList(criteria);
    	
    	modelMap.addAttribute("modelList", modelList);
        return super.view(request, modelMap, "addressbook");
    }
    
    /**
     * 转成待办任务
     * @param taskId
     * @param request
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/targetTodotask/{taskId}", method = RequestMethod.GET)
    public String targetTodoTask(@PathVariable("taskId") Long taskId, HttpServletRequest request, ModelMap modelMap) throws Exception {
    	PresaleTask task = (PresaleTask)presaleTaskService.getModel(taskId);
    	
    	task.setNextHandleTime(com.elementwin.api.utils.DateUtils.datetimeString2Date(com.elementwin.api.utils.DateUtils.getDate(new Date()) + " 00:00:00"));
    	task.setHandleStatus(Cons.PRESALE_HANDLE_STATUS.PENDING.name());
    	
    	presaleTaskService.updateModel(task);
    	
        return "redirect:/weixin/contact/addressbook";
    }
}

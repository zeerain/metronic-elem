package com.elementwin.bs.controller.wx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.elementwin.api.config.Cons;
import com.elementwin.api.utils.DateUtils;
import com.elementwin.api.utils.SpellHelper;
import com.elementwin.bs.cache.MetadataCache;
import com.elementwin.bs.model.ECard;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.model.PotentialCustomer;
import com.elementwin.bs.model.PresaleTask;
import com.elementwin.bs.service.MetadataItemService;
import com.elementwin.bs.service.PotentialCustomerService;
import com.elementwin.bs.service.PresaleImportService;
import com.elementwin.bs.service.PresaleTaskService;
import com.elementwin.bs.utils.TaskHelper;

import dibo.framework.utils.V;

@Controller
@RequestMapping("/weixin/ecard")
public class EcardController extends BaseWxController {
	private static Logger logger = Logger.getLogger(EcardController.class);
	
	private ECard eCardModel = null;
	
	@Autowired
	private PresaleTaskService presaleTaskService;
	
	@Autowired
	private PotentialCustomerService potentialCustomerService;
	
	@Autowired
	private MetadataItemService metadataItemService;
	
	@Autowired
	private PresaleImportService presaleImportService;
	
	@Override
	protected String getViewPrefix() {
		return "weixin/ecard";
	}
	
	// 微信企业号--个人名片
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String ecardView(HttpServletRequest request, ModelMap modelMap) throws Exception {
		OrgUser orgUser = getCurrentUser(request);
		
		modelMap.addAttribute("model", orgUser);
		
		// 二维码参数
		modelMap.addAttribute("ecard", getEcardModel());
		
		return super.view(request, modelMap, "view");
	}
	
	/**
	 * 企业号二维码
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/make", method = RequestMethod.GET)
	public String makeEcard(HttpServletRequest request, ModelMap modelMap) throws Exception {
		return super.view(request, modelMap, "make");
	}
	
	/***
	 * 保存customer
	 * @param request
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/saveCustomer", method=RequestMethod.POST)
	public Map<String, Object> createCustomer(HttpServletRequest request, ModelMap modelMap) throws Exception{
		OrgUser orgUser = getCurrentUser(request);
		boolean isCreateCustomer = true; // "true".equalsIgnoreCase(request.getParameter("isCreateCustomer"));
		boolean isCreateTask = "true".equalsIgnoreCase(request.getParameter("isCreateTask"));
		
		Map<String, Object> result = new HashMap<String, Object>();
		// 创建潜客
		if(isCreateCustomer || isCreateTask){
			PotentialCustomer customer = null;
			PresaleTask task = null;
			
			String phone = request.getParameter("mobilePhone");
			Map<String, Object> criteria = new HashMap<String, Object>();
			criteria.put("phone", phone);
			// 已存在
			List<PotentialCustomer> matchedList = (List<PotentialCustomer>) potentialCustomerService.getModelList(criteria);
			if(V.isNotEmpty(matchedList)){
				result.put("isDuplicate", true);
				customer = matchedList.get(0);
				
				criteria.clear();
				criteria.put("org_id", orgUser.getOrgId());
				criteria.put("customer_id", customer.getId());
				// 获取Task并进行跟进
				List<PresaleTask> tasks = (List<PresaleTask>) presaleTaskService.getModelList(criteria);
				if(V.isNotEmpty(tasks)){
					task = tasks.get(0);
					if(isCreateTask){
						task.setStatus(Cons.PRESALE_STATUS.CONTINUE.name());
						task.setComment(task.getComment() + "（客户到店，销售 "+orgUser.getRealname()+" 要继续跟进）");
						presaleTaskService.updateTaskStatus(task);						
					}
				}
			}
			if(customer == null){
				customer = createPotentialCustomer(request, orgUser, result);
				result.put("createCustomer", customer != null);
			}
			// 创建task
			if(task == null){
				task = createPresaleTask(request, orgUser, customer);
				result.put("createTask", task != null);
			}
		}
		
		return result;
	}
	
	/***
	 * 创建潜客
	 * @param request
	 * @param orgUser
	 * @return
	 * @throws Exception
	 */
	private PotentialCustomer createPotentialCustomer(HttpServletRequest request, OrgUser orgUser, Map<String, Object> result) throws Exception{
		String phone = request.getParameter("mobilePhone");
		// 新建
		PotentialCustomer customer = new PotentialCustomer();
		customer.setFromOrgId(orgUser.getOrgId());
		customer.setFromSaId(orgUser.getId());
		customer.setName(request.getParameter("realname"));
		customer.setPhone(phone);
		customer.setRegisterDate(DateUtils.getDate(null));
		customer.setCategory(request.getParameter("category"));
		
		String tags = request.getParameter("tags");
		if(V.isEmpty(tags)){
			tags = Cons.MATCH_KEYWORDS_SERVICE[0];
		}
		customer.setTags(tags); //进店标记
		
		String firstSpell = SpellHelper.getFirstSpell(customer.getName(), SpellHelper.TYPE.SINGLE, 1);
		customer.setFirstSpell(firstSpell);
		
		String comment = "";
		String organization = request.getParameter("organization");
		if(V.isNotEmpty(organization)){
			comment += "单位: " + organization;
		}
		String email = request.getParameter("email");
		if(V.isNotEmpty(email)){
			if(comment.length() > 0){
				comment += ", ";
			}
			comment += "Email: " + email;
		}
		String address = request.getParameter("orgAddress");
		if(V.isNotEmpty(address)){
			if(comment.length() > 0){
				comment += ", ";
			}
			comment += "地址: " + address;
		}
		customer.setComment(comment);
		customer.setCreateBy(orgUser.getId());
		
		boolean success = potentialCustomerService.createModel(customer);

		return success? customer : null;			
	}
	
	/***
	 * 创建售前任务
	 * @param request
	 * @param orgUser
	 * @param customer
	 * @return
	 */
	private PresaleTask createPresaleTask(HttpServletRequest request,
			OrgUser orgUser, PotentialCustomer customer) throws Exception{
		PresaleTask task = new PresaleTask();
		task.setImportId(0L);
		task.setOrgId(orgUser.getOrgId());
		task.setRowIndex(0);
		task.setSalespersonId(orgUser.getId());
		task.setSalespersonName(orgUser.getRealname());
		task.setCustomerId(customer.getId());
		task.setCustomerName(customer.getName());
		task.setCustomerPhone(customer.getPhone());
		task.setCustomerTags(customer.getTags());
		task.setRegisterDate(customer.getRegisterDate());
		task.setCreateBy(customer.getCreateBy());
		task.setComment(customer.getComment());
		task.setAssignBy(0L);
		
		TaskHelper.assignTask(presaleImportService, task, orgUser.getOrgId());
		
		boolean success = presaleTaskService.createModel(task);
		return success? task : null;		
	}
	
	/**
	 * 初始化Ecard
	 * @return
	 */
	private ECard getEcardModel(){
		// 二维码参数
		if(eCardModel == null){
			eCardModel = new ECard();
			eCardModel.setEcardSize(MetadataCache.getSystemProperty("ecard.size"));
			eCardModel.setEcardFill(MetadataCache.getSystemProperty("ecard.fill"));
			eCardModel.setEcardRadius(MetadataCache.getSystemProperty("ecard.radius"));
			eCardModel.setEcardMode(MetadataCache.getSystemProperty("ecard.mode"));
			eCardModel.setEcardFontcolor(MetadataCache.getSystemProperty("ecard.fontcolor"));
		}
		return eCardModel;
	}
		
}
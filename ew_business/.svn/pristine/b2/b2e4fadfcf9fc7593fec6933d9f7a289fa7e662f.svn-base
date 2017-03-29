package com.elementwin.bs.model;

import java.util.*;

import com.elementwin.api.config.Cons;
import com.elementwin.bs.model.PresaleRecord;

import dibo.framework.utils.V;

/***
 * 售前任务
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-18
 * Copyright 2016 www.Dibo.ltd
 */
public class PresaleTask extends com.elementwin.common.model.PresaleTask{  
	
	private static final long serialVersionUID = 75158164223894896L;
	
	private PotentialCustomer customer;
	
	// 关联对象-PresaleRecord
	private List<PresaleRecord> presaleRecordList;	

	/***
	 * 获取最近的两条
	 * @return
	 */
	public List<PresaleRecord> getLatestPresaleRecordList() {
		if(V.isNotEmpty(presaleRecordList)){
			if(presaleRecordList.size() >= 2){
				return presaleRecordList.subList(presaleRecordList.size()-2, presaleRecordList.size());				
			}
		}
		return presaleRecordList;
	}
	
	public List<PresaleRecord> getPresaleRecordList() {
		return presaleRecordList;
	}
	public void setPresaleRecordList(List<PresaleRecord> presaleRecordList) {
		this.presaleRecordList = presaleRecordList;
	}
	
	/***
	 * 重新组装记录
	 */
	public void rebuildPresaleRecordList(){
		if(V.isNotEmpty(presaleRecordList)){
			Map<Long, PresaleRecord> tempMap = new HashMap<Long, PresaleRecord>();
			for(PresaleRecord record : presaleRecordList){
				if(Cons.PRESALE_RECORD_TYPE.SA_UPDATE.name().equalsIgnoreCase(record.getType())){
					tempMap.put(record.getId(), record);
				}
			}
			List<PresaleRecord> newRecordList = new ArrayList<PresaleRecord>();
			for(PresaleRecord record : presaleRecordList){
				if(Cons.PRESALE_RECORD_TYPE.CALL.name().equalsIgnoreCase(record.getType())){
					if(record.getRefererId() != null){
						record.setSaFeedbackRecord(tempMap.get(record.getRefererId()));
					}
					newRecordList.add(record);
				}
			}
			this.setPresaleRecordList(newRecordList);
			tempMap = null;
		}
	}

	/***
	 * 获取最新的一条记录
	 * @return
	 */
	private boolean isRebuilt = false;
	public PresaleRecord getLatestRecord(){
		if(!isRebuilt){
			rebuildPresaleRecordList();
			isRebuilt = true;
		}
		if(V.isNotEmpty(this.getPresaleRecordList())){
			return this.getPresaleRecordList().get(0);
		}
		return null;
	}
	
	public PotentialCustomer getCustomer() {
		return customer;
	}
	public void setCustomer(PotentialCustomer customer) {
		this.customer = customer;
	}
	
	/***
	 * 获取通话次数
	 * @return
	 */
	public int getCallNumer(){
		int number = 0;
		if(V.isNotEmpty(presaleRecordList)){
			for(PresaleRecord record : presaleRecordList){
				if(Cons.PRESALE_RECORD_TYPE.CALL.name().equals(record.getType())){
					number++;
				}
			}
		}
		return number;
	}
	
	public String getImportanceClass(){
		String[] classList = new String[]{ "rank-info", "rank-warning", "rank-danger"};
		return classList[getImportance()];
	}
	public int getImportance(){
		int urgent = 0;
		if(V.isNotEmpty(presaleRecordList)){
			Long id = null; Date expireDate = null;
			// 获取最新的待响应任务
			for(PresaleRecord record : presaleRecordList){
				if(record.getExpiredFlag() == PresaleRecord.EXPIRED_FLAG_SET){
					if(id == null || record.getId() > id){
						id = record.getId();
						expireDate = record.getExpireTime();						
					}
				}
			}
			// 获取最新的响应
			for(PresaleRecord record : presaleRecordList){
				if((id == null || record.getId() > id) && record.getExpiredFlag() > 0){
					expireDate = null;
					break;
				}
			}
			if(expireDate != null){
				long times = System.currentTimeMillis() - expireDate.getTime();
				if(times > 0){
					if(times <= 24*60*60000){
						urgent = 1;
					}
					else{
						urgent = 2;
					}
				}
			}
		}
		return urgent;
	}

	public String getWeek() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getNextHandleTime());
		int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return Cons.WEEK[intWeek];
	}
}
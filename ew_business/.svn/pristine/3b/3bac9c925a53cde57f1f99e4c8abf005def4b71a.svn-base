package com.elementwin.bs.model;

import java.util.List;

/***
 * 售前任务-关联对象
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016-11-18
 * Copyright 2016 www.Dibo.ltd
 */
public class PresaleRecord extends com.elementwin.common.model.PresaleRecord{  

	private static final long serialVersionUID = -4969334614701234380L;

	// SA反馈的记录
	private PresaleRecord saFeedbackRecord = null;
		
	// 关联对象-PresaleCall
	private List<PresaleCall> presaleCallList;
	// 关联对象-录音
	private List<AudioMsg> audioMsgList; 
	
	public List<AudioMsg> getAudioMsgList() {
		return audioMsgList;
	}
	public void setAudioMsgList(List<AudioMsg> audioMsgList) {
		this.audioMsgList = audioMsgList;
	}
	public List<PresaleCall> getPresaleCallList() {
		return presaleCallList;
	}
	public void setPresaleCallList(List<PresaleCall> presaleCallList) {
		this.presaleCallList = presaleCallList;
	}
	
	public PresaleRecord getSaFeedbackRecord() {
		return saFeedbackRecord;
	}
	public void setSaFeedbackRecord(PresaleRecord saFeedbackRecord) {
		this.saFeedbackRecord = saFeedbackRecord;
	}
}
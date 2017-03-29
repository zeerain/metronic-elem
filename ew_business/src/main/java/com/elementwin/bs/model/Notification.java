package com.elementwin.bs.model;


/***
 * Copyright 2016 www.Dibo.ltd
 * Notification-消息通知
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
public class Notification extends com.elementwin.common.model.Notification{
	
	private static final long serialVersionUID = 5438277840098377303L;
	
    private Long readCnt; // 读取状态   0：未读； 大于0：已读

	public Long getReadCnt() {
		return readCnt;
	}

	public void setReadCnt(Long readCnt) {
		this.readCnt = readCnt;
	}
    
}
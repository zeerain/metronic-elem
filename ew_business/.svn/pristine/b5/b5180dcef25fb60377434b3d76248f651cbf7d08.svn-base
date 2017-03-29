package com.elementwin.bs.service;

import java.util.List;
import java.util.Map;

import com.elementwin.bs.model.Notification;
import com.elementwin.bs.model.OrgUser;

/***
 * Copyright 2016 www.Dibo.ltd
 * 消息通知相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
public interface NotificationService extends dibo.framework.service.BaseService{
	
	/***
	 * 创建通知已读记录
	 * @param userType
	 * @param userId
	 * @param notificationId
	 * @throws Exception
	 */
	public boolean createNotificationRead(String userType, Long userId, Long notificationId) throws Exception;
	
	/***
	 * 获取所有未读的消息通知
	 * @return
	 * @throws Exception
	 */
	public List<Notification> getUnreadNotificationList(Map<String, Object> criteria) throws Exception;

	public boolean isUserCanRead(OrgUser user, Notification notofication) throws Exception;
	
	/***
	 * 获取所有的消息通知
	 * @return
	 * @throws Exception
	 */
	public List<Notification> getNotificationList(Map<String, Object> criteria) throws Exception;
	
}
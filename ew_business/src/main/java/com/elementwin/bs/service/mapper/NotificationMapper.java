package com.elementwin.bs.service.mapper;

import java.util.List;
import java.util.Map;

import com.elementwin.common.model.NotificationRead;
import com.elementwin.bs.model.Notification;

/***
 * Copyright 2016 www.Dibo.ltd
 * 消息通知相关Mapper
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
public interface NotificationMapper extends dibo.framework.service.mapper.BaseMapper{

	int createNotificationRead(NotificationRead record) throws Exception;

	List<Notification> getUnreadNotificationList(Map<String, Object> criteria) throws Exception;
	
	List<Notification> getNotificationList(Map<String, Object> criteria) throws Exception;
	
}
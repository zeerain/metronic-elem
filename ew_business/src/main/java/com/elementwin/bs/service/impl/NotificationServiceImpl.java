package com.elementwin.bs.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.model.BaseModel;
import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.api.config.Cons;
import com.elementwin.bs.model.Notification;
import com.elementwin.bs.model.NotificationRead;
import com.elementwin.bs.model.OrgUser;
import com.elementwin.bs.service.NotificationService;
import com.elementwin.bs.service.mapper.NotificationMapper;
import com.elementwin.bs.utils.AppHelper;

/***
 * Copyright 2016 www.Dibo.ltd
 * 消息通知相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
@Service("notificationService")
@Transactional
public class NotificationServiceImpl extends BaseServiceImpl implements NotificationService {
	private static Logger logger = Logger.getLogger(NotificationServiceImpl.class);
	
	@Autowired
	NotificationMapper notificationMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return notificationMapper;
	}

	@Override
	@Transactional
	public boolean createModel(BaseModel model) throws Exception {
		Notification notice = (Notification)model;
		notice.setCreateBy(AppHelper.getCurrentUserId());
		
		return getMapper().create(model) > 0 || model.getId() != null;
	}
	
	@Override
	@Transactional
	public boolean createNotificationRead(String userType, Long userId,
			Long notificationId) throws Exception {
		NotificationRead record = new NotificationRead();
		record.setUserType(userType);
		record.setUserId(userId);
		record.setNotificationId(notificationId);
		return notificationMapper.createNotificationRead(record) > 0;
	}

	@Override
	public List<Notification> getUnreadNotificationList(Map<String, Object> criteria) throws Exception {
		if(criteria == null){
			criteria = new HashMap<String, Object>();
		}
		criteria.put("scope", Cons.SYSTEMS[1]);
		return notificationMapper.getUnreadNotificationList(criteria);
	}
	
	@Override
	public boolean isUserCanRead(OrgUser user, Notification notification) throws Exception {
		if(notification != null && notification.getScope().contains(Cons.SYSTEMS[1]) 
			&& (notification.getOrgId() == null || notification.getOrgId().equals(user.getOrgId()))
			&& (notification.getOrgRoleId() == null || notification.getOrgRoleId().equals(user.getOrgRoleItemId()))
		){
			return true;
		}
		
		return false;
	}
	
	@Override
	public List<Notification> getNotificationList(Map<String, Object> criteria) throws Exception {
		if(criteria == null){
			criteria = new HashMap<String, Object>();
		}
		criteria.put("scope", Cons.SYSTEMS[1]);
		return notificationMapper.getNotificationList(criteria);
	}
}
package com.elementwin.bs.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elementwin.bs.service.AudioMsgService;
import com.elementwin.bs.service.mapper.AudioMsgMapper;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;


/***
 * 语音消息相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2017-01-17
 * Copyright 2016 www.Dibo.ltd
 */
@Service("audioMsgService")
@Transactional
public class AudioMsgServiceImpl extends BaseServiceImpl implements AudioMsgService{
	private static Logger logger = Logger.getLogger(AudioMsgServiceImpl.class);
	
	@Autowired
	AudioMsgMapper audioMsgMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return audioMsgMapper;
	}
}
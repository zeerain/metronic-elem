package com.elementwin.bs.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.bs.service.AudioPackService;
import com.elementwin.bs.service.mapper.AudioPackMapper;

/***
 * 录音包相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2017-01-19
 * Copyright 2016 www.Dibo.ltd
 */
@Service("audioPackService")
@Transactional
public class AudioPackServiceImpl extends BaseServiceImpl implements AudioPackService{
	private static Logger logger = Logger.getLogger(AudioPackServiceImpl.class);
	
	@Autowired
	AudioPackMapper audioPackMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return audioPackMapper;
	}
}
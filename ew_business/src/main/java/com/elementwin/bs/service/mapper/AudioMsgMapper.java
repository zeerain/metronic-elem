package com.elementwin.bs.service.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.elementwin.bs.model.AudioMsg;
import com.elementwin.bs.model.PresaleRecord;

import dibo.framework.service.mapper.BaseMapper;

/***
 * 语音消息相关Mapper
 * @author Mazc@dibo.ltd
 * @version v1.0, 2017-01-17
 * Copyright 2016 www.Dibo.ltd
 */
public interface AudioMsgMapper extends BaseMapper{

	/***
	 * 获取记录相关的录音list
	 * @param modelList
	 * @return
	 */
	List<AudioMsg> getRecordRelatedAudioMsgList(@Param("list")List<PresaleRecord> modelList) throws Exception;
}
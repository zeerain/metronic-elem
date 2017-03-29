package com.elementwin.bs.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.api.config.Cons;
import com.elementwin.common.model.MetadataItem;
import com.elementwin.bs.service.MetadataItemService;
import com.elementwin.bs.service.mapper.MetadataItemMapper;

/***
 * Copyright 2016 www.Dibo.ltd
 * 元数据相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
@Service("metadataItemService")
@Transactional
public class MetadataItemServiceImpl extends BaseServiceImpl implements MetadataItemService {
	private static Logger logger = Logger.getLogger(MetadataItemServiceImpl.class);
	
	@Autowired
	MetadataItemMapper metadataItemMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return metadataItemMapper;
	}

	@Override
	public List<MetadataItem> getMetadataItems(Cons.METADATA_TYPE type) throws Exception {
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put("type", type.name());
		criteria.put("active", 1);
		return (List<MetadataItem>) super.getModelList(criteria);
	}
	
}
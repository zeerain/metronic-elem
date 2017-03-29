package com.elementwin.bs.service;

import java.util.List;

import com.elementwin.api.config.Cons;
import com.elementwin.common.model.MetadataItem;

/***
 * Copyright 2016 www.Dibo.ltd
 * 元数据相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/23
 */
public interface MetadataItemService extends dibo.framework.service.BaseService{
	
	public List<MetadataItem> getMetadataItems(Cons.METADATA_TYPE type) throws Exception;
	
}
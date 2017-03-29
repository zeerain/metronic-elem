package com.elementwin.bs.service.mapper;

import com.elementwin.bs.model.RevisitWorkOrder;
import com.elementwin.bs.model.RevisitWorkOrderOpinion;

/***
 * Copyright 2016 www.Dibo.ltd
 * 工单相关Mapper
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/09/13
 */
public interface RevisitWorkOrderMapper extends dibo.framework.service.mapper.BaseMapper{
	
	RevisitWorkOrder getWorkOrderWithOptions(Long workOrderId) throws Exception;

	boolean createWorkOrderOpinion(RevisitWorkOrderOpinion model) throws Exception;
	
}
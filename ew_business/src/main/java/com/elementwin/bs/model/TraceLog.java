package com.elementwin.bs.model;

import com.elementwin.api.config.Cons;

/***
 * Copyright 2016 www.Dibo.ltd
 * TraceLog-操作日志
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/09/13
 */
public class TraceLog extends com.elementwin.common.model.TraceLog{  
	
	private static final long serialVersionUID = 85621605926630821L;
	
	// 来源系统
	private String system = Cons.SYSTEMS[1];
	
	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

}
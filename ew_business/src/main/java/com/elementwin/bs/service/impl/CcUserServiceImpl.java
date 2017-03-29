package com.elementwin.bs.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elementwin.bs.service.CcUserService;
import com.elementwin.bs.service.mapper.CcUserMapper;

import dibo.framework.model.BaseModel;
import dibo.framework.service.impl.BaseServiceImpl;
import dibo.framework.service.mapper.BaseMapper;

/***
 * 用户相关操作Service实现类
 * @author Mazc@dibo.ltd
 */
@Service("ccUserService")
@Transactional
public class CcUserServiceImpl extends BaseServiceImpl implements CcUserService {
	private static Logger logger = Logger.getLogger(CcUserServiceImpl.class);
	
	@Autowired
	CcUserMapper customerMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return customerMapper;
	}
	
}
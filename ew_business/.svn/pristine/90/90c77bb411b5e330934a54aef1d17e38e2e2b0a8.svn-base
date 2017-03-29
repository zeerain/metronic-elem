package com.elementwin.bs.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.bs.service.BsMenuService;
import com.elementwin.bs.service.mapper.BsMenuMapper;

/***
 * 菜单相关操作Service实现类
 * @author Mazc@dibo.ltd
 */
@Service("bsMenuService")
@Transactional
public class BsMenuServiceImpl extends BaseServiceImpl implements BsMenuService {
	private static Logger logger = Logger.getLogger(BsMenuServiceImpl.class);
	
	@Autowired
	BsMenuMapper bsMenuMapper;

	@Override
	protected BaseMapper getMapper() {
		return bsMenuMapper;
	}
	
}
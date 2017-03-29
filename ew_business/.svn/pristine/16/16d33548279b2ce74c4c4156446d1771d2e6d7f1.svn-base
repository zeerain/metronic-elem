package com.elementwin.bs.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.bs.model.File;
import com.elementwin.bs.service.FileService;
import com.elementwin.bs.service.mapper.FileMapper;

/***
 * 文件相关操作Service实现类
 * @author Mazc@dibo.ltd
 */
@Service("fileService")
@Transactional
public class FileServiceImpl extends BaseServiceImpl implements FileService {
	private static Logger logger = Logger.getLogger(FileServiceImpl.class);
	
	@Autowired
	FileMapper fileMapper;

	@Override
	protected BaseMapper getMapper() {
		return fileMapper;
	}

	@Override
	public File getFileByUuid(String uuid) throws Exception {
		return fileMapper.getFileByUuid(uuid);
	}
	
}
package com.elementwin.bs.service.mapper;

import java.util.List;
import java.util.Map;

import com.elementwin.common.model.RevisitImportData;

/***
 * Copyright 2016 www.Dibo.ltd
 * 回访数据上传相关Mapper
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/31
 */
public interface RevisitImportMapper extends dibo.framework.service.mapper.BaseMapper{
	/***
	 * 创建导入数据
	 * @param data
	 * @return
	 * @throws Exception
	 */
	int createData(RevisitImportData data) throws Exception;
	
	/***
	 * 获取导入数据列表
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	List<RevisitImportData> getDataList(Map<String, Object> criteria) throws Exception;

	int findDuplicateData(RevisitImportData data) throws Exception;

	/***
	 * 获取导入数据的部分内容
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	List<RevisitImportData> getSimpleImportDataList(Map<String, Object> criteria) throws Exception;
}
package com.elementwin.bs.service;

import java.util.List;
import java.util.Map;

import com.elementwin.common.model.RevisitImportData;

/***
 * Copyright 2016 www.Dibo.ltd
 * 回访数据上传相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/31
 */
public interface RevisitImportService extends dibo.framework.service.BaseService{
	
	/***
	 * 批量创建导入数据
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public int batchCreateData(List<? extends RevisitImportData> dataList) throws Exception;
	
	/***
	 * 获取导入数据列表
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	public List<? extends RevisitImportData> getDataList(Map<String, Object> criteria) throws Exception;
	
	/***
	 * 是否是重复数据
	 * @return
	 * @throws Exception
	 */
	public List<String> findDuplicateData(List<RevisitImportData> dataRows) throws Exception;
	
	/***
	 * 获取导入数据的部分内容
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	List<RevisitImportData> getSimpleImportDataList(Map<String, Object> criteria) throws Exception;
	
}
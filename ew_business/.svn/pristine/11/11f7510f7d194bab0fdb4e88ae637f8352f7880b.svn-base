package com.elementwin.bs.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dibo.framework.model.BaseModel;
import dibo.framework.service.mapper.BaseMapper;
import dibo.framework.service.impl.BaseServiceImpl;

import com.elementwin.common.model.PresaleImportData;
import com.elementwin.common.model.RevisitImportData;
import com.elementwin.bs.service.RevisitImportService;
import com.elementwin.bs.service.mapper.RevisitImportMapper;
import com.elementwin.bs.utils.AppHelper;

/***
 * Copyright 2016 www.Dibo.ltd
 * 回访数据上传相关操作Service
 * @author Mazc@dibo.ltd
 * @version v1.0, 2016/08/31
 */
@Service("revisitImportService")
@Transactional
public class RevisitImportServiceImpl extends BaseServiceImpl implements RevisitImportService {
	private static Logger logger = Logger.getLogger(RevisitImportServiceImpl.class);
	
	@Autowired
	RevisitImportMapper revisitImportMapper;
	
	@Override
	protected BaseMapper getMapper() {
		return revisitImportMapper;
	}

	@Override
	public int getModelListCount(Map<String, Object> criteria) throws Exception {
		criteria = appendOrgIdCriteria(criteria);
		return super.getModelListCount(criteria);
	}

	@Override
	public List<? extends BaseModel> getModelList(
			Map<String, Object> criteria, int... page) throws Exception {
		criteria = appendOrgIdCriteria(criteria);
		return super.getModelList(criteria, page);
	}

	@Override
	public List<? extends BaseModel> getLimitModelList(
			Map<String, Object> criteria, int count) throws Exception {
		criteria = appendOrgIdCriteria(criteria);
		return super.getLimitModelList(criteria, count);
	}
	
	@Override
	@Transactional
	public int batchCreateData(List<? extends RevisitImportData> dataList)
			throws Exception {
		if(dataList == null || dataList.isEmpty()){
			return 0;			
		}
		for(RevisitImportData data : dataList){
			revisitImportMapper.createData(data);
		}
		return dataList.size();
	}

	@Override
	public List<? extends RevisitImportData> getDataList(Map<String, Object> criteria)
			throws Exception {
		criteria = appendOrgIdCriteria(criteria);
		return revisitImportMapper.getDataList(criteria);
	}
	
	private Map<String, Object> appendOrgIdCriteria(Map<String, Object> criteria){
		if(criteria == null){
			criteria = new HashMap<String, Object>();
		}
		criteria.put("org_id", AppHelper.getCurrentUser().getOrgId());
		return criteria;
	}

	@Override
	@Transactional
	public List<String> findDuplicateData(List<RevisitImportData> dataRows)
			throws Exception {
		if(dataRows == null || dataRows.isEmpty()){
			return null;
		}
		List<String> indexList = new ArrayList<String>();
		List<String> importIndexList = new ArrayList<String>();
		Set<String> duplicateSet = new HashSet<String>();
		//TODO 需要优化
		for(RevisitImportData data : dataRows){
			if(revisitImportMapper.findDuplicateData(data) > 0){
				indexList.add(String.valueOf(data.getRowIndex()));
			}
			String key = data.getCarFrameNo()+"_"+data.getServiceEndDate();
			if(duplicateSet.contains(key)){
				importIndexList.add(String.valueOf(data.getRowIndex()));				
			}
			else{
				duplicateSet.add(key);
			}
		}
		duplicateSet = null;
		if(indexList.isEmpty() && !importIndexList.isEmpty()){
			return importIndexList;
		}
		return indexList;
	}

	@Override
	public List<RevisitImportData> getSimpleImportDataList(
			Map<String, Object> criteria) throws Exception {
		return revisitImportMapper.getSimpleImportDataList(criteria);
	}
}
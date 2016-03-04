package com.mogujie.jarvis.web.service;

import com.mogujie.jarvis.web.entity.qo.OperationQo;
import com.mogujie.jarvis.web.entity.vo.OperationVo;
import com.mogujie.jarvis.web.mapper.OperationMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 * User: 清远
 * mail: qingyuan@mogujie.com
 * date: 16/3/1
 * time: 下午8:04
 */
@Service
public class OperationService {
  @Autowired
  private OperationMapper operationMapper;

  Logger logger = Logger.getLogger(this.getClass());

  public Map<String, Object> getOperations(OperationQo operationQo) {
    Map<String, Object> result = new HashMap<String, Object>();
    Integer count = operationMapper.getCountByCondition(operationQo);
    count = count == null ? 0 : count;

    List<OperationVo> operationVoList = this.operationMapper.getOperationsByCondition(operationQo);

    result.put("total", count);
    result.put("rows", operationVoList);

    return result;
  }

  public List<String> getAllOperationTitles() {
    return this.operationMapper.getAllOperationTitles();
  }

  public Map<String, Object> getSimilarOperationTitle(String title) {
    Map<String, Object> result = new HashMap<String, Object>();
    List<String> titles = this.operationMapper.getSimilarOperationTitle(title);
    List<Map> list = new ArrayList<>();
    for(int i=0; i<titles.size(); i++) {
      Map<String, Object> item = new HashMap<String, Object>();
      item.put("id", titles.get(i));
      item.put("text", titles.get(i));
      list.add(item);
    }

    result.put("total", titles.size());
    result.put("items", list);

    return result;
  }

}

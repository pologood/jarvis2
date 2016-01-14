package com.mogujie.jarvis.web.service;

import com.mogujie.jarvis.web.entity.qo.BizGroupQo;
import com.mogujie.jarvis.web.entity.vo.BizGroupVo;
import com.mogujie.jarvis.web.mapper.BizGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hejian on 16/1/13.
 */
@Service
public class BizGroupService {
    @Autowired
    BizGroupMapper bizGroupMapper;

    /*
    * 根据id获取bizGroup的相信信息
    * */
    public BizGroupVo getById(Integer id) {
        return bizGroupMapper.getById(id);
    }

    /*
    * 获取满足条件的所有bizGroup
    * */
    public List<BizGroupVo> getAllByCondition(BizGroupQo bizGroupQo) {
        return bizGroupMapper.getAllByCondition(bizGroupQo);
    }

    /*
    * 分页查询
    * */
    public Map<String, Object> getPaginationByCondition(BizGroupQo bizGroupQo) {
        Map<String, Object> result = new HashMap<String, Object>();

        Integer total = bizGroupMapper.getTotalByCondition(bizGroupQo);
        List<BizGroupVo> rows = bizGroupMapper.getByCondition(bizGroupQo);
        result.put("total", total);
        result.put("rows", rows);

        return result;
    }
}

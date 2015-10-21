package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.web.entity.vo.PlanSearchVo;
import com.mogujie.jarvis.web.entity.vo.PlanVo;
import java.util.List;

/**
 * Created by hejian on 15/10/21.
 */
public interface PlanMapper {
    public Integer getCountByCondition(PlanSearchVo planSearchVo);
    public List<PlanVo> getPlansByCondition(PlanSearchVo planSearchVo);
}

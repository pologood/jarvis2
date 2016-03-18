package com.mogujie.jarvis.dao.generate;

import com.mogujie.jarvis.dto.generate.DeparmentBizMap;
import com.mogujie.jarvis.dto.generate.DeparmentBizMapExample;
import com.mogujie.jarvis.dto.generate.DeparmentBizMapKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DeparmentBizMapMapper {
    int countByExample(DeparmentBizMapExample example);

    int deleteByExample(DeparmentBizMapExample example);

    int deleteByPrimaryKey(DeparmentBizMapKey key);

    int insert(DeparmentBizMap record);

    int insertSelective(DeparmentBizMap record);

    java.util.List<com.mogujie.jarvis.dto.generate.DeparmentBizMap> selectByExample(DeparmentBizMapExample example);

    DeparmentBizMap selectByPrimaryKey(DeparmentBizMapKey key);

    int updateByExampleSelective(@Param("record") DeparmentBizMap record, @Param("example") DeparmentBizMapExample example);

    int updateByExample(@Param("record") DeparmentBizMap record, @Param("example") DeparmentBizMapExample example);

    int updateByPrimaryKeySelective(DeparmentBizMap record);

    int updateByPrimaryKey(DeparmentBizMap record);
}
package com.mogujie.jarvis.dao;

import com.mogujie.jarvis.dto.Crontab;
import com.mogujie.jarvis.dto.CrontabExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CrontabMapper {
    int countByExample(CrontabExample example);

    int deleteByExample(CrontabExample example);

    int deleteByPrimaryKey(Integer cronId);

    int insert(Crontab record);

    int insertSelective(Crontab record);

    List<Crontab> selectByExample(CrontabExample example);

    Crontab selectByPrimaryKey(Integer cronId);

    int updateByExampleSelective(@Param("record") Crontab record, @Param("example") CrontabExample example);

    int updateByExample(@Param("record") Crontab record, @Param("example") CrontabExample example);

    int updateByPrimaryKeySelective(Crontab record);

    int updateByPrimaryKey(Crontab record);
}
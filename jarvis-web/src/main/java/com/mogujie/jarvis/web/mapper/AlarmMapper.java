package com.mogujie.jarvis.web.mapper;

import com.mogujie.jarvis.web.entity.vo.AlarmVo;

import java.util.List;

/**
 * Created by hejian on 15/12/17.
 */
public interface AlarmMapper {
    List<AlarmVo> getAllAlarmsByJobId(Long jobId);

    List<AlarmVo> getEnableAlarmsByJobId(Long jobId);

}

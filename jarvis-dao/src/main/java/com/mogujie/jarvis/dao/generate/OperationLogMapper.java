package com.mogujie.jarvis.dao.generate;

import com.mogujie.jarvis.dto.generate.OperationLog;
import com.mogujie.jarvis.dto.generate.OperationLogExample;
import com.mogujie.jarvis.dto.generate.OperationLogWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OperationLogMapper {
    int countByExample(OperationLogExample example);

    int deleteByExample(OperationLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OperationLogWithBLOBs record);

    int insertSelective(OperationLogWithBLOBs record);

    java.util.List<com.mogujie.jarvis.dto.generate.OperationLogWithBLOBs> selectByExampleWithBLOBs(OperationLogExample example);

    java.util.List<com.mogujie.jarvis.dto.generate.OperationLog> selectByExample(OperationLogExample example);

    OperationLogWithBLOBs selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OperationLogWithBLOBs record, @Param("example") OperationLogExample example);

    int updateByExampleWithBLOBs(@Param("record") OperationLogWithBLOBs record, @Param("example") OperationLogExample example);

    int updateByExample(@Param("record") OperationLog record, @Param("example") OperationLogExample example);

    int updateByPrimaryKeySelective(OperationLogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(OperationLogWithBLOBs record);

    int updateByPrimaryKey(OperationLog record);
}
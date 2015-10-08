/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月9日 下午2:43:18
 */

package com.mogujie.jarvis.server.scheduler.event;

import java.util.HashMap;
import java.util.Map;

import com.mogujie.jarvis.server.domain.MODIFY_JOB_TYPE;
import com.mogujie.jarvis.server.domain.ModifyJobEntry;



/**
 * @author guangming
 *
 */
public class ModifyJobEvent extends DAGJobEvent {
    private Map<MODIFY_JOB_TYPE, ModifyJobEntry> modifyJobMap =
            new HashMap<MODIFY_JOB_TYPE, ModifyJobEntry>();

    public ModifyJobEvent(long jobId, Map<MODIFY_JOB_TYPE, ModifyJobEntry> newMap) {
       super(jobId);
       this.modifyJobMap = newMap;
    }

    public Map<MODIFY_JOB_TYPE, ModifyJobEntry> getModifyJobMap() {
        return modifyJobMap;
    }

    public void setModifyJobMap(Map<MODIFY_JOB_TYPE, ModifyJobEntry> modifyJobMap) {
        this.modifyJobMap = modifyJobMap;
    }

    public void addModifyJobEntry(MODIFY_JOB_TYPE modifyJobType, ModifyJobEntry entry) {
        if (!modifyJobMap.containsKey(modifyJobType)) {
            modifyJobMap.put(modifyJobType, entry);
        }
    }

}

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

import com.mogujie.jarvis.server.domain.ModifyJobType;
import com.mogujie.jarvis.server.domain.ModifyJobEntry;



/**
 * @author guangming
 *
 */
public class ModifyJobEvent extends DAGJobEvent {
    private Map<ModifyJobType, ModifyJobEntry> modifyJobMap =
            new HashMap<ModifyJobType, ModifyJobEntry>();

    public ModifyJobEvent(long jobId, Map<ModifyJobType, ModifyJobEntry> newMap) {
       super(jobId);
       this.modifyJobMap = newMap;
    }

    public Map<ModifyJobType, ModifyJobEntry> getModifyJobMap() {
        return modifyJobMap;
    }

    public void setModifyJobMap(Map<ModifyJobType, ModifyJobEntry> modifyJobMap) {
        this.modifyJobMap = modifyJobMap;
    }

    public void addModifyJobEntry(ModifyJobType modifyJobType, ModifyJobEntry entry) {
        if (!modifyJobMap.containsKey(modifyJobType)) {
            modifyJobMap.put(modifyJobType, entry);
        }
    }

}

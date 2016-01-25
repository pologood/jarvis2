package com.mogujie.jarvis.tasks;

import com.mogujie.jarvis.tasks.domain.HiveTaskEntity;
import com.mogujie.jarvis.tasks.util.HiveConfigUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by muming on 15/12/7.
 */
public class TestHiveConfigUtils {

    @Test
    public void read(){

        String app = "ironman";
        HiveTaskEntity hive = HiveConfigUtils.getHiveJobEntry(app);
        Assert.assertNotNull(hive);
    }

}

package com.mogujie.jarvis.worker;

import com.mogujie.jarvis.core.exeception.AcceptanceException;
import com.mogujie.jarvis.worker.strategy.AcceptanceResult;
import com.mogujie.jarvis.worker.strategy.AcceptanceStrategy;
import com.mogujie.jarvis.worker.strategy.impl.MemoryAcceptanceStrategy;
import org.junit.Test;

/**
 * Created by muming on 15/12/7.
 */
public class TestMemoryAcceptanceStrategy {

    @Test
    public void Test() throws AcceptanceException {
        AcceptanceStrategy mem = new MemoryAcceptanceStrategy();
//        AcceptanceResult result = mem.accept();
//        System.out.print(result.toString());
    }

}

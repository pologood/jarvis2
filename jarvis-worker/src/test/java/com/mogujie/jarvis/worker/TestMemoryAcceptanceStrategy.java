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
    public void Test(){
        AcceptanceResult result =null;
        try{
            AcceptanceStrategy mem = new MemoryAcceptanceStrategy();
            result = mem.accept();
        }catch (AcceptanceException ex){
        }

        System.out.print(result.toString());

    }

}

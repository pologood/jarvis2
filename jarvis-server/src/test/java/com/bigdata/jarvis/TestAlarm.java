package com.bigdata.jarvis;

import org.springframework.context.ApplicationContext;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mogujie.jarvis.server.service.IDService;
import com.mogujie.jarvis.server.util.SpringContext;

public class TestAlarm {

    public static void main(String[] args) throws UnirestException {
        ApplicationContext context = SpringContext.getApplicationContext();
        IDService service = context.getBean(IDService.class);
        System.out.println(service.getNextJobId());
        System.out.println(service.getNextTaskId());
    }

}

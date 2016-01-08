package com.mogujie.jarvis.web.controller.api;

import com.mogujie.jarvis.core.domain.AlarmType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hejian on 16/1/8.
 */
@Controller()
@RequestMapping(value = "/api/alarm")
public class AlarmAPIController {

    @RequestMapping(value = "/getAlarmType")
    @ResponseBody
    public List getAlarmType() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        AlarmType[] alarmTypes = AlarmType.values();
        for (AlarmType alarmType : alarmTypes) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", alarmType.getValue());
            map.put("text", alarmType.getDescription());
            list.add(map);
        }

        return list;
    }
}

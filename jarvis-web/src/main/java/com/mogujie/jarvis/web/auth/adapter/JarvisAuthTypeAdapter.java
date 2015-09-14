package com.mogujie.jarvis.web.auth.adapter;

import com.mogu.bigdata.admin.common.service.AuthTypeAdapter;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;

import java.util.LinkedHashMap;

/**
 * Created by hejian on 15/9/14.
 */
public class JarvisAuthTypeAdapter implements AuthTypeAdapter {
    @Override
    public LinkedHashMap<Integer, String> getAll() {
        LinkedHashMap<Integer, String> re = new LinkedHashMap<Integer, String>();
        for(JarvisAuthType a: JarvisAuthType.values()){
            re.put(a.getCode(), a.getName());
        }
        return re;
    }
}

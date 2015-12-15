package com.mogujie.jarvis.web.auth.adapter;

import com.mogu.bigdata.admin.client.adapter.AuthTypeAdapter;
import com.mogu.bigdata.admin.core.consts.PlatformConfig;
import com.mogu.bigdata.admin.core.entity.Permission;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by hejian on 15/9/14.
 */
public class JarvisAuthTypeAdapter implements AuthTypeAdapter {

    public List<Permission> getAll(Integer platformId, String secret) {
        if (!platformId.equals(PlatformConfig.platformId) || !secret.equals(PlatformConfig.secret)) {
            return null;
        }
        List<Permission> re = new ArrayList<Permission>();
        for(JarvisAuthType a: JarvisAuthType.values()) {
            re.add(new Permission(a.getCode(), a.getName()));
        }
        return re;
    }
}

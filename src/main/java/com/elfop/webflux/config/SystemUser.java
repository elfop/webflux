package com.elfop.webflux.config;

import lombok.Data;

/**
 * @Description:
 * @version: V1.0
 * @author: liu zhenming
 * @Email: 1119264845@qq.com
 * @Date: 2018-08-09 16:02
 */
@Data
public class SystemUser {

    protected String id;
    protected String sysId;

    public SystemUser() {
    }

    public SystemUser(final String id, final String sysId) {
        this.id = id;
        this.sysId = sysId;
    }

    public SystemUser getSystemUser(){
        return this;
    }
}

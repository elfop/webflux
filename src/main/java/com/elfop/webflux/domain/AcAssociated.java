package com.elfop.webflux.domain;

import com.elfop.webflux.config.SystemUser;
import lombok.Data;
import reactor.core.publisher.Flux;

/**
 * @Description:
 * @version: V1.0
 * @author: liu zhenming
 * @Email: 1119264845@qq.com
 * @Date: 2018-08-20 9:45
 */
@Data
public class AcAssociated {

    private String token;
    private Flux<SystemUser> systemUsers;

    public AcAssociated() {
    }

    public AcAssociated(String token, Flux<SystemUser> systemUsers) {
        this.token = token;
        this.systemUsers = systemUsers;
    }
}

package com.elfop.webflux.domain;

import com.elfop.webflux.config.SystemUser;
import lombok.Data;

import java.util.Objects;

/**
 * @Description:
 * @version: V1.0
 * @author: liu zhenming
 * @Email: 1119264845@qq.com
 * @Date: 2018-08-14 16:25
 */
@Data
public class TokenBind extends SystemUser {

    private final String token;

    public TokenBind(String id, String sysId, String token) {
        super(id, sysId);
        this.token = token;
    }

    public TokenBind getTokenBind() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        TokenBind tokenBind = (TokenBind) o;
        return Objects.equals(token, tokenBind.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token);
    }
}

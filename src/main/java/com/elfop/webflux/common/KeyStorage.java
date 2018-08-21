package com.elfop.webflux.common;

/**
 * @Description:
 * @version: V1.0
 * @author: liu zhenming
 * @Email: 1119264845@qq.com
 * @Date: 2018-08-07 17:29
 */
public class KeyStorage {

    /**
     * 认证中心用户 key (token)
     */
    public static final String AUTHENTICATED_USER_TOKEN = "acUserToken";

    /**
     * 认证中心用户 盐
     */
    public static final String AUTHENTICATED_USER_SALT = "acUserSalt";

    /**
     * 认证中心用户 状态
     */
    public static final String AUTHENTICATED_USER_STATE= "acUserState";


    /**
     * 用户所属子系统
     */
    public static final String SUBSYSTEM = "subsystem";

    /**
     * 子系统黑名单
     */
    public static final String SYS_BLACK = "sysBlack";

    /**
     * 用户黑名单
     */
    public static final String USER_BLACK ="userBlack";

    /**
     * 认证中心用户黑名单
     */
    public static final String AUTHENTICATED_USER_BLACK = "acUserBlack";

    /**
     * 系统用户 hash key
     */
    public static final String SYS_HASH_KEY ="sysHashKey";

    /**
     * 系统用户 hash token
     */
    public static final String SYS_HASH_USER_TOKEN = "sysToken";

    /**
     * 系统用户 状态
     */
    public static final String SYS_HASH_USER_STATE = "sysUserState";

    /**
     * token 的各个系统关联关系
     */
    public static final String AUTHENTICATED_TOKEN_ASSOCIATED = "acTokenAssociated";

}

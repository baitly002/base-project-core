package org.basecode.common.generator.mybatis.generator.tools;

import java.io.Serializable;

/**
 * JDBC配置模型
 */
public class JdbcConfig implements Serializable {
    private static final long serialVersionUID = -4079723733219143609L;
    private String driver;
    private String url;
    private String user;
    private String password;

    /**
     * @return 取得JDBC驱动类
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driver 设置JDBC驱动类
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * @return 取得JDBC连接字符串
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url 设置JDBC连接字符串
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return 取得JDBC连接的用户名，即当前数据库连接的属主
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user 设置JDBC连接的用户名，即当前数据库连接的属主
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return 取得JDBC连接的密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password 设置JDBC连接的密码
     */
    public void setPassword(String password) {
        this.password = password;
    }


}

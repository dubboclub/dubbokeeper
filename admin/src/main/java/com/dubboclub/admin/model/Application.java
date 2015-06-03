package com.dubboclub.admin.model;

/**
 * Created by bieber on 2015/6/3.
 */
public class Application {

    private String application; /* 应用名 */

    private String username;      /* 提供者用户名 */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Application that = (Application) o;

        if (application != null ? !application.equals(that.application) : that.application != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return true;
    }

    @java.lang.Override
    public int hashCode() {
        int result = application != null ? application.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }
}

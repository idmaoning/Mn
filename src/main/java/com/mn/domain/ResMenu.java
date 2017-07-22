package com.mn.domain;

/**
 * Created by Administrator on 2017/7/22 0022.
 */
public class ResMenu extends BaseEntity{
    public static final String NAME = "name" ;
    public static final String URL ="url" ;
    public static final String PID = "pid";

    private String name;//名称
    private String url;//地址
    private String pid = "";//父菜单Id

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResMenu resMenu = (ResMenu) o;

        if (name != null ? !name.equals(resMenu.name) : resMenu.name != null) return false;
        if (url != null ? !url.equals(resMenu.url) : resMenu.url != null) return false;
        return pid != null ? pid.equals(resMenu.pid) : resMenu.pid == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (pid != null ? pid.hashCode() : 0);
        return result;
    }
}

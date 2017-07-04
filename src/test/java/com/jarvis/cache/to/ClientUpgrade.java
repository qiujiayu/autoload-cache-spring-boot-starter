package com.jarvis.cache.to;

import java.io.Serializable;
import java.math.BigDecimal;

public class ClientUpgrade implements Cloneable, Serializable {

    private static final long serialVersionUID=8051150316128225945L;

    /**
     * id
     */
    private Integer id;

    /**
     * 操作系统
     */
    private Integer os;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 版本号
     */
    private BigDecimal versionCode;

    /**
     * 版本名称
     */
    private String versionName;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 是否强制更新
     */
    private Byte forceUpgrade;

    /**
     * 更新日志
     */
    private String changeLog;

    @Override
    public String toString() {
        return "ClientUpgrade [id=" + id + ", os=" + os + ", packageName=" + packageName + ", versionCode=" + versionCode + ", versionName=" + versionName + ", downloadUrl=" + downloadUrl
            + ", forceUpgrade=" + forceUpgrade + ", changeLog=" + changeLog + "]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id=id;
    }

    public Integer getOs() {
        return os;
    }

    public void setOs(Integer os) {
        this.os=os;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName=packageName;
    }

    public void setVersionCode(BigDecimal versionCode) {
        this.versionCode=versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName=versionName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl=downloadUrl;
    }

    public Byte getForceUpgrade() {
        return forceUpgrade;
    }

    public void setForceUpgrade(Byte forceUpgrade) {
        this.forceUpgrade=forceUpgrade;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog=changeLog;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
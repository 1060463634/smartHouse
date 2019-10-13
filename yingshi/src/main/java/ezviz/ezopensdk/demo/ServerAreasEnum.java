package ezviz.ezopensdk.demo;

/**
 * 开放平台服务端在分为海外和国内，海外又分为5个大区
 * （北美、南美、新加坡（亚洲）、俄罗斯、欧洲）
 * 必须根据当前使用的AppKey对应大区切换到所在大区的服务器
 * 否则EZOpenSDK的接口调用将会出现异常
 */
public enum ServerAreasEnum {

    /**
     * 国内
     */
    ASIA_CHINA(0,"Asia-China", "https://open.ys7.com", "https://openauth.ys7.com", "26810f3acd794862b608b6cfbc32a6b8"),
    /**
     * 海外：俄罗斯
     */
    ASIA_Russia(5, "Asia-Russia", "https://irusopen.ezvizru.com", "https://irusopenauth.ezvizru.com"),
    /**
     * 海外：亚洲
     * （服务亚洲的所有国家，但不包括中国和俄罗斯）
     */
    ASIA(10, "Asia", "https://isgpopen.ezvizlife.com", "https://isgpopenauth.ezvizlife.com"),
    /**
     * 海外：北美洲
     */
    NORTH_AMERICA(15,"North America", "https://iusopen.ezvizlife.com", "https://iusopenauth.ezvizlife.com"),
    /**
     * 海外：南美洲
     */
    SOUTH_AMERICA(20, "South America", "https://isaopen.ezvizlife.com", "https://isaopenauth.ezvizlife.com"),
    /**
     * 海外：欧洲
     */
    EUROPE(25, "Europe", "https://ieuopen.ezvizlife.com", "https://ieuopenauth.ezvizlife.com");

    public int id;
    public String areaName;
    public String openApiServer;
    public String openAuthApiServer;
    // 预置的用于测试h5登录的appKey（该appKey的bundleId已绑定到ezviz.opensdk）
    public String defaultOpenAuthAppKey;

    ServerAreasEnum(int id, String areaName, String openApiServer, String openAuthApiServer){
        this.id = id;
        this.areaName = areaName;
        this.openApiServer = openApiServer;
        this.openAuthApiServer = openAuthApiServer;
    }

    ServerAreasEnum(int id, String areaName, String openApiServer, String openAuthApiServer, String defaultOpenAuthAppKey){
        this.id = id;
        this.areaName = areaName;
        this.openApiServer = openApiServer;
        this.openAuthApiServer = openAuthApiServer;
        this.defaultOpenAuthAppKey = defaultOpenAuthAppKey;
    }

    @Override
    public String toString() {
        return "id: " + id + ", " + "areaName: " + areaName + ", " + "openApiServer: " + openApiServer + ", " + "openAuthApiServer: " + openAuthApiServer;
    }
}

package org.mtt.webapi.xresource;

import org.mtt.webapi.core.XCloudService;

public class NotificationProxy extends XCloudService {
    
    public NotificationProxy() {
        super();
    }

    @Override
    public String getDefaultHost() {

           String xs = this.getConfigParameter("AMQ_HOST");
           if (xs == null) return "127.0.0.1";
           return xs;

    }

    @Override
    public int getDefaultPort() {
           return 61616;
    }

    @Override
    public String getDefaultURI() {
        return "notifications";
    }

    @Override
    public String getDefaultAlias() {
           return _XNotification;
    }
}

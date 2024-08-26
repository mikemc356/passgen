package com.systemwerx.common.web.service;

import java.sql.Connection;

public interface InstallService {
    public boolean processInstallActions(Connection conn, String schema);
}
package com.girlkun.database;

import com.girlkun.result.GirlkunResultSet;
import com.girlkun.result.ResultSetImpl;
import com.girlkun.utils.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Simplified replacement for the original GirlkunDB that works with MySQL 8
 * forward-only cursors by materializing result sets without calling scrollable
 * cursor operations.
 */
public class GirlkunDB {

    private static final String PROP_PATH = "data/config/girlkundb.properties";

    private static String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static String DB_HOST = "localhost";
    private static String DB_PORT = "3306";
    private static String DB_NAME = "nrotabi";
    private static String DB_USER = "root";
    private static String DB_PASSWORD = "";
    private static int MIN_CONN = 1;
    private static int MAX_CONN = 1;
    private static long MAX_LIFE_TIME = 120_000L;
    public static boolean LOG_QUERY = false;

    private static HikariDataSource ds;

    static {
        loadProperties();
        initDataSource();
    }

    private static void loadProperties() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(PROP_PATH)) {
            props.load(fis);
            DRIVER = props.getProperty("girlkun.database.driver", DRIVER);
            DB_HOST = props.getProperty("girlkun.database.host", DB_HOST);
            DB_PORT = props.getProperty("girlkun.database.port", DB_PORT);
            DB_NAME = props.getProperty("girlkun.database.name", DB_NAME);
            DB_USER = props.getProperty("girlkun.database.user", DB_USER);
            DB_PASSWORD = props.getProperty("girlkun.database.pass", DB_PASSWORD);
            MIN_CONN = Integer.parseInt(props.getProperty("girlkun.database.min", String.valueOf(MIN_CONN)));
            MAX_CONN = Integer.parseInt(props.getProperty("girlkun.database.max", String.valueOf(MAX_CONN)));
            MAX_LIFE_TIME = Long.parseLong(props.getProperty("girlkun.database.lifetime", String.valueOf(MAX_LIFE_TIME)));
            LOG_QUERY = Boolean.parseBoolean(props.getProperty("girlkun.database.log", String.valueOf(LOG_QUERY)));
            Logger.success("Load file properties thành công!\n");
        } catch (IOException e) {
            Logger.error("Không đọc được cấu hình CSDL, dùng giá trị mặc định.\n");
        } catch (Exception e) {
            Logger.logException(GirlkunDB.class, e, "Không thể tải cấu hình CSDL");
        }
    }

    private static void initDataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(DRIVER);
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useUnicode=yes&characterEncoding=UTF-8&serverTimezone=UTC", DB_HOST, DB_PORT, DB_NAME);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMinimumIdle(MIN_CONN);
        config.setMaximumPoolSize(MAX_CONN);
        config.setMaxLifetime(MAX_LIFE_TIME);

        // Keep original tuning flags
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "true");

        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void close() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
        }
    }

    public static GirlkunResultSet executeQuery(String query) throws Exception {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            if (LOG_QUERY) {
                Logger.success("Thực thi thành công câu lệnh: " + ps + "\n");
            }
            return new ResultSetImpl(rs);
        } catch (Exception e) {
            Logger.error("Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
            throw e;
        }
    }

    public static GirlkunResultSet executeQuery(String query, Object... params) throws Exception {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (LOG_QUERY) {
                    Logger.success("Thực thi thành công câu lệnh: " + ps + "\n");
                }
                return new ResultSetImpl(rs);
            }
        } catch (Exception e) {
            Logger.error("Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
            throw e;
        }
    }

    public static int executeUpdate(String query) throws Exception {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            if (LOG_QUERY) {
                Logger.success("Thực thi thành công câu lệnh: " + ps + "\n");
            }
            return ps.executeUpdate();
        } catch (Exception e) {
            Logger.error("Có lỗi xảy ra khi thực thi câu lệnh: " + query + "\n");
            throw e;
        }
    }

    public static int executeUpdate(String query, Object... params) throws Exception {
        String finalQuery = query;
        if (query.contains("insert") && query.trim().endsWith("()")) {
            // auto-fill placeholder list when caller forgot to provide values
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < params.length; i++) {
                sb.append("?");
                if (i < params.length - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
            finalQuery = query.replace("()", sb.toString());
        }
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(finalQuery)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            if (LOG_QUERY) {
                Logger.success("Thực thi thành công câu lệnh: " + ps + "\n");
            }
            return ps.executeUpdate();
        } catch (Exception e) {
            Logger.error("Có lỗi xảy ra khi thực thi câu lệnh: " + finalQuery + "\n");
            throw e;
        }
    }
}

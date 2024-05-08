package com.kony.utilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariConfiguration {

	private static HikariDataSource datasource;
	private static final Logger logger = LogManager.getLogger(HikariConfiguration.class);

	public static HikariDataSource getDataSource(FabricRequestManager dcRequest) {
		try {
			if (datasource == null || datasource.isClosed())
				synchronized (HikariConfiguration.class) {
					if (datasource == null || datasource.isClosed()) {
						HikariConfig config = new HikariConfig();
						config.setJdbcUrl(EnvironmentConfigurationsHandler.getValue("DBX_DB_HOST_URL", dcRequest));
						config.setUsername(EnvironmentConfigurationsHandler.getValue("DBX_DB_USERNAME", dcRequest));
						config.setPassword(EnvironmentConfigurationsHandler.getValue("DBX_DB_PASSWORD", dcRequest));

						config.setMaxLifetime(Integer.parseInt("600000"));
						config.setMinimumIdle(Integer.parseInt("1"));
						config.setIdleTimeout(Integer.parseInt("300000"));
						config.setMaximumPoolSize(Integer.parseInt("10"));
						config.setAutoCommit(true);

						datasource = new HikariDataSource(config);
					}
				}

		} catch (Exception e) {
			logger.error("Error occured in hikari configuration", e);
		}
		return datasource;
	}

	public static Connection getconnection() {
		Connection con = null;
		try {
			con = datasource.getConnection();
		} catch (Exception e) {
			logger.error("Exception occured while fetching connection", e);
		}
		return con;
	}

	public static synchronized void closeDatasource() {
		try {
			if (datasource != null) {
				datasource.close();
				datasource = null;
			}
		} catch (Exception e) {
			logger.error("Exception occured in closing datasource");
		}
	}

	public static void close(Connection... connections) {
		for (Connection connection : connections) {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException sqlex) {
					logger.error("sql Exception in close", sqlex);
				}
			}
		}
	}

	// Method to close one or more result sets.
	public static void close(ResultSet... resultsets) {
		for (ResultSet resultset : resultsets) {
			if (resultset != null) {
				try {
					resultset.close();
				} catch (SQLException sqlex) {
					logger.error("sql Exception", sqlex);
				}
			}
		}
	}

	// Method to close one or more statements.
	public static void close(Statement... statements) {
		for (Statement statement : statements) {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException sqlex) {
					logger.error("sql Exception", sqlex);
				}
			}
		}
	}

}

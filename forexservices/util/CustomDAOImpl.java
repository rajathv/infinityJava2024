/**
 * 
 */
package com.kony.dbx.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.controller.DataControllerRequest;

/**
 * @author Gopinath Vaddepally KH2453
 *
 */
public class CustomDAOImpl implements CustomDAO,Constants {
	private static Logger logger = LogManager.getLogger(CustomDAOImpl.class);
	
	@Override
	public void batchInsert(String tableName, String colSpec, String[] insertRecordsArray,
			DataControllerRequest request) {
		HikariConfiguration.getDataSource(request);
        int count = 0;
        Connection connection = HikariConfiguration.getconnection();
        Statement stmt = null;
        int[] result = {};
        int batchSize = 0;
		try {
            batchSize = insertRecordsArray.length;
        } catch (NumberFormatException nfe) {
            logger.info("Batch Size for insert not provided in the request. Using the defaul value:" + batchSize);
        }

        try {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            for (int i = 0; i < insertRecordsArray.length; i++) {
            	
            	String query = "insert into " + tableName +" " + colSpec+  " values " + insertRecordsArray[i];
            	logger.error("query =>"+ query);
            	
            	String sqlToInsert = "insert into " + tableName;
            	sqlToInsert += colSpec != null ? colSpec : "";
            	sqlToInsert += " values " + insertRecordsArray[i];            	
                stmt.addBatch(sqlToInsert);

                count++;

                if (count % batchSize == 0) {
                    result = stmt.executeBatch();
                    if (batchSize != result.length) { //Something went wrong.
                        logger.error("Unexpected error while inserting records");
                    }
                    connection.commit(); // All well, commit
                    logger.info("Succesfully committed batch insert");
                }
            }

            if (count % batchSize != 0) { // Commit any leftovers
                result = stmt.executeBatch();
                if ((count % batchSize) != result.length) { //Something went wrong.
                    logger.error("Unexpected error while inserting records");
                }
                connection.commit(); // All well, commit
                logger.info("Succesfully committed batch insert");
            }
        } catch (Exception e) {
            logger.error("Exception while executing batch insert:",e);
            try {
				connection.rollback();
			} catch (SQLException e1) {
                logger.error(e1);
			}
        } finally {
           HikariConfiguration.close(stmt);
           HikariConfiguration.close(connection);
        }
	}
	
	@Override
	public int[] executeSQLs(DataControllerRequest request, String... sqls) {
		HikariConfiguration.getDataSource(request);
		Connection connection = HikariConfiguration.getconnection();
		int batchSize = 0;
		try {
            batchSize = 100;// TODO: Read from the server configuration
        } catch (NumberFormatException nfe) {
            logger.debug("Batch Size for insert not provided in the request. Using the defaul value:" + batchSize);
        }

		Statement stmt = null;
		int count = 0;
		int[] result = {};
		int[] finalResult = {};
		try {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            for (int i = 0; i < sqls.length; i++) {
            	
                stmt.addBatch(sqls[i]);
                 logger.error("sql statment =>"+sqls[i]);
                count++;

                if (count % batchSize == 0) {
                    result = stmt.executeBatch();
                    if (batchSize != result.length) { //Something went wrong.
                        logger.error("Unexpected error while inserting records");
                    }
                    connection.commit(); // All well, commit
                    finalResult = ArrayUtils.addAll(finalResult, result);
                    logger.error("Succesfully committed batch insert");
                }
            }

            if (count % batchSize != 0) { // Commit any leftovers
                result = stmt.executeBatch();
                if ((count % batchSize) != result.length) { //Something went wrong.
                    logger.error("Unexpected error while inserting records");
                }
                connection.commit(); // All well, commit
                finalResult = ArrayUtils.addAll(finalResult, result);
                logger.error("Succesfully committed batch insert");
            }
        } catch (Exception e) {
            logger.error("Exception while executing batch insert:",e);
            try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new RuntimeException("SQL Exception while execute batch SQL statement in batch #" + sqls.length/batchSize + " with exception:" + e.getMessage());
			}
        } finally {
           HikariConfiguration.close(stmt);
           HikariConfiguration.close(connection);
        }
		logger.error("Final Result in Delete"+finalResult);
        return finalResult;
	}
	
}

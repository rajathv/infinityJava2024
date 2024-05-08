package com.temenos.dbx.bulkpaymentservices.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.temenos.dbx.product.constants.ServiceId;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.dataobject.Result;

public class CancellationReasonDBOperation {
	private static final Logger LOG = LogManager.getLogger(CancellationReasonDBOperation.class);
	public static final String SCHEMA_NAME = EnvironmentConfigurationsHandler.getValue("DBX_SCHEMA_NAME");
	public static final String DB_CANCELLATIONREASONS_GET = SCHEMA_NAME + "_cancellationreason_get";
	//public static final String SAMPLE_FILES = "Sample_Files";
	
	
	public Result fetchCancellationReasons() {
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = DB_CANCELLATIONREASONS_GET;

		Result result;
		try {
			result = DBPServiceExecutorBuilder.builder()
					.withServiceId(serviceName)
					.withObjectId(null)
					.withOperationId(operationName)
					.withRequestParameters(null)
					.build().getResult();
			
		} catch (JSONException je) {
			LOG.error("Failed to fetch bulk payment cancellationreason", je);
			return ErrorCodeEnum.ERR_21218.setErrorCode(new Result());
		} catch (Exception e) {
			LOG.error("Caught exception at FetchCancellationReasons: ", e);
			return ErrorCodeEnum.ERR_21218.setErrorCode(new Result());
		}

		return result;
	}
}

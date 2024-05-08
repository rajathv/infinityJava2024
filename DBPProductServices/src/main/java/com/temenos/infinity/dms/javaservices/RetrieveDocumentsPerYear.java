package com.temenos.infinity.dms.javaservices;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.error.DBPErrorCodeSetter;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.dms.resource.api.DMSResource;
import com.temenos.infinity.dms.resource.impl.DMSResourceImpl;
import com.temenos.infinity.api.commons.exception.ApplicationException;

/**
 * 
 * @author TeamEverest
 * @version Java Service end point to login and search files
 * 
 */
public class RetrieveDocumentsPerYear implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(DMSResourceImpl.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		try {
			DMSResource downloadResource = DBPAPIAbstractFactoryImpl.getResource(DMSResource.class);
			String page = request.getParameter("page");
			String accountNnumber = request.getParameter("accountNumber");
			String customerNumber = request.getParameter("customerNumber");
			String year = request.getParameter("year");
			String subType = request.getParameter("subType");
			String auth_token = request.getHeader("X-Kony-Authorization");
			Result result = downloadResource.loginAndSearchFiles(request, page, accountNnumber, customerNumber, year, subType,
					auth_token);
			if (StringUtils.isNotBlank(result.getParamValueByName("dbpErrMsg"))
					&& result.getParamValueByName("dbpErrMsg").length() > 0) {
				return result;
			}
			return result;
		} 
		catch (ApplicationException e) {
			LOG.error("RetrieveDocumentsPerYear" + e);
			return DBPErrorCodeSetter.setError(e.getErrorCodeEnum(), new Result());
		}catch (Exception e) {
			LOG.error("RetrieveDocumentsPerYear" + e);
			return ErrorCodeEnum.ERR_25001.setErrorCode(new Result());
		}

	}
}

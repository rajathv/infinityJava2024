/**
 * 
 */
package com.temenos.dbx.product.combinedaccess.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ErrorCodes;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.combinedaccess.resource.api.UserLinkingResource;

/**
 * @author muthukumarv
 *
 */
public class UserDeLinkingOperation implements JavaService2 {
	LoggerUtil logger = new LoggerUtil(UserDeLinkingOperation.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			UserLinkingResource userLinkingResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(UserLinkingResource.class);
			result = userLinkingResource.userDeLinkingOperation(methodID, inputArray, request, response);
		} catch (ApplicationException e) {
			e.getErrorCodeEnum().setErrorCode(result);
			logger.error("Exception occured while userdelinking :" + e.getMessage(), e);
		} catch (Exception e) {
			HelperMethods.setValidationMsgwithCode("Delinking operation Failed", ErrorCodes.ERROR_UPDATING_RECORD, result);
			logger.error("Exception occured while userdelinking :" + e.getMessage(), e);
		}

		return result;
	}

}

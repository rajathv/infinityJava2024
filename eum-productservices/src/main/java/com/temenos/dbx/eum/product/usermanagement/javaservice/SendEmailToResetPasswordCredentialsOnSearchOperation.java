package com.temenos.dbx.eum.product.usermanagement.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.usermanagement.javaservice.ResetPasswordOperation;
import com.temenos.dbx.eum.product.usermanagement.javaservice.SendEmailToResetPasswordCredentialsOnSearchOperation;
import com.temenos.dbx.eum.product.usermanagement.resource.api.UserManagementResource;

public class SendEmailToResetPasswordCredentialsOnSearchOperation implements JavaService2{

	LoggerUtil logger = new LoggerUtil(ResetPasswordOperation.class);

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {

		logger = new LoggerUtil(SendEmailToResetPasswordCredentialsOnSearchOperation.class);
		
		 Result result = new Result();
	        try {
	            UserManagementResource userManagementResource = DBPAPIAbstractFactoryImpl.getInstance()
	                    .getFactoryInstance(ResourceFactory.class).getResource(UserManagementResource.class);
	           result = userManagementResource.sendMailToCustomerOnSearch(methodID, inputArray, dcRequest, dcResponse);
	        } catch(ApplicationException e) {
	        	e.getErrorCodeEnum().setErrorCode(result);
	        	logger.error("Exception occured while sending email to customer",  e);
	        }catch (Exception e) {
	            logger.error("Exception occured while sending email to customer",  e);
	        }

	        return result;
	}

}
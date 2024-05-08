package com.temenos.dbx.product.payeeservices.resource.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.BackendDelegateFactory;
import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.businessdelegate.api.AuthorizationChecksBusinessDelegate;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.payeeservices.backenddelegate.api.ExternalPayeeBackendDelegate;
import com.temenos.dbx.product.payeeservices.businessdelegate.api.ExternalPayeeBusinessDelegate;
import com.temenos.dbx.product.payeeservices.dto.ExternalPayeeBackendDTO;
import com.temenos.dbx.product.payeeservices.resource.api.ExternalPayeeResource;
import com.temenos.dbx.product.payeeservices.resource.api.InterBankPayeeResource;
import com.temenos.dbx.product.payeeservices.resource.api.InternationalPayeeResource;
import com.temenos.dbx.product.payeeservices.resource.api.IntraBankPayeeResource;

public class ExternalPayeeResourceImpl implements ExternalPayeeResource {

	private static final Logger LOG = LogManager.getLogger(ExternalPayeeResourceImpl.class);
	
	ExternalPayeeBusinessDelegate externalPayeeDelegate = DBPAPIAbstractFactoryImpl.getInstance()
			.getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(ExternalPayeeBusinessDelegate.class);
	
	ExternalPayeeBackendDelegate externalPayeeBackendDelegate = DBPAPIAbstractFactoryImpl.getInstance()
			.getFactoryInstance(BackendDelegateFactory.class).getBackendDelegate(ExternalPayeeBackendDelegate.class);

	AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
			.getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
	
	@Override
	public Result fetchAllMyPayees(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) {
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		Result result = new Result();
		
		//Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		//String userId = CustomerSession.getCustomerId(customer);
		
		List<ExternalPayeeBackendDTO> externalPayeeBackendDTOs = externalPayeeDelegate.fetchPayeesFromDBXOrch(request.getHeaderMap(), request);
		if(externalPayeeBackendDTOs == null) {
			LOG.error("Error occurred while fetching payees from dbx ");
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		if(externalPayeeBackendDTOs.size() == 0){
			LOG.error("No Payees Found");
	        return JSONToResult.convert(new JSONObject().put(Constants.EXTERNALACCOUNT, new JSONArray()).toString());
        }			

		if(externalPayeeBackendDTOs.get(0).getDbpErrMsg() != null && !(externalPayeeBackendDTOs.get(0).getDbpErrMsg()).isEmpty()) {
			return ErrorCodeEnum.ERR_00000.setErrorCode(result, externalPayeeBackendDTOs.get(0).getDbpErrCode(), externalPayeeBackendDTOs.get(0).getDbpErrMsg());
		}

		//externalPayeeBackendDTOs.stream().filter(c->c.getIsApproved().equalsIgnoreCase("1")).collect(Collectors.toList());
		//Applying filters like offset,limit,sort etc
		FilterDTO filterDTO = new FilterDTO();
		try {
			filterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
		} catch (IOException e) {
			LOG.error("Exception occurred while fetching params: ",e);
            return ErrorCodeEnum.ERR_21220.setErrorCode(new Result());
		}
		externalPayeeBackendDTOs = filterDTO.filter(externalPayeeBackendDTOs);
				
		try {
	        JSONArray records = new JSONArray(externalPayeeBackendDTOs);
	        JSONObject resultObject = new JSONObject();
	        resultObject.put(Constants.EXTERNALACCOUNT,records);
	        result = JSONToResult.convert(resultObject.toString());
		}
        catch(Exception exp) {
            LOG.error("Exception occurred while converting DTO to result: ",exp);
            return ErrorCodeEnum.ERR_12048.setErrorCode(new Result());
        }
		return result;
	}

	@Override
    public Result createPayee(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) {
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		Result result = new Result();
		
		String isInternationalAccount = (String) inputParams.get("isInternationalAccount");
		String isSameBankAccount = (String) inputParams.get("isSameBankAccount");
		
		if("false".equals(isInternationalAccount) && "false".equals(isSameBankAccount)) {
			InterBankPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(InterBankPayeeResource.class);
            result  = payeeResource.createPayee(methodId, inputArray, request, response);
		}
		else if("true".equals(isInternationalAccount) && "false".equals(isSameBankAccount)) {
			InternationalPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(InternationalPayeeResource.class);
			result  = payeeResource.createPayee(methodId, inputArray, request, response);
            
		}
		else if("true".equals(isSameBankAccount) && "false".equals(isInternationalAccount)) {
        	IntraBankPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(IntraBankPayeeResource.class);
            result  = payeeResource.createPayee(methodId, inputArray, request, response);
		}
		else {
			LOG.error("Payee Type is not valid.");
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
    }

    @Override
    public Result editPayee(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) {
    	@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		Result result = new Result();
		
		String isInternationalAccount = (String) inputParams.get("isInternationalAccount");
		String isSameBankAccount = (String) inputParams.get("isSameBankAccount");
		
		if("false".equals(isInternationalAccount) && "false".equals(isSameBankAccount)) {
			InterBankPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(InterBankPayeeResource.class);
            result  = payeeResource.editPayee(methodId, inputArray, request, response);
		}
		else if("true".equals(isInternationalAccount) && "false".equalsIgnoreCase(isSameBankAccount)) {
			InternationalPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(InternationalPayeeResource.class);
			result  = payeeResource.editPayee(methodId, inputArray, request, response);
            
		}
		else if("true".equals(isSameBankAccount) && "false".equals(isInternationalAccount)) {
        	IntraBankPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(IntraBankPayeeResource.class);
            result  = payeeResource.editPayee(methodId, inputArray, request, response);
		}
		else {
			LOG.error("Payee Type is not valid.");
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
    }

    @Override
    public Result deletePayee(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) {
    	@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		Result result = new Result();
		
		String isInternationalAccount = (String) inputParams.get("isInternationalAccount");
		String isSameBankAccount = (String) inputParams.get("isSameBankAccount");
		
		if("false".equals(isInternationalAccount) && "false".equals(isSameBankAccount)) {
			InterBankPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(InterBankPayeeResource.class);
            result  = payeeResource.deletePayee(methodId, inputArray, request, response);
		}
		else if("true".equals(isInternationalAccount) && "false".equals(isSameBankAccount)) {
			InternationalPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(InternationalPayeeResource.class);
			result  = payeeResource.deletePayee(methodId, inputArray, request, response);
            
		}
		else if("true".equals(isSameBankAccount) && "false".equals(isInternationalAccount)) {
        	IntraBankPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(IntraBankPayeeResource.class);
            result  = payeeResource.deletePayee(methodId, inputArray, request, response);
		}
		else {
			LOG.error("Payee Type is not valid.");
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
    }

}

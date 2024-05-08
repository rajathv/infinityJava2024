package com.temenos.dbx.party.resource.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonObject;
import com.infinity.dbx.dbp.jwt.auth.AuthConstants;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.URLFinder;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.objectserviceutils.EventsDispatcher;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.registry.AppRegistryException;
import com.temenos.dbx.party.businessdelegate.api.DueDiligenceBusinessDelegate;
import com.temenos.dbx.party.resource.api.DueDiligenceResource;
import com.temenos.dbx.party.utils.PartyPropertyConstants;
import com.temenos.dbx.party.utils.PartyURLFinder;
import com.temenos.dbx.party.utils.PartyUtils;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.utils.DTOConstants;
import com.temenos.dbx.product.utils.InfinityConstants;

public class DueDiligenceResourceImpl implements DueDiligenceResource {

	/**
	 * A Logger object is used to log messages for a specific system or application
	 * component
	 */
	private LoggerUtil logger = new LoggerUtil(DueDiligenceResourceImpl.class);

	@SuppressWarnings({ "unchecked" })
	@Override
	/**
	 * @param methodID   : Represents method name of the Integration service.
	 * @param inputArray : Array of request params(payload) which has below
	 *                   information. Headers which have classname, prepostprocessor
	 *                   names and session ID, etc., payload object serviceExecTime
	 *                   parameters.
	 * @param dcRequest  : The dataControllerRequest that is being sent from
	 *                   Middle-ware.
	 * @param dcResponse : Response object.
	 * @return :List single Result object.
	 * @throws Exception
	 */
	public Result updateCitizenship(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {
			Map<String, Object> inputParams = (Map<String, Object>) inputArray[1];

			String[] idFields = getCustomerIDPartyID(inputParams, dcRequest);
			String customerID = idFields[0];
			String partyID = idFields[1];

			if (StringUtils.isEmpty(customerID) && StringUtils.isEmpty(partyID)) {
				logger.debug("Customer ID is empty : customerID -> " + customerID);
				ErrorCodeEnum.ERR_10209.setErrorCode(result);
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
				return result;
			}
			if (StringUtils.isEmpty(partyID)) {
				partyID = getPartyID(customerID, result, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(partyID)) {
					logger.debug("PartyID information is not available : customerID -> " + customerID + " : partyID -> "
							+ partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			} else {
				customerID = getCustomerID(result, partyID, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(customerID)) {
					logger.debug("customerID information is not available : customerID -> " + customerID
							+ " : partyID -> " + partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			}
			logger.debug("partyID -> " + partyID + " is found for customerID -> " + customerID);

			JSONArray citizenshipJsonArray = null;
			if (inputParams.containsKey(DTOConstants.CITIZENSHIP)
					&& inputParams.get(DTOConstants.CITIZENSHIP) != null) {
				citizenshipJsonArray = new JSONArray(inputParams.get(DTOConstants.CITIZENSHIP).toString());
			}
			JSONArray residenceJsonArray = null;
			if (inputParams.containsKey(DTOConstants.RESIDENCE) && inputParams.get(DTOConstants.RESIDENCE) != null) {
				residenceJsonArray = new JSONArray(inputParams.get(DTOConstants.RESIDENCE).toString());
			}

			DueDiligenceBusinessDelegate dueDiligenceBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(DueDiligenceBusinessDelegate.class);

			DBXResult response = new DBXResult();

			Map<String, Object> headers = dcRequest.getHeaderMap();
			headers = HelperMethods.addMSJWTAuthHeader(dcRequest, headers, AuthConstants.PRE_LOGIN_FLOW);
			response = dueDiligenceBusinessDelegate.updateCitizenship(citizenshipJsonArray, residenceJsonArray, partyID,
					headers);

			String id = (String) response.getResponse();
			if (StringUtils.isNotBlank(id)) {
				result.addParam("success", "success");
				result.addParam(DTOConstants.PARTYID, partyID);
				logger.debug("Party update is successful for ID -> " + partyID);
				if (StringUtils.isNotBlank(customerID)) {
					result.addParam(DTOConstants.ID, customerID);
				}
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, true, DTOConstants.PARTY);
				return result;
			}
			result.addParam(DTOConstants.BACKEND_ERR_CODE, response.getDbpErrCode());
			result.addParam(DTOConstants.BACKEND_ERR_MESSAGE, response.getDbpErrMsg());
			logger.debug("Party update is failed for ID -> " + partyID);
			ErrorCodeEnum.ERR_10213.setErrorCode(result);
			makeAuditEntry(inputArray, dcRequest, result, dcResponse, true, DTOConstants.PARTY);
		} catch (Exception e) {
			logger.error("Error in UpdateDueDiligenceResourceImpl-updateCitizenship Method : " + e.toString());
		}

		return result;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	/**
	 * @param methodID   : Represents method name of the Integration service.
	 * @param inputArray : Array of request params(payload) which has below
	 *                   information. Headers which have classname, prepostprocessor
	 *                   names and session ID, etc., payload object serviceExecTime
	 *                   parameters.
	 * @param dcRequest  : The dataControllerRequest that is being sent from
	 *                   Middle-ware.
	 * @param dcResponse : Response object.
	 * @return :List single Result object.
	 * @throws Exception
	 */
	public Result getDueDiligenceDetails(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {
			Map<String, Object> inputParams = (Map<String, Object>) inputArray[1];

			String[] idFields = getCustomerIDPartyID(inputParams, dcRequest);
			String customerID = idFields[0];
			String partyID = idFields[1];
			if (StringUtils.isEmpty(customerID) && StringUtils.isEmpty(partyID)) {
				logger.debug("Customer ID is empty : customerID -> " + customerID);
				ErrorCodeEnum.ERR_10209.setErrorCode(result);
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
				return result;
			}
			if (StringUtils.isEmpty(partyID)) {
				partyID = getPartyID(customerID, result, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(partyID)) {
					logger.debug("PartyID information is not available : customerID -> " + customerID + " : partyID -> "
							+ partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			} else {
				customerID = getCustomerID(result, partyID, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(customerID)) {
					logger.debug("customerID information is not available : customerID -> " + customerID
							+ " : partyID -> " + partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			}
			logger.debug("partyID -> " + partyID + " is found for customerID -> " + customerID);

			DueDiligenceBusinessDelegate dueDiligenceBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(DueDiligenceBusinessDelegate.class);

			DBXResult dbxResult = new DBXResult();

			Map<String, Object> headers = dcRequest.getHeaderMap();
			headers = PartyUtils.addJWTAuthHeader(dcRequest, headers, AuthConstants.PRE_LOGIN_FLOW);

			dbxResult = dueDiligenceBusinessDelegate.getPartyDetails(partyID, headers);

			if (dbxResult.getResponse() != null) {
				result = JSONToResult.convert((String) dbxResult.getResponse());
			} else {
				HelperMethods.addError(result, dbxResult);
			}
		} catch (Exception e) {
			logger.error("Error in UpdateDueDiligenceResourceImpl-getDueDiligenceDetails Method : " + e.toString());
		}
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	/**
	 * @param methodID   : Represents method name of the Integration service.
	 * @param inputArray : Array of request params(payload) which has below
	 *                   information. Headers which have classname, prepostprocessor
	 *                   names and session ID, etc., payload object serviceExecTime
	 *                   parameters.
	 * @param dcRequest  : The dataControllerRequest that is being sent from
	 *                   Middle-ware.
	 * @param dcResponse : Response object.
	 * @return :List single Result object.
	 * @throws Exception
	 */
	public Result createTaxDetail(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {
			Map<String, Object> inputParams = (Map<String, Object>) inputArray[1];

			String[] idFields = getCustomerIDPartyID(inputParams, dcRequest);
			String customerID = idFields[0];
			String partyID = idFields[1];
			if (StringUtils.isEmpty(customerID) && StringUtils.isEmpty(partyID)) {
				logger.debug("Customer ID is empty : customerID -> " + customerID);
				ErrorCodeEnum.ERR_10209.setErrorCode(result);
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
				return result;
			}
			if (StringUtils.isEmpty(partyID)) {
				partyID = getPartyID(customerID, result, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(partyID)) {
					logger.debug("PartyID information is not available : customerID -> " + customerID + " : partyID -> "
							+ partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			} else {
				customerID = getCustomerID(result, partyID, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(customerID)) {
					logger.debug("customerID information is not available : customerID -> " + customerID
							+ " : partyID -> " + partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			}
			logger.debug("partyID -> " + partyID + " is found for csutomer ID -> " + customerID);

			JSONObject taxDetails = new JSONObject();
			if (inputParams.containsKey(DTOConstants.TAXDETAILS) && inputParams.get(DTOConstants.TAXDETAILS) != null) {
				JSONArray taxDetailsJsonArray = new JSONArray(inputParams.get(DTOConstants.TAXDETAILS).toString());
				taxDetails.put(DTOConstants.TAXDETAILS, taxDetailsJsonArray);

			}
			if (logger.isDebugModeEnabled())
				logger.debug("taxDetails build for input and request params : " + taxDetails.toString());

			DueDiligenceBusinessDelegate dueDiligenceBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(DueDiligenceBusinessDelegate.class);

			DBXResult response = new DBXResult();

			Map<String, Object> headers = dcRequest.getHeaderMap();
			headers = HelperMethods.addMSJWTAuthHeader(dcRequest, headers, AuthConstants.PRE_LOGIN_FLOW);

			response = dueDiligenceBusinessDelegate.createTaxDetails(taxDetails, partyID, headers);

			String id = (String) response.getResponse();
			if (StringUtils.isNotBlank(id)) {
				result.addParam("success", "success");
				result.addParam(DTOConstants.PARTYID, partyID);
				logger.debug("TaxDetails create is successful for ID -> " + partyID);
				if (StringUtils.isNotBlank(customerID)) {
					result.addParam(DTOConstants.ID, customerID);
				}
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, true, DTOConstants.PARTY);
				return result;
			}
			result.addParam(DTOConstants.BACKEND_ERR_CODE, response.getDbpErrCode());
			result.addParam(DTOConstants.BACKEND_ERR_MESSAGE, response.getDbpErrMsg());
			logger.debug("TaxDetails create failed for ID -> " + partyID);
			ErrorCodeEnum.ERR_10213.setErrorCode(result);
			makeAuditEntry(inputArray, dcRequest, result, dcResponse, true, DTOConstants.PARTY);
		} catch (Exception e) {
			logger.error("Error in UpdateDueDiligenceResourceImpl-createTaxDetail Method : " + e.toString());
		}

		return result;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	/**
	 * @param methodID   : Represents method name of the Integration service.
	 * @param inputArray : Array of request params(payload) which has below
	 *                   information. Headers which have classname, prepostprocessor
	 *                   names and session ID, etc., payload object serviceExecTime
	 *                   parameters.
	 * @param dcRequest  : The dataControllerRequest that is being sent from
	 *                   Middle-ware.
	 * @param dcResponse : Response object.
	 * @return :List single Result object.
	 * @throws Exception
	 */
	public Result updateTaxDetail(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {
			Map<String, Object> inputParams = (Map<String, Object>) inputArray[1];

			String[] idFields = getCustomerIDPartyID(inputParams, dcRequest);
			String customerID = idFields[0];
			String partyID = idFields[1];
			if (StringUtils.isEmpty(customerID) && StringUtils.isEmpty(partyID)) {
				logger.debug("Customer ID is empty : customerID -> " + customerID);
				ErrorCodeEnum.ERR_10209.setErrorCode(result);
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
				return result;
			}
			if (StringUtils.isEmpty(partyID)) {
				partyID = getPartyID(customerID, result, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(partyID)) {
					logger.debug("PartyID information is not available : customerID -> " + customerID + " : partyID -> "
							+ partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			} else {
				customerID = getCustomerID(result, partyID, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(customerID)) {
					logger.debug("customerID information is not available : customerID -> " + customerID
							+ " : partyID -> " + partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			}
			logger.debug("partyID -> " + partyID + " is found for csutomer ID -> " + customerID);

			JSONObject taxDetails = new JSONObject();
			if (inputParams.containsKey(DTOConstants.TAXDETAILS) && inputParams.get(DTOConstants.TAXDETAILS) != null) {
				JSONArray taxDetailsJsonArray = new JSONArray(inputParams.get(DTOConstants.TAXDETAILS).toString());
				taxDetails.put(DTOConstants.TAXDETAILS, taxDetailsJsonArray);

			}
			if (logger.isDebugModeEnabled())
				logger.debug("taxDetails build for input and request params : " + taxDetails.toString());

			DueDiligenceBusinessDelegate dueDiligenceBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(DueDiligenceBusinessDelegate.class);

			DBXResult response = new DBXResult();

			Map<String, Object> headers = dcRequest.getHeaderMap();
			headers = HelperMethods.addMSJWTAuthHeader(dcRequest, headers, AuthConstants.PRE_LOGIN_FLOW);

			response = dueDiligenceBusinessDelegate.updateTaxDetails(taxDetails, partyID, headers);

			String id = (String) response.getResponse();
			if (StringUtils.isNotBlank(id)) {
				result.addParam("success", "success");
				result.addParam(DTOConstants.PARTYID, partyID);
				logger.debug("TaxDetails update is successful for ID -> " + partyID);
				if (StringUtils.isNotBlank(customerID)) {
					result.addParam(DTOConstants.ID, customerID);
				}
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, true, DTOConstants.PARTY);
				return result;
			}
			result.addParam(DTOConstants.BACKEND_ERR_CODE, response.getDbpErrCode());
			result.addParam(DTOConstants.BACKEND_ERR_MESSAGE, response.getDbpErrMsg());
			logger.debug("TaxDetails update failed for ID -> " + partyID);
			ErrorCodeEnum.ERR_10213.setErrorCode(result);
			makeAuditEntry(inputArray, dcRequest, result, dcResponse, true, DTOConstants.PARTY);
		} catch (Exception e) {
			logger.error("Error in UpdateDueDiligenceResourceImpl-updateTaxDetail Method : " + e.toString());
		}

		return result;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	/**
	 * @param methodID   : Represents method name of the Integration service.
	 * @param inputArray : Array of request params(payload) which has below
	 *                   information. Headers which have classname, prepostprocessor
	 *                   names and session ID, etc., payload object serviceExecTime
	 *                   parameters.
	 * @param dcRequest  : The dataControllerRequest that is being sent from
	 *                   Middle-ware.
	 * @param dcResponse : Response object.
	 * @return :List single Result object.
	 * @throws Exception
	 */
	public Result getTaxDetails(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {
			Map<String, Object> inputParams = (Map<String, Object>) inputArray[1];

			String[] idFields = getCustomerIDPartyID(inputParams, dcRequest);
			String customerID = idFields[0];
			String partyID = idFields[1];
			if (StringUtils.isEmpty(customerID) && StringUtils.isEmpty(partyID)) {
				logger.debug("Customer ID is empty : customerID -> " + customerID);
				ErrorCodeEnum.ERR_10209.setErrorCode(result);
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
				return result;
			}
			if (StringUtils.isEmpty(partyID)) {
				partyID = getPartyID(customerID, result, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(partyID)) {
					logger.debug("PartyID information is not available : customerID -> " + customerID + " : partyID -> "
							+ partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			} else {
				customerID = getCustomerID(result, partyID, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(customerID)) {
					logger.debug("customerID information is not available : customerID -> " + customerID
							+ " : partyID -> " + partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			}
			logger.debug("partyID -> " + partyID + " is found for csutomer ID -> " + customerID);

			DueDiligenceBusinessDelegate dueDiligenceBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(DueDiligenceBusinessDelegate.class);

			DBXResult dbxResult = new DBXResult();

			Map<String, Object> headers = dcRequest.getHeaderMap();
			headers = HelperMethods.addMSJWTAuthHeader(dcRequest, headers, AuthConstants.PRE_LOGIN_FLOW);

			dbxResult = dueDiligenceBusinessDelegate.getTaxDetails(partyID, headers);

			if (dbxResult.getResponse() != null) {
				result = JSONToResult.convert((String) dbxResult.getResponse());
			} else {
				HelperMethods.addError(result, dbxResult);
			}
		} catch (Exception e) {
			logger.error("Error in UpdateDueDiligenceResourceImpl-getTaxDetails Method : " + e.toString());
		}
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	/**
	 * @param methodID   : Represents method name of the Integration service.
	 * @param inputArray : Array of request params(payload) which has below
	 *                   information. Headers which have classname, prepostprocessor
	 *                   names and session ID, etc., payload object serviceExecTime
	 *                   parameters.
	 * @param dcRequest  : The dataControllerRequest that is being sent from
	 *                   Middle-ware.
	 * @param dcResponse : Response object.
	 * @return :List single Result object.
	 * @throws Exception
	 */
	public Result createEmploymentDetails(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {
			Map<String, Object> inputParams = (Map<String, Object>) inputArray[1];

			String[] idFields = getCustomerIDPartyID(inputParams, dcRequest);
			String customerID = idFields[0];
			String partyID = idFields[1];
			if (StringUtils.isEmpty(customerID) && StringUtils.isEmpty(partyID)) {
				logger.debug("Customer ID is empty : customerID -> " + customerID);
				ErrorCodeEnum.ERR_10209.setErrorCode(result);
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
				return result;
			}
			if (StringUtils.isEmpty(partyID)) {
				partyID = getPartyID(customerID, result, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(partyID)) {
					logger.debug("PartyID information is not available : customerID -> " + customerID + " : partyID -> "
							+ partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			} else {
				customerID = getCustomerID(result, partyID, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(customerID)) {
					logger.debug("customerID information is not available : customerID -> " + customerID
							+ " : partyID -> " + partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			}
			logger.debug("partyID -> " + partyID + " is found for csutomer ID -> " + customerID);

			JSONObject employmentDetails = new JSONObject();
			JSONObject addressDetails = new JSONObject();
			if (inputParams.containsKey(DTOConstants.EMPLOYMENTS)
					&& inputParams.get(DTOConstants.EMPLOYMENTS) != null) {
				JSONArray employmentDetailsJsonArray = new JSONArray(
						inputParams.get(DTOConstants.EMPLOYMENTS).toString());
				if (employmentDetailsJsonArray.getJSONObject(0).has("extensionData")) {
					JSONObject extensionDataObject = employmentDetailsJsonArray.getJSONObject(0)
							.getJSONArray("extensionData").getJSONObject(0);
					employmentDetailsJsonArray.getJSONObject(0).remove("extensionData");
					employmentDetailsJsonArray.getJSONObject(0).put("extensionData", extensionDataObject);
				}
				employmentDetails.put(DTOConstants.EMPLOYMENTS, employmentDetailsJsonArray);
			}
			if (inputParams.containsKey(DTOConstants.OCCUPATIONS)
					&& inputParams.get(DTOConstants.OCCUPATIONS) != null) {
				JSONArray occupationDetailsJsonArray = new JSONArray(
						inputParams.get(DTOConstants.OCCUPATIONS).toString());
				employmentDetails.put(DTOConstants.OCCUPATIONS, occupationDetailsJsonArray);
			}
			if (inputParams.containsKey(DTOConstants.PARTY_ADDRESS)
					&& inputParams.get(DTOConstants.PARTY_ADDRESS) != null) {
				JSONArray addressDetailsJsonArray = new JSONArray(
						inputParams.get(DTOConstants.PARTY_ADDRESS).toString());
				addressDetails.put(DTOConstants.PARTY_ADDRESS, addressDetailsJsonArray);
			}
			if (logger.isDebugModeEnabled())
				logger.debug("EmploymentDetails build for input and request params : " + employmentDetails.toString());

			DueDiligenceBusinessDelegate dueDiligenceBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(DueDiligenceBusinessDelegate.class);

			DBXResult response = new DBXResult();

			Map<String, Object> headers = dcRequest.getHeaderMap();
			headers = HelperMethods.addMSJWTAuthHeader(dcRequest, headers, AuthConstants.PRE_LOGIN_FLOW);

			response = dueDiligenceBusinessDelegate.createEmploymentDetails(employmentDetails, addressDetails, partyID,
					headers);

			String id = (String) response.getResponse();
			if (StringUtils.isNotBlank(id)) {
				result.addParam("success", "success");
				result.addParam(DTOConstants.PARTYID, partyID);
				logger.debug("EmploymentDetails create is successful for ID -> " + partyID);
				if (StringUtils.isNotBlank(customerID)) {
					result.addParam(DTOConstants.ID, customerID);
				}
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, true, DTOConstants.PARTY);
				return result;
			}
			result.addParam(DTOConstants.BACKEND_ERR_CODE, response.getDbpErrCode());
			result.addParam(DTOConstants.BACKEND_ERR_MESSAGE, response.getDbpErrMsg());
			logger.debug("EmploymentDetails create failed for ID -> " + partyID);
			ErrorCodeEnum.ERR_10213.setErrorCode(result);
			makeAuditEntry(inputArray, dcRequest, result, dcResponse, true, DTOConstants.PARTY);
		} catch (Exception e) {
			logger.error("Error in UpdateDueDiligenceResourceImpl-createEmploymentDetails Method : " + e.toString());
		}

		return result;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	/**
	 * @param methodID   : Represents method name of the Integration service.
	 * @param inputArray : Array of request params(payload) which has below
	 *                   information. Headers which have classname, prepostprocessor
	 *                   names and session ID, etc., payload object serviceExecTime
	 *                   parameters.
	 * @param dcRequest  : The dataControllerRequest that is being sent from
	 *                   Middle-ware.
	 * @param dcResponse : Response object.
	 * @return :List single Result object.
	 * @throws Exception
	 */
	public Result updateEmploymentDetails(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {
			Map<String, Object> inputParams = (Map<String, Object>) inputArray[1];

			String[] idFields = getCustomerIDPartyID(inputParams, dcRequest);
			String customerID = idFields[0];
			String partyID = idFields[1];
			if (StringUtils.isEmpty(customerID) && StringUtils.isEmpty(partyID)) {
				logger.debug("Customer ID is empty : customerID -> " + customerID);
				ErrorCodeEnum.ERR_10209.setErrorCode(result);
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
				return result;
			}
			if (StringUtils.isEmpty(partyID)) {
				partyID = getPartyID(customerID, result, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(partyID)) {
					logger.debug("PartyID information is not available : customerID -> " + customerID + " : partyID -> "
							+ partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			} else {
				customerID = getCustomerID(result, partyID, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(customerID)) {
					logger.debug("customerID information is not available : customerID -> " + customerID
							+ " : partyID -> " + partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			}
			logger.debug("partyID -> " + partyID + " is found for csutomer ID -> " + customerID);

			JSONObject employmentDetails = new JSONObject();
			JSONObject addressDetails = new JSONObject();
			if (inputParams.containsKey(DTOConstants.EMPLOYMENTS)
					&& inputParams.get(DTOConstants.EMPLOYMENTS) != null) {
				JSONArray employmentDetailsJsonArray = new JSONArray(
						inputParams.get(DTOConstants.EMPLOYMENTS).toString());
				if (employmentDetailsJsonArray.getJSONObject(0).has("extensionData")) {
					JSONObject extensionDataObject = employmentDetailsJsonArray.getJSONObject(0)
							.getJSONArray("extensionData").getJSONObject(0);
					employmentDetailsJsonArray.getJSONObject(0).remove("extensionData");
					employmentDetailsJsonArray.getJSONObject(0).put("extensionData", extensionDataObject);
				}
				employmentDetails.put(DTOConstants.EMPLOYMENTS, employmentDetailsJsonArray);
			}
			if (inputParams.containsKey(DTOConstants.OCCUPATIONS)
					&& inputParams.get(DTOConstants.OCCUPATIONS) != null) {
				JSONArray taxDetailsJsonArray = new JSONArray(inputParams.get(DTOConstants.OCCUPATIONS).toString());
				employmentDetails.put(DTOConstants.OCCUPATIONS, taxDetailsJsonArray);
			}
			if (inputParams.containsKey(DTOConstants.PARTY_ADDRESS)
					&& inputParams.get(DTOConstants.PARTY_ADDRESS) != null) {
				JSONArray addressDetailsJsonArray = new JSONArray(
						inputParams.get(DTOConstants.PARTY_ADDRESS).toString());
				addressDetails.put(DTOConstants.PARTY_ADDRESS, addressDetailsJsonArray);
			}
			if (logger.isDebugModeEnabled())
				logger.debug("EmploymentDetails build for input and request params : " + employmentDetails.toString());

			DueDiligenceBusinessDelegate dueDiligenceBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(DueDiligenceBusinessDelegate.class);

			DBXResult response = new DBXResult();

			Map<String, Object> headers = dcRequest.getHeaderMap();
			headers = HelperMethods.addMSJWTAuthHeader(dcRequest, headers, AuthConstants.PRE_LOGIN_FLOW);

			response = dueDiligenceBusinessDelegate.updateEmploymentDetails(employmentDetails, addressDetails, partyID,
					headers);

			String id = (String) response.getResponse();
			if (StringUtils.isNotBlank(id)) {
				result.addParam("success", "success");
				result.addParam(DTOConstants.PARTYID, partyID);
				logger.debug("EmploymentDetails create is successful for ID -> " + partyID);
				if (StringUtils.isNotBlank(customerID)) {
					result.addParam(DTOConstants.ID, customerID);
				}
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, true, DTOConstants.PARTY);
				return result;
			}
			result.addParam(DTOConstants.BACKEND_ERR_CODE, response.getDbpErrCode());
			result.addParam(DTOConstants.BACKEND_ERR_MESSAGE, response.getDbpErrMsg());
			logger.debug("EmploymentDetails create failed for ID -> " + partyID);
			ErrorCodeEnum.ERR_10213.setErrorCode(result);
			makeAuditEntry(inputArray, dcRequest, result, dcResponse, true, DTOConstants.PARTY);
		} catch (Exception e) {
			logger.error("Error in UpdateDueDiligenceResourceImpl-updateEmploymentDetails Method : " + e.toString());
		}

		return result;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	/**
	 * @param methodID   : Represents method name of the Integration service.
	 * @param inputArray : Array of request params(payload) which has below
	 *                   information. Headers which have classname, prepostprocessor
	 *                   names and session ID, etc., payload object serviceExecTime
	 *                   parameters.
	 * @param dcRequest  : The dataControllerRequest that is being sent from
	 *                   Middle-ware.
	 * @param dcResponse : Response object.
	 * @return :List single Result object.
	 * @throws Exception
	 */
	public Result getEmploymentDetails(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {
			Map<String, Object> inputParams = (Map<String, Object>) inputArray[1];

			String[] idFields = getCustomerIDPartyID(inputParams, dcRequest);
			String customerID = idFields[0];
			String partyID = idFields[1];
			if (StringUtils.isEmpty(customerID) && StringUtils.isEmpty(partyID)) {
				logger.debug("Customer ID is empty : customerID -> " + customerID);
				ErrorCodeEnum.ERR_10209.setErrorCode(result);
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
				return result;
			}
			if (StringUtils.isEmpty(partyID)) {
				partyID = getPartyID(customerID, result, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(partyID)) {
					logger.debug("PartyID information is not available : customerID -> " + customerID + " : partyID -> "
							+ partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			} else {
				customerID = getCustomerID(result, partyID, dcRequest);
				result = new Result();
				if (StringUtils.isEmpty(customerID)) {
					logger.debug("customerID information is not available : customerID -> " + customerID
							+ " : partyID -> " + partyID);
					ErrorCodeEnum.ERR_10209.setErrorCode(result);
					makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
					return result;
				}
			}
			logger.debug("partyID -> " + partyID + " is found for csutomer ID -> " + customerID);

			DueDiligenceBusinessDelegate dueDiligenceBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(DueDiligenceBusinessDelegate.class);

			DBXResult dbxResult = new DBXResult();

			Map<String, Object> headers = dcRequest.getHeaderMap();
			headers = PartyUtils.addJWTAuthHeader(dcRequest, headers, AuthConstants.PRE_LOGIN_FLOW);

			dbxResult = dueDiligenceBusinessDelegate.getEmploymentDetails(partyID, headers);

			if (dbxResult.getResponse() != null) {
				result = JSONToResult.convert((String) dbxResult.getResponse());
			} else {
				HelperMethods.addError(result, dbxResult);
			}
		} catch (Exception e) {
			logger.error("Error in UpdateDueDiligenceResourceImpl-getEmploymentDetails Method : " + e.toString());
		}
		return result;
	}

	private void makeAuditEntry(Object[] inputArray, DataControllerRequest dcRequest, Result result,
			DataControllerResponse dcResponse, boolean isSuccess, String eventType) {
		try {

			JsonObject customParams = getJsonFromInput(HelperMethods.getInputParamMap(inputArray), dcRequest);

			String operation = null;
			try {
				operation = dcRequest.getServicesManager().getOperationData().getOperationId();
			} catch (AppRegistryException e) {
				logger.error("Error while getting Operation from service Manager", e);
			}
			String eventSubType = "";
			if (operation.toLowerCase().contains("create")) {
				eventSubType = "CREATE";
			} else {
				eventSubType = "UPDATE";
			}

			if (StringUtils.isNotBlank(result.getParamValueByName(DTOConstants.ID))) {
				customParams.addProperty("customerId", result.getParamValueByName(DTOConstants.ID));
			}

			if (StringUtils.isNotBlank(result.getParamValueByName(DTOConstants.PARTYID))) {
				customParams.addProperty(DTOConstants.PARTYID, result.getParamValueByName(DTOConstants.PARTYID));
			}

			if (StringUtils.isNotBlank(result.getParamValueByName(DTOConstants.CORECUSTOMERID))) {
				customParams.addProperty(DTOConstants.CORECUSTOMERID,
						result.getParamValueByName(DTOConstants.CORECUSTOMERID));
			}

			String StatusId;
			if (isSuccess) {
				StatusId = "SID_EVENT_SUCCESS";
			} else {
				StatusId = "SID_EVENT_FAILURE";
			}

			EventsDispatcher.dispatch(dcRequest, dcResponse, eventType, eventSubType,
					DueDiligenceResourceImpl.class.getName(), StatusId, null, null, customParams);

		} catch (Exception ex) {
			logger.error("Error in UpdateDueDiligenceResourceImpl-makeAuditEntry Method : " + ex.toString());
		}
	}

	private JsonObject getJsonFromInput(Map<String, String> inputParamMap, DataControllerRequest dcRequest) {

		JsonObject jsonObject = new JsonObject();
		for (String key : inputParamMap.keySet()) {
			if (StringUtils.isNotBlank(inputParamMap.get(key))) {
				jsonObject.addProperty(key, inputParamMap.get(key));
			}
		}
		Iterator<String> iterator = dcRequest.getParameterNames();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (StringUtils.isNotBlank(dcRequest.getParameter(key))) {
				jsonObject.addProperty(key, dcRequest.getParameter(key));
			}
		}
		return jsonObject;
	}

	private String[] getCustomerIDPartyID(Map<String, Object> inputParams, DataControllerRequest dcRequest) {
		String[] cust_PartyID = new String[2];
		if (inputParams.containsKey(DTOConstants.ID) && inputParams.get(DTOConstants.ID) != null)
			cust_PartyID[0] = inputParams.get(DTOConstants.ID).toString();
		if (StringUtils.isBlank(cust_PartyID[0])) {
			cust_PartyID[0] = dcRequest.getParameter(DTOConstants.ID);
		}
		if (inputParams.containsKey(DTOConstants.PARTYID) && inputParams.get(DTOConstants.PARTYID) != null)
			cust_PartyID[1] = inputParams.get(DTOConstants.PARTYID).toString();

		if (StringUtils.isBlank(cust_PartyID[1])) {
			cust_PartyID[1] = dcRequest.getParameter(DTOConstants.PARTYID);
		}
		return cust_PartyID;
	}

	private String getPartyID(String customerID, Result result, DataControllerRequest dcRequest) {
		String filter = "Customer_id" + DBPUtilitiesConstants.EQUAL + customerID + DBPUtilitiesConstants.AND
				+ DTOConstants.BACKENDTYPE + DBPUtilitiesConstants.EQUAL + DTOConstants.PARTY;
		try {
			result = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
					URLConstants.BACKENDIDENTIFIER_GET);
		} catch (HttpCallException e) {
			logger.error("Error in UpdateDueDiligenceResourceImpl-getPartyID Method : " + e.toString());
		}
		String partyID = "";
		if (HelperMethods.hasRecords(result)) {
			partyID = HelperMethods.getFieldValue(result, DTOConstants.BACKENDID);
		}
		return partyID;
	}

	private String getCustomerID(Result result, String partyID, DataControllerRequest dcRequest) {
		String filter = DTOConstants.BACKENDID + DBPUtilitiesConstants.EQUAL + partyID + DBPUtilitiesConstants.AND
				+ DTOConstants.BACKENDTYPE + DBPUtilitiesConstants.EQUAL + DTOConstants.PARTY;
		String customerID = "";
		try {
			result = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
					URLConstants.BACKENDIDENTIFIER_GET);
		} catch (HttpCallException e) {
			logger.error("Error in UpdateDueDiligenceResourceImpl-getCustomerID Method : " + e.toString());
		}
		if (HelperMethods.hasRecords(result)) {
			customerID = HelperMethods.getFieldValue(result, "Customer_id");
		}
		return customerID;
	}
	
	public Result createDueDiligenceOperation(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse, boolean isUpdate) {
		Result result = new Result();
		try {
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			String partyID = inputParams.get(DTOConstants.PARTYID);
			if (StringUtils.isBlank(partyID)) {
				partyID = dcRequest.getParameter(DTOConstants.PARTYID);
			}

			if (StringUtils.isEmpty(partyID)) {
				logger.debug("PartyID information is not available : partyID -> "
						+ partyID);
				ErrorCodeEnum.ERR_10209.setErrorCode(result);
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, false, DTOConstants.PARTY);
				return result;
			}
			
			String userType = inputParams.get("userType");
			if(!StringUtils.equalsIgnoreCase(userType, "Company")) {
				result.addParam("success", "success");
				result.addParam(DTOConstants.PARTYID, partyID);
				logger.debug("Duediligence create is skipped for Usertype :" +userType);
				return result;
			}

			DueDiligenceBusinessDelegate dueDiligenceBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(DueDiligenceBusinessDelegate.class);
			Map<String, Object> headers = dcRequest.getHeaderMap();
			headers = HelperMethods.addMSJWTAuthHeader(dcRequest, headers, AuthConstants.PRE_LOGIN_FLOW);
			
			JSONObject registrationDetails = new JSONObject();
			if (inputParams.containsKey("registrationDetails") && inputParams.get("registrationDetails") != null) {
				JSONArray registrationDetailsJsonArray = new JSONArray()
						.put(new JSONObject(inputParams.get("registrationDetails").toString()));
				registrationDetails.put("registrationDetails", registrationDetailsJsonArray);
			}
			
			JSONObject assetLiabiltyDetails = new JSONObject();
			boolean isAssetLiabilityUpdate = false;
			if (inputParams.containsKey("assetLiabiltyDetails") && inputParams.get("assetLiabiltyDetails") != null) {
				JSONObject dataObj = new JSONObject(inputParams.get("assetLiabiltyDetails").toString());
				String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				dataObj.put("type", "Total Shareholders Equity");
				dataObj.put("asOfDate", date);
				JSONArray financialInformationJsonArray = new JSONArray().put(dataObj);
				assetLiabiltyDetails.put("assetLiabDetails", financialInformationJsonArray);
				//For update case
				if (isUpdate) {
					JSONArray assetLiabArray = dueDiligenceBusinessDelegate.getDueDiligenceData(PartyURLFinder
							.getServiceUrl(PartyPropertyConstants.DUE_DILIGENCE_ASSETLIABDETAILS, partyID), headers, "assetLiabDetails");
					for (int i = 0; i < assetLiabArray.length(); i++) {
						JSONObject obj = assetLiabArray.getJSONObject(i);
						if (obj.getString("asOfDate").equals(date)) {
							isAssetLiabilityUpdate = true;
							break;
						}
					}
				}
			}

			JSONObject financialInformation = new JSONObject();
			boolean isFinancialInformationUpdate = false;
			if (inputParams.containsKey("financialInformation") && inputParams.get("financialInformation") != null) {
				String year = new SimpleDateFormat("yyyy").format(new Date());
				JSONObject dataObj = new JSONObject(inputParams.get("financialInformation").toString());
				dataObj.put("financialType", "Total Revenue");
				dataObj.put("realisedInYear", new SimpleDateFormat("yyyy").format(new Date()));
				JSONArray financialInformationJsonArray = new JSONArray().put(dataObj);
				financialInformation.put("financialInformations", financialInformationJsonArray);
				//For update case
				if (isUpdate) {
					JSONArray financialInfoArray = dueDiligenceBusinessDelegate.getDueDiligenceData(PartyURLFinder
							.getServiceUrl(PartyPropertyConstants.DUE_DILIGENCE_FINANCIALINFORMATIONS, partyID), headers, "financialInformations");
					for (int i = 0; i < financialInfoArray.length(); i++) {
						JSONObject obj = financialInfoArray.getJSONObject(i);
						if (obj.get("realisedInYear").toString().equals(year)) {
							isFinancialInformationUpdate = true;
							break;
						}
					}
				}
			}

			DBXResult response = new DBXResult();

			if (registrationDetails != null)
				response = dueDiligenceBusinessDelegate.createRegistrationDetails(registrationDetails, partyID, headers,
						isUpdate);

			if (financialInformation != null)
				response = dueDiligenceBusinessDelegate.createFinancialInformationDetails(financialInformation, partyID,
						headers, isFinancialInformationUpdate);

			if (assetLiabiltyDetails != null)
				response = dueDiligenceBusinessDelegate.createAssetLiabiltyDetails(assetLiabiltyDetails, partyID,
						headers, isAssetLiabilityUpdate);

			String id = (String) response.getResponse();
			if (StringUtils.isNotBlank(id)) {
				result.addParam("success", "success");
				result.addParam(DTOConstants.PARTYID, partyID);
				logger.debug("Duediligence create is successful for ID -> " + partyID);
				makeAuditEntry(inputArray, dcRequest, result, dcResponse, true, DTOConstants.PARTY);
				return result;
			}
			result.addParam(DTOConstants.BACKEND_ERR_CODE, response.getDbpErrCode());
			result.addParam(DTOConstants.BACKEND_ERR_MESSAGE, response.getDbpErrMsg());
			logger.debug("Duediligence create failed for ID -> " + partyID);
			ErrorCodeEnum.ERR_10213.setErrorCode(result);
			makeAuditEntry(inputArray, dcRequest, result, dcResponse, true, DTOConstants.PARTY);
		} catch (Exception e) {
			logger.error("Exception", e);
			logger.error("Error in UpdateDueDiligenceResourceImpl-createDuediligence Method : " + e.toString());
		}

		return result;
	}

	@Override
	public Result updateAssetLiablities(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			String customerId = inputParams.get(InfinityConstants.id);
			if (StringUtils.isBlank(customerId)) {
				ErrorCodeEnum.ERR_10212.setErrorCode(result, "CustomerId is empty");
				return result;
			}
			
			String partyId = fetchPartyId(dcRequest, customerId);

			if (StringUtils.isBlank(partyId)) {
				ErrorCodeEnum.ERR_10212.setErrorCode(result, "PartyId is empty");
				return result;
			}
			
			String type = inputParams.get("type");
			String currency = inputParams.get("currency");
			String amount = inputParams.get("amount");
			DueDiligenceBusinessDelegate dueDiligenceBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(DueDiligenceBusinessDelegate.class);
			Map<String, Object> headers = dcRequest.getHeaderMap();
			headers = HelperMethods.addMSJWTAuthHeader(dcRequest, headers, AuthConstants.PRE_LOGIN_FLOW);
			
			//Get existing asset liabilities
			JSONArray assetLiabArray = dueDiligenceBusinessDelegate.getDueDiligenceData(PartyURLFinder
					.getServiceUrl(PartyPropertyConstants.DUE_DILIGENCE_ASSETLIABDETAILS, partyId), headers, "assetLiabDetails");
			boolean recordExists = StreamSupport
					.stream(assetLiabArray.spliterator(), true).map(item -> (JSONObject) item)
					.filter(item -> StringUtils.equals(item.optString("type"), type))
					.filter(item -> StringUtils
							.equals(item.optString("asOfDate"), new SimpleDateFormat("yyyy-MM-dd").format(new Date())))
					.count() > 0;
			
			JSONObject assetLiability = new JSONObject();
			assetLiability.put("type", type);
			assetLiability.put("asOfDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			assetLiability.put("currency", currency);
			assetLiability.put("amount", amount);
			JSONObject assetLiabiltyDetails = new JSONObject();
			assetLiabiltyDetails.put("assetLiabDetails", new JSONArray().put(assetLiability));
			DBXResult dbxRes = dueDiligenceBusinessDelegate.createAssetLiabiltyDetails(assetLiabiltyDetails, partyId, headers, recordExists);
			if(StringUtils.isNotBlank(dbxRes.getDbpErrMsg())) {
				ErrorCodeEnum.ERR_10213.setErrorCode(result);
				return result;
			}
			result.addParam("success", "success");
			result.addParam(DTOConstants.PARTYID, partyId);
			return result;
		} catch (Exception e) {
			ErrorCodeEnum.ERR_10213.setErrorCode(result);
		}
		return result;
	}
	
	@Override
	public Result updateFundsSource(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			String customerId = inputParams.get(InfinityConstants.id);
			if (StringUtils.isBlank(customerId)) {
				ErrorCodeEnum.ERR_10212.setErrorCode(result, "CustomerId is empty");
				return result;
			}
			
			String partyId = fetchPartyId(dcRequest, customerId);

			if (StringUtils.isBlank(partyId)) {
				ErrorCodeEnum.ERR_10212.setErrorCode(result, "PartyId is empty");
				return result;
			}
			String type = inputParams.get("type");
			String source = inputParams.get("source");
			String currency = inputParams.get("currency");
			String amount = inputParams.get("amount");
			String fundFrequency = inputParams.get("fundFrequency");
			DueDiligenceBusinessDelegate dueDiligenceBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(DueDiligenceBusinessDelegate.class);
			Map<String, Object> headers = dcRequest.getHeaderMap();
			headers = HelperMethods.addMSJWTAuthHeader(dcRequest, headers, AuthConstants.PRE_LOGIN_FLOW);
			
			//Get existing asset liabilities
			JSONArray fundsSourceArray = dueDiligenceBusinessDelegate.getDueDiligenceData(PartyURLFinder
					.getServiceUrl(PartyPropertyConstants.DUE_DILIGENCE_SOURCEOFFUNDS, partyId), headers, "sourceOfFunds");
			Optional<JSONObject> existingFundSource = StreamSupport
					.stream(fundsSourceArray.spliterator(), true).map(item -> (JSONObject) item)
					.filter(item -> StringUtils.equals(item.optString("fundsType"), type))
					.findFirst();
			
			JSONObject fundsSource = new JSONObject();
			fundsSource.put("fundsType", type);
			fundsSource.put("declarationDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			fundsSource.put("fundsCurrency", currency);
			fundsSource.put("fundsAmount", amount);
			fundsSource.put("fundsFrequency", fundFrequency);
			fundsSource.put("fundsSource", source);
			
			boolean isUpdate = false;
			if(existingFundSource.isPresent()) {
				fundsSource.put("sourceOfFundsReference", existingFundSource.get().optString("sourceOfFundsReference"));
				isUpdate = true;
			}
			JSONObject fundsSources = new JSONObject();
			fundsSources.put("sourceOfFunds", new JSONArray().put(fundsSource));
			DBXResult dbxRes = dueDiligenceBusinessDelegate.createFundsSource(fundsSources, partyId, headers, isUpdate);
			if(StringUtils.isNotBlank(dbxRes.getDbpErrMsg())) {
				ErrorCodeEnum.ERR_10213.setErrorCode(result);
				return result;
			}
			result.addParam("success", "success");
			result.addParam(DTOConstants.PARTYID, partyId);
			return result;
		} catch (Exception e) {
			ErrorCodeEnum.ERR_10213.setErrorCode(result);
		}
		return result;
	}
	
	private String fetchPartyId(DataControllerRequest dcRequest, String customerId) throws HttpCallException {
		String partyID = "";
		String filter = "Customer_id" + DBPUtilitiesConstants.EQUAL + customerId + DBPUtilitiesConstants.AND
				+ DTOConstants.BACKENDTYPE + DBPUtilitiesConstants.EQUAL + DTOConstants.PARTY;
		Result result = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
				URLConstants.BACKENDIDENTIFIER_GET);
		partyID = HelperMethods.getFieldValue(result, DTOConstants.BACKENDID);
		return partyID;
	}
	
	private String fetchCifId(DataControllerRequest dcRequest, String customerId) throws HttpCallException {
		String cifId = "";
		String filter = "Customer_id" + DBPUtilitiesConstants.EQUAL + customerId + DBPUtilitiesConstants.AND
				+ DTOConstants.BACKENDTYPE + DBPUtilitiesConstants.EQUAL + DTOConstants.T24;
		Result result = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
				URLConstants.BACKENDIDENTIFIER_GET);
		cifId = HelperMethods.getFieldValue(result, DTOConstants.BACKENDID);
		return cifId;
	}
	
	
	
	@Override
	public Result getFundsSource(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			String partyID = inputParams.get(DTOConstants.PARTY_ID);
			if (StringUtils.isBlank(partyID)) {
				ErrorCodeEnum.ERR_10212.setErrorCode(result, "partyId is empty");
				return result;
			}
			DueDiligenceBusinessDelegate dueDiligenceBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(DueDiligenceBusinessDelegate.class);
			Map<String, Object> headers = dcRequest.getHeaderMap();
			headers = HelperMethods.addMSJWTAuthHeader(dcRequest, headers, AuthConstants.PRE_LOGIN_FLOW);
			
			JSONArray fundsSource = dueDiligenceBusinessDelegate.getDueDiligenceData(PartyURLFinder
					.getServiceUrl(PartyPropertyConstants.DUE_DILIGENCE_SOURCEOFFUNDS, partyID), headers, "sourceOfFunds");
			JSONObject fundsSources = new JSONObject();
			fundsSources.put("sourceOfFunds", fundsSource);
			
			result = JSONToResult.convert(fundsSources.toString());
		}catch(Exception e) {
//			result.addParam("Error", e.getLocalizedMessage());
			ErrorCodeEnum.ERR_10213.setErrorCode(result);
		}
		return result;
	}
	
	@Override
	public Result getAssetLiablities(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			String partyID = inputParams.get(DTOConstants.PARTY_ID);
			if (StringUtils.isBlank(partyID)) {
				ErrorCodeEnum.ERR_10212.setErrorCode(result, "partyId is empty");
				return result;
			}
			DueDiligenceBusinessDelegate dueDiligenceBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(DueDiligenceBusinessDelegate.class);
			Map<String, Object> headers = dcRequest.getHeaderMap();
			headers = HelperMethods.addMSJWTAuthHeader(dcRequest, headers, AuthConstants.PRE_LOGIN_FLOW);
			
			JSONArray assetLiabArray = dueDiligenceBusinessDelegate.getDueDiligenceData(PartyURLFinder
					.getServiceUrl(PartyPropertyConstants.DUE_DILIGENCE_ASSETLIABDETAILS, partyID), headers, "assetLiabDetails");
			JSONObject fundsSources = new JSONObject();
			fundsSources.put("assetLiabDetails", assetLiabArray);
			
			result = JSONToResult.convert(fundsSources.toString());
		}catch(Exception e) {
//			result.addParam("ErrorMessage", e.getLocalizedMessage());
			ErrorCodeEnum.ERR_10213.setErrorCode(result);
		}
		return result;
	}
}

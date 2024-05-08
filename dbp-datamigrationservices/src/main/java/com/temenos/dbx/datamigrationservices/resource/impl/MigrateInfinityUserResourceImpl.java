package com.temenos.dbx.datamigrationservices.resource.impl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.core.Request;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.BackendDelegateFactory;
import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.constants.DBPConstants;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.BundleConfigurationHandler;
import com.kony.dbputilities.util.DBPDatasetConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.LegalEntityUtil;
import com.kony.dbputilities.util.MWConstants;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.eum.dbputilities.util.ServiceCallHelper;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.datamigrationservices.business.api.MigrateInfinityUserBusinessDelegate;
import com.temenos.dbx.datamigrationservices.resource.api.MigrateInfinityUserResource;
import com.temenos.dbx.datamigrationservices.utils.DataFetchUtils;
import com.temenos.dbx.datamigrationservices.utils.MigratePayeeCommonMethods;
import com.temenos.dbx.datamigrationservices.utils.MigrationUtils;
import com.temenos.dbx.eum.product.contract.backenddelegate.api.ContractBackendDelegate;
import com.temenos.dbx.eum.product.contract.businessdelegate.api.CoreCustomerBusinessDelegate;
import com.temenos.dbx.eum.product.contract.resource.api.ContractResource;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.api.InfinityUserManagementBackendDelegate;
import com.temenos.dbx.eum.product.usermanagement.businessdelegate.api.InfinityUserManagementBusinessDelegate;
import com.temenos.dbx.product.commons.dto.TransactionStatusDTO;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.FeatureActionLimitsDTO;
import com.temenos.dbx.product.dto.MembershipDTO;
import com.temenos.dbx.product.payeeservices.backenddelegate.api.InterBankPayeeBackendDelegate;
import com.temenos.dbx.product.payeeservices.backenddelegate.api.InternationalPayeeBackendDelegate;
import com.temenos.dbx.product.payeeservices.backenddelegate.api.IntraBankPayeeBackendDelegate;
import com.temenos.dbx.product.payeeservices.businessdelegate.api.InterBankPayeeBusinessDelegate;
import com.temenos.dbx.product.payeeservices.businessdelegate.api.InternationalPayeeBusinessDelegate;
import com.temenos.dbx.product.payeeservices.businessdelegate.api.IntraBankPayeeBusinessDelegate;
import com.temenos.dbx.product.payeeservices.constants.PayeeConstants;
import com.temenos.dbx.product.payeeservices.dto.InterBankPayeeBackendDTO;
import com.temenos.dbx.product.payeeservices.dto.InterBankPayeeDTO;
import com.temenos.dbx.product.payeeservices.dto.InternationalPayeeBackendDTO;
import com.temenos.dbx.product.payeeservices.dto.InternationalPayeeDTO;
import com.temenos.dbx.product.payeeservices.dto.IntraBankPayeeBackendDTO;
import com.temenos.dbx.product.payeeservices.dto.IntraBankPayeeDTO;
import com.temenos.dbx.product.payeeservices.resource.api.InterBankPayeeResource;
import com.temenos.dbx.product.payeeservices.resource.api.InternationalPayeeResource;
import com.temenos.dbx.product.payeeservices.resource.api.IntraBankPayeeResource;
import com.temenos.dbx.product.payeeservices.resource.impl.InterBankPayeeResourceImpl;
import com.temenos.dbx.product.payeeservices.resource.impl.InternationalPayeeResourceImpl;
import com.temenos.dbx.product.payeeservices.resource.impl.IntraBankPayeeResourceImpl;
import com.temenos.dbx.product.payeeservices.utils.PayeeUtils;
import com.temenos.dbx.product.signatorygroupservices.businessdelegate.api.SignatoryGroupBusinessDelegate;
import com.temenos.dbx.product.signatorygroupservices.dto.SignatoryGroupDTO;
import com.temenos.dbx.product.utils.DTOConstants;
import com.temenos.dbx.product.utils.InfinityConstants;

public class MigrateInfinityUserResourceImpl implements MigrateInfinityUserResource {

	LoggerUtil logger = new LoggerUtil(MigrateInfinityUserResourceImpl.class);

	@Override
	public Result createUser(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			Map<String, String> map = HelperMethods.getInputParamMap(inputArray);
			Iterator<String> iterator = request.getParameterNames();
			while (iterator.hasNext()) {
				String key = iterator.next();
				if ((!map.containsKey(key) || StringUtils.isBlank(map.get(key)))
						&& StringUtils.isNotBlank(request.getParameter(key))) {
					map.put(key, request.getParameter(key));
				}
			}
			String cif = map.get(InfinityConstants.cif);
			String legalEntityId = map.get(InfinityConstants.legalEntityId);
			String serviceDefId = map.get(InfinityConstants.serviceDefinitionId);
			String accounts = map.get(InfinityConstants.accounts);
			String roleId = map.get(InfinityConstants.roleId);
			String isDigitalProfileNeeded = map.get("isDigitalProfileNeeded");
			if (HelperMethods.isBlank(cif, legalEntityId, serviceDefId, accounts, roleId, isDigitalProfileNeeded)) {
				return ErrorCodeEnum.ERR_10001.setErrorCode(result);
			}

			return createContractAndUser(methodId, inputArray, map, request, response);
		} catch (Exception e) {
			logger.error("Exception occured while creating a contract " + e.getStackTrace());
			return ErrorCodeEnum.ERR_10001.setErrorCode(result);
		}

	}

	private Result createContractAndUser(String methodId, Object[] inputArray, Map<String, String> map,
			DataControllerRequest request, DataControllerResponse response) {

		Result result = new Result();
		try {
			String cif = map.get(InfinityConstants.cif);
			String serviceDefinitionId = map.get(InfinityConstants.serviceDefinitionId);
			boolean isDigitalProfileNeeded = Boolean.parseBoolean(map.get("isDigitalProfileNeeded"));
			getContractPayload(map, request, cif, serviceDefinitionId);

			ContractResource resource = DBPAPIAbstractFactoryImpl.getResource(ContractResource.class);
			try {
				result = resource.createContract(methodId, inputArray, request, response);
			} catch (ApplicationException e) {
				logger.error("Exception", e);
				e.setError(result);
			}

			if (!result.hasParamByName(InfinityConstants.contractId)
					|| StringUtils.isBlank(result.getParamValueByName(InfinityConstants.contractId))
					|| (!isDigitalProfileNeeded && !map.get(InfinityConstants.serviceType).equals("TYPE_ID_RETAIL"))) {
				return result;
			}

			String contractId = result.getParamValueByName(InfinityConstants.contractId);
			JsonObject jsonObject = createInfinityUserPayload(map, contractId, request.getHeaderMap(),
					 request, true);
			
			InfinityUserManagementBackendDelegate infinityUserManagementBackendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(InfinityUserManagementBackendDelegate.class);
			JsonObject jsonResult = (JsonObject) infinityUserManagementBackendDelegate
					.createInfinityUser(jsonObject, request.getHeaderMap()).getResponse();
			result = JSONToResult.convert(jsonResult.toString());
			String customerId = "";
			if (result.hasParamByName(InfinityConstants.id)
					&& StringUtils.isNotBlank(result.getParamValueByName(InfinityConstants.id))) {
				customerId = result.getParamValueByName(InfinityConstants.id);
				CustomerDTO customerDTO = new CustomerDTO();
				customerDTO = (CustomerDTO) customerDTO.loadDTO(customerId);
				if (customerDTO != null) {
					HashMap<String, Object> input = new HashMap<String, Object>();
					input.put(InfinityConstants.id, customerId);
					input.put(InfinityConstants.Status_id, HelperMethods.getCustomerStatus().get("NEW"));
					input.put(InfinityConstants.CustomerType_id, HelperMethods.getCustomerTypes().get("Retail"));
					customerDTO.setIsChanged(true);
					customerDTO.persist(input, request.getHeaderMap());

					map.put(InfinityConstants.userId, customerId);
					map.put(InfinityConstants.contractStatus, DBPUtilitiesConstants.CONTRACT_STATUS_ACTIVE);

					Result deliveryResult = generateInfinityUserActivationCodeAndUsername(map, request, response);
					result.addAllParams(deliveryResult.getAllParams());
					result.addStringParam("status", "success");
					result.addStringParam("contractId", contractId);
				}
			}
		} catch (ApplicationException e) {
			result = new Result();
			e.setError(result);
			logger.error("Exception occured while creating a contract " + e.getStackTrace());
			return result;
		} catch (Exception e) {
			logger.error("Exception occured while creating a contract " + e.getStackTrace());
        	return ErrorCodeEnum.ERR_10351.setErrorCode(result);
		}
		return result;

	}

	@Override
	public Object linkUserToContract(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		boolean createFlow = false;
		try {
			Map<String, String> map = HelperMethods.getInputParamMap(inputArray);
			Iterator<String> iterator = request.getParameterNames();
			while (iterator.hasNext()) {
				String key = iterator.next();
				if ((!map.containsKey(key) || StringUtils.isBlank(map.get(key)))
						&& StringUtils.isNotBlank(request.getParameter(key))) {
					map.put(key, request.getParameter(key));
				}
			}
			String legalEntityId = map.get(InfinityConstants.legalEntityId);
			String accounts = map.get(InfinityConstants.accounts);
			String roleId = map.get(InfinityConstants.roleId);
			String userId = map.get(InfinityConstants.userId);
			String contractId = map.get(InfinityConstants.contractId);
			String cif = map.get(InfinityConstants.cif);
			String contractCif = map.get("contractCif");
			
			if(StringUtils.isBlank(userId) && StringUtils.isBlank(contractCif)) {
				return ErrorCodeEnum.ERR_10001.setErrorCode(result);
			}
			
			if (HelperMethods.isBlank(legalEntityId, accounts, roleId)) {
				return ErrorCodeEnum.ERR_10001.setErrorCode(result);
			}			
			String customerId = getCustomerIdForCif(cif, legalEntityId,request);
			boolean isCifExists = StringUtils.isNotBlank(customerId);
			if(StringUtils.isBlank(userId) && !isCifExists) {
				Map<String, String> membershipDetails = getMembershipDetails(cif, legalEntityId, request);
				if(membershipDetails.isEmpty()) {
					return ErrorCodeEnum.ERR_10001.setErrorCode(result);
				}
				map.putAll(membershipDetails);
				map.put(InfinityConstants.coreCustomerId, cif);
				createFlow = true;		
			} else {
				if(StringUtils.isNotBlank(userId)) {
					CustomerDTO customerDTO = new CustomerDTO();
					customerDTO.setUserName(userId);
					customerDTO = (CustomerDTO) customerDTO.loadDTO();
					if(customerDTO == null) {
						return ErrorCodeEnum.ERR_10404.setErrorCode(result);
					}
					map.put(InfinityConstants.id, customerDTO.getId());
				} else {				
					map.put(InfinityConstants.id, customerId);
				}			
				map.put("isExistingUser", "true");
			}
			boolean doesContractExists = checkIfContractExists(contractId, contractCif, request);
			if(!doesContractExists) {
				return ErrorCodeEnum.ERR_10790.setErrorCode(result, "Invalid Account Ids");
			}
			accounts = getContractAccountDetails(contractCif, contractId, accounts, request);
			if (StringUtils.isBlank(accounts)) {
				return ErrorCodeEnum.ERR_10784.setErrorCode(result, "Invalid Account Ids");
			}
			
			map.put(InfinityConstants.accounts, accounts);
			// get service def from contract
			String serviceDefId = getServiceDefinitionIdByContract(contractId, request);
			if(StringUtils.isBlank(serviceDefId)) {
				return ErrorCodeEnum.ERR_10001.setErrorCode(result);
			}
			map.put(InfinityConstants.serviceDefinition, serviceDefId);
			map.put(InfinityConstants.cif, contractCif);
			
			JsonObject jsonObject = createInfinityUserPayload(map, contractId, request.getHeaderMap(),
					 request, true);
			InfinityUserManagementBackendDelegate infinityUserManagementBackendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(InfinityUserManagementBackendDelegate.class);
			if(createFlow) {
				JsonObject jsonResult = (JsonObject) infinityUserManagementBackendDelegate
						.createInfinityUser(jsonObject, request.getHeaderMap()).getResponse();
				result = JSONToResult.convert(jsonResult.toString());
				return generateUsernameAndActivationCode("", result, map, request, response);	
			}
			JsonObject jsonResult = (JsonObject) infinityUserManagementBackendDelegate
					.editInfinityUser(jsonObject, request.getHeaderMap()).getResponse();
			result = JSONToResult.convert(jsonResult.toString());
			return result;	
			
		} catch (Exception e) {
			logger.error("Exception occured while creating a contract " + e.getStackTrace());
			return ErrorCodeEnum.ERR_10001.setErrorCode(result);
		}
	}
	
	private String getCustomerIdForCif(String cif, String legalEntityId, DataControllerRequest request) {
		Map<String, Object> input = new HashMap<String, Object>();
		String filter = InfinityConstants.coreCustomerId + DBPUtilitiesConstants.EQUAL + cif + DBPUtilitiesConstants.AND
				+ InfinityConstants.isPrimary + DBPUtilitiesConstants.EQUAL + true + DBPUtilitiesConstants.AND
				+ InfinityConstants.LegalEntityId + DBPUtilitiesConstants.EQUAL + legalEntityId;
		input.put(DBPUtilitiesConstants.FILTER, filter);
		JsonObject jsonObject = ServiceCallHelper.invokeServiceAndGetJson(input, request.getHeaderMap(),
				URLConstants.CONTRACT_CUSTOMERS_GET);
		if (null != jsonObject && JSONUtil.hasKey(jsonObject, DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS)
				&& jsonObject.get(DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS).isJsonArray()) {
			JsonArray array = jsonObject.get(DBPDatasetConstants.DATASET_CONTRACT_CUSTOMERS).getAsJsonArray();
			if (array.size() > 0) {
				JsonObject object = array.get(0).getAsJsonObject();
				String customerId = object.get(InfinityConstants.Customer_id).getAsString();
				return customerId;
			}
		}
		return null;
	}

	@Override
	public Object createVirtualUser(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();
		try {
			Map<String, String> map = HelperMethods.getInputParamMap(inputArray);
			Iterator<String> iterator = request.getParameterNames();
			while (iterator.hasNext()) {
				String key = iterator.next();
				if ((!map.containsKey(key) || StringUtils.isBlank(map.get(key)))
						&& StringUtils.isNotBlank(request.getParameter(key))) {
					map.put(key, request.getParameter(key));
				}
			}
			
			String legalEntityId = map.get(InfinityConstants.legalEntityId);
			String contractId = map.get(InfinityConstants.contractId);
			String contractCif = map.get("contractCif");
			String accounts = map.get(InfinityConstants.accounts);
			String roleId = map.get(InfinityConstants.roleId);
			String email = map.get(InfinityConstants.email);
			String phoneCountryCode = map.get(InfinityConstants.phoneCountryCode);
			String phone = map.get(InfinityConstants.phone);
			String taxId = map.get(InfinityConstants.taxId);
			String firstName = map.get(InfinityConstants.firstName);
			String lastName = map.get(InfinityConstants.lastName);
			String dob = map.get(InfinityConstants.dob);
			if (HelperMethods.isBlank(firstName, lastName, legalEntityId, contractId, contractCif, accounts, roleId,
					email, phoneCountryCode, phone, taxId, dob)) {
				return ErrorCodeEnum.ERR_10001.setErrorCode(result);
			}
			map.put(InfinityConstants.ssn, taxId);
			map.put("isVirtualUser", "true");
			boolean isPayloadValid = MigrationUtils.validateUserDetails(map, result);
			if (!isPayloadValid) {
				return result;
			}
			map.put(DTOConstants.PHONE, phoneCountryCode+"-"+phone);
			// boolean doesContractExists = checkIfContractExists(contractId, contractCif, request);
			// if(!doesContractExists) {
			// 	return ErrorCodeEnum.ERR_10790.setErrorCode(result, "Invalid Account Ids");
			// }
			accounts = getContractAccountDetails(contractCif, contractId, accounts, request);
			if (StringUtils.isBlank(accounts)) {
				return ErrorCodeEnum.ERR_10784.setErrorCode(result, "Invalid Account Ids");
			}
			
			map.put(InfinityConstants.accounts, accounts);
			// get service def from contract
			String serviceDefId = getServiceDefinitionIdByContract(contractId, request);
			if(StringUtils.isBlank(serviceDefId)) {
				return ErrorCodeEnum.ERR_10001.setErrorCode(result);
			}
			map.put(InfinityConstants.serviceDefinition, serviceDefId);
			map.put(InfinityConstants.cif, contractCif);
			
			JsonObject jsonObject = createInfinityUserPayload(map,contractId, request.getHeaderMap(), request, true);
			InfinityUserManagementBackendDelegate infinityUserManagementBackendDelegate = DBPAPIAbstractFactoryImpl
					.getBackendDelegate(InfinityUserManagementBackendDelegate.class);
			JsonObject jsonResult = (JsonObject) infinityUserManagementBackendDelegate
					.createInfinityUser(jsonObject, request.getHeaderMap()).getResponse();
			result = JSONToResult.convert(jsonResult.toString());
			return generateUsernameAndActivationCode("", result, map, request, response);
		} catch (Exception e) {
			return ErrorCodeEnum.ERR_10001.setErrorCode(result);
		}
	}
	
	private boolean checkIfContractExists(String contractId, String contractCif, DataControllerRequest request) {
		JsonObject contractDetails = getContractDetails(contractCif, contractId, request);
		if (null != contractDetails
                && JSONUtil.hasKey(contractDetails, DBPDatasetConstants.DATASET_CONTRACTCORECUSTOMERS)
                && contractDetails.get(DBPDatasetConstants.DATASET_CONTRACTCORECUSTOMERS).isJsonArray()) {
            JsonArray array = contractDetails.get(DBPDatasetConstants.DATASET_CONTRACTCORECUSTOMERS).getAsJsonArray();
            if(array.size() > 0)
            	return true;
		}
		return false;
	}

	private JsonObject getContractDetails(String cif, String contractId, DataControllerRequest request) {
		Map<String, Object> input = new HashMap<String, Object>();
		String filter = InfinityConstants.contractId + DBPUtilitiesConstants.EQUAL + contractId
				+ DBPUtilitiesConstants.AND + InfinityConstants.coreCustomerId + DBPUtilitiesConstants.EQUAL + cif;
		input.put(DBPUtilitiesConstants.FILTER, filter);
		return ServiceCallHelper.invokeServiceAndGetJson(input, request.getHeaderMap(),
				URLConstants.CONTRACTCORECUSTOMER_GET);
	}


	@SuppressWarnings("deprecation")
	private void getContractPayload(Map<String, String> map, DataControllerRequest request, String cif,
			String serviceDefinitionId) throws ApplicationException, Exception {

		String accountsString = map.get(InfinityConstants.accounts);
		String legalEntityId = map.get(InfinityConstants.legalEntityId);
		String roleId = map.get(InfinityConstants.roleId);

		JsonArray accounts = new JsonArray();
		try {
			accounts = new JsonParser().parse(accountsString).getAsJsonArray();
		} catch (Exception e) {
			throw new ApplicationException(ErrorCodeEnum.ERR_10349, "Invalid Accounts input");
		}
		if(!isValidMembershipDetails(map, request)) {
			throw new ApplicationException(ErrorCodeEnum.ERR_10340);
		}
		if(!isValidAccountDetails(map, request)) {
			throw new ApplicationException(ErrorCodeEnum.ERR_10349, "Invalid Accounts input");
		}
		
		String serviceDefinitionName = "";
		String coreCustomerName = map.get(InfinityConstants.coreCustomerName);

		String filter = InfinityConstants.id + DBPUtilitiesConstants.EQUAL + serviceDefinitionId
				+ DBPUtilitiesConstants.AND + InfinityConstants.LegalEntityId + DBPUtilitiesConstants.EQUAL
				+ legalEntityId;

		Map<String, Object> input = new HashMap<String, Object>();
		input.put(DBPUtilitiesConstants.FILTER, filter);
		JsonObject jsonResponse = ServiceCallHelper.invokeServiceAndGetJson(input, request.getHeaderMap(),
				URLConstants.SERVICEDEFINITION_GET);

		if (jsonResponse.has(DBPDatasetConstants.DATASET_SERVICEDEFINITION)
				&& jsonResponse.get(DBPDatasetConstants.DATASET_SERVICEDEFINITION).isJsonArray()) {

			JsonObject serviceDefinition = jsonResponse.get(DBPDatasetConstants.DATASET_SERVICEDEFINITION)
					.getAsJsonArray().size() > 0
							? jsonResponse.get(DBPDatasetConstants.DATASET_SERVICEDEFINITION).getAsJsonArray().get(0)
									.getAsJsonObject()
							: new JsonObject();
			serviceDefinitionName = serviceDefinition.has(InfinityConstants.name)
					&& !serviceDefinition.get(InfinityConstants.name).isJsonNull()
							? serviceDefinition.get(InfinityConstants.name).getAsString()
							: "serviceDefinition";
			String serviceType = serviceDefinition.has(InfinityConstants.serviceType)
					&& !serviceDefinition.get(InfinityConstants.serviceType).isJsonNull()
					? serviceDefinition.get(InfinityConstants.serviceType).getAsString()
					: "TYPE_ID_RETAIL";
			map.put(InfinityConstants.serviceType, serviceType);
			try {
				if(!isValidServiceDefinitionRole(serviceDefinitionId, roleId, legalEntityId, request)) {
					throw new ApplicationException(ErrorCodeEnum.ERR_10349, "Invalid roleId");
				}
			} catch(Exception e) {
				throw new ApplicationException(ErrorCodeEnum.ERR_10349, "Invalid roleId");
			}
		} else {
			throw new ApplicationException(ErrorCodeEnum.ERR_10349);
		}

		if (HelperMethods.isBlank(cif)) {
			throw new ApplicationException(ErrorCodeEnum.ERR_10349, "coreCustomerId is Empty");
		}

		map.put(InfinityConstants.serviceDefinitionName, serviceDefinitionName);
		map.put(InfinityConstants.serviceDefinitionId, serviceDefinitionId);
		map.put(InfinityConstants.serviceDefinition, serviceDefinitionId);
		map.put(InfinityConstants.coreCustomerId, cif);		


		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty(InfinityConstants.isPrimary, "true");
		jsonObject.addProperty(InfinityConstants.coreCustomerId, cif);
		jsonObject.addProperty(InfinityConstants.coreCustomerName, coreCustomerName);
		jsonObject.addProperty("implicitAccountAccess", false);
		jsonObject.add(InfinityConstants.accounts, accounts);

		ContractBackendDelegate contractBackendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(ContractBackendDelegate.class);

		FeatureActionLimitsDTO coreCustomerFeatureActionDTO = contractBackendDelegate.getRestrictiveFeatureActionLimits(
				serviceDefinitionId, null, "", null, "", request.getHeaderMap(), false, "", legalEntityId);

		JsonArray accountLevelFeatures = new JsonArray();
		JsonArray globalLevelFeatures = new JsonArray();
		Map<String, Set<String>> featureAction = coreCustomerFeatureActionDTO.getFeatureaction();

		for (Entry<String, Set<String>> featureEntry : featureAction.entrySet()) {

			String featureId = featureEntry.getKey();
			JsonObject featureJson = coreCustomerFeatureActionDTO.getFeatureInfo().get(featureId);
			JsonObject featureObjectGlobal = new JsonObject();

			for (Entry<String, JsonElement> featureParams : featureJson.entrySet()) {
				featureObjectGlobal.add(featureParams.getKey(), featureParams.getValue());
			}

			JsonObject featureObjectAccount = new JsonObject();
			for (Entry<String, JsonElement> featureParams : featureJson.entrySet()) {
				featureObjectAccount.add(featureParams.getKey(), featureParams.getValue());
			}

			JsonArray accountpermissions = new JsonArray();
			JsonArray globalpermissions = new JsonArray();
			for (String action : featureEntry.getValue()) {

				JsonObject actionJson = coreCustomerFeatureActionDTO.getActionsInfo().get(action);
				boolean isAccountLevel = actionJson.has(InfinityConstants.isAccountLevel)
						&& !actionJson.get(InfinityConstants.isAccountLevel).isJsonNull()
						&& ("1".equals(actionJson.get(InfinityConstants.isAccountLevel).getAsString())
								|| "true".equals(actionJson.get(InfinityConstants.isAccountLevel).getAsString()));

				if (!isAccountLevel) {
					JsonObject actionObjectglobal = new JsonObject();
					for (Entry<String, JsonElement> actionParams : actionJson.entrySet()) {
						actionObjectglobal.add(actionParams.getKey(), actionParams.getValue());
					}
					actionObjectglobal.add(InfinityConstants.id, actionJson.get(InfinityConstants.actionId));
					actionObjectglobal.addProperty(InfinityConstants.isEnabled, "true");
					globalpermissions.add(actionObjectglobal);
				} else {
					JsonObject actionObjectAccount = new JsonObject();
					for (Entry<String, JsonElement> actionParams : actionJson.entrySet()) {
						actionObjectAccount.add(actionParams.getKey(), actionParams.getValue());
					}
					actionObjectAccount.add(InfinityConstants.id, actionJson.get(InfinityConstants.actionId));
					actionObjectAccount.addProperty(InfinityConstants.isEnabled, "true");
					accountpermissions.add(actionObjectAccount);
					globalpermissions.add(actionObjectAccount);
				}
			}

			if (globalpermissions.size() > 0) {
				featureObjectGlobal.add(InfinityConstants.permissions, globalpermissions);
				globalLevelFeatures.add(featureObjectGlobal);
			}

			if (accountpermissions.size() > 0) {
				featureObjectAccount.add(InfinityConstants.permissions, accountpermissions);
				accountLevelFeatures.add(featureObjectAccount);
			}
		}

		JsonArray globalLevelpermissions = new JsonArray();
		JsonObject globalLevelpermission = new JsonObject();

		globalLevelpermission.addProperty(InfinityConstants.coreCustomerId, cif);
		globalLevelpermission.add(InfinityConstants.features, globalLevelFeatures);
		globalLevelpermissions.add(globalLevelpermission);

		JsonArray accountLevelPermissions = new JsonArray();
		JsonObject accountLevelPermission = new JsonObject();
		JsonArray limitAccounts = new JsonArray();
		for (JsonElement accountElement : accounts) {

			JsonObject account = new JsonObject();
			for (Entry<String, JsonElement> accountParams : accountElement.getAsJsonObject().entrySet()) {
				account.add(accountParams.getKey(), accountParams.getValue());
			}

			account.add(InfinityConstants.featurePermissions, accountLevelFeatures);
			limitAccounts.add(account);
		}

		accountLevelPermission.addProperty(InfinityConstants.coreCustomerId, cif);
		accountLevelPermission.add(InfinityConstants.accounts, limitAccounts);
		accountLevelPermission.add("featurepermissions", accountLevelFeatures);
		accountLevelPermissions.add(accountLevelPermission);

		JsonArray features = new JsonArray();
		JsonArray transactionLimits = new JsonArray();
		JsonObject transactionLimit = new JsonObject();
		Map<String, Map<String, Map<String, String>>> featureActionLimits = coreCustomerFeatureActionDTO
				.getMonetaryActionLimits();

		for (Entry<String, Map<String, Map<String, String>>> featureEntry : featureActionLimits.entrySet()) {

			String featureId = featureEntry.getKey();
			JsonObject featureJson = coreCustomerFeatureActionDTO.getFeatureInfo().get(featureId);
			for (Entry<String, Map<String, String>> actionEntry : featureEntry.getValue().entrySet()) {

				JsonObject feature = new JsonObject();
				for (Entry<String, JsonElement> featureParams : featureJson.entrySet()) {
					feature.add(featureParams.getKey(), featureParams.getValue());
				}

				JsonObject actionJson = coreCustomerFeatureActionDTO.getActionsInfo().get(actionEntry.getKey());
				for (Entry<String, JsonElement> actionParams : actionJson.entrySet()) {
					feature.add(actionParams.getKey(), actionParams.getValue());
				}

				JsonArray limits = new JsonArray();
				for (Entry<String, String> limitsEntry : actionEntry.getValue().entrySet()) {
					JsonObject limit = new JsonObject();
					limit.addProperty(InfinityConstants.id, limitsEntry.getKey());
					limit.addProperty(InfinityConstants.value, limitsEntry.getValue() + "");
					limits.add(limit);
				}
				feature.add(InfinityConstants.limits, limits);
				features.add(feature);
			}
		}

		transactionLimit.addProperty(InfinityConstants.coreCustomerId, cif);
		transactionLimit.add("featurePermissions", features);
		transactionLimits.add(transactionLimit);

		map.put(InfinityConstants.serviceDefinitionName, serviceDefinitionName);
		map.put(InfinityConstants.serviceDefinitionId, serviceDefinitionId);

		JsonArray contractCustomers = new JsonArray();
		contractCustomers.add(jsonObject);
		map.put(InfinityConstants.contractCustomers, contractCustomers.toString());
		map.put("accountLevelPermissions1", accountLevelPermissions.toString());
		map.put("globalLevelPermissions1", globalLevelpermissions.toString());
		map.put("transactionLimits1", transactionLimits.toString());
		logger.error("Contract map : "+map);
	}

	@SuppressWarnings("deprecation")
	private JsonObject createInfinityUserPayload(Map<String, String> map, String contractId,
			Map<String, Object> headerMap, DataControllerRequest request,
			boolean isGlobalRequired) throws ApplicationException {

		JsonObject jsonObject = new JsonObject();
		
		// Populate Company List Details
		JsonArray companyList = new JsonArray();
		JsonObject company = new JsonObject();
		String cif = map.get(InfinityConstants.cif);
		String coreCustomerId = map.containsKey(InfinityConstants.coreCustomerId) ? map.get(InfinityConstants.coreCustomerId): cif;
		String serviceDefinition = map.get(InfinityConstants.serviceDefinition);
		String roleId = map.get(InfinityConstants.roleId);
		String accountsString = map.get(InfinityConstants.accounts);
		String legalEntityId = map.get(InfinityConstants.legalEntityId);
		String phone = map.get(InfinityConstants.phone);
		String phoneCountryCode = map.get(InfinityConstants.phoneCountryCode);
		String email = map.get(InfinityConstants.email);
		boolean isVirtualUser = Boolean.parseBoolean(map.get("isVirtualUser")) || Boolean.parseBoolean(map.get("isExistingUser"));
		
		if(!isValidServiceDefinitionRole(serviceDefinition, roleId, legalEntityId, request)) {
			throw new ApplicationException(ErrorCodeEnum.ERR_10349, "Invalid roleId");
		}
		
		// Populate account related data
		JsonArray accounts = new JsonArray();
		try {
			accounts = new JsonParser().parse(accountsString).getAsJsonArray();
			if(accounts.size() == 0) {
				throw new ApplicationException(ErrorCodeEnum.ERR_10349, "Invalid accounts");
			}
			for (JsonElement accountElement : accounts) {
				JsonObject account = accountElement.getAsJsonObject();
				JsonElement accelem = account.get(InfinityConstants.typeId);
				if (accelem != null && !accelem.isJsonNull()) {
					account.addProperty(InfinityConstants.accountType,
							HelperMethods.getAccountsNames().get(accelem.getAsString()));
				}
			}
		} catch (Exception e) {
			throw new ApplicationException(ErrorCodeEnum.ERR_10349, "Invalid account Array");
		}
		company.addProperty(InfinityConstants.roleId, roleId);
		company.addProperty(InfinityConstants.serviceDefinition, serviceDefinition);
		company.addProperty(InfinityConstants.cif, cif);
		company.addProperty(InfinityConstants.isPrimary, "true");
		company.addProperty(InfinityConstants.contractId, contractId);
		company.addProperty(InfinityConstants.legalEntityId, legalEntityId);
		company.add(InfinityConstants.accounts, accounts);
		company.addProperty(InfinityConstants.autoSyncAccounts, false);
		companyList.add(company);
		
		// Populate User Details
		String firstName = map.get(InfinityConstants.firstName);
		String lastName = map.get(InfinityConstants.lastName);
		String middleName = map.get(InfinityConstants.middleName);
		String ssn = map.get(InfinityConstants.ssn);
		String dob = map.get(InfinityConstants.dob);
		String drivingLicense = map.get(InfinityConstants.drivingLicenseNumber);

		JsonObject userDetails = new JsonObject();
		userDetails.addProperty(InfinityConstants.coreCustomerId, coreCustomerId);
		userDetails.addProperty(InfinityConstants.legalEntityId, legalEntityId);
		userDetails.addProperty(InfinityConstants.firstName, firstName);
		userDetails.addProperty(InfinityConstants.lastName, lastName);
		userDetails.addProperty(InfinityConstants.middleName, middleName);
		userDetails.addProperty(InfinityConstants.ssn, ssn);
		userDetails.addProperty(InfinityConstants.dob, dob);
		userDetails.addProperty(InfinityConstants.drivingLicenseNumber, drivingLicense);
		userDetails.addProperty(InfinityConstants.phone, phone);
		userDetails.addProperty(InfinityConstants.phoneCountryCode, phoneCountryCode);
		userDetails.addProperty(InfinityConstants.email, email);
		if(isVirtualUser) {
			String id = map.get(InfinityConstants.id);
			userDetails.remove(InfinityConstants.coreCustomerId);
			userDetails.addProperty(InfinityConstants.id, id);
		}


		// Populate accountLevel And Global Level Permissions
		ContractBackendDelegate contractBackendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(ContractBackendDelegate.class);
		FeatureActionLimitsDTO coreCustomerFeatureActionDTO = contractBackendDelegate.getRestrictiveFeatureActionLimits(
				serviceDefinition, null, roleId, cif, "", headerMap, false, "", legalEntityId);

		JsonArray accountLevelFeatures = new JsonArray();
		JsonArray globalLevelFeatures = new JsonArray();
		Map<String, Set<String>> featureAction = coreCustomerFeatureActionDTO.getFeatureaction();
		for (Entry<String, Set<String>> featureEntry : featureAction.entrySet()) {

			String featureId = featureEntry.getKey();
			JsonObject featureJson = coreCustomerFeatureActionDTO.getFeatureInfo().get(featureId);
			JsonObject featureObjectGlobal = new JsonObject();

			for (Entry<String, JsonElement> featureParams : featureJson.entrySet()) {
				featureObjectGlobal.add(featureParams.getKey(), featureParams.getValue());
			}

			JsonObject featureObjectAccount = new JsonObject();
			for (Entry<String, JsonElement> featureParams : featureJson.entrySet()) {
				featureObjectAccount.add(featureParams.getKey(), featureParams.getValue());
			}

			JsonArray accountpermissions = new JsonArray();
			JsonArray globalpermissions = new JsonArray();
			for (String action : featureEntry.getValue()) {

				JsonObject actionJson = coreCustomerFeatureActionDTO.getActionsInfo().get(action);
				boolean isAccountLevel = actionJson.has(InfinityConstants.isAccountLevel)
						&& !actionJson.get(InfinityConstants.isAccountLevel).isJsonNull()
						&& ("1".equals(actionJson.get(InfinityConstants.isAccountLevel).getAsString())
								|| "true".equals(actionJson.get(InfinityConstants.isAccountLevel).getAsString()));

				if (!isAccountLevel) {
					JsonObject actionObjectglobal = new JsonObject();
					for (Entry<String, JsonElement> actionParams : actionJson.entrySet()) {
						actionObjectglobal.add(actionParams.getKey(), actionParams.getValue());
					}
					actionObjectglobal.add(InfinityConstants.id, actionJson.get(InfinityConstants.actionId));
					actionObjectglobal.addProperty(InfinityConstants.isEnabled, "true");
					globalpermissions.add(actionObjectglobal);
				} else {
					JsonObject actionObjectAccount = new JsonObject();
					for (Entry<String, JsonElement> actionParams : actionJson.entrySet()) {
						actionObjectAccount.add(actionParams.getKey(), actionParams.getValue());
					}
					actionObjectAccount.add(InfinityConstants.id, actionJson.get(InfinityConstants.actionId));
					actionObjectAccount.addProperty(InfinityConstants.isEnabled, "true");
					accountpermissions.add(actionObjectAccount);
				}

			}

			if (globalpermissions.size() > 0) {
				featureObjectGlobal.add(InfinityConstants.permissions, globalpermissions);
				globalLevelFeatures.add(featureObjectGlobal);
			}
			if (accountpermissions.size() > 0) {
				featureObjectAccount.add(InfinityConstants.permissions, accountpermissions);
				accountLevelFeatures.add(featureObjectAccount);
			}
		}

		JsonArray globalLevelpermissions = new JsonArray();
		JsonObject globalLevelpermission = new JsonObject();
		globalLevelpermission.addProperty(InfinityConstants.cif, cif);
		globalLevelpermission.addProperty(InfinityConstants.legalEntityId, legalEntityId);
		globalLevelpermission.add(InfinityConstants.features, globalLevelFeatures);
		globalLevelpermissions.add(globalLevelpermission);

		JsonArray accountLevelPermissions = new JsonArray();
		JsonObject accountLevelPermission = new JsonObject();
		JsonArray limitAccounts = new JsonArray();
		for (JsonElement accountElement : accounts) {
			JsonObject account = new JsonObject();
			for (Entry<String, JsonElement> accountParams : accountElement.getAsJsonObject().entrySet()) {
				account.add(accountParams.getKey(), accountParams.getValue());
			}
			account.add(InfinityConstants.featurePermissions, accountLevelFeatures);
			limitAccounts.add(account);
		}
		accountLevelPermission.addProperty(InfinityConstants.cif, cif);
		accountLevelPermission.add(InfinityConstants.accounts, limitAccounts);
		accountLevelPermission.addProperty(InfinityConstants.legalEntityId, legalEntityId);
		accountLevelPermissions.add(accountLevelPermission);

		// Populate Transaction Limits
		JsonArray features = new JsonArray();
		JsonArray limitGroups = new JsonArray();
		Map<String, Map<String, Double>> limitGroupsMap = new HashMap<String, Map<String, Double>>();
		Map<String, Map<String, Map<String, String>>> featureActionLimits = coreCustomerFeatureActionDTO
				.getMonetaryActionLimits();

		for (Entry<String, Map<String, Map<String, String>>> featureEntry : featureActionLimits.entrySet()) {

			String featureId = featureEntry.getKey();
			JsonObject featureJson = coreCustomerFeatureActionDTO.getFeatureInfo().get(featureId);
			for (Entry<String, Map<String, String>> actionEntry : featureEntry.getValue().entrySet()) {
				JsonObject feature = new JsonObject();
				for (Entry<String, JsonElement> featureParams : featureJson.entrySet()) {
					feature.add(featureParams.getKey(), featureParams.getValue());
				}

				JsonObject actionJson = coreCustomerFeatureActionDTO.getActionsInfo().get(actionEntry.getKey());
				for (Entry<String, JsonElement> actionParams : actionJson.entrySet()) {
					feature.add(actionParams.getKey(), actionParams.getValue());
				}
				String limitGroupId = actionJson.has(InfinityConstants.limitGroupId)
						&& !actionJson.get(InfinityConstants.limitGroupId).isJsonNull()
								? actionJson.get(InfinityConstants.limitGroupId).getAsString()
								: null;
				feature.addProperty(InfinityConstants.isEnabled, "true");
				JsonArray limits = new JsonArray();
				if (!limitGroupsMap.containsKey(limitGroupId)) {
					limitGroupsMap.put(limitGroupId, new HashMap<String, Double>());
				}
				for (Entry<String, String> limitsEntry : actionEntry.getValue().entrySet()) {
					JsonObject limit = new JsonObject();
					if (!limitGroupsMap.get(limitGroupId).containsKey(limitsEntry.getKey())) {
						limitGroupsMap.get(limitGroupId).put(limitsEntry.getKey(), 0.0);
					}
					Double value = Double.parseDouble(limitsEntry.getValue());
					if (limitsEntry.getKey().equals(InfinityConstants.DAILY_LIMIT)) {
						limit.addProperty(InfinityConstants.id, InfinityConstants.PRE_APPROVED_DAILY_LIMIT);
						limit.addProperty(InfinityConstants.value, "0.0");
						limits.add(limit);
						limit = new JsonObject();
						limit.addProperty(InfinityConstants.id, InfinityConstants.AUTO_DENIED_DAILY_LIMIT);
						limit.addProperty(InfinityConstants.value, limitsEntry.getValue());
						limits.add(limit);

						limit = new JsonObject();
						limit.addProperty(InfinityConstants.id, InfinityConstants.DAILY_LIMIT);
						limit.addProperty(InfinityConstants.value, limitsEntry.getValue());
						limits.add(limit);

						limitGroupsMap.get(limitGroupId).put(limitsEntry.getKey(),
								value + limitGroupsMap.get(limitGroupId).get(limitsEntry.getKey()));
					} else if (limitsEntry.getKey().equals(InfinityConstants.WEEKLY_LIMIT)) {
						limit.addProperty(InfinityConstants.id, InfinityConstants.PRE_APPROVED_WEEKLY_LIMIT);
						limit.addProperty(InfinityConstants.value, "0.0");
						limits.add(limit);
						limit = new JsonObject();
						limit.addProperty(InfinityConstants.id, InfinityConstants.AUTO_DENIED_WEEKLY_LIMIT);
						limit.addProperty(InfinityConstants.value, limitsEntry.getValue());
						limits.add(limit);

						limit = new JsonObject();
						limit.addProperty(InfinityConstants.id, InfinityConstants.WEEKLY_LIMIT);
						limit.addProperty(InfinityConstants.value, limitsEntry.getValue());
						limits.add(limit);

						limitGroupsMap.get(limitGroupId).put(limitsEntry.getKey(),
								value + limitGroupsMap.get(limitGroupId).get(limitsEntry.getKey()));
					} else {
						limit.addProperty(InfinityConstants.id, InfinityConstants.PRE_APPROVED_TRANSACTION_LIMIT);
						limit.addProperty(InfinityConstants.value, "0.0");
						limits.add(limit);
						limit = new JsonObject();
						limit.addProperty(InfinityConstants.id, InfinityConstants.AUTO_DENIED_TRANSACTION_LIMIT);
						limit.addProperty(InfinityConstants.value, limitsEntry.getValue());
						limits.add(limit);

						limit = new JsonObject();
						limit.addProperty(InfinityConstants.id, InfinityConstants.MAX_TRANSACTION_LIMIT);
						limit.addProperty(InfinityConstants.value, limitsEntry.getValue());
						limits.add(limit);
						limitGroupsMap.get(limitGroupId).put(limitsEntry.getKey(),
								value > limitGroupsMap.get(limitGroupId).get(limitsEntry.getKey()) ? value
										: limitGroupsMap.get(limitGroupId).get(limitsEntry.getKey()));
					}
				}
				feature.add(InfinityConstants.limits, limits);
				features.add(feature);
			}
		}
		
		limitAccounts = new JsonArray();
		for (JsonElement accountElement : accounts) {
			JsonObject account = new JsonObject();
			for (Entry<String, JsonElement> accountParams : accountElement.getAsJsonObject().entrySet()) {
				account.add(accountParams.getKey(), accountParams.getValue());
			}
			account.add(InfinityConstants.featurePermissions, features);
			limitAccounts.add(account);
		}

		for (Entry<String, Map<String, Double>> entry : limitGroupsMap.entrySet()) {
			JsonObject limitGroupJsonObject = new JsonObject();
			limitGroupJsonObject.addProperty(InfinityConstants.limitGroupId, entry.getKey());
			JsonArray limits = new JsonArray();
			for (Entry<String, Double> limitEntry : entry.getValue().entrySet()) {
				JsonObject limit = new JsonObject();
				limit.addProperty(InfinityConstants.id, limitEntry.getKey());
				limit.addProperty(InfinityConstants.value, limitEntry.getValue() + "");
				limits.add(limit);
			}
			limitGroupJsonObject.add(InfinityConstants.limits, limits);
			limitGroups.add(limitGroupJsonObject);
		}
		JsonObject transactionLimit = new JsonObject();
		transactionLimit.addProperty(InfinityConstants.cif, cif);
		transactionLimit.add(InfinityConstants.accounts, limitAccounts);
		transactionLimit.add(InfinityConstants.limitGroups, limitGroups);
		transactionLimit.addProperty(InfinityConstants.legalEntityId, legalEntityId);
		JsonArray transactionLimits = new JsonArray();
		transactionLimits.add(transactionLimit);

		map.put(InfinityConstants.userDetails, userDetails.toString());
		map.put(InfinityConstants.companyList, companyList.toString());
		if (isGlobalRequired) {
			map.put(InfinityConstants.globalLevelPermissions, globalLevelpermissions.toString());
		} else {
			map.put(InfinityConstants.globalLevelPermissions, new JsonArray().toString());
		}
		map.put(InfinityConstants.accountLevelPermissions, accountLevelPermissions.toString());
		map.put(InfinityConstants.transactionLimits, transactionLimits.toString());
		
		
		if (isGlobalRequired) {
			jsonObject.add(InfinityConstants.globalLevelPermissions, globalLevelpermissions);
		} else {
			jsonObject.add(InfinityConstants.globalLevelPermissions, new JsonArray());
		}
		jsonObject.add(InfinityConstants.userDetails, userDetails);
		jsonObject.add(InfinityConstants.companyList, companyList);
		jsonObject.add(InfinityConstants.accountLevelPermissions, accountLevelPermissions);
		jsonObject.add(InfinityConstants.transactionLimits, transactionLimits);
		logger.debug("input Map to create InfinityUserMap " + map);
		return jsonObject;
	}

	private boolean isValidServiceDefinitionRole(String serviceDefId, String roleId, String legalEntityId, DataControllerRequest request)
			throws ApplicationException {

				return true;
		// Map<String, Object> input = new HashMap<String, Object>();
		// String filter = InfinityConstants.serviceDefinitionId + DBPUtilitiesConstants.EQUAL + serviceDefId
		// 		+ DBPUtilitiesConstants.AND + InfinityConstants.Group_id + DBPUtilitiesConstants.EQUAL + roleId
		// 		+ DBPUtilitiesConstants.AND + InfinityConstants.LegalEntityId + DBPUtilitiesConstants.EQUAL
		// 		+ legalEntityId;

		// input.put(DBPUtilitiesConstants.FILTER, filter);
		// JsonObject jsonResponse = ServiceCallHelper.invokeServiceAndGetJson(input, request.getHeaderMap(),
		// 		URLConstants.GROUP_SERVICEDEFINITION);

		// if (jsonResponse.has(DBPDatasetConstants.DATASET_GROUPSERVICEDEFINITION)
		// 		&& jsonResponse.get(DBPDatasetConstants.DATASET_GROUPSERVICEDEFINITION).isJsonArray()) {

		// 	JsonObject groupServiceDefinition = jsonResponse.get(DBPDatasetConstants.DATASET_GROUPSERVICEDEFINITION)
		// 			.getAsJsonArray().size() > 0
		// 					? jsonResponse.get(DBPDatasetConstants.DATASET_GROUPSERVICEDEFINITION).getAsJsonArray()
		// 							.get(0).getAsJsonObject()
		// 					: new JsonObject();

		// 	String role = groupServiceDefinition.has(InfinityConstants.Group_id)
		// 			&& !groupServiceDefinition.get(InfinityConstants.Group_id).isJsonNull()
		// 					? groupServiceDefinition.get(InfinityConstants.Group_id).getAsString()
		// 					: null;
		// 	if (!roleId.equals(role)) 
		// 		return false;
		// 	else
		// 		return true;
		// } else {
		// 	return false;
		// }
	}
	
	private boolean isValidAccountDetails(Map<String, String> map, DataControllerRequest request) throws Exception {
		MigrateInfinityUserBusinessDelegate migrationDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(MigrateInfinityUserBusinessDelegate.class);
		
		String coreCustomerId = map.get(InfinityConstants.cif);
		String legalEntityId = map.get(InfinityConstants.legalEntityId);
		
		migrationDelegate.getCoreCustomerAccountDetails(coreCustomerId, legalEntityId, request);
		return true;
	}
	
	private boolean isValidMembershipDetails(Map<String, String> map, DataControllerRequest request) throws Exception {

		String coreCustomerId = map.get(InfinityConstants.cif);
		String legalEntityId = map.get(InfinityConstants.legalEntityId);
		CoreCustomerBusinessDelegate coreCustomerBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(CoreCustomerBusinessDelegate.class);
		MembershipDTO membershipDetails = coreCustomerBusinessDelegate.getMembershipDetails(coreCustomerId,
				legalEntityId, request.getHeaderMap());
		if (membershipDetails != null) {
			JsonArray array = new JsonArray();
			JsonObject jsonObject = new JsonObject();
			String phone[] = membershipDetails.getPhone().split("-");
			jsonObject.addProperty(InfinityConstants.phoneNumber, phone[1]);
			jsonObject.addProperty(InfinityConstants.phoneCountryCode, phone[0]);
			jsonObject.addProperty(InfinityConstants.email, membershipDetails.getEmail());
			array.add(jsonObject);
			map.put(InfinityConstants.communication, array.toString());

			array = new JsonArray();
			jsonObject = new JsonObject();
			jsonObject.addProperty(InfinityConstants.addressLine1, membershipDetails.getAddressLine1());
			jsonObject.addProperty(InfinityConstants.addressLine2, membershipDetails.getAddressLine2());
			jsonObject.addProperty(InfinityConstants.cityName, membershipDetails.getCityName());
			jsonObject.addProperty(InfinityConstants.state, membershipDetails.getState());
			jsonObject.addProperty(InfinityConstants.zipCode, membershipDetails.getZipCode());
			jsonObject.addProperty(InfinityConstants.country, membershipDetails.getCountry());
			array.add(jsonObject);
			map.put(InfinityConstants.address, array.toString());

			map.put(InfinityConstants.firstName, membershipDetails.getFirstName());
			map.put(InfinityConstants.lastName, membershipDetails.getLastName());
			map.put(InfinityConstants.taxId, membershipDetails.getTaxId());
			String firstName = map.get(InfinityConstants.firstName);
			String lastName = map.get(InfinityConstants.lastName);
			String name = firstName + " " + lastName + " " + coreCustomerId;
			map.put(InfinityConstants.name, name);
			map.put(InfinityConstants.contractName, name);
			map.put(InfinityConstants.coreCustomerName, name);
			map.put(InfinityConstants.coreCustomerId, coreCustomerId);
			map.put(InfinityConstants.companyId, legalEntityId);
			
			return true;
		}
		return false;
	}
	
	private Result generateInfinityUserActivationCodeAndUsername(Map<String, String> inputParams,
			DataControllerRequest request, DataControllerResponse response) throws ApplicationException {

		Result result = new Result();
		LegalEntityUtil.addCompanyIDToHeaders(request);
		String contractStatus = StringUtils.isNotBlank(inputParams.get(InfinityConstants.contractStatus))
				? inputParams.get(InfinityConstants.contractStatus)
				: request.getParameter(InfinityConstants.contractStatus);
		if (StringUtils.isNotBlank(contractStatus)
				&& !DBPUtilitiesConstants.CONTRACT_STATUS_ACTIVE.equalsIgnoreCase(contractStatus))
			return result;
		Iterator<String> iterator = request.getParameterNames();

		while (iterator.hasNext()) {
			String key = iterator.next();
			if ((!inputParams.containsKey(key) || StringUtils.isBlank(inputParams.get(key)))
					&& StringUtils.isNotBlank(request.getParameter(key))) {
				inputParams.put(key, request.getParameter(key));
			}
		}

		String userId = StringUtils.isNotBlank(inputParams.get(InfinityConstants.userId))
				? inputParams.get(InfinityConstants.userId)
				: request.getParameter(InfinityConstants.userId);
		if (StringUtils.isBlank(userId)) {
			throw new ApplicationException(ErrorCodeEnum.ERR_10792);
		}
		boolean isVirtualUser = Boolean.parseBoolean(inputParams.get("isVirtualUser"));
		if(isVirtualUser) {
			String phone = inputParams.get(InfinityConstants.phone);
			String email = inputParams.get(InfinityConstants.email);
			inputParams.put(DTOConstants.PHONE, inputParams.getOrDefault(DTOConstants.PHONE, phone));
			inputParams.put(DTOConstants.EMAIL, email);
		}
		try {
			Map<String, String> bundleConfigurations = BundleConfigurationHandler
					.fetchBundleConfigurations(BundleConfigurationHandler.BUDLENAME_C360, request);
			InfinityUserManagementBusinessDelegate infinityUserManagementBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(InfinityUserManagementBusinessDelegate.class);

			DBXResult generatedResult = infinityUserManagementBusinessDelegate
					.generateInfinityUserActivationCodeAndUsername(bundleConfigurations, inputParams,
							request.getHeaderMap());
			JsonObject responseObject = (JsonObject) generatedResult.getResponse();
			boolean status = JSONUtil.hasKey(responseObject, InfinityConstants.status)
					? responseObject.get(InfinityConstants.status).getAsBoolean()
					: false;
			result.addStringParam("status", String.valueOf(status));
		} catch (ApplicationException e) {
			logger.error(
					"InfinityUserManagementResourceImpl : Exception occured while generating username and activation code ",
					e);
			throw new ApplicationException(e.getErrorCodeEnum());
		} catch (Exception e) {
			logger.error(
					"InfinityUserManagementResourceImpl : Exception occured while generating username and activation code ",
					e);
			throw new ApplicationException(ErrorCodeEnum.ERR_10795);
		}
		return result;
	}
	
	private String getContractAccountDetails(String cif, String contractId, String accounts, DataControllerRequest request) {
		JsonArray contractAccounts = new JsonArray();
		Map<String, Object> input = new HashMap<String, Object>();
		Set<String> finalAccounts = parseAccountsJson(accounts);
		String filter = InfinityConstants.contractId + DBPUtilitiesConstants.EQUAL + contractId
				+ DBPUtilitiesConstants.AND + InfinityConstants.coreCustomerId + DBPUtilitiesConstants.EQUAL + cif;
		input.put(DBPUtilitiesConstants.FILTER, filter);
		JsonObject contract = ServiceCallHelper.invokeServiceAndGetJson(input, request.getHeaderMap(),
				URLConstants.CONTRACTACCOUNT_GET);
		logger.error("CONTRACT_GET : " +contract);
		if (null != contract
                && JSONUtil.hasKey(contract, DBPDatasetConstants.DATASET_CONTRACTACCOUNT)
                && contract.get(DBPDatasetConstants.DATASET_CONTRACTACCOUNT).isJsonArray()) {
            JsonArray array = contract.get(DBPDatasetConstants.DATASET_CONTRACTACCOUNT).getAsJsonArray();
            for(JsonElement element : array) {
            	JsonObject account = element.getAsJsonObject();
            	String accountId = account.get(InfinityConstants.accountId).getAsString();
            	if(finalAccounts.contains(accountId)) {
            		JsonObject contractAccount = new JsonObject();
            		contractAccount.addProperty(InfinityConstants.accountId	, accountId);
            		contractAccount.addProperty(InfinityConstants.accountName, account.get(InfinityConstants.accountName).getAsString());
            		contractAccount.addProperty(InfinityConstants.isEnabled, true);
            		contractAccount.addProperty(InfinityConstants.ownerType, account.get(InfinityConstants.ownerType).getAsString());
            		contractAccounts.add(contractAccount);
            	}
            }
        }
		return contractAccounts.toString();
	}
	
	@SuppressWarnings("deprecation")
	private Set<String> parseAccountsJson(String providedAccounts) {
		// Populate account related data
		JsonArray accounts = new JsonArray();
		Set<String> finalAccounts = new HashSet<>();
		try {
			accounts = new JsonParser().parse(providedAccounts).getAsJsonArray();
			if (accounts.size() == 0) {
				return null;
			}
			for (JsonElement accountElement : accounts) {
				JsonObject account = accountElement.getAsJsonObject();
				String accountId = account.get(InfinityConstants.accountId).getAsString();
				if (StringUtils.isNotBlank(accountId)) {
					finalAccounts.add(accountId);
				}
			}
		} catch (Exception e) {
			return null;
		}
		return finalAccounts;
	}

	private String getServiceDefinitionIdByContract(String contractId, DataControllerRequest request) {
		Map<String, Object> input = new HashMap<String, Object>();
		String filter = InfinityConstants.id + DBPUtilitiesConstants.EQUAL + contractId;
		input.put(DBPUtilitiesConstants.FILTER, filter);
		JsonObject contract = ServiceCallHelper.invokeServiceAndGetJson(input, request.getHeaderMap(),
				URLConstants.CONTRACT_GET);
		logger.error("CONTRACT_GET : " +contract);
		if (null != contract
                && JSONUtil.hasKey(contract, DBPDatasetConstants.DATASET_CONTRACT)
                && contract.get(DBPDatasetConstants.DATASET_CONTRACT).isJsonArray()) {
            JsonArray array = contract.get(DBPDatasetConstants.DATASET_CONTRACT).getAsJsonArray();
            	JsonObject contractObj = array.get(0).getAsJsonObject();
            	String serviceDefId = contractObj.get(InfinityConstants.servicedefinitionId).getAsString();
            	return serviceDefId;
        }
		return null;
	}
	
	
	private Result generateUsernameAndActivationCode(String contractId, Result result, Map<String, String> map,
			DataControllerRequest request, DataControllerResponse response) {
		String customerId = "";
		if (result.hasParamByName(InfinityConstants.id)
				&& StringUtils.isNotBlank(result.getParamValueByName(InfinityConstants.id))) {
			customerId = result.getParamValueByName(InfinityConstants.id);
			CustomerDTO customerDTO = new CustomerDTO();
			customerDTO = (CustomerDTO) customerDTO.loadDTO(customerId);
			if (customerDTO != null) {
				HashMap<String, Object> input = new HashMap<String, Object>();
				input.put(InfinityConstants.id, customerId);
				input.put(InfinityConstants.Status_id, HelperMethods.getCustomerStatus().get("NEW"));
				input.put(InfinityConstants.CustomerType_id, HelperMethods.getCustomerTypes().get("Retail"));
				customerDTO.setIsChanged(true);
				customerDTO.persist(input, request.getHeaderMap());

				map.put(InfinityConstants.userId, customerId);
				map.put(InfinityConstants.contractStatus, DBPUtilitiesConstants.CONTRACT_STATUS_ACTIVE);

				Result deliveryResult;
				try {
					deliveryResult = generateInfinityUserActivationCodeAndUsername(map, request, response);
				} catch (ApplicationException e) {
					ErrorCodeEnum.ERR_10795.setErrorCode(result);
					return result;
				}
				result.addAllParams(deliveryResult.getAllParams());
				result.addStringParam("status", "success");
				if(StringUtils.isNotBlank(contractId))
					result.addStringParam("contractId", contractId);
				return result;
			}
		}
		return result;
	}

	
	private Map<String, String> getMembershipDetails(String cif, String legalEntityId, DataControllerRequest request) throws ApplicationException {
		Map<String, String> map = new HashMap<>();
		CoreCustomerBusinessDelegate coreCustomerBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(CoreCustomerBusinessDelegate.class);
		MembershipDTO membershipDetails = coreCustomerBusinessDelegate.getMembershipDetails(cif,
				legalEntityId, request.getHeaderMap());
		if (membershipDetails != null) {
			JsonArray array = new JsonArray();
			JsonObject jsonObject = new JsonObject();
			String phone[] = membershipDetails.getPhone().split("-");
			jsonObject.addProperty(InfinityConstants.phoneNumber, phone[1]);
			jsonObject.addProperty(InfinityConstants.phoneCountryCode, phone[0]);
			jsonObject.addProperty(InfinityConstants.email, membershipDetails.getEmail());
			array.add(jsonObject);
			map.put(InfinityConstants.communication, array.toString());

			array = new JsonArray();
			jsonObject = new JsonObject();
			jsonObject.addProperty(InfinityConstants.addressLine1, membershipDetails.getAddressLine1());
			jsonObject.addProperty(InfinityConstants.addressLine2, membershipDetails.getAddressLine2());
			jsonObject.addProperty(InfinityConstants.cityName, membershipDetails.getCityName());
			jsonObject.addProperty(InfinityConstants.state, membershipDetails.getState());
			jsonObject.addProperty(InfinityConstants.zipCode, membershipDetails.getZipCode());
			jsonObject.addProperty(InfinityConstants.country, membershipDetails.getCountry());
			array.add(jsonObject);
			map.put(InfinityConstants.address, array.toString());

			map.put(InfinityConstants.firstName, membershipDetails.getFirstName());
			map.put(InfinityConstants.lastName, membershipDetails.getLastName());
			map.put(InfinityConstants.taxId, membershipDetails.getTaxId());
			String firstName = map.get(InfinityConstants.firstName);
			String lastName = map.get(InfinityConstants.lastName);
			String name = firstName + " " + lastName + " " + cif;
			map.put(InfinityConstants.name, name);
			map.put(InfinityConstants.contractName, name);
			map.put(InfinityConstants.coreCustomerName, name);
			map.put(InfinityConstants.coreCustomerId, cif);
			map.put(InfinityConstants.companyId, legalEntityId);
		}
		return map;
	}

	@Override
	public Object createSignatoryGroup(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		Result result = new Result();
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		Map<String, Object> headersMap = new HashMap<String, Object>();
		Iterator<String> iterator = request.getParameterNames();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if ((!inputParams.containsKey(key) || StringUtils.isBlank(inputParams.get(key)))
					&& StringUtils.isNotBlank(request.getParameter(key))) {
				inputParams.put(key, request.getParameter(key));
			}
		}

		SignatoryGroupBusinessDelegate signatoryGroupBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(SignatoryGroupBusinessDelegate.class);

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String userId = CustomerSession.getCustomerId(customer);
		String userfullName = CustomerSession.getCustomerCompleteName(customer);
		String currentdate = HelperMethods.getFormattedTimeStamp(new Date(), "yyyy-MM-dd HH:mm:ss.S");

		JSONObject signatoryGroup = null;
		String coreCustomerId = inputParams.get(Constants.CORECUSTOMERID) == null ? ""
				: inputParams.get(Constants.CORECUSTOMERID).toString();
		String contractId = inputParams.get(Constants.CONTRACTID) == null ? ""
				: inputParams.get(Constants.CONTRACTID).toString();
		String signatoryGroupName = inputParams.get(Constants.SIGNATORYGROUPNAME) == null ? ""
				: inputParams.get(Constants.SIGNATORYGROUPNAME).toString();
		String signatoryGroupDescription = inputParams.get(Constants.SIGNATORYGROUPDESCRIPTION) == null ? ""
				: inputParams.get(Constants.SIGNATORYGROUPDESCRIPTION).toString();
		JSONArray signatories = null;

		String sigArray = inputParams.get(Constants.SIGNATORIES) == null ? ""
				: inputParams.get(Constants.SIGNATORIES).toString();
		signatories = new JSONArray(sigArray);

		if (StringUtils.isBlank(coreCustomerId)) {
			return ErrorCodeEnum.ERR_21025.setErrorCode(new Result());
		}

		if (StringUtils.isBlank(contractId)) {
			return ErrorCodeEnum.ERR_21024.setErrorCode(new Result());
		}

		if (StringUtils.isBlank(signatoryGroupName)) {
			return ErrorCodeEnum.ERR_21022.setErrorCode(new Result());
		}

		if (StringUtils.isBlank(sigArray)) {
			return ErrorCodeEnum.ERR_21026.setErrorCode(new Result());
		}

		if (MigrationUtils.containSpecialChars(signatoryGroupName) || MigrationUtils.containSpecialChars(signatoryGroupDescription)) {
			return ErrorCodeEnum.ERR_21023.setErrorCode(new Result());
		}

		signatoryGroupName = signatoryGroupName.substring(0, Math.min(signatoryGroupName.length(), 50));
		signatoryGroupDescription = signatoryGroupDescription.substring(0,
				Math.min(signatoryGroupDescription.length(), 200));

		boolean isNameDuplicate = signatoryGroupBusinessDelegate.isGroupNameDuplicate(signatoryGroupName,
				coreCustomerId, contractId, headersMap);
		if (!isNameDuplicate) {
			String customerId;
			JSONObject record;
			boolean isUserThere = false;
			boolean isSignatoryAlreadyPresentInAnotherGroup = false;
			for (int i = 0; i < signatories.length(); i++) {
				isUserThere = false;
				customerId = signatories.getJSONObject(i).get(Constants.CUSTOMERID).toString();
				List<SignatoryGroupDTO> signatoryGroups = signatoryGroupBusinessDelegate
						.getSignatoryUsers(coreCustomerId, contractId, headersMap, request);
				JSONArray rulesJSONArr = new JSONArray(signatoryGroups);
				for (int j = 0; j < rulesJSONArr.length(); j++) {
					record = rulesJSONArr.getJSONObject(j);
					if (record.get(InfinityConstants.userId).toString().equalsIgnoreCase(customerId)) {
						isUserThere = true;
						break;
					}
				}
				if (!isUserThere) {
					return ErrorCodeEnum.ERR_21018.setErrorCode(new Result());
				}

				isSignatoryAlreadyPresentInAnotherGroup = signatoryGroupBusinessDelegate
						.isSignatoryAlreadyPresentInAnotherGroup(customerId, coreCustomerId, contractId, headersMap);
				if (isSignatoryAlreadyPresentInAnotherGroup) {
					return ErrorCodeEnum.ERR_21021.setErrorCode(new Result());
				}
			}

			try {
				signatoryGroup = signatoryGroupBusinessDelegate.createSignatoryGroup(signatoryGroupName,
						signatoryGroupDescription, coreCustomerId, contractId, signatories, userId);
			} catch (Exception e1) {
				logger.error("Error while creating SignatoryGroupDTO from SignatoryGroupBusinessDelegate : " + e1);
				return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
			}
			try {
				result = JSONToResult.convert(signatoryGroup.toString());
				result.addParam(Constants.CREATEDBY, userfullName);
				result.addParam(Constants.CREATEDON, currentdate);
				return result;
			} catch (Exception e) {
				logger.error("Error while converting response SignatoryGroupDTO to result " + e);
				return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
			}
		} else {
			return ErrorCodeEnum.ERR_21020.setErrorCode(new Result());
		}

	}

	@Override
	public Object createPayee(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		Result result = new Result();
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		Iterator<String> iterator = request.getParameterNames();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if ((!inputParams.containsKey(key) || StringUtils.isBlank(inputParams.get(key)))
					&& StringUtils.isNotBlank(request.getParameter(key))) {
				inputParams.put(key, request.getParameter(key));
			}
		}
		
		final String DOMESTIC_PAYEE = "DOMESTIC";
		final String INTERNATIONAL_PAYEE = "INTERNATIONAL";
		final String SAMEBANK_PAYEE = "SAMEBANK";
		String payeeType = inputParams.get("payeeType");
		String userId = inputParams.get(InfinityConstants.userId);
		String legalEntityId = inputParams.get(InfinityConstants.legalEntityId);
		String contractCifList = inputParams.get("contractCifList");
		if (HelperMethods.isBlank(payeeType, userId, legalEntityId, contractCifList)) {
			return ErrorCodeEnum.ERR_12000.setErrorCode(result, "Mandatory fields missing. Please enter valid payeeType, userId, legalEntityId, contractCifList");
		}
		contractCifList = contractCifList.replaceAll("cif", "coreCustomerId");
		inputParams.put(InfinityConstants.cif, contractCifList);
		if (payeeType.equals(DOMESTIC_PAYEE)) {
			inputParams.put("isSameBankAccount", "false");
			inputParams.put("isInternationalAccount", "false");
			result = createInterBankPayeeOperation(inputParams, request, response);
		} else if (payeeType.equals(INTERNATIONAL_PAYEE)) {
			inputParams.put("isSameBankAccount", "false");
			inputParams.put("isInternationalAccount", "true");
			result = createInternationalPayeeOperation(inputParams, request, response);
		} else if (payeeType.equals(SAMEBANK_PAYEE)) {
			inputParams.put("isSameBankAccount", "true");
			inputParams.put("isInternationalAccount", "false");
			result = createIntraBankPayeeOperation(inputParams, request, response);
		} else {
			return ErrorCodeEnum.ERR_12000.setErrorCode(result, "Please enter valid payeeType. Valid Payee Types are: DOMESTIC, INTERNATIONAL, SAMEBANK");
		}
		if(result.hasParamByName(DBPConstants.DBP_ERROR_MESSAGE_KEY)) {
			return result;
		}
		String payeeId = result.getParamValueByName("payeeId");
		Result finalResult = new Result();
		finalResult.addStringParam("payeeId", payeeId);
		return finalResult;
	}

	private Result createInterBankPayeeOperation(Map<String, String> inputParams, DataControllerRequest request,
			DataControllerResponse response) {

		Result result = new Result();
		String userId = inputParams.get(InfinityConstants.userId);
		String legalEntityId = inputParams.get(InfinityConstants.legalEntityId);

		Map<String, List<String>> sharedCifMap = new HashMap<String, List<String>>();
		String sharedCifs = inputParams.get(InfinityConstants.cif);

		List<String> featureActions = new ArrayList<String>();
		featureActions.add(FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE_RECEPIENT);
		sharedCifMap = MigrationUtils.getContractCifMap(sharedCifs);
		boolean isAuthorized = DataFetchUtils.isUserAuthorizedForFeatureAction(featureActions, sharedCifMap, request.getHeaderMap(),
				request);
		if (!isAuthorized) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result, "Invalid User Id");
		}

		InterBankPayeeBackendDTO interBankPayeeBackendDTO = new InterBankPayeeBackendDTO();
		try {
			interBankPayeeBackendDTO = JSONUtils.parse(new JSONObject(inputParams).toString(),
					InterBankPayeeBackendDTO.class);
			interBankPayeeBackendDTO.setUserId(userId);
			interBankPayeeBackendDTO.setLegalEntityId(legalEntityId);
		} catch (IOException e) {
			logger.error("Error occured while fetching the input params: " + e);
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}

		if (!interBankPayeeBackendDTO.isValidInput()) {
			logger.error("Not a valid input");
			return ErrorCodeEnum.ERR_10710.setErrorCode(new Result());
		}

		if (StringUtils.isBlank(interBankPayeeBackendDTO.getBeneficiaryName())
				|| StringUtils.isBlank(interBankPayeeBackendDTO.getAccountNumber())
				|| (StringUtils.isBlank(interBankPayeeBackendDTO.getSwiftCode())
						&& StringUtils.isBlank(interBankPayeeBackendDTO.getRoutingNumber()))) {
			return ErrorCodeEnum.ERR_10348.setErrorCode(new Result());
		}

		if (!MigratePayeeCommonMethods.isUniquePayee(request, interBankPayeeBackendDTO, sharedCifMap)) {
			logger.error("Duplicate Payee or Error occured while checking for uniqueness.");
			return ErrorCodeEnum.ERR_12062.setErrorCode(new Result());
		}
		// Generating a Create Payee Transaction id
		String id = HelperMethods.getRandomNumericString(8);
		interBankPayeeBackendDTO.setPayeeId(id);

		TransactionStatusDTO payloadForValidateForApprovals = PayeeUtils
				.getPayloadForValidateForApprovalsForCreatePayee(interBankPayeeBackendDTO);
		try {
			result = MigratePayeeCommonMethods.createPayeeAfterApprovalInterBankPayee(request,
					payloadForValidateForApprovals.getAdditionalMetaInfo(), false);

		} catch (Exception e) {
			return ErrorCodeEnum.ERR_12053.setErrorCode(new Result());
		}
		return result;
	}

	private Result createInternationalPayeeOperation(Map<String, String> inputParams, DataControllerRequest request,
			DataControllerResponse response) {

		Result result = new Result();
		String userId = inputParams.get(InfinityConstants.userId);

		List<String> featureActions = new ArrayList<String>();
		featureActions.add(FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_CREATE_RECEPIENT);

		Map<String, List<String>> sharedCifMap = new HashMap<String, List<String>>();
		String sharedCifs = inputParams.get(InfinityConstants.cif);
		sharedCifMap = MigrationUtils.getContractCifMap(sharedCifs);
		boolean isAuthorized = DataFetchUtils.isUserAuthorizedForFeatureAction(featureActions, sharedCifMap, request.getHeaderMap(),
				request);
		if (!isAuthorized) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result, "Invalid User Id");
		}

		InternationalPayeeBackendDTO internationalPayeeBackendDTO = new InternationalPayeeBackendDTO();
		try {
			internationalPayeeBackendDTO = JSONUtils.parse(new JSONObject(inputParams).toString(),
					InternationalPayeeBackendDTO.class);
			internationalPayeeBackendDTO.setUserId(userId);
		} catch (IOException e) {
			logger.error("Error occured while fetching the input params: " + e);
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}

		if (!internationalPayeeBackendDTO.isValidInput()) {
			logger.error("Not a valid input");
			return ErrorCodeEnum.ERR_10710.setErrorCode(new Result());
		}

		if (StringUtils.isBlank(internationalPayeeBackendDTO.getBeneficiaryName())
				|| (StringUtils.isBlank(internationalPayeeBackendDTO.getIban())
						&& StringUtils.isBlank(internationalPayeeBackendDTO.getAccountNumber()))
				|| StringUtils.isBlank(internationalPayeeBackendDTO.getSwiftCode())) {
			return ErrorCodeEnum.ERR_10348.setErrorCode(new Result());
		}

		if (!MigratePayeeCommonMethods.isUniquePayee(request, internationalPayeeBackendDTO, sharedCifMap)) {
			logger.error("Duplicate Payee or Error occured while checking for uniqueness.");
			return ErrorCodeEnum.ERR_12062.setErrorCode(new Result());
		}

		String id = HelperMethods.getRandomNumericString(8);
		internationalPayeeBackendDTO.setPayeeId(id);

		TransactionStatusDTO payloadForValidateForApprovals = PayeeUtils
				.getPayloadForValidateForApprovalsForCreatePayee(internationalPayeeBackendDTO);
		try {
			result = MigratePayeeCommonMethods.createPayeeAfterApprovalInternationalPayee(request,
					payloadForValidateForApprovals.getAdditionalMetaInfo(), false);
		} catch (Exception e) {
			return ErrorCodeEnum.ERR_12053.setErrorCode(new Result());
		}
		return result;

	}

	private Result createIntraBankPayeeOperation(Map<String, String> inputParams, DataControllerRequest request,
			DataControllerResponse response) {

		Result result = new Result();
		String userId = inputParams.get(InfinityConstants.userId);

		List<String> featureActions = new ArrayList<String>();
		featureActions.add(FeatureAction.INTRA_BANK_FUND_TRANSFER_CREATE_RECEPIENT);

		Map<String, List<String>> sharedCifMap = new HashMap<String, List<String>>();
		String sharedCifs = inputParams.get(InfinityConstants.cif);

		sharedCifMap = MigrationUtils.getContractCifMap(sharedCifs);
		boolean isAuthorized = DataFetchUtils.isUserAuthorizedForFeatureAction(featureActions, sharedCifMap, request.getHeaderMap(),
				request);
		if (!isAuthorized) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result, "Invalid User Id");
		}

		IntraBankPayeeBackendDTO intraBankPayeeBackendDTO = new IntraBankPayeeBackendDTO();
		try {
			intraBankPayeeBackendDTO = JSONUtils.parse(new JSONObject(inputParams).toString(),
					IntraBankPayeeBackendDTO.class);
			intraBankPayeeBackendDTO.setUserId(userId);
		} catch (IOException e) {
			logger.error("Error occured while fetching the input params: " + e);
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}

		if (!intraBankPayeeBackendDTO.isValidInput()) {
			logger.error("Not a valid input");
			return ErrorCodeEnum.ERR_10710.setErrorCode(new Result());
		}

		if (StringUtils.isBlank(intraBankPayeeBackendDTO.getBeneficiaryName())
				|| StringUtils.isBlank(intraBankPayeeBackendDTO.getAccountNumber())) {
			return ErrorCodeEnum.ERR_10348.setErrorCode(new Result());
		}

		if (!MigratePayeeCommonMethods.isUniquePayee(request, intraBankPayeeBackendDTO, sharedCifMap)) {
			logger.error("Duplicate Payee or Error occured while checking for uniqueness.");
			return ErrorCodeEnum.ERR_12062.setErrorCode(new Result());
		}

		String id = HelperMethods.getRandomNumericString(8);
		intraBankPayeeBackendDTO.setPayeeId(id);
		TransactionStatusDTO payloadForValidateForApprovals = PayeeUtils
				.getPayloadForValidateForApprovalsForCreatePayee(intraBankPayeeBackendDTO);
		try {
			result = MigratePayeeCommonMethods.createPayeeAfterApprovalIntraBankPayee(request,
					payloadForValidateForApprovals.getAdditionalMetaInfo(), false);

		} catch (Exception e) {
			return ErrorCodeEnum.ERR_12053.setErrorCode(new Result());
		}
		return result;
	}
}

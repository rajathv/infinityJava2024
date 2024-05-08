package com.temenos.auth.usermanagement.resource.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.LegalEntityUtil;
import com.temenos.auth.admininteg.operation.GetLegalEntitiesOperation;
import com.temenos.auth.usermanagement.businessdelegate.api.AuthUserManagementBusinessDelegate;
import com.temenos.auth.usermanagement.resource.api.AuthUserManagementResource;

public class AuthUserManagementResourceImpl implements AuthUserManagementResource {
	
	private static final Logger LOG = LogManager
			.getLogger(AuthUserManagementResourceImpl.class);

	/**
	 * 1) Fetches all legal entities from Spotlight.
	 * 2) Fetches active legal entities from database.
	 * 3) Populate legal entity details like description, region etc for the response.
	 */
	@Override
	public Result getCustomerActiveLegalEntities(String methodId, Object[] inputArray,
			DataControllerRequest requestInstance, DataControllerResponse responseInstance) 
			throws ApplicationException {
		try {
			
			/**
			 * Fetches all legal entities from Spotlight.
			 */
			Result allLegalEntities = getAllLegalEntities(methodId, inputArray,
					requestInstance, responseInstance);
			
			/**
			 * Fetches active legal entities from database.
			 */
			String customerId = HelperMethods.getCustomerIdFromSession(requestInstance);
			AuthUserManagementBusinessDelegate authBusinsesDelegate = DBPAPIAbstractFactoryImpl
					.getInstance().getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(AuthUserManagementBusinessDelegate.class);
			Result customerLegalEntity = authBusinsesDelegate.getCustomerActiveLegalEntities(customerId);
			
			/**
			 * Populate legal entity details like description, region etc for the response.
			 */
			if (HelperMethods.hasRecords(allLegalEntities)) {
				List<Record> customerLegalEntityRecords = customerLegalEntity
						.getDatasetById("customerlegalentity").getAllRecords();
				Set<String> customerLegalEntitySet = new HashSet<>();
				for(Record s : customerLegalEntityRecords) {
					customerLegalEntitySet.add(s.getParamValueByName("legalEntityId"));
				}
				Iterator<Record> allLegalEntitiesITR = allLegalEntities
						.getAllDatasets().get(0).getAllRecords().iterator();
				Result returnResult = new Result();
				Dataset returnDs = new Dataset();
				returnDs.setId("customerlegalentity");
				returnResult.addDataset(returnDs);
				Record temp = null;
				String leid = "";
				while(allLegalEntitiesITR.hasNext()) {
					temp = allLegalEntitiesITR.next();
					leid = temp.getParamValueByName("id");
					if(customerLegalEntitySet.contains(leid)) {
						returnDs.addRecord(temp);
					}
				}
				return returnResult;
			}
			LOG.debug("Spotlight has returned empty response.");
			
		}catch(ApplicationException e) {
			LOG.error("Exception", e);
			throw e;
		} catch(Exception e) {
			LOG.error("Exception",e);
			throw new ApplicationException(ErrorCodeEnum.ERR_10220);
		}
		return ErrorCodeEnum.ERR_10220.setErrorCode(new Result());
	}

	/**
	 * fetches all legal entities from spotlight
	 */
	@Override
	public Result getAllLegalEntities(String methodId, Object[] inputArray, DataControllerRequest requestInstance,
			DataControllerResponse responseInstance) throws Exception {
		return (Result) new GetLegalEntitiesOperation().invoke(methodId, inputArray,
				requestInstance, responseInstance);
	}

	/**
	 * fetches all features and permissions for the loggedin user and current legal entity id
	 */
	@Override
	public Result getCustomerFeatureAndPermissions(String methodId, Object[] inputArray,
			DataControllerRequest requestInstance, DataControllerResponse responseInstance) throws Exception {
		
		try {
			AuthUserManagementBusinessDelegate businessDelegate =
			        DBPAPIAbstractFactoryImpl.getBusinessDelegate(AuthUserManagementBusinessDelegate.class);
			String cacheKey = LegalEntityUtil.getCacheKeyForCurrentLegalEntityFeaturePermissions(requestInstance);
			Map<String, String> userInfo = HelperMethods.getUserFromIdentityService(requestInstance);
			String leId = LegalEntityUtil.getLegalEntityIdFromSessionOrCache(requestInstance);
			return businessDelegate.getCustomerFeatureAndPermissions(leId, cacheKey, userInfo);
		} catch (ApplicationException e) {
			LOG.error("Exception while fetching permissions", e);
			throw e;
		}
	}

}

package com.temenos.dbx.product.signatorygroupservices.businessdelegate.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.commons.backenddelegate.api.UserBackendDelegate;
import com.temenos.dbx.product.commons.dto.CustomerDTO;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.dto.ContractCustomersDTO;
import com.temenos.dbx.product.signatorygroupservices.backenddelegate.api.SignatoryGroupBackendDelegate;
import com.temenos.dbx.product.signatorygroupservices.businessdelegate.api.SignatoryGroupBusinessDelegate;
import com.temenos.dbx.product.signatorygroupservices.dto.CustomerSignatoryGroupDTO;
import com.temenos.dbx.product.signatorygroupservices.dto.SignatoryGroupDTO;
import com.temenos.dbx.product.signatorygroupservices.dto.SignatoryGroupRequestMatrixDTO;
import com.temenos.infinity.api.commons.exception.ApplicationException;


/**
 * 
 * @author 
 * @version 1.0 Extends the {@link SignatoryGroupBusinessDelegate}
 */
public  class SignatoryGroupBusinessDelegateImpl implements SignatoryGroupBusinessDelegate {
	
	private static final Logger LOG = LogManager.getLogger(SignatoryGroupBusinessDelegate.class);

	@Override
	public List<SignatoryGroupDTO> getSignatoryUsers(String coreCustomerId, String contractId, Map<String, Object> headersMap, DataControllerRequest request){
	  try {
		SignatoryGroupBackendDelegate backendDelegate =
                DBPAPIAbstractFactoryImpl.getBackendDelegate(SignatoryGroupBackendDelegate.class);
		List<SignatoryGroupDTO> signatoryGroup = backendDelegate.getSignatoryUsers(coreCustomerId, contractId, headersMap, request);
        return signatoryGroup;
        
	  } catch (Exception e) {
          LOG.error("Error while calling the microservice :"+e);
          return null;
      } 
	}

	@Override
	public List<SignatoryGroupDTO> fetchSignatoryGroups(String coreCustomerId,String contractId, Map<String, Object> headersMap) 
		throws ApplicationException {
			 try {
	        	SignatoryGroupBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
						.getBackendDelegate(SignatoryGroupBackendDelegate.class);
	        	List<SignatoryGroupDTO> signatoryGroups = backendDelegate.fetchSignatoryGroups(coreCustomerId,contractId,headersMap);
		        return signatoryGroups;
	    	  } catch (Exception e) {
	              LOG.error("Error while fetching signatory groups :"+e);
	              return null;
	          }
  }

	@Override
	public List<SignatoryGroupDTO> fetchSignatoryDetails(String signatoryGroupId, Map<String, Object> headersMap) {
		 try {
	        	SignatoryGroupBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(SignatoryGroupBackendDelegate.class);
	        	
	        	List<SignatoryGroupDTO> signatories = backendDelegate.fetchSignatoryDetails(signatoryGroupId);
				return signatories;
	    	  } catch (Exception e) {
	              LOG.error("Error while fetching signatory group details :"+e);
	              return null;
	          }	}
	
	@Override
	public SignatoryGroupDTO fetchSignatoryGroupDetails(String signatoryGroupId, Map<String, Object> headersMap) {
		 try {
	        	SignatoryGroupBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(SignatoryGroupBackendDelegate.class);
	        	SignatoryGroupDTO signatory = backendDelegate.fetchSignatoryGroupDetails(signatoryGroupId, headersMap);
				return signatory;
	    	  } catch (Exception e) {
	              LOG.error("Error while fetching signatory group details :"+e);
	              return null;
	          }	}

	@Override
	public JSONObject createSignatoryGroup(String signatoryGroupName, String signatoryGroupDescription,
			String coreCustomerId, String contractId, JSONArray signatories,String createdby) {
		SignatoryGroupBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(SignatoryGroupBackendDelegate.class);
		
		JSONObject resultJSON = null;
		StringBuilder signatoryGroupValues = new StringBuilder();
		String signatoryGroupId = UUID.randomUUID().toString();
		signatoryGroupValues.append("\"").append(signatoryGroupId).append("\"").append(";");
		signatoryGroupValues.append("\"").append(signatoryGroupName).append("\"").append(";");
		signatoryGroupValues.append("\"").append(signatoryGroupDescription).append("\"").append(";");
		signatoryGroupValues.append("\"").append(coreCustomerId).append("\"").append(";");
		signatoryGroupValues.append("\"").append(contractId).append("\"").append(";");
		signatoryGroupValues.append("\"").append(createdby).append("\"").append(",");
        
		StringBuilder customerSignatoryGroupValues = new StringBuilder();
		String customerSignatoryGroupId = null;
		String customerId = null;
		for(int i =0;i<signatories.length();i++) {
			customerSignatoryGroupId = UUID.randomUUID().toString();
			customerId = signatories.getJSONObject(i).get(Constants.CUSTOMERID).toString();
			customerSignatoryGroupValues.append("\"").append(customerSignatoryGroupId).append("\"").append(";");
			customerSignatoryGroupValues.append("\"").append(signatoryGroupId).append("\"").append(";");
			customerSignatoryGroupValues.append("\"").append(customerId).append("\"").append(";");
			customerSignatoryGroupValues.append("\"").append(createdby).append("\"").append(",");
		}
		try {
			resultJSON = backendDelegate.createSignatoryGroup(signatoryGroupValues.toString(),customerSignatoryGroupValues.toString());
		}catch(Exception e) {
			 LOG.error("Error while creating signatory group :"+e);
             return null;
		}
		if(!resultJSON.has(Constants.ERRMSG)) {
		    	   resultJSON.put(Constants.SIGNATORYGROUPID,signatoryGroupId);
		 }
		return resultJSON;
	}

	@Override
	public boolean updateSignatoryGroupRequestMatrix(SignatoryGroupRequestMatrixDTO sigGrpReqMatrixDTO) {
		List<SignatoryGroupRequestMatrixDTO> sigGrpReqMatrixDTOList = null;
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_SIGNATORYGROUPREQUESTMATRIX_UPDATE;
		
		Map<String, Object> requestParams;
		try {
			requestParams = JSONUtils.parseAsMap(new JSONObject(sigGrpReqMatrixDTO).toString(), String.class, Object.class);
		} catch (IOException e) {
			LOG.error("Error occured while parsing the input params in updateSignatoryGroupRequestMatrix method " + e);
			return false;
		}
		boolean isApproved = sigGrpReqMatrixDTO.isApproved();
		requestParams.put("isApproved", isApproved);
		
		try {
			String updateResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject jsonRsponse = new JSONObject(updateResponse);
			JSONArray jsonArray = CommonUtils.getFirstOccuringArray(jsonRsponse);
			sigGrpReqMatrixDTOList = JSONUtils.parseAsList(jsonArray.toString(), SignatoryGroupRequestMatrixDTO.class);
		}
		
		catch(JSONException jsonExp) {
			LOG.error("JSONException occured while updating the signatorygrouprequestmatrix" + jsonExp);
			return false;
		}
		catch(Exception exp) {
			LOG.error("Exception occured while updating the signatorygrouprequestmatrix" + exp);
			return false;
		}
		
		if(sigGrpReqMatrixDTOList != null && sigGrpReqMatrixDTOList.size() != 0)
			return true;
		
		return false;
	}
	
	@Override
	public List<SignatoryGroupRequestMatrixDTO> fetchPendingSignatoryGroupRequestMatrix(String requestId) {
		List<SignatoryGroupRequestMatrixDTO> sigGrpReqMatrixDTOList = null;
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_SIGNATORYGROUPREQUESTMATRIX_GET;
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
        String filter = "requestId" + DBPUtilitiesConstants.EQUAL + requestId + DBPUtilitiesConstants.AND + 
        		"isApproved" + DBPUtilitiesConstants.EQUAL + false;
		
        requestParams.put(DBPUtilitiesConstants.FILTER, filter);
        
		try {
			String fetchResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject jsonRsponse = new JSONObject(fetchResponse);
			JSONArray jsonArray = CommonUtils.getFirstOccuringArray(jsonRsponse);
			sigGrpReqMatrixDTOList = JSONUtils.parseAsList(jsonArray.toString(), SignatoryGroupRequestMatrixDTO.class);
		}
		
		catch(JSONException jsonExp) {
			LOG.error("JSONException occured while fetching pending signatorygrouprequestmatrix" + jsonExp);
			return null;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while fetching pending signatorygrouprequestmatrix" + exp);
			return null;
		}
		
		return sigGrpReqMatrixDTOList;
	}
	
	@Override
	public List<CustomerSignatoryGroupDTO> fetchCustomerSignatoryGroups(String customerId) {
		List<CustomerSignatoryGroupDTO> customerSignatoryGroupDTOs = new ArrayList<CustomerSignatoryGroupDTO>();

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_FETCH_CUSTOMERSIGNATORYGROUP_DETAILS_PROC;
        
        Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("_customerId", customerId);

		try {
			String response = DBPServiceExecutorBuilder.builder().
	        		withServiceId(serviceName).
	        		withObjectId(null).
	        		withOperationId(operationName).
	        		withRequestParameters(requestParams).
	        		build().getResponse();
			JSONObject jsonResponse = new JSONObject(response);
			JSONArray jsonArray = CommonUtils.getFirstOccuringArray(jsonResponse);
			if (jsonArray != null) {
				customerSignatoryGroupDTOs = JSONUtils.parseAsList(jsonArray.toString(),
						CustomerSignatoryGroupDTO.class);
			}
			
		}
		
		catch(JSONException jsonExp) {
			LOG.error("JSONException occured while fetchCustomerSignatoryGroupProc" + jsonExp);
			return null;
		}
		catch(Exception exp) {			
			LOG.error("Exception occured while fetchCustomerSignatoryGroupProc" + exp);
			return null;
		}
		
		return customerSignatoryGroupDTOs;
	}

	@Override
	public List<CustomerSignatoryGroupDTO> fetchCustomerSignatoryGroupsAssociatedWithRequest(String customerId, String requestId) {
		List<CustomerSignatoryGroupDTO> customerSignatoryList = new ArrayList<>();

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_FETCH_SIGNATORYGROUP_FOR_CUSTOMER_AND_REQUEST_PROC;

		Map<String, Object> reqParams = new HashMap<>();
		reqParams.put("_customerId", customerId);
		reqParams.put("_requestId", requestId);

		CustomerSignatoryGroupDTO temp = new CustomerSignatoryGroupDTO();

		try{
			String response = DBPServiceExecutorBuilder.builder().withServiceId(serviceName)
					.withObjectId(null)
					.withOperationId(operationName)
					.withRequestParameters(reqParams)
					.withRequestHeaders(null)
					.build().getResponse();
			JSONObject responseObj = new JSONObject(response);
			JSONArray responseArr = responseObj.getJSONArray("records");
			if(responseArr.length() == 0){
				return null;
			}
			else{
				customerSignatoryList = JSONUtils.parseAsList(responseArr.toString(), CustomerSignatoryGroupDTO.class);
				return customerSignatoryList;
			}
		} catch(Exception e){
			return null;
		}
	}
	
	@Override
	public JSONObject updateSignatoryGroup(String signatoryGroupId,String signatoryGroupName, String signatoryGroupDescription,
			String coreCustomerId, String contractId, JSONArray signatories,String createdby) {
		SignatoryGroupBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(SignatoryGroupBackendDelegate.class);
		
		JSONObject resultJSON = null;
		StringBuilder signatoryGroupValues = new StringBuilder();
		signatoryGroupValues.append("\"").append(signatoryGroupId).append("\"").append(";");
		signatoryGroupValues.append("\"").append(signatoryGroupName).append("\"").append(";");
		signatoryGroupValues.append("\"").append(signatoryGroupDescription).append("\"").append(";");
		signatoryGroupValues.append("\"").append(createdby).append("\"").append(",");
        
		StringBuilder deleteSigValues = new StringBuilder();
		StringBuilder newSigValues = new StringBuilder();
		String customerSignatoryGroupId = null;
		String customerId = null;
		
		for(int i =0;i<signatories.length();i++) {
			if(!signatories.getJSONObject(i).has("isUserRemoved")) {
				customerSignatoryGroupId = UUID.randomUUID().toString();
				customerId = signatories.getJSONObject(i).get(Constants.CUSTOMERID).toString();
				newSigValues.append("\"").append(customerSignatoryGroupId).append("\"").append(";");
				newSigValues.append("\"").append(signatoryGroupId).append("\"").append(";");
				newSigValues.append("\"").append(customerId).append("\"").append(";");
				newSigValues.append("\"").append(createdby).append("\"").append(",");
			} else if(signatories.getJSONObject(i).get("isUserRemoved").toString().equalsIgnoreCase("true")) {
				customerId = signatories.getJSONObject(i).get(Constants.CUSTOMERID).toString();	
				deleteSigValues.append("\"").append(signatoryGroupId).append("\"").append(";");
				deleteSigValues.append("\"").append(customerId).append("\"").append(",");	
			}
		}
		if(deleteSigValues.length() > 0) {
		deleteSigValues.deleteCharAt(deleteSigValues.length() - 1);		
		}
		if(newSigValues.length() > 0) {
		newSigValues.deleteCharAt(newSigValues.length() - 1);
		}
		signatoryGroupValues.deleteCharAt(signatoryGroupValues.length() - 1);
		try {
			resultJSON = backendDelegate.updateSignatoryGroup(signatoryGroupValues.toString(),newSigValues.toString(), deleteSigValues.toString());
		}catch(Exception e) {
			 LOG.error("Error while creating signatory group :"+e);
             return null;
		}
		if(!resultJSON.has(Constants.ERRMSG)) {
		    	   resultJSON.put(Constants.SIGNATORYGROUPID,signatoryGroupId);
		 }
		return resultJSON;
	}
	
		@Override
	 public boolean hasSignatoryGroupAccesstoUser(String sigGroupId, String coreCustomerId,String contractId,Map<String, Object> headersMap) {
	     
	     List<SignatoryGroupDTO> resultDTO = null;
	 String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
	 String operationName = OperationName.DB_SIGNATORYGROUP_GET;
	 try {
	  Map<String,Object> requestParams = new HashMap <String,Object>();
	        
	        StringBuilder filterQuery = new StringBuilder();
	         filterQuery.append(Constants.CORECUSTOMERID).append(DBPUtilitiesConstants.EQUAL).append(coreCustomerId)
	  .append(DBPUtilitiesConstants.AND).append(Constants.CONTRACTID).append(DBPUtilitiesConstants.EQUAL)
	  .append(contractId).append(DBPUtilitiesConstants.AND).append(Constants.SIGNATORYGROUPID).append(DBPUtilitiesConstants.EQUAL)
	  .append(sigGroupId);
	         requestParams.put(DBPUtilitiesConstants.FILTER, filterQuery.toString());
	         String getResponse = DBPServiceExecutorBuilder.builder().
	    withServiceId(serviceName).
	    withObjectId(null).
	    withOperationId(operationName).
	    withRequestParameters(requestParams).
	    build().getResponse();
	  JSONObject responseObj = new JSONObject(getResponse);
	  if(responseObj.getInt(Constants.OPSTATUS) == 0 && responseObj.getInt(Constants.HTTP_STATUS) == 0) {
	      JSONArray signatoryGroups  = responseObj.getJSONArray(Constants.SIGNATORYGROUP);
	               for(int i=0;i<signatoryGroups.length();i++) {
	                JSONObject jsonObj = signatoryGroups.getJSONObject(i);
	                if(jsonObj.get("signatoryGroupId").toString().equals(sigGroupId)) {
	                 return true;
	                }
	               }
	  }
	  return false;
	 }
	 catch(JSONException e) {
	  LOG.error("Exception occured while fetching signatory groups", e);
	  return false;
	 }catch (Exception e) {
	  LOG.error("Exception occured while fetching signatory groups", e);
	  return false;
	 }
	
 }
 @Override
    public boolean isSignatoryAlreadyPresentInAnotherGroup(String customerId, String coreCustomerId, String contractId, Map<String, Object> headersMap){
      try {
        SignatoryGroupBackendDelegate backendDelegate =
                DBPAPIAbstractFactoryImpl.getBackendDelegate(SignatoryGroupBackendDelegate.class);
        List<SignatoryGroupDTO> signatoryGroups = backendDelegate.fetchSignatoryGroups(coreCustomerId, contractId, headersMap);
        String signatoryGroupsStr = JSONUtils.stringifyCollectionWithTypeInfo(signatoryGroups, SignatoryGroupDTO.class);
        JSONArray signatoryGroupsArr = new JSONArray(signatoryGroupsStr);
         if (signatoryGroupsArr.length() <= 0) {
             return false;
         }else {
             JSONObject signatoryGroup ;
             JSONObject signatory;
             String sigCustId;
             List<SignatoryGroupDTO> signatories = null;
             String signatoriesStr = null;
             String signatoryGroupId = null;
             JSONArray signatoryArr = null;
             for(int i=0;i<signatoryGroupsArr.length();i++) {
            	 signatoryGroupId = signatoryGroupsArr.getJSONObject(i).has(Constants.SIGNATORYGROUPID)? signatoryGroupsArr.getJSONObject(i).get(Constants.SIGNATORYGROUPID).toString(): null;
            	 if(signatoryGroupId != null) {
	            	 signatories = backendDelegate.fetchSignatories(signatoryGroupId,headersMap);
	 		         signatoriesStr = JSONUtils.stringifyCollectionWithTypeInfo(signatories, SignatoryGroupDTO.class);
	 		         signatoryArr = new JSONArray(signatoriesStr);
	 		        for(int j=0;j<signatoryArr.length();j++) {
	 		        	 signatory = signatoryArr.getJSONObject(j);
	 		        	 sigCustId = signatory.get(Constants.CUSTOMERID).toString();
	 	                 if(sigCustId.equalsIgnoreCase(customerId)) {
	 	                    return true;
	 	                 }
	 		        }
            	 }
             }
         }
         return false;
       
      } catch (Exception e) {
          LOG.error("Error while calling the microservice :"+e);
          return false;
      }
    }
	@Override
    public boolean isGroupNameDuplicate(String groupName, String coreCustomerId, String contractId, Map<String, Object> headersMap){
      try {
        SignatoryGroupBackendDelegate backendDelegate =
                DBPAPIAbstractFactoryImpl.getBackendDelegate(SignatoryGroupBackendDelegate.class);
        List<SignatoryGroupDTO> signatoryGroups = backendDelegate.fetchSignatoryGroups(coreCustomerId, contractId, headersMap);
        String signatoryGroupsStr = JSONUtils.stringifyCollectionWithTypeInfo(signatoryGroups, SignatoryGroupDTO.class);
         JSONArray signatoryGroupsArr = new JSONArray(signatoryGroupsStr);
         if (signatoryGroupsArr.length() <= 0) {
             return false;
         }else {
             JSONObject signatoryGroup ;
             String groupNameStr;
             for(int i=0;i<signatoryGroupsArr.length();i++) {
                 signatoryGroup = signatoryGroupsArr.getJSONObject(i);
                 groupNameStr = signatoryGroup.has(Constants.SIGNATORYGROUPNAME)? signatoryGroup.get(Constants.SIGNATORYGROUPNAME).toString() :null;
                 if(groupNameStr!=null && groupNameStr.equalsIgnoreCase(groupName)) {
                     return true;
                 }
             }
         }
         return false;
       
      } catch (Exception e) {
          LOG.error("Error while calling the microservice :"+e);
          return false;
      }
    }

	@Override
	public boolean checkContractCorecustomer(String contractId, String coreCustomerId, String userId, Map<String, Object> headersMap) {
		try{
			SignatoryGroupBackendDelegate backendDelegate= DBPAPIAbstractFactoryImpl.getBackendDelegate(SignatoryGroupBackendDelegate.class);
			return backendDelegate.checkContractCorecustomer(contractId,coreCustomerId,userId,headersMap);
		}catch(Exception e) {
			LOG.error("Error while calling the microservice :"+e);
	        return false;
		}
	}

	@Override
	public JSONArray getCorecustomersForUser(String userId, Map<String, Object> headersMap) {
		try{
			SignatoryGroupBackendDelegate backendDelegate= DBPAPIAbstractFactoryImpl.getBackendDelegate(SignatoryGroupBackendDelegate.class);
			List<ContractCustomersDTO> dtolist= backendDelegate.getCorecustomersForUser(userId,headersMap);
			if (dtolist!=null) {
				JSONArray customers = new JSONArray();
				dtolist.forEach((dto)->{
					customers.put(dto.getCoreCustomerId());
				});
				return customers;
			}
		}catch(Exception e) {
			LOG.error("Error while calling the microservice :"+e);
	        return null;
		}
		return null;
	}

	public boolean deleteSignatoryGroup(String signatoryGroupId, Map<String, Object> headersMap) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_SIGNATORYGROUP_DELETE;
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("signatoryGroupId", signatoryGroupId);
		
		try {
			String deleteResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject jsonRsponse = new JSONObject(deleteResponse);
			if(jsonRsponse.getInt("opstatus") == 0 && jsonRsponse.getInt("httpStatusCode") == 0 && jsonRsponse.getInt("deletedRecords") == 1) {
				return true;
			}
		}
		
		catch(JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while deleting the signatory group",jsonExp);
			return false;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while deleting the signatory group",exp);
			return false;
		}
		
		return false;	
	}
	
	@Override
	public List<SignatoryGroupDTO> fetchSignatoryGroupCustomers(String signatoryGroupId) {
		List<SignatoryGroupDTO> signatoryGroupCustomerDTOs = new ArrayList<SignatoryGroupDTO>();

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_FETCH_SIGNATORYGROUP_CUSTOMER_DETAILS_PROC;
        
        Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("_signatoryGroupId", signatoryGroupId);

		try {
			String response = DBPServiceExecutorBuilder.builder().
	        		withServiceId(serviceName).
	        		withObjectId(null).
	        		withOperationId(operationName).
	        		withRequestParameters(requestParams).
	        		build().getResponse();
			JSONObject jsonResponse = new JSONObject(response);
			JSONArray jsonArray = CommonUtils.getFirstOccuringArray(jsonResponse);
			if (jsonArray != null) {
				signatoryGroupCustomerDTOs = JSONUtils.parseAsList(jsonArray.toString(), SignatoryGroupDTO.class);
			}
			
		}
		
		catch(JSONException jsonExp) {
			LOG.error("JSONException occured while fetchCustomerSignatoryGroupProc" + jsonExp);
			return null;
		}
		catch(Exception exp) {			
			LOG.error("Exception occured while fetchCustomerSignatoryGroupProc" + exp);
			return null;
		}
		
		return signatoryGroupCustomerDTOs;
	}

	@Override
	public boolean isEligibleForDelete(String signatoryGroupId) {
		try{
			SignatoryGroupBackendDelegate backendDelegate= DBPAPIAbstractFactoryImpl.getBackendDelegate(SignatoryGroupBackendDelegate.class);
			boolean hasPendingApproval = backendDelegate.isGroupPartOfPendingTransaction(signatoryGroupId);
			boolean isApprovalgroup = backendDelegate.isGroupPartOfApprovalRule(signatoryGroupId);
			if(hasPendingApproval || isApprovalgroup) {
				return false;
			}
		}catch(Exception e) {
			LOG.error("Error while calling the microservice :"+e);
	        return false;
		}
		return true;
	}

	@Override
	public boolean updateSignatoryGroupForInfinityUser(String cif, String contractId, String customerId, String signatoryGroupId) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_UPDATE_SIGNATORYGROUP_FORUSER_PROC;

        String customerIds=customerId.replace("\"","");
		Map<String, Object> requestParams =  new HashMap<String, Object>();
		requestParams.put("_coreCustomerId", cif);
		requestParams.put("_contractId", contractId);
		requestParams.put("_customerId", customerIds);
		requestParams.put("_signatorygroupId", signatoryGroupId);
		
		String response = null;
		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject responseObj = new JSONObject(response);
			if((responseObj != null) && responseObj.has(Constants.OPSTATUS) && responseObj.getInt(Constants.OPSTATUS) == 0 && responseObj.has(Constants.HTTP_STATUS)) {
				return true;
			}
		} catch (JSONException e) {
			LOG.error("Caught exception at updateSignatoryGroupForInfinityUser method: " + e);
			return false;
		} catch (Exception e) {
			LOG.error("Caught exception at updateSignatoryGroupForInfinityUser method: " + e);
			return false;
		}

		return false;
	}  

}

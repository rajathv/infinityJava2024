package com.kony.task.datavalidation;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.memorymgmt.UserDetailsManager;
import com.kony.scaintegration.helper.Helper;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class ValidateUpdateUserDetailsTask implements ObjectProcessorTask {
	
	String PARAM_ADDRESSES = "Addresses";
	String PARAM_EMAILIDS = "EmailIds";
	String PARAM_CONTACTNUMBERS = "ContactNumbers";
	String PARAM_PHONE_NUMBERS = "phoneNumbers";
	String PARAM_DELETE_ADDRESS_ID = "deleteAddressID";
	String PARAM_DELETE_COMM_ID = "deleteCommunicationID";
	String PARAM_COMM_TYPE = "communicationType";
	String PARAM_USERNAME = "userName";
	String PARAM__USERNAME = "UserName";
	
	private static final Logger LOG = LogManager.getLogger(ValidateUpdateUserDetailsTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		if(!HelperMethods.isDACEnabled()) {
			LOG.debug("data access control is disabled");
			return true;
		}
		if(HelperMethods.isAdminUser(fabricRequestManager)) {
			LOG.debug("by passing check for admin user");
			return true;
		}
		UserDetailsManager manager = new UserDetailsManager(fabricRequestManager);
		JsonElement payloadAsJsonElement = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
		if(payloadAsJsonElement == null || payloadAsJsonElement.isJsonNull()) {
			LOG.debug("empty payload received. Nothing to valiadte.");
			return true;
		}
		JsonObject requestPayload = payloadAsJsonElement.getAsJsonObject();
		
		if(HelperMethods.isMFAVerify(requestPayload) || Helper.isScaVerify(requestPayload)) {
			LOG.debug("This is MFA verification call");
			return true;
		}
		
		JsonObject userDetails = JSONUtil.parseAsJsonObject(manager.getUserDetailsFromSession());
		
		if(validateUsername(requestPayload, userDetails) &&
				validatePhonenumberUpdate(requestPayload, userDetails) &&
				validateEmailidUpdate(requestPayload, userDetails) &&
				validateAddressUpdate(requestPayload, userDetails)) {
			return true;
		}
		JsonObject resPayload = null;
        if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
            resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        }
        resPayload = ErrorCodeEnum.ERR_12403.setErrorCode(resPayload);
        fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
		return false;
	}
	
	public boolean validateUsername(JsonObject requestPayload, JsonObject userDetails) {
		String cachedUserName = JSONUtil.getString(userDetails, PARAM_USERNAME);
		String userNameInReq = "";
		if(requestPayload.has(PARAM_USERNAME)) {
			userNameInReq = JSONUtil.getString(requestPayload, PARAM_USERNAME);
		}
		if(requestPayload.has(PARAM__USERNAME)) {
			userNameInReq = JSONUtil.getString(requestPayload, PARAM__USERNAME);
		}
			
		if(StringUtils.isNotBlank(userNameInReq)) {
			LOG.debug("comparing the id of username for update : {}",
						(userNameInReq.equals(cachedUserName)));
			return userNameInReq.equals(cachedUserName);
		}
		LOG.debug("request doesnt have username.");
		return true;
	}
	
	public boolean validatePhonenumberUpdate(JsonObject requestPayload, JsonObject userDetails) {
		String communicationType = JSONUtil.getString(requestPayload, PARAM_COMM_TYPE);
		if(requestPayload.has(PARAM_PHONE_NUMBERS)) {
			JsonArray phoneNumbers = null;
			if(requestPayload.get(PARAM_PHONE_NUMBERS).isJsonPrimitive()) {
				phoneNumbers = JSONUtil.parseAsJsonArray(requestPayload.get(PARAM_PHONE_NUMBERS).getAsString());
			} else {
				phoneNumbers = JSONUtil.getJsonArrary(requestPayload, PARAM_PHONE_NUMBERS);
			}
			LOG.debug("phoneNumbers {}", phoneNumbers);
			JsonObject phoneNumber = phoneNumbers.get(0).getAsJsonObject();
			String id = JSONUtil.getString(phoneNumber, "id");
			if(StringUtils.isNotBlank(id)) {
				JsonArray cachedPhoneNumbers = JSONUtil.getJsonArrary(userDetails, PARAM_CONTACTNUMBERS);
				LOG.debug("cachedPhoneNumbers {}", cachedPhoneNumbers);
				return isValueFound(cachedPhoneNumbers, "id", id);
			}
			LOG.debug("Trying to add new phoneNumber.");
		} else if(requestPayload.has(PARAM_DELETE_COMM_ID) &&
				PARAM_PHONE_NUMBERS.equalsIgnoreCase(communicationType)) {
			String id = JSONUtil.getString(requestPayload, PARAM_DELETE_COMM_ID);
			JsonArray cachedPhoneNumbers = JSONUtil.getJsonArrary(userDetails, PARAM_CONTACTNUMBERS);
			LOG.debug("cachedPhoneNumbers {}", cachedPhoneNumbers);
			return isValueFound(cachedPhoneNumbers, "id", id);
		}
		LOG.debug("request is not on phoneNumber create/update/delete.");
		return true;
	}
	
	public boolean validateEmailidUpdate(JsonObject requestPayload, JsonObject userDetails) {
		String communicationType = JSONUtil.getString(requestPayload, PARAM_COMM_TYPE);
		if(requestPayload.has(PARAM_EMAILIDS)) {
			JsonArray emailIds = null;
			if(requestPayload.get(PARAM_EMAILIDS).isJsonPrimitive()) {
				emailIds = JSONUtil.parseAsJsonArray(requestPayload.get(PARAM_EMAILIDS).getAsString());
			} else {
				emailIds = JSONUtil.getJsonArrary(requestPayload, PARAM_EMAILIDS);
			}
			LOG.debug("emailIds {}", emailIds);
			JsonObject emailId = emailIds.get(0).getAsJsonObject();
			String id = JSONUtil.getString(emailId, "id");
			if(StringUtils.isNotBlank(id)) {
				JsonArray cachedEmailIds = JSONUtil.getJsonArrary(userDetails, PARAM_EMAILIDS);
				LOG.debug("cachedEmailIds {}", cachedEmailIds);
				return isValueFound(cachedEmailIds, "id", id);
			}
			LOG.debug("Trying to add new emaild.");
		} else if(requestPayload.has(PARAM_DELETE_COMM_ID) &&
				PARAM_EMAILIDS.equalsIgnoreCase(communicationType)) {
			String id = JSONUtil.getString(requestPayload, PARAM_DELETE_COMM_ID);
			JsonArray cachedEmailIds = JSONUtil.getJsonArrary(userDetails, PARAM_EMAILIDS);
			LOG.debug("cachedEmailIds {}", cachedEmailIds);
			return isValueFound(cachedEmailIds, "id", id);
		}
		LOG.debug("request is not on emaild create/update/delete.");
		return true;
	}

	public boolean validateAddressUpdate(JsonObject requestPayload, JsonObject userDetails) {
		if(requestPayload.has(PARAM_ADDRESSES)) {
			JsonArray addresses = null;
			if(requestPayload.get(PARAM_ADDRESSES).isJsonPrimitive()) {
				addresses = JSONUtil.parseAsJsonArray(requestPayload.get(PARAM_ADDRESSES).getAsString());
			} else {
				addresses = JSONUtil.getJsonArrary(requestPayload, PARAM_ADDRESSES);
			}
			LOG.debug("addresses {}", addresses);
			JsonObject address = addresses.get(0).getAsJsonObject();
			String id = JSONUtil.getString(address, "id");
			if(StringUtils.isNotBlank(id)) {
				JsonArray cachedAddresss = JSONUtil.getJsonArrary(userDetails, PARAM_ADDRESSES);
				LOG.debug("cachedAddresss {}", cachedAddresss);
				return isValueFound(cachedAddresss, "id", id);
			}
			LOG.debug("Trying to add new address.");
		} else if(requestPayload.has(PARAM_DELETE_ADDRESS_ID)) {
			String id = JSONUtil.getString(requestPayload, PARAM_DELETE_ADDRESS_ID);
			JsonArray cachedAddresss = JSONUtil.getJsonArrary(userDetails, PARAM_ADDRESSES);
			LOG.debug("cachedAddresss {}", cachedAddresss);
			return isValueFound(cachedAddresss, "Address_id", id);
		}
		LOG.debug("request is not on address create/update/delete.");
		return true;
	}
	
	private boolean isValueFound(JsonArray array, String key, String value) {
		boolean status = false;
		try {
			for (JsonElement ele : array) {
				if(ele.isJsonObject()) {
					String temp = JSONUtil.getString(ele.getAsJsonObject(), key);
					if(value.equalsIgnoreCase(temp)) {
						status = true;
						LOG.debug("key {}, value {} is found.",key, value);
						break;
					}
				}
			}
		} catch (Exception e) {
			LOG.error("exception while searching id ", e);
		}
		return status;
	}
}

package com.kony.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.memorymgmt.UserDetailsManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;

public class UserDetailsHelper {

    private UserDetailsHelper() {
    }

    private static final Logger LOG = LogManager.getLogger(UserDetailsHelper.class);

    public static void saveUserDetailsIntoSession(JsonObject response, FabricRequestManager fabricRequestManager) {
        String userDetailsObject = "records";
        if (null != response && !response.isJsonNull() && response.has(userDetailsObject)) {
            JsonArray userDetails = response.getAsJsonArray(userDetailsObject);
            String userDetailsMap = getUserDetailsMap(userDetails);
            UserDetailsManager detailsManager = new UserDetailsManager(fabricRequestManager);
            detailsManager.saveUserDetailsIntoSession(userDetailsMap);
        }

    }

    public static String reloadUserDetailsIntoSession(FabricRequestManager fabricRequestManager) {
        String responseString = null;
        try {
            String res = DBPServiceExecutorBuilder.builder().withObjectId("Login")
                    .withServiceId("Users")
                    .withOperationId("get").build().getResponse();
            JsonObject response = new JsonParser().parse(res).getAsJsonObject();
            String userDetailsObject = "records";
            if (null != response && !response.isJsonNull() && response.has(userDetailsObject)) {
                JsonArray userDetails = response.getAsJsonArray(userDetailsObject);
                responseString = getUserDetailsMap(userDetails);
                UserDetailsManager detailsManager = new UserDetailsManager(fabricRequestManager);
                detailsManager.saveUserDetailsIntoSession(responseString);
            }
        } catch (Exception e) {
            LOG.error("Error while reloading external accounts:", e);
        }
        return responseString;
    }

    private static String getUserDetailsMap(JsonArray userDetails) {
        JsonObject resMap = new JsonObject();
        if (null != userDetails && !userDetails.isJsonNull() && userDetails.size() > 0) {
            JsonElement userDetailsObjEle = userDetails.get(0);
            if (userDetailsObjEle != null && userDetailsObjEle.isJsonObject()) {
                JsonObject userDetailsObj = userDetailsObjEle.getAsJsonObject();
                JsonElement contactDetails = userDetailsObj.get("ContactNumbers");
                JsonElement emailIds = userDetailsObj.get("EmailIds");
                JsonElement addresses = userDetailsObj.get("Addresses");
                JsonElement userName = userDetailsObj.get("userName");
                if (contactDetails != null && !contactDetails.isJsonNull() && contactDetails.isJsonArray()
                        && contactDetails.getAsJsonArray().size() > 0) {
                    resMap.add("ContactNumbers", contactDetails);
                }
                if (emailIds != null && !emailIds.isJsonNull() && emailIds.isJsonArray()
                        && emailIds.getAsJsonArray().size() > 0) {
                    resMap.add("EmailIds", emailIds);
                }
                if (addresses != null && !addresses.isJsonNull() && addresses.isJsonArray()
                        && addresses.getAsJsonArray().size() > 0) {
                    resMap.add("Addresses", addresses);
                }
                if(userName != null && !userName.isJsonNull()) {
                	resMap.add("userName", userName);
                }
            }
        }
        return resMap.toString();
    }

}

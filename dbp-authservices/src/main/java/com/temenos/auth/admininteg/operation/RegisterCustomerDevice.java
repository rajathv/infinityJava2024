package com.temenos.auth.admininteg.operation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.kony.AdminConsole.Utilities.CommonUtilities;
import com.kony.AdminConsole.Utilities.URLConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.MWConstants;
import com.kony.dbputilities.utils.DBPUtilitiesConstants;
import com.kony.dbputilities.utils.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

import eu.bitwalker.useragentutils.UserAgent;

public class RegisterCustomerDevice implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(RegisterCustomerDevice.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {

        Result result = new Result();
        try {

            String lastUsedIp = dcRequest.getHeader("X-Forwarded-For");
            String status_id = "SID_DEVICE_REGISTERED";
            String lastLoginTime = getCurrentTimeStamp();
            Map<String, Object> inputParams = CommonUtilities.getInputMapFromInputArray(inputArray);
            String userName = (String) inputParams.get("UserName");
            String customerID = (String) inputParams.get("customerID");
            String reportingParams = dcRequest.getHeader("X-Kony-ReportingParams");

            if (StringUtils.isNotBlank(reportingParams)) {
                JSONObject reportingParamsJson = new JSONObject(
                        URLDecoder.decode(reportingParams, StandardCharsets.UTF_8.name()));
                String deviceId = reportingParamsJson.optString("did");
                String channel_id = reportingParamsJson.optString("chnl");
                String appId = reportingParamsJson.optString("aid");
                String deviceName = new String();
                String operatingSystem = new String();

                if (channel_id.equalsIgnoreCase("desktop")) {
                    UserAgent userAgent = UserAgent.parseUserAgentString(dcRequest.getHeader("User-Agent"));
                    StringBuilder sb = new StringBuilder();
                    sb.append(userAgent.getBrowser().getName()).append(" ")
                            .append(userAgent.getBrowserVersion().getVersion());
                    deviceName = sb.toString();
                    operatingSystem = userAgent.getOperatingSystem().getName();
                } else {
                    deviceName = reportingParamsJson.optString("dm");
                    operatingSystem = reportingParamsJson.optString("plat") + " " + reportingParamsJson.optString("os");
                }

                result = inputvalidation(deviceId, userName, customerID, status_id, deviceName, lastUsedIp,
                        operatingSystem, channel_id, lastLoginTime, appId, dcRequest);

                if (result.getParamByName(MWConstants.ERR_MSG) != null
                        && StringUtils.isNotBlank(result.getParamByName(MWConstants.ERR_MSG).getName())) {
                    result.addParam(new Param("Status", "Failure", MWConstants.STRING));
                    return result;
                }

                result = registerCustomerDevice(deviceId, userName, customerID, deviceName, lastUsedIp,
                        operatingSystem, channel_id, lastLoginTime, appId, dcRequest);

                return result;
            }
        } catch (Exception e) {
            Result res = new Result();
            res.addParam(new Param("errmsg", e.getMessage(), "string"));
            return res;
        }

        return result;
    }

    private Result inputvalidation(String deviceId, String userName, String customerID, String status_id,
            String deviceName, String lastUsedIp, String operatingSystem, String channel_id, String lastLoginTime,
            String appId, DataControllerRequest dcRequest) {

        Result result = new Result();

        if (StringUtils.isBlank(deviceId))
            ErrorCodeEnum.ERR_20694.setErrorCode(result);
        if (StringUtils.isBlank(userName))
            ErrorCodeEnum.ERR_20613.setErrorCode(result);
        if (StringUtils.isBlank(customerID))
            ErrorCodeEnum.ERR_20613.setErrorCode(result);
        if (StringUtils.isBlank(status_id))
            ErrorCodeEnum.ERR_20692.setErrorCode(result);
        if (StringUtils.isBlank(deviceName))
            ErrorCodeEnum.ERR_20694.setErrorCode(result);
        if (StringUtils.isBlank(lastUsedIp))
            ErrorCodeEnum.ERR_20695.setErrorCode(result);
        if (StringUtils.isBlank(operatingSystem))
            ErrorCodeEnum.ERR_20696.setErrorCode(result);
        if (StringUtils.isBlank(channel_id))
            ErrorCodeEnum.ERR_20693.setErrorCode(result);
        if (StringUtils.isBlank(lastLoginTime))
            ErrorCodeEnum.ERR_20697.setErrorCode(result);

        return result;
    }

    public Result registerCustomerDevice(String deviceId, String userName, String customerID, String deviceName,
            String lastUsedIp, String operatingSystem, String channel_id, String lastLoginTime,
            String appId, DataControllerRequest dcRequest) {

        dcRequest.setAttribute("isServiceBeingAccessedByOLB", true);
        String isTracking = "false";
        String statusId = new String();
        try {
            if (StringUtils.isNotBlank(channel_id)) {
                if (channel_id.equalsIgnoreCase("Mobile"))
                    channel_id = "CH_ID_MOB";
                else if (channel_id.equalsIgnoreCase("Desktop")) {
                    channel_id = "CH_ID_INT";
                    if (operatingSystem.contains("Android") || operatingSystem.contains("iPhone")
                            || operatingSystem.contains("iPad")) {
                        channel_id = "CH_ID_MOB_INT";
                    }
                } else if (channel_id.equalsIgnoreCase("Tablet"))
                    channel_id = "CH_ID_TABLET";
                else if (channel_id.equalsIgnoreCase("MobileWeb"))
                    channel_id = "CH_ID_MOB_INT";
                else
                    channel_id = "CH_ID_MOB";
            } else {
                channel_id = "CH_ID_MOB";
            }

            Map<String, Object> postParametersMap = new HashMap<>();
            postParametersMap.put(DBPUtilitiesConstants.FILTER,
                    "id eq '" + deviceId + "' and Customer_id eq '" + customerID + "'");

            Result deviceinfo = HelperMethods.invokeServiceAndGetResult(dcRequest, postParametersMap,
                    HelperMethods.getHeaders(dcRequest), URLConstants.CUSTOMERDEVICE_READ);

            if (deviceinfo != null && !HelperMethods.hasError(deviceinfo)) {
                if (deviceinfo.getAllDatasets().size() > 0
                        && deviceinfo.getAllDatasets().contains((deviceinfo.getDatasetById("customerdevice")))
                        && (deviceinfo.getDatasetById("customerdevice").getAllRecords()).size() > 0) {
                    statusId = "SID_DEVICE_REGISTERED";
                    return updateCustomerDevice(deviceId, userName, customerID, statusId, deviceName, lastUsedIp,
                            operatingSystem, channel_id, lastLoginTime, appId, dcRequest);
                }
            }
            statusId = "SID_DEVICE_REGISTERED";
            return createCustomerDevice(deviceId, userName, customerID, statusId, deviceName, lastUsedIp,
                    operatingSystem, channel_id, lastLoginTime, appId, dcRequest);
        } catch (Exception e) {
            Result errorResult = new Result();
            ErrorCodeEnum.ERR_20001.setErrorCode(errorResult);
            return errorResult;
        }
    }

    public static Result createCustomerDevice(String deviceId, String userName, String customerID, String status_id,
            String deviceName, String lastUsedIp, String operatingSystem, String channel_id, String lastLoginTime,
            String appId, DataControllerRequest dcRequest) {

        Result result = new Result();

        Map<String, Object> postParametersMap = new HashMap<>();
        postParametersMap.put("id", deviceId);
        postParametersMap.put("Customer_id", customerID);
        postParametersMap.put("Status_id", status_id);
        postParametersMap.put("DeviceName", deviceName);
        postParametersMap.put("LastUsedIp", lastUsedIp);
        postParametersMap.put("LastLoginTime", lastLoginTime);
        postParametersMap.put("OperatingSystem", operatingSystem);
        postParametersMap.put("Channel_id", channel_id);
        postParametersMap.put("appid", appId);
        postParametersMap.put("createdby", userName);
        postParametersMap.put("modifiedby", userName);
        postParametersMap.put("createdts", CommonUtilities.getISOFormattedLocalTimestamp());
        postParametersMap.put("lastmodifiedts", CommonUtilities.getISOFormattedLocalTimestamp());
        postParametersMap.put("synctimestamp", CommonUtilities.getISOFormattedLocalTimestamp());

        Result deviceinfo = HelperMethods.invokeServiceAndGetResult(dcRequest, postParametersMap,
                HelperMethods.getHeaders(dcRequest), URLConstants.CUSTOMERDEVICE_CREATE);

        if (deviceinfo == null || HelperMethods.hasError(deviceinfo) || (HelperMethods.hasRecords(deviceinfo)
                && deviceinfo.getParamValueByName(MWConstants.OPSTATUS) != "0")) {
            LOG.error("Failed to register a device!");
            result.addParam(
                    new Param("RegisterResponse", (deviceinfo != null) ? String.valueOf(deviceinfo.getAllRecords())
                            : "Failed to register a device!", MWConstants.STRING));
            result.addParam(new Param("Status", "Failed", MWConstants.STRING));
            result.addParam(
                    new Param("OperationCode", ErrorCodeEnum.ERR_20691.getErrorCodeAsString(), MWConstants.INT));
            return result;
        }

        Param statusParam = new Param("Status", "Successful", MWConstants.STRING);
        result.addParam(new Param("OperationCode", "0", MWConstants.INT));
        result.addParam(statusParam);
        return result;
    }

    public static Result updateCustomerDevice(String deviceId, String userName, String customerID, String status_id,
            String deviceName, String lastUsedIp, String operatingSystem, String channel_id, String lastLoginTime,
            String appId, DataControllerRequest dcRequest) {

        Result result = new Result();
        Map<String, Object> postParametersMap = new HashMap<>();
        postParametersMap.put("id", deviceId);
        postParametersMap.put("Customer_id", customerID);
        postParametersMap.put("Status_id", status_id);
        if (StringUtils.isNotBlank(deviceName))
            postParametersMap.put("DeviceName", deviceName);
        postParametersMap.put("LastUsedIp", lastUsedIp);
        postParametersMap.put("LastLoginTime", lastLoginTime);
        if (StringUtils.isNotBlank(operatingSystem))
            postParametersMap.put("OperatingSystem", operatingSystem);
        if (StringUtils.isNotBlank(channel_id))
            postParametersMap.put("Channel_id", channel_id);
        if (StringUtils.isNotBlank(appId))
            postParametersMap.put("appid", appId);
        postParametersMap.put("modifiedby", userName);
        postParametersMap.put("lastmodifiedts", CommonUtilities.getISOFormattedLocalTimestamp());

        Result deviceinfo = HelperMethods.invokeServiceAndGetResult(dcRequest, postParametersMap,
                HelperMethods.getHeaders(dcRequest), URLConstants.CUSTOMERDEVICE_UPDATE);

        if (deviceinfo == null || HelperMethods.hasError(deviceinfo) || (HelperMethods.hasRecords(deviceinfo)
                && deviceinfo.getParamValueByName(MWConstants.OPSTATUS) != "0")) {
            LOG.error("Failed to register a device!");
            result.addParam(
                    new Param("RegisterResponse", (deviceinfo != null) ? String.valueOf(deviceinfo.getAllRecords())
                            : "Failed to register a device!", MWConstants.STRING));
            result.addParam(new Param("Status", "Failed", MWConstants.STRING));
            result.addParam(
                    new Param("OperationCode", ErrorCodeEnum.ERR_20691.getErrorCodeAsString(), MWConstants.INT));
            return result;
        }

        Param statusParam = new Param("Status", "Successful", MWConstants.STRING);
        result.addParam(new Param("OperationCode", "0", MWConstants.INT));
        result.addParam(statusParam);
        return result;
    }

    public static String getCurrentTimeStamp() {
        return getFormattedTimeStamp(new Date(), null);
    }

    public static String getFormattedTimeStamp(Date dt, String format) {
        String dtFormat = "yyyy-MM-dd'T'hh:mm:ss";
        if (StringUtils.isNotBlank(format)) {
            dtFormat = format;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(dtFormat);
        return formatter.format(dt);
    }

}

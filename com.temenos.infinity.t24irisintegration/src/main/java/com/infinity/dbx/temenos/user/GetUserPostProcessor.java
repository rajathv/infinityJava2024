package com.infinity.dbx.temenos.user;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.kony.dbx.BasePostProcessor;
import com.kony.dbx.util.CommonUtils;
import com.kony.dbx.util.Constants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class GetUserPostProcessor extends BasePostProcessor implements TemenosConstants, UserConstants, Constants {

    private static final Logger logger = LogManager.getLogger(GetUserPostProcessor.class);

    @Override
    public Result execute(Result result, DataControllerRequest request, DataControllerResponse response)
            throws Exception {

        Result userGet = getDbxUser(request);

        HashMap<String, Object> inputParams = new HashMap<String, Object>();
        // Read customer table
        Record userRecord = result.getDatasetById(DATASET_USER) != null
                ? result.getDatasetById(DATASET_USER).getRecord(0)
                : new Record();

        Dataset phoneDs = userRecord.getDatasetById("ContactNumbers");
        if (phoneDs == null) {
            phoneDs = new Dataset();
            phoneDs.setId("ContactNumbers");
        }

        Dataset EmailIds = userRecord.getDatasetById("EmailIds");
        if (EmailIds == null) {
            EmailIds = new Dataset();
            EmailIds.setId("EmailIds");
        }

        boolean isPhoneSet = false;
        if (phoneDs != null) {
            for (Record rec : phoneDs.getAllRecords()) {
                if ("true".equalsIgnoreCase(CommonUtils.getParamValue(rec, TemenosConstants.PARAM_IS_PRIMARY))) {
                    isPhoneSet = true;
                    break;
                }
            }
        }

        String phone = "";
        Record phoneRecord = new Record();
        boolean isphoneRecordSet = false;
        boolean isEmailRecordSet = false;
        Dataset contactDetails = userRecord.getDatasetById("contactDetails");
        if (!isPhoneSet && contactDetails != null) {
            Record rec = contactDetails.getAllRecords().get(0);
            phone = rec.getParamValueByName("phone");

            if (phone != null) {
                if (phone.contains("-")) {
                    phoneRecord.addParam("phoneCountryCode", phone.split("-")[0]);
                    phoneRecord.addParam("Value", phone.split("-")[1]);
                } else {
                    phoneRecord.addParam("Value", phone);
                }
                phoneRecord.addParam("isPrimary", "true");
                phoneRecord.addParam("Extension", "Mobile");
                phoneRecord.addParam("Type_id", "COMM_TYPE_PHONE");
                phoneRecord.addParam("id", "1");
                isPhoneSet = true;
            }
        }
        boolean isEmailSet = false;
        if (EmailIds != null) {
            for (Record rec : EmailIds.getAllRecords()) {
                if ("true".equalsIgnoreCase(CommonUtils.getParamValue(rec, TemenosConstants.PARAM_IS_PRIMARY))) {
                    isEmailSet = true;
                    break;
                }
            }
        }
        String email = "";
        Record emailRecord = new Record();
        if (!isEmailSet && contactDetails != null) {
            Record rec = contactDetails.getAllRecords().get(0);
            email = rec.getParamValueByName("email");
            emailRecord.addParam("Value", email);
            emailRecord.addParam("isPrimary", "true");
            emailRecord.addParam("Type_id", "COMM_TYPE_EMAIL");
            emailRecord.addParam("id", "1");

            isEmailSet = true;
        }

        
        Dataset addressDS = userRecord.getDatasetById(DATASET_ADDRESS);
        if (addressDS == null) {
            addressDS = new Dataset(DATASET_ADDRESS);
            userRecord.addDataset(addressDS);
            userRecord.removeParamByName(DATASET_ADDRESS);
        }

        Record addressRecord = new Record();
        boolean isAddressNew = true;
        if (addressDS != null && addressDS.getAllRecords() != null && !addressDS.getAllRecords().isEmpty()) {
            for(Record record : addressDS.getAllRecords()) {
                if(Boolean.parseBoolean(record.getParamValueByName("isPrimary"))){
                    addressRecord = record;
                    isAddressNew = false;
                }
                else if(!record.hasParamByName("isPrimary")) {
                    addressRecord = record;
                    isAddressNew = false;
                }
            }
        }
          
        addressRecord.addStringParam(ADDR_TYPE, ADDRESS_TYPE_HOME);
        if(StringUtils.isBlank(addressRecord.getParamValueByName("country"))) {
            addressRecord.addStringParam("country", userRecord.getParamValueByName("country"));
        }
        if(StringUtils.isBlank(addressRecord.getParamValueByName("AddressLine1"))) {
            addressRecord.addStringParam("AddressLine1", userRecord.getParamValueByName("AddressLine1"));
        }
        if(StringUtils.isBlank(addressRecord.getParamValueByName("AddressLine2"))) {
            addressRecord.addStringParam("AddressLine2", userRecord.getParamValueByName("AddressLine2"));
        }
        if(StringUtils.isBlank(addressRecord.getParamValueByName("ZipCode"))) {
            addressRecord.addStringParam("ZipCode", userRecord.getParamValueByName("ZipCode"));
        }
        if(StringUtils.isBlank(addressRecord.getParamValueByName("City_id"))) {
            addressRecord.addStringParam("City_id", userRecord.getParamValueByName("City_id"));
        }
        if(StringUtils.isBlank(addressRecord.getParamValueByName("CityName"))) {
            addressRecord.addStringParam("CityName", userRecord.getParamValueByName("CityName"));
        }
        if(StringUtils.isBlank(addressRecord.getParamValueByName(ADDR_TYPE))) {
            addressRecord.addStringParam(ADDR_TYPE, ADDRESS_TYPE_HOME);
        }

        if(StringUtils.isNotBlank(CommonUtils.getParamValue(addressRecord, "country"))) {
            String country = CommonUtils.getParamValue(addressRecord, "country");
            inputParams.put(PARAM_DOLLAR_FILTER, "Name eq '" + country + "'");
            Result countryGet = CommonUtils.callIntegrationService(request, inputParams, null,
                    TemenosConstants.SERVICE_BACKEND_CERTIFICATE, TemenosConstants.OP_COUNTRY_GET, false);
            if (countryGet.getDatasetById(DS_COUNTRY) != null
                    && !countryGet.getDatasetById(DS_COUNTRY).getAllRecords().isEmpty()) {
                String countryCode = countryGet.getDatasetById(DS_COUNTRY).getRecord(0).getParamValueByName("Code");
                addressRecord.addStringParam("CountryCode", countryCode);
            } else {
                addressRecord.addStringParam("CountryCode", country);
            }
        }
        addressRecord.addStringParam("isPrimary", "true");
        addressRecord.addStringParam("Address_id", "1");
   
        if(isAddressNew) {
            addressDS.addRecord(addressRecord);
        }

        if (userGet != null && userGet.getDatasetById("customer") != null && userRecord != null) {

            Record customerGetRecord = userGet.getDatasetById("customer").getRecord(0);

            Dataset ContactNumbers = customerGetRecord.getDatasetById("ContactNumbers") != null
                    ? customerGetRecord.getDatasetById("ContactNumbers")
                    : new Dataset();

            customerGetRecord.removeDatasetById("ContactNumbers");
            for (Record dbRec : ContactNumbers.getAllRecords()) {
            	String phoneNumber = dbRec.getParamValueByName("Value");
				if (StringUtils.isNotBlank(phone) && phone.contains("-")) {
					String phoneCountryCode = (dbRec.getParamValueByName("phoneCountryCode")) + "-";
					String phoneNo = phoneCountryCode + phoneNumber;
					if (StringUtils.isNotBlank(phoneNo)) {
						if (StringUtils.isNotBlank(phone) && phone.equals(phoneNo)) {
							isphoneRecordSet = true;
						}
						phoneDs.addRecord(dbRec);
					}
				} else {
					if (StringUtils.isNotBlank(phoneNumber)) {
						if (StringUtils.isNotBlank(phone) && phone.equals(phoneNumber)) {
							isphoneRecordSet = true;
						}
						phoneDs.addRecord(dbRec);
					}
				}
			}

            ContactNumbers = customerGetRecord.getDatasetById("EmailIds") != null
                    ? customerGetRecord.getDatasetById("EmailIds")
                    : new Dataset();
            customerGetRecord.removeDatasetById("EmailIds");
            for (Record dbRec : ContactNumbers.getAllRecords()) {
                String phoneNumber = dbRec.getParamValueByName("Value");

                if (StringUtils.isNotBlank(phoneNumber)) {
                    if (StringUtils.isNotBlank(email) && email.equals(phoneNumber)) {
                        isEmailRecordSet = true;
                    }

                    EmailIds.addRecord(dbRec);
                }
            }

            ContactNumbers = customerGetRecord.getDatasetById("Addresses") != null
                    ? customerGetRecord.getDatasetById("Addresses")
                    : new Dataset();
            customerGetRecord.removeDatasetById("Addresses");

            for (Record dbRec : ContactNumbers.getAllRecords()) {
            	if(StringUtils.isBlank(dbRec.getParamValueByName(ADDR_TYPE))) {
            		dbRec.addStringParam(ADDR_TYPE, ADDRESS_TYPE_HOME);
                }
                addressDS.addRecord(dbRec);
            }

            for (Param param : customerGetRecord.getAllParams()) {
                userRecord.addParam(param);
            }
            for (Record record : customerGetRecord.getAllRecords()) {
                userRecord.addRecord(record);
            }
            for (Dataset dataset : customerGetRecord.getAllDatasets()) {
                userRecord.addDataset(dataset);
            }

            String customerName = userRecord.getParamValueByName(CUSTOMER_NAME);
            if (StringUtils.isNotBlank(customerName) && customerName.contains(" ")) {
                userRecord.addStringParam(FIRSTNAME, customerName.substring(0, customerName.indexOf(' ')));
                userRecord.addStringParam(LASTNAME, customerName.substring(customerName.indexOf(' ') + 1));
            } else {
                userRecord.addStringParam(FIRSTNAME, customerGetRecord.getParamValueByName(FIRSTNAME));
                userRecord.addStringParam(LASTNAME, customerGetRecord.getParamValueByName(LASTNAME));
            }

            String bankName = CommonUtils.getProperty(TemenosConstants.TEMENOS_PROPERTIES_FILE,
                    TemenosConstants.PROP_PREFIX_TEMENOS, PROP_GENERAL, PROP_BANKNAME);
            userRecord.addStringParam(UserConstants.BANK_NAME, bankName);
        }

        if (!isphoneRecordSet && isPhoneSet && StringUtils.isNotBlank(phone)) {
            phoneDs.addRecord(phoneRecord);
        }

        if (!isEmailRecordSet && isEmailSet && StringUtils.isNotBlank(email)) {
            EmailIds.addRecord(emailRecord);
        }

        userRecord.addDataset(EmailIds);
        userRecord.addDataset(phoneDs);
        userRecord.addDataset(addressDS);

        return result;
    }

    /**
     * Call the dbpRbLocalServicesJava.getUserDetails() operation to retrieve the user's details for downstream
     * processing.
     * 
     * @param HashMap
     *            The hashmap containing the input parameters
     * @param DataControllerRequest
     *            Request object
     * @return Result The result object from calling the service
     **/
    private Result getDbxUser(DataControllerRequest request) throws Exception {

        if (Boolean.parseBoolean(request.getParameter("isSuperAdmin"))
                && !Boolean.parseBoolean(request.getParameter("isCustomerPresent"))) {
            return new Result();
        }

        String getUserServiceName = KONY_DBX_SERVICE_JAVA;
        String getUserOperationName = KONY_DBX_OP_JAVA_GETUSERDETAILS_CONCURRENT;
        
        request.addRequestParam_("isCallingFromT24", "true");
        
        Result result = CommonUtils.callIntegrationService(request, null, request.getHeaderMap(), getUserServiceName,
                getUserOperationName, true);

        return result;
    }
}

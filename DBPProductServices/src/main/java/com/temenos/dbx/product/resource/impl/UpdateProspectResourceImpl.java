package com.temenos.dbx.product.resource.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonObject;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.businessdelegate.api.UpdateProspectBusinessDelegate;
import com.temenos.dbx.product.dto.UpdateProspectDTO;
import com.temenos.dbx.product.resource.api.UpdateProspectResource;

public class UpdateProspectResourceImpl implements UpdateProspectResource {
    private static final Logger LOG = LogManager.getLogger(UpdateProspectResourceImpl.class);

    @Override
    public Result updateProspect(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) {
        Map<String, String> inputMap = HelperMethods.getInputParamMap(inputArray);
        Result result = new Result();
        String customerId = inputMap.get("id");
        String personalInformation = inputMap.get("personalInformation");
        String contactInformation = inputMap.get("contactInformation");
        if (StringUtils.isBlank(personalInformation)) {
            personalInformation = dcRequest.getParameter("personalInformation");
        }
        if (StringUtils.isBlank(contactInformation)) {
            contactInformation = dcRequest.getParameter("contactInformation");
        }
        if (StringUtils.isBlank(customerId)) {
            customerId = dcRequest.getParameter("id");
        }
        if (StringUtils.isBlank(customerId)) {
            ErrorCodeEnum.ERR_12605.setErrorCode(result);
        } else if ((StringUtils.isNotBlank(personalInformation) || StringUtils.isNotBlank(contactInformation))
                && checkIfUserIsTypeProspect(customerId, dcRequest)) {
            UpdateProspectDTO updateProspectDTO = new UpdateProspectDTO();
            updateDTO(updateProspectDTO, personalInformation, contactInformation);
            UpdateProspectBusinessDelegate updateProspectBD = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(BusinessDelegateFactory.class)
                    .getBusinessDelegate(UpdateProspectBusinessDelegate.class);
            JsonObject object =
                    updateProspectBD.updateProspect(updateProspectDTO, customerId, URLConstants.CUSTOMER_UPDATE,
                            URLConstants.CUSTOMERCOMMUNICATION_UPDATE, URLConstants.CUSTOMERCOMMUNICATION_GET,
                            HelperMethods.getHeaders(dcRequest));
            if (!JSONUtil.hasKey(object, DBPUtilitiesConstants.VALIDATION_ERROR)) {
                Param param = new Param("result", "update successful", DBPUtilitiesConstants.STRING_TYPE);
                result.addParam(param);
            }

        } else {
            ErrorCodeEnum.ERR_12606.setErrorCode(result);
        }

        return result;
    }

    private void updateDTO(UpdateProspectDTO updateProspectDTO, String personalInformation, String contactInformation) {
        HashMap<String, String> personalInfo = HelperMethods.getRecordMap(personalInformation);
        HashMap<String, String> commInfo = HelperMethods.getRecordMap(contactInformation);
        updateProspectDTO.setFirstName(personalInfo.get("FirstName"));
        updateProspectDTO.setLastName(personalInfo.get("LastName"));
        updateProspectDTO.setDateOfBirth(personalInfo.get("DateOfBirth"));
        updateProspectDTO.setEmail(commInfo.get("Email"));
        updateProspectDTO.setPhone(commInfo.get("Phone"));
    }

    private boolean checkIfUserIsTypeProspect(String customerId, DataControllerRequest dcRequest) {
        String filterQuery = "id" + DBPUtilitiesConstants.EQUAL + customerId;
        Result customerData = null;
        try {
            customerData = HelperMethods.callGetApi(dcRequest, filterQuery, HelperMethods.getHeaders(dcRequest),
                    URLConstants.CUSTOMERVERIFY_GET);
        } catch (HttpCallException e) {
            LOG.error(e.getMessage());
        }
        String customerType = HelperMethods.getFieldValue(customerData, "CustomerType_id");
        return "TYPE_ID_PROSPECT".equals(customerType);
    }

}

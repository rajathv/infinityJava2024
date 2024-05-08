package com.temenos.dbx.product.businessdelegate.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dbp.core.util.JSONUtils;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.temenos.dbx.product.businessdelegate.api.ApproverBusinessDelegate;
import com.temenos.dbx.product.dto.AccountActionApproverListDTO;

/**
 * 
 * @author KH2627
 * @version 1.0 Implements the {@link ApproverBusinessDelegate}
 */

public class ApproverBusinessDelegateImpl implements ApproverBusinessDelegate {
	private static final Logger LOG = Logger.getLogger(ApproverBusinessDelegateImpl.class);
    @Override
    public List<AccountActionApproverListDTO> getAccountActionApproverList(
            AccountActionApproverListDTO accountActionApproverListInputDTO,
            Map<String, Object> headerParams, String url) {

        Map<String, Object> inputParams = new HashMap<>();
        inputParams.put("_contractId", accountActionApproverListInputDTO.getContractId());
        inputParams.put("_cif", accountActionApproverListInputDTO.getCif());
        inputParams.put("_accountIds", accountActionApproverListInputDTO.getAccountId());
        inputParams.put("_approvalActionList", accountActionApproverListInputDTO.getApprovalActionList());
        inputParams.put("_featureId", accountActionApproverListInputDTO.getFeatureId());

        JsonObject response = ServiceCallHelper.invokeServiceAndGetJson(inputParams, headerParams, url);

        List<AccountActionApproverListDTO> customerDTOApproverList = new ArrayList<>();

        if (!response.has("opstatus")
                || response.get("opstatus").getAsInt() != 0
                || !response.has("records")) {
            // exception while fetching the data
            return null;
        }

        if (response.get("records").getAsJsonArray().size() == 0) {
            // no approvers
            return customerDTOApproverList;
        }
        try {
            customerDTOApproverList =
                    JSONUtils.parseAsList(response.get("records").getAsJsonArray().toString(),
                            AccountActionApproverListDTO.class);
        } catch (IOException e) {
            LOG.error(e);
        }
        return customerDTOApproverList;
    }

}

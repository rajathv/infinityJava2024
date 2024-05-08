package com.temenos.infinity.tradefinanceservices.resource.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.infinity.dbx.temenos.transactions.TransactionConstants;
import com.infinity.dbx.temenos.utils.TemenosUtils;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.GetLetterOfCreditsByIDBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.InitiateDownloadImportLCSwiftBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.InitiateDownloadLetterOfCreditsAckBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.constants.Constants;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.InitiateDownloadLetterOfCreditsAckResource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.PARAM_STATUS_APPROVED;
import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.PREFIX_IMPORT_LOC;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.generateTradeFinanceFileID;

public class InitiateDownloadLetterOfCreditsAckResourceImpl implements InitiateDownloadLetterOfCreditsAckResource {

    private static final Logger LOG = LogManager.getLogger(GetLetterOfCreditsResourceImpl.class);

    @Override
    public Result initiateDownloadLetterOfCreditsAck(Object[] inputArray, DataControllerRequest request) {
        byte[] bytes = new byte[0];
        Result result = new Result();
        LetterOfCreditsDTO letterOfCredit = new LetterOfCreditsDTO();
        InitiateDownloadImportLCSwiftBusinessDelegate swiftbusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(InitiateDownloadImportLCSwiftBusinessDelegate.class);
        InitiateDownloadLetterOfCreditsAckBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(InitiateDownloadLetterOfCreditsAckBusinessDelegate.class);
        GetLetterOfCreditsByIDBusinessDelegate getSRMSID = DBPAPIAbstractFactoryImpl.getBusinessDelegate(GetLetterOfCreditsByIDBusinessDelegate.class);

        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
        String customerId = CustomerSession.getCustomerId(customer);

        @SuppressWarnings("unchecked")
        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
        String srmsRequestId = inputParams.get(TradeFinanceConstants.PARAM_SRMS_ReqOrderID).toString();
        if (StringUtils.isBlank(srmsRequestId)) {
            LOG.error("SRMS RequestId is missing");
            return ErrorCodeEnum.ERRTF_29055.setErrorCode(new Result(), Constants.PROVIDE_MANDATORY_FIELDS);
        }
        letterOfCredit.setSrmsReqOrderID(request.getParameter("srmsReqOrderID"));
        LetterOfCreditsDTO locData = new LetterOfCreditsDTO();
        try {
            locData = getSRMSID.getLetterOfCreditsByID(letterOfCredit, request);
            if (StringUtils.isNotBlank(customerId) && customerId.equalsIgnoreCase(locData.getCustomerId())) {
                LOG.info("Passed UserId Validation");
            } else {
                return ErrorCodeEnum.ERRTF_29070.setErrorCode(new Result());
            }
        } catch (Exception e) {
            LOG.error("Error while get call of LOC", e);
        }

        try {
            if (StringUtils.isNotBlank(srmsRequestId)) {

                //Retrieving Swift Enable tag from Bundle Configurations
                JSONObject bundleConfig = TemenosUtils.getBundleConfigurations(TransactionConstants.DBP_BUNDLE,
                        "SWIFT_TAG_ENABLE", request);
                String swiftEnable = bundleConfig.get("configurations").toString();
                JSONArray swift = new JSONArray(swiftEnable);
                JSONObject swiftBundle = (JSONObject) swift.get(0);
                String swiftEnable2 = swiftBundle.get("config_value").toString();
                JSONObject s = new JSONObject(swiftEnable2);

                // Checking Swift Enable tag and calling business Delegate Class Accordingly
                if (s.get("Swift Enable").equals("True") && locData.getStatus().equals(PARAM_STATUS_APPROVED)) {
                    bytes = swiftbusinessDelegate.generateImportLCSwiftPdf(srmsRequestId, request);
                } else {
                    bytes = businessDelegate.getRecordPDFAsBytes(locData, customerId, request);
                }
            }
            String fileId = generateTradeFinanceFileID(PREFIX_IMPORT_LOC);
            MemoryManager.saveIntoCache(fileId, Base64.encodeBase64String(bytes), 120);
            result.addParam("fileId", fileId);
            return result;
        } catch (Exception e) {
            LOG.error("Error while generating the trade finance file", e);
            return ErrorCodeEnum.ERRTF_29056.setErrorCode(new Result());
        }
    }

}

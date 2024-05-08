package com.temenos.dbx.transaction.resource.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.transaction.businessdelegate.api.BlockedFundsBusinessDelegate;
import com.temenos.dbx.transaction.dto.BlockedFundDTO;
import com.temenos.dbx.transaction.resource.api.BlockedFundsResource;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class BlockedFundsResourceImpl implements BlockedFundsResource{
    private static final Logger LOG = LogManager.getLogger(BlockedFundsResourceImpl.class);

    @Override
    public Result getBlockedFunds(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) {

        Result result = new Result();

        // Initialization of business Delegate Class
        BlockedFundsBusinessDelegate businessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(BusinessDelegateFactory.class)
                .getBusinessDelegate(BlockedFundsBusinessDelegate.class); 

        try {
            Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
            String accountID = inputParams.get("accountID") == null ? "" : inputParams.get("accountID");
            String searchStartDate = inputParams.get("searchStartDate") == null ? "" : inputParams.get("searchStartDate");
            String searchEndDate = inputParams.get("searchEndDate") == null ? "" : inputParams.get("searchEndDate");
            String lockedEventId = inputParams.get("lockedEventId") == null ? "" : inputParams.get("lockedEventId");
            String offset=inputParams.get("offset") == null ? "" : inputParams.get("offset");
            String limit=inputParams.get("limit") == null ? "" : inputParams.get("limit");
            BlockedFundDTO inputDTO = new BlockedFundDTO();
            inputDTO.setAccountID(accountID);
            inputDTO.setSearchStartDate(searchStartDate);
            inputDTO.setSearchEndDate(searchEndDate);
            inputDTO.setLockedEventId(lockedEventId);
            
            List<BlockedFundDTO> blockedFundsDTO;

            if (StringUtils.isBlank(accountID)) {
                return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
            }
            
            blockedFundsDTO = businessDelegate.getBlockedFunds(inputDTO);
            
            JSONArray rulesJSONArr = new JSONArray(blockedFundsDTO);
            JSONObject responseObj = new JSONObject();
            responseObj.put("Transactions", rulesJSONArr);
            result = JSONToResult.convert(responseObj.toString());
            Dataset dataset = new Dataset();
			Record totalRecord = new Record();
			totalRecord.addParam(new Param("totalSize",String.valueOf(rulesJSONArr.length())));
			totalRecord.addParam(new Param("pageStart",offset));
			totalRecord.addParam(new Param("pageSize", limit));
			dataset.addRecord(totalRecord);
			dataset.setId("Meta");
			result.addDataset(dataset);
        } catch (Exception e) {
            LOG.error("Caught exception at getBlockedFunds " + e); 
            return ErrorCodeEnum.ERR_12000.setErrorCode(result);
        }

        return result;
    }

}

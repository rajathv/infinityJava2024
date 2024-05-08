package com.temenos.infinity.api.arrangements.postprocessors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.TemenosBasePostProcessor;
import com.infinity.dbx.temenos.utils.TemenosUtils;
import com.kony.dbx.util.CommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.arrangements.constants.UserAccountSettingConstants;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */

public class GetBlockedFundsPostProcessor extends TemenosBasePostProcessor implements UserAccountSettingConstants {
    Logger logger = LogManager.getLogger(GetBlockedFundsPostProcessor.class);
    
    @Override
    public Result execute(Result result, DataControllerRequest request, DataControllerResponse response)
            throws Exception {
        Dataset transactionsDS = result.getDatasetById(TRANSACTION);
        String ErrMsg = result.getParamValueByName(UserAccountSettingConstants.PARAM_ERROR_MESSAGE) != ""
                ? result.getParamValueByName(UserAccountSettingConstants.PARAM_ERROR_MESSAGE) : "";

        List<Record> transactionRecords = transactionsDS != null ? transactionsDS.getAllRecords() : null;
        if ((transactionRecords == null || transactionRecords.isEmpty()) && StringUtils.isEmpty(ErrMsg)) {
            logger.debug(ERR_EMPTY_RESPONSE);
            Result transactionResult = TemenosUtils.getEmptyResult(TRANSACTION);
            if(StringUtils.isNotBlank(result.getParamValueByName(UserAccountSettingConstants.PARAM_PAGESTART))) {
            	transactionResult.addParam(UserAccountSettingConstants.PARAM_PAGESTART, result.getParamValueByName(UserAccountSettingConstants.PARAM_PAGESTART));
            }
            if(StringUtils.isNotBlank(result.getParamValueByName(UserAccountSettingConstants.PARAM_PAGESIZE))) {
            	transactionResult.addParam(UserAccountSettingConstants.PARAM_PAGE_SIZE_PENDING, result.getParamValueByName(UserAccountSettingConstants.PARAM_PAGESIZE));
            }
            if(StringUtils.isNotBlank(result.getParamValueByName(UserAccountSettingConstants.PARAM_TOTALSIZE))) {
            	transactionResult.addParam(UserAccountSettingConstants.PARAM_TOTAL_SIZE_PENDING, result.getParamValueByName(UserAccountSettingConstants.PARAM_TOTALSIZE));
            }
            return transactionResult;
        }
        
        if ((transactionRecords != null && !transactionRecords.isEmpty())){
            for (Record transaction : transactionRecords) {
                String fromDate = CommonUtils.getParamValue(transaction, UserAccountSettingConstants.PARAM_FROM_DATE);
                String toDate = CommonUtils.getParamValue(transaction, UserAccountSettingConstants.PARAM_TO_DATE);
                if(StringUtils.isNotBlank(fromDate)){
                    //Convert to standard date format
                    transaction.addParam(UserAccountSettingConstants.PARAM_FROM_DATE, convertDate(fromDate));
                }
                if(StringUtils.isNotBlank(toDate)){
                    //Convert to standard date format
                    transaction.addParam(UserAccountSettingConstants.PARAM_TO_DATE, convertDate(toDate));
                }
            }
        }
        Dataset dataset = new Dataset();
		Record record = new Record();
		dataset.setId("Meta");
		if (StringUtils.isNotBlank(result.getParamValueByName(UserAccountSettingConstants.PARAM_TOTALSIZE)))
			record.addParam(new Param(UserAccountSettingConstants.PARAM_TOTALSIZE, result.getParamValueByName(UserAccountSettingConstants.PARAM_TOTALSIZE)));
		if (StringUtils.isNotBlank(result.getParamValueByName(UserAccountSettingConstants.PARAM_PAGESIZE)))
			record.addParam(new Param(UserAccountSettingConstants.PARAM_PAGESIZE, result.getParamValueByName(UserAccountSettingConstants.PARAM_PAGESIZE)));
		if (StringUtils.isNotBlank(result.getParamValueByName(UserAccountSettingConstants.PARAM_PAGESTART)))
			record.addParam(new Param(UserAccountSettingConstants.PARAM_PAGESTART, result.getParamValueByName(UserAccountSettingConstants.PARAM_PAGESTART)));
		dataset.addRecord(record);
		result.addDataset(dataset);
		return result;
    }
    
    public String convertDate(String Date){
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = (Date) formatter.parse(Date);
            } catch (ParseException e) {
                logger.error("Unable to parse Date" + e);
            }
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String processedDate = DATE_FORMAT.format(date);
        return processedDate;
    }
}
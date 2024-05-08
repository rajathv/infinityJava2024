package com.infinity.dbx.temenos.cards;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.TemenosBasePostProcessor;
import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.infinity.dbx.temenos.utils.TemenosUtils;
import com.kony.dbx.util.CommonUtils;
import com.kony.dbx.util.Constants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class GetCardsPostProcessor extends TemenosBasePostProcessor {
	private static final Logger logger = LogManager.getLogger(GetCardsPostProcessor.class);

	@Override
	public Result execute(Result result, DataControllerRequest request, DataControllerResponse response) {
		try {
			result = super.execute(result, request, response);
			Dataset cards = result.getDatasetById(CardConstants.DATASET_CARDS);
			List<Record> cardRecords = cards != null ? cards.getAllRecords() : null;			
			if (cardRecords == null || cardRecords.isEmpty()
					|| (StringUtils.isNotBlank(result.getParamValueByName(PARAM_ERR_MSG)) && result
							.getParamValueByName(PARAM_ERR_MSG).contains(TemenosConstants.ERR_MSG_NO_RECORDS))) {
				return TemenosUtils.getEmptyResult(CardConstants.DATASET_CARDS);
			}
			for (Record rec : cards.getAllRecords()) {
				String accNum = rec.getParamValueByName(CardConstants.ACCOUNT_NUMBER);
				String expDate = rec.getParamValueByName(CardConstants.EXPIRATION_DATE);
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.DATE_YYYYMMDD);
				LocalDate expirationDate = LocalDate.parse(expDate, dateTimeFormatter);
				LocalDate today = LocalDate.now();
				if (expirationDate.compareTo(today) < 0) {
					rec.addParam(new Param(CardConstants.CARD_STATUS, CardConstants.CARD_STATUS_INACTIVE,
							Constants.PARAM_DATATYPE_STRING));
				} else {
					rec.addParam(new Param(CardConstants.CARD_STATUS, CardConstants.CARD_STATUS_ACTIVE,
							Constants.PARAM_DATATYPE_STRING));
				}
				rec.addParam(
						new Param(CardConstants.IS_INTERNATIONAL, Constants.FALSE, Constants.PARAM_DATATYPE_STRING));
				rec.addParam(new Param(CardConstants.MASKED_ACCOUNT_NUMBER, CardUtils.mask(accNum),
						Constants.PARAM_DATATYPE_STRING));
				rec.addParam(new Param(CardConstants.CARD_TYPE, CardConstants.CARD_TYPE_DEBIT,
						Constants.PARAM_DATATYPE_STRING));
			}
		} catch (Exception e) {
			Result errorResult = new Result();
			logger.error("Exception Occured while in get cards post processor:" + e);
			CommonUtils.setErrMsg(errorResult, e.getMessage());
			return errorResult;
		}
		return result;
	}
}

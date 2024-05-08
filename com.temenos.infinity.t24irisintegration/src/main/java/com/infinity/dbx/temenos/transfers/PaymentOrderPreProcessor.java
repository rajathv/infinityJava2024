package com.infinity.dbx.temenos.transfers;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.infinity.dbx.temenos.TemenosBasePreProcessor;
import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.kony.dbx.util.CommonUtils;
import com.kony.dbx.util.Constants;
import com.kony.dbx.util.EnvironmentConfigurationsHandler;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class PaymentOrderPreProcessor extends TemenosBasePreProcessor {

	private static final Logger logger = LogManager.getLogger(PaymentOrderPreProcessor.class);

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(HashMap params, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {
			super.execute(params, request, response, result);

			String transactionType = CommonUtils.getParamValue(params, Constants.PARAM_TRANSACTION_TYPE);
			if (StringUtils.isNotBlank(transactionType)
					&& !Constants.TRANSCTION_TYPE_INTERNAL_TRANSFER.equalsIgnoreCase(transactionType)
					&& !Constants.TRANSCTION_TYPE_EXTERNAL_TRANSFER.equalsIgnoreCase(transactionType)) {
				CommonUtils.setOpStatusOk(result);
				logger.error("Please provide valid transfer type");
				return false;
			}
			TransferUtils.setPaymentDetails(params, request);
			String ScheduledDate = CommonUtils.getParamValue(params, Constants.PARAM_SCHEDULED_DATE);
			if (StringUtils.isNotBlank(ScheduledDate)) {
				params.put(TransferConstants.PARAM_EXECUTION_DATE, CommonUtils.convertDateToYYYYMMDD(ScheduledDate));
			}
			String paymentContainer = EnvironmentConfigurationsHandler.getValue(T24_PAYMENT_CONTAINER, request);
			if (StringUtils.isNotBlank(paymentContainer) && Constants.TRUE.equalsIgnoreCase(paymentContainer)) {
				params.put(TransferConstants.PARAM_PAYMENT_CONTAINER, paymentContainer);
			}

			String transactionNotes = CommonUtils.getParamValue(params, Constants.PARAM_TRANSACTION_NOTES);
			if (StringUtils.isNotBlank(transactionNotes)) {
				if (transactionNotes.length() < Constants.PARAM_INST_ID_REFERENCE_LEN)
					params.put(Constants.PARAM_INST_ID_REFERENCE, transactionNotes);
				else
					params.put(Constants.PARAM_INST_ID_REFERENCE,
							transactionNotes.substring(0, Constants.PARAM_INST_ID_REFERENCE_LEN));

			}

			String creditValueDate = CommonUtils.getParamValue(params, Constants.CREDIT_VALUE_DATE);
            if (StringUtils.isNotBlank(creditValueDate)) {
                params.put(Constants.CREDIT_VALUE_DATE, CommonUtils.convertDateToYYYYMMDD(creditValueDate));
            }
			// Process Charges
			String charges = CommonUtils.getParamValue(params, TransferConstants.PARAM_CHARGES);
			logger.error("Charges Array + " + charges);
			if (StringUtils.isNotBlank(charges)) {
				JSONArray finalChargesArray = new JSONArray();

				try {
					JSONArray chargesArray = new JSONArray(charges);
					if (chargesArray.length() > 0) {
						for (Object item : chargesArray) {
							if ((item instanceof JSONObject)) {
								JSONObject chargeObject = new JSONObject(item.toString());
								if (chargeObject.has(TransferConstants.PARAM_CHARGE_ACC_CCY_ID)
										&& chargeObject.has(TransferConstants.PARAM_CHARGE_ACC_CCY_AMOUNT)) {
									continue;
								}
								if (chargeObject.has(TransferConstants.PARAM_CHARGE_ACC_CCY_ID)) {
									chargeObject.remove(TransferConstants.PARAM_CHARGE_ACC_CCY_ID);
								}
								if (chargeObject.has(TransferConstants.PARAM_CHARGE_ACC_CCY_AMOUNT)) {
									chargeObject.remove(TransferConstants.PARAM_CHARGE_ACC_CCY_AMOUNT);
								}
								finalChargesArray.put(chargeObject);
							}
						}
					}
				} catch (Exception e) {
					logger.error("Error while processing charges : " + charges + e);
				}

				params.remove(TransferConstants.PARAM_CHARGES);
				params.put(TransferConstants.PARAM_CHARGES, finalChargesArray.toString());
			}
			params.remove(TemenosConstants.USER_ID);

		} catch (Exception e) {
			logger.error("Exception occurred in One Time Transfer PreProcesor:" + e);
			return false;
		}

		return Boolean.TRUE;
	}

}

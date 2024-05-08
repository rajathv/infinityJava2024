package com.temenos.infinity.api.arrangements.resource.impl;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.arrangements.constants.ErrorCodeEnum;
import com.temenos.infinity.api.arrangements.dto.MortgageRepaymentDTO;
import com.temenos.infinity.api.arrangements.dto.PartialRepaymentDTO;
import com.temenos.infinity.api.arrangements.resource.api.SubmitChangeRepaymentResource;
import com.temenos.infinity.api.arrangements.resource.api.SubmitPartialRepaymentResource;
import com.temenos.infinity.api.arrangements.businessdelegate.api.SubmitChangeRepaymentBusinessDelegate;
import com.temenos.infinity.api.arrangements.businessdelegate.api.SubmitPartialRepaymentBusinessDelegate;


public class SubmitPartialRepaymentResourceImpl implements SubmitPartialRepaymentResource {

	private static final Logger LOG = LogManager.getLogger(SubmitPartialRepaymentResource.class);
	@Override
	public Result SubmitPartialRepayment(PartialRepaymentDTO partialRepayment, DataControllerRequest request, HashMap<String, Object> headerMap) {
		Result result = new Result();

		
		SubmitPartialRepaymentBusinessDelegate partialRepaymentBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(SubmitPartialRepaymentBusinessDelegate.class);
		
       
			try {
				partialRepayment = partialRepaymentBusinessDelegate.SubmitPartialRepayment(partialRepayment, headerMap);

				if (StringUtils.isBlank(partialRepayment.getId())) {

					return ErrorCodeEnum.ERR_20055.setErrorCode(new Result());
				}
				if (StringUtils.isNotBlank(partialRepayment.getErrorMessage())) {
					String dbpErrCode = partialRepayment.getCode();
					String dbpErrMessage = partialRepayment.getErrorMessage();
					if (StringUtils.isNotBlank(dbpErrMessage)) {
						String msg = ErrorCodeEnum.ERR_20061.getMessage(partialRepayment.getId(),
								partialRepayment.getStatus(), dbpErrMessage);
						return ErrorCodeEnum.ERR_20061.setErrorCode(new Result(), msg);
					}
				}
				JSONObject repaymentDayDTO = new JSONObject(partialRepayment);
				result = JSONToResult.convert(repaymentDayDTO.toString());

			} catch (Exception e) {
				LOG.error(e);
				LOG.debug("Failed to update repaymentDay in OMS " + e);
				return ErrorCodeEnum.ERR_20052.setErrorCode(new Result());
			}
		
		return result;
	}


}

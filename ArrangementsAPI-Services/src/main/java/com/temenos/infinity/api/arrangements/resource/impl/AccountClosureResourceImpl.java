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
import com.temenos.infinity.api.arrangements.businessdelegate.api.AccountClosureBusinessDelegate;
import com.temenos.infinity.api.arrangements.constants.ErrorCodeEnum;
import com.temenos.infinity.api.arrangements.dto.AccountClosureDTO;
import com.temenos.infinity.api.arrangements.resource.api.AccountClosureResource;


public class AccountClosureResourceImpl implements AccountClosureResource {

	private static final Logger LOG = LogManager.getLogger(AccountClosureResourceImpl.class);
	@Override
	public Result CloseAccount(AccountClosureDTO accountClosureDTO, DataControllerRequest request, HashMap<String, Object> headerMap) {
		Result result = new Result();

		
		AccountClosureBusinessDelegate accountClosureBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(AccountClosureBusinessDelegate.class);
		
       
			try {
				accountClosureDTO = accountClosureBusinessDelegate.CloseAccount(accountClosureDTO, headerMap);

				if (StringUtils.isBlank(accountClosureDTO.getId())) {
					return ErrorCodeEnum.ERR_20055.setErrorCode(new Result());
				}
				if (StringUtils.isNotBlank(accountClosureDTO.getErrorMessage())) {
					String dbpErrCode = accountClosureDTO.getCode();
					String dbpErrMessage = accountClosureDTO.getErrorMessage();
					if (StringUtils.isNotBlank(dbpErrMessage)) {
						String msg = ErrorCodeEnum.ERR_20061.getMessage(accountClosureDTO.getId(),
								accountClosureDTO.getStatus(), dbpErrMessage);
						return ErrorCodeEnum.ERR_20061.setErrorCode(new Result(), msg);
					}
				}
				JSONObject repaymentDayDTO = new JSONObject(accountClosureDTO);
				result = JSONToResult.convert(repaymentDayDTO.toString());

			} catch (Exception e) {
				LOG.error(e);
				LOG.debug("Failed to update repaymentDay in OMS " + e);
				return ErrorCodeEnum.ERR_20052.setErrorCode(new Result());
			}
		
		return result;
	}


}

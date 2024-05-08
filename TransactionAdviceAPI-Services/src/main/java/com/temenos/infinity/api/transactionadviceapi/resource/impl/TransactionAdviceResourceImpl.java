package com.temenos.infinity.api.transactionadviceapi.resource.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.transactionadviceapi.businessdelegate.api.TransactionAdviceBusinessDelegate;
import com.temenos.infinity.api.transactionadviceapi.config.ServerConfigurations;
import com.temenos.infinity.api.transactionadviceapi.constants.ErrorCodeEnum;
import com.temenos.infinity.api.transactionadviceapi.dto.AutoFormCookie;
import com.temenos.infinity.api.transactionadviceapi.dto.AutoFormDownload;
import com.temenos.infinity.api.transactionadviceapi.resource.api.TransactionAdviceResource;

public class TransactionAdviceResourceImpl implements TransactionAdviceResource {

	private static final Logger LOG = LogManager.getLogger(TransactionAdviceResourceImpl.class);

	@Override
	public Result loginAndDownload(String customerId, String accountId, String transactionRef, String mediaType,
			String transactionType, String page,String operation,String auth_token) {

		Result result = new Result();
		//If no AuthToken is passed to input for download(Public) throw Invalid Input Exception
		 if(operation.equalsIgnoreCase("download")&& StringUtils.isBlank(auth_token))
		{
			LOG.debug("Download Operation performed without Auth Token");
			return ErrorCodeEnum.ERR_20001.setErrorCode(new Result());
		}
		if (StringUtils.isBlank(customerId) || StringUtils.isBlank(accountId) || StringUtils.isBlank(transactionRef)
				|| StringUtils.isBlank(mediaType) || StringUtils.isBlank(transactionType)) {
			LOG.debug("Missing Params");
			return ErrorCodeEnum.ERR_20001.setErrorCode(new Result());
		}
		try {
			if (StringUtils.isBlank(ServerConfigurations.AUTOFORM_USERNAME.getValue())
					|| StringUtils.isBlank(ServerConfigurations.AUTOFORM_PASSWORD.getValue())) {
				LOG.debug("Missing AutoForm Creds, Kindly add it to environment variables");
				return ErrorCodeEnum.ERR_20005.setErrorCode(new Result());
			}
		} catch (Exception e) {

			LOG.debug("Missing AutoForm Creds, Kindly add it to environment variables");
			return ErrorCodeEnum.ERR_20005.setErrorCode(new Result());
		}
		try {

			TransactionAdviceBusinessDelegate loginAndDownloadBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(TransactionAdviceBusinessDelegate.class);
			// Login Operation
			AutoFormCookie LoginResult = loginAndDownloadBusinessDelegate.login(auth_token);

			String xsrf = LoginResult.getXsrftoken();
			String jsessionid = LoginResult.getJSESSIONID();

			if (StringUtils.isBlank(xsrf) || StringUtils.isBlank(jsessionid)) {
				LOG.debug("Login Failed");
				return ErrorCodeEnum.ERR_20004.setErrorCode(new Result());
			}

			// Search Operation
			AutoFormDownload document = loginAndDownloadBusinessDelegate.search(customerId, accountId, transactionRef,
					mediaType, transactionType, page, xsrf, jsessionid,auth_token);

			// Download Operation
			if (document.getDocumentId() != null && document.getRevision() != null) {
				String DownloadResult = loginAndDownloadBusinessDelegate.download(document.getDocumentId(),
						document.getRevision(), xsrf, jsessionid,auth_token);
				result.addParam("base64", DownloadResult);
				LOG.debug("Document Retrieved Successfully");
			} else
				{LOG.debug("Document Retrieve Failed");
				return ErrorCodeEnum.ERR_20003.setErrorCode(new Result());}

		} catch (Exception e) {
			LOG.error(e);
			LOG.debug("Document Retrieve Failed");
			return ErrorCodeEnum.ERR_20041.setErrorCode(new Result());
		}
		return result;
	}

}
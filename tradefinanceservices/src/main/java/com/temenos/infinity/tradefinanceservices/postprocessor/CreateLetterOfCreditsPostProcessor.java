package com.temenos.infinity.tradefinanceservices.postprocessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.LetterOfCreditsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.CreateLetterOfCreditsBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.constants.Constants;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditStatusDTO;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsActionDTO;

public class CreateLetterOfCreditsPostProcessor implements DataPostProcessor2{
	
	 private static final Logger LOG = LogManager.getLogger(CreateLetterOfCreditsPostProcessor.class);
	 
	 @Override
		public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
				throws Exception {
			
			CreateLetterOfCreditsBusinessDelegate locBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(CreateLetterOfCreditsBusinessDelegate.class);
			
			String signatoryApprovalRequired = request.getParameter("signatoryApprovalRequired")!= null ? request.getParameter("signatoryApprovalRequired") : "";
			String backendEndId = request.getParameter("backendEndId")!= null ? request.getParameter("backendEndId") : "";
			String isSelfApprovalFlag = request.getParameter("isSelfApprovalFlag") != null ? request.getParameter("isSelfApprovalFlag") : "";
			String isSrmsFailed = request.getParameter("isSrmsFailed") != null ? request.getParameter("isSrmsFailed") : "";
			
			if ("true".equalsIgnoreCase(signatoryApprovalRequired)) {
				String requestId = request.getParameter("requestId") != null ? request.getParameter("requestId") : "";
				
				if ("true".equalsIgnoreCase(isSrmsFailed)) {
	               if(!locBusinessDelegate.deleteLetterOfCreditFromApprovalQueue(requestId)) {
	            	   LOG.error("Error occured while deleting the request id from approval queue");
					   return ErrorCodeEnum.ERR_26023.setErrorCode(new Result());
	               }
				} else {
					
					String featureActionId = Constants.CREATE_LOC_FEATURE_ACTION_ID;
					boolean isSeflApproval = Boolean.parseBoolean(isSelfApprovalFlag);
					
					LetterOfCreditStatusDTO letterOfCreditDTO = locBusinessDelegate.updateBackendIdInApprovalQueue(requestId,
							backendEndId, isSeflApproval, featureActionId, request);
					if (letterOfCreditDTO == null) {
					     LOG.error("Error occured while updating the backend id in approval queue");
						return ErrorCodeEnum.ERR_26022.setErrorCode(new Result());
					}
					if(letterOfCreditDTO.getDbpErrCode() != null || letterOfCreditDTO.getDbpErrMsg() != null){
						result.addParam(new Param("dbpErrCode", letterOfCreditDTO.getDbpErrCode()));
						result.addParam(new Param("dbpErrMsg", letterOfCreditDTO.getDbpErrMsg()));
						return result;
					}
					
					if(Constants.SENT.equalsIgnoreCase(letterOfCreditDTO.getStatus())) {
						LetterOfCreditsBackendDelegate approveBackendDelegate = DBPAPIAbstractFactoryImpl
				                .getBackendDelegate(LetterOfCreditsBackendDelegate.class);
						
						LetterOfCreditsActionDTO letterOfCredit = new LetterOfCreditsActionDTO();
						letterOfCredit.setComments("Approved");
						letterOfCredit.setRequestId(backendEndId);
						letterOfCredit.setSignatoryAction("true");
						letterOfCredit = approveBackendDelegate.approveLetterOfCredit(letterOfCredit, request);
						if(letterOfCredit == null) {
							 LOG.error("Error occured while updating the backend id in approval queue");
							 return ErrorCodeEnum.ERR_26022.setErrorCode(new Result());
						}
						result.addParam("status", "Request placed");
						
					}
				}
			}
			return result;
		}

}

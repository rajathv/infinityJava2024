package com.temenos.infinity.api.wealth.preandpostprocessors;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.sessionmanager.SessionScope;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthservices.util.PortfolioWealthUtils;

/**
 * (INFO) If status is set as a part of the request , the operation is exited
 * else operation is executed.
 * 
 * @author balaji.krishnan
 *
 */

public class GetPastProposalOrchPreProcessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(GetPastProposalOrchPreProcessor.class);

	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {
			Set<String> userPermissions = SessionScope.getAllPermissionsFromIdentityScope(request);
			 if (!userPermissions.contains(TemenosConstants.INVESTMENT_PROPOSAL_PAST_PROPOSAL_VIEW)) {
	            	result.addParam("opstatus", "1582");
	            	result.addParam("status", TemenosConstants.FAILURE);
					result.addParam("error", "Logged in user not authorized to perform this action");
	                return false;
	            }
			 String wealthCore = PortfolioWealthUtils.getWealthCoreFromCache(request);
			 String portfolioId = null, portfolioservicetype = "", filter = "";
			 if(wealthCore.equals("Mock")){
	            if (inputMap.get(TemenosConstants.PORTFOLIOID) != null
	                    && inputMap.get(TemenosConstants.PORTFOLIOID).toString().trim().length() > 0) {
	                portfolioId = inputMap.get(TemenosConstants.PORTFOLIOID).toString();
	            } else {
	                return unauthAccess(result,TemenosConstants.PORTFOLIOID);
	        }
	            if (inputMap.get(TemenosConstants.PORTFOLIOSERVICETYPE) != null 
	                    && inputMap.get(TemenosConstants.PORTFOLIOSERVICETYPE).toString().trim().length() > 0) {
	                portfolioservicetype = inputMap.get(TemenosConstants.PORTFOLIOSERVICETYPE).toString();
	            } else {
	                return unauthAccess(result,TemenosConstants.PORTFOLIOSERVICETYPE);
	            }
			 }
			 if((wealthCore.equals("TAP")) || (wealthCore.equals("TAP,Refinitiv"))){
		            if (inputMap.get(TemenosConstants.CONTEXTID) != null
		                    && inputMap.get(TemenosConstants.CONTEXTID).toString().trim().length() > 0) {
		                portfolioId = inputMap.get(TemenosConstants.CONTEXTID).toString();
		            } else {
		                return unauthAccess(result,TemenosConstants.CONTEXTID);
		        }
		            if (inputMap.get(TemenosConstants.FILTER) != null && inputMap.get(TemenosConstants.FILTER).toString().trim().length() > 0) {
		                filter = inputMap.get(TemenosConstants.FILTER).toString();
		            } else {
		                return unauthAccess(result,TemenosConstants.FILTER);
		            }
				 }
	            List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);
	            //String wealthCore = "";
	            
	            //if (allportfoliosList.contains(portfolioId) && portfolioservicetype.equalsIgnoreCase("Advisory"))
	            if ((allportfoliosList.contains(portfolioId) && portfolioservicetype.equalsIgnoreCase("Advisory")) || portfolioId!=null && (filter.equalsIgnoreCase("previousYear") || filter.equalsIgnoreCase("currentYear")))
	            {          
	           	 //wealthCore = PortfolioWealthUtils.getWealthCoreFromCache(request);
	                request.addRequestParam_(TemenosConstants.WEALTH_CORE, wealthCore);
	                    return true;
	                } 
	            else {
	                LOG.error("Invalid request");
	                result.addParam("status", "Failure");
	                result.addParam("error", "Unauthorized Access");
	                return false;
	            }
				
			} catch (Exception e) {
				LOG.error("Error in GetPastProposalOrchPreProcessor" + e);
				e.getMessage();
				return false;
			}

		}
		private boolean unauthAccess(Result result, String param) {
		    LOG.error("Error:Invalid input! , Mandatory fields not given");
		    result.addParam("status", "Failure");
		    result.addParam("error", "Invalid Input!  " + param + " is mandatory.");
		    result.addHttpStatusCodeParam("0");
		    return false;
		}
	}

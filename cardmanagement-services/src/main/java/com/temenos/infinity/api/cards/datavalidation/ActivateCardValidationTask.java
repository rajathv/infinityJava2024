package com.temenos.infinity.api.cards.datavalidation;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceInvocationWrapper;
import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.util.JSONUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.temenos.infinity.api.cards.dto.CardsDTO;
import com.temenos.infinity.api.cards.constants.OperationName;
import com.temenos.infinity.api.cards.constants.ServiceId;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.temenos.infinity.api.cards.utils.CardsManager;
import com.temenos.infinity.api.cards.utils.SessionMap;
import com.temenos.infinity.api.cards.utils.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.dataobject.Result;

public class ActivateCardValidationTask  implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(ActivateCardValidationTask.class);
	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		if(!HelperMethods.isDACEnabled()) {
			LOG.debug("data access control is disabled");
			return true;
		}
		JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
    	JsonObject resPayload = null;
    	resPayload = ErrorCodeEnum.ERR_10152.setErrorCode(resPayload);  
		String userId = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
		if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
			JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
			if(requestPayload.has("MFAAttributes"))
			{
				requestPayload.remove("MFAAttributes");
			}
			if(requestPayload.has("mfaState"))
			{
				requestPayload.remove("mfaState");
			}
			if(requestPayload.has("mfaMode"))
			{
				requestPayload.remove("mfaMode");
			}
			if(requestPayload.has("mfaType"))
			{
				requestPayload.remove("mfaType");
			}
			if(requestPayload.has("serviceKey"))
			{
				requestPayload.remove("serviceKey");
			}
			if (requestPayload.has("isMFARequired"))
			{
				requestPayload.remove("isMFARequired");
			}
			fabricResponseManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
			String	cardId = HelperMethods.getStringFromJsonObject(requestPayload, "cardId");
			CardsManager cardManager = new CardsManager(fabricRequestManager, fabricResponseManager);
			 SessionMap cardsMap = cardManager.getCardsFromSession(userId);
			 SessionMap cardsMapWithType = cardManager.getCardsFromSessionWithType(userId);
		        if ((null == cardsMap)||(cardsMap.hasKey(cardId)==false)) {
					fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
		            return false;
		        }
		        if(cardsMap.hasKey(cardId))
		        {
		        	if(cardsMapWithType.hasKey(cardId+"-"+"Credit"))
		        	{
		        		HelperMethods.setCardType("Credit");
		        	}
		        	else if(cardsMapWithType.hasKey(cardId+"-"+"Debit"))
		        	{
		        		HelperMethods.setCardType("Debit");
		        	}
		        }
			
			String	cvv = HelperMethods.getStringFromJsonObject(requestPayload, "cvv");
			String	oldcvv = HelperMethods.getStringFromJsonObject(requestPayload, "oldcvv");
			String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
			String operationNameForGet = OperationName.DB_CARDS_GET;
			HashMap<String, Object> hashMap = new HashMap<>();
			if(StringUtils.isNotBlank(cardId)&&StringUtils.isNotBlank(cvv))
			{
				 String loggedInUserID = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
				 String filter="User_id" + " eq " + loggedInUserID + " and " + "id"
							+ " eq " + cardId;
					hashMap.put("$filter", filter);
					@SuppressWarnings("deprecation")
					String cardNumber = DBPServiceInvocationWrapper.invokeServiceAndGetJSON(serviceName, null,
							operationNameForGet, hashMap, null, "");
					JSONObject jsonRsponse = new JSONObject(cardNumber);
					JSONArray countJsonArr = jsonRsponse.getJSONArray("card");
					JSONObject cardNumberdata = countJsonArr.getJSONObject(0);
					Long cardNumberVal=cardNumberdata.getLong("cardNumber");
					if(countJsonArr.length()<1)
					{
						return false;
					}
					hashMap.clear();
                if(StringUtils.isNotBlank(oldcvv))
                {
				String filtercvv = "User_id" + " eq " + loggedInUserID + " and " + "cardNumber"
						+ " eq " + cardNumberVal+" and "+"("+"cvv" + " eq " + cvv + " or "+"cvv" + " eq " + oldcvv+")";
				hashMap.put("$filter", filtercvv);
				String updateCards = DBPServiceInvocationWrapper.invokeServiceAndGetJSON(serviceName, null,
						operationNameForGet, hashMap, null, "");
				JSONObject jsonRs = new JSONObject(updateCards);
				JSONArray countJsonArray = jsonRs.getJSONArray("card");
				hashMap.clear();
				if(countJsonArray.length()<2)
				{
					resPayload = ErrorCodeEnum.ERR_21016.setErrorCode(resPayload);  
					fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);	
					return false;
                }
				else
				{
					int flag=0;
					for(int i=0;i<countJsonArray.length();i++)
					{
						JSONObject rec = countJsonArray.getJSONObject(i);
						String cvvVal=Integer.toString(rec.getInt("cvv"));
						String cardid=Integer.toString(rec.getInt("Id"));
						if(cardid.equals(cardId))
						{
							flag=1;
						}
					}
						
							if(flag==0)
							{
								resPayload = ErrorCodeEnum.ERR_21016.setErrorCode(resPayload);  
								fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);	
								return false;
							}
						
					
					}
				
                }
                else
                {
                	String filtercvv = "User_id" + " eq " + loggedInUserID + " and " +"cardNumber"
    						+ " eq " + cardNumberVal+" and "+"("+"cvv" + " eq " + cvv +")";
                	hashMap.put("$filter", filtercvv);
                	String updateCards = DBPServiceInvocationWrapper.invokeServiceAndGetJSON(serviceName, null,
    						operationNameForGet, hashMap, null, "");
                	JSONObject jsonRs = new JSONObject(updateCards);
    				JSONArray countJsonArray = jsonRs.getJSONArray("card");
    				hashMap.clear();
    				if(countJsonArray.length()!=1)
    				{
    					resPayload = ErrorCodeEnum.ERR_21016.setErrorCode(resPayload);  
    					fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);	
    					return false;
                    }
                }
                
			
			}
			else
			{
				resPayload = ErrorCodeEnum.ERR_10152.setErrorCode(resPayload);  
				fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);	
				return false;
			}
		
		}
	//	fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);	
		return true;
	}
	 
		
}
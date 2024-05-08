package com.kony.memorymgmt;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.model.SavingsPotHelper;
import com.kony.utilities.Constants;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SavingsPotManager {
	private static final Logger LOG = LogManager.getLogger(AccountsManager.class);

    private FabricRequestManager fabricRequestManager = null;
    private String customerId = null;
    private static final String SAVINGS_POTS = "SAVINGS_POTS";
    
    public SavingsPotManager(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) {
        this.fabricRequestManager = fabricRequestManager;
        this.customerId = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
    }

    public SavingsPotManager(FabricRequestManager fabricRequestManager) {
        this.fabricRequestManager = fabricRequestManager;
        this.customerId = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
    }
    
    public SavingsPotManager(String customerId) {
        this.customerId = customerId;
    }

    public SessionMap getSavingsPotFromSession(String customerId) {
        SessionMap savingsPotMap = (SessionMap) MemoryManager.retrieve(this.fabricRequestManager,
        		SavingsPotManager.SAVINGS_POTS + this.customerId);
        if (null == savingsPotMap || savingsPotMap.isEmpty()) {
            LOG.debug("savingsPotMap is null. Reload function will be triggered.");
            SavingsPotHelper.reloadSavingsPotsOfUserIntoSession(fabricRequestManager);
            savingsPotMap = (SessionMap) MemoryManager.retrieve(this.fabricRequestManager,
            		SavingsPotManager.SAVINGS_POTS + this.customerId);
        }

        return savingsPotMap;
    }
    
    public void saveSavingsPotIntoSession(SessionMap savingsPotMap) {
        if (null != savingsPotMap) {
            MemoryManager.save(this.fabricRequestManager, SavingsPotManager.SAVINGS_POTS + this.customerId,
            		savingsPotMap);
        }
    }

	public boolean validateSavingsPot(String customerId, String savingsPotId) {
		   	if(StringUtils.isBlank(savingsPotId)) {
		    		return false;
		    	}
		        SessionMap savingsPotMap = getSavingsPotMapFromSession(customerId);

		        if (null == savingsPotMap || savingsPotMap.isEmpty()) {
		            LOG.debug("validateSavingsPot - savingsPotMap Null / Empty");
		            return false;
		        }

		        LOG.debug("validateSavingsPot: " + savingsPotMap.toString());
		        return savingsPotMap.hasKey(savingsPotId);
		        
    }
    
   public SessionMap getSavingsPotMapFromSession(String customerId) {
    SessionMap savingsPotMap = (SessionMap) MemoryManager.retrieve(this.fabricRequestManager,
    		SavingsPotManager.SAVINGS_POTS + this.customerId);
    if (null == savingsPotMap || savingsPotMap.isEmpty()) {
        LOG.debug("savingsPotMap is null. Reload function will be triggered.");
        SavingsPotHelper.reloadSavingsPotsOfUserIntoSession(fabricRequestManager);
        savingsPotMap = (SessionMap) MemoryManager.retrieve(this.fabricRequestManager,
        		SavingsPotManager.SAVINGS_POTS + this.customerId);
    }
     return savingsPotMap;
    }
  
   public boolean isSavingsPotClosed(String customerId, String savingsPotId) {
	   	if(StringUtils.isBlank(savingsPotId)) {
	    		return false;
	    	}
	        SessionMap savingsPotMap = getSavingsPotMapFromSession(customerId);

	        if (null == savingsPotMap || savingsPotMap.isEmpty()) {
	            LOG.debug("isSavingsPotClosed - savingsPotMap Null / Empty");
	            return false;
	        }

	        LOG.debug("isSavingsPotClosed: " + savingsPotMap.toString());
	        String potStatus =  savingsPotMap.getAttributeValueForKey(savingsPotId, Constants.STATUS);
	        if(potStatus.equalsIgnoreCase(Constants.CLOSED))
	        	return true;
	        else
	        	return false;
}

public String getFundingAccount(String customerId, String savingsPotId) {
    SessionMap savingsPotMap = getSavingsPotMapFromSession(customerId);
    if (null == savingsPotMap || savingsPotMap.isEmpty()) {
        LOG.debug("getFundingAccount - savingsPotMap Null / Empty");
        return "";
    }
    LOG.debug("getFundingAccount: " + savingsPotMap.toString());
    String fundingAccountId =  savingsPotMap.getAttributeValueForKey(savingsPotId, Constants.FUNDINGACCOUNTID);
    return fundingAccountId;
}

public String getAvailableBalanceInPot(String customerId, String savingsPotId) {
    SessionMap savingsPotMap = getSavingsPotMapFromSession(customerId);
    if (null == savingsPotMap || savingsPotMap.isEmpty()) {
        LOG.debug("getAvailableBalanceInPot - savingsPotMap Null / Empty");
        return "";
    }
    LOG.debug("getAvailableBalanceInPot: " + savingsPotMap.toString());
    String availableBalance =  savingsPotMap.getAttributeValueForKey(savingsPotId, Constants.AVAILABLEBALANCE);
    return availableBalance;
}
public void closeSavingsPotInSession(String customerId, JsonElement savingsPotId) {
    SessionMap savingsPotMap = getSavingsPotMapFromSession(customerId);
    if (null == savingsPotMap || savingsPotMap.isEmpty()) {
        LOG.debug("getFundingAccount - savingsPotMap Null / Empty");
    }
    LOG.debug("closeSavingsPotInSession: " + savingsPotMap.toString());
    savingsPotMap.addAttributeForKey(savingsPotId.getAsString(),Constants.STATUS,Constants.CLOSED);
    saveSavingsPotIntoSession(savingsPotMap);
}

public void addSavingsPotInSession(String customerId, JsonElement savingsPotId , JsonElement fundingAccountId, JsonElement fundingAccountHoldingsId, String potType) {
    SessionMap savingsPotMap = getSavingsPotMapFromSession(customerId);
    if (null == savingsPotMap || savingsPotMap.isEmpty()) {
        LOG.debug("getFundingAccount - savingsPotMap Null / Empty");
    }
    LOG.debug("addSavingsPotInSession: " + savingsPotMap.toString());
    savingsPotMap.addAttributeForKey(savingsPotId.getAsString(),Constants.FUNDINGACCOUNTID,fundingAccountId.getAsString());
    savingsPotMap.addAttributeForKey(savingsPotId.getAsString(),Constants.AVAILABLEBALANCE,"0");
    savingsPotMap.addAttributeForKey(savingsPotId.getAsString(),Constants.STATUS,Constants.ACTIVE);
    savingsPotMap.addAttributeForKey(savingsPotId.getAsString(),Constants.FUNDINGACCOUNTHOLDINGSID,fundingAccountHoldingsId.getAsString());
    savingsPotMap.addAttributeForKey(savingsPotId.getAsString(),Constants.POTTYPE,potType);
    saveSavingsPotIntoSession(savingsPotMap);
}

public void setAvailableBalanceInPot(String customerId, JsonElement savingsPotId, String updatedAvailableBalance) {
    SessionMap savingsPotMap = getSavingsPotMapFromSession(customerId);
    if (null == savingsPotMap || savingsPotMap.isEmpty()) {
        LOG.debug("getAvailableBalanceInPot - savingsPotMap Null / Empty");
    }
    LOG.debug("setAvailableBalanceInPot: " + savingsPotMap.toString());
    savingsPotMap.addAttributeForKey(savingsPotId.getAsString(), Constants.AVAILABLEBALANCE,updatedAvailableBalance);
    saveSavingsPotIntoSession(savingsPotMap);
}
public String getFundingAccountHoldingsId(String customerId, String savingsPotId) {
    SessionMap savingsPotMap = getSavingsPotMapFromSession(customerId);
    if (null == savingsPotMap || savingsPotMap.isEmpty()) {
        LOG.debug("getFundingAccountHoldingsId - savingsPotMap Null / Empty");
        return "";
    }
    LOG.debug("getFundingAccountHoldingsId: " + savingsPotMap.toString());
    String fundingAccountHoldingsId =  savingsPotMap.getAttributeValueForKey(savingsPotId, Constants.FUNDINGACCOUNTHOLDINGSID);
    return fundingAccountHoldingsId;
}
public String getPotType(String customerId, String savingsPotId) {
    SessionMap savingsPotMap = getSavingsPotMapFromSession(customerId);
    if (null == savingsPotMap || savingsPotMap.isEmpty()) {
        LOG.debug("getPotType - savingsPotMap Null / Empty");
        return "";
    }
    LOG.debug("getPotType: " + savingsPotMap.toString());
    String potType =  savingsPotMap.getAttributeValueForKey(savingsPotId, Constants.POTTYPE);
    return potType;
}
}

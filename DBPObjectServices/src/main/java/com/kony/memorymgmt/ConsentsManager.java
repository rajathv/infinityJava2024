package com.kony.memorymgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.HelperMethods;
import com.kony.model.PayeeHelper;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class ConsentsManager {
	private static final Logger LOG = LogManager.getLogger(ConsentsManager.class);

    private FabricRequestManager fabricRequestManager = null;
    private String customerId = null;
    private static final String CDP_CONSENTS_ARRANGEMENT_ID = "CDP_CONSENTS_ARRANGEMENT_ID";
    private static final String PSD_CONSENTS_ARRANGEMENT_ID = "PSD_CONSENTS_ARRANGEMENT_ID";
    
    public ConsentsManager(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) {
        this.fabricRequestManager = fabricRequestManager;
        this.customerId = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
    }

    public ConsentsManager(FabricRequestManager fabricRequestManager) {
        this.fabricRequestManager = fabricRequestManager;
        this.customerId = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
    }
	
	public void saveCDPConsentIntoSession(SessionMap externalAccountsMap) {
	        if (null != externalAccountsMap) {
	            MemoryManager.save(this.fabricRequestManager, ConsentsManager.CDP_CONSENTS_ARRANGEMENT_ID + this.customerId,
	                    externalAccountsMap);
	        }
	    }
	
	public void savePSDConsentIntoSession(SessionMap externalAccountsMap) {
        if (null != externalAccountsMap) {
            MemoryManager.save(this.fabricRequestManager, ConsentsManager.PSD_CONSENTS_ARRANGEMENT_ID + this.customerId,
                    externalAccountsMap);
        }
    }
	
	public SessionMap getPSDConsentFromSession(String customerId) {
        SessionMap consentsMap =
                (SessionMap) MemoryManager.retrieve(this.fabricRequestManager, ConsentsManager.PSD_CONSENTS_ARRANGEMENT_ID + this.customerId);
        return consentsMap;
    }
	
	public SessionMap getCDPConsentFromSession(String customerId) {
        SessionMap consentsMap =
                (SessionMap) MemoryManager.retrieve(this.fabricRequestManager, ConsentsManager.CDP_CONSENTS_ARRANGEMENT_ID + this.customerId);
        return consentsMap;
    }

}

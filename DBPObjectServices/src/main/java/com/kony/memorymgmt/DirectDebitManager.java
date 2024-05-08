package com.kony.memorymgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.HelperMethods;
import com.kony.model.PayeeHelper;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class DirectDebitManager {
	private static final Logger LOG = LogManager.getLogger(DirectDebitManager.class);

    private FabricRequestManager fabricRequestManager = null;
    private String customerId = null;
    private static final String DIRECT_DEBIT_ID = "DIRECT_DEBIT_ID";
    
    public DirectDebitManager(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) {
        this.fabricRequestManager = fabricRequestManager;
        this.customerId = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
    }

    public DirectDebitManager(FabricRequestManager fabricRequestManager) {
        this.fabricRequestManager = fabricRequestManager;
        this.customerId = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
    }
	
	public void saveDirectDebitIntoSession(SessionMap externalAccountsMap) {
	        if (null != externalAccountsMap) {
	            MemoryManager.save(this.fabricRequestManager, DirectDebitManager.DIRECT_DEBIT_ID + this.customerId,
	                    externalAccountsMap);
	        }
	    }
	
	public SessionMap getDirectDebitFromSession(String customerId) {
        SessionMap consentsMap =
                (SessionMap) MemoryManager.retrieve(this.fabricRequestManager, DirectDebitManager.DIRECT_DEBIT_ID + this.customerId);
        return consentsMap;
    }

}

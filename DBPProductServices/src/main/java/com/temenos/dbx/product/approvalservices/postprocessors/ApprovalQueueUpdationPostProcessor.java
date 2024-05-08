package com.temenos.dbx.product.approvalservices.postprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.approvalservices.resource.api.ApprovalQueueResource;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;


public class ApprovalQueueUpdationPostProcessor implements ObjectServicePostProcessor {

	    private static final Logger LOG = LogManager.getLogger(ApprovalQueueUpdationPostProcessor.class);
	    
		@Override
		public void execute(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
				throws Exception {
			// TODO Auto-generated method stub
			try {
				//Initializing of ApprovalQueueResource through Abstract factory method
				ApprovalQueueResource approvalQueueResource = DBPAPIAbstractFactoryImpl.getResource(ApprovalQueueResource.class);
				
				approvalQueueResource.autoRejectPendingTransactionsInApprovalQueue(fabricRequestManager, fabricResponseManager);
			}
			catch(Exception e) {
				LOG.error("iFailed to update the approval queue after users permission change: ");
			}
			
		}
}

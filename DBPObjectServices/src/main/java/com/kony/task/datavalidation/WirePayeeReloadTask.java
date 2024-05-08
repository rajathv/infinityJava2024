package com.kony.task.datavalidation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.kony.model.PayeeHelper;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class WirePayeeReloadTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(WirePayeeReloadTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			PayeeHelper.reloadWireTransferPayeesIntoSession(fabricRequestManager);
		} catch (Exception e) {
			LOG.error("Error while loading payees into session", e);
		}
		return true;
	}

}
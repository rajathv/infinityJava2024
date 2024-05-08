/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.businessdelegate.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.CreateExportLCBackendDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.CreateExportLCBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.dto.ExportLOCDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateExportLCBusinessDelegateImpl implements CreateExportLCBusinessDelegate {

	private static final Logger LOG = LogManager.getLogger(CreateExportLCBusinessDelegateImpl.class);

	@Override
	public ExportLOCDTO createExportLetterOfCredit(ExportLOCDTO createPayloadDTO, DataControllerRequest request) {
		CreateExportLCBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(CreateExportLCBackendDelegate.class);
		ExportLOCDTO exportLCDTO = new ExportLOCDTO();
		try {
			exportLCDTO = backendDelegate.createExportLetterOfCredit(createPayloadDTO, request);
		} catch (Exception e) {
			LOG.error("Error occurred while creating export letter of credit. " + e);
		}
		return exportLCDTO;
	}

	public ExportLOCDTO updateExportLetterOfCredit(ExportLOCDTO createPayloadDTO, DataControllerRequest request) {
		CreateExportLCBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(CreateExportLCBackendDelegate.class);
		ExportLOCDTO exportLCDTO = new ExportLOCDTO();
		try {
			exportLCDTO = backendDelegate.updateExportLetterOfCredit(createPayloadDTO, request);
		} catch (Exception e) {
			LOG.error("Error occurred while updating export letter of credit.  " + e);
		}
		return exportLCDTO;
	}

}

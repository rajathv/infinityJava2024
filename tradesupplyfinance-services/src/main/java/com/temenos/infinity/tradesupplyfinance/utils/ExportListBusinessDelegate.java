/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.utils;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.commons.exception.ApplicationException;

import java.util.List;

/**
 * @author k.meiyazhagan
 */
public interface ExportListBusinessDelegate {
    <T> List<T> getRecordsList(DataControllerRequest request) throws ApplicationException;
}

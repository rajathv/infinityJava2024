/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.businessdelegate.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.GuaranteesBackendDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.GuaranteesBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.dto.GuranteesDTO;
import com.temenos.infinity.tradefinanceservices.utils.ExcelBusinessDelegate;

import java.util.List;

public class GuaranteesBusinessDelegateImpl implements GuaranteesBusinessDelegate, ExcelBusinessDelegate {

    public GuranteesDTO createGuarantees(GuranteesDTO guaranteesDto, DataControllerRequest request) {

        GuaranteesBackendDelegate guaranteesBackend = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(GuaranteesBackendDelegate.class);
        return guaranteesBackend.createGuarantees(guaranteesDto, request);
    }

    public GuranteesDTO updateGuarantees(GuranteesDTO guaranteesDto, DataControllerRequest request) {

        GuaranteesBackendDelegate guaranteesBackend = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(GuaranteesBackendDelegate.class);
        return guaranteesBackend.updateGuarantees(guaranteesDto, request);
    }

    @Override
    public GuranteesDTO getGuaranteesById(String srmsId, DataControllerRequest request) {
        GuaranteesBackendDelegate guaranteesBackend = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(GuaranteesBackendDelegate.class);
        return guaranteesBackend.getGuaranteesById(srmsId, request);
    }

    @Override
    public List<GuranteesDTO> getGuranteesLC(GuranteesDTO guranteesDTO, DataControllerRequest request) {
        GuaranteesBackendDelegate guaranteesBackend = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(GuaranteesBackendDelegate.class);
        return guaranteesBackend.getGuranteesLC(guranteesDTO, request);
    }

    @Override
    public List<GuranteesDTO> getList(DataControllerRequest request) throws ApplicationException {
        GuaranteesBackendDelegate guaranteesBackend = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(GuaranteesBackendDelegate.class);
        return guaranteesBackend.getGuranteesLC(null, request);
    }
}

/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.businessdelegate.api;

import com.dbp.core.api.BusinessDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.dto.GuranteesDTO;

import java.util.List;

public interface GuaranteesBusinessDelegate extends BusinessDelegate {
    GuranteesDTO createGuarantees(GuranteesDTO guaranteesDto, DataControllerRequest request);

    GuranteesDTO updateGuarantees(GuranteesDTO guaranteesDto, DataControllerRequest request);

    GuranteesDTO getGuaranteesById(String srmsId, DataControllerRequest request);

    List<GuranteesDTO> getGuranteesLC(GuranteesDTO guranteesDTO, DataControllerRequest request);
}

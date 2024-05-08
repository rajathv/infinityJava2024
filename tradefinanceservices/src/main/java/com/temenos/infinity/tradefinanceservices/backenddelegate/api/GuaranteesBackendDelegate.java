/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.api;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.dto.ClauseDTO;
import com.temenos.infinity.tradefinanceservices.dto.GuranteesDTO;

import java.util.List;
import java.util.Map;

public interface GuaranteesBackendDelegate extends BackendDelegate {
	GuranteesDTO createGuarantees(GuranteesDTO guaranteesDto, DataControllerRequest request);

	GuranteesDTO updateGuarantees(GuranteesDTO guaranteesDto, DataControllerRequest request);

	GuranteesDTO getGuaranteesById(String srmsId, DataControllerRequest request);

    List<GuranteesDTO> getGuranteesLC(GuranteesDTO guranteesDTO, DataControllerRequest request);

	List<ClauseDTO> createClauses(Map<String, Object> requestParameters, DataControllerRequest dataControllerRequest);
}

package com.temenos.dbx.party.businessdelegate.impl;

import java.util.Map;

import com.kony.dbputilities.util.logger.LoggerUtil;
import com.temenos.dbx.party.businessdelegate.api.ProspectBusinessDelegate;
import com.temenos.dbx.party.utils.PartyUtils;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.PartyDTO;
import com.temenos.dbx.product.utils.CustomerUtils;
import com.temenos.dbx.product.utils.DTOUtils;

public class ProspectBusinessDelegateImpl
implements ProspectBusinessDelegate {

    private LoggerUtil logger;

    @Override
    public DBXResult saveProspect(PartyDTO partyDTO, Map<String, Object> map) {
        logger = new LoggerUtil(ProspectBusinessDelegateImpl.class);
        DBXResult dbxResult = new DBXResult();
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(partyDTO.getPartyId());
        customerDTO.setUserName(partyDTO.getPartyId());
        customerDTO.setIsNew(true);
        PartyUtils.updateCustomerDTOFromPartyDTO(customerDTO, partyDTO);
        logger.debug("CustomerDTO for create customer Service is :"+DTOUtils.getJsonObjectFromObjectPrint(customerDTO).toString());
        
        if(DTOUtils.persistObject(customerDTO, map)) {
            dbxResult.setResponse(customerDTO.getId());
        }
        else {
            dbxResult.setDbpErrMsg("Propect Ceartion Failed");
        }
        
        return dbxResult;
    }

    @Override
    public DBXResult updateProspect(PartyDTO partyDTO, Map<String, Object> map) {
        logger = new LoggerUtil(ProspectBusinessDelegateImpl.class);
        DBXResult dbxResult = new DBXResult();
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO = (CustomerDTO) DTOUtils.buildDTOFromDatabase(customerDTO, partyDTO.getPartyId(), true);
        PartyUtils.updateCustomerDTOFromPartyDTO(customerDTO, partyDTO);
        logger.debug("CustomerDTO for update customer Service is :"+DTOUtils.getJsonObjectFromObjectPrint(customerDTO).toString());
        if(DTOUtils.persistObject(customerDTO, map)) {
            dbxResult.setResponse(customerDTO.getId());
        }
        else {
            dbxResult.setDbpErrMsg("Propect Ceartion Failed");
        }
        
        return dbxResult;
    }

}

package com.temenos.infinity.api.savingspot.businessdelegate.impl;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.infinity.api.savingspot.backenddelegate.api.SavingsPotBackendDelegate;
import com.temenos.infinity.api.savingspot.businessdelegate.api.SavingsPotBusinessDelegate;
import com.temenos.infinity.api.savingspot.dto.SavingsPotCategoriesDTO;
import com.temenos.infinity.api.savingspot.dto.SavingsPotDTO;

public class SavingsPotBusinessDelegateImpl implements SavingsPotBusinessDelegate{

	private static final Logger LOG = LogManager.getLogger(SavingsPotBusinessDelegateImpl.class);

	@Override
	public List<SavingsPotDTO> getAllSavingsPot(String fundingAccountId, Map<String, Object> headersMap){
	  try {
		SavingsPotBackendDelegate backendDelegate =
                DBPAPIAbstractFactoryImpl.getBackendDelegate(SavingsPotBackendDelegate.class);
        List<SavingsPotDTO> savingsPotList = backendDelegate.getAllSavingsPot(fundingAccountId,headersMap);
        return savingsPotList;
        
	  } catch (Exception e) {
          LOG.error("Error while calling the microservice :"+e);
          return null;
      } 
	}

	@Override
	public List<SavingsPotCategoriesDTO> getCategories(String type, Map<String, Object> headersMap){
		 try {
              SavingsPotBackendDelegate backendDelegate =
	                    DBPAPIAbstractFactoryImpl.getBackendDelegate(SavingsPotBackendDelegate.class);
	            List<SavingsPotCategoriesDTO> categories = backendDelegate.getCategories(type,headersMap);
	            return categories;
	        } catch (Exception e) {
	        	LOG.error("Error while calling the microservice :"+e);
	            return null;
	        }
	}

	@Override
	public SavingsPotDTO createSavingsPot(String fundingAccountHoldingId, String partyId, String productId,
			String potAccountId, String potName, String potType, String savingsType, String currency,
			String targetAmount, String targetPeriod, String frequency, String startDate, String endDate,
			String periodicContribution,Map<String, Object> headersMap){
	  try {
		SavingsPotBackendDelegate backendDelegate =
                DBPAPIAbstractFactoryImpl.getBackendDelegate(SavingsPotBackendDelegate.class);
        SavingsPotDTO savingsPot = backendDelegate.createSavingsPot(fundingAccountHoldingId,partyId,productId,
    		 potAccountId, potName, potType,savingsType,currency,
    		 targetAmount, targetPeriod,frequency,startDate,endDate, periodicContribution,headersMap);
        return savingsPot;
        
	  } catch (Exception e) {
          LOG.error("Error while calling the microservice :"+e);
          return null;
      } 
	}
	
	@Override
	public SavingsPotDTO closeSavingsPot(String savingsPotId,Map<String, Object> headersMap){
	  try {
		SavingsPotBackendDelegate backendDelegate =
                DBPAPIAbstractFactoryImpl.getBackendDelegate(SavingsPotBackendDelegate.class);
        SavingsPotDTO savingsPot = backendDelegate.closeSavingsPot(savingsPotId,headersMap);
        return savingsPot;
        
	  } catch (Exception e) {
          LOG.error("Error while calling the microservice :"+e);
          return null;
      } 
	}
	
	
	@Override
	public SavingsPotDTO updateSavingsPotBalance(String savingsPotId,String amount, String isCreditDebit,Map<String, Object> headersMap){
	  try {
		SavingsPotBackendDelegate backendDelegate =
                DBPAPIAbstractFactoryImpl.getBackendDelegate(SavingsPotBackendDelegate.class);
        SavingsPotDTO savingsPot = backendDelegate.updateSavingsPotBalance(savingsPotId,amount,isCreditDebit,headersMap);
        return savingsPot;
        
	  } catch (Exception e) {
          LOG.error("Error while calling the microservice :"+e);
          return null;
      } 
	}
	
	@Override
	public SavingsPotDTO updateSavingsPot(Map<String, Object> updateSavingsPotMap,Map<String, Object> headersMap) {
	  try {
		SavingsPotBackendDelegate backendDelegate =
                DBPAPIAbstractFactoryImpl.getBackendDelegate(SavingsPotBackendDelegate.class);
        SavingsPotDTO savingsPot = backendDelegate.updateSavingsPot(updateSavingsPotMap,headersMap);
        return savingsPot;
        
	  } catch (Exception e) {
          LOG.error("Error while calling the microservice :"+e);
          return null;
      } 
	}
}

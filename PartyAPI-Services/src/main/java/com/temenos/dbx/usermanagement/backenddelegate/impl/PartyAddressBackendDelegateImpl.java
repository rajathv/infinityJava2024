package com.temenos.dbx.usermanagement.backenddelegate.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.infinity.dbx.dbp.jwt.auth.AuthConstants;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPDatasetConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.URLFinder;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.temenos.dbx.party.utils.PartyMappings;
import com.temenos.dbx.party.utils.PartyPropertyConstants;
import com.temenos.dbx.party.utils.PartyURLFinder;
import com.temenos.dbx.party.utils.PartyUtils;
import com.temenos.dbx.product.dto.AddressDTO;
import com.temenos.dbx.product.dto.BackendIdentifierDTO;
import com.temenos.dbx.product.dto.Country;
import com.temenos.dbx.product.dto.CustomerAddressViewDTO;
import com.temenos.dbx.product.dto.CustomerDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.dto.PartyAddress;
import com.temenos.dbx.product.dto.Region;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.api.AddressBackendDelegate;
import com.temenos.dbx.product.usermanagement.backenddelegate.api.BackendIdentifiersBackendDelegate;
import com.temenos.dbx.eum.product.usermanagement.backenddelegate.impl.AddressBackendDelegateImpl;
import com.temenos.dbx.product.utils.DTOConstants;
import com.temenos.dbx.product.utils.HTTPOperations;

public class PartyAddressBackendDelegateImpl implements AddressBackendDelegate {
	private static final Logger LOG = Logger.getLogger(PartyAddressBackendDelegateImpl.class);

    @Override
    public List<CustomerAddressViewDTO> getCustomerAddress(String customerId, String legalEntityId, Map<String, Object> headerMap)
            throws ApplicationException {

        LoggerUtil logger = new LoggerUtil(PartyCommunicationBackendDelegateImpl.class);

        DBXResult dbxResult = new DBXResult();

        List<CustomerAddressViewDTO> dtoList = new ArrayList<>();

        String partyID = null;
        if (StringUtils.isBlank(customerId)) {
            return dtoList;
        }

        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(customerId);
        customerDTO = (CustomerDTO) customerDTO.loadDTO();

        if (customerDTO != null) {

            if (HelperMethods.getBusinessUserTypes().contains(customerDTO.getCustomerType_id())
                    || customerDTO.isCombinedUser()) {
                return new AddressBackendDelegateImpl().getCustomerAddress(customerId, legalEntityId, headerMap);
            }

            BackendIdentifierDTO backendIdentifierDTO = new BackendIdentifierDTO();
            backendIdentifierDTO.setCustomer_id(customerId);
            backendIdentifierDTO.setBackendType(DTOConstants.PARTY);
            backendIdentifierDTO.setCompanyLegalUnit(legalEntityId);

            try {
                dbxResult = DBPAPIAbstractFactoryImpl.getBackendDelegate(BackendIdentifiersBackendDelegate.class)
                        .get(backendIdentifierDTO, headerMap);
            } catch (ApplicationException e1) {
                // TODO Auto-generated catch block
                logger.error("Error while fetching backend identifier for customer ID " + customerId);
            }

        } else {
            partyID = customerId;
        }
        try {
            if (dbxResult.getResponse() != null) {
                BackendIdentifierDTO identifierDTO = (BackendIdentifierDTO) dbxResult.getResponse();
                partyID = identifierDTO.getBackendId();
            }
            String partyURL = URLFinder.getServerRuntimeProperty(URLConstants.PARTY_HOST_URL) +
                    PartyURLFinder.getServiceUrl(PartyPropertyConstants.PARTY_ADDRESS, partyID);

            PartyUtils.addJWTAuthHeader(headerMap, AuthConstants.PRE_LOGIN_FLOW);
            DBXResult response = HTTPOperations.sendHttpRequest(HTTPOperations.operations.GET,
                    partyURL, null, headerMap);

            JsonObject jsonObject = new JsonParser().parse((String) response.getResponse()).getAsJsonObject();

            if (jsonObject.has(DTOConstants.PARTY_ADDRESS)
                    && !jsonObject.get(DTOConstants.PARTY_ADDRESS).isJsonNull()) {
                List<PartyAddress> contactAddresses =
                        PartyAddress.loadFromJsonArray(jsonObject.get(DTOConstants.PARTY_ADDRESS).getAsJsonArray());

                for (PartyAddress contactAddress : contactAddresses) {
                    if (contactAddress.getCommunicationNature() != null
                            && contactAddress.getCommunicationNature().equals(DTOConstants.PHYSICAL)) {

                        CustomerAddressViewDTO addressViewDTO = new CustomerAddressViewDTO();
                        addressViewDTO.setAddressId(contactAddress.getAddressesReference());
                        if (PartyMappings.getcustomerAddressTypeMapping()
                                .containsKey(contactAddress.getAddressType())) {
                            addressViewDTO.setAddressType(PartyMappings.getcustomerAddressTypeMapping()
                                    .get(contactAddress.getAddressType()));
                        } else {
                            addressViewDTO.setAddressType(contactAddress.getAddressType());
                        }
                        addressViewDTO.setAddressLine1(contactAddress.getBuildingName());
                        addressViewDTO.setAddressLine2(contactAddress.getStreetName());
                        addressViewDTO.setZipCode(contactAddress.getPostalOrZipCode());
                        addressViewDTO.setRegionId(contactAddress.getRegionCode());
                        addressViewDTO.setRegionCode(contactAddress.getRegionCode());
                        addressViewDTO.setCountryId(contactAddress.getCountryCode());
                        addressViewDTO.setCityId(contactAddress.getTown());

                        if (StringUtils.isNotBlank(addressViewDTO.getRegionId())) {
                            Region region = (Region) new Region().loadDTO(addressViewDTO.getRegionId());
                            if (region != null) {
                                addressViewDTO.setRegionName(region.getName());
                                if (StringUtils.isBlank(addressViewDTO.getCountryId())) {
                                    addressViewDTO.setCountryId(region.getCountry_id());
                                }
                            } else {
                                addressViewDTO.setRegionName(contactAddress.getCountrySubdivision());
                            }
                        }

                        if (StringUtils.isNotBlank(addressViewDTO.getCountryId())) {
                            Country country = (Country) new Country().loadDTO(addressViewDTO.getCountryId());
                            if (country != null) {
                                addressViewDTO.setCountryCode(country.getCode());
                                addressViewDTO.setCountryName(country.getName());
                            } else {
                                addressViewDTO.setCountryCode(addressViewDTO.getCountryId());
                                addressViewDTO.setCountryName(addressViewDTO.getCountryId());
                            }
                        }
                        
						else {
							addressViewDTO.setCountryId(
									StringUtils.isNotBlank(contactAddress.getCountrySubdivision())
									? contactAddress.getCountrySubdivision().split("-")[0] : null);
						}
						if (StringUtils.isBlank(addressViewDTO.getRegionId())) {
							addressViewDTO.setRegionId(contactAddress.getCountrySubdivision());

						}

//                        addressViewDTO.setCityName(getCityName(contactAddress.getTown(),headerMap));
                        addressViewDTO.setCityName(contactAddress.getTown());

                        if (Boolean.parseBoolean(contactAddress.getPrimary())) {
                            addressViewDTO.setIsPrimary("true");
                        } else {
                            addressViewDTO.setIsPrimary("false");
                        }                      
                        dtoList.add(addressViewDTO);
                    }
                }

            }
        } catch (Exception e) {
            logger.error("Caught exception while getting Party Address Details: ", e);
            return dtoList;
        }

        return dtoList;
    }

    @Override
    public DBXResult getCustomerAddressForUserResponse(CustomerAddressViewDTO addressDTO,
            Map<String, Object> headerMap) {
        DBXResult dbxResult = new DBXResult();

        try {
            List<CustomerAddressViewDTO> addressViewDTOs = getCustomerAddress(addressDTO.getCustomerId(), addressDTO.getCompanyLegalUnit(), headerMap);
            dbxResult.setResponse(addressViewDTOs);
        } catch (ApplicationException e) {
            
        	LOG.error(e);
        }

        return dbxResult;
    }

    @Override
    public AddressDTO getAddressDetails(String addressId, Map<String, Object> headerMap) throws ApplicationException {
        AddressBackendDelegate addressBackendDelegate = new AddressBackendDelegateImpl();
        return addressBackendDelegate.getAddressDetails(addressId, headerMap);
    }
    
    public String getCityName(String cityId, Map<String, Object> headerMap) {

        String filter = "id" + DBPUtilitiesConstants.EQUAL + cityId;
        Map<String, Object> inputParams = new HashMap<String, Object>();
        inputParams.put(DBPUtilitiesConstants.FILTER, filter);
        JsonObject jsonObject =
                ServiceCallHelper.invokeServiceAndGetJson(inputParams, headerMap, URLConstants.CITY_GET);

        if (jsonObject.has(DBPDatasetConstants.DATASET_CITY)) {
            JsonElement array = jsonObject.get(DBPDatasetConstants.DATASET_CITY);
            JsonArray cities = !array.isJsonNull() && array.isJsonArray() ? array.getAsJsonArray() : new JsonArray();

            if (cities.size() > 0) {
                JsonElement city = cities.get(0).getAsJsonObject().get("Name");
                return !city.isJsonNull() ? city.getAsString() : null;
            }
        }

        return null;
    }
}

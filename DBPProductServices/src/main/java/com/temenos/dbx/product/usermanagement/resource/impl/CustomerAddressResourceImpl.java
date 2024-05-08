package com.temenos.dbx.product.usermanagement.resource.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.ConvertJsonToResult;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.dto.CustomerAddressDTO;
import com.temenos.dbx.product.dto.CustomerAddressViewDTO;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.AddressBusinessDelegate;
import com.temenos.dbx.product.usermanagement.resource.api.CustomerAddressResource;
import com.temenos.dbx.product.utils.DTOUtils;

public class CustomerAddressResourceImpl implements CustomerAddressResource {

	@Override
	public Result getCustomerAddress(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws ApplicationException {
		Result result = new Result();
		try {
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			String customerId = inputParams.get("Customer_id");
			if (StringUtils.isNotBlank(customerId)) {

				AddressBusinessDelegate addressBP =
						DBPAPIAbstractFactoryImpl.getInstance()
						.getFactoryInstance(BusinessDelegateFactory.class)
						.getBusinessDelegate(AddressBusinessDelegate.class);

				List<CustomerAddressViewDTO> dtoList =
						addressBP.getCustomerAddress(customerId, dcRequest.getHeaderMap());
				if (null != dtoList) {
					String addressString =
							JSONUtils.stringifyCollectionWithTypeInfo(dtoList, CustomerAddressViewDTO.class);
					JSONArray addressArray = new JSONArray(addressString);
					JSONObject addressJson = new JSONObject();
					addressJson.put("Addresses", addressArray);
					result = ConvertJsonToResult.convert(addressJson.toString());
				}
			}
		} catch (ApplicationException e) {
			throw new ApplicationException(e.getErrorCodeEnum());
		} catch (Exception e) {
			throw new ApplicationException(ErrorCodeEnum.ERR_10224);
		}
		return result;
	}

	@Override
	public Object getCustomerAddressForUserResponse(String methodID, Object[] inputArray,
			DataControllerRequest dcRequest, DataControllerResponse dcResponse) {

		Result returnResult = new Result();
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		String customer_id = inputParams.get("Customer_id");
		
		CustomerAddressViewDTO addressDTO = new CustomerAddressViewDTO();
		addressDTO.setCustomerId(customer_id);
		AddressBusinessDelegate addressBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AddressBusinessDelegate.class);
		
		DBXResult  dbxResult = addressBusinessDelegate.getCustomerAddressForUserResponse(addressDTO, dcRequest.getHeaderMap());
		if(dbxResult.getResponse() != null) {
			List<CustomerAddressViewDTO> addressDTOs = (List<CustomerAddressViewDTO>) dbxResult.getResponse();
			Dataset dataset = new Dataset("Addresses");
			for(CustomerAddressViewDTO addressDTO2 : addressDTOs) {
				Record record = DTOUtils.getRecordFromDTO(addressDTO2, true);
				dataset.addRecord(record);
			}
			
			returnResult.addDataset(dataset);
		}

		return returnResult;
	}

}

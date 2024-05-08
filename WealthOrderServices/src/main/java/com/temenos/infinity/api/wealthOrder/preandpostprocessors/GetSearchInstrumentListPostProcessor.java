package com.temenos.infinity.api.wealthOrder.preandpostprocessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealthOrder.config.WealthAPIServices;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

/**
 * (INFO) If status is set as a part of the request , the operation is exited
 * else operation is executed.
 * 
 * @author balaji.krishnan
 *
 */

public class GetSearchInstrumentListPostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager.getLogger(GetSearchInstrumentListPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		try {
			String  RICCode = "";
			JSONArray instrumentArray = new JSONArray();
			JSONObject responseJSON = new JSONObject();
			JSONObject instSearchJSON = new JSONObject();
			JSONArray altInsArray = new JSONArray();
			JSONObject altInsJSON = new JSONObject();
			
			Record headerRec = result.getRecordById("header");
			String statusVal = headerRec.getParamValueByName("status");
			if (statusVal != null && statusVal.trim().equalsIgnoreCase("success")) {
				Dataset ds = result.getDatasetById("body");
				if (ds != null) {
					JSONObject resultObj = new JSONObject();
					resultObj.put("Field",
							Utilities.convertStringToJSONArray(ResultToJSON.convertDataset(ds).toString()));
					JSONArray refnitivArray = resultObj.getJSONArray("Field");

					if (refnitivArray != null && refnitivArray.length() > 0) {

						String[] colArray = new String[] { "holdingsType", "instrumentId", "ISIN", "description" };

						for (int i = 0; i < refnitivArray.length(); i++) {
							instSearchJSON = refnitivArray.getJSONObject(i);

							for (String key : colArray) {
								if (!instSearchJSON.has(key)) {
									instSearchJSON.put(key, "");
								}
							}
							
							if (instSearchJSON.has("alternateInstruments")) {
								altInsArray = instSearchJSON.getJSONArray("alternateInstruments");
								if (altInsArray != null && altInsArray.length() > 0) {
									for (int k = 0; k < altInsArray.length(); k++) {
										altInsJSON = altInsArray.getJSONObject(k);

										RICCode = (altInsJSON.has("alternateInstrument")
												&& altInsJSON.get("alternateInstrument").toString() != null)
														? (altInsJSON.get("alternateInstrument").toString().trim()
																.equalsIgnoreCase("RICCODE")
																		? (altInsJSON.has("RICCode")
																				? altInsJSON.get("RICCode").toString()
																				: "")
																		: "")
														: "";
										if (RICCode.length() > 0) {
											break;
										}
									}
								}
								instSearchJSON.remove("alternateInstruments");
							}
							
							instSearchJSON.put(TemenosConstants.RICCODE, RICCode);
							instSearchJSON.put("application", "SC");
							instrumentArray.put(instSearchJSON);
						}

					}

				}
				responseJSON.put("scinstrumentList", instrumentArray);
				responseJSON.put("opstatus", "0");
				responseJSON.put("httpStatusCode", "200");
				responseJSON.put("status", statusVal);
			} else {
				Record errorRec = result.getRecordById("error");
				if (errorRec != null) {
					Record error = errorRec.getAllRecords().get(0);

					responseJSON.put("errormessage", error.getParamValueByName("message"));
				} else {
					responseJSON.put("errormessage", "");
				}
				responseJSON.put("status", statusVal);
			}

			return Utilities.constructResultFromJSONObject(responseJSON);
		} catch (Exception e) {

			LOG.error("Error while invoking GetInstrumentTotalPostProcessor - "
					+ WealthAPIServices.WEALTHSERVICESORCHESTRATION.getOperationName() + "  : " + e);
		}

		return result;
	}

	@SuppressWarnings("unused")
	private String getUserFavorites(DataControllerRequest request) throws ApplicationException {
		Map<String, String> input = new HashMap<>();
		String userId = HelperMethods.getUserIdFromSession(request);
		Result result = new Result();
		Result finalResult = new Result();
		JSONObject resultJson = new JSONObject();
		String filter = "userId" + DBPUtilitiesConstants.EQUAL + userId;
		input.put(DBPUtilitiesConstants.FILTER, filter);
		try {
			result = HelperMethods.callGetApi(request, input.get(DBPUtilitiesConstants.FILTER),
					HelperMethods.getHeaders(request), URLConstants.WEALTH_USER_FAVORITES_GET);
			if (HelperMethods.hasRecords(result)) {
				List<Dataset> dataset = result.getAllDatasets();
				List<Record> drecords = dataset.get(0).getAllRecords();
				resultJson = CommonUtils.convertRecordToJSONObject(drecords.get(0));
			}
			resultJson.put("opstatus", result.getOpstatusParamValue());
			resultJson.put("httpStatusCode", result.getHttpStatusCodeParamValue());
			finalResult = Utilities.constructResultFromJSONObject(resultJson);

		} catch (Exception e) {
			LOG.error("Exception occured while fetching the field order from backend delegate :" + e.getMessage());
			throw new ApplicationException(ErrorCodeEnum.ERR_21001);
		}
		String instrumentIds = finalResult.getParamValueByName(TemenosConstants.USERFAVORITESIDS);
		return instrumentIds;

	}

}

package com.temenos.infinity.api.wealthOrder.preandpostprocessors;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealthOrder.backenddelegate.impl.OrdersListBackendDelegateImpl;
import com.temenos.infinity.api.wealthOrder.config.WealthAPIServices;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

/**
 * (INFO) If status is set as a part of the request , the operation is exited
 * else operation is executed.
 * 
 * @author balaji.krishnan
 *
 */

public class GetOrderDetailsPostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager.getLogger(GetOrderDetailsPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		try {
			OrdersListBackendDelegateImpl ordersListBackendDelegateImpl = new OrdersListBackendDelegateImpl();

			String[] typeArr = null, orderStatusArr = null, portfolioIdArr = null, searchArr = null, sortByArr = null,
					sortOrderArr = null, pageSizeArr = null, pageOffsetArr = null, startDateArr = null,
					endDateArr = null;
			String type = "", responseStatus = "", portfolioId = "", startDate = "", endDate = "", search = "",
					status = "", sortBy = "", sortOrder = "", pageSize = "", pageOffset = "", orderMode = "",
					ordertransactType = "", validity = "";
			int totalCount = 0, pageSizeVal = 0, pageOffsetVal = 0;

			portfolioIdArr = request.getParameterValues(TemenosConstants.PORTFOLIOID);
			typeArr = request.getParameterValues(TemenosConstants.ORDERSVIEW_TYPE);
			startDateArr = request.getParameterValues(TemenosConstants.STARTDATE);
			endDateArr = request.getParameterValues(TemenosConstants.ENDDATE);
			searchArr = request.getParameterValues(TemenosConstants.SEARCHBYINSTRUMENTNAME);
			sortByArr = request.getParameterValues(TemenosConstants.SORTBY);
			sortOrderArr = request.getParameterValues(TemenosConstants.SORTORDER);
			pageSizeArr = request.getParameterValues(TemenosConstants.PAGESIZE);
			pageOffsetArr = request.getParameterValues(TemenosConstants.PAGEOFFSET);

			if (typeArr != null) {
				type = typeArr[0].toString().trim();
			}
			orderStatusArr = new String[] { "OPEN", "PENDING", "TRANSMITTED" };
			List<String> orderStatusList = Arrays.asList(orderStatusArr);

			if (startDateArr != null && startDateArr.length > 0) {
				startDate = startDateArr[0].trim();
			}
			if (endDateArr != null && endDateArr.length > 0) {
				endDate = endDateArr[0].trim();
			}
			if (searchArr != null && searchArr.length > 0) {
				search = searchArr[0].trim();
			}
			if (sortByArr != null && sortByArr.length > 0) {
				sortBy = sortByArr[0].trim();
			}
			if (sortOrderArr != null && sortOrderArr.length > 0) {
				sortOrder = sortOrderArr[0].trim();
			}
			if (pageSizeArr != null && pageSizeArr.length > 0) {
				pageSize = pageSizeArr[0].trim();
			}
			if (pageOffsetArr != null && pageOffsetArr.length > 0) {
				pageOffset = pageOffsetArr[0].trim();
			}
			if (portfolioIdArr != null && portfolioIdArr.length > 0) {
				portfolioId = portfolioIdArr[0].trim();
			}

			pageSizeVal = (pageSize != null && pageSize.trim().length() > 0) ? Integer.parseInt(pageSize) : 0;
			pageOffsetVal = (pageOffset != null && pageOffset.trim().length() > 0) ? Integer.parseInt(pageOffset) : 0;

			Record headerRec = result.getRecordById("header");
			JSONObject responseJSON = new JSONObject();
			String statusVal = headerRec.getParamValueByName("status");
			if (statusVal != null && statusVal.trim().equalsIgnoreCase("success")) {

				Dataset ds = result.getDatasetById("body");
				if (ds != null) {

					JSONObject resultObj = new JSONObject();
					resultObj.put("Field",
							Utilities.convertStringToJSONArray(ResultToJSON.convertDataset(ds).toString()));
					JSONArray refnitivArray = resultObj.getJSONArray("Field");

					if (refnitivArray != null && refnitivArray.length() > 0) {

						String[] colArray = new String[] { TemenosConstants.DESCRIPTION, "ISIN", "orderedAt",
								"instrumentId", "holdingsType", TemenosConstants.QUANTITY, TemenosConstants.LIMITPRICE,
								TemenosConstants.STOPPRICE, TemenosConstants.TRADEDATE, "orderMode", "orderModeType",
								TemenosConstants.ORDERTYPE, TemenosConstants.ORDER_REFERENCE, "orderStatus", "validity",
								"instrumentCurrency", TemenosConstants.TRANSACTIONNAME, TemenosConstants.ISINEXCHANGE,
								TemenosConstants.SWIPEACTIONENABLE };

						JSONArray finalArr = new JSONArray();

						for (int i = 0; i < refnitivArray.length(); i++) {
							JSONObject ordersObj = refnitivArray.getJSONObject(i);

							responseStatus = ordersObj.has("orderStatus") ? ordersObj.get("orderStatus").toString()
									: "";

							if ((type.equalsIgnoreCase("OPEN") && orderStatusList.contains(responseStatus))
									|| (type.equalsIgnoreCase("HISTORY")
											&& !orderStatusList.contains(responseStatus))) {
								for (String key : colArray) {

									if (!ordersObj.has(key)) {
										ordersObj.put(key, "");
									}

									if (key.trim().equalsIgnoreCase("orderMode")) {

										orderMode = ordersObj.get("orderMode").toString();
										if (orderMode.equalsIgnoreCase("SEL")) {
											orderMode = "Sell";
										}
										orderMode = orderMode.trim().length() > 0
												? (orderMode.substring(0, 1).toUpperCase()
														+ orderMode.substring(1, orderMode.length()).toLowerCase())
												: "";
										ordersObj.put(key, orderMode);
									}

									if (key.trim().equalsIgnoreCase("orderModeType")) {

										ordertransactType = (orderType(ordersObj.get("orderModeType").toString()));
										ordersObj.put(key, ordertransactType);
									}

									if (key.trim().equalsIgnoreCase(TemenosConstants.ORDERTYPE)) {
										orderMode = orderMode.trim().length() > 0
												? (orderMode.substring(0, 1).toUpperCase()
														+ orderMode.substring(1, orderMode.length()).toLowerCase())
												: "";
										ordersObj.put(key, (orderMode + " " + ordertransactType));
									}

									if (key.trim().equalsIgnoreCase("orderStatus")) {
										status = orderStatus(ordersObj.get("orderStatus").toString());
										ordersObj.put(TemenosConstants.ORDERS_STATUS, status);

										if (status.trim().equalsIgnoreCase(TemenosConstants.STATUS_COMPLETED)) {
											if (ordertransactType.trim().equalsIgnoreCase(TemenosConstants.LIMIT_TYPE)
													|| ordertransactType.trim()
															.equalsIgnoreCase(TemenosConstants.STOPLIMIT_TYPE)) {

												ordersObj.put(TemenosConstants.ORDER_EXECUTION_PRICE,
														(ordersObj.get(TemenosConstants.LIMITPRICE).toString()
																.length() > 0
																		? ordersObj.get(TemenosConstants.LIMITPRICE)
																				.toString()
																		: ""));
											} else if (ordertransactType.trim()
													.equalsIgnoreCase(TemenosConstants.STOPLOSS_TYPE)) {
												ordersObj.put(TemenosConstants.ORDER_EXECUTION_PRICE,
														(ordersObj.get(TemenosConstants.STOPPRICE).toString()
																.length() > 0
																		? ordersObj.get(TemenosConstants.STOPPRICE)
																				.toString()
																		: ""));
											} else {
												ordersObj.put(TemenosConstants.ORDER_EXECUTION_PRICE, "");
											}
										} else {
											ordersObj.put(TemenosConstants.ORDER_EXECUTION_PRICE, "");
										}

									}

									if (key.trim().equalsIgnoreCase(TemenosConstants.QUANTITY)
											|| key.trim().equalsIgnoreCase(TemenosConstants.STOPPRICE)
											|| key.trim().equalsIgnoreCase(TemenosConstants.LIMITPRICE)) {
										ordersObj.put(key,
												(ordersObj.get(key).toString().length() > 0
														? ordersObj.get(key).toString()
														: ""));
									}

									if (key.trim().equalsIgnoreCase("validity")) {

										validity = ordersObj.get(key).toString().trim().length() > 0
												? (ordersObj.get(key).toString().equalsIgnoreCase("GTD") ? "Day Order"
														: "Good Till Canceled")
												: "Good Till Canceled";
										ordersObj.put(key, validity);
									}
									if (key.trim().equalsIgnoreCase(TemenosConstants.ISINEXCHANGE)) {
										ordersObj.put(key,
												((!ordersObj.get("ISIN").toString().equalsIgnoreCase(""))
														? ((!ordersObj.get(TemenosConstants.HOLDINGS_TYPE).toString()
																.equalsIgnoreCase(""))
																		? (ordersObj.get("ISIN").toString() + " | "
																				+ ordersObj.get(
																						TemenosConstants.HOLDINGS_TYPE)
																						.toString())
																		: ordersObj.get("ISIN").toString())
														: ((!ordersObj.get(TemenosConstants.HOLDINGS_TYPE).toString()
																.equalsIgnoreCase(""))
																		? ordersObj.get(TemenosConstants.HOLDINGS_TYPE)
																		: "")));
									}
									if (key.trim().equalsIgnoreCase(TemenosConstants.SWIPEACTIONENABLE)) {
										if (orderMode.equalsIgnoreCase("BUY") || orderMode.equalsIgnoreCase("SELL")) {
											if (ordertransactType.equalsIgnoreCase("Market")
													|| ordertransactType.equalsIgnoreCase("Limit")
													|| ordertransactType.equalsIgnoreCase("Stop Loss")
													|| ordertransactType.equalsIgnoreCase("Stop Limit")) {
												if (validity.equalsIgnoreCase("Day Order")
														|| validity.equalsIgnoreCase("Good Till Canceled")) {
													ordersObj.put(key, "true");
												} else {
													ordersObj.put(key, "false");
												}
											} else {
												ordersObj.put(key, "false");
											}
										} else {
											ordersObj.put(key, "false");
										}
									}

								}

								ordersObj.remove("orderStatus");

								finalArr.put(ordersObj);
							}

						}

//						for (int i = 0; i < refnitivArray.length(); i++) {
//							JSONObject refnitivObj = refnitivArray.getJSONObject(i);
//							JSONObject responseObj = new JSONObject();
//
//							responseStatus = refnitivObj.has("orderStatus") ? refnitivObj.get("orderStatus").toString()
//									: "";
//
//							if ((type.equalsIgnoreCase("OPEN") && orderStatusList.contains(responseStatus))
//									|| (type.equalsIgnoreCase("HISTORY")
//											&& !orderStatusList.contains(responseStatus))) {
//
//								responseObj.put(TemenosConstants.DESCRIPTION,
//										refnitivObj.has(TemenosConstants.DESCRIPTION)
//												? refnitivObj.get(TemenosConstants.DESCRIPTION)
//												: "");
//
//								responseObj.put("ISIN", refnitivObj.has("ISIN") ? refnitivObj.get("ISIN") : "");
//
//								responseObj.put("orderedAt",
//										refnitivObj.has("orderedAt") ? refnitivObj.get("orderedAt") : "");
//
//								responseObj.put("instrumentId",
//										refnitivObj.has("instrumentId") ? refnitivObj.get("instrumentId") : "");
//
//								responseObj.put("holdingsType",
//										refnitivObj.has("holdingsType") ? refnitivObj.get("holdingsType") : "");
//
//								responseObj.put(TemenosConstants.QUANTITY,
//										refnitivObj.has("quantity") ? refnitivObj.get("quantity") : "0");
//
//								responseObj.put(TemenosConstants.LIMITPRICE,
//										refnitivObj.has("limitPrice") ? refnitivObj.get("limitPrice") : "0");
//
//								responseObj.put(TemenosConstants.STOPPRICE,
//										refnitivObj.has("stopPrice") ? refnitivObj.get("stopPrice") : "0");
//
//								responseObj.put(TemenosConstants.TRADEDATE,
//										refnitivObj.has(TemenosConstants.TRADEDATE)
//												? refnitivObj.get(TemenosConstants.TRADEDATE)
//												: "");
//
//								responseObj.put(TemenosConstants.ORDERS_STATUS,
//										refnitivObj.has("orderStatus")
//												? (orderStatus(refnitivObj.get("orderStatus").toString()))
//												: "");
//
//								orderMode = refnitivObj.has("orderMode") ? refnitivObj.get("orderMode").toString() : "";
//								responseObj.put("orderMode", orderMode);
//
//								ordertransactType = refnitivObj.has("orderModeType")
//										? (orderType(refnitivObj.get("orderModeType").toString()))
//										: "";
//
//								responseObj.put("orderModeType", ordertransactType);
//
//								orderMode = orderMode.trim().length() > 0
//										? (orderMode.substring(0, 1).toUpperCase()
//												+ orderMode.substring(1, orderMode.length()).toLowerCase())
//										: "";
//
//								responseObj.put(TemenosConstants.ORDERTYPE, (orderMode + " " + ordertransactType));
//
//								responseObj.put(TemenosConstants.ORDER_REFERENCE,
//										refnitivObj.has(TemenosConstants.ORDER_REFERENCE)
//												? refnitivObj.get(TemenosConstants.ORDER_REFERENCE)
//												: "");
//
//								responseObj.put("instrumentCurrency", "USD");
//								/*
//								 * responseObj.put("validity", "Day Order"); responseObj.put("RICCode", i);
//								 * responseObj.put("valueDate", refnitivObj.has("orderDate") ?
//								 * refnitivObj.get("orderDate") : ""); responseObj.put("total", "0");
//								 * responseObj.put(TemenosConstants.INSTRUMENTAMOUNT, "0");
//								 * responseObj.put(TemenosConstants.FEES, "0");
//								 * responseObj.put(TemenosConstants.EXCHANGERATE, "0");
//								 * responseObj.put(TemenosConstants.NETAMOUNT, "0");
//								 * responseObj.put(TemenosConstants.TRANSACTIONID,i);
//								 */
//								finalArr.put(responseObj);
//							}
//						}

						JSONArray sortedJSON = new JSONArray();
						if (startDate == null || endDate == null || startDate.equals("") || endDate.equals("")) {
							sortedJSON = finalArr;
						} else {
							sortedJSON = ordersListBackendDelegateImpl.filterDate(finalArr, startDate, endDate);
						}

						if (sortBy != null) {
							sortedJSON = ordersListBackendDelegateImpl.sortArray(sortedJSON, sortBy, sortOrder);
						}

						if (search != null) {
							sortedJSON = ordersListBackendDelegateImpl.returnSearch(sortedJSON, search);
						}

						totalCount = sortedJSON.length();

						if (pageSizeVal > 0 && pageOffsetVal >= 0) {
							sortedJSON = ordersListBackendDelegateImpl.pagination(sortedJSON, pageSizeVal,
									pageOffsetVal);
						}
						responseJSON.put("portfolioID", portfolioId);
						responseJSON.put("ordersDetails", sortedJSON);
						responseJSON.put(TemenosConstants.STARTDATE, startDate);
						responseJSON.put(TemenosConstants.ENDDATE, endDate);
						responseJSON.put(TemenosConstants.SORTBY, sortBy);
						responseJSON.put(TemenosConstants.SORTORDER, sortOrder);
						responseJSON.put("totalCount", totalCount);
						responseJSON.put("opstatus", "0");
						responseJSON.put("httpStatusCode", "200");
						responseJSON.put("status", statusVal);

					}
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
			}
		} catch (

		Exception e) {

			LOG.error("Error while invoking GetOrderDetailsPostProcessor - "
					+ WealthAPIServices.WEALTHSERVICESORCHESTRATION.getOperationName() + "  : " + e);
		}

		return result;
	}

	private String orderType(String orderType) {
		if (orderType != null && orderType.trim().equalsIgnoreCase("LIMIT")) {
			return "Limit";
		} else if (orderType != null && orderType.trim().equalsIgnoreCase("MARKET")) {
			return "Market";
		} else if (orderType != null && orderType.trim().equalsIgnoreCase("STOP")) {
			return "Stop Loss";
		} else if (orderType != null && orderType.trim().equalsIgnoreCase("STOP.LIMIT")) {
			return "Stop Limit";
		}
		return orderType;
	}

	private String orderStatus(String orderStatus) {
		if (orderStatus != null && (orderStatus.trim().equalsIgnoreCase("OPEN")
				|| orderStatus.trim().equalsIgnoreCase("PENDING") || orderStatus.trim().equalsIgnoreCase("TRANSMITTED")
				|| orderStatus.trim().equalsIgnoreCase("ACCEPTED"))) {
			return "Open";
		} else if (orderStatus != null && (orderStatus.trim().equalsIgnoreCase("EXECUTED")
				|| orderStatus.trim().equalsIgnoreCase("TRADED") || orderStatus.trim().equalsIgnoreCase("COMPLETED"))) {
			return "Completed";
		} else if (orderStatus != null && (orderStatus.trim().equalsIgnoreCase("CANCELLED"))) {
			return "Cancelled";
		} else if (orderStatus != null && (orderStatus.trim().equalsIgnoreCase("REJECTED"))) {
			return "Rejected";
		} else if (orderStatus != null && (orderStatus.trim().equalsIgnoreCase("EXPIRED"))) {
			return "Expired";
		} else {
			return "";
		}
	}
}

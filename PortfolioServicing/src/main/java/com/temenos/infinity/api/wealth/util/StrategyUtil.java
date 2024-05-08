package com.temenos.infinity.api.wealth.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.temenos.infinity.api.wealth.constants.StrategyConstants;
import com.temenos.infinity.api.wealth.vo.AssetVO;
import com.temenos.infinity.api.wealth.vo.ComputeVO;
import com.temenos.infinity.api.wealth.vo.RegionVO;
import com.temenos.infinity.api.wealth.vo.SectorVO;
import com.temenos.infinity.api.wealth.vo.SegmentVO;
import com.temenos.infinity.api.wealthservices.exception.InfWlthException;


/**
 * StrategyUtil does the following: 
 * 1) Reads the asset allocation data defined for the recommended strategy
 * 2) Parse & constructs the required VO objects
 * 3) Compute the logic as per the user action at segment, region & sector level and returns the strategy allocation data
 * 
 * @author Rajesh Kappera
 */
public class StrategyUtil { 
	private static final Logger LOG = Logger.getLogger(StrategyUtil.class);
	private static final String INDIVIDUAL_STRATEGY_MOCK_DATA_PORT1 ="{\"segment\": [{\"id\": \"1\",\"name\": \"Stocks\",\"recommendedValue\": \"40\",\"region\": [{\"id\": \"5\",\"name\": \"US\",\"recommendedValue\": \"20\",\"sector\": [{\"id\": \"12\",\"name\": \"Aerospace & Defense\",\"recommendedValue\": \"2\",\"targetValue\": \"2\"},{\"id\": \"13\",\"name\": \"Auto & Truck Manufacturers\",\"recommendedValue\": \"2\",\"targetValue\": \"2\"},{\"id\": \"14\",\"name\": \"Beverages (Nonalcoholic)\",\"recommendedValue\": \"2.5\",\"targetValue\": \"2.5\"},{\"id\": \"15\",\"name\": \"Biotechnology & Drugs\",\"recommendedValue\": \"2.5\",\"targetValue\": \"2.5\"},{\"id\": \"16\",\"name\": \"Communications Equipment\",\"recommendedValue\": \"2\",\"targetValue\": \"2\"},{\"id\": \"17\",\"name\": \"Computer Services\",\"recommendedValue\": \"2\",\"targetValue\": \"2\"},{\"id\": \"18\",\"name\": \"Consumer Financial Services\",\"recommendedValue\": \"3\",\"targetValue\": \"3\"},{\"id\": \"19\",\"name\": \"Regional Banks\",\"recommendedValue\": \"2\",\"targetValue\": \"2\"},{\"id\": \"20\",\"name\": \"Retail\",\"recommendedValue\": \"2\",\"targetValue\": \"2\"}],\"targetValue\": \"20\"},{\"id\": \"6\",\"name\": \"EU\",\"recommendedValue\": \"10\",\"sector\": [{\"id\": \"21\",\"name\": \"Retail\",\"recommendedValue\": \"10\",\"targetValue\": \"10\"}],\"targetValue\": \"10\"},{\"id\": \"7\",\"name\": \"Asia\",\"recommendedValue\": \"10\",\"sector\": [{\"id\": \"22\",\"name\": \"Regional Banks\",\"recommendedValue\": \"10\",\"targetValue\": \"10\"}],\"targetValue\": \"10\"}],\"targetValue\": \"40\"},{\"id\": \"2\",\"name\": \"Bonds\",\"recommendedValue\": \"30\",\"region\": [{\"id\": \"8\",\"name\": \"US\",\"recommendedValue\": \"15\",\"sector\": [{\"id\": \"23\",\"name\": \"Consumer Financial Services\",\"recommendedValue\": \"5\",\"targetValue\": \"5\"},{\"id\": \"24\",\"name\": \"Regional Banks\",\"recommendedValue\": \"5\",\"targetValue\": \"5\"},{\"id\": \"25\",\"name\": \"Retail\",\"recommendedValue\": \"5\",\"targetValue\": \"5\"}],\"targetValue\": \"15\"},{\"id\": \"9\",\"name\": \"EU\",\"recommendedValue\": \"12\",\"sector\": [{\"id\": \"26\",\"name\": \"Aerospace & Defense\",\"recommendedValue\": \"4\",\"targetValue\": \"4\"},{\"id\": \"27\",\"name\": \"Auto & Truck Manufacturers\",\"recommendedValue\": \"4\",\"targetValue\": \"4\"},{\"id\": \"28\",\"name\": \"Beverages (Nonalcoholic)\",\"recommendedValue\": \"4\",\"targetValue\": \"4\"}],\"targetValue\": \"12\"},{\"id\": \"10\",\"name\": \"Asia\",\"recommendedValue\": \"3\",\"sector\": [{\"id\": \"29\",\"name\": \"Regional Banks\",\"recommendedValue\": \"3\",\"targetValue\": \"3\"}],\"targetValue\": \"3\"}],\"targetValue\": \"30\"},{\"id\": \"3\",\"name\": \"Funds\",\"recommendedValue\": \"20\",\"region\": [{\"id\": \"11\",\"name\": \"US\",\"recommendedValue\": \"20\",\"sector\": [{\"id\": \"30\",\"name\": \"Exchange Traded Funds\",\"recommendedValue\": \"20\",\"targetValue\": \"20\"}],\"targetValue\": \"20\"}],\"targetValue\": \"20\"},{\"id\": \"4\",\"name\": \"Cash\",\"recommendedValue\": \"10\",\"targetValue\": \"10\"}]}" ;
	private static final String INDIVIDUAL_STRATEGY_MOCK_DATA_PORT2 ="{\"segment\": [{\"id\": \"1\",\"name\": \"Stocks\",\"recommendedValue\": \"25\",\"region\": [{\"id\": \"5\",\"name\": \"US\",\"recommendedValue\": \"15\",\"sector\": [{\"id\": \"12\",\"name\": \"Aerospace & Defense\",\"recommendedValue\": \"1.5\",\"targetValue\": \"1.5\"},{\"id\": \"13\",\"name\": \"Auto & Truck Manufacturers\",\"recommendedValue\": \"2.5\",\"targetValue\": \"2.5\"},{\"id\": \"14\",\"name\": \"Beverages (Nonalcoholic)\",\"recommendedValue\": \"1.5\",\"targetValue\": \"1.5\"},{\"id\": \"15\",\"name\": \"Biotechnology & Drugs\",\"recommendedValue\": \"2.75\",\"targetValue\": \"2.75\"},{\"id\": \"16\",\"name\": \"Communications Equipment\",\"recommendedValue\": \"1.25\",\"targetValue\": \"1.25\"},{\"id\": \"17\",\"name\": \"Computer Services\",\"recommendedValue\": \"1.50\",\"targetValue\": \"1.50\"},{\"id\": \"18\",\"name\": \"Consumer Financial Services\",\"recommendedValue\": \"2\",\"targetValue\": \"2\"},{\"id\": \"19\",\"name\": \"Regional Banks\",\"recommendedValue\": \"1\",\"targetValue\": \"1\"},{\"id\": \"20\",\"name\": \"Retail\",\"recommendedValue\": \"1\",\"targetValue\": \"1\"}],\"targetValue\": \"15\"},{\"id\": \"6\",\"name\": \"EU\",\"recommendedValue\": \"5\",\"sector\": [{\"id\": \"21\",\"name\": \"Retail\",\"recommendedValue\": \"5\",\"targetValue\": \"5\"}],\"targetValue\": \"5\"},{\"id\": \"7\",\"name\": \"Asia\",\"recommendedValue\": \"5\",\"sector\": [{\"id\": \"22\",\"name\": \"Regional Banks\",\"recommendedValue\": \"5\",\"targetValue\": \"5\"}],\"targetValue\": \"5\"}],\"targetValue\": \"25\"},{\"id\": \"2\",\"name\": \"Bonds\",\"recommendedValue\": \"25\",\"region\": [{\"id\": \"8\",\"name\": \"US\",\"recommendedValue\": \"20\",\"sector\": [{\"id\": \"23\",\"name\": \"Consumer Financial Services\",\"recommendedValue\": \"10\",\"targetValue\": \"10\"},{\"id\": \"24\",\"name\": \"Regional Banks\",\"recommendedValue\": \"5\",\"targetValue\": \"5\"},{\"id\": \"25\",\"name\": \"Retail\",\"recommendedValue\": \"5\",\"targetValue\": \"5\"}],\"targetValue\": \"20\"},{\"id\": \"9\",\"name\": \"EU\",\"recommendedValue\": \"4\",\"sector\": [{\"id\": \"26\",\"name\": \"Aerospace & Defense\",\"recommendedValue\": \"2\",\"targetValue\": \"2\"},{\"id\": \"27\",\"name\": \"Auto & Truck Manufacturers\",\"recommendedValue\": \"1\",\"targetValue\": \"1\"},{\"id\": \"28\",\"name\": \"Beverages (Nonalcoholic)\",\"recommendedValue\": \"1\",\"targetValue\": \"1\"}],\"targetValue\": \"4\"},{\"id\": \"10\",\"name\": \"Asia\",\"recommendedValue\": \"1\",\"sector\": [{\"id\": \"29\",\"name\": \"Regional Banks\",\"recommendedValue\": \"1\",\"targetValue\": \"1\"}],\"targetValue\": \"1\"}],\"targetValue\": \"25\"},{\"id\": \"3\",\"name\": \"Funds\",\"recommendedValue\": \"25\",\"region\": [{\"id\": \"11\",\"name\": \"US\",\"recommendedValue\": \"25\",\"sector\": [{\"id\": \"30\",\"name\": \"Exchange Traded Funds\",\"recommendedValue\": \"25\",\"targetValue\": \"25\"}],\"targetValue\": \"25\"}],\"targetValue\": \"25\"},{\"id\": \"4\",\"name\": \"Cash\",\"recommendedValue\": \"25\",\"targetValue\": \"25\"}]}" ;
	private static final String [] PORTFOLIO_LIST = {"100777-4", "100777-5"};
	private static final String [] PORTFOLIO_JSON_DATA = {INDIVIDUAL_STRATEGY_MOCK_DATA_PORT1, INDIVIDUAL_STRATEGY_MOCK_DATA_PORT2};
	private static final double RANGE = 0.09;
	
	private static HashMap<String, String> strategyMockData = null;
	private static HashMap<String, AssetVO> assetDataMap = null;
	
	private static void populateMockData() {
		if (null == strategyMockData) {
			strategyMockData = new HashMap<String, String>();
		}
		for (int ndx = 0; ndx < PORTFOLIO_LIST.length; ndx++) {
			strategyMockData.put(PORTFOLIO_LIST[ndx], PORTFOLIO_JSON_DATA[ndx]);
		}
	}
	
	private static String getStrategyMockData(String portfolioId) {
		if (null == strategyMockData) {
			populateMockData();
		}
		
		return strategyMockData.get(portfolioId);
	}
	
	private static ArrayList<SectorVO> processSectorData(JSONArray sectorList) throws JSONException {
		LOG.info("==========> StrategyUtil - processSectorData - Begin");
		ArrayList<SectorVO> sectorVOList = new ArrayList<SectorVO>();
		if (null != sectorList) {
			
			for (int ndx = 0; ndx < sectorList.length(); ndx++) {
				JSONObject sectorData = (JSONObject) sectorList.get(ndx);
				if (null != sectorData) {
					SectorVO sectorVO = new SectorVO();
					sectorVO.setSectorId(sectorData.getInt(StrategyConstants.ID));
					sectorVO.setSectorName(sectorData.getString(StrategyConstants.NAME));
					sectorVO.setRecommendedValue(sectorData.getDouble(StrategyConstants.RECOMMENDED_VALUE));
					sectorVO.setTargetValue(sectorData.getDouble(StrategyConstants.TARGET_VALUE));
					
					sectorVOList.add(sectorVO);
				}
			}
		}
		
		LOG.debug("==========> sectorVOList : " + sectorVOList);
		LOG.info("==========> StrategyUtil - processSectorData - End");
		return sectorVOList;
	}
	
	private static double roundNumber(double doubleValue) {
		LOG.info("==========> StrategyUtil - roundNumber - Begin");
		BigDecimal bigDecimal = new BigDecimal(doubleValue);
		bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP); 
		
		LOG.info("==========> StrategyUtil - roundNumber - End");
		return bigDecimal.doubleValue();
	}
	
	private static ArrayList<RegionVO> processRegionData(JSONArray regionList) throws JSONException {
		LOG.info("==========> StrategyUtil - processRegionData - Begin");
		ArrayList<RegionVO> regionVOList = new ArrayList<RegionVO>();
		
		if (null != regionList) {
			for (int ndx = 0; ndx < regionList.length(); ndx++) {
				JSONObject regionData = (JSONObject) regionList.get(ndx);
				if (null != regionData) {
					RegionVO regionVO = new RegionVO();
					
					regionVO.setRegionId(regionData.getInt(StrategyConstants.ID));
					regionVO.setRegionName(regionData.getString(StrategyConstants.NAME));
					regionVO.setRecommendedValue(regionData.getDouble(StrategyConstants.RECOMMENDED_VALUE));
					regionVO.setTargetValue(regionData.getDouble(StrategyConstants.TARGET_VALUE));
					
					// Process Sector Data
					regionVO.setSectorList(processSectorData((JSONArray) regionData.get(StrategyConstants.SECTOR)));
					
					regionVOList.add(regionVO);
					
				}
			}
		}
		
		LOG.debug("==========> regionVOList : " + regionVOList);
		LOG.info("==========> StrategyUtil - processRegionData - End");
		return regionVOList;
	}
	
	private static ArrayList<SegmentVO> processSegmentData(JSONArray segmentList) throws JSONException { 
		LOG.info("==========> StrategyUtil - processSegmentData - Begin");
		ArrayList<SegmentVO> segmentVOList = new ArrayList<SegmentVO>();
		
		if (null != segmentList) {
			for (int ndx = 0; ndx < segmentList.length(); ndx++) {
				JSONObject segmentData = (JSONObject) segmentList.get(ndx);
				if (null != segmentData) {
					SegmentVO segmentVO = new SegmentVO();
					
					segmentVO.setSegmentId(segmentData.getInt(StrategyConstants.ID));
					segmentVO.setSegmentName(segmentData.getString(StrategyConstants.NAME));
					segmentVO.setRecommendedValue(segmentData.getDouble(StrategyConstants.RECOMMENDED_VALUE));
					segmentVO.setTargetValue(segmentData.getDouble(StrategyConstants.TARGET_VALUE));
					segmentVO.setCustomized(false); // Default value is false
					
					// Process Region Data
					if (!StrategyConstants.CACH.equalsIgnoreCase(segmentVO.getSegmentName())) {
						segmentVO.setRegionList(processRegionData((JSONArray) segmentData.get(StrategyConstants.REGION)));
					}
					
					segmentVOList.add(segmentVO);
				}
			}
		}
		
		LOG.debug("==========> segmentVOList : " + segmentVOList);
		LOG.info("==========> StrategyUtil - processSegmentData - End");
		return segmentVOList;
	}
	
	private static void populateAssetData() {
		LOG.info("==========> StrategyUtil - populateAssetData - Begin");
		
		if (null == assetDataMap) {
			assetDataMap = new HashMap<String, AssetVO>();
		}
		
		try {
			for (int ndx = 0; ndx < PORTFOLIO_LIST.length; ndx++) {
				JSONObject assetAllocationData = new JSONObject(getStrategyMockData(PORTFOLIO_LIST[ndx]));
				
				if (null != assetAllocationData) {
					AssetVO assetVO = new AssetVO();
					assetVO.setUpdateCount(0); // Default value is 0 
					assetVO.setSegmentList(StrategyUtil.processSegmentData((JSONArray) assetAllocationData.get(StrategyConstants.SEGMENT)));
					
					assetDataMap.put(PORTFOLIO_LIST[ndx], assetVO);
				}
			}
		} catch(JSONException jEx) {
			LOG.error("==========> JSONException occurred while fetching Asset Data: " + jEx.getMessage());
		}
		
		LOG.info("==========> StrategyUtil - populateAssetData - End");
	}
	
	private static AssetVO getAssetData(String portfolioId) { 
		LOG.info("==========> StrategyUtil - getAssetData - Begin");
		
		if (null == assetDataMap) {
			populateAssetData();
		}
		
		LOG.info("==========> StrategyUtil - getAssetData - End");
		return assetDataMap.get(portfolioId);
	}
	
	public static AssetVO getDefaultAssetData(String portfolioId) {
		return getAssetData(portfolioId);
	}
	
	private static void computeRegionData(RegionVO regionVO, double diffPercent) {
		LOG.info("==========> StrategyUtil - computeRegionData - Begin");
		regionVO.setTargetValue(regionVO.getTargetValue() + (regionVO.getTargetValue() * diffPercent) / 100);
		
		if (null != regionVO.getSectorList()) {
			for (SectorVO sectorVO : regionVO.getSectorList()) {
				sectorVO.setTargetValue(sectorVO.getTargetValue() + (sectorVO.getTargetValue() * diffPercent) / 100);
			}
		}
		
		LOG.info("==========> StrategyUtil - computeRegionData - End");
	}
	
	private static void computeSegmentData(SegmentVO segmentVO, double diffPercent) {
		LOG.info("==========> StrategyUtil - computeSegmentData - Begin");
	
		segmentVO.setTargetValue(roundNumber(segmentVO.getTargetValue() + (segmentVO.getTargetValue() * diffPercent) / 100.0));
		
		if (null != segmentVO.getRegionList()) {
			for (RegionVO regionVO : segmentVO.getRegionList()) {
				regionVO.setTargetValue(roundNumber(regionVO.getTargetValue() + (regionVO.getTargetValue() * diffPercent) / 100.0));
				
				if (null != regionVO.getSectorList()) {
					for (SectorVO sectorVO : regionVO.getSectorList()) {
						sectorVO.setTargetValue(roundNumber(sectorVO.getTargetValue() + (sectorVO.getTargetValue() * diffPercent) / 100.0));
					}
				}
			}
		}
		
		LOG.info("==========> StrategyUtil - computeSegmentData - End");
	}
	
	private static boolean verifyRegionData(RegionVO regionVO) {
		LOG.info("==========> StrategyUtil - verifyRegionData - Begin");
		boolean isRegionDataValid = false;
		double totalSectorValue = 0;
		if (null != regionVO) {
			ArrayList<SectorVO> sectorList = regionVO.getSectorList();
			
			if (null != sectorList) {
				for (SectorVO sectorVO : sectorList) {
					if (null != sectorVO) {
						totalSectorValue += sectorVO.getTargetValue();
						if (sectorVO.getTargetValue() < 0) {
							break;
						}
					}
				}
			}
			
			if (Math.abs(roundNumber(totalSectorValue) - regionVO.getTargetValue()) <= RANGE) {
				isRegionDataValid = true;
			} 	
		}
		
		LOG.debug("==========> isRegionDataValid: " + isRegionDataValid);
		LOG.info("==========> StrategyUtil - verifyRegionData - End");
		return isRegionDataValid;
	}
	
	private static boolean verifySegmentData(SegmentVO segmentVO) {
		LOG.info("==========> StrategyUtil - verifySegmentData - Begin");
		boolean isSegmentDataValid = false;
		double totalRegionValue = 0;
		if (null != segmentVO) {
			ArrayList<RegionVO> regionList = segmentVO.getRegionList();
			
			if (null != regionList) {
				for (RegionVO regionVO : regionList) {
					
					if (!verifyRegionData(regionVO)) {
						break;
					} else {
						totalRegionValue += regionVO.getTargetValue();
					}
				}
			} else if(StrategyConstants.CACH.equalsIgnoreCase(segmentVO.getSegmentName())) {
				isSegmentDataValid = true;
			}
			
			if (Math.abs(roundNumber(totalRegionValue) - segmentVO.getTargetValue()) <= RANGE) {
				isSegmentDataValid = true;
			} 
		}
		
		LOG.debug("==========> isSegmentDataValid: " + isSegmentDataValid);
		LOG.info("==========> StrategyUtil - verifySegmentData - End");
		return isSegmentDataValid;
	}
	
	private static boolean verifyAssetData(AssetVO assetVO) {
		LOG.info("==========> StrategyUtil - verifyAssetData - Begin");
		boolean isAssetDataValid = false;
		
		double totalAssetValue = 0;
		
		if (null != assetVO) {
			ArrayList<SegmentVO> segmentList = assetVO.getSegmentList();
			
			if (null != segmentList) {
				for (SegmentVO segmentVO : segmentList) {
					
					if (!verifySegmentData(segmentVO)) {
						break;
					} else {
						totalAssetValue += segmentVO.getTargetValue();
					}
				}
			}
		}
		
		if (Math.abs(roundNumber(totalAssetValue) - 100.0) <= RANGE) {
			isAssetDataValid = true;
		}
		
		LOG.debug("==========> isAssetDataValid: " + isAssetDataValid);
		LOG.info("==========> StrategyUtil - verifyAssetData - End");
		return isAssetDataValid;
	}
	
	public static HashMap<String, ArrayList<AssetVO>> computeAssetData(HashMap<String, ArrayList<AssetVO>> assetMap, ComputeVO computeVO) throws InfWlthException {
		LOG.info("==========> StrategyUtil - computeAssetData - Begin");
		
		ArrayList<AssetVO> assetList = null;
		AssetVO customAssetVO = null;
		double freezedValue = 0;
		if (null != computeVO) {
			if (computeVO.getUpdateCount() > 0) {
				// Get assetVO from the cache
				
				if (null != assetMap && !assetMap.isEmpty()) {
					
					assetList = assetMap.get(computeVO.getPortfolioId());
					
					if (null != assetList && !assetList.isEmpty()) {
						customAssetVO = SerializationUtils.clone(assetList.get(assetList.size() - 1)); // Get recent AssetVO
					} else {
						customAssetVO = SerializationUtils.clone(getAssetData(computeVO.getPortfolioId())); // Use base AssetVO
					}
				} else {
					customAssetVO = SerializationUtils.clone(getAssetData(computeVO.getPortfolioId())); // Use base AssetVO
				}
				
				// Calculate freezed value
				for (SegmentVO segmentVO : customAssetVO.getSegmentList()) {
					if (segmentVO.isCustomized()) {
						freezedValue += segmentVO.getTargetValue();
					}
				}
				LOG.debug("==========> freezedValue: " + freezedValue);
			} else {
				// Get base assetVO
				customAssetVO = SerializationUtils.clone(getAssetData(computeVO.getPortfolioId())); 
			}
			
			double diffPercent = 0;
			
			// Traverse through AssetVO to find the difference  
			ArrayList<SegmentVO> segmentList = customAssetVO.getSegmentList();
			for (SegmentVO segmentVO : segmentList) {
				if (computeVO.getId() == segmentVO.getSegmentId()) {
					double segmentTargetValue = segmentVO.getTargetValue();
					double diffValue = computeVO.getTargetValue() - segmentVO.getTargetValue();
					LOG.debug("==========> diffValue: " + diffValue);
					diffPercent = (diffValue / segmentVO.getTargetValue()) * 100;
					LOG.debug("==========> diffPercent: " + diffPercent);
					computeSegmentData(segmentVO, diffPercent);
					
					diffPercent = (diffValue / (100 - segmentTargetValue - freezedValue)) * 100;
					
					segmentVO.setCustomized(true); //Freeze the segment as the computation is complete
					break;
				} else {
					// Traverse through RegionVO to find the difference 
					if (null != segmentVO.getRegionList()) {
						for (RegionVO regionVO : segmentVO.getRegionList()) {
							if (computeVO.getId() == regionVO.getRegionId()) {
								double diffValue = computeVO.getTargetValue() - regionVO.getTargetValue();
								LOG.debug("==========> diffValue: " + diffValue);
								
								diffPercent = (diffValue / regionVO.getTargetValue()) * 100;
								LOG.debug("==========> diffPercent: " + diffPercent);
								computeRegionData(regionVO, diffPercent);
								
								// Update Segment
								diffPercent = (diffValue / (100 - segmentVO.getTargetValue() - freezedValue)) * 100;
								LOG.debug("==========> diffPercent: " + diffPercent);
								
								segmentVO.setTargetValue(segmentVO.getTargetValue() + diffValue);
								segmentVO.setCustomized(true); //Freeze the segment as the computation is complete
								break;
							} else {
								// Traverse through SectorVO to find the difference 
								if (null != regionVO.getSectorList()) {
									for (SectorVO sectorVO : regionVO.getSectorList()) {
										if (computeVO.getId() == sectorVO.getSectorId()) {
											double diffValue = computeVO.getTargetValue() - sectorVO.getTargetValue();
											LOG.debug("==========> diffValue: " + diffValue);
											
											// Update Sector
											sectorVO.setTargetValue(sectorVO.getTargetValue() + diffValue);
											
											// Update Region
											regionVO.setTargetValue(regionVO.getTargetValue() + diffValue);
											
											// Update Segment
											diffPercent = (diffValue / (100 - segmentVO.getTargetValue() - freezedValue)) * 100;
											LOG.debug("==========> diffPercent: " + diffPercent);
											
											segmentVO.setTargetValue(segmentVO.getTargetValue() + diffValue);
											segmentVO.setCustomized(true); //Freeze the segment as the computation is complete
											break;
										}
									}
								}
							}
						}
					}
					
				}
			}
			
			// Now, compute for rest of the segments
			for (SegmentVO segmentVO : segmentList) {
				if (!segmentVO.isCustomized()) {
					computeSegmentData(segmentVO, -diffPercent);
				} 
			}
			
			customAssetVO.setUpdateCount(customAssetVO.getUpdateCount() + 1);
			LOG.debug("==========> customAssetVO : " + customAssetVO);
			
			
			if (!verifyAssetData(customAssetVO)) {
				throw new InfWlthException("Percentage allocation is not equal to 100%.");
			}
			
			if (null == assetList) {
				assetList = new ArrayList<AssetVO>();
			}
			
			// Add customized assetVO to the assetList
			assetList.add(customAssetVO);
			LOG.debug("==========> assetList : " + assetList);
			
			if (null == assetMap) {
				assetMap = new HashMap<String, ArrayList<AssetVO>>();
			}
			
			assetMap.put(computeVO.getPortfolioId(), assetList);
		}
		
		LOG.debug("==========> assetMap : " + assetMap);
		LOG.info("==========> StrategyUtil - computeAssetData - End");
		return assetMap;
	}
}

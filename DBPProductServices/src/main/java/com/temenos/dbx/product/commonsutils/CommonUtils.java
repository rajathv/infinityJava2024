package com.temenos.dbx.product.commonsutils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kony.dbx.util.Constants;
import com.konylabs.middleware.api.ConfigurableParametersHelper;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.ServicesManagerHelper;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.ehcache.ResultCache;
import com.konylabs.middleware.ehcache.ResultCacheImpl;
import com.konylabs.middleware.exceptions.MiddlewareException;

public class CommonUtils {
	private static final Logger LOG = LogManager.getLogger(CommonUtils.class);
	/*
	 * Generates unique id as string.
	 * SecureRandom is used instead of Random to withstand a cryptographic attack.
	 */
	public static String generateUniqueID(int length) {
		try {
			String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
	        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
	        String NUMBER = "0123456789";
	        String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
	       
	        SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG", "SUN");	        		
	        
	        if (length < 1) throw new IllegalArgumentException();
	        StringBuilder sb = new StringBuilder(length);
	        
	        for (int i = 0; i < length; i++) {
	            // 0-62 (exclusive), random returns 0-61
	            int rndCharAt = secureRandomGenerator.nextInt(DATA_FOR_RANDOM_STRING.length());
	            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

	            sb.append(rndChar);
	        }
	        return sb.toString();	
		} catch (Exception e) {
			return null;
		}
	}
	
	public static boolean isDMSIntegrationEnabled() {
		ServicesManager serviceManager;
		try {
			serviceManager = ServicesManagerHelper.getServicesManager();
			ConfigurableParametersHelper configurableParametersHelper = serviceManager
					.getConfigurableParametersHelper();
			String isDMSIntegrationEnabled = configurableParametersHelper.getServerProperty("DMS_INTEGRATION_ENABLED");
			if(StringUtils.isBlank(isDMSIntegrationEnabled))
				return false;
			return BooleanUtils.toBoolean(configurableParametersHelper.getServerProperty("DMS_INTEGRATION_ENABLED"));
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return true;
	}
	
	/*
	 * Generates unique id as string with hyphen in intervals(EX - for interval 4 -> egft-er4g-ert64-kkio, for interval 2 -> er-u7-s2,etc)
	 * SecureRandom is used instead of Random to withstand a cryptographic attack.
	 */
	public static String generateUniqueIDHyphenSeperated(int interval, int length) {
		try {
			String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
	        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
	        String NUMBER = "0123456789";
	        String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
	       
	        SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG", "SUN");	        		
	        
	        if (length < 1) throw new IllegalArgumentException();
	        StringBuilder sb = new StringBuilder(length);
	        int intervalCheck = 0;
	        for (int i = 0; i < length; i++) {
	            // 0-62 (exclusive), random returns 0-61
	            int rndCharAt = secureRandomGenerator.nextInt(DATA_FOR_RANDOM_STRING.length());
	            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
	            intervalCheck++;
	            if(intervalCheck == interval)
	            	{
	            	  sb.append("-");
	            	  intervalCheck = 0;
	            	}
	            sb.append(rndChar);
	        }
	        return sb.toString();	
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public static JSONArray getFirstOccuringArray(JSONObject obj) {
		
		if(StringUtils.isNotBlank(obj.optString("dbpErrMsg"))) {
			JSONArray array = new JSONArray();
			array.put(obj);
			return array;
		}

		Iterator<String> keys = obj.keySet().iterator();
		while(keys.hasNext()) {
			try {
				return obj.getJSONArray(keys.next());
			}
			catch(JSONException e) {
				//do nothing;
			}
		}
		return null;
	}

	/*
	 * Method to create file and write decoded base64 contents to it.
	 * @param fileContent - base64 encoded file content
	 * @param fileBaseName - file base name
	 * @param fileExtension - file extension
	 * @return file - created file
	 */
    public static File constructFileObjectFromBase64String(String fileContent, String fileBaseName, String fileExtension) {
    	File file = null;
		String fileDir = System.getProperty("java.io.tmpdir");
		try {
			file = new File(fileDir, fileBaseName + "." + fileExtension);
			byte[] fileContents = Base64.getDecoder().decode(fileContent);
			BufferedOutputStream bos = new BufferedOutputStream (new FileOutputStream(file));
			bos.write(fileContents);
			bos.flush();
			bos.close();
		}
		catch (Exception e) {
			LOG.error("Exception while decoding and writing into file: ", e);
			if (file != null) {
				file.delete();
			}
			return null;
		}
		return file;
	}
	
	public String getMaskedValue(String accountNumber) {
        String lastFourDigits;
        if (StringUtils.isNotBlank(accountNumber)) {
            if (accountNumber.length() > 4) {
                lastFourDigits = accountNumber.substring(accountNumber.length() - 4);
                accountNumber = "XXXX" + lastFourDigits;
            } else {
                accountNumber = "XXXX" + accountNumber;
            }
        }
        return accountNumber;
    }
    
	public static Object retreiveFromSession(String key, DataControllerRequest dcRequest) {
		try {
			ServicesManager servicesManager = dcRequest.getServicesManager();
			String sessionId = dcRequest.getHeader(Constants.DEVICE_ID);
			return retriveDataFromCache(servicesManager, key, sessionId);
		} catch (Exception e) {
			LOG.error("Exception occured:" + e);
			return null;
		}

	}
	/**
	 * retrieves data from cache
	 * 
	 * @param servicesManager
	 * @param result
	 * @param key
	 * @param sessionId
	 */
	private static Object retriveDataFromCache(ServicesManager servicesManager, String key, String sessionId) {
		Object result = null;
		try {
			String cacheKey = "";
			String userId = servicesManager.getIdentityHandler() != null
					? servicesManager.getIdentityHandler().getUserId()
					: "";
			cacheKey = StringUtils.isNotBlank(userId) && !Constants.USER_ID_ANONYMOUS.equalsIgnoreCase(userId) ? userId
					: sessionId;
			ResultCache resultCache = servicesManager.getResultCache();
			String valueInCache = "";
			try {
				valueInCache = resultCache.retrieveFromCache(cacheKey) != null
						? (String) resultCache.retrieveFromCache(cacheKey)
						: null;
			} catch (Exception e) {
				try {
					valueInCache = (String) ServicesManagerHelper.getServicesManager().getResultCache()
							.retrieveFromCache(cacheKey);
				} catch (MiddlewareException e1) {
					LOG.error(e1);
					valueInCache = (String) ResultCacheImpl.getInstance().retrieveFromCache(cacheKey);
				}
			}
			if (StringUtils.isNotBlank(valueInCache)) {
				Gson gson = new Gson();
				Type type = new TypeToken<HashMap<String, Object>>() {
				}.getType();
				Map<String, Object> resultMap = gson.fromJson(valueInCache, type);
				if (resultMap != null) {
					if (resultMap.get(key) != null) {
						result = resultMap.get(key);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Exception occured while retrieving from cache" + e);
			return null;
		}
		return result;
	}
}

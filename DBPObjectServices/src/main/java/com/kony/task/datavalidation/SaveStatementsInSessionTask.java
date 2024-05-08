package com.kony.task.datavalidation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.kony.memorymgmt.AccountsManager;
import com.kony.memorymgmt.SessionMap;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SaveStatementsInSessionTask implements ObjectProcessorTask{
	private static final Logger LOG = LogManager.getLogger(SaveStatementsInSessionTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			if (null != response && !response.isJsonNull()) {
				
				String regex = "/(.*?)\\\"";
				Pattern p = Pattern.compile(regex);
		        Matcher m = p.matcher(response.toString());
		        SessionMap statementsMap = new SessionMap();
		        AccountsManager accountManager = new AccountsManager(fabricRequestManager, fabricResponseManager);
		        while(m.find()) {
		        	statementsMap.addKey(m.group().substring(1,m.group().length()-1));
		        }
		        accountManager.saveStatementsIntoSession(statementsMap);
			}
		} catch (Exception e) {
			LOG.error("Exception while caching accounts in session", e);
		}
		return true;
	}

}

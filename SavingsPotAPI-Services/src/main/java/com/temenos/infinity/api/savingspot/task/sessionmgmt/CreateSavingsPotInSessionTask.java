package com.temenos.infinity.api.savingspot.task.sessionmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.memorymgmt.SavingsPotManager;
import com.kony.utilities.Constants;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class CreateSavingsPotInSessionTask implements ObjectProcessorTask {
private static final Logger LOG = LogManager.getLogger(CreateSavingsPotInSessionTask.class);

 @Override
public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
throws Exception {
JsonElement savingsPotId = null;
try {
JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
if(!response.has("dbpErrMsg") && !response.has("dbpErrCode")) {
JsonObject request = (JsonObject) fabricRequestManager.getPayloadHandler().getPayloadAsJson();
savingsPotId = response.get(Constants.SAVINGSPOTID);
JsonElement fundingAccountId = request.get(Constants.FUNDINGACCOUNTID);
JsonParser parser = new JsonParser();
String fundingAccountHoldingsId = request.get(Constants.FUNDINGACCOUNTID).toString();
String potType = request.get(Constants.POTTYPE).toString();
if ((fundingAccountHoldingsId).toString().contains("-")) {
fundingAccountId = parser.parse(fundingAccountHoldingsId.toString().substring(fundingAccountHoldingsId.indexOf("-") + 1));
}
// JsonElement fundingAccountHoldingsId = request.get(Constants.FUNDINGACCOUNTHOLDINGSID);
String customerId = HelperMethods.getAPIUserIdFromSession(fabricRequestManager).toString();
SavingsPotManager savingsPotManager = new SavingsPotManager(fabricRequestManager, fabricResponseManager);
// savingsPotManager.addSavingsPotInSession(customerId, savingsPotId,fundingAccountId,fundingAccountId);
savingsPotManager.addSavingsPotInSession(customerId, savingsPotId,fundingAccountId,parser.parse(fundingAccountHoldingsId),potType);
}
} catch (Exception e) {
LOG.error("Exception while closing SavingsPot with id"+savingsPotId.toString()+" in session", e);
}
return true;
}

}
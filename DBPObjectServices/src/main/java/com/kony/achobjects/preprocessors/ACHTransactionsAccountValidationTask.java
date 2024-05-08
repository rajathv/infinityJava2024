package com.kony.achobjects.preprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.memorymgmt.AccountsManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class ACHTransactionsAccountValidationTask implements ObjectProcessorTask {

    private static final Logger LOG = LogManager.getLogger(ACHTransactionsAccountValidationTask.class);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
            throws Exception {

        JsonObject reqPayload = fabricRequestManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        String debitAccount, template_id;
        String template_id_key = "Template_id";
        String debit_account_key = "DebitAccount";

        LOG.debug("Entered into ACHTransactionsAccountValidationTask");

        if (HelperMethods.isJsonNotNull(reqPayload)) {
            LOG.debug("request payload is not null");

            debitAccount = reqPayload.has(debit_account_key) ? reqPayload.get(debit_account_key).getAsString() : null;

            template_id = reqPayload.has(template_id_key) ? reqPayload.get(template_id_key).getAsString() : null;

            LOG.debug("Debit Account: " + debitAccount);

            if (!(debitAccount == null || debitAccount.isEmpty())) {
                AccountsManager accManager = new AccountsManager(fabricRequestManager);
                boolean accountValidationStatus = accManager.validateInternalAccount(debitAccount);
                LOG.debug("Account Validation status: " + accountValidationStatus);
                if (!accountValidationStatus) {
                    JsonObject resultJson = new JsonObject();
                    ErrorCodeEnum.ERR_13501.setErrorCode(resultJson);
                    fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resultJson);
                }
                return accountValidationStatus;
            } else if (template_id == null || template_id.isEmpty()) {
                LOG.debug("Debit Account and Template Id is Null. "
                        + "Passing the Task, considering this is MFA Request.");
                return true;
            } else {
                LOG.debug("This is template execution. "
                        + "Company Validation task will take care of Account Validation.");
                return true;
            }
        } else {
            return false;
        }
    }

}

package com.temenos.infinity.api.savingspot.postprocessors;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;
import com.temenos.infinity.api.savingspot.task.sessionmgmt.SaveSavingsPotsInSessionTask;
import com.temenos.infinity.api.savingspot.task.sessionmgmt.UpdateSavingsPotBalanceInSessionTask;

public class updateSavingsPotBalancePostProcessor implements ObjectServicePostProcessor{

	@SuppressWarnings("unchecked")
    @Override
    public void execute(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager)
            throws Exception {
        Class<? extends ObjectProcessorTask>[] tasks = new Class[] { UpdateSavingsPotBalanceInSessionTask.class };
        ObjectProcessorTaskManager.invokeAll(fabricReqManager, fabricResManager, tasks);
    }
}
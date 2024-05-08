package com.kony.consent.postprocessors;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.kony.consent.task.sessionmgmt.SavePSDConsentArrangementID;
import com.kony.objectserviceutils.EventLogPostProcessorTask;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class GetPSDConsentsPostProcessor implements ObjectServicePostProcessor {

	@SuppressWarnings("unchecked")
    @Override
    public void execute(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager)
            throws Exception {
        Class<? extends ObjectProcessorTask>[] tasks = new Class[] { SavePSDConsentArrangementID.class , EventLogPostProcessorTask.class};
        ObjectProcessorTaskManager.invokeAll(fabricReqManager, fabricResManager, tasks);
    }
}

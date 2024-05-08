package com.temenos.infinity.api.wealthOrder.preandpostprocessors;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
/**
 * (INFO) Builds the Result in the desired format for the Refinitiv service.
 * 
 * @author madhumathi
 */
public class GetOrchSearchFavouritesPreProcessor implements DataPreProcessor2 {
    private static final Logger LOG = LogManager.getLogger(GetOrchSearchFavouritesPreProcessor.class);
    @SuppressWarnings({ "rawtypes" })
    @Override
    public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
            Result result) throws Exception {
        try {
            if (request.getParameter("isFavouriteSearch") != null && request.getParameter("isFavouriteSearch").equalsIgnoreCase("true")) {
                return true ;
            } else {
                LOG.error("Invalid request");
                result.addParam("status", "Failure");
                result.addParam("error", "Unauthorized Access");
                return false;
            }
        } catch (Exception e) {
            LOG.error("Error in GetOrchSearchFavouritesPreProcessor" + e);
            e.getMessage();
            return false;
        }
    }
}
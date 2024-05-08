package com.temenos.infinity.api.holdings.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.APIImplementationTypes;
import com.dbp.core.api.factory.BackendDelegateFactory;
import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.servlet.IntegrationCustomServlet;
import com.temenos.infinity.api.holdings.constants.MSCertificateConstants;
import com.temenos.infinity.api.holdings.mapper.HoldingsBackendDelegateMapper;
import com.temenos.infinity.api.holdings.mapper.HoldingsBusinessDelegateMapper;
import com.temenos.infinity.api.holdings.mapper.HoldingsResourceMapper;
import com.temenos.infinity.api.holdings.util.HoldingsUtils;
import com.temenos.infinity.transact.tokenmanager.config.BackendCertificateStore;
import com.temenos.infinity.transact.tokenmanager.dto.BackendCertificate;

/**
 * Custom servlet used to intialize/destroy resources required by this module
 * 
 * @author Aditya Mankal
 *
 */
@IntegrationCustomServlet(servletName = "HoldingsCustomResourcesServlet", urlPatterns = {
        "HoldingsCustomResourcesServlet" })
public class HoldingsCustomResourcesServlet extends HttpServlet {
	
    private static final Logger LOG = LogManager.getLogger(HoldingsCustomResourcesServlet.class);

    private static final long serialVersionUID = -4057264841719228868L;

    @Override
    public void init() throws ServletException {

        // Register Resource Delegates
        DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(ResourceFactory.class)
                .registerResourceMappings(new HoldingsResourceMapper(), APIImplementationTypes.BASE);

        // Register Business Delegates
        DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class)
                .registerBusinessDelegateMappings(new HoldingsBusinessDelegateMapper(),
                        APIImplementationTypes.BASE);

        // Register Backend Delegates
        DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BackendDelegateFactory.class)
                .registerBackendDelegateMappings(new HoldingsBackendDelegateMapper(),
                        APIImplementationTypes.BASE);
        
        try {
            // Read from the persistent store and construct the instance of BackendCertificate
            BackendCertificate backendCertificate = HoldingsUtils.getCertFromDB(MSCertificateConstants.BACKENDCERTNAME);

            // Register backend-certificate
            BackendCertificateStore.registerBackendCertificate(MSCertificateConstants.BACKEND, backendCertificate);
            }catch (Exception e) {
    				LOG.error("Unable to fetch and register Backend Certificate from DB");
    		}
    }

    @Override
    public void destroy() {
    }

}
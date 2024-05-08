package com.temenos.infinity.api.arrangements.servlet;

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
import com.temenos.infinity.api.arrangements.constants.MSCertificateConstants;
import com.temenos.infinity.api.arrangements.mapper.ArrangementsBackendDelegateMapper;
import com.temenos.infinity.api.arrangements.mapper.ArrangementsBusinessDelegateMapper;
import com.temenos.infinity.api.arrangements.mapper.ArrangementsResourceMapper;
import com.temenos.infinity.api.arrangements.utils.ArrangementsUtils;
import com.temenos.infinity.transact.tokenmanager.config.BackendCertificateStore;
import com.temenos.infinity.transact.tokenmanager.dto.BackendCertificate;

/**
 * Custom servlet used to intialize/destroy resources required by this module
 * 
 * @author Aditya Mankal
 *
 */
@IntegrationCustomServlet(servletName = "ArrangementsCustomResourcesServlet", urlPatterns = {
        "ArrangementsCustomResourcesServlet" })
public class ArrangementsCustomResourcesServlet extends HttpServlet {
	
    private static final Logger LOG = LogManager.getLogger(ArrangementsCustomResourcesServlet.class);

    private static final long serialVersionUID = 8812359867642815541L;

    @Override
    public void init() throws ServletException {
        // Register Resource Delegates
        DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(ResourceFactory.class)
                .registerResourceMappings(new ArrangementsResourceMapper(), APIImplementationTypes.BASE);

        // Register Business Delegates
        DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class)
                .registerBusinessDelegateMappings(new ArrangementsBusinessDelegateMapper(), 
                        APIImplementationTypes.BASE);

        // Register Backend Delegates
        DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BackendDelegateFactory.class)
                .registerBackendDelegateMappings(new ArrangementsBackendDelegateMapper(),
                        APIImplementationTypes.BASE);
        try {
        // Read from the persistent store and construct the instance of BackendCertificate
        BackendCertificate backendCertificate = ArrangementsUtils.getCertFromDB(MSCertificateConstants.BACKENDCERTNAME);

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

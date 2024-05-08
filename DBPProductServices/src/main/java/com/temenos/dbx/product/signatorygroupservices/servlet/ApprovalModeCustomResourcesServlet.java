package com.temenos.dbx.product.signatorygroupservices.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.dbp.core.api.APIImplementationTypes;
import com.dbp.core.api.factory.BackendDelegateFactory;
import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.signatorygroupservices.mapper.ApprovalModeBackendDelegateMapper;
import com.temenos.dbx.product.signatorygroupservices.mapper.ApprovalModeBusinessDelegateMapper;
import com.temenos.dbx.product.signatorygroupservices.mapper.ApprovalModeResourceMapper;
import com.temenos.dbx.product.signatorygroupservices.mapper.SignatoryGroupBackendDelegateMapper;
import com.temenos.dbx.product.signatorygroupservices.mapper.SignatoryGroupBusinessDelegateMapper;
import com.temenos.dbx.product.signatorygroupservices.mapper.SignatoryGroupResourceMapper;
import com.konylabs.middleware.servlet.IntegrationCustomServlet;



/**
 * 
 * @author KH1755
 * @version 1.0
 * extends {@link HttpServlet}
 */
@IntegrationCustomServlet(servletName = "ApprovalModeCustomResourcesServlet", urlPatterns = {
"ApprovalModeCustomResourcesServlet" })
public class ApprovalModeCustomResourcesServlet extends HttpServlet{
	private static final long serialVersionUID = 5258692671194055112L;

    @Override
    public void init() throws ServletException {
    	
        DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(ResourceFactory.class)
                .registerResourceMappings(new ApprovalModeResourceMapper(), APIImplementationTypes.BASE);

        DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class)
                .registerBusinessDelegateMappings(new ApprovalModeBusinessDelegateMapper(), APIImplementationTypes.BASE);
        
        DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BackendDelegateFactory.class)
		.registerBackendDelegateMappings(new ApprovalModeBackendDelegateMapper(),
				APIImplementationTypes.BASE);
    }

    @Override
    public void destroy() {
    }
}

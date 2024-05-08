package com.temenos.dbx.eum.product.limitsandpermissions.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.dbp.core.api.APIImplementationTypes;
import com.dbp.core.api.factory.BackendDelegateFactory;
import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.servlet.IntegrationCustomServlet;
import com.temenos.dbx.eum.product.limitsandpermissions.mapper.LimitsAndPermissionsBackendDelegateMapper;
import com.temenos.dbx.eum.product.limitsandpermissions.mapper.LimitsAndPermissionsBusinessDelegateMapper;
import com.temenos.dbx.eum.product.limitsandpermissions.mapper.LimitsAndPermissionsResourceMapper;

@IntegrationCustomServlet(servletName = "LimitsAndPermissionsCustomResourceServlet", urlPatterns = {
"LimitsAndPermissionsCustomResourceServlet" })
public class LimitsAndPermissionsServlet extends HttpServlet{
	
	public static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {

        DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(ResourceFactory.class)
                .registerResourceMappings(new LimitsAndPermissionsResourceMapper(), APIImplementationTypes.BASE);

        DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class)
                .registerBusinessDelegateMappings(new LimitsAndPermissionsBusinessDelegateMapper(), APIImplementationTypes.BASE);

        DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BackendDelegateFactory.class)
        .registerBackendDelegateMappings(new LimitsAndPermissionsBackendDelegateMapper(), APIImplementationTypes.BASE);
    }

    @Override
    public void destroy() {

    }

}

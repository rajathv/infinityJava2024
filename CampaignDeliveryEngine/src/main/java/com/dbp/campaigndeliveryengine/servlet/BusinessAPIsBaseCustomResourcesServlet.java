package com.dbp.campaigndeliveryengine.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.dbp.campaigndeliveryengine.mapper.BaseBusinessDelegateMapper;
import com.dbp.campaigndeliveryengine.mapper.BaseResourcesMapper;
import com.dbp.core.api.APIImplementationTypes;
import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;

import com.konylabs.middleware.servlet.IntegrationCustomServlet;

@IntegrationCustomServlet(servletName = "BusinessAPIsBaseCustomResourcesServlet", urlPatterns = {
		"BusinessAPIsBaseCustomResourcesServlet" })
public class BusinessAPIsBaseCustomResourcesServlet extends HttpServlet {

	private static final long serialVersionUID = 5258692671194055192L;

	@Override
	public void init() throws ServletException {
		// registering base resources and business delegates
		DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(ResourceFactory.class)
				.registerResourceMappings(new BaseResourcesMapper(), APIImplementationTypes.BASE);

		DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class)
				.registerBusinessDelegateMappings(new BaseBusinessDelegateMapper(), APIImplementationTypes.BASE);
	}

	@Override
	public void destroy() {
		//do nothing
	}

}

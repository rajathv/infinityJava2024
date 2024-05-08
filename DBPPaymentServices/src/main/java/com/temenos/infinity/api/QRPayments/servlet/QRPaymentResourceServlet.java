package com.temenos.infinity.api.QRPayments.servlet;

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
import com.temenos.infinity.api.QRPayments.mapper.QRPaymentBackendDelegateMapper;
import com.temenos.infinity.api.QRPayments.mapper.QRPaymentBusinessDelegateMapper;
import com.temenos.infinity.api.QRPayments.mapper.QRPaymentResourceMapper;

@IntegrationCustomServlet(servletName = "QRPaymentResourceServlet", urlPatterns = { "QRPaymentResourceServlet" })
public class QRPaymentResourceServlet extends HttpServlet {

	private static final Logger logger = LogManager.getLogger(QRPaymentResourceServlet.class);
	private static final long serialVersionUID = -2023367655414960364L;

	@Override
	public void init() throws ServletException {

		// Register Resource Delegates
		DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(ResourceFactory.class)
				.registerResourceMappings(new QRPaymentResourceMapper(), APIImplementationTypes.BASE);

		// Register Business Delegates
		DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class)
				.registerBusinessDelegateMappings(new QRPaymentBusinessDelegateMapper(), APIImplementationTypes.BASE);

		// Register Backend Delegates
		DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BackendDelegateFactory.class)
				.registerBackendDelegateMappings(new QRPaymentBackendDelegateMapper(), APIImplementationTypes.BASE);
	}

	@Override
	public void destroy() {
	}

}
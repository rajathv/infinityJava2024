/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.config;

import com.temenos.infinity.api.commons.config.InfinityServices;

import static com.kony.dbputilities.util.EnvironmentConfigurationsHandler.getValue;

/**
 * @author k.meiyazhagan
 */
public enum TradeSupplyFinanceAPIServices implements InfinityServices {

    SERVICEREQUESTJAVA_CREATEORDER("ServiceRequestJavaService", "createOrder"),
    SERVICEREQUESTJAVA_UPDATEORDER("ServiceRequestJavaService", "updateServiceRequest"),
    SERVICEREQUESTJAVA_GETORDERDETAILS("ServiceRequestJavaService", "getOrderDetails"),
    SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID("ServiceRequestJavaService", "getServiceRequestsByID"),
    RECEIVABLECSVIMPORT_CREATESINGLEBILLS("ReceivableBillsCsvImport", "CreateCsvImportBills"),
    RECEIVABLECSVIMPORT_DELETESINGLEBILLS("ReceivableBillsCsvImport", "DeleteCsvImportedBills"),
    DBPRBLOCALSERVICE_PAYEE_GET("dbpRbLocalServicesdb", getValue("DBX_SCHEMA_NAME") + "_corporatepayees_get");


    private final String serviceName;
    private final String operationName;

    TradeSupplyFinanceAPIServices(String serviceName, String operationName) {
        this.serviceName = serviceName;
        this.operationName = operationName;
    }

    @Override
    public String getServiceName() {
        return this.serviceName;
    }

    @Override
    public String getOperationName() {
        return this.operationName;
    }
}

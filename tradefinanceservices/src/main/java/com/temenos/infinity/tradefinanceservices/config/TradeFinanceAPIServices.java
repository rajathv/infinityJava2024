package com.temenos.infinity.tradefinanceservices.config;

import com.temenos.infinity.api.commons.config.InfinityServices;

import static com.kony.dbputilities.util.EnvironmentConfigurationsHandler.getValue;

public enum TradeFinanceAPIServices implements InfinityServices {

    SERVICEREQUESTJAVA_CREATEORDER("ServiceRequestJavaService", "createOrder"),
    SERVICEREQUESTJAVA_GETORDERDETAILS("ServiceRequestJavaService", "getOrderDetails"),
    SERVICEREQUESTJAVASERVICE_GETTEMPLATEDETAILS("ServiceRequestJavaService", "getTemplateDetails"),
    SERVICEREQUESTJAVASERVICE_WITHDRAW("ServiceRequestJavaService", "withdrawServiceRequest"),
    SERVICEREQUESTJAVASERVICE_REJECT("ServiceRequestJavaService", "rejectServiceRequest"),
    SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID("ServiceRequestJavaService", "getServiceRequestsByID"),
    SERVICEREQUESTJSON_GETSERVICEREQUESTBYID("ServiceRequestJSON", "getServiceRequestsByID"),
    SERVICEREQUESTJSON_UPDATEORDER("ServiceRequestJSON", "updateOrder"),
    SERVICEREQUESTJSON_GETTEMPLATEDETAILS("ServiceRequestJSON", "getTemplateDetails"),
    SERVICEREQUESTJSON_TRIGGERFORSTATUS("ServiceRequestJSON", "triggerForStatus"),
    DBPUSERATTRIBUTES_GETUSERATTRIBUTEDETAILS("dbpUserAttributes", "getUserAttributesDetails"),
    SERVICEREQUESTJAVA_UPDATEORDER("ServiceRequestJavaService", "updateServiceRequest"),
    SERVICEREQUESTJAVA_APPROVE("ServiceRequestJavaService", "approveServiceRequest"),
    SERVICE_BACKEND_CERTIFICATE("dbpRbLocalServicesdb", "dbxdb_backendcertificate_get"),
    SERVICEREQUESTJAVA_GETLETTEROFCREDITDETAILS("ServiceRequestJavaService", "getServiceRequestsByID"),
    DOCUMENTINTEGRATIONSERVICES_UPLOADDOCUMENT("DocumentIntegrationServices", "uploadDocument"),
    DOCUMENTINTEGRATIONSERVICES_DOWNLOADDOCUMENT("DocumentIntegrationServices", "downloadDocument"),
    DOCUMENTINTEGRATIONSERVICES_DELETEDOCUMENT("DocumentIntegrationServices", "deleteDocument"),
    DBPRBLOCALSERVICE_PAYEE_GET("dbpRbLocalServicesdb", getValue("DBX_SCHEMA_NAME") + "_corporatepayees_get"),
    DBPRBLOCALSERVICE_PAYEE_CREATE("dbpRbLocalServicesdb", getValue("DBX_SCHEMA_NAME") + "_corporatepayees_create"),
    DBPRBLOCALSERVICE_PAYEE_UPDATE("dbpRbLocalServicesdb", getValue("DBX_SCHEMA_NAME") + "_corporatepayees_update"),
    DBPRBLOCALSERVICE_TRADECONFIG_GET("dbpRbLocalServicesdb", getValue("DBX_SCHEMA_NAME") + "_tradefinanceconfiguration_get"),
    DBPRBLOCALSERVICE_TRADECONFIG_CREATE("dbpRbLocalServicesdb", getValue("DBX_SCHEMA_NAME") + "_tradefinanceconfiguration_create"),
    DBPRBLOCALSERVICE_TRADECONFIG_UPDATE("dbpRbLocalServicesdb", getValue("DBX_SCHEMA_NAME") + "_tradefinanceconfiguration_update"),
    DBPRBLOCALSERVICE_PAYEMENT_PAYEE_UPDATE("dbpRbLocalServicesdb", getValue("DBX_SCHEMA_NAME") + "_externalaccount_update"),
    DASHBOARD_ORCH_FETCHALLTRADES("DashboardOverviewOrchestration", "FetchAllTradeDetails"),
    DASHBOARD_ORCH_FETCHPAYABLES("DashboardOverviewOrchestration", "GetPayables"),
    DASHBOARD_ORCH_FETCHRECEIVABLES("DashboardOverviewOrchestration", "GetReceivables"),
    DBPRBLOCALSERVICE_PAYEMENT_CREATE_CLAUSE("ClauseOrchestration", "CreateClause");


    private String serviceName, operationName;

    /**
     * @param serviceName
     * @param operationName
     */
    TradeFinanceAPIServices(String serviceName, String operationName) {
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

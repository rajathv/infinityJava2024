/**
 * 
 */
package com.temenos.dbx.datamigrationservices.test;

/**
 * @author amitabh.kotha
 *
 */
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.dbp.core.api.factory.DBPAPIAbstractFactory;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.datamigrationservices.javaservice.CreateUser;
import com.temenos.dbx.datamigrationservices.resource.api.MigrateInfinityUserResource;

@PrepareForTest({ DBPAPIAbstractFactoryImpl.class, DBPAPIAbstractFactory.class})
public class CreateUserTest {
	
	DBPAPIAbstractFactory dbpAPIAbstractFactory;
	LoggerUtil loggerUtil;
	ResourceFactory resourceFactory;
	JavaService2 javaService2;
	DataControllerRequest request;
	DataControllerResponse response;
	String methodID;
	Object[] inputArray;
	Result actualResult;
	MigrateInfinityUserResource migrationResource;
	private static MockedStatic<DBPAPIAbstractFactoryImpl> mockedStatic;
	private static MockedStatic<LoggerUtil> loggerUtilMockedStatic;
	
	@BeforeClass
	public static void init() {
		mockedStatic = Mockito.mockStatic(DBPAPIAbstractFactoryImpl.class);
		loggerUtilMockedStatic = Mockito.mockStatic(LoggerUtil.class);
	}
	

	@AfterClass
	public static void cleanup() {
		mockedStatic.close();
		loggerUtilMockedStatic.close();
	}
	
	@Before
	public void executedBefore() throws Exception {
		methodID = "METHODID";
		inputArray = new Object[10];
		request = mock(DataControllerRequest.class);
		response = mock(DataControllerResponse.class);
		
		resourceFactory = mock(ResourceFactory.class);
		migrationResource = mock(MigrateInfinityUserResource.class);
		when(resourceFactory.getResource(MigrateInfinityUserResource.class))
		.thenReturn(migrationResource);
		javaService2 = new CreateUser();
	}

	@Test
	public void testInvokeCreateUser() throws Exception {
		Result result = new Result();
		result.addOpstatusParam(0);
		result.addHttpStatusCodeParam(0);
		
		when(migrationResource.createUser(methodID, inputArray, request, response)).thenReturn(result);
		actualResult = (Result) javaService2.invoke(methodID, inputArray, request, response);
		System.out.println(actualResult);
		assertEquals(result.getOpstatusParamValue(), actualResult.getOpstatusParamValue());
		assertEquals(result.getHttpStatusCodeParamValue(), actualResult.getHttpStatusCodeParamValue());
	}

}

package com.temenos.dbx.regulator.businessdelegate.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.delegate.ServicePermissionsBusinessDelegate;
import com.dbp.core.dto.ServicePermissionsDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.MWConstants;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;

/**
 * Class to register the DBP Service - Permission Mapping
 * 
 * @author Chandan Gupta
 *
 */
public class ServicePermissionsBusinessDelegateImpl implements ServicePermissionsBusinessDelegate {

	private static final Logger LOG = LogManager.getLogger(ServicePermissionsBusinessDelegateImpl.class);

	@Override
	public List<ServicePermissionsDTO> getServicePermissionsMappings() {

		try {

			List<ServicePermissionsDTO> fabricServicePermissions = new ArrayList<>();

			JsonObject serviceResponseJSON = ServiceCallHelper.invokeServiceAndGetJson(null, null,
					URLConstants.SERVICE_PERMISSION_MAPPER_READ);

			if (!HelperMethods.isJsonNotNull(serviceResponseJSON)
					|| !HelperMethods.isJsonNotNull(serviceResponseJSON.get(MWConstants.OPSTATUS))
					|| serviceResponseJSON.get(MWConstants.OPSTATUS).getAsInt() != 0) {
				LOG.error(
						"Failed to fetch Fabric Service Permission mappings. Service Response:" + serviceResponseJSON);
				return null;
			}
			LOG.debug("Fetched Fabric Service Permission mappings");

			JsonArray servicePermissionsArray = serviceResponseJSON.get("service_permission_mapper").getAsJsonArray();
			if (servicePermissionsArray != null) {

				JsonObject currJSON = null;
				List<String> permissionsList;
				ServicePermissionsDTO fabricServicePermission = null;
				String id, serviceName, objectName, operationName, permissions;

				for (Object currObject : servicePermissionsArray) {
					if (currObject instanceof JsonObject) {
						currJSON = (JsonObject) currObject;

						id = currJSON.get("id").getAsString();
						serviceName = currJSON.get("service_name").getAsString();
						objectName = HelperMethods.isJsonNotNull(currJSON.get("object_name"))
								? currJSON.get("object_name").getAsString()
								: null;
						operationName = HelperMethods.isJsonNotNull(currJSON.get("operation"))
								? currJSON.get("operation").getAsString()
								: null;
						permissions = HelperMethods.isJsonNotNull(currJSON.get("permissions"))
								? currJSON.get("permissions").getAsString()
								: null;
						permissionsList = getStringifiedArrayAsList(permissions);
						LOG.error("permissions " + permissions);
						LOG.error("fabricServicePermission :" + serviceName + ", " + objectName + ", " + operationName
								+ ", " + permissionsList);
						fabricServicePermission = new ServicePermissionsDTO(id, serviceName, objectName,
								operationName, permissionsList);

						fabricServicePermissions.add(fabricServicePermission);
					}
				}
			}

			LOG.debug("Returning Service Permission map");
			return fabricServicePermissions;

		} catch (Exception e) {
			LOG.error("Exception in registering service-permission mapping. Exception:", e);
			return null;
		}
	}

	public static List<String> getStringifiedArrayAsList(String permissions) {

		if (StringUtils.isNotBlank(permissions)) {
			LOG.error("spilt permissions " + Arrays.asList(permissions.split(",")).toString());
			return Arrays.asList(permissions.split(","));
		}
		return new ArrayList<>();
	}
}

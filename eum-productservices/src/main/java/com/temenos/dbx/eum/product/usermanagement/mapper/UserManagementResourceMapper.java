package com.temenos.dbx.eum.product.usermanagement.mapper;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.DBPAPIMapper;
import com.dbp.core.api.Resource;
import com.temenos.dbx.eum.product.usermanagement.resource.api.ProfileManagementResource;
import com.temenos.dbx.eum.product.usermanagement.resource.impl.ProfileManagementResourceImpl;

public class UserManagementResourceMapper implements DBPAPIMapper<Resource> {
	private static final Logger LOG = LogManager.getLogger(UserManagementResourceMapper.class);

	@Override
	public Map<Class<? extends Resource>, Class<? extends Resource>> getAPIMappings() {
		Map<Class<? extends Resource>, Class<? extends Resource>> map = new HashMap<>();
		map.put(ProfileManagementResource.class, ProfileManagementResourceImpl.class);
		return map;
	}
}

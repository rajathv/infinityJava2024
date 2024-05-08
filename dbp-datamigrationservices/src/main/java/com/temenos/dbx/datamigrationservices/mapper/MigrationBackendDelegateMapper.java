package com.temenos.dbx.datamigrationservices.mapper;

import java.util.HashMap;
import java.util.Map;

import com.dbp.core.api.BackendDelegate;
import com.dbp.core.api.DBPAPIMapper;
import com.temenos.dbx.datamigrationservices.backend.api.MigrateInfinityUserBackendDelegate;
import com.temenos.dbx.datamigrationservices.backend.impl.MigrateInfinityUserBackendDelegateImpl;


public class MigrationBackendDelegateMapper implements DBPAPIMapper<BackendDelegate> {
	@Override
    public Map<Class<? extends BackendDelegate>, Class<? extends BackendDelegate>> getAPIMappings() {
		Map<Class<? extends BackendDelegate>, Class<? extends BackendDelegate>> map = new HashMap<>();
		map.put(MigrateInfinityUserBackendDelegate.class, MigrateInfinityUserBackendDelegateImpl.class);
		return map;
	}
}

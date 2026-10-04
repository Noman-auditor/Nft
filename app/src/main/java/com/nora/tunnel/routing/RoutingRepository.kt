package com.nora.tunnel.routing

import com.nora.tunnel.data.database.NoraDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutingRepository @Inject constructor(
    private val database: NoraDatabase
) {

    fun observeRules(): Flow<List<RoutingRule>> =
        database.routingDao().observeAll()

    suspend fun addRule(rule: RoutingRule) {
        database.routingDao().upsert(rule)
    }

    suspend fun updateRule(rule: RoutingRule) {
        database.routingDao().upsert(rule)
    }

    suspend fun deleteRule(rule: RoutingRule) {
        database.routingDao().delete(rule)
    }

    suspend fun setEnabled(id: String, enabled: Boolean) {
        database.routingDao().setEnabled(id, enabled)
    }

    suspend fun clearAll() {
        database.routingDao().deleteAll()
    }
}

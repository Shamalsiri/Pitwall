package com.ssiriwardana.pitwall.data.repository

import android.util.Log
import com.ssiriwardana.pitwall.core.utils.Resource
import com.ssiriwardana.pitwall.data.local.datasource.DriverLocalDataSource
import com.ssiriwardana.pitwall.data.local.entity.DriverEntity
import com.ssiriwardana.pitwall.data.mapper.DriverMapper
import com.ssiriwardana.pitwall.data.remote.datasource.DriverRemoteDataSource
import com.ssiriwardana.pitwall.domain.model.Driver
import com.ssiriwardana.pitwall.domain.repository.DriverRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * Single source of truth for the drivers
 */
class DriverRepositoryImpl(
    private val remoteDataSource: DriverRemoteDataSource,
    private val localDataSource: DriverLocalDataSource
): DriverRepository {

    companion object {
        val TAG = DriverRepositoryImpl::class.simpleName
    }

    /**
     * Retrieve drivers.
     * From local first, update the cache with remote
     */
    override suspend fun getAllDrivers(forceRefresh: Boolean): Flow<Resource<List<Driver>>> = flow {
        val cacheDrivers = localDataSource.getAllDriversOneShot()
        val cacheDomainDrivers = DriverMapper.mapToDomain(cacheDrivers)

        emit(Resource.Loading(data = cacheDomainDrivers.takeIf { it.isNotEmpty() }))

        val shouldFetchRemote = forceRefresh ||
                                !localDataSource.isCachedDataValid() ||
                                cacheDrivers.isEmpty()

        // return cache values since the cache is valid
        if (!shouldFetchRemote) {
            emit(Resource.Success(cacheDomainDrivers))
            return@flow
        }

        // Fetch fresh data from remote
        try {
            val combineData = remoteDataSource.getCurrentDrivers()
            val domainDrivers = DriverMapper.mapToDomainModel(combineData)

            if (domainDrivers.isNotEmpty()) {
                // Update cache with new data
                val entities = DriverMapper.mapToEntities(domainDrivers)
                localDataSource.clearAndInsertDrivers(entities)

                emit(Resource.Success(domainDrivers))
            } else {
                // No data from remote, use cache if available
                if (cacheDomainDrivers.isNotEmpty()) {
                    emit(Resource.Success(cacheDomainDrivers))
                } else {
                    emit(Resource.Error("No driver data available"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllDrivers: Failed to get fresh data from remote source", e)
            val errMsg = e.message ?: "Unknown error occurred"

            if (cacheDomainDrivers.isNotEmpty()) {
                emit(Resource.Error(
                    message = "Failed to fetch latest data: $errMsg",
                    data = cacheDomainDrivers
                ))
            } else {
                emit(Resource.Error("Failed to fetch drivers: $errMsg"))
            }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Get driver details given the driverId
     * @param driverId unique id of the driver to search
     */
    override suspend fun getDriverDetails(driverId: String): Flow<Resource<Driver>> = flow {
        emit(Resource.Loading())

        val cachedDriver = localDataSource.getDriverById(driverId)

        if (cachedDriver != null) {
           val domainDriver = DriverMapper.mapToDomain(cachedDriver)
            emit(Resource.Loading(data = domainDriver))

            if (localDataSource.isCachedDataValid()) {
                emit(Resource.Success(domainDriver))
                return@flow
            }
        }

        // Update local cache with remote
        try {
            val combineDriverData = remoteDataSource.getDriverDetails(driverId)
            val drivers = DriverMapper.mapToDomainModel(combineDriverData)
            val driver = drivers.firstOrNull()

            if (driver != null) {
                val entity = DriverMapper.mapToEntity(driver)
                localDataSource.insertDrivers(listOf(entity))

                emit(Resource.Success(driver))
            } else {

                if (cachedDriver != null) {
                    emit(Resource.Success(DriverMapper.mapToDomain(cachedDriver)))
                } else {
                    emit(Resource.Error("Driver not found"))
                }
            }
        } catch (e: Exception) {
            val errMsg = e.message ?: "Unknown error occurred"

            if (cachedDriver != null) {
                emit(Resource.Error(
                    message = "Failed to fetch latest details: $errMsg",
                    data = DriverMapper.mapToDomain(cachedDriver)
                ))
            } else {
                emit(Resource.Error("Failed to fetch driver details: $errMsg"))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun searchDrivers(query: String): Flow<Resource<List<Driver>>> =
        localDataSource.searchDrivers(query)
            .map<List<DriverEntity>, Resource<List<Driver>>> { entities ->
                val drivers = DriverMapper.mapToDomain(entities)
                if (drivers.isNotEmpty()) {
                    Resource.Success(drivers)
                } else {
                    Resource.Success(emptyList())
                }
            }
            .catch { e ->
                emit(Resource.Error(e.message ?: "Search failed", emptyList()))
            }
            .flowOn(Dispatchers.IO)


}

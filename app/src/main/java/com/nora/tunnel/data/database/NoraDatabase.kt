package com.nora.tunnel.data.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.nora.tunnel.core.model.Core
import com.nora.tunnel.core.model.Protocol
import com.nora.tunnel.core.model.TunnelProfile
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class Converters {

    @TypeConverter
    fun protocolToString(value: Protocol): String = value.name

    @TypeConverter
    fun stringToProtocol(value: String): Protocol =
        Protocol.valueOf(value)

    @TypeConverter
    fun coreToString(value: Core): String = value.name

    @TypeConverter
    fun stringToCore(value: String): Core =
        Core.valueOf(value)
}

@Database(
    entities = [
        TunnelProfile::class,
        ConnectionSession::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class NoraDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao

    abstract fun sessionDao(): SessionDao
}

@Dao
interface ProfileDao {

    @Query(
        "SELECT * FROM profiles ORDER BY updatedAt DESC"
    )
    fun observeAll(): Flow<List<TunnelProfile>>

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun upsert(profile: TunnelProfile)

    @Delete
    suspend fun delete(profile: TunnelProfile)

    @Query(
        "SELECT * FROM profiles WHERE id = :id"
    )
    suspend fun getById(
        id: String
    ): TunnelProfile?
}

@Entity(tableName = "connection_history")
data class ConnectionSession(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val profileId: String,

    val profileName: String,

    val protocol: Protocol,

    val core: Core,

    val startTime: Long,

    val endTime: Long? = null,

    val durationSec: Long = 0,

    val rxBytes: Long = 0,

    val txBytes: Long = 0,

    val result: String,

    val disconnectReason: String? = null
)

@Dao
interface SessionDao {

    @Query(
        "SELECT * FROM connection_history ORDER BY startTime DESC"
    )
    fun observeAll(): Flow<List<ConnectionSession>>

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insert(
        session: ConnectionSession
    )

    @Query(
        "DELETE FROM connection_history WHERE id = :id"
    )
    suspend fun delete(id: String)
}

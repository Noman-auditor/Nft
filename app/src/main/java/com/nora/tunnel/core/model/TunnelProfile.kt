yKey

@Entity(tableName = "profiles")
data class TunnelProfile(

    @PrimaryKey
    val id: String,

    val name: String,

    val protocol: Protocol,

    val core: Core,

    val transport: Transport,

    val serverAddress: String,

    val serverPort: Int,

    val config: String = "",

    val username: String? = null,

    val enabled: Boolean = true,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long = System.currentTimeMillis()
)

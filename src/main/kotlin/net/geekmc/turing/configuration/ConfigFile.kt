package net.geekmc.turing.configuration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/***
 * ConfigFile entity.
 *
 * Values here will be the default values, if the responding key doesn't exist in config.yml.
 */
@Serializable
data class ConfigFile(
    var address: String = "0.0.0.0",
    var port: Int = 25565,
    var bungeecord: Boolean = false,
    var optifine: Boolean = true,
    @SerialName("chunk-view-distance") var chunkViewDistance: Int = 16,
    @SerialName("entity-view-distance") var entityViewDistance: Int = 16,
    @SerialName("brand-name") var brandName: String = "ExampleBrandName"
)

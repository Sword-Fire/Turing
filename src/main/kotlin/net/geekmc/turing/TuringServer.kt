package net.geekmc.turing

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import net.geekmc.turing.configuration.ConfigFile
import net.minestom.server.extras.bungee.BungeeCordProxy
import net.minestom.server.extras.optifine.OptifineSupport
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path
import kotlin.io.path.notExists
import net.minestom.server.MinecraftServer as Server

/**
 * Entrypoint of entire server.
 *
 * Using [Clikt](https://ajalt.github.io/clikt/) to parse cmdline.
 */
class TuringServer : CliktCommand() {
    private val debug by option().flag(default = false)

    private lateinit var server: Server
    private lateinit var config: ConfigFile

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TuringServer::class.java)
        private const val CONFIG_PATH_IN_RESOURCE = "/configurations/config.yml"
        private const val CONFIG_PATH_IN_FILESYSTEM = "config.yml"
    }

    override fun run() {
        initLogger()

        server = Server.init()

        if (Path(CONFIG_PATH_IN_FILESYSTEM).notExists()) {
            copyResource(CONFIG_PATH_IN_RESOURCE, CONFIG_PATH_IN_FILESYSTEM)
        }

        LOGGER.info("Loading config...")
        config = Yaml.default.decodeFromStream(File(CONFIG_PATH_IN_FILESYSTEM).inputStream())
        applyConfig(config)

        LOGGER.info("Starting server on ${config.address}:${config.port}...")
        server.start(config.address, config.port)
    }

    /**
     * Initialize logger.
     *
     * When [debug] is true, the level of root logger will be set to [Level.DEBUG].
     */
    private fun initLogger() {
        if (debug) {
            Configurator.setAllLevels(LogManager.getRootLogger().name, Level.DEBUG)
        } else {
            Configurator.setAllLevels(LogManager.getRootLogger().name, Level.INFO)
        }
    }

    /**
     * Apply configs to [Server]
     *
     * @param config [ConfigFile] object which will be applied.
     */
    private fun applyConfig(config: ConfigFile) {
        if (config.bungeecord) {
            LOGGER.info("Bungeecord support is enabled.")
            BungeeCordProxy.enable()
        } else {
            LOGGER.info("Bungeecord support is disabled.")
        }

        if (config.optifine) {
            LOGGER.info("Optifine support is enabled.")
            OptifineSupport.enable()
        } else {
            LOGGER.info("Optifine support is disabled.")
        }

        Server.setBrandName(config.brandName)
        @Suppress("DEPRECATION") Server.setChunkViewDistance(config.chunkViewDistance)
        @Suppress("DEPRECATION") Server.setEntityViewDistance(config.entityViewDistance)
    }

    /**
     * Copy resource to filesystem.
     *
     * @param source resource path, which start with "/". e.g., "/configurations/config.yml".
     * @param target target path on filesystem.
     * @param isReplace if true, exist file will be replaced with no exception will be thrown.
     *
     * @exception [FileAlreadyExistsException] when file already exists and isReplace == false
     */
    private fun copyResource(source: String, target: String, isReplace: Boolean = false) {
        val targetFile = File(target).absoluteFile.normalize()
        Files.createDirectories(targetFile.toPath().parent)

        TuringServer::class.java.getResourceAsStream(source).let { iStream ->
            check(iStream != null) {
                "Resource $source can't be found in ${TuringServer::class.java.getResource("")}"
            }
            if (isReplace) {
                Files.copy(iStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } else {
                Files.copy(iStream, targetFile.toPath())
            }
        }
    }
}

fun main(vararg args: String) = TuringServer().main(args)
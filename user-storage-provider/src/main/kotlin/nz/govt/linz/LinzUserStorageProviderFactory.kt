package nz.govt.linz

import io.ebean.DatabaseFactory
import io.ebean.config.DatabaseConfig
import io.ebean.datasource.DataSourceConfig
import nz.govt.linz.DatabaseConfiguration.Companion.PASSWORD
import nz.govt.linz.DatabaseConfiguration.Companion.URL
import nz.govt.linz.DatabaseConfiguration.Companion.USERNAME
import nz.govt.linz.ifx.InformixPlatform
import nz.govt.linz.service.UserService
import org.keycloak.component.ComponentModel
import org.keycloak.component.ComponentValidationException
import org.keycloak.models.KeycloakSession
import org.keycloak.models.RealmModel
import org.keycloak.provider.ProviderConfigProperty
import org.keycloak.provider.ProviderConfigurationBuilder
import org.keycloak.storage.UserStorageProviderFactory

class LinzUserStorageProviderFactory : UserStorageProviderFactory<LinzUserStorageProvider> {

    init {
        System.err.println("CREATING LinzUserStorageProviderFactory")
    }

    private lateinit var userService: UserService

    override fun create(session: KeycloakSession?, model: ComponentModel?): LinzUserStorageProvider {
        if (!this::userService.isInitialized) {
            userService = createUserService(checkNotNull(model).getDatabaseConfiguration())
        }
        System.err.println("LinzUserStorageProviderFactory.create")
        session?.setAttribute(UserService.ATTRIBUTE, userService)
        return LinzUserStorageProvider(checkNotNull(session), checkNotNull(model))
    }

    override fun getConfigProperties(): List<ProviderConfigProperty> {
        System.err.println("LinzUserStorageProviderFactory.getConfigProperties")
        return ProviderConfigurationBuilder.create()
            .property(URL, "JDBC URL", "Informix URL", ProviderConfigProperty.STRING_TYPE, "", null)
            .property(USERNAME, "Username", "Informix Username", ProviderConfigProperty.STRING_TYPE, "", null)
            .property(PASSWORD, "Password", "Informix Password", ProviderConfigProperty.PASSWORD, "", null)
            .build()
    }

    override fun validateConfiguration(session: KeycloakSession?, realm: RealmModel?, config: ComponentModel?) {
        System.err.println("LinzUserStorageProviderFactory.validateConfiguration")
        checkNotNull(config)
        val url = config[URL]
        val username = config[USERNAME]
        val password = config[PASSWORD]

        if (url.isNullOrBlank() || username.isNullOrBlank() || password.isNullOrBlank()) {
            throw ComponentValidationException("All database properties are required")
        }
    }

    override fun onUpdate(
        session: KeycloakSession?,
        realm: RealmModel?,
        oldModel: ComponentModel?,
        newModel: ComponentModel?
    ) {
        if (newModel != oldModel) {
            System.err.println("Creating new database")
            if (!this::userService.isInitialized) {
                userService.shutdown()
            }
            try {
                userService = createUserService(checkNotNull(newModel).getDatabaseConfiguration())
            } catch (e: Exception) {
                userService = createUserService(checkNotNull(oldModel).getDatabaseConfiguration())
                throw e
            }
        } else {
            System.err.println("Ignoring update, no change")
        }
    }

    override fun getId(): String {
        System.err.println("LinzUserStorageProviderFactory.getId")
        return PROVIDER_ID
    }

    override fun getHelpText(): String {
        System.err.println("LinzUserStorageProviderFactory.getHelpText")
        return "LINZ User Provider"
    }

    companion object {
        const val PROVIDER_ID = "linz-user-provider"
    }

    private fun createUserService(config: DatabaseConfiguration): UserService {
        // DriverManager.registerDriver(IfxDriver())
        // somehow this is necessary here as provider libs (inside providers dir) are not automatically
        // recognized and drivers are not automatically registered
        Class.forName("com.informix.jdbc.IfxDriver")
        val (url, username, password) = config
        return DatabaseConfig().also { dbConfig ->
            dbConfig.dataSourceConfig = DataSourceConfig().also { dsConfig ->
                dsConfig.driver = "com.informix.jdbc.IfxDriver"
                dsConfig.heartbeatSql = "select 'heartbeat' from crs_sys_code limit 1"
                dsConfig.url = url
                dsConfig.username = username
                dsConfig.password = password
            }
            dbConfig.isDefaultServer = true
            dbConfig.name = "ifx"
            dbConfig.databasePlatform = InformixPlatform()
        }.let { DatabaseFactory.create(it) }.let { UserService(it) }
    }
}

data class DatabaseConfiguration(val url: String, val username: String, val password: String) {
    companion object {
        const val URL = "informix.url"
        const val USERNAME = "informix.username"
        const val PASSWORD = "informix.password"
    }
}

fun ComponentModel.getDatabaseConfiguration() = DatabaseConfiguration(this[URL], this[USERNAME], this[PASSWORD])
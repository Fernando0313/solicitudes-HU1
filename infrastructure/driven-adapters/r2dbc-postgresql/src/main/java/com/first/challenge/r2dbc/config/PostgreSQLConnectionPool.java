package com.first.challenge.r2dbc.config;

import com.first.challenge.model.secret.SecretModel;
import com.first.challenge.secretsmanager.adapter.SecretsAdapter;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.client.SSLMode;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Configuration
public class PostgreSQLConnectionPool {
    /* Change these values for your project */
    public static final int INITIAL_SIZE = 12;
    public static final int MAX_SIZE = 15;
    public static final int MAX_IDLE_TIME = 30;
    public static final int DEFAULT_PORT = 5432;
    private final SecretsAdapter secretsAdapter;

    public PostgreSQLConnectionPool(SecretsAdapter secretsAdapter) {
        this.secretsAdapter = secretsAdapter;
    }


    @Bean
    public ConnectionPool connectionPool(PostgresqlConnectionProperties properties) {
        // 🚨 Bloqueamos SOLO en arranque para inicializar el pool
        SecretModel secret = secretsAdapter.getSecrets().block();

        PostgresqlConnectionConfiguration.Builder builder = PostgresqlConnectionConfiguration.builder()
                .host(secret.getDbSolicitudHost())
                .port(properties.port())
                .database(secret.getDbSolicitudDatabase())
                .schema(properties.schema())
                .username(secret.getDbSolicitudUser())     // 👈 secret
                .password(secret.getDbSolicitudpassword()); // 👈 secret

        if (Boolean.TRUE.equals(properties.ssl())) {
            builder.enableSsl();
            builder.sslMode(SSLMode.REQUIRE);
        }

        PostgresqlConnectionConfiguration dbConfiguration = builder.build();

        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder()
                .connectionFactory(new PostgresqlConnectionFactory(dbConfiguration))
                .name("api-postgres-connection-pool")
                .initialSize(INITIAL_SIZE)
                .maxSize(MAX_SIZE)
                .maxIdleTime(Duration.ofMinutes(MAX_IDLE_TIME))
                .validationQuery("SELECT 1")
                .build();

        return new ConnectionPool(poolConfiguration);
    }

}
package com.crm.ticket.config;

import io.r2dbc.mssql.MssqlConnectionFactoryProvider;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.crm.ticket.data.repository")
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    @Value("${spring.r2dbc.url}")
    private String r2dbcUrl;

    @Value("${spring.r2dbc.username}")
    private String username;

    @Value("${spring.r2dbc.password}")
    private String password;

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
        ConnectionFactoryOptions urlOptions = ConnectionFactoryOptions.parse(r2dbcUrl);

        ConnectionFactoryOptions.Builder optionsBuilder = ConnectionFactoryOptions.builder()
                .from(urlOptions)
                .option(ConnectionFactoryOptions.USER, username)
                .option(ConnectionFactoryOptions.PASSWORD, password)
                // 1. Hatalı çalışan önbellekleme mekanizmasını kapatıyoruz.
                .option(Option.valueOf("preparedStatementCacheSize"), 0)
                // 2. Hatalara yol açabilen diğer bir durum yönetimi özelliği olan
                // imleçli yürütmeyi (cursored execution) kapatıyoruz.
                .option(Option.valueOf("preferCursoredExecution"), false);

        return new MssqlConnectionFactoryProvider().create(optionsBuilder.build());
    }
}
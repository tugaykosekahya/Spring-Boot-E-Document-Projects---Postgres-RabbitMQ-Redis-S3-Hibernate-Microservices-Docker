package tr.gov.gib.evdbelge.evdbelgeaktarma.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@EnableJpaRepositories(
        basePackages = {"tr.gov.gib.evdbelge.evdbelgeaktarma.dao"},
        entityManagerFactoryRef = "postgreEvdbelgeEntityManager",
        transactionManagerRef = "postgreEvdbelgeTransactionManager"
)
public class PostgreDBConfig {

    @Value("${postgres.evdbelge.datasource.driver-class}") public String POSTGRES_DRIVER_MANAGER;
    @Value("${postgres.evdbelge.datasource.hibernate.dialect}") public String POSTGRES_HIBERNATE_DIALECT;
    @Value("${postgres.evdbelge.datasource.hibernate.show_sql}") public String POSTGRES_HIBERNATE_SHOW_SQL;
    @Value("${postgres.evdbelge.datasource.jdbc}") public String POSTGRES_EVDBELGE_DATABASE_URL;
    @Value("${postgres.evdbelge.datasource.username}") public String POSTGRES_EVDBELGE_USERNAME;
    @Value("${postgres.evdbelge.datasource.password}") public String POSTGRES_EVDBELGE_PASSWORD;
    @Value("${postgres.evdbelge.datasource.maximum-pool-size}") public int POSTGRES_EVDBELGE_MAX_POOL_SIZE;
    @Value("${postgres.evdbelge.datasource.minimum-idle}") public int POSTGRES_EVDBELGE_MIN_IDLE;
    @Value("${postgres.evdbelge.datasource.connectionTimeout}") public long POSTGRES_EVDBELGE_CONNECTION_TIMEOUT;
    @Value("${postgres.evdbelge.datasource.validationTimeout}") public long POSTGRES_EVDBELGE_VALIDATION_TIMEOUT;
    @Value("${postgres.evdbelge.datasource.maxLifetime}") public long POSTGRES_EVDBELGE_MAX_LIFETIME;

    public HikariDataSource dataSourceEvdbelge;

    public HikariDataSource dataSourceEvdbelge() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(POSTGRES_DRIVER_MANAGER);
        hikariConfig.setJdbcUrl(POSTGRES_EVDBELGE_DATABASE_URL);
        hikariConfig.setUsername(POSTGRES_EVDBELGE_USERNAME);
        hikariConfig.setPassword(POSTGRES_EVDBELGE_PASSWORD);
        hikariConfig.setMaximumPoolSize(POSTGRES_EVDBELGE_MAX_POOL_SIZE);
        hikariConfig.setMinimumIdle(POSTGRES_EVDBELGE_MIN_IDLE);
        hikariConfig.setConnectionTimeout(POSTGRES_EVDBELGE_CONNECTION_TIMEOUT);
        hikariConfig.setValidationTimeout(POSTGRES_EVDBELGE_VALIDATION_TIMEOUT);
        hikariConfig.setMaxLifetime(POSTGRES_EVDBELGE_MAX_LIFETIME);
        if(dataSourceEvdbelge == null) dataSourceEvdbelge = new HikariDataSource(hikariConfig);
        return dataSourceEvdbelge;
    }

    @Primary
    @Bean(name = "postgreEvdbelgeEntityManager")
    public LocalContainerEntityManagerFactoryBean postgreEntityManagerFactory() {
        HashMap properties = new HashMap();
        properties.put("hibernate.dialect", POSTGRES_HIBERNATE_DIALECT);
        properties.put("hibernate.show_sql", POSTGRES_HIBERNATE_SHOW_SQL);
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.put("hibernate.jdbc.use_get_generated_keys", "true");
        properties.put("hibernate.ddl-auto", "none");
        properties.put("open-in-view", "false");
        properties.put("hibernate.format_sql", "true");
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), properties, null)
                .dataSource(dataSourceEvdbelge())
                .packages("tr.gov.gib.evdbelge.evdbelgeaktarma.entity")
                .persistenceUnit("postgreEvdbelge")
                .build();
    }

    @Bean(name = "postgreEvdbelgeTransactionManager")
    public PlatformTransactionManager transactionManagerEvdbelge() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(postgreEntityManagerFactory().getObject());
        tm.setDataSource(dataSourceEvdbelge());
        return tm;
    }
}

package tr.gov.gib.evdbelge.evdbelgesorgulama.config;

import org.hibernate.dialect.PostgreSQL10Dialect;

import java.sql.Types;

public class PostgresDialect extends PostgreSQL10Dialect {

    public PostgresDialect() {
        super();
        registerHibernateType(Types.OTHER, JsonBinaryType.class.getName());
        registerHibernateType(Types.OTHER, InetType.class.getName());
    }
}

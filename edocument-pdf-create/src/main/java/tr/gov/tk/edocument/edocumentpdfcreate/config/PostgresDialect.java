package tr.gov.gib.evdbelge.evdbelgepdfolusturma.config;

import org.hibernate.dialect.PostgreSQL10Dialect;

import java.sql.Types;

public class PostgresDialect extends PostgreSQL10Dialect {

    public PostgresDialect() {
        super();
        registerHibernateType(Types.OTHER, JsonBinaryType.class.getName());
    }
}

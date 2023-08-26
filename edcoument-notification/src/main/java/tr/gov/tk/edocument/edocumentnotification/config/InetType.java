package tr.gov.gib.evdbelge.evdbelgeteblig.config;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import tr.gov.gib.tahsilat.thsbaseobject.GibObjectMapper;

import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class InetType implements UserType {

    private static final GibObjectMapper GIB_OBJECT_MAPPER = new GibObjectMapper();

    @Override
    public int[] sqlTypes() {
        return new int[] {Types.OTHER };
    }

    @Override
    public Class returnedClass() {
        return String.class;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        /*final String value =  (String) StandardBasicTypes.STRING.nullSafeGet(rs, names, session, owner);
        return value;*/
        final String cellContent = rs.getString(names[0]);
        if (cellContent == null) {
            return null;
        }
        try {
            return GIB_OBJECT_MAPPER.readValue(cellContent.getBytes(StandardCharsets.UTF_8), returnedClass());
        } catch (final Exception exp) {
            throw new RuntimeException("Failed to convert json type: " + exp.getMessage(), exp);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {

        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }
        try {
            final StringWriter writer = new StringWriter();
            GIB_OBJECT_MAPPER.writeValue(writer, value);
            writer.flush();
            st.setObject(index, value.toString(), Types.OTHER);
        } catch (final Exception exp) {
            throw new RuntimeException("Failed to convert json type: " + exp.getMessage(), exp);
        }

    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return true;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return null;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return null;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return null;
    }

}

package tr.gov.gib.evdbelge.evdbelgesorgulama.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;
import tr.gov.gib.tahsilat.thsbaseobject.GibObjectMapper;

import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

public class JsonBinaryType implements UserType, ParameterizedType {

    private static final GibObjectMapper GIB_OBJECT_MAPPER = new GibObjectMapper();
    private static final Gson GSON = new Gson();
    private Class<?> clazz = Object.class;

    @Override
    public void setParameterValues(Properties params) {
        String className = params.getProperty("className");

        try {
            if(className != null)
                clazz = Class.forName(className);
        } catch (ClassNotFoundException cnfe) {
            throw new HibernateException("className not found", cnfe);
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[] {Types.JAVA_OBJECT};
    }

    @Override
    public Class returnedClass() {
        return clazz;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        } else if (x == null || y == null) {
            return false;
        } else {
            return x.equals(y);
        }
    }


    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {

        final String cellContent = rs.getString(names[0]);
        if (cellContent == null) {
            return null;
        }
        try {
            if(returnedClass().equals(JsonObject.class))
                return GSON.fromJson(cellContent, JsonObject.class);
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
            if(value instanceof JsonObject)
                st.setObject(index, value.toString(), Types.OTHER);
            else {
                final StringWriter writer = new StringWriter();
                GIB_OBJECT_MAPPER.writeValue(writer, value);
                writer.flush();
                st.setObject(index, value.toString(), Types.OTHER);
            }
        } catch (final Exception exp) {
            throw new RuntimeException("Failed to convert json type: " + exp.getMessage(), exp);
        }

    }

    @Override
    @SneakyThrows
    public Object deepCopy(Object value) throws HibernateException {
        /*try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(value);
            //oos.flush();

            try (ByteArrayInputStream bais = new ByteArrayInputStream(bos.toByteArray());
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                return ois.readObject();
            }
        } catch (ClassNotFoundException | IOException exp) {
            throw new HibernateException(exp);
        }*/
        /*return GIB_OBJECT_MAPPER.readValue(GIB_OBJECT_MAPPER.writeValueAsString(value),
                returnedClass());*/
        return value == null ? null : GSON.fromJson(value.toString(), JsonObject.class);
        //return GSON.fromJson(GIB_OBJECT_MAPPER.toJsonString(value), returnedClass());
    }

    @Override
    public boolean isMutable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        // TODO Auto-generated method stub
        return null;
    }

}

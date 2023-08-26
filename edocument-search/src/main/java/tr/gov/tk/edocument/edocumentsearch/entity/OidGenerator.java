package tr.gov.gib.evdbelge.evdbelgesorgulama.entity;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import tr.com.cs.csap.util.OIDFactory;

import java.io.Serializable;

public class OidGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return getOid();
    }

    public static synchronized String getOid() {
        try {
            return OIDFactory.getOID();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

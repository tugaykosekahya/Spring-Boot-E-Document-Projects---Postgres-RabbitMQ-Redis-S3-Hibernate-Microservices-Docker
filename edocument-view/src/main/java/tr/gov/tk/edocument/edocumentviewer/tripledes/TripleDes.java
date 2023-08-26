package tr.gov.gib.evdbelge.evdbelgegoruntuleme.tripledes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class TripleDes {

    private static final String CRYPT_ALGORITHM = "DESede";
    private static final String PADDING = "DESede/CBC/PKCS5Padding";
    private static final String CHAR_ENCODING = "UTF-8";

    @Value("${tripledes.key}")
    private String myKey;

    @Value("${tripledes.iv}")
    private String myIv;

    public String encrypt(String message) throws Exception {
        final SecretKey key = new SecretKeySpec(getKeyOrIv(myKey), CRYPT_ALGORITHM);
        final IvParameterSpec iv = new IvParameterSpec(getKeyOrIv(myIv));
        final Cipher cipher = Cipher.getInstance(PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        final byte[] plainTextBytes = message.getBytes(CHAR_ENCODING);
        final byte[] cipherText = cipher.doFinal(plainTextBytes);

        return Base64.getEncoder().withoutPadding().encodeToString(cipherText);
    }

    public String decrypt(String message) throws Exception {
        final SecretKey key = new SecretKeySpec(getKeyOrIv(myKey), CRYPT_ALGORITHM);
        final IvParameterSpec iv = new IvParameterSpec(getKeyOrIv(myIv));
        final Cipher decipher = Cipher.getInstance(PADDING);
        decipher.init(Cipher.DECRYPT_MODE, key, iv);

        final byte[] cipherText = decipher.doFinal(Base64.getDecoder().decode(message));

        return new String(cipherText, CHAR_ENCODING);
    }

    private byte[] getKeyOrIv(String value){
        return Base64.getDecoder().decode(value);
    }
}

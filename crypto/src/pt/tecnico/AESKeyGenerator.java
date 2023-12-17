package pt.tecnico;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class AESKeyGenerator {

    public static Key genKey() throws GeneralSecurityException, IOException {
        // get an AES private key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        Key key = keyGen.generateKey();

        return key;
    }

    public static Key read(String keyPath) throws GeneralSecurityException, IOException {
        FileInputStream fis = new FileInputStream(keyPath);
        byte[] encoded = new byte[fis.available()];
        fis.read(encoded);
        fis.close();

        return new SecretKeySpec(encoded, 0, 16, "AES");
    }

    public static void write(String keyPath, Key key) throws GeneralSecurityException, IOException {
        byte[] encoded = key.getEncoded();


        FileOutputStream fos = new FileOutputStream(keyPath);
        fos.write(encoded);
        fos.close();
    }

}

package pt.tecnico.groove.service;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;

import org.springframework.util.FileCopyUtils;

import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.IOException;
import java.security.Key;

import pt.tecnico.JsonProtector;

@Service
public class KeyService {

    private static final String keyFolder = "/keys/";

    public static Key readSecretKey(String secretKeyPath) throws Exception {
        byte[] encoded = readResource(keyFolder + secretKeyPath);
        return new SecretKeySpec(encoded, "AES");
    }

    public static void writeSecretKey(String secretKeyPath, Key key) throws Exception {
        byte[] encoded = key.getEncoded();
        writeResource(keyFolder + secretKeyPath, encoded);
    }

    private static byte[] readResource(String resourcePath) throws IOException {
        Resource resource = new ClassPathResource(resourcePath);
        return FileCopyUtils.copyToByteArray(resource.getInputStream());
    }

    private static void writeResource(String resourcePath, byte[] encoded) throws IOException {
        // Create the file in the resources folder
        File file = new File("src/main/resources" + resourcePath);
        file.getParentFile().mkdirs();
        file.createNewFile();

        // Write the encoded key to the file
        WritableResource resource = new PathResource(file.getAbsolutePath());
        FileCopyUtils.copy(encoded, resource.getOutputStream());
    }

    public static JsonObject protectKey(Key userKey, Key masterKey) throws Exception {
        return JsonProtector.protectKey(userKey, masterKey);
    }
}

package pt.tecnico;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.util.Base64;

public class JsonProtector {

    public static String CIPHER_ALGO = "AES";
    public static String CIPHER_BLOCK_MODE = "CTR";
    public static String CIPHER_PADDING = "NoPadding";

    public static String CIPHER = CIPHER_ALGO + "/" + CIPHER_BLOCK_MODE + "/" + CIPHER_PADDING;

    public static String MAC_ALGO = "HmacSHA256";
    public static long MIC_TTL = 30000; // 30 seconds

    // Used to encrypt user key with 'master' key
    public static JsonObject protectKey(Key userKey, Key key) throws Exception {
        byte[] iv = generateIV();

        JsonObject root = new JsonObject();

        byte[] chipheredKey = cipher(userKey.getEncoded(), key, iv);
        String base64ChipheredKey = Base64.getEncoder().encodeToString(chipheredKey);

        root.addProperty("userkey", base64ChipheredKey);

        JsonObject metadata = createMetadata(iv);
        root.add("metadata", metadata);
        root.addProperty("MIC", createMIC(root, metadata, key));

        return root;
    }

    public static Key unprotectKey(JsonObject root, Key masterKey) throws Exception {
        JsonObject metadata = root.get("metadata").getAsJsonObject();
        String MIC = root.get("MIC").getAsString();

        System.out.println("MIC: " + MIC);

        // Extract IV
        JsonObject cipherMetadata = metadata.get("cipher").getAsJsonObject();
        System.out.println("cipherMetadata: " + cipherMetadata);
        byte[] iv = Base64.getDecoder().decode(cipherMetadata.get("initialization-vector").getAsString());

        // Decipher user key
        byte[] cipheredUserKey = Base64.getDecoder().decode(root.get("userkey").getAsString());
        byte[] userKeyBytes = decipher(cipheredUserKey, masterKey, iv);


        return new SecretKeySpec(userKeyBytes, "AES");
    }

    public static JsonObject protect(JsonObject data, Key key) throws Exception {
        byte[] iv = generateIV();

        JsonObject root = new JsonObject();

        // Cipher media content
        JsonObject media = data.get("media").getAsJsonObject();
        JsonObject mediaContent = media.get("mediaContent").getAsJsonObject();

        byte[] cipheredMediaContent = cipher(mediaContent.toString().getBytes(), key, iv);
        String base64CipheredMediaContent = Base64.getEncoder().encodeToString(cipheredMediaContent);

        media.addProperty("mediaContent", base64CipheredMediaContent);
        data.add("media", media);
        root.add("data", data);

        JsonObject metadata = createMetadata(iv);
        root.add("metadata", metadata);
        root.addProperty("MIC", createMIC(data, metadata, key));

        return root;
    }

    public static void check(JsonObject root, Key key) throws Exception{
        JsonObject data = root.get("data").getAsJsonObject();
        JsonObject metadata = root.get("metadata").getAsJsonObject();
        String MIC = root.get("MIC").getAsString();

        String recomputedMIC = createMIC(data, metadata, key);

        if (!MIC.equals(recomputedMIC)) {
            throw new Exception("MIC does not match");
        }

        long timestamp = metadata.get("mic").getAsJsonObject().get("timestamp").getAsLong();
        long now = System.currentTimeMillis();

        // Check if MIC is older than 30 seconds
        if (now - timestamp > MIC_TTL) {
            throw new Exception("Message is not fresh");
        }

        System.out.println("Message is fresh and authentic");
    }

    public static JsonObject unprotect(JsonObject root, Key key) throws Exception {
        JsonObject data = root.get("data").getAsJsonObject();
        JsonObject media = data.get("media").getAsJsonObject();
        String mediaContentString = media.get("mediaContent").getAsString();

        // Extract IV
        JsonObject cipherMetadata = root.get("metadata").getAsJsonObject().get("cipher").getAsJsonObject();
        byte[] iv = Base64.getDecoder().decode(cipherMetadata.get("initialization-vector").getAsString());

        // Decipher media content
        byte[] cipheredMediaContent = Base64.getDecoder().decode(mediaContentString);
        byte[] mediaContentBytes = decipher(cipheredMediaContent, key, iv);
        JsonObject mediaContent = new Gson().fromJson(new String(mediaContentBytes), JsonObject.class);

        media.add("mediaContent", mediaContent);

        return data;
    }

    private static byte[] decipher(byte[] bytes, Key key, byte[] iv)
            throws IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(CIPHER);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        return cipher.doFinal(bytes);
    }
    public static byte[] generateIV() {
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }

    public static byte[] cipher(byte[] bytes, Key key, byte[] iv) throws IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(CIPHER);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        return cipher.doFinal(bytes);
    }

    private static JsonObject createMetadata(byte[] iv) {
        JsonObject metadata = new JsonObject();

        JsonObject cipherMetadata = new JsonObject();
        String base64Iv = Base64.getEncoder().encodeToString(iv);
        cipherMetadata.addProperty("algorithm", CIPHER_ALGO);
        cipherMetadata.addProperty("block-mode", CIPHER_BLOCK_MODE);
        cipherMetadata.addProperty("padding", CIPHER_PADDING);
        cipherMetadata.addProperty("initialization-vector", base64Iv);

        metadata.add("cipher", cipherMetadata);

        JsonObject micMetadata = new JsonObject();
        micMetadata.addProperty("algorithm", MAC_ALGO);
        micMetadata.addProperty("timestamp", System.currentTimeMillis());

        metadata.add("mic", micMetadata);

        return metadata;
    }

    private static String createMIC(JsonObject data, JsonObject metadata, Key key) throws Exception {
        String allData = data.toString() + metadata.toString();

        byte[] plainBytes = allData.getBytes();
        byte[] macBytes = makeMAC(plainBytes, key);

        return Base64.getEncoder().encodeToString(macBytes);
    }

    public static byte[] makeMAC(byte[] plainBytes, Key key) throws Exception {
        Mac mac = Mac.getInstance(MAC_ALGO);
        mac.init(key);
        return mac.doFinal(plainBytes);
    }

}

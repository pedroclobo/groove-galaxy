package pt.tecnico;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * The SecureGroove class provides functionality for protecting and unprotecting
 * JSON files using AES encryption. It supports the following operations:
 * - protect: Ciphers a file using a secret key and saves the ciphered data to an output file.
 * - check: Checks the authenticity of a ciphered file.
 * - unprotect: Deciphers an encrypted file using a secret key and saves the deciphered data to an output file.
 */
public class SecureGroove {

    public static String CIPHER_ALGO = "AES";
    public static String CIPHER_BLOCK_MODE = "CTR";
    public static String CIPHER_PADDING = "NoPadding";

    public static String CIPHER = CIPHER_ALGO + "/" + CIPHER_BLOCK_MODE + "/" + CIPHER_PADDING;

    public static String MAC_ALGO = "HmacSHA256";
    public static long MIC_TTL = 30000; // 30 seconds

    /**
     * The main method is the entry point of the SecureGroove program.
     * It parses the command line arguments and executes the corresponding command.
     *
     * @param args The command line arguments passed to the program.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        String commandKind = args[0];
        switch (commandKind) {
            case "protect":
                if (args.length != 4) {
                    printHelp();
                    return;
                }

                String inputPath = args[1];
                String outputPath = args[2];
                String keyPath = args[3];

                try {
                    protect(inputPath, outputPath, keyPath);
                } catch (Exception e) {
                    System.err.println("Error protecting file:\n" + e.getMessage());
                }

                break;

            case "check":
                if (args.length != 3) {
                    printHelp();
                    return;
                }

                inputPath = args[1];
                keyPath = args[2];

                try {
                    check(inputPath, keyPath);
                } catch (Exception e) {
                    System.err.println("Error checking file:\n" + e.getMessage());
                }

                break;

            case "unprotect":
                if (args.length != 4) {
                    printHelp();
                    return;
                }

                inputPath = args[1];
                outputPath = args[2];
                keyPath = args[3];

                try {
                    unprotect(inputPath, outputPath, keyPath);
                } catch (Exception e) {
                    System.err.println("Error unprotecting file:\n" + e.getMessage());
                }

                break;

            default:
                printHelp();
                break;
        }
    }

    /**
     * Prints the help message with the available command line options.
     */
    private static void printHelp() {
        System.err.println("Argument(s) missing!");
        System.err.println("Usage: secure-groove help");
        System.err.println("Usage: secure-groove protect (input-file) (output-file) (key-file)");
        System.err.println("Usage: secure-groove check (input-file) (key-file)");
        System.err.println("Usage: secure-groove unprotect (input-file) (output-file) (key-file)");
    }

    /**
     * Ciphers the media content from the input file using the provided secret
     * key and writes the ciphered data along with the cipher algorithm metadata
     * to the output file.
     *
     * @param inputPath  The path to the input file containing the data to be ciphered.
     * @param outputPath The path to the output file where the ciphered data and metadata will be saved.
     * @param keyPath    The path to the file containing the secret key used for encryption.
     * @throws Exception If an error occurs during the encryption process.
     */
    private static void protect(String inputPath, String outputPath, String keyPath) throws Exception {
        Key key = readSecretKey(keyPath);
        JsonObject data = readJsonFile(inputPath);
        writeJsonFile(outputPath, JsonProtector.protect(data, key));
    }

    /**
     * Checks the integrity of the media content in the input file using the provided secret
     * key.
     *
     * @param inputPath  The path to the input file containing the ciphered data.
     * @param keyPath    The path to the file containing the secret key used for decryption.
     * @throws Exception If an error occurs during the decryption process.
     */
    private static void check(String inputPath, String keyPath) throws Exception {
        Key key = readSecretKey(keyPath);
        JsonObject root = readJsonFile(inputPath);
        JsonProtector.check(root, key);
    }

    /**
     * Deciphers the media content in the input file using the provided secret
     * key and writes the deciphered data to the output file.
     *
     * @param inputPath  The path to the input file containing the ciphered data.
     * @param outputPath The path to the output file where the deciphered data will be written.
     * @param keyPath    The path to the file containing the secret key used for decryption.
     * @throws Exception If an error occurs during the decryption process.
     */
    private static void unprotect(String inputPath, String outputPath, String keyPath) throws Exception {
        Key key = readSecretKey(keyPath);
        JsonObject root = readJsonFile(inputPath);
        writeJsonFile(outputPath, JsonProtector.unprotect(root, key));
    }

    /**
     * Creates a JSON object encapsulating the metadata associated the
     * encryption algorithm.
     *
     * @param iv The initialization vector used for encryption.
     * @return The metadata JSON object.
     */
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

    /**
     * Creates a Message Integrity Code (MIC) String representation for the given data.
     *
     * @param data The data to be protected.
     * @param key  The secret key used for encryption.
     * @return The String representation of the MIC.
     * @throws Exception If an error occurs during the MIC computation.
     */
    private static String createMIC(JsonObject data, JsonObject metadata, Key key) throws Exception {
        String allData = data.toString() + metadata.toString();

        byte[] plainBytes = allData.getBytes();
        byte[] macBytes = makeMAC(plainBytes, key);

        return Base64.getEncoder().encodeToString(macBytes);
    }

    /**
     * Reads the content of a file and returns it as a byte array.
     *
     * @param path the path of the file to be read
     * @return the content of the file as a byte array
     * @throws FileNotFoundException if the file specified by the path does not exist
     * @throws IOException           if an I/O error occurs while reading the file
     */
    private static byte[] readFile(String path) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(path);

        byte[] content = new byte[fis.available()];
        fis.read(content);

        fis.close();

        return content;
    }

    /**
     * Reads a JSON file from the specified path and returns the corresponding
     * JsonObject.
     *
     * @param path the path of the JSON file to be read
     * @return the JsonObject read from the file
     * @throws FileNotFoundException if the file is not found
     * @throws IOException           if an I/O error occurs while reading the file
     */
    public static JsonObject readJsonFile(String path) throws FileNotFoundException, IOException {
        Gson gson = new Gson();
        return gson.fromJson(new String(readFile(path)), JsonObject.class);
    }

    /**
     * Reads a secret AES key from the specified file path.
     *
     * @param secretKeyPath the path to the secret key file
     * @return the secret key as a Key object
     * @throws Exception if an error occurs while reading the secret key
     */
    public static Key readSecretKey(String secretKeyPath) throws Exception {
        byte[] encoded = readFile(secretKeyPath);
        SecretKeySpec keySpec = new SecretKeySpec(encoded, "AES");
        return keySpec;
    }

    /**
     * Writes a JSON object to a file.
     *
     * @param path The path of the file to write to.
     * @param json The JSON object to write.
     * @throws FileNotFoundException If the file specified by the path cannot be found.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public static void writeJsonFile(String path, JsonObject json) throws FileNotFoundException, IOException {
        try (FileWriter fileWriter = new FileWriter(path)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(json, fileWriter);
        }
    }

    /**
     * Generates a random initialization vector (IV) of 16 bytes.
     *
     * @return the generated IV
     */
    public static byte[] generateIV() {
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }

    /**
     * Ciphers the given byte array using the specified key and initialization vector (IV).
     *
     * @param bytes the byte array to be ciphered
     * @param key the encryption key
     * @param iv the initialization vector (IV)
     * @return the ciphered byte array
     *
     * @throws IllegalBlockSizeException if the block size is invalid
     * @throws BadPaddingException if the padding is invalid
     * @throws NoSuchAlgorithmException if the specified algorithm is not available
     * @throws NoSuchPaddingException if the specified padding scheme is not available
     * @throws InvalidKeyException if the encryption key is invalid
     * @throws InvalidAlgorithmParameterException if the initialization vector (IV) is invalid
     */
    public static byte[] cipher(byte[] bytes, Key key, byte[] iv) throws IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(CIPHER);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        return cipher.doFinal(bytes);
    }

    /**
     * Deciphers the given byte array using the specified key and initialization vector (IV).
     *
     * @param bytes the byte array to be decrypted
     * @param key the key used for decryption
     * @param iv the initialization vector (IV) used for decryption
     * @return the decrypted byte array
     *
     * @throws IllegalBlockSizeException if the block size is invalid
     * @throws BadPaddingException if the padding is invalid
     * @throws NoSuchAlgorithmException if the specified algorithm is not available
     * @throws NoSuchPaddingException if the specified padding scheme is not available
     * @throws InvalidKeyException if the specified key is invalid
     * @throws InvalidAlgorithmParameterException if the specified algorithm parameters are invalid
     */
    public static byte[] decipher(byte[] bytes, Key key, byte[] iv)
            throws IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(CIPHER);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        return cipher.doFinal(bytes);
    }


    /**
     * Computes the Message Integrity Code (MIC) for the given data using the specified key.
     *
     * @param plainBytes the data to be protected
     * @param key the secret key used for protection
     * @return the MIC as a byte array
     * @throws Exception if an error occurs during the MIC computation
     */
    public static byte[] makeMAC(byte[] plainBytes, Key key) throws Exception {
        Mac mac = Mac.getInstance(MAC_ALGO);
        mac.init(key);
        byte[] recomputedMACBytes = mac.doFinal(plainBytes);
        return recomputedMACBytes;
    }
}

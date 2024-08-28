package myproject;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java -jar <jarfile> <PRN> <JSON File Path>");
            return;
        }

        String prn = args[0].toLowerCase().replaceAll("\\s", "");
        String jsonFilePath = args[1];

        // Parse JSON file
        JsonElement jsonElement = JsonParser.parseReader(new FileReader(jsonFilePath));
        String destinationValue = findDestination(jsonElement);

        if (destinationValue == null) {
            System.out.println("No 'destination' key found in the JSON file.");
            return;
        }

        // Generate random string
        String randomString = generateRandomString(8);

        // Concatenate PRN, destination value, and random string
        String concatenatedValue = prn + destinationValue + randomString;

        // Generate MD5 hash
        String md5Hash = generateMD5Hash(concatenatedValue);

        // Output the result
        System.out.println(md5Hash + ";" + randomString);
    }

    private static String findDestination(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("destination")) {
                return jsonObject.get("destination").getAsString();
            }
            for (String key : jsonObject.keySet()) {
                String value = findDestination(jsonObject.get(key));
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashInBytes = md.digest(value.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

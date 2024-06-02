package dev.plytki.upload;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class PterodactylUpload {

    private final String apiUrl;
    private final String apiKey;

    public PterodactylUpload(String apiUrl, String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public void uploadFile(String signedUrl, String filePath) {
        String boundary = UUID.randomUUID().toString();
        try {
            File file = new File(filePath);
            URL url = new URL(signedUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            OutputStream outputStream = connection.getOutputStream();
            DataOutputStream writer = new DataOutputStream(outputStream);

            // Write file content
            writer.writeBytes("--" + boundary + "\r\n");
            writer.writeBytes("Content-Disposition: form-data; name=\"files\"; filename=\"" + file.getName() + "\"\r\n");
            writer.writeBytes("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName()) + "\r\n");
            writer.writeBytes("\r\n");

            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                writer.write(buffer, 0, bytesRead);
            }
            inputStream.close();

            writer.writeBytes("\r\n");
            writer.writeBytes("--" + boundary + "--\r\n");
            writer.flush();
            writer.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("File uploaded successfully.");
            } else {
                System.out.println("File upload failed: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSignedURL() {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                String responseStr = response.toString();
                JsonElement element = JsonParser.parseString(responseStr);
                return element.getAsJsonObject().getAsJsonObject("attributes").get("url").getAsString();
            } else {
                System.out.println("GET request failed: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

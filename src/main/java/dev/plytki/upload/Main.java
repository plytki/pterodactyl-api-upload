package dev.plytki.upload;

public class Main {

    private static final String API_URL = "https://pterodactyl.file.properties/api/client/servers/a37918f5/files/upload";
    private static final String API_KEY = "ptlc_..."; // Replace with your API key

    public static void main(String[] args) {
        PterodactylUpload pterodactyl = new PterodactylUpload(API_URL, API_KEY);
        String signedUrl = pterodactyl.getSignedURL();
        if (signedUrl != null) {
            String filePath = "path/to/file";
            pterodactyl.uploadFile(signedUrl, filePath);
        } else {
            System.out.println("Failed to get the signed URL.");
        }
    }

}

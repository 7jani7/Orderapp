package com.app.order.bean;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;

public class FtpUploadFile {

    public static void main(String[] args) {

        String server = "localhost";
        int port = 21;
        String user = "admin";
        String pass = "admin";
        String filename = "responseFile.csv";

        FTPClient ftpClient = new FTPClient();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classLoader.getResourceAsStream(filename)) {
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);

            ftpClient.storeFile(filename, is);
            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
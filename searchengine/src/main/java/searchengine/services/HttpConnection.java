package searchengine.services;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import searchengine.config.ConnectionConfig;

import java.io.IOException;

public class HttpConnection {
    private ConnectionConfig connectionConfig;

    public  HttpConnection(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    public  Connection getConnection(String url) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Jsoup.connect(url)
                .userAgent(connectionConfig.getUserAgent())
                .referrer(connectionConfig.getReferrer())
                .ignoreHttpErrors(true);
    }
    public  boolean isExistsConnection(String url){
        Connection connection = getConnection(url);
        try {
            int statusCode = connection.execute().statusCode();
            if(statusCode == 200){
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

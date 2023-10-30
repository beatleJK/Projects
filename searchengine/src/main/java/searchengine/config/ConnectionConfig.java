package searchengine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "connection-settings")
public class ConnectionConfig {
    private int timeout;
    private String userAgent;
    private String referrer;

    @Override
    public String toString() {
        return "ConnectionConfig{" +
                "timeout=" + timeout +
                ", userAgent='" + userAgent + '\'' +
                ", referrer='" + referrer + '\'' +
                '}';
    }
}

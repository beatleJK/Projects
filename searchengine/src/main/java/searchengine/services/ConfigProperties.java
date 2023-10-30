package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import searchengine.config.SitesList;

@Configuration
@RequiredArgsConstructor
public class ConfigProperties {
    private final SitesList sitesList;
    public SitesList getSitesList() {
    return sitesList;
    }

}
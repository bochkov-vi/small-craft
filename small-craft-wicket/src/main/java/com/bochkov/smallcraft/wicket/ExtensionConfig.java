package com.bochkov.smallcraft.wicket;

import com.bochkov.wicket.component.select2.Select2ApplicationExtension;
import com.giffing.wicket.spring.boot.context.extensions.ApplicationInitExtension;
import com.giffing.wicket.spring.boot.context.extensions.WicketApplicationInitConfiguration;
import com.giffing.wicket.spring.boot.starter.configuration.extensions.external.webjars.WebjarsConfig;
import com.giffing.wicket.spring.boot.starter.configuration.extensions.external.webjars.WebjarsProperties;
import de.agilecoders.wicket.webjars.WicketWebjars;
import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import org.apache.wicket.protocol.http.WebApplication;

@ApplicationInitExtension
public class ExtensionConfig implements WicketApplicationInitConfiguration {

    @Override
    public void init(WebApplication webApplication) {
        WebjarsSettings settings = new WebjarsSettings();

        WicketWebjars.install(webApplication, settings);
        Select2ApplicationExtension.install(webApplication);
    }
}

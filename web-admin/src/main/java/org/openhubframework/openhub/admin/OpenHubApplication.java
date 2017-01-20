/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openhubframework.openhub.admin;

import static org.openhubframework.openhub.api.route.RouteConstants.WEB_URI_PREFIX;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import net.bull.javamelody.MonitoringFilter;
import net.bull.javamelody.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.ErrorPageFilter;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import org.openhubframework.openhub.admin.web.config.CamelRoutesConfig;
import org.openhubframework.openhub.admin.web.config.MvcConfig;
import org.openhubframework.openhub.admin.web.config.WebSecurityConfig;
import org.openhubframework.openhub.admin.web.filter.RequestResponseLoggingFilter;
import org.openhubframework.openhub.api.exception.ErrorExtEnum;
import org.openhubframework.openhub.api.route.CamelConfiguration;
import org.openhubframework.openhub.common.AutoConfiguration;
import org.openhubframework.openhub.common.log.LogContextFilter;
import org.openhubframework.openhub.core.config.CamelConfig;
import org.openhubframework.openhub.core.config.JpaConfig;
import org.openhubframework.openhub.core.config.WebServiceConfig;
import org.openhubframework.openhub.modules.ErrorEnum;


/**
 * OpenHub application configuration.
 * <p/>
 * This class configures root Spring context. One child context for Spring MVC is created.
 *
 * @author Petr Juza
 * @since 2.0
 * @see CamelRoutesConfig
 * @see MvcConfig
 * @see WebSecurityConfig
 * @see CamelConfig
 * @see WebServiceConfig
 * @see JpaConfig
 */
@EnableAutoConfiguration
@EnableConfigurationProperties
// note: all routes with @CamelConfiguration are configured in CamelRoutesConfig
@ComponentScan(basePackages = {"org.openhubframework.openhub.common",
        "org.openhubframework.openhub.core",
        "org.openhubframework.openhub.modules",
        "org.openhubframework.openhub.admin"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = AutoConfiguration.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = CamelConfiguration.class)})
@Configuration
@ImportResource({"classpath:net/bull/javamelody/monitoring-spring.xml", "classpath:sp_h2_server.xml", "classpath:sp_camelContext.xml"})
@PropertySource(value = {"classpath:/extensions.cfg"})
public class OpenHubApplication extends SpringBootServletInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(OpenHubApplication.class);

    private static final String JAVAMELODY_URL = "/monitoring/javamelody";

    /**
     * Registers {@link RequestResponseLoggingFilter}.
     */
    @Bean
    public FilterRegistrationBean loggingRest() {
        LOG.info("REQ/RES logging initialization");
        
        RequestResponseLoggingFilter filter = new RequestResponseLoggingFilter();
        filter.setLogUnsupportedContentType(true);

        return new FilterRegistrationBean(filter);
    }

    /**
     * Registers URL prefix.
     */
    @Bean
    public ServletRegistrationBean dispatcherWebRegistration(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet);
        registration.addUrlMappings(WEB_URI_PREFIX + "*");
        return registration;
    }

    /**
     * Creates JavaMelody filter.
     */
    @Bean
   	public FilterRegistrationBean monitoringJavaMelody() {
        LOG.info("JavaMelody initialization: " + JAVAMELODY_URL);

   		FilterRegistrationBean registration = new FilterRegistrationBean(new MonitoringFilter());
        Map<String, String> initParams = new HashMap<>();
        initParams.put("monitoring-path", JAVAMELODY_URL);
        initParams.put("disabled", "true");
   		registration.setInitParameters(initParams);

   		return registration;
   	}

    /**
     * Sets up filter for adding context information to logging.
     */
    @Bean
    public Filter logContextFilter() {
        return new LogContextFilter();
    }

    /**
     * Defines localized messages for admin GUI.
     */
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }

    /**
     * Defines error codes catalogue.
     */
    @Bean
    public Map<String, ErrorExtEnum[]> errorCodesCatalog() {
          Map<String, ErrorExtEnum[]> map = new HashMap<>();
          map.put("core", ErrorEnum.values());
          return map;
      }

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        // add session listener for JavaMelody
        container.addListener(SessionListener.class);

        super.onStartup(container);
    }

    // ----------------------------------------------
    // reason of this code snippet: http://stackoverflow.com/questions/30170586/how-to-disable-errorpagefilter-in-spring-boot

    @Bean
    public ErrorPageFilter errorPageFilter() {
        return new ErrorPageFilter();
    }

    @Bean
    public FilterRegistrationBean disableSpringBootErrorFilter(ErrorPageFilter filter) {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }
    // ----------------------------------------------

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder(OpenHubApplication.class)
                .run(args);
    }
}

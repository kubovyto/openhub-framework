package org.openhubframework.openhub.modules.in.city;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.openhubframework.openhub.modules.in.city.OneWordCityRoute.GLOBAL_WEATHER_SERVICE_UNMARSHALL_ID;
import static org.openhubframework.openhub.modules.in.city.OneWordCityRoute.GLOBAL_WEATHER_WEATHER_SERVICE_ID;
import static org.openhubframework.openhub.modules.in.city.OneWordCityRoute.ROUTE_GLOBAL_WEATHER_SERVICE_ID;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.restlet.RestletComponent;
import org.apache.camel.spi.RestConsumerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openhubframework.openhub.modules.AbstractExampleModuleTest;
import org.openhubframework.openhub.modules.in.city.domain.CitiesDto;
import org.openhubframework.openhub.modules.out.globalweather.ws.GetCitiesByCountryResponse;
import org.openhubframework.openhub.test.route.ActiveRoutes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Tomáš Kubový <a href="mailto:tomas.kubovy@bsc-ideas.com">tomas.kubovy@bsc-ideas.com</a>
 */
@ContextConfiguration(classes = OneWordCityRouteTest.Context.class)
@ActiveRoutes(classes = OneWordCityRoute.class)
public class OneWordCityRouteTest extends AbstractExampleModuleTest {

    @Produce(uri = "direct:globalWeatherService")
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:test")
    private MockEndpoint mock;

    @Test
    public void testRouteIn_responseOK() throws Exception {
        final String soapResponse =
                "<NewDataSet>" +
                        "<Table>\n" +
                        "\t<Country>Germany</Country>\n" +
                        "\t<City>Berlin Schoenefeld</City>\n" +
                        "</Table>\n" +
                        "<Table>\n" +
                        "\t<Country>Germany</Country>\n" +
                        "\t<City>Dresden Klotzsche</City>\n" +
                        "</Table>\n" +
                        "<Table>\n" +
                        "\t<Country>Germany</Country>\n" +
                        "\t<City>Erfurt Bindersleben</City>\n" +
                        "</Table>\n" +
                        "<Table>\n" +
                        "\t<Country>Germany</Country>\n" +
                        "\t<City>Frankfurt</City>\n" +
                        "</Table>\n" +
                        "<Table>\n" +
                        "\t<Country>Germany</Country>\n" +
                        "\t<City>Hamburg</City>\n" +
                        "</Table>\n" +
                        "</NewDataSet>";


        getCamelContext().getRouteDefinition(ROUTE_GLOBAL_WEATHER_SERVICE_ID)
                .adviceWith(getCamelContext(), new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        this.weaveById(GLOBAL_WEATHER_WEATHER_SERVICE_ID).replace().process(new Processor() {
                            @Override
                            public void process(final Exchange exchange) throws Exception {
                                //do not call soap
                                exchange.getIn().setBody("");
                            }
                        });

                        this.weaveById(GLOBAL_WEATHER_SERVICE_UNMARSHALL_ID).replace().process(new Processor() {
                            @Override
                            public void process(final Exchange exchange) throws Exception {
                                GetCitiesByCountryResponse response = new GetCitiesByCountryResponse();
                                response.setGetCitiesByCountryResult(soapResponse);
                                exchange.getIn().setBody(response);
                            }
                        });

                        weaveAddLast().to("mock:test");
                    }
                });

        CitiesDto response = (CitiesDto) producer.requestBodyAndHeader("-", "name", "Germany");
        Assert.assertThat(response.getCityNames().size(), is(2));
        Assert.assertThat(response.getCityNames(), hasItems("Frankfurt", "Hamburg"));
    }

    @Configuration
    public static class Context {
        @Bean
        public RestConsumerFactory restConsumerFactory() {
            return new RestletComponent();
        }
    }
}

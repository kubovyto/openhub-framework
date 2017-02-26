/*
 * Copyright (c) 2017 BSC Praha, spol. s r.o.
 */

package org.openhubframework.openhub.modules.in.city;

import static org.openhubframework.openhub.api.common.jaxb.JaxbDataFormatHelper.jaxb;

import java.util.List;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.openhubframework.openhub.api.route.AbstractBasicRoute;
import org.openhubframework.openhub.api.route.CamelConfiguration;
import org.openhubframework.openhub.modules.ExampleProperties;
import org.openhubframework.openhub.modules.in.city.domain.CitiesDto;
import org.openhubframework.openhub.modules.in.city.domain.ErrorDto;
import org.openhubframework.openhub.modules.out.globalweather.ws.GetCitiesByCountry;
import org.openhubframework.openhub.modules.out.globalweather.ws.GetCitiesByCountryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.ws.WebServiceException;

/**
 * @author Tomáš Kubový <a href="mailto:tomas.kubovy@bsc-ideas.com">tomas.kubovy@bsc-ideas.com</a>
 */
@Profile(ExampleProperties.EXAMPLE_PROFILE)
@CamelConfiguration(value = OneWordCityRoute.ROUTE_BEAN)
public class OneWordCityRoute extends AbstractBasicRoute {

    static final String ROUTE_BEAN = "oneWordCityRouteBean";

    public static final String ROUTE_GLOBAL_WEATHER_SERVICE_ID = "globalWeatherServiceId";
    public static final String GLOBAL_WEATHER_WEATHER_SERVICE_ID = "globalWeatherServiceId";
    public static final String GLOBAL_WEATHER_SERVICE_UNMARSHALL_ID = "globalWeatherServiceUnmarshallId";

    @Autowired
    @Qualifier("arrayListAggregationFilterStrategy")
    private AggregationStrategy aggregationStrategy;

    @Override
    protected void doConfigure() throws Exception {

        // Enable Jackson JSON type converter.
        getContext().getProperties().put("CamelJacksonEnableTypeConverter", "true");

        // Allow Jackson JSON to convert to pojo types also (by default Jackson only converts to String and other simple types).
        getContext().getProperties().put("CamelJacksonTypeConverterToPojo", "true");

        onException(WebServiceException.class)
                .handled(true)
                .transform().method(this, "toException");

        rest("/cityNames")
                .get()
                .consumes("application/json")
                .produces("application/json")
                .to("direct:globalWeatherService");

        from("direct:globalWeatherService")
                .routeId(ROUTE_GLOBAL_WEATHER_SERVICE_ID)
                .transform().method(this, "toGetCitiesByCountry")
                .marshal(jaxb(GetCitiesByCountry.class))
                .to("spring-ws:http://www.webservicex.com/globalweather.asmx?soapAction=http://www.webserviceX.NET/GetCitiesByCountry")
                .id(GLOBAL_WEATHER_WEATHER_SERVICE_ID)
                .unmarshal(jaxb(GetCitiesByCountryResponse.class))
                .id(GLOBAL_WEATHER_SERVICE_UNMARSHALL_ID)

                .transform(bodyAs(GetCitiesByCountryResponse.class).method("getCitiesByCountryResult"))

                .log(LoggingLevel.INFO, "CitiesByCountryResponse: ${body}")
                .split(xpath("//City/text()"), aggregationStrategy)
                .process(new Processor() {
                    @Override
                    public void process(final Exchange exchange) throws Exception {
                        //do nothing (required node for split)
                    }
                })
                .end()
                .to("direct:process");


        from("direct:process")
                .choice()
                .when(header("tofile").isEqualTo(true))
                .to("direct:saveToFileAndRest")
                .otherwise()
                .to("direct:toRest")
                .end();

        from("direct:saveToFileAndRest")
                .multicast()
                .to("direct:saveToFile",
                        "direct:toRest");

        from("direct:saveToFile")
                .transform().method(this, "toFileFormat")
                .to("file://output?fileName=citynames.txt");

        from("direct:toRest")
                .transform().method(this, "toCities");

        from("direct:handleException")
                .to("log:handleException")
                .transform().method(this, "toException");
    }


    public static boolean isOneWordCity(@Body final String cityName) {
        if (StringUtils.isEmpty(cityName)) {
            return false;
        }

        boolean oneWord = cityName.trim().replaceAll("\\s+", " ").indexOf(" ") < 0;
        return oneWord;
    }

    public GetCitiesByCountry toGetCitiesByCountry(@Header("name") final String countryName) {
        GetCitiesByCountry result = new GetCitiesByCountry();
        result.setCountryName(countryName);
        return result;
    }

    public CitiesDto toCities(@Body final List<String> cityNames) {
        CitiesDto result = new CitiesDto();
        result.setCityNames(cityNames);
        return result;
    }

    public String toFileFormat(final List<String> cities) {
        StringBuilder b = new StringBuilder();
        for (String city : cities) {
            b.append(String.format("%s%n", city));
        }
        return b.toString();
    }

    public ErrorDto toException(@Body final Exception exception) {
        ErrorDto result = new ErrorDto();
        result.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        result.setMessage(exception.getMessage());
        return result;
    }


}

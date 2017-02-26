/*
 * Copyright (c) 2017 BSC Praha, spol. s r.o.
 */

package org.openhubframework.openhub.modules.in.city.config;

import static org.openhubframework.openhub.modules.in.city.OneWordCityRoute.isOneWordCity;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.openhubframework.openhub.modules.ExampleProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Tomáš Kubový <a href="mailto:tomas.kubovy@bsc-ideas.com">tomas.kubovy@bsc-ideas.com</a>
 */
@Configuration
@Profile(ExampleProperties.EXAMPLE_PROFILE)
public class MyRouteConfig {

    @Bean
    @Qualifier("arrayListAggregationFilterStrategy")
    public ArrayListAggregationFilterStrategy aggregationStrategy() {
        return new ArrayListAggregationFilterStrategy();
    }


    public static class ArrayListAggregationFilterStrategy implements AggregationStrategy {

        public ArrayListAggregationFilterStrategy() {
            super();
        }

        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            Message newIn = newExchange.getIn();
            String newBody = newIn.getBody(String.class);
            ArrayList list = null;
            if (oldExchange == null) {
                list = new ArrayList();
                tryAddToList(list, newBody);
                newIn.setBody(list);
                return newExchange;
            } else {
                Message in = oldExchange.getIn();
                list = in.getBody(ArrayList.class);
                tryAddToList(list, newBody);
                return oldExchange;
            }
        }

        private void tryAddToList(final List list, final String item) {
            if (isOneWordCity(item)) {
                list.add(item);
            }
        }
    }
}

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

package org.openhubframework.openhub.core.common.converters;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.joda.time.Duration;
import org.joda.time.Seconds;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import org.openhubframework.openhub.core.config.ConverterAutoConfiguration;


/**
 * Test that all converters is registered and working properly.
 *
 * @author Petr Juza
 * @since 2.0
 */
@RunWith(Parameterized.class)
@ContextConfiguration
public class ConvertersTest {

    @ClassRule
    public static final SpringClassRule SCR = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private ConfigurableEnvironment env;

    private final Object from;
    private final Class<?> targetClass;
    private final Object expected;

    /**
     * Create parametrized classes.
     *
     * @param from object to convert
     * @param targetClass target class to convert into
     * @param expected expected converted object.
     */
    public ConvertersTest(Object from, Class<?> targetClass, Object expected) {
        this.from = from;
        this.targetClass = targetClass;
        this.expected = expected;
    }

    /**
     * Data for parametrized test.
     *
     * @return the data for constructor
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "105", Seconds.class, Seconds.seconds(105) },
                { "PT107S", Seconds.class, Seconds.seconds(107) },
                { 108, Seconds.class, Seconds.seconds(108) },
                { "1090", Duration.class, Duration.millis(1090) },
                { 1099L, Duration.class, Duration.millis(1099) },
                { "PT72.345S", Duration.class, Duration.millis(72345) },

                { Seconds.seconds(123), Integer.class, 123 },
                { Seconds.seconds(177), String.class, "177" },
                { Duration.millis(1099), Long.class, 1099L },
                { Duration.millis(45230), String.class, "45230" }
        });
    }

    @Test
    public void testConvert() {
        Object res = env.getConversionService().convert(from, targetClass);
        assertEquals(expected, res);
    }

    @Configuration
    @Import(ConverterAutoConfiguration.class)
    static class TestConfiguration {

        @Bean
        public ConversionService conversionService() {
            return new DefaultConversionService();
        }
    }
}

/*
 * Copyright (c) 2017 BSC Praha, spol. s r.o.
 */

package org.openhubframework.openhub.modules.in.city.domain;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Tomáš Kubový <a href="mailto:tomas.kubovy@bsc-ideas.com">tomas.kubovy@bsc-ideas.com</a>
 */
public class CitiesDto {

    private List<String> cityNames;

    public List<String> getCityNames() {
        return cityNames;
    }

    public void setCityNames(final List<String> cityNames) {
        this.cityNames = cityNames;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("cityNames", cityNames)
                .toString();
    }
}

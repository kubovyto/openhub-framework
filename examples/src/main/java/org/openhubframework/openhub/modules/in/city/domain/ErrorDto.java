/*
 * Copyright (c) 2017 BSC Praha, spol. s r.o.
 */

package org.openhubframework.openhub.modules.in.city.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Tomáš Kubový <a href="mailto:tomas.kubovy@bsc-ideas.com">tomas.kubovy@bsc-ideas.com</a>
 */
public class ErrorDto {
    private String message;
    private int status;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public ErrorDto withMessage(final String message) {
        this.message = message;
        return this;
    }

    public ErrorDto withStatus(final int status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("message", message)
                .append("status", status)
                .toString();
    }
}


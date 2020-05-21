package org.basecode.common.logger.stacktrace;

import java.io.Serializable;

public class Stack implements Serializable {
    private String stack;

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }
}

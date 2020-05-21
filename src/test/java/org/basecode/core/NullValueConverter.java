package org.basecode.core;

import org.apache.commons.beanutils.Converter;

public class NullValueConverter implements Converter {
    @Override
    public <T> T convert(Class<T> aClass, Object o) {
        return null;
    }
}

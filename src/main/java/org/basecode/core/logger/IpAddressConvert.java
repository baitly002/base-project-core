package org.basecode.core.logger;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.basecode.core.util.LocalIpAddressUtil;

public class IpAddressConvert extends ClassicConverter {

    private static final String ip= LocalIpAddressUtil.resolveLocalIps().toString();
    @Override
    public String convert(ILoggingEvent event) {
        return ip;
    }
}

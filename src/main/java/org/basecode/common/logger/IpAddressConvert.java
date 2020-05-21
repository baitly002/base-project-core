package org.basecode.common.logger;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.basecode.common.util.LocalIpAddressUtil;

public class IpAddressConvert extends ClassicConverter {

    private static final String ip= LocalIpAddressUtil.resolveLocalIps().toString();
    @Override
    public String convert(ILoggingEvent event) {
        return ip;
    }
}

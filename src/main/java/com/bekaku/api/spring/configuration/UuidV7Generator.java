package com.bekaku.api.spring.configuration;

import com.bekaku.api.spring.util.UuidUtils;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

public class UuidV7Generator extends SequenceStyleGenerator {
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        return UuidUtils.generateUUID(); // UUIDv7 from library
    }
}

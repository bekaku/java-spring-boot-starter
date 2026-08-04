package com.bekaku.api.spring.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

public class LongListToStringSerializer extends JsonSerializer<List<Long>> {
    @Override
    public void serialize(List<Long> values, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        if (values != null) {
            for (Long value : values) {
                if (value != null) {
                    gen.writeString(value.toString()); // บังคับเขียนเป็น String
                } else {
                    gen.writeNull();
                }
            }
        }
        gen.writeEndArray();
    }
}

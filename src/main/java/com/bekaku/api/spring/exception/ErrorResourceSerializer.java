package com.bekaku.api.spring.exception;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorResourceSerializer extends ValueSerializer<ErrorResource> {
    @Override
    public void serialize(ErrorResource value, JsonGenerator gen, SerializationContext serializers)  {
        Map<String, List<String>> json = new HashMap<>();
        gen.writeStartObject();
        gen.writeName("errors");
        for (FieldErrorResource fieldErrorResource : value.getFieldErrors()) {
            if (!json.containsKey(fieldErrorResource.getField())) {
                json.put(fieldErrorResource.getField(), new ArrayList<String>());
            }
            json.get(fieldErrorResource.getField()).add(fieldErrorResource.getMessage());
        }
        for (Map.Entry<String, List<String>> pair : json.entrySet()) {
            gen.writeArrayPropertyStart(pair.getKey());
            pair.getValue().forEach(gen::writeString);
            gen.writeEndArray();
        }
        gen.writeEndObject();
        gen.writeEndObject();
    }
}
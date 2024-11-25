package com.fision.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
/**
 * @author LordDev
 */
public class CustomDoubleSerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else if (value % 1 == 0) {
            gen.writeNumber(value.longValue()); // Jika tidak ada angka di belakang koma
        } else {
            gen.writeNumber(value); // Jika ada angka di belakang koma
        }
    }
}

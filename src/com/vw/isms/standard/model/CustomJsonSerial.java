package com.vw.isms.standard.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by clg on 2018/3/13.
 */
class CustomJsonSerial extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        Field[] fields = value.getClass().getDeclaredFields();
        for(Field f:fields){
            f.setAccessible(true);
            try {
                Object v = f.get(value);
                if(v==null){
                    jgen.writeStringField(f.getName(),"");
                }
                else if(v instanceof Long){
                    jgen.writeStringField(f.getName(), String.valueOf(v));
                }
                else if(v instanceof Integer){
                    jgen.writeNumberField(f.getName(),(Integer) v);
                }
                else{
                    jgen.writeStringField(f.getName(), String.valueOf(v));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        jgen.writeEndObject();
    }
}

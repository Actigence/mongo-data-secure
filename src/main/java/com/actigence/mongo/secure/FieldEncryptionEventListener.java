package com.actigence.mongo.secure;

import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Created by Actigence on 11/22/2015.
 */
@Component
public class FieldEncryptionEventListener extends AbstractMongoEventListener {

    private final Logger log = LoggerFactory.getLogger(FieldEncryptionEventListener.class);

    @Override
    public void onBeforeConvert(Object source) {
        for (Field field : source.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Encrypted.class)) {
                try {
                    field.setAccessible(true);
                    String value = (String) field.get(source);
                    if (!isEmpty(value)) {
                        field.set(source, FieldEncryptionUtil.encrypt(value));
                    }
                    field.setAccessible(false);
                } catch (Exception e) {
                    log.error("{} encrypting field.{}.{}", e.getClass().getName(), source.getClass().getName(), field.getName());
                    log.error(e.getMessage());
                }
            }
        }
    }

    @Override
    public void onAfterConvert(DBObject dbo, Object source) {
        for (Field field : source.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Encrypted.class)) {
                try {
                    field.setAccessible(true);
                    String value = (String) field.get(source);
                    if (!isEmpty(value)) {
                        field.set(source, FieldEncryptionUtil.decrypt(value));
                    }
                    field.setAccessible(false);
                } catch (Exception e) {
                    log.error("{} decrypting field.{}.{}", e.getClass().getName(), source.getClass().getName(), field.getName());
                    log.error(e.getMessage());
                }
            }
        }
    }

    private boolean isEmpty(String value) {
        return (value == null || value.trim().equals(""));
    }
}


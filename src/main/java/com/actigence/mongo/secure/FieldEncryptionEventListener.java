package com.actigence.mongo.secure;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

/**
 * Created by Actigence on 11/22/2015.
 */
@Component
public class FieldEncryptionEventListener extends AbstractMongoEventListener {
}

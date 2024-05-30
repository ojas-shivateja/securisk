package com.insure.rfq.utils;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class CustomSequenceGenerator implements IdentifierGenerator {

	@Override
	public Serializable  generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		// Implement your custom logic to generate the unique identifier here
        // For example, you can generate a unique ID based on a custom algorithm or using an external service

        String customSequence = "CUSTOM_SEQUENCE";
		return customSequence;
	}

}

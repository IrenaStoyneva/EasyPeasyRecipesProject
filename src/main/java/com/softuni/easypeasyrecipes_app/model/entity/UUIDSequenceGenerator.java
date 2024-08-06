package com.softuni.easypeasyrecipes_app.model.entity;

import jakarta.persistence.SequenceGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.util.EnumSet;
import java.util.UUID;

public class UUIDSequenceGenerator implements BeforeExecutionGenerator {
    @Override
    public Object generate(
            SharedSessionContractImplementor sharedSessionContractImplementor,
            Object owner,
            Object currentValue,
            EventType eventType) {
        return UUID.randomUUID();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }

    @Override
    public boolean generatedOnExecution() {
        return BeforeExecutionGenerator.super.generatedOnExecution();
    }

    @Override
    public boolean generatedOnExecution(Object entity, SharedSessionContractImplementor session) {
        return BeforeExecutionGenerator.super.generatedOnExecution(entity, session);
    }

    @Override
    public boolean allowAssignedIdentifiers() {
        return BeforeExecutionGenerator.super.allowAssignedIdentifiers();
    }

    @Override
    public boolean generatesSometimes() {
        return BeforeExecutionGenerator.super.generatesSometimes();
    }

    @Override
    public boolean generatesOnInsert() {
        return BeforeExecutionGenerator.super.generatesOnInsert();
    }

    @Override
    public boolean generatesOnUpdate() {
        return BeforeExecutionGenerator.super.generatesOnUpdate();
    }
}

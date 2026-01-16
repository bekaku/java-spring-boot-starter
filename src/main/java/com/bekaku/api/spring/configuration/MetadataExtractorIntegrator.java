package com.bekaku.api.spring.configuration;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.spi.BootstrapContext; // New Import Required
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class MetadataExtractorIntegrator
        implements org.hibernate.integrator.spi.Integrator {

    public static final MetadataExtractorIntegrator INSTANCE =
            new MetadataExtractorIntegrator();

    private Database database;

    private Metadata metadata;

    public Database getDatabase() {
        return database;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    // --- UPDATED METHOD ---
    // Hibernate 6 changed the signature of this method.
    // 1. Second parameter is now BootstrapContext (was SessionFactoryImplementor)
    // 2. Third parameter is now SessionFactoryImplementor (was SessionFactoryServiceRegistry)
    @Override
    public void integrate(
            Metadata metadata,
            BootstrapContext bootstrapContext,
            SessionFactoryImplementor sessionFactory) {

        this.database = metadata.getDatabase();
        this.metadata = metadata;
    }

    @Override
    public void disintegrate(
            SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {
        // This signature remains the same in Hibernate 6
    }
}
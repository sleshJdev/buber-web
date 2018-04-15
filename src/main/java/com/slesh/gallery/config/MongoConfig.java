package com.slesh.gallery.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.Collections;

@EnableMongoRepositories(basePackages = "com.slesh.gallery.persistence.repository")
public class MongoConfig extends AbstractMongoConfiguration {

    private final String dbName;
    private final String host;
    private final int port;

    public MongoConfig(@Value("${db.name}") String dbName,
                       @Value("${db.host}") String host,
                       @Value("${db.port}") int port) {
        this.dbName = dbName;
        this.host = host;
        this.port = port;
    }

    @Override
    protected String getDatabaseName() {
        return dbName;
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(host, port);
    }

    @Override
    protected Collection<String> getMappingBasePackages() {
        return Collections.singleton("com.slesh.gallery.persistence.model");
    }
}

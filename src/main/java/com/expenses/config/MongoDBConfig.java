package com.expenses.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;

@Configuration
public class MongoDBConfig {

    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://expenseApp:expensePass@localhost:27017/?w=majority&authSource=expenseDb");
    }

    @Bean
    public MongoTemplate mongoDbConfig() throws IOException {
        MongoTemplate mongoTemplate = new MongoTemplate(
                mongoClient(), "expenseDb");
        return mongoTemplate;
    }
}

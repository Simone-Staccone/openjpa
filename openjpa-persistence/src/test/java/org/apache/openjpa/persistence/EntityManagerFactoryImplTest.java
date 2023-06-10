package org.apache.openjpa.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


class EntityManagerFactoryImplTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getBroker() {
        //EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pv_2");
        //EntityManagerFactoryImpl entityManagerFactory = new EntityManagerFactoryImpl();

        Assertions.assertTrue(true);
    }

    @Test
    void getEntityManagerFactory() {
    }

    @Test
    void pushFetchPlan() {
    }
}

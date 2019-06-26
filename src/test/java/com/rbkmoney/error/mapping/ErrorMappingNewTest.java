package com.rbkmoney.error.mapping;

import com.rbkmoney.damsel.domain.Failure;
import com.rbkmoney.woody.api.flow.error.WUndefinedResultException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ErrorMappingNewTest {

    private ErrorMapping errorMapping;

    @Before
    public void setUp() {
        ClassLoader current = ErrorMappingNewTest.class.getClassLoader();
        InputStream inputStream = current.getResourceAsStream("fixture/errors_new.json");

        errorMapping = new ErrorMapping(inputStream);
        errorMapping.validateMapping();
    }

    @Test(expected = ErrorMappingException.class)
    public void testMapFailureException() {
        errorMapping.mapFailure("wrong code", "wrong description");
    }

    @Test(expected = NullPointerException.class)
    public void testMapFailureNullCode() {
        errorMapping.mapFailure(null, "Invalid amount");
    }

    @Test(expected = WUndefinedResultException.class)
    public void testUndenfinedException() {
        errorMapping.mapFailure("004", "Timeout");
    }

    @Test
    public void testMapFailureCode() {
        Failure failure = errorMapping.mapFailure("001");
        assertEquals("authorization_failed", failure.getCode());
        assertEquals(failure.getReason(), "'001' - 'null'");
        assertEquals("operation_blocked", failure.getSub().getCode());
    }

    @Test
    public void testMapFailureCodeDesc(){
        Failure failure = errorMapping.mapFailure("002", "Invalid amount");
        assertNotNull(failure);
        assertEquals(failure.getReason(), "'002' - 'Invalid amount'");
        assertEquals("operation_blocked", failure.getSub().getCode());
    }

    @Test
    public void testMapFailureCodeDescState(){
        Failure failure = errorMapping.mapFailure("003", "Invalid cardholder", "payment");
        assertNotNull(failure);
        assertEquals(failure.getReason(), "'003' - 'Invalid cardholder'");
        assertEquals("operation_blocked", failure.getSub().getCode());
    }

}
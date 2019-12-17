package com.rbkmoney.error.mapping;

import com.rbkmoney.damsel.domain.Failure;
import com.rbkmoney.woody.api.flow.error.WUndefinedResultException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Slf4j
public class ErrorMappingTest {

    private ErrorMapping errorMapping;

    @Before
    public void setUp() {
        ClassLoader current = ErrorMappingTest.class.getClassLoader();
        InputStream inputStream = current.getResourceAsStream("fixture/errors.json");

        errorMapping = new ErrorMapping(inputStream);
        errorMapping.validateMappingFormat();
    }

    @Test(expected = ErrorMappingException.class)
    public void testMakeFailureByDescriptionException() {
        errorMapping.getFailureByCodeAndDescription("wrong code", "wrong description");
    }

    @Test
    public void testMakeFailureByDescriptionCodeNull() {
        Failure failure = errorMapping.getFailureByCodeAndDescription(null, "Invalid amount");

        assertEquals("authorization_failed", failure.getCode());
    }

    @Test
    public void testMakeFailureByDescriptionNullCode() {
        Failure failure = errorMapping.getFailureByCodeAndDescription("203", null);

        assertEquals("authorization_failed", failure.getCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMakeFailureByDescriptionNullCodeNull() {
        errorMapping.getFailureByCodeAndDescription(null, null);
    }

    @Test(expected = WUndefinedResultException.class)
    public void testUndefinedException() {
        errorMapping.getFailureByCodeAndDescription("008", "Timeout");
    }

    @Test
    public void testGetFailureByCodeAndDescription() {
        Map<String, String> map = new HashMap<>();
        map.put("DECLINED", "Failure(code:authorization_failed, reason:'DECLINED' - 'DECLINED', sub:SubFailure(code:unknown))");
        map.put("209", "Failure(code:authorization_failed, reason:'209' - '209', sub:SubFailure(code:unknown))");

        map.forEach((k, v) -> {
            Failure failure = errorMapping.getFailureByCodeAndDescription(k, k);
            log.info(failure.toString());
            assertEquals(v, failure.toString());
        });
    }

    @Test
    public void testGetFailureByRegexp() {
        Failure failure = errorMapping.getFailureByRegexp("AccessDeniedException");
        assertNotNull(failure);
    }

    @Test(expected = RuntimeException.class)
    public void testUnexpectedException() {
        errorMapping.getFailureByCodeAndDescription("001", "Unsupported version");
    }
}

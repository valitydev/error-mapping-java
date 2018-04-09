package com.rbkmoney.error.mapping;

import com.rbkmoney.damsel.domain.Failure;
import com.rbkmoney.woody.api.flow.error.WUndefinedResultException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ErrorMappingTest {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @Test(expected = WUndefinedResultException.class)
    public void testUndenfinedException() {
        errorMapping.getFailureByCodeAndDescription("008", "Timeout");
    }

    @Test
    public void testGetFailureByCodeAndDescription() {
        Map<String, String> map = new HashMap<>();
        map.put("DECLINED","Failure(code:authorization_failed, reason:'DECLINED' - 'DECLINED', sub:SubFailure(code:unknown))");
        map.put("209","Failure(code:authorization_failed, reason:'209' - '209', sub:SubFailure(code:unknown))");

        map.forEach((k, v) -> {
                    Failure failure = errorMapping.getFailureByCodeAndDescription(k, k);
                    logger.info(failure.toString());
                    assertEquals(v, failure.toString());
                }
        );
    }

    @Test(expected = RuntimeException.class)
    public void testUnexpectedException() {
        errorMapping.getFailureByCodeAndDescription("001","Unsupported version");
    }

}
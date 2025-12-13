package com.bloodbowlclub.auth.io.security.filters;
import com.bloodbowlclub.lib.persistance.event_store.EventStore;
import com.bloodbowlclub.lib.tests.TestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class JwtRequestFilterTest extends TestCase {

    @Autowired
    private EventStore eventStore;
    @Autowired
    private JwtRequestFilter jwtFilter;


    {}
    @Test
    @DisplayName("Check a request without requested header ")
    void testANot() {
    }

}
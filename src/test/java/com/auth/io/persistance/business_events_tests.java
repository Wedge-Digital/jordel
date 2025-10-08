package com.auth.io.persistance;

import com.auth.io.persistance.write.BusinessEventRepository;
import com.shared.domain.events.InvalidAggregateRoot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class business_events_tests {
    @Autowired
    private BusinessEventRepository eventRepo;

    @Test
    void load_and_retrieve_differents_events() throws InvalidAggregateRoot {
    }

}

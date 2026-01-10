package com.bloodbowlclub.reference.web;

import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.lib.web.ApiResponse;
import com.bloodbowlclub.reference.domain.RosterRef;
import com.bloodbowlclub.reference.service.ReferenceDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;


@SpringBootTest()
class ReferenceDataControllerTest extends TestCase {

    @Autowired
    private ReferenceDataService referenceDataService;

    @Test
    void ListAllRoster() {
        ReferenceDataController controller = new ReferenceDataController(referenceDataService);
        ResponseEntity<ApiResponse<List<RosterRef>>> result = controller.ListAllRoster();
        assertEqualsResultset(result);
    }

}
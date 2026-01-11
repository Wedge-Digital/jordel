package com.bloodbowlclub.reference.web;

import com.bloodbowlclub.lib.tests.TestCase;
import com.bloodbowlclub.lib.web.ApiResponse;
import com.bloodbowlclub.reference.domain.RosterRef;
import com.bloodbowlclub.reference.domain.SkillRef;
import com.bloodbowlclub.reference.service.ReferenceDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;


@SpringBootTest()
class ReferenceDataControllerTest extends TestCase {

    @Autowired
    @Qualifier("referenceDataServiceForAPI")
    private ReferenceDataService referenceDataService;

    @Test
    void testGetAllRosters() {
        ReferenceDataController controller = new ReferenceDataController(referenceDataService);
        ResponseEntity<ApiResponse<List<RosterRef>>> result = controller.getAllRosters();
        assertEqualsResultset(result.getBody());
    }

    @Test
    void testAllSkills() {
        ReferenceDataController controller = new ReferenceDataController(referenceDataService);
        ResponseEntity<ApiResponse<List<SkillRef>>> result = controller.getAllSkills();
        assertEqualsResultset(result);
    }

}
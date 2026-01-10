package com.bloodbowlclub.reference.web;

import com.bloodbowlclub.lib.web.ApiResponse;
import com.bloodbowlclub.reference.domain.RosterRef;
import com.bloodbowlclub.reference.service.ReferenceDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ref/v1")
public class ReferenceDataController {

    private final ReferenceDataService referenceDataService;

    public ReferenceDataController(ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    @PostMapping("/roster")
    public ResponseEntity<ApiResponse<List<RosterRef>>> ListAllRoster() {
        return ResponseEntity.ok(ApiResponse.success(referenceDataService.getAllRosters()));
    }
}
package com.bloodbowlclub.reference.web;

import com.bloodbowlclub.lib.web.ApiResponse;
import com.bloodbowlclub.reference.domain.RosterRef;
import com.bloodbowlclub.reference.domain.SkillRef;
import com.bloodbowlclub.reference.domain.StarPlayerRef;
import com.bloodbowlclub.reference.service.ReferenceDataService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/ref/v1")
public class ReferenceDataController {

    private final ReferenceDataService referenceDataService;

    public ReferenceDataController(@Qualifier("referenceDataServiceForAPI") ReferenceDataService referenceDataService) {
        this.referenceDataService = referenceDataService;
    }

    /**
     * Retourne tous les rosters dans la langue demandée via le header Accept-Language.
     * Par défaut: anglais.
     */
    @GetMapping("/rosters")
    public ResponseEntity<ApiResponse<List<RosterRef>>> getAllRosters() {
        Locale locale = LocaleContextHolder.getLocale();
        List<RosterRef> rosters = referenceDataService.getAllRosters(locale);
        return ResponseEntity.ok(ApiResponse.success(rosters));
    }

    @GetMapping("/skills")
    public ResponseEntity<ApiResponse<List<SkillRef>>> getAllSkills() {
        Locale locale = LocaleContextHolder.getLocale();
        List<SkillRef> skills = referenceDataService.getAllSkills(locale);
        return ResponseEntity.ok(ApiResponse.success(skills));
    }

    /**
     * Retourne tous les star players dans la langue demandée via le header Accept-Language.
     * Par défaut: anglais.
     */
    @GetMapping("/star-players")
    public ResponseEntity<ApiResponse<List<StarPlayerRef>>> getAllStarPlayers() {
        Locale locale = LocaleContextHolder.getLocale();
        List<StarPlayerRef> starPlayers = referenceDataService.getAllStarPlayers(locale);
        return ResponseEntity.ok(ApiResponse.success(starPlayers));
    }
}
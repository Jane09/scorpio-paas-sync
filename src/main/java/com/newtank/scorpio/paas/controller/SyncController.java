package com.newtank.scorpio.paas.controller;

import com.newtank.scorpio.paas.service.SyncService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sync")
public class SyncController {


    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @RequestMapping(value = "/{tenantId}",method = RequestMethod.GET)
    public void sync(@PathVariable("tenantId") Long tenantId) {
        syncService.syncDataFromHxlToAries(tenantId);
    }
}

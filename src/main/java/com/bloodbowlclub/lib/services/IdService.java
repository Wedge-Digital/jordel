package com.bloodbowlclub.lib.services;

import org.springframework.stereotype.Service;
import ulid4j.Ulid;


@Service
public class IdService implements AbstractIdService {
    public String getStringId() {
        Ulid ulid = new Ulid();
        return ulid.create();
    }
}

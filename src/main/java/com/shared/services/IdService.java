package com.shared.services;

import ulid4j.Ulid;

public class IdService {
    public static String getStringId() {
        Ulid ulid = new Ulid();
        return ulid.toString();
    }
}

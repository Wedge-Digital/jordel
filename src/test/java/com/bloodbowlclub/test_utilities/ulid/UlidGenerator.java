package com.bloodbowlclub.test_utilities.ulid;

import ulid4j.Ulid;

public class UlidGenerator {

    public static String generate() {
        return new Ulid().create();
    }
}

package com.bloodbowlclub.lib.domain;

public abstract class ValueObject<T> {

    protected T value;

    public abstract String toString();

    public abstract boolean equalsString(String id);

}

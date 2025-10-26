package com.mtech.accountservice.entity;

public enum AccountStatus {
    ACTIVE,
    FROZEN,
    CLOSED;

    public static AccountStatus fromString(String status) {
        try {
            return AccountStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid account status: " + status);
        }
    }
}

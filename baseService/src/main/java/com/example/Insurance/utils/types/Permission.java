package com.example.Insurance.utils.types;

public enum Permission {

    // Customer
    CUSTOMER_CREATE,
    CUSTOMER_READ,
    CUSTOMER_UPDATE,
    CUSTOMER_DELETE,

    // Quotation
    QUOTATION_CREATE,
    QUOTATION_READ,
    QUOTATION_UPDATE,
    QUOTATION_DELETE,

    // Proposal
    PROPOSAL_CREATE,
    PROPOSAL_READ,
    PROPOSAL_UPDATE,
    PROPOSAL_SEND,
    PROPOSAL_APPROVE,

    // Policy
    POLICY_CREATE,
    POLICY_READ,
    POLICY_UPDATE,
    POLICY_DELETE,
    POLICY_ISSUE,
    POLICY_CANCEL,
    POLICY_RENEW,

    // Email
    EMAIL_SEND,

    // File transfer
    FILE_TRANSFER_CREATE,
    FILE_TRANSFER_SEND,
    FILE_TRANSFER_RECEIVE,

    // User management
    USER_CREATE,
    USER_READ,
    USER_UPDATE,
    USER_DELETE
}

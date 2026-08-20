package com.example.Insurance.utils.types;
import java.util.Set;

// this is great that creating this if (user.getRole() == Role.ADMIN) ... to every time

public enum Role {

    ADMIN(Set.of(Permission.values())),

    UNDERWRITER(Set.of(
            Permission.CUSTOMER_READ,

            Permission.QUOTATION_CREATE,
            Permission.QUOTATION_READ,
            Permission.QUOTATION_UPDATE,

            Permission.PROPOSAL_CREATE,
            Permission.PROPOSAL_READ,
            Permission.PROPOSAL_UPDATE,
            Permission.PROPOSAL_SEND,

            Permission.EMAIL_SEND,

            Permission.FILE_TRANSFER_CREATE,
            Permission.FILE_TRANSFER_SEND,
            Permission.FILE_TRANSFER_RECEIVE
    )),

    AGENT(Set.of(
            Permission.CUSTOMER_READ,

            Permission.QUOTATION_CREATE,
            Permission.QUOTATION_READ,

            Permission.PROPOSAL_CREATE,
            Permission.PROPOSAL_READ,
            Permission.PROPOSAL_SEND
    ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
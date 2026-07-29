package com.example.Insurance.dto.branch;

import com.example.Insurance.utils.types.BranchType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchResponse {

    private Long id;
    private String branchCode;
    private String branchName;
    private BranchType branchType;
    private String address;
    private String contactNumber;
    private String email;
    private Boolean active;

}

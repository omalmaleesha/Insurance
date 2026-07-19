package com.example.Insurance.dto.branch;

import com.example.Insurance.utils.BranchType;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchRequest {

    private String branchCode;
    private String branchName;
    private BranchType branchType;
    private String address;
    private String contactNumber;
    private String email;

}

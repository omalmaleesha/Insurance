package com.example.ClaimInsurance.service.gReport;

import com.example.ClaimInsurance.service.gReport.impl.GReportDatabaseContext;
import com.example.ClaimInsurance.service.gReport.impl.GReportProcessingContext;

public interface GReportDataService {
    GReportProcessingContext loadContext(Long jobId);
    GReportDatabaseContext loadDatabaseContext(
            Long jobId
    );
}
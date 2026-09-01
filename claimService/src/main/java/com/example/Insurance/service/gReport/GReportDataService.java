package com.example.Insurance.service.gReport;

import com.example.Insurance.service.gReport.impl.GReportDatabaseContext;
import com.example.Insurance.service.gReport.impl.GReportProcessingContext;

public interface GReportDataService {
    GReportProcessingContext loadContext(Long jobId);
    GReportDatabaseContext loadDatabaseContext(
            Long jobId
    );
}
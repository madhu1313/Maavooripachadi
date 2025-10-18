package com.maavooripachadi.privacy;


import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.util.List;


@Service
public class PrivacyExportService {
    private final ConsentRecordRepository consents;
    public PrivacyExportService(ConsentRecordRepository consents){ this.consents = consents; }


    public byte[] exportConsentsCsv(String subjectId){
        List<ConsentRecord> list = consents.findBySubjectIdOrderByCreatedAtDesc(subjectId);
        StringBuilder sb = new StringBuilder();
        sb.append("createdAt,category,status,policyVersion,source\n");
        for (ConsentRecord c : list){
            sb.append(c.getCreatedAt()).append(',')
                    .append(c.getCategory()).append(',')
                    .append(c.getStatus()).append(',')
                    .append(c.getPolicyVersion()==null?"":c.getPolicyVersion()).append(',')
                    .append(c.getSource()==null?"":c.getSource()).append('\n');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
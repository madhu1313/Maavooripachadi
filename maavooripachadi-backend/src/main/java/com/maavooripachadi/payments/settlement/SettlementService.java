package com.maavooripachadi.payments.settlement;


import com.maavooripachadi.payments.settlement.dto.BatchSummaryResponse;
import com.maavooripachadi.payments.settlement.dto.SettlementIngestRequest;
import com.maavooripachadi.payments.settlement.util.Csv;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class SettlementService {


    private final SettlementBatchRepository batches;
    private final SettlementLineRepository lines;
    private final ReconcileFileRepository files;


    public SettlementService(SettlementBatchRepository batches, SettlementLineRepository lines, ReconcileFileRepository files) {
        this.batches = batches;
        this.lines = lines;
        this.files = files;
    }


    @Transactional
    public SettlementBatch ingest(SettlementIngestRequest req) {
        SettlementBatch b = new SettlementBatch();
        b.setGateway(req.getGateway());
        b.setPayoutDate(LocalDate.parse(req.getPayoutDate()));
        b.setFileId(req.getFileId());
        b.setChecksum(req.getChecksum());
        b.setTotalAmountPaise(0);
        b.setCountTxns(0);
        batches.save(b);
        if (req.getUrl() != null && !req.getUrl().isBlank()) {
            try {
                String csv = fetch(req.getUrl());
                List<String[]> rows = Csv.parse(csv);
                int total = 0;
                int count = 0;
                boolean header = true;
                for (String[] r : rows) {
                    if (header) {
                        header = false;
                        continue;
                    }
                    if (r.length < 5) continue; // orderNo, paymentId, amount, fee, tax, status
                    SettlementLine line = new SettlementLine();
                    line.setBatch(b);
                    line.setOrderNo(r[0]);
                    line.setGatewayPaymentId(r[1]);
                    int amt = parseIntSafe(r[2]);
                    int fee = parseIntSafe(r[3]);
                    int tax = parseIntSafe(r[4]);
                    String status = (r.length > 5 ? r[5] : "SETTLED");
                    line.setAmountPaise(amt);
                    line.setFeePaise(fee);
                    line.setTaxPaise(tax);
                    line.setStatus(status);
                    lines.save(line);
                    total += amt;
                    count++;
                }
                b.setTotalAmountPaise(total);
                b.setCountTxns(count);
            } catch (Exception e) {
// keep batch record even if parse failed
            }
        }
        return batches.save(b);
    }


    @Transactional(readOnly = true)
    public List<BatchSummaryResponse> list() {
        List<SettlementBatch> all = batches.findAll();
        List<BatchSummaryResponse> out = new ArrayList<>();
        for (SettlementBatch b : all) {
            BatchSummaryResponse r = new BatchSummaryResponse();
            r.setId(b.getId());
            r.setGateway(b.getGateway());
            r.setPayoutDate(String.valueOf(b.getPayoutDate()));
            r.setCountTxns(b.getCountTxns());
            r.setTotalAmountPaise(b.getTotalAmountPaise());
            out.add(r);
        }
        return out;
    }


    private String fetch(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<byte[]> resp = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        return new String(resp.body(), StandardCharsets.UTF_8);
    }


    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
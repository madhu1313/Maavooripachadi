package com.maavooripachadi.shipping;

import com.maavooripachadi.shipping.dto.RateQuoteRequest;
import com.maavooripachadi.shipping.dto.RateQuoteResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RateService {
    private final RateCardRepository rates;
    public RateService(RateCardRepository rates){ this.rates = rates; }

    @Transactional(readOnly = true)
    public List<RateQuoteResponse> quote(RateQuoteRequest req){
        // naive zone match by presence of toPincode in zone.pincodesCsv
        List<RateQuoteResponse> out = new ArrayList<>();
        for (RateCard rc : rates.findAll()){
            String csv = rc.getZone().getPincodesCsv()==null?"":rc.getZone().getPincodesCsv();
            if (!csv.contains(req.getToPincode())) continue; // simplistic; replace with robust matcher
            int grams = Math.max(req.getWeightGrams(), 1);
            int extra = Math.max(0, grams - 500);
            int slabs = (int)Math.ceil(extra / 500.0);
            int price = rc.getBasePaise() + slabs * rc.getPer500gPaise();
            RateQuoteResponse r = new RateQuoteResponse(); r.setCarrier(rc.getCarrier()); r.setServiceLevel(rc.getService()); r.setAmountPaise(price);
            out.add(r);
        }
        return out;
    }
}

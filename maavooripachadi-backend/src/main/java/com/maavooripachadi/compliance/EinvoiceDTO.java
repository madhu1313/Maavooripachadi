package com.maavooripachadi.compliance;


import java.util.List;


public record EinvoiceDTO(
        String orderNo,
        String sellerGstin,
        String buyerGstin,
        String buyerLegalName,
        String buyerAddr,
        String buyerStateCode,
        int totalValuePaise,
        int taxableValuePaise,
        int igstPaise,
        int cgstPaise,
        int sgstPaise,
        List<Item> items
) {
  public record Item(String hsnsac, String desc, int qty, int unitPricePaise, int amountPaise) {}
}
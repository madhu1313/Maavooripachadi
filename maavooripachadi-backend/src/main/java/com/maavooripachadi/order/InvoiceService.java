package com.maavooripachadi.order;
import org.springframework.stereotype.Service; import lombok.RequiredArgsConstructor; import java.io.*; import org.apache.pdfbox.pdmodel.*; import org.apache.pdfbox.pdmodel.font.PDType1Font;
@Service @RequiredArgsConstructor public class InvoiceService {
  public byte[] render(String orderNo){ try (var doc=new PDDocument()) { var page=new PDPage(); doc.addPage(page); try(var cs=new org.apache.pdfbox.pdmodel.PDPageContentStream(doc,page)){ cs.setFont(PDType1Font.HELVETICA_BOLD,18); cs.beginText(); cs.newLineAtOffset(50,750); cs.showText("Invoice: "+orderNo); cs.endText(); } try(var baos=new ByteArrayOutputStream()){ doc.save(baos); return baos.toByteArray(); } } catch(Exception e){ return new byte[0]; } }
}

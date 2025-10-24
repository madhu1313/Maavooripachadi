package com.maavooripachadi.order;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;

class InvoiceServiceTest {

    private InvoiceService service;

    @BeforeEach
    void setUp() {
        service = new InvoiceService();
    }

    @Test
    void renderGeneratesNonEmptyPdf() throws Exception {
        byte[] bytes = service.render("ORD-999");

        assertThat(bytes).isNotEmpty();

        try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(bytes))) {
            assertThat(doc.getNumberOfPages()).isEqualTo(1);
        }
    }
}

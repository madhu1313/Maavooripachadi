package com.maavooripachadi.returns;

import com.maavooripachadi.returns.dto.CreateReturnRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnsPublicControllerTest {

    @Mock private ReturnsService returnsService;

    private ReturnsPublicController controller;

    @BeforeEach
    void setUp() {
        controller = new ReturnsPublicController(returnsService);
    }

    @Test
    void openDelegatesToService() {
        CreateReturnRequest request = new CreateReturnRequest();
        ReturnRequest response = new ReturnRequest();
        when(returnsService.create(request)).thenReturn(response);

        assertThat(controller.open(request)).isSameAs(response);
        verify(returnsService).create(request);
    }
}

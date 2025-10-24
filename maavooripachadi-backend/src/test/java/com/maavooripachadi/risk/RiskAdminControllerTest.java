package com.maavooripachadi.risk;

import com.maavooripachadi.risk.dto.DenyListUpsertRequest;
import com.maavooripachadi.risk.dto.RuleRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskAdminControllerTest {

    @Mock private DenyListService denyListService;
    @Mock private DenyListRepository denyListRepository;
    @Mock private VelocityWindowRepository velocityWindowRepository;
    @Mock private RiskRuleRepository riskRuleRepository;
    @Mock private RiskCaseRepository riskCaseRepository;

    private RiskAdminController controller;

    @BeforeEach
    void setUp() {
        controller = new RiskAdminController(denyListService, denyListRepository, velocityWindowRepository, riskRuleRepository, riskCaseRepository);
    }

    @Test
    void upsertDelegatesToService() {
        DenyListUpsertRequest request = new DenyListUpsertRequest();
        DenyListEntry entry = new DenyListEntry();
        when(denyListService.upsert(request)).thenReturn(entry);

        assertThat(controller.upsert(request)).isSameAs(entry);
        verify(denyListService).upsert(request);
    }

    @Test
    void saveVelocityPersistsWindow() {
        VelocityWindow window = new VelocityWindow();
        when(velocityWindowRepository.save(window)).thenReturn(window);

        assertThat(controller.saveVel(window)).isSameAs(window);
        verify(velocityWindowRepository).save(window);
    }

    @Test
    void saveRuleBuildsEntityFromRequest() {
        RuleRequest request = new RuleRequest();
        request.setName("HighAmount");
        request.setOperator(RuleOperator.GT);
        request.setLeftKey("amountPaise");
        request.setRightValue("100000");
        request.setScoreImpact(20);
        request.setPriority(5);
        request.setActive(true);

        RiskRule saved = new RiskRule();
        when(riskRuleRepository.save(any(RiskRule.class))).thenReturn(saved);

        assertThat(controller.saveRule(request)).isSameAs(saved);
        verify(riskRuleRepository).save(any(RiskRule.class));
    }

    @Test
    void closeUpdatesCaseStatus() {
        RiskCase riskCase = new RiskCase();
        when(riskCaseRepository.findById(10L)).thenReturn(Optional.of(riskCase));
        when(riskCaseRepository.save(riskCase)).thenReturn(riskCase);

        RiskCase result = controller.close(10L, "resolved");

        assertThat(result.getStatus()).isEqualTo(CaseStatus.CLOSED);
        assertThat(result.getNotes()).isEqualTo("resolved");
    }
}

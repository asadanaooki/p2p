package com.example.p2p.controller.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.validation.BindingResult;

import com.example.p2p.form.admin.ApprovalStepApproverCreateForm;
import com.example.p2p.form.admin.ApprovalStepCreateForm;
import com.example.p2p.form.admin.ApprovalWorkflowCreateForm;
import com.example.p2p.service.admin.ApprovalService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities = { "管理者" })
class ApprovalControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ApprovalService approvalService;

    @ParameterizedTest
    @MethodSource("createOkCases")
    void createWorkflow_parameter_ok(Consumer<ApprovalWorkflowCreateForm> consumer) throws Exception {
        ApprovalWorkflowCreateForm form = baseForm();
        consumer.accept(form);
        ApprovalStepCreateForm stepForm = form.getApprovalSteps().get(0);
        ApprovalStepApproverCreateForm approverForm = stepForm.getApprovalStepApprovers().get(0);

        MockHttpServletRequestBuilder builder = post("/setting/approval/PR/workflow").with(csrf())
            .param("approvalSteps[0].name", stepForm.getName())
            .param("approvalSteps[0].approvalStepApprovers[0].userId", approverForm.getUserId())
            .param("approvalSteps[0].approvalStepApprovers[0].amountMin", approverForm.getAmountMin().toString());
        if (approverForm.getAmountMax() != null) {
            builder.param("approvalSteps[0].approvalStepApprovers[0].amountMax",
                    approverForm.getAmountMax().toString());
        }

        mockMvc.perform(builder)
            .andExpect(model().attributeHasNoErrors())
            .andExpect(redirectedUrl("/admin/approval-workflow"));
    }

    @ParameterizedTest
    @MethodSource("createNgCases")
    void createWorkflow_parameter_ng(Consumer<ApprovalWorkflowCreateForm> consumer, String expField, String expMessage)
            throws Exception {
        ApprovalWorkflowCreateForm form = baseForm();
        consumer.accept(form);
        ApprovalStepCreateForm stepForm = new ApprovalStepCreateForm();
        ApprovalStepApproverCreateForm approverForm = new ApprovalStepApproverCreateForm();
        if (!form.getApprovalSteps().isEmpty()) {
            stepForm = form.getApprovalSteps().get(0);
        }
        if (!form.getApprovalSteps().isEmpty() && !stepForm.getApprovalStepApprovers().isEmpty()) {
            approverForm = stepForm.getApprovalStepApprovers().get(0);
        }

        MockHttpServletRequestBuilder builder = post("/setting/approval/PR/workflow").with(csrf());

        if (stepForm.getName() != null) {
            builder.param("approvalSteps[0].name", stepForm.getName());
        }
        if (approverForm.getUserId() != null) {
            builder.param("approvalSteps[0].approvalStepApprovers[0].userId", approverForm.getUserId());
        }
        if (approverForm.getAmountMin() != null) {
            builder.param("approvalSteps[0].approvalStepApprovers[0].amountMin",
                    approverForm.getAmountMin().toString());
        }
        if (approverForm.getAmountMax() != null) {
            builder.param("approvalSteps[0].approvalStepApprovers[0].amountMax",
                    approverForm.getAmountMax().toString());
        }

        MvcResult res = mockMvc.perform(builder)
            .andExpect(model().attributeHasFieldErrors("form", expField))
            .andExpect(view().name("admin/approval-workflow"))
            .andReturn();

        Map<String, Object> model = res.getModelAndView().getModel();
        BindingResult br = (BindingResult) model.get(BindingResult.MODEL_KEY_PREFIX + "form");
        assertThat(br.getFieldError(expField).getDefaultMessage()).isEqualTo(expMessage);
    }

    static Stream<Arguments> createOkCases() {
        return Stream.of(
                // approvalSteps
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> {
                    ApprovalStepCreateForm stepForm = new ApprovalStepCreateForm();
                    ApprovalStepApproverCreateForm approverForm = new ApprovalStepApproverCreateForm();

                    approverForm.setUserId("a".repeat(36));
                    approverForm.setAmountMin(0);
                    approverForm.setAmountMax(5000);

                    stepForm.setName("testStep");
                    stepForm.setApprovalStepApprovers(List.of(approverForm));
                    f.setApprovalSteps(List.of(stepForm));
                }),

                // ApprovalStepCreateForm
                // name
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> f.getApprovalSteps().get(0).setName("test")),
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> f.getApprovalSteps()
                    .get(0)
                    .setName("t".repeat(100))),
                // approvalStepApprovers
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> {
                    ApprovalStepApproverCreateForm form = new ApprovalStepApproverCreateForm();
                    form.setUserId("a");
                    form.setAmountMin(0);
                    form.setAmountMax(100);
                    f.getApprovalSteps().get(0).setApprovalStepApprovers(List.of(form));
                }),

                // ApprovalStepApproverCreateForm
                // amountMin
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> f.getApprovalSteps()
                    .get(0)
                    .getApprovalStepApprovers()
                    .get(0)
                    .setAmountMin(0)),
                // amountMax
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> f.getApprovalSteps()
                    .get(0)
                    .getApprovalStepApprovers()
                    .get(0)
                    .setAmountMax(null)),
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> f.getApprovalSteps()
                    .get(0)
                    .getApprovalStepApprovers()
                    .get(0)
                    .setAmountMax(0)),
                // assertTrue
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> {
                    ApprovalStepApproverCreateForm form = f.getApprovalSteps().get(0).getApprovalStepApprovers().get(0);
                    form.setAmountMin(1000);
                    form.setAmountMax(2500);
                }));
    }

    static Stream<Arguments> createNgCases() {
        return Stream.of(
                // approvalSteps
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> f.setApprovalSteps(Collections.EMPTY_LIST),
                        "approvalSteps", "承認ステップを1件以上登録してください。"),

                // ApprovalStepCreateForm
                // name
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> f.getApprovalSteps().get(0).setName(null),
                        "approvalSteps[0].name", "承認ステップ名を入力してください。"),
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> f.getApprovalSteps().get(0).setName(" "),
                        "approvalSteps[0].name", "承認ステップ名を入力してください。"),
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> f.getApprovalSteps()
                    .get(0)
                    .setName("a".repeat(101)), "approvalSteps[0].name", "承認ステップ名は100文字以内で入力してください。"),

                // approvalStepApprovers
                Arguments.of(
                        (Consumer<ApprovalWorkflowCreateForm>) f -> f.getApprovalSteps()
                            .get(0)
                            .setApprovalStepApprovers(Collections.EMPTY_LIST),
                        "approvalSteps[0].approvalStepApprovers", "承認者を1人以上追加してください。"),

                // ApprovalStepApproverCreateForm
                // userId
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> f.getApprovalSteps()
                    .get(0)
                    .getApprovalStepApprovers()
                    .get(0)
                    .setUserId(null), "approvalSteps[0].approvalStepApprovers[0].userId", "承認者を選択してください。"),
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> f.getApprovalSteps()
                    .get(0)
                    .getApprovalStepApprovers()
                    .get(0)
                    .setUserId(""), "approvalSteps[0].approvalStepApprovers[0].userId", "承認者を選択してください。"),
                // amountMin
                Arguments.of(
                        (Consumer<ApprovalWorkflowCreateForm>) f -> f.getApprovalSteps()
                            .get(0)
                            .getApprovalStepApprovers()
                            .get(0)
                            .setAmountMin(null),
                        "approvalSteps[0].approvalStepApprovers[0].amountMin", "最小金額を入力してください。"),
                Arguments.of(
                        (Consumer<ApprovalWorkflowCreateForm>) f -> f.getApprovalSteps()
                            .get(0)
                            .getApprovalStepApprovers()
                            .get(0)
                            .setAmountMin(-1),
                        "approvalSteps[0].approvalStepApprovers[0].amountMin", "最小金額は0以上で入力してください。"),
                // amountMax
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> {
                    ApprovalStepApproverCreateForm form = f.getApprovalSteps().get(0).getApprovalStepApprovers().get(0);
                    form.setAmountMin(1);
                    form.setAmountMax(-1);
                }, "approvalSteps[0].approvalStepApprovers[0].amountMax", "最大金額は0以上で入力してください。"),

                // assertTrue
                Arguments.of((Consumer<ApprovalWorkflowCreateForm>) f -> {
                    ApprovalStepApproverCreateForm form = f.getApprovalSteps().get(0).getApprovalStepApprovers().get(0);
                    form.setAmountMin(4000);
                    form.setAmountMax(2500);
                }, "approvalSteps[0].approvalStepApprovers[0].amountRangeValid", "最大金額は最小金額以上で入力してください。"));
    }

    ApprovalWorkflowCreateForm baseForm() {
        ApprovalWorkflowCreateForm workflowForm = new ApprovalWorkflowCreateForm();
        ApprovalStepCreateForm stepForm = new ApprovalStepCreateForm();
        ApprovalStepApproverCreateForm approverForm = new ApprovalStepApproverCreateForm();

        approverForm.setUserId("a".repeat(36));
        approverForm.setAmountMin(0);
        approverForm.setAmountMax(5000);

        stepForm.setName("testStep");
        stepForm.setApprovalStepApprovers(List.of(approverForm));

        workflowForm.setApprovalSteps(List.of(stepForm));

        return workflowForm;

    }

}

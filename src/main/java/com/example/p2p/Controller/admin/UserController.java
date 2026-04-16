package com.example.p2p.controller.admin;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.p2p.advice.NormalizationEditor;
import com.example.p2p.dto.RoleOptionDto;
import com.example.p2p.dto.UserDetailDto;
import com.example.p2p.exception.BusinessException;
import com.example.p2p.form.UnitEditForm;
import com.example.p2p.form.UserSearchForm;
import com.example.p2p.form.UserUpsertForm;
import com.example.p2p.service.admin.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/setting/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    private MessageSource messageSource;
    private ModelMapper modelMapper;

    private static final String LAST_SEARCH_CONDITION = "lastSearchCondition";
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @InitBinder("keyword")
    public void initSearchBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new NormalizationEditor());
    }

    @GetMapping
    public String showUserList(@Valid @ModelAttribute("form") UserSearchForm form, 
            BindingResult bindingResult,
            Model model,
            HttpSession session) {
        logger.debug("ユーザー一覧表示開始");
        if (bindingResult.hasErrors()) {
            logger.warn("ユーザー一覧の入力エラー: errorCount={}", bindingResult.getErrorCount());
            UserSearchForm lastCondition = (UserSearchForm) session.getAttribute(LAST_SEARCH_CONDITION);
            UserSearchForm formToSearch = lastCondition == null ? new UserSearchForm() : lastCondition;
            formToSearch = lastCondition == null ? new UserSearchForm() : lastCondition;
            model.addAttribute("view", userService.searchUsers(formToSearch));
            return "user-list";
        }
        session.setAttribute(LAST_SEARCH_CONDITION, form);
        model.addAttribute("view", userService.searchUsers(form));

        logger.debug("ユーザー一覧表示完了");
        return "user-list";
    }

    @GetMapping("/{userId}")
    public String showRoleDetail(@PathVariable String userId, Model model) {
        logger.debug("ユーザー詳細表示開始");
        model.addAttribute("userId", userId);
        model.addAttribute("user", userService.getUserDetail(userId));

        logger.debug("ユーザー詳細表示完了");
        return "user-detail";
    }

    @GetMapping("/create")
    public String showUserCreateForm(@ModelAttribute("form") UserUpsertForm form, Model model) {
        logger.debug("ユーザー作成画面表示");
        return "user-create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("form") UserUpsertForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        logger.info("ユーザー作成開始");
        if (result.hasErrors()) {
            logger.warn("ユーザー作成の入力エラー: errorCount={}", result.getErrorCount());
            return "user-create";
        }
        String userId;
        try {
            userId = userService.create(form);
        }
        catch (BusinessException e) {
            result.rejectValue("email", "duplicate", messageSource.getMessage("common.duplicate", null, null));
            return "user-create";
        }
        redirectAttributes.addFlashAttribute("created", true);
        redirectAttributes.addFlashAttribute("successMessage",
                messageSource.getMessage("common.create.success", null, null));
        redirectAttributes.addAttribute("userId", userId);
        logger.info("ユーザー作成成功");
        return "redirect:/setting/user/{userId}";
    }
    
    @GetMapping("/{userId}/edit")
    public String showUserEditForm(@PathVariable String userId,
            @ModelAttribute("form") UserUpsertForm form,
            Model model) {
        logger.debug("ユーザー編集表示開始");
        model.addAttribute("userId", userId);
        UserDetailDto user = userService.getUserDetail(userId);
        modelMapper.map(user, form);

        logger.debug("ユーザー編集表示完了");
        return "user-edit";
    }
    
     @PostMapping("/{userId}/update")
     public String update(@PathVariable String userId, 
             @Valid @ModelAttribute("form") UserUpsertForm form,
     BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
     if (bindingResult.hasErrors()) {
     model.addAttribute("userId", userId);
     return "user-edit";
     }
     try {
     userService.update(userId, form);
     }
     catch (BusinessException e) {
     model.addAttribute("userId", userId);
     bindingResult.rejectValue("email", "duplicate",
     messageSource.getMessage("common.duplicate", null, null));
     return "user-edit";
     }
     redirectAttributes.addFlashAttribute("successMessage",
     messageSource.getMessage("common.update.success", null, null));
     redirectAttributes.addAttribute("userId", userId);
     return "redirect:/setting/user/{userId}";
    
     }
     
     @ModelAttribute("roleOptions")
     public List<RoleOptionDto> addRoleOptions() {
         return userService.getRoleOptions();
     }

}

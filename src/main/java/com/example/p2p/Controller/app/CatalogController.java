package com.example.p2p.controller.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.p2p.form.app.CatalogSearchForm;
import com.example.p2p.service.app.CatalogService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/catalog")
@AllArgsConstructor
public class CatalogController {

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    private final CatalogService catalogService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, "supplierId", new StringTrimmerEditor(true));
    }

    
    @PreAuthorize("hasAuthority('PR_CREATE')") // TODO: 今後、POやInvも追加？
    @GetMapping
    public String showCatalogList(@ModelAttribute("catalogSearchForm") CatalogSearchForm catalogSearchForm,
            Model model) {
        logger.debug("カタログ一覧画面表示開始");

        model.addAttribute("catalogView", catalogService.searchItems(catalogSearchForm));

        logger.debug("カタログ一覧画面表示完了");

        return "fragments/catalog :: catalogContent";
    }

    @PreAuthorize("hasAuthority('PR_CREATE')") // TODO: 今後、POやInvも追加？
    @GetMapping("/search")
    public String searchCatalogList(@Valid @ModelAttribute("catalogSearchForm") CatalogSearchForm catalogSearchForm,
            BindingResult bindingResult, Model model, HttpServletResponse response) {
        logger.debug("カタログ一覧検索開始");

        if (bindingResult.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "app/catalog :: catalogSearchErrors";
        }

        model.addAttribute("catalogView", catalogService.searchItems(catalogSearchForm));

        logger.debug("カタログ一覧検索完了");

        return "app/catalog :: catalogResultArea";
    }

}
package com.example.p2p.service.app;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.p2p.dto.app.CatalogListRowDto;
import com.example.p2p.dto.app.CatalogListViewDto;
import com.example.p2p.form.app.CatalogSearchForm;
import com.example.p2p.mapper.ItemMapperCustom;
import com.example.p2p.mapper.SupplierMapperCustom;
import com.example.p2p.util.CommonUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CatalogService {

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    private ItemMapperCustom itemMapperCustom;
    
    private SupplierMapperCustom supplierMapperCustom;

    public CatalogListViewDto searchItems(CatalogSearchForm form) {
        logger.debug("カタログ商品検索開始");

        CatalogListViewDto dto = new CatalogListViewDto();
        int page = form.getPage();
        List<CatalogListRowDto> items = itemMapperCustom.selectCatalogItems(form);
        dto.setItems(items);
        dto.setSupplierOptions(supplierMapperCustom.selectSupplierOptions());
        dto.setCurrentPage(page);
        dto.setPageNumberList(
                CommonUtil.createPageNumbers(itemMapperCustom.countCatalogItems(form), form.getSize(), page, 2));

        logger.debug("カタログ商品検索完了");

        return dto;
    }

}

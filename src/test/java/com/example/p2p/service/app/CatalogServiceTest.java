package com.example.p2p.service.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.p2p.dto.admin.ItemListViewDto;
import com.example.p2p.dto.app.CatalogListViewDto;
import com.example.p2p.entity.ItemExample;
import com.example.p2p.entity.PurchaseRequestDetailExample;
import com.example.p2p.form.app.CatalogSearchForm;
import com.example.p2p.mapper.ItemMapper;
import com.example.p2p.mapper.PurchaseRequestDetailMapper;

@SpringBootTest
@Transactional
class CatalogServiceTest {

    @Autowired
    CatalogService catalogService;
    
    @Autowired
    ItemMapper itemMapper;
    
    @Autowired
    PurchaseRequestDetailMapper purchaseRequestDetailMapper;
    
    @Nested
    class SearchCatalogItems {

        @Test
        void searchCatalogItems_notFound() {
            purchaseRequestDetailMapper.deleteByExample(new PurchaseRequestDetailExample());
            itemMapper.deleteByExample(new ItemExample());
            
            CatalogListViewDto actual = catalogService.searchItems(new CatalogSearchForm());

            assertThat(actual.getItems()).isEmpty();
            assertThat(actual.getSupplierOptions().size()).isEqualTo(3);
            assertThat(actual.getCurrentPage()).isEqualTo(1);
            assertThat(actual.getPageNumberList()).isEmpty();
        }
        
        @Test
        void searchCatalogItems_hits() {
            CatalogListViewDto actual = catalogService.searchItems(new CatalogSearchForm());

            assertThat(actual.getItems().size()).isEqualTo(2);
            assertThat(actual.getSupplierOptions().size()).isEqualTo(3);
            assertThat(actual.getCurrentPage()).isEqualTo(1);
            assertThat(actual.getPageNumberList().size()).isEqualTo(2);
            
            assertThat(actual.getItems().get(0).getItemId()).isEqualTo("f758e462-f526-4b23-a822-8821c5c62adf");
        }

    }
    
}

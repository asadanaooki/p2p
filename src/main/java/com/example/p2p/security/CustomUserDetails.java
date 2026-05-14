package com.example.p2p.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.p2p.dto.app.AuthenticationUserDto;
import com.example.p2p.enums.VisibilityScope;

import lombok.Data;

@Data
public class CustomUserDetails implements UserDetails {

    private String username;

    private String password;

    private String email;

    private String roleName;

    private VisibilityScope prViewScope;

    private VisibilityScope poViewScope;

    private VisibilityScope receiptViewScope;

    private VisibilityScope invoiceViewScope;

    private List<SimpleGrantedAuthority> authorities;

    public CustomUserDetails(AuthenticationUserDto authUser) {
        this.username = authUser.getUserId();
        this.password = authUser.getPasswordHash();
        this.email = authUser.getEmail();
        this.roleName = authUser.getRoleName();
        this.prViewScope = authUser.getPrViewScope();
        this.poViewScope = authUser.getPoViewScope();
        this.receiptViewScope = authUser.getReceiptViewScope();
        this.invoiceViewScope = authUser.getInvoiceViewScope();

        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        // ロール名称
        authorities.add(new SimpleGrantedAuthority(authUser.getRoleName()));
        // PR作成
        if (authUser.getPrCreate()) {
            authorities.add(new SimpleGrantedAuthority("PR_CREATE"));
        }
        // PO作成
        if (authUser.getPoCreate()) {
            authorities.add(new SimpleGrantedAuthority("PO_CREATE"));
        }
        // Receipt作成
        if (authUser.getReceiptCreate()) {
            authorities.add(new SimpleGrantedAuthority("RECEIPT_CREATE"));
        }
        // PR閲覧範囲
        if (authUser.getPrViewScope() == VisibilityScope.ALL) {
            authorities.add(new SimpleGrantedAuthority("PR_VIEW_ALL"));
        }
        else if (authUser.getPrViewScope() == VisibilityScope.SELF) {
            authorities.add(new SimpleGrantedAuthority("PR_VIEW_SELF"));
        }
        // PO閲覧範囲
        if (authUser.getPoViewScope() == VisibilityScope.ALL) {
            authorities.add(new SimpleGrantedAuthority("PO_VIEW_ALL"));
        }
        else if (authUser.getPoViewScope() == VisibilityScope.SELF) {
            authorities.add(new SimpleGrantedAuthority("PO_VIEW_SELF"));
        }
        // Receipt閲覧範囲
        if (authUser.getReceiptViewScope() == VisibilityScope.ALL) {
            authorities.add(new SimpleGrantedAuthority("RECEIPT_VIEW_ALL"));
        }
        else if (authUser.getReceiptViewScope() == VisibilityScope.SELF) {
            authorities.add(new SimpleGrantedAuthority("RECEIPT_VIEW_SELF"));
        }
        // Invoice閲覧範囲
        if (authUser.getInvoiceViewScope() == VisibilityScope.ALL) {
            authorities.add(new SimpleGrantedAuthority("INVOICE_VIEW_ALL"));
        }
        else if (authUser.getInvoiceViewScope() == VisibilityScope.SELF) {
            authorities.add(new SimpleGrantedAuthority("INVOICE_VIEW_SELF"));
        }
        // PR承認
        if (authUser.getPrApprove()) {
            authorities.add(new SimpleGrantedAuthority("PR_APPROVE"));
        }
        // PO承認
        if (authUser.getPoApprove()) {
            authorities.add(new SimpleGrantedAuthority("PO_APPROVE"));
        }
        // Invoice承認
        if (authUser.getInvoiceApprove()) {
            authorities.add(new SimpleGrantedAuthority("INVOICE_APPROVE"));
        }
        // 管理設定
        if (authUser.getSettingManage()) {
            authorities.add(new SimpleGrantedAuthority("SETTING_MANAGE"));
        }

        this.authorities = authorities;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

}

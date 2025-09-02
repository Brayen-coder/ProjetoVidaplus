package br.com.vidaplus.util;

import br.com.vidaplus.model.UserAccount;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static UserAccount currentUser(){
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if(a != null && a.getPrincipal() instanceof UserAccount u){
            return u;
        }
        return null;
    }
}

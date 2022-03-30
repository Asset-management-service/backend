package com.backend.moamoa.global.util;

import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
public class SecurityUtil {


    public static Long getCurrentUserId(){
       UserAuthentication authentication =(UserAuthentication)SecurityContextHolder.getContext().getAuthentication();

       return authentication.getUserId();
    }

}

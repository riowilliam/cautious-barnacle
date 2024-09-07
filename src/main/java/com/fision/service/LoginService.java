package com.fision.service;

import com.fision.entity.TbUser;

/**
 * @author LordDev
 */
public interface LoginService {
    TbUser getUserByUsername(String username);
    Boolean isUserValid(String username, String password);
    String authenticate(String username, String password);
    void sendResetPassword(TbUser user);
    TbUser getUserByEmail(String email);
    String getToken(String plainText, TbUser tbUser) throws Exception;
    String getRole(TbUser tbUser);
    String generateRandomPassword();
}

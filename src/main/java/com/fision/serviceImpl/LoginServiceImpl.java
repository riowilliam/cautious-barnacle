package com.fision.serviceImpl;

import com.fision.config.JWTTokenUtil;
import com.fision.entity.TbUser;
import com.fision.entity.TmRole;
import com.fision.repository.TbUserRepository;
import com.fision.repository.TmRoleRepository;
import com.fision.service.LoginService;
import com.fision.utils.AesEncodeDecodeService;
import com.fision.utils.ConstantsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

/**
 * @author LordDev
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Value("${secret.key}")
    protected String secretKey;

    @Autowired
    TbUserRepository tbUserRepository;

    @Autowired
    TmRoleRepository tmRoleRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JWTTokenUtil jwtTokenUtil;

    @Autowired
    private AesEncodeDecodeService aesEncodeDecodeService;

    @Autowired
    private JavaMailSender emailSender;


    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String OTHER_CHAR = "!@#$%&*()-+=<>?";

    private static final String PASSWORD_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
    private static final int PASSWORD_LENGTH = 12;

    @Override
    public TbUser getUserByUsername(String username) {
        return tbUserRepository.findByUsername(username);
    }

    @Override
    public Boolean isUserValid(String username, String password) {
        Boolean isValid = Boolean.FALSE;
        TbUser user = tbUserRepository.findByUsername(username);
        if(user != null) {
            isValid = BCrypt.checkpw(password, user.getPassword());
        }
        return isValid;
    }

    @Override
    public String authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return jwtTokenUtil.generateToken(userDetails);
    }

    @Override
    public void sendResetPassword(TbUser user) {
        SimpleMailMessage message = new SimpleMailMessage();
        // Create new random password
        String newPassword = generateRandomPassword();
        user.setPassword(BCrypt.hashpw(newPassword , BCrypt.gensalt(12)));

        message.setTo(user.getEmail());
        message.setSubject(ConstantsUtils.RESET_PASSWORD);
        message.setText(ConstantsUtils.EMAIL_BODY_TMPL + newPassword);
//        message.setFrom("noreply@fision.com");
        emailSender.send(message);

        tbUserRepository.save(user);
    }

    @Override
    public TbUser getUserByEmail(String email) {
        return tbUserRepository.findByEmail(email);
    }

    @Override
    public String getToken(String plainText, TbUser tbUser) throws Exception {
        String token = Base64.getEncoder().encodeToString(aesEncodeDecodeService.encrypt(plainText, secretKey));
        tbUserRepository.save(tbUser);
        return token;
    }

    @Override
    public String getRole(TbUser tbUser) {
        TmRole tmRole = tmRoleRepository.findByRoleCode(tbUser.getRoleCode());
        return tmRole != null ? tmRole.getRoleName() : "User";
    }

    @Override
    public String generateRandomPassword() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(PASSWORD_CHARS.length());
            password.append(PASSWORD_CHARS.charAt(randomIndex));
        }

        return password.toString();
    }
}

package com.fision.controller;

import com.fision.dto.AuthRequestDto;
import com.fision.dto.LoginRequestDto;
import com.fision.dto.LoginResponseDto;
import com.fision.dto.ResponseDto;
import com.fision.entity.TbUser;
import com.fision.service.LoginService;
import com.fision.utils.AesEncodeDecodeService;
import com.fision.utils.ConstantsUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LordDev
 */
@RestController
@RequestMapping("/api/")
@CrossOrigin
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    LoginService loginService;

    @Autowired
    AesEncodeDecodeService aesEncodeDecodeService;

    // To do remove this
    @GetMapping("getEncryptedPassword")
    public ResponseDto<?> getEncryptedPassword(@RequestParam String password) {
        try {
            String encryptedPass = BCrypt.hashpw(password, BCrypt.gensalt(12));
            return new ResponseDto<>(encryptedPass, ConstantsUtils.SUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("login")
    public ResponseDto<?> userLogin(@RequestBody String requestLogin) {
        try {
            if(requestLogin == null || requestLogin.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            LoginRequestDto loginRequestDto = gson.fromJson(requestLogin, LoginRequestDto.class);
            LoginResponseDto loginResponseDto = new LoginResponseDto();
            if(loginRequestDto != null) {
                TbUser user = loginService.getUserByUsername(loginRequestDto.getUsername());
                String userRole = loginService.getRole(user);
                loginResponseDto.setIsValid(loginService.isUserValid(loginRequestDto.getUsername(), loginRequestDto.getPassword()));
                loginResponseDto.setJwtToken(loginResponseDto.getIsValid() == Boolean.TRUE ? loginService.authenticate(loginRequestDto.getUsername(), loginRequestDto.getPassword()) : null);
                loginResponseDto.setFullName(loginResponseDto.getIsValid() == Boolean.TRUE ? user.getFullName() : null);
                loginResponseDto.setUsername(loginResponseDto.getIsValid() == Boolean.TRUE ? user.getUsername() : null);
                loginResponseDto.setUserRole(userRole);
                return loginResponseDto.getIsValid() == Boolean.TRUE ? new ResponseDto<>("Success.", loginResponseDto, HttpStatus.OK) :
                        new ResponseDto<>(ConstantsUtils.INVALID_USERNAME_OR_PASS, loginResponseDto, HttpStatus.UNAUTHORIZED);
            } else {
                loginResponseDto.setIsValid(Boolean.FALSE);
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, loginResponseDto, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // To do remove this
    @PostMapping("authenticate")
    public ResponseDto<?> getAuth(@RequestBody String authRequest) {
        try {
            Gson gson = new Gson();
            AuthRequestDto authRequestDto = gson.fromJson(authRequest, AuthRequestDto.class);
            String token = loginService.authenticate(authRequestDto.getUsername(), authRequestDto.getPassword());
            String encryptedText = Base64.getEncoder().encodeToString(aesEncodeDecodeService.encrypt(token, "12345678901234567890123456789012"));

            Map<String, Object> data = new HashMap<>();
            data.put("encrypted",  encryptedText);
            data.put("decrypted", aesEncodeDecodeService.decrypt(aesEncodeDecodeService.encrypt(token, "12345678901234567890123456789012"), "12345678901234567890123456789012"));

            return new ResponseDto<>("Success.", data, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("sendResetPassword")
    public ResponseDto<?> sendResetPassword(@RequestParam String email) {
        try {
            TbUser user = loginService.getUserByEmail(email);
            if (user == null) {
                return new ResponseDto<>(ConstantsUtils.INVALID_EMAIL, null, HttpStatus.UNAUTHORIZED);
            }

            loginService.sendResetPassword(user);
            return new ResponseDto<>(HttpStatus.OK.getReasonPhrase(), "Email sent, please check your email.", HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.fision.controller;

import com.fision.dto.*;
import com.fision.service.UserService;
import com.fision.utils.ConstantsUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @author LordDev
 */

@RestController
@RequestMapping("/api/user/")
@CrossOrigin
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @PostMapping("changeUsername")
    public ResponseDto<?> changeUsername(@RequestBody String changeUsernameRequest) {
        try {
            if(changeUsernameRequest == null || changeUsernameRequest.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            ChangeUsernameRequestDto requestDto = gson.fromJson(changeUsernameRequest, ChangeUsernameRequestDto.class);
            String result = userService.changeUsername(requestDto);
            return new ResponseDto<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("changePassword")
    public ResponseDto<?> changePassword(@RequestBody String changePasswordRequest) {
        try {
            if(changePasswordRequest == null || changePasswordRequest.isEmpty()) {
                return new ResponseDto<>("Invalid Request.", null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            ChangePasswordRequestDto requestDto = gson.fromJson(changePasswordRequest, ChangePasswordRequestDto.class);
            String result = userService.changePassword(requestDto);
            return new ResponseDto<>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getUserList")
    public ResponseDto<?> getUserList() {
        try {
            String[] userList = userService.getUserList();
            return new ResponseDto<>(ConstantsUtils.SUCCESS, userList, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getProfile")
    public ResponseDto<?> getUserProfile(@RequestParam String username) {
        try {
            UserProfileDto userProfileDto = userService.getUserProfile(username);
            if(userProfileDto != null) {
                return new ResponseDto<>(ConstantsUtils.SUCCESS, userProfileDto, HttpStatus.OK);
            } else {
                return new ResponseDto<>(ConstantsUtils.USER_NOT_FOUND, null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("createUser")
    public ResponseDto<?> createUser(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>("Invalid Request.", null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            UserProfileDto userProfileDto = gson.fromJson(requestDto, UserProfileDto.class);
            UserProfileDto existingUser = userService.getUserProfile(username);
            if(existingUser != null) {
                return new ResponseDto<>(ConstantsUtils.USERNAME_ALREADY_USED, HttpStatus.OK);
            } else {
                userService.addUser(username, userProfileDto);
                return new ResponseDto<>(ConstantsUtils.USER_HAS_BEEN_CREATED, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("editUser")
    public ResponseDto<?> editUser(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>("Invalid Request.", null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            UserProfileDto userProfileDto = gson.fromJson(requestDto, UserProfileDto.class);
            UserProfileDto requesterUser = userService.getUserProfile(username);
            if(requesterUser != null && (requesterUser.getRoleCode().equalsIgnoreCase(ConstantsUtils.SUPER_ADM)
                    || requesterUser.getRoleCode().equalsIgnoreCase(ConstantsUtils.ADMIN)) && userProfileDto != null) {
                userService.editUserRole(username, userProfileDto);
                return new ResponseDto<>(ConstantsUtils.USER_HAS_BEEN_UPDATED, HttpStatus.OK);
            } else {
                return new ResponseDto<>(HttpStatus.FORBIDDEN.getReasonPhrase(), HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getUserListPaging")
    public ResponseDto<?> getUserListPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) String contact
    ) {
        try {
            Page<UserListDto> userListDtos = userService.getUserListPaging(
                    pageNo, pageSize, sortBy, sortOrder,
                    fullName != null && !fullName.isEmpty() ? fullName : null, email != null && !email.isEmpty() ? email : null,
                    roleCode != null && !roleCode.isEmpty() ? roleCode : null, contact != null && !contact.isEmpty() ? contact : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, userListDtos, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getRoleList")
    public ResponseDto<?> getRoleList() {
        try {
            List<RoleListDto> roleList = userService.getRoleList();
            return new ResponseDto<>(ConstantsUtils.SUCCESS, roleList, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

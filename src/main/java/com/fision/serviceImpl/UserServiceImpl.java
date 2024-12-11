package com.fision.serviceImpl;

import com.fision.dto.*;
import com.fision.entity.primary.TbUser;
import com.fision.repository.primary.TbUserRepository;
import com.fision.repository.primary.TmRoleRepository;
import com.fision.service.LoginService;
import com.fision.service.UserService;
import com.fision.utils.ConstantsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author LordDev
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    TbUserRepository tbUserRepository;

    @Autowired
    TmRoleRepository tmRoleRepository;

    @Autowired
    LoginService loginService;

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public String changeUsername(ChangeUsernameRequestDto requestDto) {
        // Find current user
        TbUser user = tbUserRepository.findByUsername(requestDto.getOldUsername());

        if (user == null) {
            return ConstantsUtils.USER_NOT_FOUND;
        } else {
            Boolean isValid = loginService.isUserValid(requestDto.getOldUsername(), requestDto.getPassword());
            if (isValid) {
                // Already exist or not?
                TbUser anotherUser = tbUserRepository.findByUsername(requestDto.getNewUsername());
                if(anotherUser != null) {
                    return ConstantsUtils.USERNAME_ALREADY_USED;
                } else {
                    user.setUsername(requestDto.getNewUsername());
                    user.setModifiedTm(new Date());
                    user.setModifiedBy(requestDto.getNewUsername());
                    tbUserRepository.save(user);
                    return ConstantsUtils.USERNAME_HAS_CHANGED;
                }
            } else {
                return ConstantsUtils.INVALID_PASSWORD;
            }
        }
    }

    @Override
    public String changeFullName(ChangeFullNameDto requestDto) {
        // Find current user
        TbUser user = tbUserRepository.findByUsername(requestDto.getUsername());

        if (user == null) {
            return ConstantsUtils.USER_NOT_FOUND;
        } else {
            Boolean isValid = loginService.isUserValid(requestDto.getUsername(), requestDto.getPassword());
            if (isValid) {
                user.setFullName(requestDto.getNewFullName());
                user.setModifiedTm(new Date());
                user.setModifiedBy(requestDto.getUsername());
                tbUserRepository.save(user);
                return ConstantsUtils.FULLNAME_HAS_CHANGED;
            } else {
                return ConstantsUtils.INVALID_PASSWORD;
            }
        }
    }

    @Override
    public String changePassword(ChangePasswordRequestDto requestDto) {
        // Cant be same with current password
        if(requestDto.getOldPassword().equals(requestDto.getNewPassword())) {
            return ConstantsUtils.PASSWORD_CANT_BE_SAME;
        }

        // Find current user
        TbUser user = tbUserRepository.findByUsername(requestDto.getUsername());
        if (user == null) {
            return ConstantsUtils.USER_NOT_FOUND;
        } else {
            Boolean isValid = loginService.isUserValid(requestDto.getUsername(), requestDto.getOldPassword());
            if (isValid) {
                    user.setPassword(BCrypt.hashpw(requestDto.getNewPassword(), BCrypt.gensalt(12)));
                    user.setModifiedTm(new Date());
                    user.setModifiedBy(requestDto.getUsername());
                    tbUserRepository.save(user);
                    return ConstantsUtils.PASSWORD_HAS_CHANGED;
            } else {
                return ConstantsUtils.INVALID_PASSWORD;
            }
        }
    }

    @Override
    public String[] getUserList() {
        List<TbUser> userList = tbUserRepository.findAll(Sort.by(Sort.DEFAULT_DIRECTION, "fullName"));
        return userList.stream()
                .map(TbUser::getFullName)
                .toArray(String[]::new);
    }

    @Override
    public UserProfileDto getUserProfile(String username) {
        TbUser user = tbUserRepository.findByUsername(username);
        if (user != null) {
            UserProfileDto userProfileDto = new UserProfileDto();
            userProfileDto.setUsername(user.getUsername());
            userProfileDto.setFullName(user.getFullName());
            userProfileDto.setEmail(user.getEmail());
            userProfileDto.setContact(user.getContact());
            userProfileDto.setRoleCode(user.getRoleCode());
            return userProfileDto;
        } else {
            return null;
        }
    }

    @Override
    public void addUser(String requestBy, UserProfileDto userProfileDto) {
        TbUser user = new TbUser();
        user.setUsername(userProfileDto.getUsername());
        user.setFullName(userProfileDto.getFullName());
        user.setEmail(userProfileDto.getEmail());
        user.setCreatedBy(requestBy);
        user.setModifiedBy(requestBy);
        user.setContact(userProfileDto.getContact());
        user.setRoleCode(userProfileDto.getRoleCode());

        SimpleMailMessage message = new SimpleMailMessage();
        String newPassword = loginService.generateRandomPassword();
        user.setPassword(BCrypt.hashpw(newPassword , BCrypt.gensalt(12)));

        message.setTo(user.getEmail());
        message.setSubject(ConstantsUtils.NEW_ACCOUNT_PASSWORD);
        message.setText(ConstantsUtils.EMAIL_BODY_TMPL.replace("USER_PLACEHOLDER", user.getUsername()) + newPassword);
        emailSender.send(message);

        tbUserRepository.save(user);
    }

    @Override
    public void editUserRole(String requestBy, UserProfileDto userProfileDto) {
        TbUser user = tbUserRepository.findByUsername(userProfileDto.getUsername());
        user.setRoleCode(userProfileDto.getRoleCode());
        tbUserRepository.save(user);
    }

    @Override
    public Page<UserListDto> getUserListPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                               String fullName, String email, String roleCode, String contact) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        return tbUserRepository.getUserListPaging(fullName, email, roleCode, contact, pageable);
    }

    @Override
    public List<RoleListDto> getRoleList() {
        return tmRoleRepository.getRoleList();
    }
}

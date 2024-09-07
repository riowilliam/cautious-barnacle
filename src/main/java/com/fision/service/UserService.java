package com.fision.service;

import com.fision.dto.*;
import com.fision.entity.TbUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    String changeUsername(ChangeUsernameRequestDto requestDto);
    String changePassword(ChangePasswordRequestDto requestDto);
    String[] getUserList();
    UserProfileDto getUserProfile(String username);
    void addUser(String requestBy, UserProfileDto userProfileDto);
    void editUserRole(String requestBy, UserProfileDto userProfileDto);
    Page<UserListDto> getUserListPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                        String fullName, String email, String ruleCode, String contact);
    List<RoleListDto> getRoleList();
}

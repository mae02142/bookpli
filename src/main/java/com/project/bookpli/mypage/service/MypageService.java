package com.project.bookpli.mypage.service;

import com.project.bookpli.common.exception.BaseException;
import com.project.bookpli.common.response.BaseResponseStatus;
import com.project.bookpli.entity.User;
import com.project.bookpli.mypage.dto.UserDTO;
import com.project.bookpli.mypage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserRepository userRepository;

    public UserDTO getUserProfile(Long userId) {
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));
        return UserDTO.fromEntity(user);
    }

    @Transactional
    public void patchNickName(Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue(); // 안전한 타입 변환
        String nickName = (String) request.get("userNickname");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));

        user.updateNickName(nickName);
    }

    public UserDTO duplicateCheckNickname(String nickName) {
        Optional<User> user = userRepository.findByUserNickname(nickName);
        return user.map(UserDTO::fromEntity).orElse(null);
    }
}

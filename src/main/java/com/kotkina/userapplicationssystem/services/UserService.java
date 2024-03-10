package com.kotkina.userapplicationssystem.services;

import com.kotkina.userapplicationssystem.entities.RoleType;
import com.kotkina.userapplicationssystem.entities.User;
import com.kotkina.userapplicationssystem.exceptions.AlreadyExistsException;
import com.kotkina.userapplicationssystem.exceptions.DataNotFoundException;
import com.kotkina.userapplicationssystem.repositories.UserRepository;
import com.kotkina.userapplicationssystem.web.models.response.VerifiedDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final VerificationService verificationService;

    public List<User> getUsersAll() {
        return userRepository.findAll();
    }

    public User addRoleToUser(String userPhone, RoleType roleType) {
        User user = getUserByVerifiedPhone(userPhone);

        boolean roleIsAdded = user.getRoles().add(roleType);

        if (!roleIsAdded) throw new AlreadyExistsException("У пользователя уже есть указанная роль.");

        return userRepository.save(user);
    }

    public User getUserByVerifiedPhone(String phone) {
        List<VerifiedDataResponse> verifiedDatas = verificationService.getVerifiedPhone(phone);
        for (VerifiedDataResponse data : verifiedDatas) {
            User user = userRepository.findUserByCountryCodeAndCityCodeAndPhoneNumber(data.getCountryCode(), data.getCityCode(), data.getNumber())
                    .orElse(null);
            if (user != null) return user;
        }
        throw new DataNotFoundException("Не удалось верифицировать телефон / Пользователь с указанным телефоном не найден.");
    }
}

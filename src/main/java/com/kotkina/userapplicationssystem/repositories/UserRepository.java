package com.kotkina.userapplicationssystem.repositories;

import com.kotkina.userapplicationssystem.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Override
    List<User> findAll();

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByCountryCodeAndCityCodeAndPhoneNumber(Integer countryCode, Integer cityCode, String phoneNumber);
}

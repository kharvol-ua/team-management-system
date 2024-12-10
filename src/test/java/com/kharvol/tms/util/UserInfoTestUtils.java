package com.kharvol.tms.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kharvol.tms.domain.UserInfoDto;
import com.kharvol.tms.persistence.model.UserInfo;
import com.kharvol.tms.persistence.repository.UserInfoRepository;
import com.kharvol.tms.service.UserInfoService;
import java.time.LocalDate;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserInfoTestUtils implements TestUtils<UserInfoDto, UserInfo, UserInfoService, UserInfoRepository> {

    public static final String DEFAULT_USERNAME = "test.kharvoll";
    public static final String DEFAULT_PASSWORD = "Sometestpassword1";

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Override
    public UserInfoDto buildDefaultDto() {


        return UserInfoDto.builder()
                .username(DEFAULT_USERNAME)
                .password(DEFAULT_PASSWORD)
                .dateOfBirth(LocalDate.of(2023, 8, 17))
                .firstName("Kharvol")
                .lastName("Kharkiv")
                .status(UserInfoService.STATUS_ACTIVE)
                .build();
    }

    @Override
    public UserInfoDto buildAndSaveDefaultDto() {
        return getService().save(buildDefaultDto());
    }

    @Override
    public UserInfo buildDefaultModel() {
        return UserInfo.builder()
                .username(DEFAULT_USERNAME)
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .dateOfBirth(LocalDate.of(2023, 8, 17))
                .firstName("Kharvol")
                .lastName("Kharkiv")
                .status(UserInfoService.STATUS_ACTIVE)
                .build();
    }

    @Override
    public UserInfoDto buildDtoWithoutRequiredFields() {
        return UserInfoDto.builder()
                .password(DEFAULT_PASSWORD)
                .dateOfBirth(LocalDate.of(2023, 8, 17))
                .firstName("Kharvol")
                .lastName("Kharkiv")
                .status(UserInfoService.STATUS_ACTIVE)
                .build();
    }

    @Override
    public UserInfoDto buildUniqueDto() {
        return UserInfoDto.builder()
                .username(RandomStringUtils.secure().nextAlphanumeric(8, 21))
                .password(DEFAULT_PASSWORD)
                .dateOfBirth(LocalDate.of(2023, 8, 17))
                .firstName("Kharvol")
                .lastName("Kharkiv")
                .status(UserInfoService.STATUS_ACTIVE)
                .build();
    }

    @Override
    public UserInfoDto buildDtoToUpdate() {
        return UserInfoDto.builder()
                .username(DEFAULT_USERNAME)
                .firstName(RandomStringUtils.secure().nextAlphanumeric(8, 21))
                .lastName(RandomStringUtils.secure().nextAlphanumeric(8, 21))
                .dateOfBirth(LocalDate.of(1995, 3, 17))
                .status(UserInfoService.STATUS_INACTIVE)
                .nickname(RandomStringUtils.secure().nextAlphanumeric(8, 21))
                .build();
    }

    @Override
    public JsonNode buildJsonToPatch() {
        ObjectNode userInfoToPatch = JsonNodeFactory.instance.objectNode();
        userInfoToPatch.put("firstName", RandomStringUtils.secure().nextAlphanumeric(8, 21));
        userInfoToPatch.put("lastName", RandomStringUtils.secure().nextAlphanumeric(8, 21));
        userInfoToPatch.put("dateOfBirth", LocalDate.of(1995, 3, 17).toString());

        return userInfoToPatch;
    }

    @Override
    public UserInfoDto buildAndSaveUniqueDto() {
        return getService().save(buildUniqueDto());
    }

    @Override
    public UserInfoRepository getRepository() {
        return userInfoRepository;
    }

    @Override
    public UserInfoService getService() {
        return userInfoService;
    }
}

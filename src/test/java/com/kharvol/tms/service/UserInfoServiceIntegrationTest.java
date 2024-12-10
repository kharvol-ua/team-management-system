package com.kharvol.tms.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kharvol.tms.domain.UserInfoDto;
import com.kharvol.tms.exception.ErrorMessageCode;
import com.kharvol.tms.exception.InvalidValueException;
import com.kharvol.tms.persistence.model.UserInfo;
import com.kharvol.tms.util.TestUtils;
import com.kharvol.tms.util.UserInfoTestUtils;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource("classpath:/i18n/error_en.properties")
@TestPropertySource("classpath:/i18n/validation_en.properties")
public class UserInfoServiceIntegrationTest extends AbstractServiceIntegrationTest<UserInfoDto, UserInfo> {

    @Autowired
    private UserInfoTestUtils userInfoTestUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void whenPatchFirstNameAndFindById_shouldReturnPatchedUser() {
        UserInfoService service = userInfoTestUtils.getService();

        UserInfoDto userInfoDto = userInfoTestUtils.buildAndSaveDefaultDto();

        String expectedFirstName = RandomStringUtils.secure().nextAlphabetic(7);

        ObjectNode userInfoPatch = JsonNodeFactory.instance.objectNode();
        userInfoPatch.put("firstName", expectedFirstName);


        service.patch(userInfoDto.getId(), userInfoPatch);
        Optional<UserInfoDto> patchedUserInfoOpt = service.findById(userInfoDto.getId());

        assertThat(patchedUserInfoOpt).isPresent();

        UserInfoDto patchedUserInfo = patchedUserInfoOpt.get();

        assertThat(patchedUserInfo).extracting("firstName").isEqualTo(expectedFirstName);
    }

    @Test
    public void whenSaveUser_passwordShouldBeEncrypted() {
        UserInfoDto userInfoDto = userInfoTestUtils.buildDefaultDto();
        UserInfoDto savedUserInfoDto = userInfoTestUtils.getService().save(userInfoDto);

        Optional<UserInfo> foundUserInfoOpt = userInfoTestUtils.getRepository().findById(savedUserInfoDto.getId());

        assertThat(foundUserInfoOpt).isPresent();
        UserInfo foundUserInfo = foundUserInfoOpt.get();

        assertThat(passwordEncoder.matches(userInfoDto.getPassword(), foundUserInfo.getPassword())).isTrue();
    }

    @Test
    public void whenSaveUserWIthNotAllowedStatus_shouldThrowException() {
        final String notAllowedStatus = "FAKE_STATUS";

        UserInfoDto userInfoDto = userInfoTestUtils.buildDefaultDto();
        userInfoDto.setStatus(notAllowedStatus);

        assertThatThrownBy(() -> userInfoTestUtils.getService().save(userInfoDto))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(messageService.getMessage(ErrorMessageCode.USER_NOT_ALLOWED_STATUS).formatted(notAllowedStatus, UserInfoService.ALLOWED_STATUSES));
    }

    @Test
    public void whenUpdateUserWithNotAllowedStatus_shouldThrowException() {
        final String notAllowedStatus = "FAKE_STATUS";

        UserInfoDto userInfoDto = userInfoTestUtils.buildAndSaveDefaultDto();
        userInfoDto.setStatus(notAllowedStatus);

        assertThatThrownBy(() -> userInfoTestUtils.getService().update(userInfoDto.getId(), userInfoDto))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(messageService.getMessage(ErrorMessageCode.USER_NOT_ALLOWED_STATUS).formatted(notAllowedStatus, UserInfoService.ALLOWED_STATUSES));
    }

    @Test
    public void whenPatchUserWithNotAllowedStatus_shouldThrowException() {
        final String notAllowedStatus = "FAKE_STATUS";

        UserInfoDto userInfoDto = userInfoTestUtils.buildAndSaveDefaultDto();

        ObjectNode userInfoPatchJson = JsonNodeFactory.instance.objectNode();
        userInfoPatchJson.put("status", notAllowedStatus);

        assertThatThrownBy(() -> userInfoTestUtils.getService().patch(userInfoDto.getId(), userInfoPatchJson))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(messageService.getMessage(ErrorMessageCode.USER_NOT_ALLOWED_STATUS).formatted(notAllowedStatus, UserInfoService.ALLOWED_STATUSES));
    }

    @Test
    public void whenFindUserById_shouldReturnUserDetailsWithoutPassword() {
        UserInfoService service = userInfoTestUtils.getService();
        UserInfoDto userInfoDto = userInfoTestUtils.buildAndSaveDefaultDto();

        Optional<UserInfoDto> foundUserInfoDtoOpt = service.findById(userInfoDto.getId());

        assertThat(foundUserInfoDtoOpt).isPresent();

        UserInfoDto foundUserInfo = foundUserInfoDtoOpt.get();
        assertThat(foundUserInfo.getPassword()).isNull();
    }

    @Override
    protected <S extends BaseService<UserInfoDto>, R extends JpaRepository<UserInfo, String>> TestUtils<UserInfoDto, UserInfo, S, R> getTestUtils() {
        return (TestUtils<UserInfoDto, UserInfo, S, R>) userInfoTestUtils;
    }

    @Override
    protected boolean supportsUniqueConstraint() {
        return true;
    }

    @Override
    protected String[] getFieldsToIgnore() {
        return Stream.concat(Arrays.stream(FIELDS_TO_IGNORE), Arrays.stream(new String[]{"password"}))
                .toArray(String[]::new);
    }
}

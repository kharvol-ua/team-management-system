package com.kharvol.tms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.kharvol.tms.domain.UserInfoDto;
import com.kharvol.tms.domain.mapping.UserInfoMapper;
import com.kharvol.tms.exception.ErrorMessageCode;
import com.kharvol.tms.exception.InvalidValueException;
import com.kharvol.tms.persistence.model.UserInfo;
import com.kharvol.tms.persistence.repository.UserInfoRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Validator;
import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserInfoService extends AbstractBaseService<UserInfoDto, UserInfo> {

    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_INACTIVE = "Inactive";

    public static final List<String> ALLOWED_STATUSES = List.of(STATUS_ACTIVE, STATUS_INACTIVE);

    private final UserInfoMapper userInfoMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoRepository userInfoRepository;

    public UserInfoService(Validator validator,
                           MessageService messageService,
                           UserInfoMapper userInfoMapper,
                           PasswordEncoder passwordEncoder,
                           UserInfoRepository userInfoRepository) {
        super(validator, messageService, userInfoRepository);
        this.userInfoMapper = userInfoMapper;
        this.passwordEncoder = passwordEncoder;
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public void validateOnExist(String id) {

        if (!userInfoRepository.existsById(id)) {
            throw new EntityNotFoundException(messageService.getMessage(ErrorMessageCode.USER_WITH_ID_DOES_NOT_EXIST));
        }
    }

    @Override
    public void validateOnDuplicate(UserInfoDto dto) {
        String username = dto.getUsername();

        if (userInfoRepository.existsByUsername(username)) {
            throw new EntityExistsException(messageService.getMessage(ErrorMessageCode.USER_WITH_USERNAME_ALREADY_EXISTS).formatted(username));
        }

    }

    @Override
    public void validateOnDuplicate(UserInfoDto dto, String id) {
        String username = dto.getUsername();

        if (userInfoRepository.existsByUsernameAndIdNot(username, id)) {
            throw new EntityExistsException(messageService.getMessage(ErrorMessageCode.USER_WITH_USERNAME_ALREADY_EXISTS).formatted(username));
        }
    }

    @Override
    protected void beforeSave(UserInfoDto dto, UserInfo model) {
        validateStatus(dto);
        model.setPassword(passwordEncoder.encode(dto.getPassword()));
    }

    @Override
    protected void beforeUpdate(UserInfoDto dto, UserInfo model) {
        validateStatus(dto);
    }

    @Override
    protected void validatePatched(UserInfoDto patchedDto, Class<?>... groups) {
        super.validatePatched(patchedDto, groups);
        validateStatus(patchedDto);
    }

    @Override
    protected UserInfo convertToModel(UserInfoDto dto) {
        return userInfoMapper.toUserInfo(dto);
    }

    @Override
    protected UserInfoDto convertToDto(UserInfo model) {
        return userInfoMapper.toUserInfoDto(model);
    }

    @Override
    protected UserInfoDto convertToDto(JsonNode jsonNode) {
        try {
            return OBJECT_MAPPER.treeToValue(jsonNode, UserInfoDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected UserInfo updateFromDto(UserInfo model, UserInfoDto dto) {
        return userInfoMapper.updateFromUserInfoDto(model, dto);
    }

    @Override
    protected UserInfo patchFromDto(UserInfo model, UserInfoDto dto) {
        return userInfoMapper.patchFromUserInfoDto(model, dto);
    }

    private void validateStatus(UserInfoDto userInfoDto) {
        String status = userInfoDto.getStatus();

        if (!ALLOWED_STATUSES.contains(status)) {
            throw new InvalidValueException(messageService.getMessage(ErrorMessageCode.USER_NOT_ALLOWED_STATUS).formatted(status, ALLOWED_STATUSES));
        }
    }
}

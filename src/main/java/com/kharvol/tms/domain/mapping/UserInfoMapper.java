package com.kharvol.tms.domain.mapping;

import com.kharvol.tms.domain.UserInfoDto;
import com.kharvol.tms.persistence.model.UserInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = BaseMapper.class)
public abstract class UserInfoMapper {

    @InheritConfiguration(name = "anyModelToDto")
    @Mapping(target = "password", ignore = true)
    public abstract UserInfoDto toUserInfoDto(UserInfo userInfo);

    @InheritConfiguration(name = "anyDtoToModel")
    @Mapping(target = "password", ignore = true)
    public abstract UserInfo toUserInfo(UserInfoDto userInfoDto);

    @InheritConfiguration(name = "toUserInfo")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ON_IMPLICIT_CONVERSION,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    public abstract UserInfo updateFromUserInfoDto(@MappingTarget UserInfo userInfo, UserInfoDto userInfoDto);

    @InheritConfiguration(name = "toUserInfo")
    public abstract UserInfo patchFromUserInfoDto(@MappingTarget UserInfo userInfo, UserInfoDto userInfoDto);

}

package com.kharvol.tms.domain.mapping;

import com.kharvol.tms.domain.BaseDto;
import com.kharvol.tms.persistence.model.BaseModel;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@MapperConfig(
        componentModel = ComponentModel.SPRING,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BaseMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdDate", source = "createdDate")
    @Mapping(target = "modifiedBy", source = "modifiedBy")
    @Mapping(target = "modifiedDate", source = "modifiedDate")
    BaseDto anyModelToDto(BaseModel baseModel);

    @Mapping(target = "id", source = "id", ignore = true)
    @Mapping(target = "createdBy", source = "createdBy", ignore = true)
    @Mapping(target = "createdDate", source = "createdDate", ignore = true)
    @Mapping(target = "modifiedBy", source = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedDate", source = "modifiedDate", ignore = true)
    BaseModel anyDtoToModel(BaseDto baseDto);

}

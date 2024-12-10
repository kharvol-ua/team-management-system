package com.kharvol.tms.service;


import com.kharvol.tms.domain.BaseDto;

public interface ValidationService<D extends BaseDto> {

    void validateOnExist(String id);

    void validateOnDuplicate(D dto);

    void validateOnDuplicate(D dto, String id);

}

package com.kharvol.tms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.kharvol.tms.domain.BaseDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BaseService<D extends BaseDto> {

    D save(@Valid D dto);

    Optional<D> findById(String id);

    Page<D> findAll(Pageable pageable);

    List<D> findAll();

    void delete(String id);

    D update(String id, @Valid D dto);

    D patch(String id, JsonNode jsonNode);
}

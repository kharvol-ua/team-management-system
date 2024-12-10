package com.kharvol.tms.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.kharvol.tms.domain.BaseDto;
import com.kharvol.tms.persistence.model.BaseModel;
import com.kharvol.tms.service.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestUtils<
        D extends BaseDto,
        M extends BaseModel,
        S extends BaseService<D>,
        R extends JpaRepository<M, String>> {

    D buildDefaultDto();

    D buildAndSaveDefaultDto();

    M buildDefaultModel();

    D buildDtoWithoutRequiredFields();

    D buildUniqueDto();

    D buildAndSaveUniqueDto();

    D buildDtoToUpdate();

    JsonNode buildJsonToPatch();

    R getRepository();

    S getService();

}

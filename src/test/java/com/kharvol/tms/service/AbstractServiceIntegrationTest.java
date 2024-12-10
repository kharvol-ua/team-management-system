package com.kharvol.tms.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kharvol.tms.domain.BaseDto;
import com.kharvol.tms.persistence.model.BaseModel;
import com.kharvol.tms.util.TestUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@TestPropertySource(locations = "classpath:i18n/error.properties")
@TestPropertySource(locations = "classpath:i18n/validation.properties")
@Transactional
public abstract class AbstractServiceIntegrationTest<D extends BaseDto, M extends BaseModel> implements ServiceIntegrationTest {

    protected static final String FAKE_ID = "FAKE_ID";
    protected static final String[] FIELDS_TO_IGNORE = {"id", "createdBy", "createdDate", "modifiedBy", "modifiedDate"};

    @Autowired
    protected MessageService messageService;

    @Test
    @Override
    public void whenSave_shouldSaveAndReturnWithId() {
        BaseService<D> service = getTestUtils().getService();

        D dto = getTestUtils().buildDefaultDto();
        D savedDto = service.save(dto);

        assertThat(savedDto.getId()).isNotNull();
    }

    @Test
    @Override
    public void whenSaveWithoutRequiredFields_shouldThrowException() {
        BaseService<D> service = getTestUtils().getService();

        D dtoWithoutRequiredFields = getTestUtils().buildDtoWithoutRequiredFields();

        assertThatThrownBy(() -> service.save(dtoWithoutRequiredFields))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @Override
    public void whenSave_shouldValidateOnUnique() {
        if (!supportsUniqueConstraint()) {
            return;
        }

        BaseService<D> service = getTestUtils().getService();

        D defaultDto = getTestUtils().buildDefaultDto();
        D savedDefaultDto = service.save(defaultDto);

        assertThat(savedDefaultDto.getId()).isNotNull();

        assertThatThrownBy(() -> service.save(defaultDto))
                .isInstanceOf(EntityExistsException.class);
    }

    @Test
    @Override
    public void whenFindByNonExistentId_shouldReturnEmpty() {
        BaseService<D> service = getTestUtils().getService();

        service.findById(FAKE_ID);

        assertThat(service.findById(FAKE_ID)).isEmpty();
    }

    @Test
    @Override
    public void whenFindById_shouldReturnSavedEntity() {
        BaseService<D> service = getTestUtils().getService();

        D dto = getTestUtils().buildDefaultDto();
        D savedDto = service.save(dto);

        Optional<D> foundDtoOpt = service.findById(savedDto.getId());
        assertThat(foundDtoOpt).isPresent();

        D foundDto = foundDtoOpt.get();
        assertThat(foundDto.getId()).isEqualTo(savedDto.getId());
    }

    @Test
    @Override
    public void whenGetAll_shouldReturnEmptyList() {
        BaseService<D> service = getTestUtils().getService();

        List<D> allDtos = service.findAll();

        assertThat(allDtos).isEmpty();
    }

    @Test
    @Override
    public void whenGetAll_shouldReturnNonEmptyList() {
        BaseService<D> service = getTestUtils().getService();

        D savedUniqueDto1 = getTestUtils().buildAndSaveUniqueDto();
        D savedUniqueDto2 = getTestUtils().buildAndSaveUniqueDto();
        D savedUniqueDto3 = getTestUtils().buildAndSaveUniqueDto();
        D savedUniqueDto4 = getTestUtils().buildAndSaveUniqueDto();
        D savedUniqueDto5 = getTestUtils().buildAndSaveUniqueDto();

        List<D> savedUniqueDtos = List.of(
                savedUniqueDto1,
                savedUniqueDto2,
                savedUniqueDto3,
                savedUniqueDto4,
                savedUniqueDto5
        );

        List<D> foundDtos = service.findAll();

        assertThat(foundDtos).hasSameSizeAs(savedUniqueDtos);
        assertThat(foundDtos).hasSameElementsAs(savedUniqueDtos);
    }

    @Test
    @Override
    public void whenDeleteByNonExistentId_shouldThrowException() {
        BaseService<D> service = getTestUtils().getService();

        assertThatThrownBy(() -> service.delete(FAKE_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @Override
    public void whenDeleteById_shouldDeleteEntity() {
        BaseService<D> service = getTestUtils().getService();

        D savedDefaultDto = getTestUtils().buildAndSaveDefaultDto();
        service.delete(savedDefaultDto.getId());

        Optional<D> foundDeletedDtoOpt = service.findById(savedDefaultDto.getId());
        assertThat(foundDeletedDtoOpt).isEmpty();
    }

    @Test
    @Override
    public void whenUpdateByNonExistentId_shouldThrowException() {
        BaseService<D> service = getTestUtils().getService();

        assertThatThrownBy(() -> service.update(FAKE_ID, getTestUtils().buildDefaultDto()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @Override
    public void whenUpdateById_shouldUpdateEntity() {
        BaseService<D> service = getTestUtils().getService();

        D savedDefaultDto = getTestUtils().buildAndSaveDefaultDto();
        D dtoToUpdate = getTestUtils().buildDtoToUpdate();

        service.update(savedDefaultDto.getId(), dtoToUpdate);
        Optional<D> updateDtoOpt = service.findById(savedDefaultDto.getId());

        assertThat(updateDtoOpt).isPresent();

        D updatedDto = updateDtoOpt.get();

        assertThat(updatedDto)
                .usingRecursiveComparison()
                .ignoringFields(getFieldsToIgnore())
                .isEqualTo(dtoToUpdate);
    }

    @Test
    @Override
    public void whenPatchByNonExistentId_shouldThrowException() {
        BaseService<D> service = getTestUtils().getService();

        assertThatThrownBy(() -> service.patch(FAKE_ID, getTestUtils().buildJsonToPatch()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @Override
    public void whenPatchById_shouldPatchEntity() {
        BaseService<D> service = getTestUtils().getService();

        D savedDefaultDto = getTestUtils().buildAndSaveDefaultDto();
        JsonNode jsonToPatch = getTestUtils().buildJsonToPatch();

        service.patch(savedDefaultDto.getId(), jsonToPatch);
        Optional<D> updateDtoOpt = service.findById(savedDefaultDto.getId());

        assertThat(updateDtoOpt).isPresent();

        D patchedDto = updateDtoOpt.get();

        var patchedFieldNames = new ArrayList<String>();
        var patchedFieldValues = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        jsonToPatch.fields().forEachRemaining(entry -> {
            patchedFieldNames.add(entry.getKey());
            JsonNode value = entry.getValue();

            if (value.isTextual()) {
                String textValue = value.asText();

                var classes = List.of(LocalDate.class, LocalDateTime.class, OffsetDateTime.class);

                boolean parsedToAnyDate = false;

                for (var aClass : classes) {
                    try {
                        patchedFieldValues.add(objectMapper.treeToValue(value, aClass));
                        parsedToAnyDate = true;
                    } catch (JsonProcessingException ignored) {}
                }

                if (!parsedToAnyDate) {
                    patchedFieldValues.add(textValue);
                }
            } else {
                patchedFieldValues.add(objectMapper.convertValue(value, new TypeReference<>() {}));
            }
        });

        assertThat(patchedDto)
                .extracting(patchedFieldNames.toArray(new String[0]))
                .containsExactly(patchedFieldValues.toArray(new Object[0]));
    }

    protected abstract <S extends BaseService<D>,
                        R extends JpaRepository<M, String>>
    TestUtils<D, M, S, R> getTestUtils();

    protected abstract boolean supportsUniqueConstraint();

    protected abstract String[] getFieldsToIgnore();
}

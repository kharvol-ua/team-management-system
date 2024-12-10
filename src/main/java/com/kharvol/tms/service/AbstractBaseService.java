package com.kharvol.tms.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kharvol.tms.domain.BaseDto;
import com.kharvol.tms.persistence.model.BaseModel;
import com.kharvol.tms.validation.group.OnCreate;
import com.kharvol.tms.validation.group.OnPatch;
import com.kharvol.tms.validation.group.OnUpdate;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Log4j2
@Validated
@Transactional
public abstract class AbstractBaseService<D extends BaseDto, M extends BaseModel> implements BaseService<D>, ValidationService<D> {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    protected final Validator validator;
    protected final MessageService messageService;
    private final JpaRepository<M, String> repository;

    public AbstractBaseService(Validator validator,
                               MessageService messageService,
                               JpaRepository<M, String> repository) {
        this.validator = validator;
        this.messageService = messageService;
        this.repository = repository;
    }

    @Override
    @Validated({OnCreate.class, Default.class})
    public D save(@Valid D dto) {
        M model = convertToModel(dto);

        validateOnDuplicate(dto);

        beforeSave(dto, model);
        M savedModel = repository.save(model);
        afterSave(dto, savedModel);

        return convertToDto(savedModel);
    }

    @Override
    public Optional<D> findById(String id) {
        return repository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public Page<D> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public List<D> findAll() {
        return repository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public void delete(String id) {
        validateOnExist(id);

        beforeDelete(id);
        repository.deleteById(id);
        afterDelete(id);
    }

    @Override
    @Validated({OnUpdate.class, Default.class})
    public D update(String id, @Valid D dto) {
        validateOnExist(id);

        M model = repository.findById(id).get();

        beforeUpdate(dto, model);
        M updatedModel = updateFromDto(model, dto);
        afterUpdate(dto, updatedModel);

        return convertToDto(updatedModel);
    }

    @Override
    public D patch(String id, JsonNode jsonNode) {
        validateOnExist(id);

        M model = repository.findById(id).get();
        beforePatch(jsonNode, model);
        model = setNullFields(model, jsonNode);
        D dto = convertToDto(jsonNode);
        M patchedModel = patchFromDto(model, dto);
        afterPatch(jsonNode, patchedModel);

        D patchedDto = convertToDto(patchedModel);

        validatePatched(patchedDto, OnPatch.class, Default.class);

        return patchedDto;
    }

    protected void validatePatched(D patchedDto, Class<?>... groups) {
        Set<ConstraintViolation<D>> constraintViolations = validator.validate(patchedDto, groups);

        if (!constraintViolations.isEmpty()) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    protected void beforeSave(D dto, M model) {}

    protected void afterSave(D dto, M savedModel) {}

    protected void beforeUpdate(D dto, M model) {}

    protected void afterUpdate(D dto, M updatedModel) {}

    protected void beforePatch(JsonNode jsonNode, M model) {}

    protected void afterPatch(JsonNode jsonNode, M model) {}

    protected void beforeDelete(String id) {}

    protected void afterDelete(String id) {}

    protected M setNullFields(M model, JsonNode jsonNode) {

        Iterator<Entry<String, JsonNode>> fields = jsonNode.fields();

        while (fields.hasNext()) {
            Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode fieldValue = field.getValue();

            if (fieldValue == null || fieldValue.isNull() || (fieldValue.isTextual() && fieldValue.asText().equals("null"))) {
                Field modelField = FieldUtils.getField(model.getClass(), fieldName);
                modelField.setAccessible(true);
                try {
                    modelField.set(model, null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return model;
    }

    protected abstract M convertToModel(D dto);

    protected abstract D convertToDto(M model);

    protected abstract D convertToDto(JsonNode jsonNode);

    protected abstract M updateFromDto(M model, D dto);

    protected abstract M patchFromDto(M model, D dto);
}

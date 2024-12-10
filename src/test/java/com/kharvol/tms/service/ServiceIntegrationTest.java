package com.kharvol.tms.service;

public interface ServiceIntegrationTest {

    void whenSave_shouldSaveAndReturnWithId();

    void whenSaveWithoutRequiredFields_shouldThrowException();

    void whenSave_shouldValidateOnUnique();

    void whenFindByNonExistentId_shouldReturnEmpty();

    void whenFindById_shouldReturnSavedEntity();

    void whenGetAll_shouldReturnEmptyList();

    void whenGetAll_shouldReturnNonEmptyList();

    void whenDeleteByNonExistentId_shouldThrowException();

    void whenDeleteById_shouldDeleteEntity();

    void whenUpdateByNonExistentId_shouldThrowException();

    void whenUpdateById_shouldUpdateEntity();

    void whenPatchByNonExistentId_shouldThrowException();

    void whenPatchById_shouldPatchEntity();

}

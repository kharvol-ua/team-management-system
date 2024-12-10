package com.kharvol.tms.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageSourceAccessor messageSourceAccessor;

    public MessageService(@Qualifier("errorMessageSource") MessageSource messageSource) {

        this.messageSourceAccessor = new MessageSourceAccessor(messageSource);
    }

    public String getMessage(String code) {
        return messageSourceAccessor.getMessage(code, code, LocaleContextHolder.getLocale());
    }
}

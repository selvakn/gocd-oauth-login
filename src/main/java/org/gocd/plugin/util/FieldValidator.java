package org.gocd.plugin.util;

import java.util.Map;

@FunctionalInterface
public interface FieldValidator {
    void validate(Map<String, Object> fieldValidation);
}

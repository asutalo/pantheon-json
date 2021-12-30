package com.eu.at_it.pantheon.json.validation;

import java.util.Map;
import java.util.function.Predicate;

/**
 * for future use in validation, for example to confirm old password before updating to new
 */
public interface ParamValidation extends Predicate<Map<String, Object>> {
}

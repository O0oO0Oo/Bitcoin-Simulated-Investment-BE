package com.cryptocurrency.investment.config.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<Enum, CharSequence> {

    private Class<? extends java.lang.Enum> enumClass;
    private boolean ignoreCase;

    @Override
    public void initialize(Enum enumAnnotation) {
        this.enumClass = enumAnnotation.enumClass();
        this.ignoreCase = enumAnnotation.ignoreCase();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            java.lang.Enum<?> enumValue = java.lang.Enum.valueOf(enumClass, value.toString());
            if (ignoreCase && !enumValue.name().equalsIgnoreCase(value.toString())) {
                return false;
            }
        } catch (IllegalArgumentException ex) {
            return false;
        }

        return true;
    }
}

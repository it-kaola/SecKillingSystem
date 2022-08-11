package com.bjpowernode.vo;

import com.bjpowernode.utils.ValidatorPhoneUtils;
import com.bjpowernode.validator.IsMobile;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        // 判断是否为必填字段
        if(required){
            return ValidatorPhoneUtils.isLegalPhone(value); // 进行手机合法性的校验
        }else{
            if(StringUtils.isEmpty(value)){
                return true;
            }else{
                return ValidatorPhoneUtils.isLegalPhone(value); // 进行手机合法性的校验
            }
        }
    }
}

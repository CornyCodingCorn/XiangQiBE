package com.XiangQi.XiangQiBE.Components;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

@Component
public class Validator {
  private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  private final javax.validation.Validator validator = factory.getValidator();

  public void validate(Object object) throws ValidationException {
    var errors = validator.validate(object);
    if (errors.size() > 0) {
      String message = "";
      for (var it = errors.iterator(); it.hasNext();) {
        var error = it.next();
        message += "Path " + error.getPropertyPath() + ": " + error.getMessage();
        if (it.hasNext()) {
          message += " | ";
        }
      }

      var exception = new ValidationException(message);
      throw exception;
    }
  }
}

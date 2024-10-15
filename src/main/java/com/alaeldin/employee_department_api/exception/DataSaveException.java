package com.alaeldin.employee_department_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DataSaveException extends RuntimeException
{
    public DataSaveException(String message) {
        super(message);

    }
}

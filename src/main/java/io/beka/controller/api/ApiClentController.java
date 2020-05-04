package io.beka.controller.api;

import io.beka.exception.InvalidRequestException;
import io.beka.model.dto.ApiClientDto;
import io.beka.service.ApiClientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping(path = "/api/apiClient")
@AllArgsConstructor
@RestController
public class ApiClentController {
    private final ApiClientService apiClientService;


    @PostMapping
    public ResponseEntity<Void> createApiClient(@Valid @RequestBody ApiClientDto apiClientDto, BindingResult bindingResult) {
        validate(apiClientDto, bindingResult);
//        apiClientService.save(apiClientDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    private void validate(@Valid @RequestBody ApiClientDto dto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }

//        if (StringUtils.isEmpty(dto.getApiName())) {
//            bindingResult.rejectValue("apiName", "REQUIRED", "can't be empty");
//        }
//        if(apiClientService.findByApiName(dto.getApiName()).isPresent()){
//            bindingResult.rejectValue("apiName", "DUPLICATED", "duplicate api name");
//        }
//
//        if (bindingResult.hasErrors()) {
//            throw new InvalidRequestException(bindingResult);
//        }

    }

}

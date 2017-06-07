package com.wasp.api.institution.gateway.controller

import com.paymentcomponents.common.request.FinancialInstitutionRequest
import com.paymentcomponents.common.response.FinancialInstitutionResponse
import com.wasp.api.institution.gateway.service.FinancialInstitutionService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.client.RestTemplate

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by ushaheu on 6/5/17.
 */
class FinancialInstitutionController {

    private RestTemplate restTemplate
    private FinancialInstitutionService financialInstitutionService

    @Autowired
    FinancialInstitutionController(RestTemplate restTemplate, FinancialInstitutionService financialInstitutionService) {
        this.restTemplate = restTemplate
        this.financialInstitutionService = financialInstitutionService
    }

    @RequestMapping(value = "/v1/getFinancialInstitutions", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "getFinancialInstitutions", notes = "Get a List of Financial Institutions")
    @ApiResponses([
            @ApiResponse(code = 200, message = "OK", response = FinancialInstitutionResponse.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Error.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    ])
    public FinancialInstitutionResponse getFinancialInstitutions(
            @RequestBody FinancialInstitutionRequest financialInstitutionRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return financialInstitutionService.listFinancialInstitutions(financialInstitutionRequest, httpServletRequest.servletPath)
    }
}

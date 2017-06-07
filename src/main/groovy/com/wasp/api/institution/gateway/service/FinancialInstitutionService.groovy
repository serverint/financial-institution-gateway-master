package com.wasp.api.institution.gateway.service

import com.paymentcomponents.common.Constants
import com.paymentcomponents.common.exceptions.WaspApiValidationException
import com.paymentcomponents.common.models.Bank
import com.paymentcomponents.common.request.FinancialInstitutionRequest
import com.paymentcomponents.common.request.subobjects.TransactionLocationInformation
import com.paymentcomponents.common.response.FinancialInstitutionResponse
import com.wasp.api.institution.gateway.kafka.interfaces.LogsChannel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate

/**
 * Created by ushaheu on 6/3/17.
 */
class FinancialInstitutionService {

    private RestTemplate restTemplate
    private LogsChannel

    @Autowired
    FinancialInstitutionService(RestTemplate restTemplate, LogsChannel) {
        this.restTemplate = restTemplate
        this.LogsChannel = LogsChannel
    }

    private Bank getBankByInstitutionCode(String institutionCode) {
        try {
            Bank bank = restTemplate.exchange("http://application-gateway/banks/{financialInstitutionId}", HttpMethod.GET, null, new ParameterizedTypeReference<Bank>() {
            }, institutionCode)?.body
            return bank
        } catch (Exception e) {
            throw new WaspApiValidationException(Constants.ERROR_CODES.institution_bank_not_found.toString(), "Institution bank with Code $institutionCode not found")
        }
    }

    private static void validationChecks(FinancialInstitutionRequest financialInstitutionRequest) {

        // ---------- value validations ----------
        if (!financialInstitutionRequest.batchNumber || financialInstitutionRequest.batchNumber.is("")) {
            throw new WaspApiValidationException(Constants.ERROR_CODES.constraint_violation.toString(), "Batch Number must be specified.")
        }
        if (!financialInstitutionRequest.channelCode || financialInstitutionRequest.channelCode > 0) {
            throw new WaspApiValidationException(Constants.ERROR_CODES.constraint_violation.toString(), "Channel Code must be specified.")
        }
        if (!financialInstitutionRequest.instructingInstitutionCode || financialInstitutionRequest.instructingInstitutionCode.is("")) {
            throw new WaspApiValidationException(Constants.ERROR_CODES.constraint_violation.toString(), "Instructing Institution Code must be specified.")
        }

    }

    public FinancialInstitutionResponse listFinancialInstitutions(FinancialInstitutionRequest financialInstitutionRequest, String context) {

        validationChecks(financialInstitutionRequest)

        Bank institutingBank = getBankByInstitutionCode(financialInstitutionRequest.instructingInstitutionCode)

        FinancialInstitutionResponse financialInstitutionResponse = new FinancialInstitutionResponse()
        financialInstitutionResponse.instructingInstitutionCode = financialInstitutionRequest.instructingInstitutionCode
        financialInstitutionResponse.channelCode = financialInstitutionRequest.channelCode
        financialInstitutionResponse.batchNumber = financialInstitutionRequest.batchNumber
        financialInstitutionResponse.requestId = financialInstitutionRequest.requestId
        financialInstitutionResponse.numberOfRecords = this.numberOfRecords()
        financialInstitutionResponse.records = getFinancialInstitutionList()
        financialInstitutionResponse.responseCode = Constants.FINANCIAL_INSTITUTION_RESPONSE_CODE_VALUES.SUCCESSFUL_REQUEST.name()

        return financialInstitutionResponse
    }

    private List<Bank> getFinancialInstitutionList(){
        return this.restTemplate.exchange("http://application-gateway/banks/all", HttpMethod.GET, null, new ParameterizedTypeReference<List<Bank>>(){})?.body
    }

    private int numberOfRecords(){
        List<Bank> bankList = this.getFinancialInstitutionList();
        return bankList.isEmpty()? 0 : bankList.size()
    }


}

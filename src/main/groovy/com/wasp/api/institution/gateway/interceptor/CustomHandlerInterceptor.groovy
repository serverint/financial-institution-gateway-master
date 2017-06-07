package com.wasp.api.institution.gateway.interceptor

import com.paymentcomponents.common.Utils
import com.paymentcomponents.common.log.RequestLogger
import com.paymentcomponents.common.models.Log
import com.wasp.api.institution.gateway.kafka.interfaces.LogsChannel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

/**
 * Created by aalexandrakis on 26/04/2017.
 */
@Component
public class CustomHandlerInterceptor implements HandlerInterceptor {
    final protected RequestLogger logger = new RequestLogger(getClass().getName());
    //before the actual handler will be executed
    @Autowired
    LogsChannel logsChannel

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        logger.info("Incoming Request ", httpServletRequest)
        if (httpServletResponse.getStatus() == 200) {
            return true;
        } else {
            addLog(httpServletRequest, httpServletResponse);
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (httpServletResponse.getStatus() != 200 && httpServletResponse.getStatus() != 201) {
            addLog(httpServletRequest, httpServletResponse);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        addLog(httpServletRequest, httpServletResponse);
    }

    private void addLog(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            def log = constructLog(httpServletRequest, httpServletResponse);
            logger.info("Write log to Kafka ", httpServletRequest, log)
            logsChannel.output().send(MessageBuilder.withPayload(log).build())
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static def constructLog(HttpServletRequest request, HttpServletResponse response) {
        HttpServletRequestWrapper req = new HttpServletRequestWrapper(request);
        HttpServletResponseWrapper res = new HttpServletResponseWrapper(response);

        Log log = new Log();

        log.requestHeaders = Utils.getHeadersFromHttpServletRequest(request)
//        log.requestPayload = Utils.getPayloadFromHttpServletRequest(req)
//        log.responsePayload = Utils.getPayloadFromHttpServletResponse(response)

        log.setRemoteAddress(request.getHeader("X-FORWARDED-FOR"));
        if (log.getRemoteAddress() == null) {
            log.setRemoteAddress(request.getRemoteAddr());
        }
        log.setPath(request.getServletPath());
        log.setMethod(request.getMethod().trim());
        log.setHttpStatusCode(String.valueOf(response.getStatus()));

//        ----------------------------- TODO REMOVE LATER -----------------------------
//        log.setResponseErrorCode(request.getAttribute(Constants.DATAMATION_RESPONSE_ERROR_CODE) != null ? request.getAttribute(Constants.DATAMATION_RESPONSE_ERROR_CODE).toString() : null);
//        request.removeAttribute(Constants.DATAMATION_RESPONSE_ERROR_CODE);
//        log.setRequestDate((Date) request.getAttribute("startTime"));

        log.setRequestDate(new Date());
        log.setRequestEndDate(new Date());

        log.setRequestId(UUID.randomUUID().toString().substring(0, 30));
        log.setFinancialInstitutionId("aaa")
//        ----------------------------- TODO REMOVE LATER -----------------------------

        log.setDuration((log.getRequestEndDate().getTime() - log.getRequestDate().getTime()) / 1000.0000);

        return log
    }
}
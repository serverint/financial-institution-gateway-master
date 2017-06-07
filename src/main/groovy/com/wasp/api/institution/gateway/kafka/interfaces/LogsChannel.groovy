package com.wasp.api.institution.gateway.kafka.interfaces

import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel

/**
 * Created by aalexandrakis on 26/04/2017.
 */
public interface LogsChannel {

    @Output(value = "logs")
    MessageChannel output()

}
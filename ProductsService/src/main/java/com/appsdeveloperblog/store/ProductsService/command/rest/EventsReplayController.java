package com.appsdeveloperblog.store.ProductsService.command.rest;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/management")
public class EventsReplayController {

    @Autowired
    private EventProcessingConfiguration eventProcessingConfiguration;

    @PostMapping("/eventProcessor/{processorName}/reset")
    public ResponseEntity replayEvents(@PathVariable String processorName) {
        Optional<TrackingEventProcessor> trackingEventProcessor =
                eventProcessingConfiguration.eventProcessor(processorName, TrackingEventProcessor.class);
        if (trackingEventProcessor.isPresent()) {
            TrackingEventProcessor eventProcessor = trackingEventProcessor.get();
            eventProcessor.shutDown();
            eventProcessor.resetTokens();
            eventProcessor.start();
            return ResponseEntity.ok().body("The event processor with name " + processorName + " has been reset");
        }
        return ResponseEntity.badRequest().body("The event processor with name " + processorName + " is not a tracking event processor. " +
                "Only Tracking event processor is supported");
    }

}

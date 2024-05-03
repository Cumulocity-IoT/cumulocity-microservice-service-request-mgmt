package cumulocity.microservice.service.request.mgmt.service;

import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Service for any microservice runtime related features.
 *
 * @author alexander.pester@softwareag.com
 * @version 1.2.2
 * <p>
 * 03.05.2024
 */
@Service
public class MicroserviceRepresentationService {
    private static final Logger LOG = LoggerFactory.getLogger(MicroserviceRepresentationService.class);

    private final MicroserviceSubscriptionsService subscriptions;
    private final ServiceRequestPriorityService priorityService;
    private final ServiceRequestStatusConfigService statusConfigService;

    public MicroserviceRepresentationService(MicroserviceSubscriptionsService subscriptions, ServiceRequestPriorityService priorityService, ServiceRequestStatusConfigService statusConfigService) {
        this.priorityService = priorityService;
        this.statusConfigService = statusConfigService;
        this.subscriptions = subscriptions;
    }

    /**
     * Event listener for microservice subscription.
     *
     * @param event
     */
    @EventListener
    private void onSubscriptionEvent(final MicroserviceSubscriptionAddedEvent event) {
        String subscriptTenant = event.getCredentials().getTenant();
        subscriptions.runForTenant(subscriptTenant, new Runnable() {

            @Override
            public void run() {
                priorityService.createDefaultPriorityList();
                statusConfigService.createDefaultStatusList();
            }
        });
    }
}

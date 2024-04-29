package cumulocity.microservice.service.request.mgmt.service;

import com.cumulocity.microservice.subscription.model.MicroserviceSubscriptionAddedEvent;
import com.cumulocity.microservice.subscription.service.MicroserviceSubscriptionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Service for add microservice as representation in cumulocity als
 * managedobject with agent fragment in order to store events etc. to it.
 *
 * @author alexander.pester@softwareag.com
 * @version 0.0.1
 * <p>
 * 03.09.2019
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
     * Create default priority and status list for new tenant if not yet directyl exists
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

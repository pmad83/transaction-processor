package pl.pm.transactionprocessor.kubernetes;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoordinationV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Lease;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/*
 * Klasa KubernetesLeaderElection wybiera lidera instancji.
 */
@Component
public class KubernetesLeaderElection {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesLeaderElection.class);
    private final ApiClient client;
    private final CoordinationV1Api coordinationApi;

    private static final String LEASE_NAME = "transaction-leader-election";
    private static final String NAMESPACE = "default";
    private static final String HOSTNAME_ENV = "HOSTNAME";

    public KubernetesLeaderElection() throws IOException {

        if (!isRunningInKubernetes()) {
            logger.info("Pomijanie KubernetesLeaderElection, ponieważ aplikacja nie działa w klastrze Kubernetes");
            this.client = null;
            this.coordinationApi = null;
            return;
        }

        this.client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        this.coordinationApi = new CoordinationV1Api();
    }

    @PostConstruct
    public void init()  {
        if (isRunningInKubernetes()) {
            runLeaderElection();
        }
    }

    public void runLeaderElection()  {
        CoordinationV1Api api = new CoordinationV1Api();
        CoreV1Api coreApi = new CoreV1Api();

        try {
            V1Lease lease = api.readNamespacedLease(LEASE_NAME, NAMESPACE).execute();
            String currentLeader = lease.getSpec().getHolderIdentity();

            // Jeśli nie znaleziono lidera, to ustawiamy na bieżący
            if (currentLeader == null || !isPodRunning(coreApi, currentLeader)) {
                logger.info("Nie znaleziono lidera");
                lease.getSpec().setHolderIdentity(getPodName());
                api.replaceNamespacedLease(LEASE_NAME, NAMESPACE, lease).execute();
                logger.info("Nowy lider: {}", getPodName());
            }  else {
                logger.info("Inna instancja jest liderem: {}", currentLeader);

            }
        } catch (ApiException e) {
            logger.warn("Błąd podczas ustawienia lidera lub ustawiono lidera w innej transakcji");
        }
    }

    public boolean isLeader()  {
        // Sprawdzamy lidera tylko, gdy aplikacja uruchomiona jest w klastrze
        if (isRunningInKubernetes()) {
            String currentLeader = getCurrentLeader();
            return currentLeader != null && currentLeader.equals(getPodName());
        } else {
            return true;
        }
    }

    private String getPodName() {
        String podName = System.getenv(HOSTNAME_ENV);
        return podName;
    }

    private String getCurrentLeader() {
        try {
            V1Lease lease = coordinationApi.readNamespacedLease(LEASE_NAME, NAMESPACE).execute();
            return lease.getSpec().getHolderIdentity();
        } catch (ApiException e) {
            logger.error("Błąd przy pobieraniu lidera", e);
            return null;
        }
    }

    private boolean isPodRunning(CoreV1Api coreApi, String podName) throws ApiException {
        logger.info("Sprawdzanie czy instancja istnieje: {}", podName);

        V1PodList podList = coreApi.listNamespacedPod(NAMESPACE).execute();
        List<String> activePods = podList.getItems().stream()
                .map(pod -> pod.getMetadata().getName())
                .toList();

        return activePods.contains(podName);
    }

    private boolean isRunningInKubernetes() {
        return System.getenv("KUBERNETES_SERVICE_HOST") != null;
    }
}

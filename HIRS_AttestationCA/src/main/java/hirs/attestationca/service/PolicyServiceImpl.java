package hirs.attestationca.service;

import hirs.FilteredRecordsList;
import hirs.attestationca.repository.PolicyRepository;
import hirs.data.persist.policy.Policy;
import hirs.persist.CriteriaModifier;
import hirs.persist.DBManagerException;
import hirs.persist.OrderedQuery;
import hirs.persist.service.DefaultService;
import hirs.persist.service.PolicyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A <code>PolicyServiceImpl</code> manages <code>Policy</code>s. A
 * <code>PolicyServiceImpl</code> is used to store and manage policies. It has
 * support for the basic create, read, update, and delete methods.
 */
@Service
public class PolicyServiceImpl extends DbServiceImpl<Policy> implements DefaultService<Policy>,
        PolicyService, OrderedQuery<Policy> {

    private static final Logger LOGGER = LogManager.getLogger(PolicyServiceImpl.class);
    @Autowired
    private PolicyRepository policyRepository;

    @Override
    public List<Policy> getList() {
        LOGGER.debug("Getting all policies...");

        return getRetryTemplate().execute(new RetryCallback<List<Policy>, DBManagerException>() {
            @Override
            public List<Policy> doWithRetry(final RetryContext context)
                    throws DBManagerException {
                policyRepository.findAll();
                return null;
            }
        });
    }

    @Override
    public void updateElements(final List<Policy> policies) {
        LOGGER.debug("Updating {} certificates...", policies.size());

        policies.stream().forEach((policy) -> {
            if (policy != null) {
                this.updatePolicy(policy, policy.getId());
            }
        });
        policyRepository.flush();
    }

    @Override
    public void deleteObjectById(final UUID uuid) {
        LOGGER.debug("Deleting policy by id: {}", uuid);

        getRetryTemplate().execute(new RetryCallback<Void, DBManagerException>() {
            @Override
            public Void doWithRetry(final RetryContext context)
                    throws DBManagerException {
                policyRepository.deleteById(uuid);
                policyRepository.flush();
                return null;
            }
        });
    }

    @Override
    public Policy savePolicy(final Policy policy) {
        LOGGER.debug("Saving policy: {}", policy);

        return getRetryTemplate().execute(new RetryCallback<Policy, DBManagerException>() {
            @Override
            public Policy doWithRetry(final RetryContext context)
                    throws DBManagerException {
                return policyRepository.save(policy);
            }
        });
    }

    @Override
    public Policy updatePolicy(final Policy policy, final UUID uuid) {
        LOGGER.debug("Updating policy: {}", policy);
        Policy dbPolicy;

        if (uuid == null) {
            LOGGER.debug("Policy not found: {}", policy);
            dbPolicy = policy;
        } else {
            // will not return null, throws and exception
            dbPolicy = policyRepository.getReferenceById(uuid);

            // run through things that aren't equal and update
        }

        return savePolicy(dbPolicy);
    }

    @Override
    public FilteredRecordsList getOrderedList(
            final Class<Policy> clazz, final String columnToOrder,
            final boolean ascending, final int firstResult, final int maxResults,
            final String search, final Map<String, Boolean> searchableColumns)
            throws DBManagerException {
        return null;
    }

    @Override
    public FilteredRecordsList<Policy> getOrderedList(
            final Class<Policy> clazz, final String columnToOrder,
            final boolean ascending, final int firstResult, final int maxResults,
            final String search, final Map<String, Boolean> searchableColumns,
            final CriteriaModifier criteriaModifier)
            throws DBManagerException {
        return null;
    }
}

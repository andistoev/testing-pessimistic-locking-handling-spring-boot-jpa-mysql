package com.andistoev.psmlockingservice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public abstract class CustomizedItemRepositoryContext {

    @Getter
    @Value("${concurrency.pessimisticLocking.requiredToSetLockTimeoutForTestsAtStartup: false}")
    private boolean requiredToSetLockTimeoutForTestsAtStartup;

    @Value("${concurrency.pessimisticLocking.requiredToSetLockTimeoutForEveryQuery: true}")
    private boolean requiredToSetLockTimeoutForEveryQuery;

    @Getter
    @Value("${concurrency.pessimisticLocking.requiredToSetLockTimeoutQueryHint: false}")
    private boolean requiredToSetLockTimeoutQueryHint;

    @Getter
    @Value("${concurrency.pessimisticLocking.delayAtTheEndOfTheQueryForPessimisticLockingTestingInMs: 2000}")
    private long delayAtTheEndOfTheQueryForPessimisticLockingTestingInMs;

    @Getter
    @Value("${concurrency.pessimisticLocking.minimalPossibleLockTimeOutInMs: 1000}")
    private long minimalPossibleLockTimeOutInMs;

    @Getter
    @Value("${concurrency.pessimisticLocking.lockTimeOutInMsForQueryGetItem: 5000}")
    private long lockTimeOutInMsForQueryGetItem;

    protected final EntityManager em;

    protected void setLockTimeout(long timeoutDurationInMs) {
        long timeoutDurationInSec = TimeUnit.MILLISECONDS.toSeconds(timeoutDurationInMs);
        Query query = em.createNativeQuery("set session innodb_lock_wait_timeout = " + timeoutDurationInSec);
        query.executeUpdate();
    }

    protected long getLockTimeout() {
        Query query = em.createNativeQuery("select @@innodb_lock_wait_timeout");
        long timeoutDurationInSec = ((BigInteger) query.getSingleResult()).longValue();
        return TimeUnit.SECONDS.toMillis(timeoutDurationInSec);
    }

    protected Query setLockTimeoutIfRequired(Query query) {
        if (requiredToSetLockTimeoutForEveryQuery) {
            log.info("... set lockTimeOut {} ms through native query ...", getLockTimeOutInMsForQueryGetItem());
            setLockTimeout(getLockTimeOutInMsForQueryGetItem());
        }

        if (requiredToSetLockTimeoutQueryHint) {
            log.info("... set lockTimeOut {} ms through query hint ...", getLockTimeOutInMsForQueryGetItem());
            query.setHint("javax.persistence.lock.timeout", String.valueOf(getLockTimeOutInMsForQueryGetItem()));
        }

        return query;
    }

    protected void insertArtificialDealyAtTheEndOfTheQueryForTestsOnly() {
        // for testing purposes only
    }
}

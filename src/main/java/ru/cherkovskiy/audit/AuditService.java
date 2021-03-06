package ru.cherkovskiy.audit;

import ru.cherkovskiy.domain.history.HistoryData;
import ru.cherkovskiy.domain.history.HistoryFilter;
import ru.cherkovskiy.domain.HttpRequest;
import ru.cherkovskiy.domain.statistics.ServerStatus;
import rx.Observable;


public interface AuditService {

    /**
     * Async save or update request.
     *
     * @param request
     */
    void saveOrUpdate(HttpRequest request);

    /**
     *
     * Subscribe to current server status.
     * Async observer and subscriber.
     *
     * @return shared observable for target of broadcasting of events
     */
    Observable<ServerStatus> getCurrentStatus();

    /**
     * Get history data by history filter.
     * Async observer and subscriber.
     *
     * @param historyFilter
     * @return
     */
    Observable<HistoryData> getHistoryBy(HistoryFilter historyFilter);

    /**
     * Get body as string by uuid of request.
     * Async observer and subscriber.
     *
     * @param uuid
     * @return
     */
    Observable<String> getBodyByUUID(String uuid);

    /**
     * Get error as string by uuid of request.
     * Async observer and subscriber.
     *
     * @param uuid
     * @return
     */
    Observable<String> getErrorByUUID(String uuid);
}

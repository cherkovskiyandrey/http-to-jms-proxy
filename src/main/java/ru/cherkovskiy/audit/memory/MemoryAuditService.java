package ru.cherkovskiy.audit.memory;


import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import ru.cherkovskiy.audit.AuditService;
import ru.cherkovskiy.domain.HttpRequest;
import ru.cherkovskiy.domain.history.HistoryData;
import ru.cherkovskiy.domain.history.HistoryElement;
import ru.cherkovskiy.domain.history.HistoryFilter;
import ru.cherkovskiy.domain.statistics.AggregationStat;
import ru.cherkovskiy.domain.statistics.ServerStatus;
import ru.cherkovskiy.exceptions.HttpException;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import javax.annotation.PreDestroy;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;


@Component
public class MemoryAuditService implements AuditService {
    private final static int HISTORY_SIZE = 100000;
    private final static int MAX_BODY_SIZE = 10000;
    private final static int HISTORY_DROP_SIZE = 100;

    private final ExecutorService executorService;
    private final Map<String, HttpRequest> activeRequests = new HashMap<>(100);
    private final Subscription publisherControl;
    private final ConnectableObservable<ServerStatus> regularStateObserver;
    private final Consumer<HttpRequest> chainOfHandlers;
    private ArrayList<HttpRequest> history = new ArrayList<>(HISTORY_SIZE);
    private long successAmount = 0;
    private long errorAmount = 0;
    private String lastUUIDForCalc;

    public MemoryAuditService() {
        this.executorService = createSingleExecutorServiceByName(MemoryAuditService.class.getName());

        this.regularStateObserver = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(Schedulers.from(executorService))
                .map(i -> getNextServerStatus())
                .observeOn(Schedulers.io())
                .publish();

        this.publisherControl = regularStateObserver.connect();

        this.chainOfHandlers = ((Consumer<HttpRequest>) this::newRequestHandler)
                .andThen(this::updateRequestHandler)
                .andThen(this::finishRequestHandler);
    }

    @PreDestroy
    public void destory() {
        publisherControl.unsubscribe();
    }

    private static ExecutorService createSingleExecutorServiceByName(String name) {
        return Executors.newSingleThreadExecutor(r -> {
            final Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName(name);
            return t;
        });
    }

    private ServerStatus getNextServerStatus() {
        try {
            final List<HttpRequest> requestsForLastCall = getRequestInHistoryFromLastCall(lastUUIDForCalc);

            return new ServerStatus(
                    new AggregationStat(successAmount, activeRequests.size(), errorAmount),
                    calcInputRequestsFrom(requestsForLastCall),
                    calcOutputRequestsFrom(requestsForLastCall)
            );
        } finally {
            if (!history.isEmpty()) {
                lastUUIDForCalc = Lists.reverse(history).get(0).getUUID();
            }
        }
    }

    private long calcOutputRequestsFrom(List<HttpRequest> requestsForLastCall) {
        return requestsForLastCall.stream()
                .filter(state -> state.getStatus() == HttpRequest.State.OK)
                .count();
    }

    private long calcInputRequestsFrom(List<HttpRequest> requestsForLastCall) {
        return activeRequests.size() + requestsForLastCall.size();
    }

    private List<HttpRequest> getRequestInHistoryFromLastCall(String lastUUIDForCalc) {
        final List<HttpRequest> requestsForLastCall = new ArrayList<>();

        final boolean hasFound = Lists.reverse(history)
                .stream()
                .peek(requestsForLastCall::add)
                .anyMatch(state -> state.getUUID().equals(lastUUIDForCalc));

        if (hasFound && !requestsForLastCall.isEmpty()) {
            Lists.reverse(requestsForLastCall).remove(0);
        }

        return requestsForLastCall;
    }

    private void newRequestHandler(HttpRequest request) {
        if (!activeRequests.containsKey(request.getUUID())) {
            activeRequests.put(request.getUUID(), request);
        }
    }

    private void updateRequestHandler(HttpRequest request) {
        final HttpRequest reqInCache = activeRequests.get(request.getUUID());
        if (reqInCache != null &&
                !request.isFinished() &&
                !request.equals(reqInCache)) {
            activeRequests.put(request.getUUID(), request);
        }
    }

    private void finishRequestHandler(HttpRequest request) {
        if (activeRequests.containsKey(request.getUUID()) && request.isFinished()) {
            activeRequests.remove(request.getUUID());
            addToHistory(request);
            if (request.getStatus() == HttpRequest.State.OK) {
                successAmount++;
            } else {
                errorAmount++;
            }
        }
    }

    @Override
    public void saveOrUpdate(HttpRequest request) {
        CompletableFuture.runAsync(() -> {
            chainOfHandlers.accept(request);
        }, executorService);
    }

    private void addToHistory(HttpRequest requestState) {
        manageCapacity();
        history.add(reduceContent(requestState));
    }

    private HttpRequest reduceContent(HttpRequest currentRequest) {
        final byte[] body = currentRequest.getBody();
        if (body.length > MAX_BODY_SIZE) {
            currentRequest = currentRequest.withBody(Arrays.copyOf(body, MAX_BODY_SIZE));
        }
        return currentRequest;
    }

    private void manageCapacity() {
        if (history.size() == HISTORY_SIZE) {
            history = new ArrayList<>(history.subList(HISTORY_DROP_SIZE, history.size()));
        }
    }

    @Override
    public Observable<ServerStatus> getCurrentStatus() {
        return regularStateObserver;
    }

    @Override
    public Observable<HistoryData> getHistoryBy(HistoryFilter historyFilter) {
        return Observable.fromCallable(() -> getHistoryByHelper(historyFilter)
        )
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(Schedulers.computation())
                ;
    }

    @Override
    public Observable<String> getBodyByUUID(String uuid) {
        return Observable.fromCallable(() -> getBodyByUUIDHelper(uuid)
        )
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(Schedulers.computation())
                ;
    }

    private String getBodyByUUIDHelper(String uuid) {
        return history.stream()
                .filter(httpRequest -> httpRequest.getUUID().equalsIgnoreCase(uuid))
                .findFirst()
                .map(HttpRequest::getBodyAsString)
                .orElseThrow(() -> HttpException.of("Document not found or error does not exists", 404));
    }

    @Override
    public Observable<String> getErrorByUUID(String uuid) {
        return Observable.fromCallable(() -> getErrorByUUIDHelper(uuid)
        )
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(Schedulers.computation())
                ;
    }

    private String getErrorByUUIDHelper(String uuid) {
        return history.stream()
                .filter(httpRequest -> httpRequest.getUUID().equalsIgnoreCase(uuid))
                .findFirst()
                .map(HttpRequest::getError)
                .map(throwable -> {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    final PrintStream printStream = new PrintStream(out);
                    throwable.printStackTrace(printStream);
                    return out.toString();
                })
                .orElseThrow(() -> HttpException.of("Document not found or error does not exists", 404));
    }

    private HistoryData getHistoryByHelper(HistoryFilter historyFilter) {
        final List<HttpRequest> candidatesByFilter = candidatesByFilter(historyFilter);

        int curLastPage = candidatesByFilter.size() / historyFilter.getPageSize() +
                (candidatesByFilter.size() % historyFilter.getPageSize() != 0 ? 1 : 0);

        int calculatedPage = Math.min(curLastPage, historyFilter.getPageNumber());
        int skipAmount = calculatedPage != 0 ? (calculatedPage - 1) * historyFilter.getPageSize() : 0;

        final List<HistoryElement> historyElements = candidatesByFilter
                .stream()
                .skip(skipAmount)
                .limit(historyFilter.getPageSize())
                .map(this::toHistoryElement)
                .collect(Collectors.toList());

        return HistoryData.builder()
                .from(historyFilter.getFrom())
                .to(historyFilter.getTo())
                .pageSize(historyFilter.getPageSize())
                .maxPage(curLastPage)
                .pageNumber(calculatedPage)
                .historyElements(historyElements)
                .build();
    }

    private HistoryElement toHistoryElement(HttpRequest httpRequest) {
        return HistoryElement.builder()
                .bodySize(httpRequest.getBody().length)
                .contentType(httpRequest.getContentType().toString())
                .durationOfProxy(Duration.between(
                        httpRequest.getReceivedTimestamp(),
                        httpRequest.getEndOfRequest()).toMillis()
                )
                .receivedTimestamp(httpRequest.getReceivedTimestamp())
                .sourceAddress(httpRequest.getRemoteAddress().toString())
                .UUID(httpRequest.getUUID())
                .status(httpRequest.getStatus() == HttpRequest.State.OK ?
                        HistoryElement.Status.OK : HistoryElement.Status.FAIL)
                .build();
    }

    private List<HttpRequest> candidatesByFilter(HistoryFilter historyFilter) {
        return history.stream()
                .filter(r -> {
                            final LocalDateTime in = r.getReceivedTimestamp();
                            return in.compareTo(historyFilter.getFrom()) >= 0 &&
                                    in.compareTo(historyFilter.getTo()) < 0;
                        }
                )
                .sorted()
                .collect(Collectors.toList());
    }
}

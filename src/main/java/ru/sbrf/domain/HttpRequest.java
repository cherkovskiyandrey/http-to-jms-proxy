package ru.sbrf.domain;

import com.google.common.net.HostAndPort;
import lombok.Value;
import org.apache.commons.lang3.ArrayUtils;
import ratpack.http.MediaType;
import ratpack.http.Request;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Supplier;

@Value
public class HttpRequest implements Comparable<HttpRequest> {

    public enum State {
        NEW,
        OK,
        FAIL;
    }

    private final String UUID;
    private final long contentLength;
    private final HostAndPort localAddress;
    private final HostAndPort remoteAddress;
    private final String uri;
    private final String proto;
    private final MediaType contentType;
    private final String method;
    private final LocalDateTime receivedTimestamp;
    private final byte[] body;
    private final LocalDateTime endOfRequest;
    private final State status;
    private final Throwable error;


    public HttpRequest(Request request) {
        this.UUID = java.util.UUID.randomUUID().toString();
        this.contentLength = request.getContentLength();
        this.localAddress = request.getLocalAddress();
        this.remoteAddress = request.getRemoteAddress();
        this.uri = request.getRawUri();
        this.proto = request.getProtocol();
        this.contentType = request.getContentType();
        this.method = request.getMethod().getName();
        this.receivedTimestamp = LocalDateTime.ofInstant(request.getTimestamp(), ZoneId.systemDefault());
        this.body = ArrayUtils.EMPTY_BYTE_ARRAY;
        endOfRequest = null;
        status = State.NEW;
        error = null;
    }

    private static <T> T getOrDefault(Supplier<T> rewriteVal, T defaultVal) {
        return rewriteVal != null ? rewriteVal.get() : defaultVal;
    }

    private static <T> Supplier<T> toSupplier(T t) {
        return () -> t;
    }

    private HttpRequest(HttpRequest template,
                        Supplier<LocalDateTime> endOfRequest,
                        Supplier<Throwable> error,
                        Supplier<State> status,
                        Supplier<byte[]> body
    ) {
        this.UUID = template.UUID;
        this.contentLength = template.contentLength;
        this.localAddress = template.localAddress;
        this.remoteAddress = template.remoteAddress;
        this.uri = template.uri;
        this.proto = template.proto;
        this.contentType = template.contentType;
        this.method = template.method;
        this.receivedTimestamp = template.receivedTimestamp;

        this.endOfRequest = getOrDefault(endOfRequest, template.endOfRequest);
        this.error = getOrDefault(error, template.error);
        this.status = getOrDefault(status, template.status);
        this.body = getOrDefault(body, template.getBody());
    }

    public HttpRequest endWithError(Throwable err) {
        return new HttpRequest(this,
                toSupplier(LocalDateTime.now()),
                toSupplier(err),
                toSupplier(State.FAIL),
                null
        );
    }

    public HttpRequest end() {
        return new HttpRequest(this,
                toSupplier(LocalDateTime.now()),
                null,
                toSupplier(State.OK),
                null
        );
    }

    public boolean isFinished() {
        return status != State.NEW;
    }

    public HttpRequest withBody(byte[] body) {
        return new HttpRequest(this,
                null,
                null,
                null,
                toSupplier(body)
        );
    }

    public String getBodyAsString() {
        final String charset = contentType.getCharset("UTF-8");
        if(Charset.isSupported(charset)) {
            return new String(body, Charset.forName(charset));
        }
        return new String(body);
    }

    @Override
    public int compareTo(HttpRequest o) {
        int cmp = receivedTimestamp.compareTo(o.receivedTimestamp);
        if (cmp != 0) return cmp;
        return UUID.compareTo(o.UUID);
    }
}

package com.my.okhttpdemo;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * OKHttp上传进度
 *
 * Created by YJH on 2017/3/15 23:02.
 */

public class CountingRequestBody extends RequestBody {

    protected RequestBody delegate;
    private Listener listener;
    private CountingSink countingSink;

    public CountingRequestBody(RequestBody delegate, Listener listener) {
        this.listener = listener;
        this.delegate = delegate;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            return -1;
        }
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();

    }

    protected final class CountingSink extends ForwardingSink {

        private long bytesWritten;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            listener.onRequestProgress(bytesWritten, contentLength());
        }
    }

    interface Listener {
        void onRequestProgress(long byteWrite, long contentLength);
    }
}

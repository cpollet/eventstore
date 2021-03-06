package net.cpollet.es.stores;

import com.google.common.truth.Truth;
import net.cpollet.es.Event;
import net.cpollet.es.EventNotStoredException;
import net.cpollet.es.stores.fakes.FailingEventStore;
import net.cpollet.es.stores.fakes.SucceedingEventStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(MockitoJUnitRunner.class)
public class TestDefaultAsyncEventStore {
    private ExecutorService executorService;

    @Before
    public void setUp() {
        executorService = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
    }

    @Test
    public void storeAsync_returnsAFailedStorageResult_whenAnExceptionWasThrown() throws EventNotStoredException, InterruptedException, ExecutionException, TimeoutException {
        EventNotStoredException exception = new EventNotStoredException();

        DefaultAsyncEventStore store = new DefaultAsyncEventStore(new FailingEventStore(exception), executorService);

        CompletableFuture<Event> future = store.storeAsync(null, null);

        future.whenComplete((event, ex) -> {
            Truth.assertThat(future.isCompletedExceptionally()).isTrue();
            Truth.assertThat(event).isNull();
            Truth.assertThat(ex).isSameAs(exception);
        });
    }

    @Test
    public void storeAsync_returnsASuccessfulStorageResult_whenNoExceptionsWereThrown() throws EventNotStoredException, InterruptedException, ExecutionException, TimeoutException {
        Event event = new Event(null, null, 0L, 0L, null, null, null);
        DefaultAsyncEventStore store = new DefaultAsyncEventStore(new SucceedingEventStore(event), executorService);

        CompletableFuture<Event> future = store.storeAsync(null, null);

        future.whenComplete((e, ex)-> {
            Truth.assertThat(future.isCompletedExceptionally()).isFalse();
            Truth.assertThat(e).isSameAs(event);
        });
    }
}
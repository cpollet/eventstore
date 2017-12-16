package net.cpollet.es;

import com.google.common.truth.Truth;
import net.cpollet.es.stores.FailingEventStore;
import net.cpollet.es.stores.SucceedingEventStore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestDefaultListenableEventStore {

    @Mock
    private Listener listener;

    @Test
    public void store_notifies_whenThereAreNoExceptions() throws EventNotStoredException {
        Event event = new Event(null, null, 0L, 0L, null, null, null);
        DefaultListenableEventStore store = new DefaultListenableEventStore(new SucceedingEventStore(event), listener);

        store.store(null, null);

        Mockito.verify(listener, Mockito.times(1)).onEventPersisted(event);
    }

    @Test
    public void store_doesNotNotify_whenThereAreExceptions() throws EventNotStoredException {
        EventNotStoredException exception = new EventNotStoredException();
        DefaultListenableEventStore store = new DefaultListenableEventStore(new FailingEventStore(exception), listener);

        try {
            store.store(null, null);
        } catch (Exception e) {
            // nothing
        }

        Mockito.verify(listener, Mockito.never()).onEventPersisted(ArgumentMatchers.any());
    }

    @Test
    public void store_bubbleException_whenThereAreExceptions() throws EventNotStoredException {
        EventNotStoredException exception = new EventNotStoredException();
        DefaultListenableEventStore store = new DefaultListenableEventStore(new FailingEventStore(exception), listener);

        try {
            store.store(null, null);
        } catch (Exception e) {
            Truth.assertThat(e).isSameAs(exception);
            return;
        }

        Assert.fail("Exception expected");
    }

    @Test
    public void addListener_addsListener() throws EventNotStoredException {
        Event event = new Event(null, null, 0L, 0L, null, null, null);
        DefaultListenableEventStore store = new DefaultListenableEventStore(new SucceedingEventStore(event));

        store.addListener(listener).store(null, null);

        Mockito.verify(listener, Mockito.times(1)).onEventPersisted(event);
    }
}

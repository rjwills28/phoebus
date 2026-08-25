package org.phoebus.archive.reader.appliance;

import org.epics.archiverappliance.retrieval.client.EpicsMessage;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;

/**
 * Iterator whose hasNext() blocks until released, for concurrency tests.
 *
 * The caller awaits {@link #entered} to confirm the worker thread is inside
 * hasNext(), then counts down {@link #release} to unblock it.
 */
class BlockingGenMsgIterator implements Iterator<EpicsMessage> {

    final CountDownLatch entered = new CountDownLatch(1);
    final CountDownLatch release = new CountDownLatch(1);
    int count = 0;

    @Override
    public boolean hasNext() {
        // First needs to check that the iterator is not empty
        // so allow to return true the first time hasNext() is called
        if (count == 0) {
            count = count + 1;
            return true;
        }
        entered.countDown();
        try {
            release.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    @Override
    public EpicsMessage next() {
        throw new NoSuchElementException();
    }
}

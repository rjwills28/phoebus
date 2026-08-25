package org.phoebus.archive.reader.appliance;

import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadInfo;
import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadType;
import org.epics.archiverappliance.retrieval.client.EpicsMessage;
import org.epics.archiverappliance.retrieval.client.GenMsgIterator;
import org.epics.vtype.VNumber;
import org.epics.vtype.VStatistics;
import org.epics.vtype.VType;
import org.junit.jupiter.api.Test;
import org.phoebus.archive.reader.UnknownChannelException;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.phoebus.archive.reader.appliance.TestHelper.*;

class ApplianceOptimizedValueIteratorTest {

    private static final Instant START = Instant.now().minusSeconds(3600);
    private static final Instant END = Instant.now();
    private static final int POINTS = 100;

    @Test
    void fetchUrlContainsOptimizedNOperator() throws Exception {
        FakeDataRetrieval dr = new FakeDataRetrieval(probeStream(PayloadType.SCALAR_DOUBLE));
        dr.whenPvContains("optimized_", genericStream());

        FakeApplianceArchiveReader reader = new FakeApplianceArchiveReader(dr);
        new ApplianceOptimizedValueIterator(reader, "TEST:PV", START, END, POINTS, false);

        assertTrue(dr.pvsCalled.stream().anyMatch(pv -> pv.startsWith("optimized_") && pv.endsWith("(TEST:PV)")),
                "Expected optimized_<N>(TEST:PV) call, got: " + dr.pvsCalled);
    }

    @Test
    void determineDisplayRejectsEnum() {
        FakeDataRetrieval dr = new FakeDataRetrieval(probeStream(PayloadType.SCALAR_ENUM));
        FakeApplianceArchiveReader reader = new FakeApplianceArchiveReader(dr);

        assertThrows(ArchiverApplianceInvalidTypeException.class,
                () -> new ApplianceOptimizedValueIterator(reader, "TEST:PV", START, END, POINTS, false));
    }

    private static EpicsMessage waveformMsg(double mean, double std, double min, double max, int count) {
        EpicsMessage msg = mock(EpicsMessage.class);
        when(msg.getElementCount()).thenReturn(5);
        when(msg.getNumberAt(0)).thenReturn(mean);
        when(msg.getNumberAt(1)).thenReturn(std);
        when(msg.getNumberAt(2)).thenReturn(min);
        when(msg.getNumberAt(3)).thenReturn(max);
        when(msg.getNumberAt(4)).thenReturn(count);
        when(msg.getTimestamp()).thenReturn(new java.sql.Timestamp(System.currentTimeMillis()));
        when(msg.getSeverity()).thenReturn(0);
        when(msg.getStatus()).thenReturn(0);
        return msg;
    }

    private ApplianceOptimizedValueIterator makeWithWaveformData(boolean useStatistics) throws Exception {
        EpicsMessage dataMsg = waveformMsg(1.0, 0.1, 0.5, 1.5, 10);
        GenMsgIterator dataStream = mock(GenMsgIterator.class);
        PayloadInfo waveformInfo = PayloadInfo.newBuilder().setType(PayloadType.WAVEFORM_DOUBLE).buildPartial();
        when(dataStream.iterator()).thenReturn(Collections.singletonList(dataMsg).iterator());
        when(dataStream.getPayLoadInfo()).thenReturn(waveformInfo);

        FakeDataRetrieval dr = new FakeDataRetrieval(probeStream(PayloadType.SCALAR_DOUBLE));
        dr.whenPvContains("optimized_", dataStream);

        FakeApplianceArchiveReader reader = new FakeApplianceArchiveReader(dr);
        return new ApplianceOptimizedValueIterator(reader, "TEST:PV", START, END, POINTS, useStatistics);
    }

    @Test
    void nextReturnsVStatisticsWhenUseStatisticsTrue() throws Exception {
        ApplianceOptimizedValueIterator iter = makeWithWaveformData(true);
        assertTrue(iter.hasNext());
        VType result = iter.next();
        assertInstanceOf(VStatistics.class, result);
    }

    @Test
    void nextReturnsVNumberWhenUseStatisticsFalse() throws Exception {
        ApplianceOptimizedValueIterator iter = makeWithWaveformData(false);
        assertTrue(iter.hasNext());
        VType result = iter.next();
        assertInstanceOf(VNumber.class, result);
    }

    @Test
    void emptyIteratorException() {
        FakeDataRetrieval dr = new FakeDataRetrieval(emptyStream());
        FakeApplianceArchiveReader reader = new FakeApplianceArchiveReader(dr, false, false);
        ArchiverApplianceException exception = assertThrows(
                ArchiverApplianceException.class,
                () -> new ApplianceOptimizedValueIterator(reader, "TEST:PV", START, END, POINTS, false));

        assertEquals(exception.getMessage(), "Fetched datastream is empty");
    }
}

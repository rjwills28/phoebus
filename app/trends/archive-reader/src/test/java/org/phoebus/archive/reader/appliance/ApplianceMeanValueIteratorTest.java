package org.phoebus.archive.reader.appliance;

import edu.stanford.slac.archiverappliance.PB.EPICSEvent.PayloadType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.phoebus.archive.reader.appliance.TestHelper.genericStream;
import static org.phoebus.archive.reader.appliance.TestHelper.probeStream;

class ApplianceMeanValueIteratorTest {

    private static final Instant START = Instant.now().minusSeconds(3600);
    private static final Instant END = Instant.now();
    private static final int POINTS = 60;

    @Test
    void fetchUrlContainsMeanIntervalOperator() throws Exception {
        FakeDataRetrieval dr = new FakeDataRetrieval(probeStream(PayloadType.SCALAR_DOUBLE));
        dr.whenPvContains("mean_", genericStream());

        FakeApplianceArchiveReader reader = new FakeApplianceArchiveReader(dr);
        new ApplianceMeanValueIterator(reader, "TEST:PV", START, END, POINTS);

        assertTrue(dr.pvsCalled.stream().anyMatch(pv -> pv.startsWith("mean_") && pv.endsWith("(TEST:PV)")),
                "Expected mean_<interval>(TEST:PV) in calls, got: " + dr.pvsCalled);
    }

    @Test
    void determineDisplayRejectsEnum() {
        FakeDataRetrieval dr = new FakeDataRetrieval(probeStream(PayloadType.SCALAR_ENUM));
        FakeApplianceArchiveReader reader = new FakeApplianceArchiveReader(dr);

        assertThrows(ArchiverApplianceInvalidTypeException.class,
                () -> new ApplianceMeanValueIterator(reader, "TEST:PV", START, END, POINTS));
    }

    @Test
    void determineDisplayAcceptsDouble() throws Exception {
        FakeDataRetrieval dr = new FakeDataRetrieval(probeStream(PayloadType.SCALAR_DOUBLE));
        dr.whenPvContains("mean_", genericStream());

        FakeApplianceArchiveReader reader = new FakeApplianceArchiveReader(dr);
        ApplianceMeanValueIterator iter = new ApplianceMeanValueIterator(reader, "TEST:PV", START, END, POINTS);

        assertNotNull(iter.display);
    }
}

package org.phoebus.archive.reader.appliance;

import edu.stanford.slac.archiverappliance.PB.EPICSEvent;
import org.epics.archiverappliance.retrieval.client.EpicsMessage;
import org.epics.archiverappliance.retrieval.client.GenMsgIterator;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Unit-test helper for creating Message iterators*/
public class TestHelper {
    public static GenMsgIterator genericStream() {
        EpicsMessage msg = mock(EpicsMessage.class);
        GenMsgIterator s = mock(GenMsgIterator.class);
        when(s.iterator()).thenReturn(Collections.synchronizedList(Arrays.asList(msg,msg,msg)).iterator());
        when(s.getPayLoadInfo()).thenReturn(EPICSEvent.PayloadInfo.newBuilder().setType(EPICSEvent.PayloadType.SCALAR_DOUBLE).buildPartial());
        return s;
    }

    public static GenMsgIterator probeStream(EPICSEvent.PayloadType type) {
        EpicsMessage msg = mock(EpicsMessage.class);
        GenMsgIterator s = mock(GenMsgIterator.class);
        when(s.iterator()).thenReturn(Collections.singletonList(msg).iterator());
        when(s.getPayLoadInfo()).thenReturn(EPICSEvent.PayloadInfo.newBuilder().setType(type).buildPartial());
        return s;
    }

    public static GenMsgIterator countStream(int count) {
        EpicsMessage msg = mock(EpicsMessage.class);
        when(msg.getNumberValue()).thenReturn(count);
        GenMsgIterator s = mock(GenMsgIterator.class);
        when(s.iterator()).thenReturn(Collections.singletonList(msg).iterator());
        return s;
    }

    public static GenMsgIterator emptyStream() {
        GenMsgIterator s = mock(GenMsgIterator.class);
        when(s.iterator()).thenReturn(Collections.emptyIterator());
        return s;
    }
}

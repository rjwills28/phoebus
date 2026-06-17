package org.csstudio.apputil.formula.string;


import org.csstudio.apputil.formula.spi.FormulaFunction;
import org.epics.util.array.ListNumber;
import org.epics.vtype.*;
import org.phoebus.core.vtypes.VTypeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A formula functions for string concatenation
 * @author Kunal Shroff
 */
public class PVFunction implements FormulaFunction {

    @Override
    public String getCategory() {
        return "string";
    }

    @Override
    public String getName() {
        return "pv";
    }

    @Override
    public String getDescription() {
        return "Create a PV object";
    }

    @Override
    public boolean isVarArgs() {
        return true;
    }

    @Override
    public List<String> getArguments() {
        return List.of("String...");
    }

    @Override
    public VType compute(VType... args) throws Exception {
        System.out.println("###############");
        final String arg = VTypeHelper.toString(args[0]);
        System.out.println("PVFunction "+ arg);
        return VString.of(arg, Alarm.none(), Time.now());
    }

 
}

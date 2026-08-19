package org.phoebus.pv.formula;

import io.reactivex.rxjava3.disposables.Disposable;
import org.csstudio.apputil.formula.Formula;
import org.csstudio.apputil.formula.spi.FormulaFunction;
import org.epics.util.array.ListNumber;
import org.epics.vtype.*;
import org.phoebus.pv.PV;
import org.phoebus.pv.PVPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.phoebus.pv.PV.logger;

/**
 * A formula functions for string concatenation
 * @author Kunal Shroff
 */
public class PVFunction implements FormulaFunction {
    
    VType pv_value = VString.of("NaN", Alarm.none(), Time.now());
    Formula formula = null;
    PV pv = null;

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
        return "Concatenate a list of strings of a string array";
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
        String p = ((VString)args[0]).getValue();
        System.out.println("\n############ PV Function given "+p);
        PV pv = PVPool.getPV(p);
        //System.out.println("### Adding subscriber for "+p);
        Disposable subscription = pv.onValueEvent()
                .throttleLatest(FormulaPVPreferences.throttle_ms, TimeUnit.MILLISECONDS)
                .subscribe(this::handleUpdate);

        formula.findVariable(p);
        //return res;
        pv_value = pv.read();
        System.out.println("############ PV Function pv_value for "+pv.getName()+" = "+pv_value);
        subscription.dispose();
        return pv_value;
    }
    
    @Override
    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    private void handleUpdate(final VType value)
    {
        System.out.println("############ PV Function handleUpdate: "+value);
        pv_value = value;
    }

    /**
     * Returns true is the value is a StringArray or can be converted to a StringArray
     * @param value 
     * @return boolean true if value can be used as a string array
     */
    private boolean isStringArray(VType value)
    {
        return value instanceof VStringArray 
            || value instanceof VNumberArray;
    }

    private List<String> getStringArray(VType value) {
        if(value instanceof VStringArray)
        {
            return ((VStringArray) value).getData();
        }
        else if (value instanceof VNumberArray)
        {
            List<String> stringData = new ArrayList<String>();
            ListNumber data = ((VNumberArray) value).getData();
            for (int i = 0; i < data.size(); i++) {
                stringData.add(String.valueOf(data.getDouble(i)));
            }
            return stringData;
        }
        return Collections.emptyList();
    }

    private boolean isString(VType value)
    {
        return value instanceof VString;
    }
}

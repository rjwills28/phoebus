/*******************************************************************************
 * Copyright (c) 2019-2020 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.phoebus.pv.formula;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import org.csstudio.apputil.formula.Formula;
import org.csstudio.apputil.formula.VariableNode;
import org.epics.vtype.Alarm;
import org.epics.vtype.Display;
import org.epics.vtype.Time;
import org.epics.vtype.VDouble;
import org.epics.vtype.VString;
import org.epics.vtype.VType;
import org.phoebus.pv.PV;

/** Formula-based {@link PV}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FormulaPV extends PV
{
    /** Evaluate formulas on one thread
     *  to decouple and throttle input updates
     */
    private static final ExecutorService update_thread = Executors.newSingleThreadExecutor(target ->
    {
        final Thread thread = new Thread(target, "FormulaPV");
        thread.setDaemon(true);
        return thread;
    });

    /** Is there already a pending update? */
    private AtomicBoolean pending = new AtomicBoolean();

    private Formula formula;
    private volatile FormulaInput[] inputs;

    protected FormulaPV(final String name, String expression)
    {
        super(name);
        Boolean isPVFunction = false;
        try
        {
            // Parse expression...
            System.out.println("\n#### Formula PV "+name+", "+expression);
            String subExpression = expression;
            
            if (expression.contains("pv(")){
                isPVFunction = true;
                System.out.println("#######  HERE");
                StringBuffer buf = new StringBuffer();
                CharacterIterator it
                        = new StringCharacterIterator(expression);
                while (it.current() != CharacterIterator.DONE) {
                    if (it.current() == 'p'){
                        it.next();
                        if (it.current() == 'v') {
                            it.next();
                            if (it.current() == '(') {
                                int bracketCount = 1;
                                char last = it.current();
                                it.next();
                                while (it.current() != CharacterIterator.DONE && (bracketCount != 0)) {
                                    last = it.current();

                                    if (last == '(')
                                        bracketCount = bracketCount + 1;
                                    else if (last == ')') {
                                        bracketCount = bracketCount - 1;
                                        if (bracketCount == 0) {
                                            it.next();
                                            break;
                                        }
                                    }
                                    buf.append(last);
                                    it.next();
                                }
                            }
                            String internal = buf.toString();
                            System.out.println("##### internal "+internal);
                            subExpression = internal;
                        }
                    }
                    it.next();
                }
                formula = new Formula(subExpression, true);
            } else {
                formula = new Formula(expression, true);
            }
            
            

            System.out.println("#### Formula PV callng eval() ");
            VType value = formula.eval();
            notifyListenersOfValue(value);

            String st = ((VString) value).getValue();
            System.out.println("#### Formula 1st eval value " + st);
            ArrayList<VariableNode> varList = new ArrayList<>();
            if (isPVFunction) {
                for (VariableNode var: formula.getVariables()) {
                    varList.add(var);
                }
                String newExpression = expression.replace("pv("+subExpression+")", "`"+st+"`");
                if (varList.size() == 0){
                    //newExpression = expression.replace("pv("+subExpression+")", "\""+st+"\"");
                    System.out.println("### newExpression "+newExpression);
                    formula = new Formula(newExpression, true);
                    value = formula.eval();

                    System.out.println("#### Formula isPvfunvion eval value " + value);
                }
                
            }
            
            // Determine variables, connect to PVs
            VariableNode vars[] = formula.getVariables();
            for (VariableNode var: formula.getVariables()) {
                varList.add(var);
            }
            inputs = new FormulaInput[varList.size()];
            for (int i=0; i<inputs.length; ++i)
            {   // Initialize 'disconnected' until PV sends first value
                System.out.println("### FormulaPV Variables "+varList.get(i));
                varList.get(i).setValue(VDouble.of(Double.NaN, Alarm.disconnected(), Time.now(), Display.none()));
                inputs[i] = new FormulaInput(this, varList.get(i));
            }
            //inputs[inputs.length-1] = new FormulaInput(this, new VariableNode("temperature:water"));
            System.out.println("#### Formula PV number of inputs "+inputs.length);

            // Set initial value
            doUpdate();
            
            
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, "Formula PV error in " + expression, ex);
            // Set initial value
            notifyListenersOfValue(VString.of(ex.getMessage(), Alarm.noValue(), Time.now()));
        }
    }

    /** @return Formula expression */
    public String getExpression()
    {
        return formula.getFormula();
    }

    /** @return PVs that are inputs to this formula */
    public Collection<PV> getInputs()
    {
        final List<PV> pvs = new ArrayList<>(inputs.length);
        for (FormulaInput input : inputs)
            pvs.add(input.getPV());
        return pvs;
    }

    /** Schedule evaluation of formula */
    void update()
    {
        if (pending.getAndSet(true))
            logger.log(Level.FINE, () -> getName() + " skips recalc on " + Thread.currentThread());
        else
            update_thread.submit(this::doUpdate);
    }

    /** Compute updated value of formula and notify listeners */
    private void doUpdate()
    {
        pending.set(false);
        logger.log(Level.FINE, () -> getName() + " recalc on " + Thread.currentThread());

        // Simulate slow evaluation
        // try { Thread.sleep(100); } catch (InterruptedException e) {}

        VType value = formula.eval();
        System.out.println("#### Formula PV doUpdate() value for "+getName()+" = "+value);
        /**
        if (true) {
            String newExpression = "`"+(((VString)value).getValue())+"`";
                //newExpression = expression.replace("pv("+subExpression+")", "\""+st+"\"");
            try {
                System.out.println("##### newExpression "+newExpression);
                formula = new Formula(newExpression, true);
                value = formula.eval();
                System.out.println("##### value "+value);

                VariableNode vars[] = formula.getVariables();
                inputs = new FormulaInput[vars.length];
                for (int i=0; i<inputs.length; ++i)
                {   // Initialize 'disconnected' until PV sends first value
                    System.out.println("### FormulaPV Variables "+vars[i]);
                    vars[i].setValue(VDouble.of(Double.NaN, Alarm.disconnected(), Time.now(), Display.none()));
                    inputs[i] = new FormulaInput(this, vars[i]);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            
        }*/
        notifyListenersOfValue(value);
    }

    @Override
    protected void close()
    {
        // Close variable PVs
        // Inputs or individual input may be null for formulas that failed to initialize
        if (inputs != null)
            for (FormulaInput input : inputs)
                if (input != null)
                    input.close();
        inputs = null;
    }
}

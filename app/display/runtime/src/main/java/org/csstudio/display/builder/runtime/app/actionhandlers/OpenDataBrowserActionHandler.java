/*
 * Copyright (C) 2024 European Spallation Source ERIC.
 */

package org.csstudio.display.builder.runtime.app.actionhandlers;

import org.csstudio.display.actions.OpenDataBrowserAction;
import org.csstudio.display.builder.model.DisplayModel;
import org.csstudio.display.builder.model.Widget;
import org.csstudio.display.builder.model.spi.ActionHandler;
import org.csstudio.display.builder.model.spi.ActionInfo;
import org.csstudio.display.builder.representation.ToolkitRepresentation;
import org.csstudio.trends.databrowser3.DataBrowserApp;
import org.csstudio.trends.databrowser3.DataBrowserInstance;
import org.csstudio.trends.databrowser3.model.PVItem;
import org.phoebus.framework.macros.MacroHandler;
import org.phoebus.framework.macros.MacroValueProvider;
import org.phoebus.framework.util.ResourceParser;
import org.phoebus.framework.workbench.ApplicationService;
import org.phoebus.util.time.TimeParser;
import org.phoebus.util.time.TimeRelativeInterval;
import org.phoebus.util.time.TimestampFormats;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenDataBrowserActionHandler implements ActionHandler {

    private final Logger logger = Logger.getLogger(OpenDataBrowserActionHandler.class.getName());

    @Override
    public void handleAction(Widget sourceWidget, ActionInfo pluggableActionInfo) {
        OpenDataBrowserAction action = (OpenDataBrowserAction) pluggableActionInfo;

        try
        {
            final DisplayModel model = sourceWidget.getTopDisplayModel();
            final ToolkitRepresentation<Object, Object> toolkit = ToolkitRepresentation.getToolkit(model);
            final MacroValueProvider macros = sourceWidget.getMacrosOrProperties();
            final String pv_names = action.getPVs().strip();
            final String timeframe = action.getTimeframe().strip();

            // Build URI from list of PVs
            String pv_uri = "pv://?";
            String[] pvs = pv_names.split(" ");
            for (int i = 0; i < pvs.length; i++) {
                try {
                    pvs[i] = MacroHandler.replace(macros, pvs[i]);
                    pv_uri = pv_uri.concat(pvs[i]);
                    if (i < pvs.length - 1)
                        pv_uri = pv_uri.concat("&");
                } catch (Exception ignore) {
                    // NOP
                }
            }
            final String final_pv_uri = pv_uri;

            // Set timeframe
            TimeRelativeInterval time_interval;
            if (timeframe.contains(":") || timeframe.contains("-")){
                // An absolute time has been provided
                Instant ints = TimestampFormats.parse(timeframe);
                time_interval = TimeRelativeInterval.of(ints,
                        TimeParser.parseTemporalAmount(TimeParser.NOW));
            } else {
                // Temporal timeframe
                time_interval = TimeRelativeInterval.of(TimeParser.parseTemporalAmount(timeframe),
                        TimeParser.parseTemporalAmount(TimeParser.NOW));
            }

            toolkit.submit(() ->
            {   // Create databrowser instance
                DataBrowserInstance instance = ApplicationService.createInstance(DataBrowserApp.NAME,
                        ResourceParser.createResourceURI(final_pv_uri));

                // Set the default archiver
                for (String pv: pvs) {
                    PVItem pv_item = (PVItem) instance.getModel().getItem(pv);
                    pv_item.useDefaultArchiveDataSources();
                }

                // Set timeframe
                instance.getModel().setTimerange(time_interval);

                return null;
            });
        }
        catch (Throwable ex)
        {
            logger.log(Level.WARNING, action+" failed. Cannot open data browser", ex);
        }
    }

    @Override
    public boolean matches(ActionInfo pluggableActionInfo) {
        return pluggableActionInfo.getType().equalsIgnoreCase(OpenDataBrowserAction.OPEN_DATA_BROWSER);
    }
}

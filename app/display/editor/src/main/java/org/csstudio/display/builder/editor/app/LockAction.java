/*******************************************************************************
 * Copyright (c) 2015-2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.csstudio.display.builder.editor.app;

import org.csstudio.display.builder.editor.DisplayEditor;
import org.csstudio.display.builder.editor.Messages;
import org.csstudio.display.builder.model.DisplayModel;
import org.csstudio.display.builder.model.widgets.GroupWidget;
import org.phoebus.ui.javafx.ImageCache;

import javafx.scene.control.MenuItem;

/** Action to lock a group of widgets
 *  @author Becky Williams
 */
@SuppressWarnings("nls")
public class LockAction extends MenuItem
{
    private final DisplayEditor editor;
    private final GroupWidget widget;

    /** @param editor Editor
     *  @param widgets Selected widgets
     */
    public LockAction(final DisplayEditor editor, final GroupWidget widget)
    {
        super(Messages.Lock,
              ImageCache.getImageView(DisplayModel.class, "/icons/lock.png"));
        this.editor = editor;
        this.widget = widget;
        setOnAction(event -> run());
    }

    /** Execute */
    public void run()
    {
        try
        {
            widget.setPropertyValue("locked", true);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        editor.getWidgetSelectionHandler().toggleSelection(widget);
    }
}

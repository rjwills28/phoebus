/*
 * Copyright (C) 2023 European Spallation Source ERIC.
 */

package org.csstudio.display.builder.representation.javafx.actions;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import org.csstudio.display.builder.model.Messages;
import org.csstudio.display.builder.model.Widget;
import org.csstudio.display.builder.model.persist.ModelReader;
import org.csstudio.display.builder.model.persist.ModelWriter;
import org.csstudio.display.builder.model.persist.XMLTags;
import org.csstudio.display.builder.model.spi.PluggableActionInfo;
import org.phoebus.framework.nls.NLS;
import org.phoebus.framework.persistence.XMLUtil;
import org.phoebus.ui.javafx.ImageCache;
import org.w3c.dom.Element;

import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecuteCommandAction extends PluggableActionBase {

    private static final String EXECUTE_COMMAND = "command";

    private String command;

    public ExecuteCommandAction() {
        this.description = Messages.ActionExecuteCommand;
    }

    @Override
    public Image getImage() {
        return ImageCache.getImage(ExecuteScriptAction.class, "/icons/execute_script.png");
    }

    @Override
    public Node getEditor(Widget widget) {
        ResourceBundle resourceBundle = NLS.getMessages(org.csstudio.display.builder.representation.javafx.Messages.class);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setResources(resourceBundle);
        fxmlLoader.setLocation(this.getClass().getResource("ExecuteCommandActionDetails.fxml"));
        fxmlLoader.setControllerFactory(clazz -> {
            try {
                return clazz.getConstructor(Widget.class, PluggableActionInfo.class).newInstance(widget, ExecuteCommandAction.this);
            } catch (Exception e) {
                Logger.getLogger(ExecuteCommandAction.class.getName()).log(Level.SEVERE, "Failed to construct ExecuteCommandActionDetailsController", e);
            }
            return null;
        });

        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readFromXML(ModelReader modelReader, Element actionXml) throws Exception {
        // Legacy:
        // <action type="EXECUTE_CMD">
        //   <command>echo Hello</command>
        //   <command_directory>$(user.home)</command_directory>
        //   <wait_time>10</wait_time>
        //   <description>Hello</description>
        // </action>
        //
        // New:
        // <action type="command">
        //   <command>echo Hello</command>
        //   <description>Hello</description>
        // </action>
        command = XMLUtil.getChildString(actionXml, XMLTags.COMMAND).orElse("");
        String directory = XMLUtil.getChildString(actionXml, "command_directory")
                .orElse(null);
        // Legacy allowed "opi.dir" as magic macro.
        // Commands are now by default resolved relative to the display file.
        if ("$(opi.dir)".equals(directory))
            directory = null;
        // Legacy allowed user.home as a 'current working directory'.
        // Commands are now executed with their location as cwd.
        if ("$(user.home)".equals(directory))
            directory = null;
        // If a legacy directory was provided, locate command there
        if (directory != null && !directory.isEmpty())
            command = directory + "/" + command;
        if (description.isEmpty())
            description = Messages.ActionExecuteCommand;
    }

    @Override
    public void writeToXML(ModelWriter modelWriter, XMLStreamWriter writer) throws Exception {

        writer.writeAttribute(XMLTags.TYPE, EXECUTE_COMMAND);
        writeDescriptionToXML(writer, description);
        writer.writeStartElement(XMLTags.COMMAND);
        writer.writeCharacters(command);
        writer.writeEndElement();
    }

    @Override
    public boolean matchesAction(String actionId) {
        return actionId.equalsIgnoreCase(EXECUTE_COMMAND) ||
                "EXECUTE_CMD".equalsIgnoreCase(type);
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

package org.phoebus.logbook;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Eric Berryman taken from shroffk
 * 
 */
public interface LogClient {

    /**
     * Create a single LogEntry <code>log</code>, if the LogEntry already exists it is replaced.
     *
     * @param log - LogEntry to be added
     * @return - the created log entry
     */
    public LogEntry set(LogEntry log) throws LogbookException;

    /**
     * Returns a LogEntry that exactly matches the logId <code>logId</code>
     *
     * @param logId LogEntry id
     * @return LogEntry object
     */
    public LogEntry getLog(Long logId);

    /**
     * Query for logs based on the criteria specified in the map
     *
     * @param map - search parameters
     * @return collection of LogEntry objects
     */
    public List<LogEntry> findLogs(Map<String, String> map);


    /**
     * Get a list of all the logbooks currently existings
     * 
     * @return string collection of logbooks
     */
    public Collection<Logbook> listLogbooks();

    /**
     * Get a list of all the tags currently existing
     * 
     * @return string collection of tags
     */
    public Collection<Tag> listTags();

    /**
     * Get a list of all the Properties currently existing
     * 
     * @return list of properties
     */
    public Collection<Property> listProperties();

    /**
     * List the supported log levels
     * 
     * @return a list of supported levels
     */
    public Collection<String> listLevels();
    
    /**
     * List all the active attributes associated with the property
     * <code>propertyName</code> property must exist, name != null
     * 
     * @param propertyName - string property name
     * @return list of property attributes
     */
    public Collection<String> listAttributes(String propertyName);

    /**
     * Return all the logs. ***Warning can return a lot of data***
     * 
     * @return Collection of all LogEntry entires
     */
    public Collection<LogEntry> listLogs();


    /**
     * Returns a collection of attachments that matches the logId <code>logId</code>
     * 
     * @param logId LogEntry id
     * @return attachments collection object
     */
    public Collection<Attachment> listAttachments(Long logId);

    /**
     * Retrieve an attachment of a log entry
     * @param logId - log id
     * @param attachment - the at
     * @return {@link InputStream} to the attachment file
     */
    public InputStream getAttachment(Long logId, Attachment attachment);

    /**
     * 
     * @param logId - log id
     * @param attachmentName - attachment name
     * @return {@link InputStream} to the attachment file
     */
    public InputStream getAttachment(Long logId, String attachmentName);

    /**
     * return the complete property <code>property</code>
     * 
     * @param property - property name
     * @return the @property if it exists
     */
    public Property getProperty(String property);

    /**
     * Create a Tag <code>tag</code>.
     *
     * @param tag - the tag to be created
     * @return The created tag
     */
    public Tag set(Tag tag);

    /**
     * Create tag <code>tag</code> on the set of logs <code>logIds</code>
     *
     * @param tag - create a new tag
     * @param logIds - the log ids to which the above tag is to be added
     * @return the created tag
     */
    public Tag set(Tag tag, Collection<Long> logIds);

    /**
     * Create a new logbook <code>logbook</code> with no associated logs.
     * 
     * @param Logbook - the @logbook to be created
     * @return the created logbook
     */
    public Logbook set(Logbook Logbook);

    /**
     * Create of replace <code>logbook</code> and add them to the logs <code>logIds</code>
     * 
     * @param logbook - logbook to be created
     * @param logIds - log ids to which the created logbook is to be
     */
    public Logbook set(Logbook logbook, Collection<Long> logIds);

    /**
     * Create or replace property <code>property</code>
     * 
     * @param property - the property to be created or replaced
     * @return the created/updated property
     */
    public Property set(Property property);

    /**
     * Update a LogEntry entry <code>LogEntry </code>
     *
     * @param log - the updated log entry
     * @return the updated LogEntry entry
     */
    public LogEntry update(LogEntry log);

    /**
     * Update a set of logs
     *
     * @param logs set of logs to be added
     * @return return updated logentries
     */
    public Collection<LogEntry> update(Collection<LogEntry> logs);

    /**
     * Update an existing property.
     *
     * @param property - the property to be updates
     * @return the updated property
     */
    public Property update(Property property);

    /**
     * Update Tag <code>tag </code> by adding it to LogEntry with id <code>logId</code>
     *
     * @param tag - tag to be updated
     * @param logId LogEntry id the tag is to be added
     * @return updated tag
     */
    public Tag update(Tag tag, Long logId);

    /**
     * Update the Tag <code>tag</code> by adding it to the set of the logs with ids
     * <code>logIds</code>
     *
     * @param tag - tag to be updated
     * @param logIds - collection of LogEntry id
     * @return updated tag
     */
    public Tag update(Tag tag, Collection<Long> logIds);

    /**
     * Add Logbook <code>logbook</code> to the LogEntry <code>logId</code>
     * 
     * @param logbook - logbook to be updated
     * @param logId - LogEntry id the logbook is to be added
     * @return updated logbook
     */
    public Logbook update(Logbook logbook, Long logId);

    /**
     * Update the Tag <code>logbook</code> by adding it to the set of the logs with ids
     * <code>logIds</code>
     * 
     * @param logbook - logbook to be updated
     * @param logIds - LogEntry id the logbook is to be added
     * @return updated logbook
     */
    public Logbook update(Logbook logbook, Collection<Long> logIds);

    /**
     * Update Property <code>property</code> by adding it to LogEntry with id
     * <code>logId</code>
     * 
     * @param property - property to be
     * @param logId - LogEntry id the property to be added
     * @return updated property
     */
    public Property update(Property property, Long logId);

    /**
     * @param logId - the log id of the entry to which the file is to be attached
     * @param local - local file to be attached to logId
     * @return the new attachment add to the log entry
     */
    public Attachment add(File local, Long logId);

    /**
     * @param logId - the log id of the entry to be retrieved
     * @return The log identified by logId
     */
    @Deprecated
    public LogEntry findLogById(Long logId);

    /**
     * @param pattern - search pattern
     * @return List of matching {@link LogEntry}
     */
    public List<LogEntry> findLogsBySearch(String pattern);

    /**
     * Find log entires with tag {tagname}
     * @param tagName - tag name
     * @return List of matching {@link LogEntry}
     */
    public List<LogEntry> findLogsByTag(String tagName);

    /**
     * Find log entires with logbook
     * @param logbookName - logbook name
     * @return List of matching {@link LogEntry}
     */
    public List<LogEntry> findLogsByLogbook(String logbookName);

    /**
     * This function is a subset of queryLogs should it be removed??
     * <p>
     * search for logs with property <code>property</code> and optionally value
     * matching pattern<code>propertyValue</code>
     * 
     * @param propertyName
     * @param attributeName
     * @param attributeValue
     * @return List of matching {@link LogEntry}
     */
    public List<LogEntry> findLogsByProperty(String propertyName, String attributeName, String attributeValue);

    /**
     * Find log entires with the given property
     *
     * @param propertyName - name of the required property
     * @return List of matching {@link LogEntry}
     */
    public List<LogEntry> findLogsByProperty(String propertyName);


    /**
     * Delete the tag with name <code>tag</code
     *
     * @param tagName - the name of the tag to be deleted
     */
    public default void deleteTag(String tagName) throws LogbookException {
        throw new LogbookException(new UnsupportedOperationException());
    }

    /**
     * Delete the logbook with name <code>logbook</code>
     *
     * @param logbookName - the name of the logbook to be deleted
     */
    public default void deleteLogbook(String logbookName) throws LogbookException {
        throw new LogbookException(new UnsupportedOperationException());
    }

    /**
     * Delete the property with name <code>property</code>
     *
     * @param propertyName - property to be deleted
     */
    public default void deleteProperty(String propertyName) throws LogbookException {
        throw new LogbookException(new UnsupportedOperationException());
    }

    /**
     * Remove the LogEntry <code>log</code>
     *
     * @param log LogEntry to be removed
     */
    public default void delete(LogEntry log) throws LogbookException {
        throw new LogbookException(new UnsupportedOperationException());
    }

    /**
     * Remove the LogEntry identified by <code>logId</code>
     *
     * @param logId LogEntry id LogEntry id to be removed
     */
    public default void delete(Long logId) throws LogbookException {
        throw new LogbookException(new UnsupportedOperationException());
    }

    /**
     * Remove the LogEntry collection identified by <code>logIds</code>
     *
     * @param logIds logs to be removed
     */
    public default void delete(Collection<LogEntry> logIds) throws LogbookException {
        throw new LogbookException(new UnsupportedOperationException());
    }

    /**
     * Remove tag <code>tag</code> from the LogEntry with the id <code>logId</code>
     * 
     * @param tag - the tag to be removed
     * @param logId - the log entry from which the tag is to be removed
     */
    public default void delete(Tag tag, Long logId) throws LogbookException {
        throw new LogbookException(new UnsupportedOperationException());
    }

    /**
     * Remove the tag <code>tag </code> from all the logs identified by <code>logIds</code>
     *
     * @param tag - the tag to be removed
     * @param logIds - the logs from which the tag is to be removed
     */
    public default void delete(Tag tag, Collection<Long> logIds) throws LogbookException {
        throw new LogbookException(new UnsupportedOperationException());
    }

    /**
     * Remove logbook <code>logbook</code> from the LogEntry with name <code>logName</code>
     *
     * @param logbook - the logbook to be removed
     * @param logId - the log entry from which the logbook is to be removed
     */
    public default void delete(Logbook logbook, Long logId) throws LogbookException {
        throw new LogbookException(new UnsupportedOperationException());
    }

    /**
     * Remove the logbook <code>logbook</code> from the set of logs <code>logIds</code>
     * 
     * @param logbook - the logbook to be removed
     * @param logIds - the logs from which the logbook is to be removed
     */
    public default void delete(Logbook logbook, Collection<Long> logIds) throws LogbookException {
        throw new LogbookException(new UnsupportedOperationException());
    }

    /**
     * Remove property <code>property</code> from the LogEntry with id <code>logId</code>
     * 
     * @param property - the property to be deleted
     * @param logId - LogEntry id from which the property is to be removed
     */
    public default void delete(Property property, Long logId) throws LogbookException {
        throw new LogbookException(new UnsupportedOperationException());
    }

    /**
     * Remove the property <code>property</code> from the set of logs
     * <code>logIds</code>
     * 
     * @param property - the property to be deleted
     * @param logIds - the logs from which to property is to be removed
     */
    public default void delete(Property property, Collection<Long> logIds) throws LogbookException {
        throw new LogbookException(new UnsupportedOperationException());
    }

    /**
     * Remove file attachment from LogEntry <code>logId<code>
     *
     * @param fileName - the file name to be removed
     * @param logId - the logid from which the attached file is to be removed
     */
    public default void delete(String fileName, Long logId) throws LogbookException {
        throw new LogbookException(new UnsupportedOperationException());
    }
}

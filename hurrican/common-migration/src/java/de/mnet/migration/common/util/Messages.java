/**
 * Copyright (c) 2010 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 23.06.2010 17:42:41
 */
package de.mnet.migration.common.util;

import java.lang.reflect.*;
import java.util.*;

import de.mnet.migration.common.MigrationTransformator;
import de.mnet.migration.common.exception.TransformationException;
import de.mnet.migration.common.result.SourceIdList;
import de.mnet.migration.common.result.TargetIdList;
import de.mnet.migration.common.result.TransformationResult;
import de.mnet.migration.common.result.TransformationStatus;


/**
 * A transformation message. Subclass and add fields of type Messages.Message, which can then be used to add messages
 * for the transformation result. This class is ThreadSafe.
 *
 *
 */
public class Messages {
    private final ThreadLocal<SourceIdList> preparedSources = new ThreadLocal<SourceIdList>();
    private final ThreadLocal<String> preparedMessage = new ThreadLocal<String>();
    private final ThreadLocal<Boolean> evaluated = new ThreadLocal<Boolean>();
    private volatile List<Message> messages = null;

    /**
     * Generic error
     */
    public final Message ERROR = new Message(TransformationStatus.ERROR,
            MigrationTransformator.CLASS_DEFAULT, "Error in Transformation: %s");
    /**
     * Generic bad data
     */
    public final Message BAD_DATA = new Message(TransformationStatus.BAD_DATA,
            MigrationTransformator.CLASS_DEFAULT, "Bad Data in Transformation: %s");
    /**
     * Generic skipped
     */
    public final Message SKIPPED = new Message(TransformationStatus.SKIPPED,
            MigrationTransformator.CLASS_DEFAULT, "Datensatz wurde übersprungen: %s");
    /**
     * Generic info
     */
    public final Message INFO = new Message(TransformationStatus.INFO,
            MigrationTransformator.CLASS_DEFAULT, "Information zum Datensatz: %s");


    public Messages() {
        // Cannot use getMessagesFromFields() here, as the subclass was not
        // initialized yet. Thus, getMessagesFromFields() has to be called
        // always before any access of the messages field.
    }


    public TransformationResult evaluate(TargetIdList targets) {
        return evaluate(null, targets);
    }

    public TransformationResult evaluate(SourceIdList sources, TargetIdList targets) {
        getMessagesFromFields();
        TransformationStatus status = TransformationStatus.OK;
        long classification = MigrationTransformator.CLASS_DEFAULT;
        String classificationString = null;
        List<Exception> exceptions = new ArrayList<Exception>();
        List<String> infos = new ArrayList<String>();
        if ((preparedMessage.get() != null) && (preparedMessage.get().length() > 0)) {
            infos.add(preparedMessage.get());
        }
        SourceIdList allSources = sources;
        if (preparedSources.get() != null) {
            allSources = preparedSources.get().merge(sources);
        }
        for (Message message : messages) {
            if (!message.messages.get().isEmpty()) {
                infos.addAll(message.messages.get());
                exceptions.addAll(message.exceptions.get());
                classification |= message.classification;
                if (classificationString == null) {
                    classificationString = message.fieldName;
                }
                else {
                    classificationString = classificationString + "|" + message.fieldName;
                }
                status = status.worse(message.status);
            }
        }
        evaluated.set(true);
        return new TransformationResult(status, allSources, status.hasTargetIds() ? targets : null,
                TransformationStatus.OK.equals(status) ? null : infos, classification, classificationString, exceptions);
    }


    /**
     * Returns the number of messages currently added since the last call to prepare
     */
    public int size() {
        getMessagesFromFields();
        int size = 0;
        for (Message message : messages) {
            size += message.messages.get().size();
        }
        return size;
    }


    /**
     * Clears the messages for this thread and sets a prefix for the resulting info.
     */
    public void prepare(String message) {
        prepare(message, null);
    }


    /**
     * Clears the messages for this thread and sets a sources list for the resulting info.
     */
    public void prepare(SourceIdList sources) {
        prepare(null, sources);
    }


    /**
     * Clears the messages for this thread and sets a prefix for the resulting info.
     */
    public void prepare(String message, SourceIdList sources) {
        getMessagesFromFields();
        for (Message msg : messages) {
            msg.messages.get().clear();
            msg.exceptions.get().clear();
        }
        preparedMessage.set(message);
        preparedSources.set(sources);
        evaluated.set(Boolean.FALSE);
    }

    public void addSources(SourceIdList sources) {
        if (preparedSources.get() != null) {
            preparedSources.set(preparedSources.get().merge(sources));
        }
        else {
            preparedSources.set(sources);
        }
    }

    /**
     * @return true, if the messages were already evaluated since the last prepare call
     */
    public boolean wasEvaluated() {
        return !Boolean.TRUE.equals(evaluated.get());
    }


    protected void getMessagesFromFields() {
        if (messages == null) {
            // double checked locking - working with Java 1.5+ and volatile messages
            synchronized (this) {
                if (messages == null) {
                    List<Field> fields = ReflectionUtil.getAllFields(Messages.class, this.getClass());
                    List<Message> list = new ArrayList<Messages.Message>();
                    for (Field field : fields) {
                        if (Message.class.isAssignableFrom(field.getType())) {
                            try {
                                field.setAccessible(true);
                                Message message = (Message) field.get(this);
                                if (message.fieldName == null) {
                                    message.fieldName = field.getName();
                                }
                                list.add(message);
                            }
                            catch (Exception e) {
                                throw new RuntimeException("Exception trying to get my own field", e);
                            }
                        }
                    }
                    this.messages = Collections.unmodifiableList(list);
                }
            }
        }
    }


    public class Message {
        private final String format;
        private final TransformationStatus status;
        private volatile Long classification;
        private volatile String fieldName;
        private final ThreadLocal<List<Exception>> exceptions = new ThreadLocal<List<Exception>>() {
            @Override
            protected List<Exception> initialValue() {
                return new Vector<Exception>();
            }
        };
        private final ThreadLocal<List<String>> messages = new ThreadLocal<List<String>>() {
            @Override
            protected List<String> initialValue() {
                return new Vector<String>();
            }
        };

        public Message(TransformationStatus status, Long classification, String format) {
            this.classification = classification;
            this.format = format;
            this.status = status;
        }

        public Message(TransformationStatus status, Long classification, String format, String fieldName) {
            this.classification = classification;
            this.format = format;
            this.status = status;
            this.fieldName = fieldName;
        }

        public MessageThrower add(Object... arguments) {
            return add(null, arguments);
        }

        public MessageThrower add(Exception e, Object... arguments) {
            messages.get().add(String.format(format, arguments));
            exceptions.get().add(e);
            return new MessageThrower();
        }

        public boolean isEmpty() {
            return messages.get().isEmpty();
        }

        public int size() {
            return messages.get().size();
        }

        public Long getClassification() {
            return classification;
        }
    }

    public final class MessageThrower {
        public void throwIt() {
            throw new TransformationException(Messages.this.evaluate(null, null));
        }

        public void throwIt(SourceIdList sources) {
            throw new TransformationException(Messages.this.evaluate(sources, null));
        }

        public void throwIt(SourceIdList sources, TargetIdList targets) {
            throw new TransformationException(Messages.this.evaluate(sources, targets));
        }

        public void throwIt(TargetIdList targets) {
            throw new TransformationException(Messages.this.evaluate(null, targets));
        }
    }
}

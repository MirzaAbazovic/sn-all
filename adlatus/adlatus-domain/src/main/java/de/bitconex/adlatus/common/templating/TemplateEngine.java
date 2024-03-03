package de.bitconex.adlatus.common.templating;

/**
 * FreeMarkerTemplateEngine is just the implementation of this interface. The interface should be documented without mentioning concrete implementations, which should change overtime.
 *
 * @see <a href="https://bitconex.atlassian.net/wiki/spaces/ADL/overview">SOAP WITA messages</a>
 */
public interface TemplateEngine {
    /**
     * It does not create new process, the name 'process' is in the meaning of processing template and given data, and generating message using them both.
     *
     * @param dataModel {@link Object} representing our data model.
     * @param templateName String representing our template name.
     * @return {@link freemarker.template.Template}
     */
    String process(Object dataModel, String templateName);
}

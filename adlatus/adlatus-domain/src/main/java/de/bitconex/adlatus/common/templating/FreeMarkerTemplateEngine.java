package de.bitconex.adlatus.common.templating;

import de.bitconex.adlatus.common.infrastructure.exception.GenerateMessageException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;

@Service
public class FreeMarkerTemplateEngine implements TemplateEngine {
    private static final String TEMPLATE_PATH = "/templates";
    private final Configuration cfg;

    public FreeMarkerTemplateEngine() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setClassForTemplateLoading(this.getClass(), TEMPLATE_PATH);
        this.cfg = configuration;
    }

    // for testing
    public FreeMarkerTemplateEngine(Configuration cfg) {
        this.cfg = cfg;
    }

    @Override
    public String process(Object dataModel, String templateName) {
        StringWriter out = new StringWriter();
        Template template;
        try {
            template = cfg.getTemplate(templateName);
        } catch (IOException e) {
            throw new GenerateMessageException("Could not load template " + templateName);
        }

        try {
            template.process(dataModel, out);
        } catch (TemplateException | IOException e) {
            throw new GenerateMessageException("Could not process template " + templateName + " with data model " + dataModel);
        }

        return out.toString();
    }
}

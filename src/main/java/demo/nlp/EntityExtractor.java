package demo.nlp;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class EntityExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(EntityExtractor.class);
    private NameFinderME entityFinder = null;

    public void train(String trainDirectory, boolean validate) {
        LOG.info("============== TRAINING STARTED ================");
        try {
            ObjectStream<String> fileStream = new PlainTextByLineStream(new FileReader(trainDirectory + "/dataset.txt"));
            ObjectStream<NameSample> sampleStream = new NameSampleDataStream(fileStream);
            TokenNameFinderModel model = NameFinderME.train("en", "train", sampleStream,
                                                            Collections.<String, Object>emptyMap());
            entityFinder = new NameFinderME(model);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        LOG.info("============== TRAINING FINISHED ================");
        if (validate) {
            this.validate(trainDirectory + "/validate.txt");
        }
    }

    public HashMap<String, String> extract(String str) {
        String[] tokens = WhitespaceTokenizer.INSTANCE.tokenize(str);

        Span[] entities = entityFinder.find(tokens);
        String[] values = Span.spansToStrings(entities, tokens);

        HashMap<String, String> results = new HashMap<>();
        for (int i = 0; i < entities.length; i++) {
            results.put(entities[i].getType(), values[i]);
        }

        return results;
    }

    private void validate(String filePath) {
        LOG.info("============= VALIDATION STARTED ================");
        int amount = 0, correct = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                amount += 1;
                correct = this.validateQuery(line) ? correct + 1 : correct;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        LOG.info("Validated queries: {}, correct queries predicted: {}/{}", amount, correct, amount);
        LOG.info("============= VALIDATION FINISHED ================");
    }

    private boolean validateQuery(String query) {
        String[] entries = query.split("::");

        HashMap<String, String> expected = new HashMap<>();
        HashMap<String, String> actual = new HashMap<>();

        for (int i = 1; i < entries.length; i++) {
            String[] temp = entries[i].split("=");
            expected.put(temp[0], temp[1]);
        }
        actual = this.extract(entries[0]);

        LOG.info("Query:    {}", entries[0]);
        LOG.info("Actual:   {}", actual);
        LOG.info("Expected: {}", expected);
        LOG.info("=============================");

        return Objects.equals(actual, expected);
    }
}

package demo.nlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IntentExtractor {

    private DoccatModel model;
    private static final Logger LOG = LoggerFactory.getLogger(IntentExtractor.class);

    public void train(String trainingDirectory) {
        this.training(trainingDirectory, 0, 50);
    }

    public void train(String trainingDirectory, int c, int iterations) {
        this.training(trainingDirectory, c, iterations);
    }

    private void training(String trainingDirectory, int c, int iterations) {
        try (InputStream inputStream = new FileInputStream(trainingDirectory + "/dataset.txt")) {
            ObjectStream<String> lineStream = new PlainTextByLineStream(inputStream, "UTF-8");
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
            model = DocumentCategorizerME.train("en", sampleStream, c, iterations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String parse(String sentence) {
        DocumentCategorizerME classifier = new DocumentCategorizerME(model);
        double[] outcomes = classifier.categorize(sentence);
        String category = classifier.getBestCategory(outcomes);
        String intent = null;

        switch (category) {
            case "0":
                intent = "hello";
                break;

            case "1":
                intent = "bye";
                break;

            case "2":
                intent = "book_room";
                break;

            default:
                intent = "NOT-RECOGNIZED";
        }

        return intent;
    }

}
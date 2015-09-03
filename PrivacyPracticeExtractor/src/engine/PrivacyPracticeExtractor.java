package engine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import engine.classifier.IClassifier;
import engine.preprocessing.IPreProcessor;
import engine.preprocessing.IStemmer;
import engine.preprocessing.ITokenizer;

import services.ISettingLoader;
import services.ITextReader;
import services.ITextWriter;
import services.LabelConverter;
import services.Logger;
import model.*;
import engine.preprocessing.*;
import services.*;

public class PrivacyPracticeExtractor {
	HashMap<Label, Tree<Label>> labelMapping;
	HashMap<Label, Text> preprocessedTexts;
	List<Label> labels;

	ITextReader textReader;
	ITextWriter textWriter;
	ISettingLoader settingLoader;
	IPreProcessor preProcessor;
	IClassifier classifier;

	Dictionary corpus;

	public PrivacyPracticeExtractor(ITextReader tReader, ITextWriter tWriter,
			ISettingLoader sLoader, IPreProcessor preProcessor,
			IClassifier classifier) {
		labelMapping = new HashMap<Label, Tree<Label>>();
		labels = new LinkedList<Label>();
		preprocessedTexts = new HashMap<Label, Text>();
		corpus = new Dictionary();

		this.textReader = tReader;
		this.textWriter = tWriter;
		this.settingLoader = sLoader;
		this.preProcessor = preProcessor;
		this.classifier = classifier;

		this.classifier.setRequiredData(labelMapping, labels);

	}

	public void extract(String text) {
		try {
			String pathToStructureFile = settingLoader
					.getPolicyTreeStructureFile();
			Tree<Label> rootLabel = LabelConverter.read(textReader,
					pathToStructureFile);
			mapLabels(rootLabel);
			
			corpus = DictionaryConverter
					.read(settingLoader.getCorpusFile());
			
			List<Label> prototypes = PrototypeConverter.read(settingLoader
					.getPrototypesFile());
			
			//load prototype vectors
			for(int i = 0; i < prototypes.size();i++){
				for(int k = 0; k < labels.size();k++){
					if(prototypes.get(i).getName().equals(labels.get(k).getName())){
						labels.get(k).setVector(prototypes.get(i).getFeature());
					}
				}
			}
			
			preProcessor.updateCorpus(corpus);
			
			Text processingText = preProcessor.processToText(text);
			processingText = readLabelData(settingLoader.getTrainingDataFolder()
						+ Label.getLabelPath(labelMapping.get(labels.get(1))));
			
			HashMap<Sentence, Vector> vectors = preProcessor.processToVector(processingText);
			
			Iterator it = vectors.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        Sentence sen = (Sentence)pair.getKey();
		        Label result = classifier.predictLabel((Vector)pair.getValue());
		        System.out.println(result.getName());
		        it.remove();
		    }

		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	public void trainExtractor(boolean useExistingCorpus, boolean saveCorpus) {
		try {
			String pathToStructureFile = settingLoader
					.getPolicyTreeStructureFile();
			Tree<Label> rootLabel = LabelConverter.read(textReader,
					pathToStructureFile);
			mapLabels(rootLabel);
			Label recent;
			String pathToTrainingData;
			Logger.info("Beginning training Data");

			// Reading training data and populating corpus
			for (int i = 1; i < labels.size(); i++) {
				recent = labels.get(i);
				pathToTrainingData = settingLoader.getTrainingDataFolder()
						+ Label.getLabelPath(labelMapping.get(recent));

				Text readTrainingText = readLabelData(pathToTrainingData);
				preprocessedTexts.put(recent, readTrainingText);
				if (!useExistingCorpus)
					corpus.populate(readTrainingText);
				Logger.info("Corpus Populated with Trainingdata from Label: "
						+ recent.getName());
			}

			if (useExistingCorpus)
				corpus = DictionaryConverter
						.read(settingLoader.getCorpusFile());

			if (!useExistingCorpus && saveCorpus) {
				DictionaryConverter
						.write(corpus, settingLoader.getCorpusFile());
			}
			// Mapping training data into vector space
			preProcessor.updateCorpus(corpus);
			Logger.info("Mapping into Vector Space...");
			HashMap<Vector, Label> trainingSet = prepareTrainingData();

			Logger.info("Classifier training started...");
			List<Label> results = classifier.train(trainingSet, rootLabel);

			PrototypeConverter
					.write(results, settingLoader.getPrototypesFile());
			Logger.info("Training complete: " + results.size());
		} catch (Exception err) {
			err.printStackTrace();
			Logger.error(err.getMessage());
		}
	}

	private HashMap<Vector, Label> prepareTrainingData() throws Exception {
		HashMap<Vector, Label> trainingSet = new HashMap<Vector, Label>();
		Label recent;
		for (int i = 1; i < labels.size(); i++) {
			recent = labels.get(i);
			Text next = preprocessedTexts.get(recent);
			HashMap<Sentence, Vector> vectors = preProcessor
					.processToVector(next);
			// Iterate through vectors hashmap
			Iterator it = vectors.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				trainingSet.put((Vector) pair.getValue(), recent);
				it.remove();
			}
			Logger.info("Data from Label: " + recent.getName()
					+ " mapped into Vector Space...");
		}
		return trainingSet;
	}

	private Text readLabelData(String pathToLabelFolder) throws IOException {
		File dir = new File(pathToLabelFolder);
		File[] directoryListing = dir.listFiles();
		StringBuffer readText = new StringBuffer();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if (child.isFile()) {
					readText.append(textReader.readText(child.getAbsolutePath()));
				}
			}
		}

		Text processedText = preProcessor.processToText((readText.toString()));
		return processedText;
	}

	/**
	 * Scan for all available labels
	 * 
	 * @param root
	 *            - Root node of tree structure
	 */
	private void mapLabels(Tree<Label> root) {
		labelMapping.put(root.getData(), root);
		labels.add(root.getData());

		for (Tree<Label> children : root.getChildren())
			mapLabels(children);
	}
}

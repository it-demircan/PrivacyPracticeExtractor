package engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import engine.classifier.IClassifier;
import engine.extractor.IExtractor;
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
	HashMap<Label, Integer> labelDocumentCounter; // Number of documents used to
													// train (each label)
	List<Label> labels;

	ITextReader textReader;
	ITextWriter textWriter;
	ISettingLoader settingLoader;
	IPreProcessor preProcessor;
	IClassifier classifier;
	IExtractor extractor;

	Dictionary corpus;

	public PrivacyPracticeExtractor(ITextReader tReader, ITextWriter tWriter,
			ISettingLoader sLoader, IPreProcessor preProcessor,
			IClassifier classifier, IExtractor extractor) {
		labelMapping = new HashMap<Label, Tree<Label>>();
		labels = new LinkedList<Label>();
		preprocessedTexts = new HashMap<Label, Text>();
		labelDocumentCounter = new HashMap<Label, Integer>();
		corpus = new Dictionary();

		this.textReader = tReader;
		this.textWriter = tWriter;
		this.settingLoader = sLoader;
		this.preProcessor = preProcessor;
		this.classifier = classifier;
		this.extractor = extractor;

		this.classifier.setRequiredData(labelMapping, labels);

	}

	public HashMap<Label, String> summarize(HashMap<Label, List<Sentence>> classifiedSentences) throws Exception{
		extractor.initialize();
		
		HashMap<Label, String> summa = extractor.extract(classifiedSentences);
		return summa;
	}

	public HashMap<Label, List<Sentence>> classifyPolicySentences(String text)
			throws Exception {
		HashMap<Label, List<Sentence>> classifiedSentences = new HashMap<Label, List<Sentence>>();
		Logger.info("Classification process starts...");
		prepareClassifier();
		
		for (int i = 0; i < labels.size(); i++) {
			classifiedSentences.put(labels.get(i), new LinkedList<Sentence>());
		}
		
		HashMap<Sentence, Vector> vectors = preProcessor.preProcessText(text);
		
		Iterator it = vectors.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Sentence sen = (Sentence) pair.getKey();
			Label result = classifier.predictLabel((Vector) pair
					.getValue());
			
			classifiedSentences.get(result).add(sen);
			it.remove();
		}
		Logger.info("Classification process finished...");
		return classifiedSentences;
	}

	/**
	 * This Class prepare data for evaluation
	 */
	public void evaluateClassifier() {
		try {
			/* ******************** */
			/* Preparation Process */
			/* ******************** */
			Logger.info("Validation process starts...");
			
			prepareClassifier();

			/* ******************************* */
			/* Extracting Process starts here */
			/* ******************************* */
			Text processingText;

			// Skip first Label
			List<Sentence> classified = new LinkedList<Sentence>();
			for (int i = 1; i < labels.size(); i++) {
				processingText = readLabelData(settingLoader
						.getTestDataFolder()
				// .getTrainingDataFolder()
						+ Label.getLabelPath(labelMapping.get(labels.get(i))));

				HashMap<Sentence, Vector> vectors = preProcessor
						.processToVector(processingText);

				Iterator it = vectors.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					Sentence sen = (Sentence) pair.getKey();
					Label result = classifier.predictLabel((Vector) pair
							.getValue());

					sen.setLabel(labels.get(i), result);
					classified.add(sen);
					it.remove();
				}
				Logger.info("Testing classifier with data from Label: "
						+ labels.get(i).getName());
			}

			Logger.info("Evaluation computations started..");
			Evaluator eva = new Evaluator(labels, labelMapping,
					labelDocumentCounter, corpus.getNumberOfSentences());
			eva.evaluate(classified);

			Logger.info("Evaluation Complete..");
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	

	/**
	 * Trains the Extractor
	 * 
	 * @param useExistingCorpus
	 *            - If you want to use a existing corpus file
	 * @param saveCorpus
	 *            - When you dont use a existing corpus file, you can save the
	 *            newly calculated corpus
	 * @param noWords
	 *            - Top k words for Feature Selection
	 */
	public void trainExtractor(boolean useExistingCorpus, boolean saveCorpus,
			int noWords) {
		try {
			String pathToStructureFile = settingLoader
					.getPolicyTreeStructureFile();
			Tree<Label> rootLabel = LabelConverter.read(textReader,
					pathToStructureFile);
			mapLabels(rootLabel);
			Label recent;
			String pathToTrainingData;
			Logger.info("Start training classifier");

			// Reading training data and populating corpus
			for (int i = 1; i < labels.size(); i++) {
				recent = labels.get(i);
				pathToTrainingData = settingLoader.getTrainingDataFolder()
						+ Label.getLabelPath(labelMapping.get(recent));

				Text readTrainingText = readLabelData(pathToTrainingData);
				preprocessedTexts.put(recent, readTrainingText);
				if (!useExistingCorpus)
					corpus.populate(readTrainingText);

				labelDocumentCounter.put(recent, readTrainingText
						.getSentences().size());
				Logger.info("Corpus Populated with Trainingdata from Label: "
						+ recent.getName());
			}

			if (useExistingCorpus) {
				corpus = DictionaryConverter
						.read(settingLoader.getCorpusFile());
				Logger.info("Corpus read from file...");
			} else {
				// feature selection
				Logger.info("Feature selection started...");
				corpus = preProcessor.reduceCorpus(labels,
						readLabelDataForFeatureSelection(), corpus, noWords);
				Logger.info("Feature selection finished...");
			}

			if (!useExistingCorpus && saveCorpus) {
				DictionaryConverter
						.write(corpus, settingLoader.getCorpusFile());
				Logger.info("Corpus saved...");
			}

			// Save the number of documents for each label
			LabelDocumentCounterConverter.write(labelDocumentCounter,
					settingLoader.getLabelDocumentCounterFile());
			Logger.info("Documents per Label saved...");

			/* **************************************** */
			/* Mapping training data into vector space */
			/* **************************************** */

			preProcessor.updateCorpus(corpus);
			Logger.info("Mapping into Vector Space...");
			HashMap<Vector, Label> trainingSet = prepareTrainingData();

			Logger.info("Classifier training started...");
			List<Label> results = classifier.train(trainingSet, rootLabel);

			PrototypeConverter
					.write(results, settingLoader.getPrototypesFile());
			Logger.info("Classifier training complete.");
		} catch (Exception err) {
			err.printStackTrace();
			Logger.error(err.getMessage());
		}
	}
	
	/**
	 * Loads data from storage to classify
	 * @throws Exception
	 */
	private void prepareClassifier() throws Exception {
		// prepare tree structure
		String pathToStructureFile = settingLoader.getPolicyTreeStructureFile();
		Tree<Label> rootLabel = LabelConverter.read(textReader,
				pathToStructureFile);
		mapLabels(rootLabel);
		Logger.info("Tree Structure for Policy loaded...");
		// load corpus
		corpus = DictionaryConverter.read(settingLoader.getCorpusFile());
		Logger.info("Corpus read from file...");
		// Load the number of documents for each label
		loadLabelDocumentCounter();
		Logger.info("Documents per Label counted...");

		// load prototype vectors and map them
		List<Label> prototypes = PrototypeConverter.read(settingLoader
				.getPrototypesFile());

		for (int i = 0; i < prototypes.size(); i++) {
			for (int k = 0; k < labels.size(); k++) {
				if (prototypes.get(i).getName().equals(labels.get(k).getName())) {
					labels.get(k).setVector(prototypes.get(i).getFeature());
				}
			}
		}

		Logger.info("Prototypes loaded...");

		// Inject loaded corpus
		preProcessor.updateCorpus(corpus);
		Logger.info("Corpus injected into Preprocessor...");
	}

	/**
	 * Prepares the trainings data for classification.
	 * 
	 * @return Vectors mapped with their classes/labels
	 */
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

	/**
	 * Read all relevant data for each label/class
	 */
	private HashMap<Label, Text> readLabelDataForFeatureSelection()
			throws IOException {
		HashMap<Label, Text> readData = new HashMap<Label, Text>();
		for (int i = 1; i < labels.size(); i++) {
			Text processingText = readLabelData(settingLoader
					.getTrainingDataFolder()
					+ Label.getLabelPath(labelMapping.get(labels.get(i))));
			readData.put(labels.get(i), processingText);
		}
		return readData;
	}

	/**
	 * Reads all data for a specific label/class
	 */
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
	 * Loads the mapping between labels and the number of documents(sentences)
	 * which belongs to this label
	 */
	private void loadLabelDocumentCounter() throws ClassNotFoundException,
			FileNotFoundException, IOException {
		HashMap<Label, Integer> recentMapping = LabelDocumentCounterConverter
				.read(settingLoader.getLabelDocumentCounterFile());
		labelDocumentCounter = new HashMap<Label, Integer>();
		Iterator it = recentMapping.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Label recentLabel = (Label) pair.getKey();

			for (int i = 0; i < labels.size(); i++) {
				if (recentLabel.compareTo(labels.get(i)) == 0)
					labelDocumentCounter.put(labels.get(i),
							(Integer) pair.getValue());
			}
			it.remove();
		}
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

package main;

import engine.*;
import services.*;

/* * Entry point of the privacy practice extractor.
 @author Muhammed Demircan

 */
public class Main {
	public static void main(String[] args) {
		int n = args.length;
		try {
			if (n == 0)
				throw new Exception(
						"Not enough input arguments - type -usage for more information and help.");
			PrivacyPracticeExtractor ppe = null;
			switch (args[0]) {
			case "-train":
				ppe = ExtractorFactory.createPrivacyPracticeExtractor(false);
				if (args[1] == null) {
					ppe.trainClassifier(false, true);
				} else if (args[1].equals("y")) {
					ppe.trainClassifier(true, false);
				} else if (args[1].equals("n")) {
					ppe.trainClassifier(false, true);
				}
				break;
			case "-ext":
				if (args[1] == null) {
					throw new Exception(
							"Path to privacy policy missing - type -usage as argument for more information and help.");
				} else {
					ppe = ExtractorFactory.createPrivacyPracticeExtractor(true);
					ITextReader tr = new TextReader();
					String privacy = tr.readText(args[1]);
					ppe.extract(privacy, "./");
				}
				break;
			case "-valid":
				ppe = ExtractorFactory.createPrivacyPracticeExtractor(false);
				ppe.evaluateClassifier();
				break;
			case "-usage":
				String infoText = "######### Help and Usage ##########\r\n";
				infoText += "\r\n";
				infoText += "\r\n";
				infoText += "-train (y/n) => starts the training. Type y as second argument to use an existing corpus, n otherwise.\r\n";
				infoText += "\r\n";
				infoText += "-valid => starts the evaluation process of a trained extractor.\r\n";
				infoText += "\r\n";
				infoText += "-ext <pathToPrivacyPolicy> => starts the extraction process for the given privacy policy (.txt).\r\n";
				Logger.info(infoText);
				break;
			default:
				throw new Exception(
						"Invalid mode selection. Type -usage as first argument for more information and help.");
			}
			
			Logger.saveLog();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
}

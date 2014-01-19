package poem;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.collections4.map.MultiKeyMap;

public class PoemQuery {
	public HashMap<Integer, String> commaTokenMap = new HashMap<Integer, String>();
	public MultiKeyMap wordSentenceMap = new MultiKeyMap();
	public HashMap<Character, Integer> wordCountMap = new HashMap<Character, Integer>();
	public HashMap<Character, LinkedHashMap<Character, Double>> wordwordProbMap = new HashMap<Character, LinkedHashMap<Character, Double>>();

	public HashSet<Integer> noYunSet = null;
	public int length = 0;
	public int poemYun = 0;

	private PrintWriter writer = null;

	public Object readObject(String filename) {
		Object object = null;
		try {
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			object = ois.readObject();
			ois.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	public PoemQuery() {
		System.out.println("Loading Knowledge base...");
		commaTokenMap = (HashMap<Integer, String>) readObject("commaToken.map");
		wordSentenceMap = (MultiKeyMap) readObject("wordSentence.map");
		wordCountMap = (HashMap<Character, Integer>) readObject("wordCount.map");
		wordwordProbMap = (HashMap<Character, LinkedHashMap<Character, Double>>) readObject("wordwordProb.map");

	}

	public String process(String poemString) {

		String fullPoemString = null;
		System.out.println("Preprocessing...");
		preprocess(poemString);
		System.out.println("Getting candidate words...");
		HashSet<Character> candidateWordSet = getCandidateWords(poemString);
		System.out.println(candidateWordSet.size());
		System.out.println("Getting candidate sentences...");
		HashSet<String> candidateSentenceSet = getCandidateSentences(
				candidateWordSet, poemYun);
		System.out.println(candidateSentenceSet.size());
		System.out.println("Calculating Probability...");
		String secondString = calcProb(poemString, candidateSentenceSet);
		System.out.println("2: " + secondString);
		String thirdString = getThird(poemString, secondString,
				candidateWordSet);
		System.out.println("3: " + thirdString);
		String fourthString = getFourth(thirdString);
		System.out.println("4: " + fourthString);
		fullPoemString = new String(poemString + "\n" + secondString + "\n"
				+ thirdString + "\n" + fourthString + "\n");
		writer.write(fullPoemString);
		System.out.println("全诗：\n" + fullPoemString);
		writer.close();
		return fullPoemString;
	}

	private void preprocess(String poemString) {

		length = poemString.length();
		poemYun = PinyinUtil.getYunMuInt(poemString.substring(length - 1));
		noYunSet = new HashSet<Integer>(PinyinUtil.yunMuSet);
		noYunSet.remove(poemYun);
		System.out.println(length);
		System.out.println("poemYun: " + Integer.toString(poemYun));
		try {
			writer = new PrintWriter("result.txt", "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// for (Integer yun : noYunSet) {
		// System.out.print(yun);
		// System.out.print(' ');
		// }
		// System.out.println();

	}

	public HashSet<Character> getCandidateWords(String poemString) {

		HashSet<Character> candidateWordSet = new HashSet<Character>();

		for (int i = 0; i < poemString.length(); i++) {
			Character character = poemString.charAt(i);
			LinkedHashMap<Character, Double> map = wordwordProbMap
					.get(character);
			ArrayList<Character> keys = new ArrayList<Character>(map.keySet());
			if (keys.size() < 6) {
				for (int j = keys.size() - 1; j >= 0; j--) {
					// System.out.println(keys.get(j) + " "
					// + map.get(keys.get(j)).toString());
					candidateWordSet.add(keys.get(j));
				}
			} else {
				for (int j = keys.size() - 1; j >= keys.size() - 6; j--) {
					// System.out.println(keys.get(j) + " "
					// + map.get(keys.get(j)).toString());
					candidateWordSet.add(keys.get(j));
				}
			}

		}

		return candidateWordSet;
	}

	public HashSet<String> getCandidateSentences(
			HashSet<Character> candidateWordSet, int yun) {
		HashSet<String> candidateSentenceSet = new HashSet<String>();
		for (Character character : candidateWordSet) {
			// System.out.println(character.toString());
			if (wordSentenceMap.containsKey(character, length, yun)) {
				HashSet<Integer> commaIndexSet = (HashSet<Integer>) wordSentenceMap
						.get(character, length, yun);
				HashSet<Integer> sampleSet;
				if (commaIndexSet.size() > 20) {
					List<Integer> list = new ArrayList<Integer>(commaIndexSet);
					Collections.shuffle(list);
					sampleSet = new HashSet<Integer>(list.subList(0, 10));
				} else {
					sampleSet = commaIndexSet;
				}
				for (Integer key : sampleSet) {
					String candidateString = (String) commaTokenMap.get(key);
					candidateSentenceSet.add(candidateString);
				}

			}

			// System.out.println(commaIndexSet.size());

		}
		return candidateSentenceSet;
	}

	public String calcProb(String poemString,
			HashSet<String> candidateSentenceSet) {

		String matchString = null;
		ArrayList<String> sentenceList = new ArrayList<String>(
				candidateSentenceSet);
		ArrayList<Double> probList = new ArrayList<Double>();
		LinkedHashMap<String, Double> probMap = new LinkedHashMap<String, Double>();
		for (String sentence : sentenceList) {
			double prob = 1;
			for (int i = 0; i < length; i++) {
				Character character = poemString.charAt(i);
				for (int j = 0; j < sentence.length(); j++) {
					Character character2 = sentence.charAt(j);
					if (wordwordProbMap.containsKey(character)) {
						if (wordwordProbMap.get(character).containsKey(
								character2)) {
							prob *= wordwordProbMap.get(character).get(
									character2);
						} else {
							prob *= 0.2 / wordCountMap.get(character);
						}
					} else {
						prob *= 0.5;
					}
				}

			}
			// probList.add(prob);
			probMap.put(sentence, prob);
		}

		probMap = (LinkedHashMap<String, Double>) MapUtil.sortByValue(probMap);
		ArrayList<String> keys = new ArrayList<String>(probMap.keySet());
		for (int i = keys.size() - 1; i >= 0; i--) {
			writer.println(keys.get(i) + " " + probMap.get(keys.get(i)));
		}
		writer.println();
		matchString = keys.get(keys.size() - 1);
		return matchString;
	}

	private String getThird(String poemString, String secondString,
			HashSet<Character> firstCandidateWordSet) {
		String thirdString = null;
		HashSet<Character> secondCandidateWordSet = getCandidateWords(secondString);
		firstCandidateWordSet.addAll(secondCandidateWordSet);
		ArrayList<Integer> list = new ArrayList<Integer>(noYunSet);
		Collections.shuffle(list);
		int yun = list.get(0);
		HashSet<String> candidateSentenceSet = getCandidateSentences(
				firstCandidateWordSet, yun);
		thirdString = calcProb(secondString, candidateSentenceSet);
		return thirdString;
	}

	private String getFourth(String thirdString) {
		String fourthString = null;
		HashSet<Character> candidateWordSet = getCandidateWords(thirdString);
		HashSet<String> candidateSentenceSet = getCandidateSentences(
				candidateWordSet, poemYun);
		fourthString = calcProb(thirdString, candidateSentenceSet);

		return fourthString;
	}

	public static void main(String[] args) throws Exception {
		CommandLineParser lineParser = new PosixParser(); // Apache CommonCLI
		Options options = new Options();
		options.addOption("i", "input", true, "input poem query string");

		CommandLine cmdLine = lineParser.parse(options, args);
		HelpFormatter formatter = new HelpFormatter();

		if (cmdLine.hasOption("i") == true) {
			String poemString = cmdLine.getOptionValue("i");
			System.out.println(poemString);
			if (poemString.length() != 5 && poemString.length() != 7) {
				System.out.println("Length not supported");
				return;
			}
			PoemQuery pq = new PoemQuery();
			String poemFullString = pq.process(poemString);

		} else {
			formatter.printHelp("PoemQuery", options);
		}

	}
}

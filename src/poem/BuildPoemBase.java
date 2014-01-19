package poem;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BuildPoemBase {
	ArrayList<Poem> poemArrayList = new ArrayList<Poem>();
	public ArrayList<Sentence> sentences5 = new ArrayList<Sentence>();
	public ArrayList<Sentence> sentences7 = new ArrayList<Sentence>();

	public HashMap<Integer, Sentence> sentenceMap = new HashMap<Integer, Sentence>();
	public HashMap<Integer, String> commaTokenMap = new HashMap<Integer, String>();
	// public HashMap<Character, HashMap<Integer, HashMap<Integer,
	// ArrayList<Integer>>>> wordSentenceMap = new HashMap<Character,
	// HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>>();
	public MultiKeyMap wordSentenceMap = new MultiKeyMap();
	public HashMap<Character, Integer> wordCountMap = new HashMap<Character, Integer>();
	public MultiKeyMap wordwordCountMap = new MultiKeyMap();
	public HashMap<Character, LinkedHashMap<Character, Double>> wordwordProbMap = new HashMap<Character, LinkedHashMap<Character, Double>>();

	// public HashMap<Character, ArrayList<Character>> wordProbSort = new
	// HashMap<Character, ArrayList<Character>>();

	public void readPoems(String poemPathString) {
		Gson gsBuilderGson = (new GsonBuilder()).disableHtmlEscaping().create();
		try {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(poemPathString), "UTF-8"));
			String line;
			while ((line = bReader.readLine()) != null) {
				Poem poem = gsBuilderGson.fromJson(line, Poem.class);
				poemArrayList.add(poem);
			}
			bReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void separate() {
		for (Poem poem : poemArrayList) {
			// System.out.println(poem.title);
			// System.out.println(poem.content);
			PoemTokened ptPoemTokened = new PoemTokened(poem);
			ArrayList<Sentence> poemSentences = ptPoemTokened.getSentences();
			if (poemSentences != null) {
				for (Sentence sentence : poemSentences) {
					if (sentence.length == 5) {
						sentences5.add(sentence);
					} else if (sentence.length == 7) {
						sentences7.add(sentence);
					}
				}

			}
		}
	}

	public void buildSentenceIndex() {
		Integer sentenceKey = new Integer(1);
		for (Sentence sentence : sentences5) {
			sentenceMap.put(sentenceKey, sentence);
			commaTokenMap.put(2 * sentenceKey, sentence.leftString);
			commaTokenMap.put(2 * sentenceKey + 1, sentence.rightString);
			sentenceKey += 1;
		}
		for (Sentence sentence : sentences7) {
			sentenceMap.put(sentenceKey, sentence);
			commaTokenMap.put(2 * sentenceKey, sentence.leftString);
			commaTokenMap.put(2 * sentenceKey + 1, sentence.rightString);
			sentenceKey += 1;
		}
		// System.out.println(sentenceKey);
		// System.out.println(sentenceMap.get(200000));
		writeObject(commaTokenMap, "commaToken.map");

	}

	public void writeObject(Object object, String filename) {
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void buildWordSentenceIndex(Character character, Integer length,
			Integer Yun, Integer commaTokenKey) {
		if (wordSentenceMap.containsKey(character, length, Yun)) {
			HashSet<Integer> commaIndexSet = (HashSet<Integer>) wordSentenceMap
					.get(character, length, Yun);
			commaIndexSet.add(commaTokenKey);
			wordSentenceMap.put(character, length, Yun, commaIndexSet);
		} else {
			HashSet<Integer> commaIndexSet = new HashSet<Integer>();
			commaIndexSet.add(commaTokenKey);
			wordSentenceMap.put(character, length, Yun, commaIndexSet);
		}
	}

	private void wordCount(Character character) {
		if (wordCountMap.containsKey(character)) {
			Integer countInteger = wordCountMap.get(character);
			countInteger += 1;
			wordCountMap.put(character, countInteger);
		} else {
			wordCountMap.put(character, new Integer(1));
		}
	}

	public void buildWordIndex() {
		/* build word-sentence index */
		for (Entry<Integer, Sentence> entry : sentenceMap.entrySet()) {
			Integer sentenceKey = entry.getKey();
			Sentence sentence = entry.getValue();
			Integer leftKey = 2 * sentenceKey;
			Integer rightKey = 2 * sentenceKey + 1;
			Integer leftYun = sentence.leftYunInt;
			Integer rightYun = sentence.rightYunInt;
			int length = sentence.length;
			for (int i = 0; i < length; i++) {
				Character characterLeft = sentence.leftString.charAt(i);
				Character characterRight = sentence.rightString.charAt(i);
				// left part word-sentence index
				buildWordSentenceIndex(characterLeft, length, leftYun, leftKey);
				// right part word-sentence index
				buildWordSentenceIndex(characterRight, length, rightYun,
						rightKey);
				wordCount(characterLeft);
				wordCount(characterRight);
				// word count index

			}
		}
		writeObject(wordSentenceMap, "wordSentence.map");
		writeObject(wordCountMap, "wordCount.map");
		// calc transition counts
		for (Entry<Integer, Sentence> entry : sentenceMap.entrySet()) {
			Integer sentenceKey = entry.getKey();
			Sentence sentence = entry.getValue();
			int length = sentence.length;
			for (int i = 0; i < length; i++) {
				Character characterLeft = sentence.leftString.charAt(i);
				for (int j = 0; j < length; j++) {
					Character characterRight = sentence.rightString.charAt(j);
					if (wordwordCountMap.containsKey(characterLeft,
							characterRight)) {
						Integer countInteger = (Integer) wordwordCountMap.get(
								characterLeft, characterRight);
						countInteger += 1;
						wordwordCountMap.put(characterLeft, characterRight,
								countInteger);
					} else {
						wordwordCountMap.put(characterLeft, characterRight, 1);
					}

				}
			}

		}

		// calc transition probs
		MapIterator iterator = wordwordCountMap.mapIterator();
		while (iterator.hasNext()) {
			iterator.next();
			MultiKey mk = (MultiKey) iterator.getKey();
			Character leftChar = (Character) mk.getKey(0);
			Character rightChar = (Character) mk.getKey(1);
			// System.out.println(leftChar);
			Integer wordwordCount = (Integer) iterator.getValue();
			Integer wordCount = (Integer) wordCountMap.get(leftChar);
			Double prob = (new Double(wordwordCount) + 1)
					/ (new Double(wordCount) + 1);
			// wordwordProbMap.put(leftChar, rightChar, prob);
			if (wordwordProbMap.containsKey(leftChar)) {
				LinkedHashMap<Character, Double> map = wordwordProbMap
						.get(leftChar);
				map.put(rightChar, prob);
				wordwordProbMap.put(leftChar, map);
			} else {
				LinkedHashMap<Character, Double> map = new LinkedHashMap<Character, Double>();
				map.put(rightChar, prob);
				wordwordProbMap.put(leftChar, map);
			}
		}
		// sort word-word prob
		for (Entry<Character, LinkedHashMap<Character, Double>> entry : wordwordProbMap
				.entrySet()) {
			Character leftCharacter = entry.getKey();
			LinkedHashMap<Character, Double> map = entry.getValue();
			map = (LinkedHashMap<Character, Double>) MapUtil.sortByValue(map);
			// ArrayList<Character> keys = new
			// ArrayList<Character>(map.keySet());
			// for (int i = keys.size() - 1; i >= 0; i--) {
			// System.out.println(map.get(keys.get(i)));
			// }
			wordwordProbMap.put(leftCharacter, map);
		}

		writeObject(wordwordProbMap, "wordwordProb.map");

		// for (Entry<Character, Integer> entry : wordCountMap.entrySet()) {
		// Character character = entry.getKey();
		// Integer countInteger = entry.getValue();
		// System.out.println(character.toString() + countInteger.toString());
		// }
		// MapIterator iterator = wordSentenceMap.mapIterator();
		// while (iterator.hasNext()) {
		// Object key = iterator.next();
		// HashSet<Integer> commaIntSet = (HashSet<Integer>) iterator
		// .getValue();
		// for (Integer integer : commaIntSet) {
		// System.out.println(integer);
		// }
		//
		// }

	}

	public void process(String poemPathString) {
		System.out.println("Reading poems...");
		readPoems(poemPathString);
		System.out.println("Separating sentences...");
		separate();
		System.out.println("Building sentence index...");
		buildSentenceIndex();
		System.out.println("Building word index...");
		buildWordIndex();
		System.out.println("Finished building poem knowledge base.");
	}

	public static void main(String[] args) throws Exception {
		CommandLineParser lineParser = new PosixParser(); // Apache CommonCLI
		Options options = new Options();
		options.addOption("i", "input", true, "path of input json poem file");

		CommandLine cmdLine = lineParser.parse(options, args);
		HelpFormatter formatter = new HelpFormatter();

		if (cmdLine.hasOption("i") == true) {
			String poemPathString = cmdLine.getOptionValue("i");
			System.out.println(poemPathString);
			BuildPoemBase bpb = new BuildPoemBase();
			bpb.process(poemPathString);
			// System.out.println(bpb.sentences5.size());
			// System.out.println(bpb.sentences7.size());
		} else {
			formatter.printHelp("BuildPoemBase", options);
		}

	}
}
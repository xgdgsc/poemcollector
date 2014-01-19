package poem;

import java.util.ArrayList;

public class PoemTokened {
	public ArrayList<Sentence> sentences = new ArrayList<Sentence>();

	// public int length = 0;

	public PoemTokened(Poem poem) {
		String[] sentenceString = null;
		if (poem.content != null && poem.content.indexOf("__") == -1) {
			sentenceString = poem.content.split("。");
		}

		if (sentenceString != null) {
			for (String sentence : sentenceString) {
				// System.out.println(sentence);
				Sentence s = new Sentence(sentence);
				if (s.length == 5 || s.length == 7) {

					sentences.add(s);

				} else {
					return;
				}
			}
		}
	}

	public ArrayList<Sentence> getSentences() {
		return sentences;
	}
}

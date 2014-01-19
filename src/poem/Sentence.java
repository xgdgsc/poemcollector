package poem;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Sentence implements Serializable {

	private static final long serialVersionUID = 1L;
	// public ArrayList<String> words;
	public int length = 0;
	public String leftString = null;
	public String rightString = null;
	// public String leftYunString = null;
	// public String rightYunString = null;
	public int leftYunInt = -1;
	public int rightYunInt = -1;
	String sentenceStr = null;
	// public static Set<String> yunMuSet = new HashSet<String>();
	public static Set<Integer> yunMuIntSet = new HashSet<Integer>();

	public Sentence(String sentenceString) {
		sentenceStr = sentenceString;
		String[] commaTokenStrings = sentenceString.split("，");

		if (commaTokenStrings.length == 2) {
			commaTokenStrings[0] = commaTokenStrings[0].trim();
			commaTokenStrings[1] = commaTokenStrings[1].trim();
			if (commaTokenStrings[0].length() == 5
					&& commaTokenStrings[1].length() == 5) {
				length = 5;

				// System.out.println(commaTokenStrings.length);
				// System.out.println(sentenceString);
			} else if (commaTokenStrings[0].length() == 7
					&& commaTokenStrings[1].length() == 7) {
				length = 7;
				// System.out.println(sentenceString);
			} else {
				return;
			}

		} else {
			return;
		}
		leftString = commaTokenStrings[0];
		rightString = commaTokenStrings[1];

		// leftYunString = PinyinUtil.getYunMu(leftString.substring(leftString
		// .length() - 1));
		// rightYunString =
		// PinyinUtil.getYunMu(rightString.substring(rightString
		// .length() - 1));
		leftYunInt = PinyinUtil.getYunMuInt(leftString.substring(leftString
				.length() - 1));
		rightYunInt = PinyinUtil.getYunMuInt(rightString.substring(rightString
				.length() - 1));
		// if (leftYunString.equals("er") || rightYunString.equals("er")) {
		// System.out.println(sentenceStr);
		// }
		// if (leftYunString != "NoPY" && rightYunString != "NoPY") {
		// yunMuSet.add(leftYunString);
		// yunMuSet.add(rightYunString);
		// }
		yunMuIntSet.add(leftYunInt);
		yunMuIntSet.add(rightYunInt);
		// System.out.println(yunMuSet.size());

	}

}

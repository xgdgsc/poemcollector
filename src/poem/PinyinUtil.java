package poem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class PinyinUtil {

	// public static int count = 0;
	public static final Set<Integer> yunMuSet = new HashSet<Integer>(
			Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));

	public static String getPinYin(String inputString) {
		// System.out.println(count);
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);

		char[] input = inputString.trim().toCharArray();
		StringBuffer output = new StringBuffer("");

		try {
			for (int i = 0; i < input.length; i++) {
				if (Character.toString(input[i]).matches("[\u4E00-\u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							input[i], format);
					if (temp != null) {
						output.append(temp[0]);
						// output.append(" ");

					} else {
						// count += 1;
						return "NoPY";
					}
				} else
					output.append(Character.toString(input[i]));
			}
		} catch (Exception e) {
			System.out.println(inputString);
			e.printStackTrace();
		}
		return output.toString();
	}

	public static String getYunMu(String inputString) {
		String pinyin = getPinYin(inputString);
		String YunMu = null;
		String yunString = "aeiou";
		if (pinyin != null) {
			if (pinyin.equals("NoPY")) {
				return pinyin;
			}
			if (pinyin.equals("ye")) {
				return pinyin;
			}
			if (yunString.indexOf(pinyin.charAt(0)) != -1) {
				// System.out.println(pinyin.charAt(0));
				return pinyin;
			} else {
				if (pinyin.length() == 1) {
					return pinyin;
				}
				String yunMuTemp = pinyin.substring(1, pinyin.length());
				// System.out.println(yunMuTemp);
				if (yunMuTemp.charAt(0) == 'h') {
					// System.out.println(yunMuTemp);
					YunMu = yunMuTemp.substring(1, yunMuTemp.length());

				} else {
					// System.out.println(yunMuTemp);
					YunMu = yunMuTemp;
				}
			}
		} else {
			return "NoPY";
		}
		return YunMu;
	}

	public static int getYunMuInt(String inputString) {
		int ym = 0;
		String YunMu = getYunMu(inputString);
		if (YunMu.equals("a") || YunMu.equals("ia") || YunMu.equals("ua")) {
			ym = 1;
		} else if (YunMu.equals("ai") || YunMu.equals("uai")) {
			ym = 2;
		} else if (YunMu.equals("an") || YunMu.equals("ian")
				|| YunMu.equals("uan") || YunMu.equals("van")) {
			ym = 3;
		} else if (YunMu.equals("ang") || YunMu.equals("iang")
				|| YunMu.equals("uang")) {
			ym = 4;
		} else if (YunMu.equals("ao") || YunMu.equals("iao")) {
			ym = 5;
		} else if (YunMu.equals("e") || YunMu.equals("o") || YunMu.equals("uo")) {
			ym = 6;
		} else if (YunMu.equals("ei") || YunMu.equals("ui")) {
			ym = 7;
		} else if (YunMu.equals("en") || YunMu.equals("in")
				|| YunMu.equals("un") || YunMu.equals("vn")) {
			ym = 8;
		} else if (YunMu.equals("eng") || YunMu.equals("ing")
				|| YunMu.equals("ong") || YunMu.equals("iong")) {
			ym = 9;
		} else if (YunMu.equals("er")) {
			ym = 10;
		} else if (YunMu.equals("i")) {
			ym = 11;
		} else if (YunMu.equals("ie") || YunMu.equals("ye")) {
			// if (YunMu.equals("ye")) {
			// System.out.println(inputString);
			// }
			ym = 12;
		} else if (YunMu.equals("ou") || YunMu.equals("iu")) {
			ym = 13;
		} else if (YunMu.equals("u")) {
			ym = 14;
		} else if (YunMu.equals("v")) {
			ym = 15;
		} else if (YunMu.equals("ve") || YunMu.equals("ue")) {
			// System.out.println(inputString);
			ym = 16;
		}

		// if (ym == 0) {
		// System.out.println(inputString);
		// System.out.println(YunMu);
		// }
		return ym;
	}

	public static void main(String[] args) {
		String chs = "概率";
		System.out.println(chs);
		System.out.println(getPinYin(chs));
		for (Integer yun : yunMuSet) {
			System.out.println(yun);

		}
	}
}

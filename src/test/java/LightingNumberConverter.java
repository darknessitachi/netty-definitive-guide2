import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Darkness
 * @date 2017年4月17日 下午2:17:26
 * @version 1.0
 * @since 1.0
 */
public class LightingNumberConverter {

	private int emptyCell;

	List<List<Integer>> lightingNos;

	public LightingNumberConverter(int row, int cell, int emptyCell) {
		this.emptyCell = emptyCell;

		lightingNos = new ArrayList<>();
		if (emptyCell != -1) {
			cell -= 1;
		}
		for (int i = 0; i < cell; i++) {
			List<Integer> nos = new ArrayList<>(row);
			for (int j = 0; j < row; j++) {
				int number = i * row + (j + 1);
				nos.add(number);
			}
			lightingNos.add(nos);
		}
		Collections.reverse(lightingNos);
	}

	private int convert(int cell, int row) {
		if (emptyCell != -1 && cell > emptyCell) {
			cell -= 1;
		}
		return lightingNos.get(cell - 1).get(row - 1);
	}

	public static void main(String[] args) {
		LightingNumberConverter lightingNumberConverter = new LightingNumberConverter(3, 5, -1);
		int lightingNo = lightingNumberConverter.convert(1, 1);
		System.out.println(lightingNo + ", " + (lightingNo == 13));

		lightingNumberConverter = new LightingNumberConverter(3, 5, 3);
		lightingNo = lightingNumberConverter.convert(1, 1);
		System.out.println(lightingNo + ", " + (lightingNo == 10));
	}

}

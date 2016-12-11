package start1;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Test {

	// 丸括弧なしメソッド呼び出しテスト *************
	public int length = 100;

	public int length() {
		return 3000;
	}
	// ***********************************************


	public static void main(String[] args) {
		Optional<String> option = Optional.of("a");
		@SuppressWarnings("unused")
		String s = option.orElseGet(() ->  "Ooops!"); // 空の場合Suplierが返す値を返す
		new File("").getAbsolutePath();

		List<String> friends = Arrays.asList(new String[]{"bob","keit","john","dug"});
		friends.stream().filter(f -> f.contains("b")).forEach(System.out::println);
		friends.stream().filter(f -> f.contains("b")).filter(f -> f.contains("l")).forEach(System.out::println);
		friends.stream().filter(f -> f.contains("b") && f.contains("l")).forEach(System.out::println);
		friends.stream().filter(f -> f.contains("b")).toArray(); // to Object[]
		friends.stream().filter(f -> f.contains("b")).collect(Collectors.toList());

		int n[] = {1};
		System.out.println(n[0]);

	}

	@SuppressWarnings("unused")
	private static void test() {
		//int[] i = new int[]{1,2,3,4,5,6,7};
		//i[10] = 10;

//		int[] n1 = new int[-1]; // NegativeArraySizeException

		int[] n1 = {100,200};
		//Array.newInstance(int.class, n1);
		System.out.println(n1.length);
		n1[0] = 1;

		String[] s1 = {"","2"};
		Array.newInstance(String.class, 3);
		// s1[2] = "a"; // ArrayIndexOutOfBoundsException
		// System.out.println(s1[2]); // ArrayIndexOutOfBoundsException

		int n3[] = {100,1000,100000};
		//Array.newInstance(int.class, 2);
		System.out.println(n3[2]); // 適格


		new ArrayList<>().get(0); // IndexOutOfBoundsException
		new int[]{}[0] = 0; // ArrayIndexOutOfBoundsException
		Integer[] n = new Integer[10];
		Number[] ns = n; // コンパイルできてしまう
		ns[0] = new Long(0); // コンパイルエラーにならない 実行時にArrayStoreException
	}
}


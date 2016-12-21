package start2;

import java.util.function.Consumer;

public class Test {
	public static void doSomething(Consumer<Integer> c) {
		c.accept(100);
	}
}

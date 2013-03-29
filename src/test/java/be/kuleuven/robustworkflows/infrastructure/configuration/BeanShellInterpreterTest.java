package be.kuleuven.robustworkflows.infrastructure.configuration;

import java.io.InputStreamReader;

import bsh.Interpreter;



public class BeanShellInterpreterTest {

	public static void main(String[] args) {
		
		Interpreter bsh = new Interpreter(new InputStreamReader(System.in), System.out, System.err, true);
		bsh.run();
	}
}

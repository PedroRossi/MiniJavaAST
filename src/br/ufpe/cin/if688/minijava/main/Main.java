package br.ufpe.cin.if688.minijava.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import br.ufpe.cin.if688.minijava.antlr.MiniJavaLexer;
import br.ufpe.cin.if688.minijava.antlr.MiniJavaParser;
import br.ufpe.cin.if688.minijava.antlr.MiniJavaParser.GoalContext;
import br.ufpe.cin.if688.minijava.antlr.MiniJavaASTVisitor;
import br.ufpe.cin.if688.minijava.ast.Program;
import br.ufpe.cin.if688.minijava.visitor.PrettyPrintVisitor;

@SuppressWarnings("deprecation")
public class Main {

	private static GoalContext fromFileToGoalContext(String filePath) throws IOException {
		InputStream stream = new FileInputStream(filePath);
		ANTLRInputStream input = new ANTLRInputStream(stream);
		MiniJavaLexer lexer = new MiniJavaLexer(input);
		CommonTokenStream token = new CommonTokenStream(lexer);
		return new MiniJavaParser(token).goal();
	}
	
	public static void main(String[] args) throws IOException {
		String [] tests = new String[6];
		tests[0] = "test/resources/BinarySearch.java";
		tests[1] = "test/resources/BinaryTree.java";
		tests[2] = "test/resources/BubbleSort.java";
		tests[3] = "test/resources/LinearSearch.java";
		tests[4] = "test/resources/LinkedList.java";
		tests[5] = "test/resources/QuickSort.java";
		for (String test : tests) {
			Program p = (Program) new MiniJavaASTVisitor().visit(fromFileToGoalContext(test));	
			PrettyPrintVisitor ppv = new PrettyPrintVisitor();
			ppv.visit(p);
		}
	}

}

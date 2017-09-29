package br.ufpe.cin.if688.minijava.antlr;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import br.ufpe.cin.if688.minijava.antlr.MiniJavaParser.ClassDeclarationContext;
import br.ufpe.cin.if688.minijava.antlr.MiniJavaParser.ExpressionContext;
import br.ufpe.cin.if688.minijava.antlr.MiniJavaParser.GoalContext;
import br.ufpe.cin.if688.minijava.antlr.MiniJavaParser.IdentifierContext;
import br.ufpe.cin.if688.minijava.antlr.MiniJavaParser.MainClassContext;
import br.ufpe.cin.if688.minijava.antlr.MiniJavaParser.MethodDeclarationContext;
import br.ufpe.cin.if688.minijava.antlr.MiniJavaParser.StatementContext;
import br.ufpe.cin.if688.minijava.antlr.MiniJavaParser.TypeContext;
import br.ufpe.cin.if688.minijava.antlr.MiniJavaParser.VarDeclarationContext;
import br.ufpe.cin.if688.minijava.ast.And;
import br.ufpe.cin.if688.minijava.ast.ArrayAssign;
import br.ufpe.cin.if688.minijava.ast.ArrayLength;
import br.ufpe.cin.if688.minijava.ast.ArrayLookup;
import br.ufpe.cin.if688.minijava.ast.Assign;
import br.ufpe.cin.if688.minijava.ast.Block;
import br.ufpe.cin.if688.minijava.ast.BooleanType;
import br.ufpe.cin.if688.minijava.ast.Call;
import br.ufpe.cin.if688.minijava.ast.ClassDecl;
import br.ufpe.cin.if688.minijava.ast.ClassDeclExtends;
import br.ufpe.cin.if688.minijava.ast.ClassDeclList;
import br.ufpe.cin.if688.minijava.ast.ClassDeclSimple;
import br.ufpe.cin.if688.minijava.ast.Exp;
import br.ufpe.cin.if688.minijava.ast.ExpList;
import br.ufpe.cin.if688.minijava.ast.False;
import br.ufpe.cin.if688.minijava.ast.Formal;
import br.ufpe.cin.if688.minijava.ast.FormalList;
import br.ufpe.cin.if688.minijava.ast.Identifier;
import br.ufpe.cin.if688.minijava.ast.If;
import br.ufpe.cin.if688.minijava.ast.IntArrayType;
import br.ufpe.cin.if688.minijava.ast.IntegerLiteral;
import br.ufpe.cin.if688.minijava.ast.IntegerType;
import br.ufpe.cin.if688.minijava.ast.LessThan;
import br.ufpe.cin.if688.minijava.ast.MainClass;
import br.ufpe.cin.if688.minijava.ast.MethodDecl;
import br.ufpe.cin.if688.minijava.ast.MethodDeclList;
import br.ufpe.cin.if688.minijava.ast.Minus;
import br.ufpe.cin.if688.minijava.ast.NewArray;
import br.ufpe.cin.if688.minijava.ast.NewObject;
import br.ufpe.cin.if688.minijava.ast.Not;
import br.ufpe.cin.if688.minijava.ast.Plus;
import br.ufpe.cin.if688.minijava.ast.Print;
import br.ufpe.cin.if688.minijava.ast.Program;
import br.ufpe.cin.if688.minijava.ast.Statement;
import br.ufpe.cin.if688.minijava.ast.StatementList;
import br.ufpe.cin.if688.minijava.ast.This;
import br.ufpe.cin.if688.minijava.ast.Times;
import br.ufpe.cin.if688.minijava.ast.True;
import br.ufpe.cin.if688.minijava.ast.Type;
import br.ufpe.cin.if688.minijava.ast.VarDecl;
import br.ufpe.cin.if688.minijava.ast.VarDeclList;
import br.ufpe.cin.if688.minijava.ast.While;

public class MiniJavaASTVisitor implements MiniJavaVisitor<Object> {

	@Override
	public Object visit(ParseTree arg0) {
		return arg0.accept(this);
	}

	@Override
	public Object visitChildren(RuleNode arg0) {
		return null;
	}

	@Override
	public Object visitErrorNode(ErrorNode arg0) {
		return null;
	}

	@Override
	public Object visitTerminal(TerminalNode arg0) {
		return null;
	}

	@Override
	public Object visitIdentifier(IdentifierContext ctx) {
		return null;
	}

	@Override
	public Object visitGoal(GoalContext ctx) {
		MainClass mainClass = (MainClass) ctx.mainClass().accept(this);
		ClassDeclList classList = new ClassDeclList();

		for (ClassDeclarationContext cdc : ctx.classDeclaration()) {
			classList.addElement((ClassDecl) cdc.accept(this));
		}

		return new Program(mainClass, classList);
	}

	@Override
	public Object visitMainClass(MainClassContext ctx) {
		Identifier nomeClasse = (Identifier) ctx.identifier(0).accept(this);
		Identifier nomeParametro = (Identifier) ctx.identifier(1).accept(this);
		Statement codigoMain = (Statement) ctx.statement().accept(this);
		return new MainClass(nomeClasse, nomeParametro, codigoMain);
	}
	
	@Override
	public Object visitClassDeclaration(ClassDeclarationContext ctx) {
		Identifier nomeClasse = (Identifier) ctx.identifier(0).accept(this);

		VarDeclList varDeclarationList = new VarDeclList();
		for (VarDeclarationContext vdc : ctx.varDeclaration()) {
			varDeclarationList.addElement((VarDecl) vdc.accept(this));
		}

		MethodDeclList methodDeclarationList = new MethodDeclList();
		for (MethodDeclarationContext mdc : ctx.methodDeclaration()) {
			methodDeclarationList.addElement((MethodDecl) mdc.accept(this));
		}

		if (ctx.identifier().size() == 1) {
			return new ClassDeclSimple(nomeClasse, varDeclarationList, methodDeclarationList);
		} else {
			Identifier classeExtendida = (Identifier) ctx.identifier(1).accept(this);
			return new ClassDeclExtends(nomeClasse, classeExtendida, varDeclarationList, methodDeclarationList);
		}
	}
	
	@Override
	public Object visitVarDeclaration(VarDeclarationContext ctx) {
		Type tipo = (Type) ctx.type().accept(this);
		Identifier nomeVariavel = (Identifier) ctx.identifier().accept(this);
		return new VarDecl(tipo, nomeVariavel);
	}
	
	@Override
	public Object visitMethodDeclaration(MethodDeclarationContext ctx) {
		Type tipo = (Type) ctx.type(0).accept(this);
		Identifier nomeMetodo = (Identifier) ctx.identifier(0).accept(this);

		FormalList formatList = new FormalList();
		for (int i = 1; i < ctx.type().size(); i++) {
			formatList.addElement(new Formal((Type) ctx.type(i).accept(this), (Identifier) ctx.identifier(i).accept(this)));
		}

		VarDeclList varDeclarationList = new VarDeclList();
		for (VarDeclarationContext vdc : ctx.varDeclaration()) {
			varDeclarationList.addElement((VarDecl) vdc.accept(this));
		}

		StatementList statementList = new StatementList();
		for (StatementContext sc : ctx.statement()) {
			statementList.addElement((Statement) sc.accept(this));
		}

		Exp exp = (Exp) ctx.expression().accept(this);

		return new MethodDecl(tipo, nomeMetodo, formatList, varDeclarationList, statementList, exp);
	}

	@Override
	public Object visitType(TypeContext ctx) {
		String val = ctx.getText();
		Object tipo = null;
		switch(val) {
		case "int":
			tipo = new IntegerType();
			break;
		case "int[]":
			tipo = new IntArrayType();
			break;
		case "boolean":
			tipo = new BooleanType();
			break;
		default:
			tipo = new Identifier(val);
		}
		return tipo;
	}
	
	@Override
	public Object visitStatement(StatementContext ctx) {
		String token = ctx.getStart().getText();
		Object ret = null;
		
		switch(token) {
		case "{":
			StatementList stmList = new StatementList();
			
			for (StatementContext stmCtx : ctx.statement())
				stmList.addElement((Statement) stmCtx.accept(this));
			
			ret = new Block(stmList);
			break;
		case "if":
			Exp ifExp = (Exp) ctx.expression().get(0).accept(this);
			
			Statement ifStm = (Statement) ctx.statement().get(0).accept(this);
			Statement elseStm = (Statement) ctx.statement().get(1).accept(this);
			
			ret = new If(ifExp, ifStm, elseStm);
			break;
		case "while":
			Exp whileExp = (Exp) ctx.expression().get(0).accept(this);
			
			Statement whileStm = (Statement) ctx.statement().get(0).accept(this);
			
			ret = new While(whileExp, whileStm);
			break;
		case "System.out.println":
			Exp sysoExp = (Exp) ctx.expression().get(0).accept(this);
			
			ret = new Print(sysoExp);
			break;
		default:
			Identifier id = (Identifier) ctx.identifier().accept(this);
			
			Exp exp1 = (Exp) ctx.expression().get(0).accept(this);
			
			if (ctx.expression().size() == 1) {
				ret = new Assign(id, exp1);
			} else {
				Exp exp2 = (Exp) ctx.expression().get(0).accept(this);
				ret = new ArrayAssign(id, exp1, exp2);
			}
		}
		
		return ret;
	}
	
	@Override
	public Object visitExpression(ExpressionContext ctx) {
		int expLen = ctx.expression().size();
		int childLen = ctx.getChildCount();
		
		String start = ctx.getStart().getText();

		Object ret = null;
		
		if (childLen >= 5) {
			Exp exp = (Exp) ctx.expression(0).accept(this);
			Identifier id = (Identifier) ctx.identifier().accept(this);

			ExpList expList = new ExpList();
			for (int i = 1; i < ctx.expression().size(); i++)
				expList.addElement((Exp) ctx.expression(i).accept(this));

			ret = new Call(exp, id, expList);
		} else if (expLen == 2) {
			Exp exp1 = (Exp) ctx.expression(0).accept(this);
			Exp exp2 = (Exp) ctx.expression(1).accept(this);

			String op = ctx.getChild(1).getText();

			if (childLen == 3) {
				switch (op) {
				case "&&":
					ret = new And(exp1, exp2);
					break;
				case "<":
					ret = new LessThan(exp1, exp2);
					break;
				case "+":
					ret = new Plus(exp1, exp2);
					break;
				case "-":
					ret = new Minus(exp1, exp2);
					break;
				default:
					ret = new Times(exp1, exp2);
				}
			} else {
				ret = new ArrayLookup(exp1, exp2);
			}
		} else if (expLen == 1) {
			Exp exp = (Exp) ctx.expression(0).accept(this);

			switch(start) {
			case "!":
				ret = new Not(exp);
				break;
			case "(":
				ret = (Exp) ctx.expression(0).accept(this);
				break;
			case "new":
				ret = new NewArray(exp);
				break;
			default:
				ret = new ArrayLength(exp);
			}
		} else {
			switch(start) {
			case "new":
				ret = new NewObject((Identifier) ctx.identifier().accept(this));
				break;
			case "this":
				ret = new This();
				break;
			case "true":
				ret = new True();
				break;
			case "false":
				ret = new False();
				break;
			default:
				if (start.matches("\\d+")) {
					ret = Integer.parseInt(ctx.getText());
				} else {
					ret = (Identifier) ctx.identifier().accept(this);
				}
			}
		}
		return ret;
	}

}

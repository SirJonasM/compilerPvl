package tables.parser;

import tables.scanner.Token;
import tables.scanner.TokenType;

public class ParseException extends Exception {
		
	private static String message(Token token, TokenType expected, TokenType ... expectedMore) {
		String msg = "Line " + token.lineNo + ": expected " + expected;		
		for(int i = 0; i < expectedMore.length - 1; i++)
			msg = msg + ", " + expectedMore[i];
		if(expectedMore.length >= 1)
			msg = msg + " or " + expectedMore[expectedMore.length - 1];
		msg = msg + " but got " + token;
		return msg;
	}

	public ParseException(Token token, TokenType expected, TokenType ... expectedMore) {
		super(message(token, expected, expectedMore));
	}
}

// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Hashtable;

import static jminusminus.TokenKind.*;

/**
 * A lexical analyzer for j--, that has no backtracking mechanism.
 * 
 * When you add a new token to the scanner, you must also add an entry in the
 * TokenKind enum in TokenInfo.java specifying the kind and image of the new
 * token.
 */

class Scanner {

    /** End of file character. */
    public final static char EOFCH = CharReader.EOFCH;

    /** Keywords in j--. */
    private Hashtable<String, TokenKind> reserved;

    /** Source characters. */
    private CharReader input;

    /** Next unscanned character. */
    private char ch;

    /** Whether a scanner error has been found. */
    private boolean isInError;

    /** Source file name. */
    private String fileName;

    /** Line number of current token. */
    private int line;

    /**
     * Construct a Scanner object.
     * 
     * @param fileName
     *            the name of the file containing the source.
     * @exception FileNotFoundException
     *                when the named file cannot be found.
     */

    public Scanner(String fileName) throws FileNotFoundException {
        this.input = new CharReader(fileName);
        this.fileName = fileName;
        isInError = false;

        // Keywords in j--
        reserved = new Hashtable<String, TokenKind>();
        reserved.put(ABSTRACT.image(), ABSTRACT);
        reserved.put(ASSERT.image(), ASSERT);
        reserved.put(BOOLEAN.image(), BOOLEAN);
        reserved.put(BREAK.image(), BREAK);
        reserved.put(BYTE.image(), BYTE);
        reserved.put(CASE.image(), CASE);
        reserved.put(CATCH.image(), CATCH);
        reserved.put(CHAR.image(), CHAR);
        reserved.put(CLASS.image(), CLASS);
        reserved.put(CONST.image(), CONST);
        reserved.put(CONTINUE.image(), CONTINUE);
        reserved.put(DEFAULT.image(), DEFAULT);
        reserved.put(DO.image(), DO);
        reserved.put(DOUBLE.image(), DOUBLE);
        reserved.put(ELSE.image(), ELSE);
        reserved.put(ENUM.image(), ENUM);
        reserved.put(EXTENDS.image(), EXTENDS);
        reserved.put(FALSE.image(), FALSE);
        reserved.put(FINAL.image(), FINAL);
        reserved.put(FINALLY.image(), FINALLY);
        reserved.put(FLOAT.image(), FLOAT);
        reserved.put(FOR.image(), FOR);
        reserved.put(GOTO.image(), GOTO);
        reserved.put(IF.image(), IF);
        reserved.put(IMPLEMENTS.image(), IMPLEMENTS);
        reserved.put(IMPORT.image(), IMPORT);
        reserved.put(INSTANCEOF.image(), INSTANCEOF);
        reserved.put(INT.image(), INT);
        reserved.put(INTERFACE.image(), INTERFACE);
        reserved.put(LONG.image(), LONG);
        reserved.put(NATIVE.image(), NATIVE);
        reserved.put(NEW.image(), NEW);
        reserved.put(NULL.image(), NULL);
        reserved.put(PACKAGE.image(), PACKAGE);
        reserved.put(PRIVATE.image(), PRIVATE);
        reserved.put(PROTECTED.image(), PROTECTED);
        reserved.put(PUBLIC.image(), PUBLIC);
        reserved.put(RETURN.image(), RETURN);
        reserved.put(SHORT.image(), SHORT);
        reserved.put(STATIC.image(), STATIC);
        reserved.put(STRICTFP.image(), STRICTFP);
        reserved.put(SUPER.image(), SUPER);
        reserved.put(SWITCH.image(), SWITCH);
        reserved.put(SYNCHRONIZED.image(), SYNCHRONIZED);
        reserved.put(THIS.image(), THIS);
        reserved.put(THROW.image(), THROW);
        reserved.put(THROWS.image(), THROWS);
        reserved.put(TRANSIENT.image(), TRANSIENT);
        reserved.put(TRUE.image(), TRUE);
        reserved.put(TRY.image(), TRY);
        reserved.put(VOID.image(), VOID);
        reserved.put(VOLATILE.image(), VOLATILE);
        reserved.put(WHILE.image(), WHILE); 

        // Prime the pump.
        nextCh();
    }

    /**
     * Scan the next token from input.
     * 
     * @return the the next scanned token.
     */

    public TokenInfo getNextToken() {
        StringBuffer buffer;
        boolean moreWhiteSpace = true;
        while (moreWhiteSpace) {
            while (isWhitespace(ch)) {
                nextCh();
            }
            if (ch == '/') {
                nextCh();
                if (ch == '/') {
                    while (ch != '\n' && ch != EOFCH) {
                        nextCh();
                    }
                } else if (ch == '*') {
                	nextCh();
                	while (ch != EOFCH) {
                		if (ch == '*') {
                			nextCh();
                			if (ch == '/') {
                				break; 
                			}
                			else {
                				continue; 
                			}
                		}
                		nextCh(); 
                	}
                	if(ch == EOFCH) {
                    		reportScannerError("Unexpected end of file found in multiline comment");
                	}
                	else {
                	nextCh(); 
                	}
                } else if (ch == '='){
                	nextCh();
                	return new TokenInfo(DIV_ASSIGN, line);
                }
                else {
                		return new TokenInfo(DIV, line); 
                }
            } else {
                moreWhiteSpace = false;
            }
        }
        line = input.line();
        switch (ch) {
        case '(':
            nextCh();
            return new TokenInfo(LPAREN, line);
        case ')':
            nextCh();
            return new TokenInfo(RPAREN, line);
        case '{':
            nextCh();
            return new TokenInfo(LCURLY, line);
        case '}':
            nextCh();
            return new TokenInfo(RCURLY, line);
        case '[':
            nextCh();
            return new TokenInfo(LBRACK, line);
        case ']':
            nextCh();
            return new TokenInfo(RBRACK, line);
        case ';':
            nextCh();
            return new TokenInfo(SEMI, line);
        case ',':
            nextCh();
            return new TokenInfo(COMMA, line);
        case '=':
            nextCh();
            if (ch == '=') {
                nextCh();
                return new TokenInfo(EQUAL, line);
            } else {
                return new TokenInfo(ASSIGN, line);
            }
        case '!':
            nextCh();
            if(ch == '='){
            	nextCh();
            	return new TokenInfo(NOT_EQUAL, line);
            } else {
            	return new TokenInfo(LNOT, line);
            }
            
        case '*':
            nextCh();
            if (ch == '=') {
                nextCh();
                return new TokenInfo(STAR_ASSIGN, line);
            } else {
            	return new TokenInfo(STAR, line);
            }
        case '%':
            nextCh();
            if (ch == '=') {
                nextCh();
                return new TokenInfo(MOD_ASSIGN, line);
            } else {
            	return new TokenInfo(MOD, line);
            }
        case '+':
            nextCh();
            if (ch == '=') {
                nextCh();
                return new TokenInfo(PLUS_ASSIGN, line);
            } else if (ch == '+') {
                nextCh();
                return new TokenInfo(INC, line);
            } else {
                return new TokenInfo(PLUS, line);
            }
        case '-':
            nextCh();
            if (ch == '=') {
                nextCh();
                return new TokenInfo(MINUS_ASSIGN, line);
            } else if (ch == '-') {
                nextCh();
                return new TokenInfo(DEC, line);
            } else {
                return new TokenInfo(MINUS, line);
            }
        case '~':
        	nextCh();
        	return new TokenInfo(BNOT, line);
        case '|':
        	nextCh();
            if (ch == '|') {
                nextCh();
                return new TokenInfo(LOR, line);
            } else if (ch == '=') {
            	nextCh();
                return new TokenInfo(BOR_ASSIGN, line);
            } else {
            	return new TokenInfo(BOR, line);
            }
        case '^':
            nextCh();
            if (ch == '=') {
                nextCh();
                return new TokenInfo(BXOR_ASSIGN, line);
            } else {
            	return new TokenInfo(BXOR, line);
            }
        case '&':
            nextCh();
            if (ch == '&') {
                nextCh();
                return new TokenInfo(LAND, line);
            } else if (ch == '=') {
            	nextCh();
                return new TokenInfo(BAND_ASSIGN, line);
            } else {
            	return new TokenInfo(BAND, line);
            }
        case '>':
            nextCh();
            if (ch == '='){
            	nextCh();
            	return new TokenInfo(GE, line);
            } else if (ch == '>'){
            	nextCh();
            	if(ch == '>'){
            		nextCh();
            		if(ch == '=' ){
            			nextCh();
            			return new TokenInfo(BSR_ASSIGN, line);
            		}
            		else {
            			return new TokenInfo(BSR, line);
            		}
            	} else if(ch == '='){
            		nextCh();
            		return new TokenInfo(SR_ASSIGN, line);
            	}
            	else {
            		return new TokenInfo(SR, line);
            	}
            } else {
            	return new TokenInfo(GT, line);
            }
        case '<':
            nextCh();
            if (ch == '=') {
                nextCh();
                return new TokenInfo(LE, line);
            } else if (ch == '<'){
            	nextCh();
            	if (ch == '=') {
            		nextCh();
            		return new TokenInfo(SL_ASSIGN, line);
            	} else {
            		return new TokenInfo(SL, line);
            	}
            } else {
            	return new TokenInfo(LT, line);
            }
        case '?':
        	nextCh();
        	return new TokenInfo(QUESTION, line);
        case ':':
        	nextCh();
        	return new TokenInfo(COLON, line);
        case '@':
        	nextCh();
        	return new TokenInfo(ANNOTATION, line); 
        case '\'':
        	buffer = new StringBuffer();
        	bufferIncrement(buffer);
        	if(ch == '\\'){
        		bufferIncrement(buffer);
        		switch(ch){
        		case '\'':
        			bufferIncrement(buffer);
        			if(ch == '\''){
        				return new TokenInfo(CHAR_LITERAL, bufferIncrement(buffer).toString(), line);
        			}
        			else{
        				reportScannerError("Wrong format literal  -> "+ buffer.toString());
        				return getNextToken();
        			}
        		case 'b' :
        		case 't' :
        		case 'n' :
        		case 'r' :
        		case 'f' :
        		case '"' :
        		case '\\':
        			bufferIncrement(buffer);
        			break;
        		default  :
        			int flag = 0;
                	if( isOctalDigit(ch) ){
                		if(ch >= '0' && ch <= '3'){
                			flag = 1;
                		}
                		bufferIncrement(buffer);
                		if( isOctalDigit(ch) ){
                			bufferIncrement(buffer);
                			if(flag == 1){
                				if( isOctalDigit(ch) ){
                					bufferIncrement(buffer);
                				}
                			}	
                		}
                	}
                	else if(ch == 'u'){
                		while(ch == 'u'){
                			bufferIncrement(buffer);
                		}
                		if( isHexDigit(ch) ){
                			int hexDigitCounter = 0;
                			while(isHexDigit(ch)){
                				hexDigitCounter++;
                				if(hexDigitCounter > 4){
                					break;
                				}
                				bufferIncrement(buffer);
                			}
                			if(hexDigitCounter < 4){
                				reportScannerError("Bad Unicode Sequence -> "+ bufferIncrement(buffer).toString());
                				charLiteralRecovery();
                        		return getNextToken();
                			}
                			if(buffer.toString().matches("'\\\\uu*005[cC]")){
            					if(ch == '\\'){
            						bufferIncrement(buffer);
            						if(ch == 'u'){
            							while(ch == 'u'){
            	                			bufferIncrement(buffer);
            	                		}
            							if(isHexDigit(ch)){
            								int hexDigitCounter2 = 0;
            								while(isHexDigit(ch)){
            	                				hexDigitCounter2++;
            	                				if(hexDigitCounter2 > 4){
            	                					break;
            	                				}
            	                				bufferIncrement(buffer);
            	                			}
            	                			if(hexDigitCounter2 != 4){
            	                				reportScannerError("Bad Unicode Sequence -> "+ bufferIncrement(buffer).toString());
            	                				charLiteralRecovery();
            	                        		return getNextToken();
            	                			}
            	                			if(!(buffer.toString().matches("'\\\\uu*005[cC]\\\\uu*0027") ||
            	                			buffer.toString().matches("'\\\\uu*005[cC]\\\\uu*0062") ||
            	                			buffer.toString().matches("'\\\\uu*005[cC]\\\\uu*0074") ||
            	                			buffer.toString().matches("'\\\\uu*005[cC]\\\\uu*006[eE]") ||
            	                			buffer.toString().matches("'\\\\uu*005[cC]\\\\uu*0072") ||
            	                			buffer.toString().matches("'\\\\uu*005[cC]\\\\uu*0022") ||
            	                			buffer.toString().matches("'\\\\uu*005[cC]\\\\uu*005[cC]") ||
            	                			buffer.toString().matches("'\\\\uu*005[cC]\\\\uu*000[cC]"))
            	                			){
            	                				reportScannerError("Bad Unicode Sequence -> "+ buffer.toString());
            	                				charLiteralRecovery();
            	                        		return getNextToken();
            	                			}
            							}
            							else{
            								reportScannerError("Bad Unicode Sequence -> "+ bufferIncrement(buffer).toString());
                            				charLiteralRecovery();
                                    		return getNextToken();
            							}
            						}
            					}
            					else if(ch == '\'' || ch == 'b' || ch == 'n' || ch == 'r' 
            							|| ch == '"' || ch == 't' || ch == 'f'){
            						if(ch == '\''){
            							bufferIncrement(buffer);
            		        			if(ch == '\''){
            		        				return new TokenInfo(CHAR_LITERAL, bufferIncrement(buffer).toString(), line);
            		        			}
            		        			else{
            		        				reportScannerError("Wrong format literal  -> "+ buffer.toString());
            		        				return getNextToken();
            		        			}
            						}
            						bufferIncrement(buffer);
            					}
            					else if( isOctalDigit(ch) ){
            						int flag1 = 0;
            						if(ch >= '0' && ch <= '3'){
                            			flag1 = 1;
                            		}
                            		bufferIncrement(buffer);
                            		if( isOctalDigit(ch) ){
                            			bufferIncrement(buffer);
                            			if(flag1 == 1){
                            				if( isOctalDigit(ch) ){
                            					bufferIncrement(buffer);
                            				}
                            			}	
                            		}
            					}
            					else{
            						reportScannerError("Bad Unicode Sequence -> "+ bufferIncrement(buffer).toString());
                    				charLiteralRecovery();
                            		return getNextToken();
            					}
            				}
                		}
                		else{
                			reportScannerError("Bad Unicode Sequence -> "+ bufferIncrement(buffer).toString());
                			charLiteralRecovery();
                    		return getNextToken();
                		}
                	}
                	else{
                		reportScannerError("Bad Escape Sequence -> "+ bufferIncrement(buffer).toString());
                		charLiteralRecovery();
                		return getNextToken();
                	}
        		}
        		if(ch == '\''){
        			return new TokenInfo(CHAR_LITERAL, bufferIncrement(buffer).toString(), line);
        		}
        		else{
        			buffer.append(ch);
        			reportScannerError(ch
                            + " found by scanner where closing ' was expected -> "+ buffer.toString());
        			charLiteralRecovery();
        			return getNextToken();
        		}
        	}
        	else if(ch == '\''){
        		bufferIncrement(buffer);
        		if(ch == '\''){
        			reportScannerError("Wrong format literal  -> "+ bufferIncrement(buffer).toString());
        		}
        		else{
        			reportScannerError("Wrong format literal  -> "+ buffer.toString());
        		}
    			return getNextToken();
        	}
        	else {
        		bufferIncrement(buffer);
        		if(ch == '\''){
        			bufferIncrement(buffer);
        			return new TokenInfo(CHAR_LITERAL, buffer.toString(), line);
        		}
        		else{
        			buffer.append(ch);
                    reportScannerError(ch
                            + " found by scanner where closing ' was expected. -> "+ buffer.toString());
                    nextCh();
                    charLiteralRecovery();
                    return getNextToken();
        		}
        	}
        case '"':
            buffer = new StringBuffer();
            bufferIncrement(buffer);
            while (ch != '"' && ch != '\n' && ch != EOFCH) {
                if (ch == '\\') {
                	bufferIncrement(buffer);
                	switch(ch){
                	case 'b' :
            		case 't' :
            		case 'n' :
            		case 'r' :
            		case 'f' :
            		case '"' :
            		case '\\':
            		case '\'':
            			bufferIncrement(buffer);
            			break;
            		default:
                    	if( isDigit(ch) ){
                    		if(ch == '9' || ch == '8'){
                    			reportScannerError("Wrong format literal -> "+ bufferIncrement(buffer).toString());
                    			stringLiteralRecovery();
                        		return getNextToken();
                    		}
                    	}
                    	else if(ch == 'u'){
                    		while(ch == 'u'){
                    			bufferIncrement(buffer);
                    		}
                    		if( isHexDigit(ch) ){
                    			int hexDigitCounter = 0;
                    			while(isHexDigit(ch)){
                    				hexDigitCounter++;
                    				if(hexDigitCounter > 4){
                    					break;
                    				}
                    				bufferIncrement(buffer);
                    			}
                    			if(hexDigitCounter < 4){
                    				reportScannerError("Bad Unicode Sequence -> "+ bufferIncrement(buffer).toString());
                    				stringLiteralRecovery();
                            		return getNextToken();
                    			}
                    			if(buffer.toString().matches(".*\\\\uu*005[cC]")){
                    				if(ch == '\\'){
                						bufferIncrement(buffer);
                						if(ch == 'u'){
                							while(ch == 'u'){
                	                			bufferIncrement(buffer);
                	                		}
                							if(isHexDigit(ch)){
                								int hexDigitCounter2 = 0;
                								while(isHexDigit(ch)){
                	                				hexDigitCounter2++;
                	                				if(hexDigitCounter2 > 4){
                	                					break;
                	                				}
                	                				bufferIncrement(buffer);
                	                			}
                	                			if(hexDigitCounter2 < 4){
                	                				reportScannerError("Bad Unicode Sequence -> "+ bufferIncrement(buffer).toString());
                	                				stringLiteralRecovery();
                	                        		return getNextToken();
                	                			}
                	                			if(!(buffer.toString().matches(".*\\\\uu*005[cC]\\\\uu*0027") ||
                	                			buffer.toString().matches(".*\\\\uu*005[cC]\\\\uu*0062") || 
                	                			buffer.toString().matches(".*\\\\uu*005[cC]\\\\uu*0074") || 
                	                			buffer.toString().matches(".*\\\\uu*005[cC]\\\\uu*006[eE]") || 
                	                			buffer.toString().matches(".*\\\\uu*005[cC]\\\\uu*0072") || 
                	                			buffer.toString().matches(".*\\\\uu*005[cC]\\\\uu*0022") || 
                	                			buffer.toString().matches(".*\\\\uu*005[cC]\\\\uu*005[cC]") ||
                	                			buffer.toString().matches(".*\\\\uu*005[cC]\\\\uu*000[cC]"))
                	                			){
                	                				reportScannerError("Bad Unicode Sequence -> "+ buffer.toString());
                	                				stringLiteralRecovery();
                	                        		return getNextToken();
                	                			}
                							}
                							else{
                								reportScannerError("Bad Unicode Sequence -> "+ bufferIncrement(buffer).toString());
                                				stringLiteralRecovery();
                                        		return getNextToken();
                							}
                							
                						}
                					}
                    				else if(ch == '\'' || ch == 'b' || ch == 'n' || ch == 'r' 
                							|| ch == '"' || ch == 't' || ch == 'f'){
                						bufferIncrement(buffer);
                					}
                    				else if( isDigit(ch) ){
                    					if(ch == '9' || ch == '8'){
                                			reportScannerError("Wrong format literal -> "+ bufferIncrement(buffer).toString());
                                			stringLiteralRecovery();
                                    		return getNextToken();
                                		}
                    				}
                    				else{
                    					reportScannerError("Bad Escape Sequence -> "+ bufferIncrement(buffer).toString());
                                		stringLiteralRecovery();
                                		return getNextToken();
                    				}
                    			}
                    		}
                    		else{
                    			reportScannerError("Bad Unicode Sequence -> "+ bufferIncrement(buffer).toString());
                    			stringLiteralRecovery();
                        		return getNextToken();
                    		}
                    	}
                    	else{
                    		reportScannerError("Bad Escape Sequence -> "+ bufferIncrement(buffer).toString());
                    		stringLiteralRecovery();
                    		return getNextToken();
                    	}
                	}
                } else {
                    bufferIncrement(buffer);
                }
            }
            if (ch == '\n') {
                reportScannerError("Unexpected end of line found in String -> ");
            } else if (ch == EOFCH) {
                reportScannerError("Unexpected end of file found in String");
            } else {
                // Scan the closing "
            	bufferIncrement(buffer);
            }
            return new TokenInfo(STRING_LITERAL, buffer.toString(), line);
        case '.':
        	buffer = new StringBuffer();
            bufferIncrement(buffer);
            if(isDigit(ch)){
            	while(isDigit(ch)){
        			bufferIncrement(buffer);
        		}
        		if( isExponent(ch) ){
        			return ( exponentCall(buffer) );
        		}
        		else if(isFloat(ch)){
        			return new TokenInfo(FLOAT_LITERAL, bufferIncrement(buffer).toString(), line);
        		}
        		else if(isDouble(ch)){
        			return new TokenInfo(DOUBLE_LITERAL, bufferIncrement(buffer).toString(), line);
        		}
        		else
        		{
        			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
        		}
                
            }else {
            	return new TokenInfo(DOT, line);
            }
            
        case EOFCH:
            return new TokenInfo(EOF, line);
        case '0':
        	buffer = new StringBuffer();
            bufferIncrement(buffer);
            if(isHexa(ch)){ 
            	bufferIncrement(buffer);
            	if(isHexDigit(ch)){ 
            		while( isHexDigit(ch) ){
            			bufferIncrement(buffer);
                	} 
            		if( isLong(ch) ){ 
                		return new TokenInfo(LONG_LITERAL, bufferIncrement(buffer).toString(), line);
                	} else if(ch == '.'){ 
                		bufferIncrement(buffer);
                			while( isHexDigit(ch) ){
                				bufferIncrement(buffer);
                			}
                			if( isBinaryExponent(ch) ){
                				return ( binaryExponentCall(buffer) );
                			}
                			else {
                    			reportScannerError("Wrong format literal -> " + bufferIncrement(buffer).toString());
                    			return getNextToken();
                			}
                	}
                	else if( isBinaryExponent(ch) ){
                		return ( binaryExponentCall(buffer) );
                	}
                	else {
                		return new TokenInfo(INT_LITERAL, buffer.toString(), line);
                	}
            	}
            	else if(ch == '.'){
            	bufferIncrement(buffer);
            		if(isHexDigit(ch)){
            			while( isHexDigit(ch) ){
            				buffer = bufferIncrement(buffer);
            			}
            			if( isBinaryExponent(ch) ){
            				return ( binaryExponentCall(buffer) );
            			}
            			else{
                			reportScannerError("Wrong format literal -> " + bufferIncrement(buffer).toString());
                			return getNextToken();
            			}
            		}
            		else{
            			reportScannerError("Wrong format literal -> " + bufferIncrement(buffer).toString());
            			return getNextToken();
            		}
            	}
            	else{
            		reportScannerError("Wrong format literal -> " + bufferIncrement(buffer).toString());
        			return getNextToken();
            	}
            	
            } else if(isDigit(ch)){
            	int flag = 0;
            	if(ch == '9' || ch == '8'){
            		flag = 1;
            	}
            	bufferIncrement(buffer);
            	while( isDigit(ch) ){
            		if(ch == '9' || ch == '8'){
            			flag = 1;
            		}
            		bufferIncrement(buffer);
            	}
            	if( isLong(ch) ){
            		bufferIncrement(buffer);
            		if(flag == 0){
            			return new TokenInfo(LONG_LITERAL, buffer.toString(), line);
            		}
            		else {
            			reportScannerError("Wrong format literal -> " + buffer.toString()); 
            			return getNextToken();
            		}
            	}
            	else if( isExponent(ch) ){
            		return ( exponentCall(buffer) );
            	}
            	else if(ch == '.'){
            		bufferIncrement(buffer);
            		while(isDigit(ch)){
            			bufferIncrement(buffer);
            		}
            		if( isExponent(ch) ){
            			return ( exponentCall(buffer) );
            		}
            		else if(isFloat(ch)){
            			return new TokenInfo(FLOAT_LITERAL, bufferIncrement(buffer).toString(), line);
            		}
            		else if(isDouble(ch)){
            			return new TokenInfo(DOUBLE_LITERAL, bufferIncrement(buffer).toString(), line);
            		}
            		else
            		{
            			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
            		}
            	}
            	else{	
            		if( isFloat(ch) ){
            			return new TokenInfo(FLOAT_LITERAL, bufferIncrement(buffer).toString(), line);
            		}
            		if( isDouble(ch) ){
            			return new TokenInfo(DOUBLE_LITERAL, bufferIncrement(buffer).toString(), line);
            		}
            		
            		if(flag == 0){
            			return new TokenInfo(INT_LITERAL, buffer.toString(), line);
            		}
            		else {
            			reportScannerError("Wrong format literal -> " + buffer.toString()); 
            			return getNextToken();
            		}
            	}
            } 
            else if(isBinary(ch)){ 
            	bufferIncrement(buffer);
            	if(isBinaryDigit(ch)){
            	while( isBinaryDigit(ch) ){
            		bufferIncrement(buffer);
            	}
            	if( isLong(ch) ){ 
            		return new TokenInfo(LONG_LITERAL, bufferIncrement(buffer).toString(), line);
            	}
            	return new TokenInfo(INT_LITERAL, buffer.toString(), line);
            	}
            	else{
            		reportScannerError("Wrong format literal -> "+ bufferIncrement(buffer).toString());
            		return getNextToken();
            	}
            } 
            else if(ch == '.'){
            	bufferIncrement(buffer);
        		while(isDigit(ch)){
        			bufferIncrement(buffer);
        		}
        		if( isExponent(ch) ){
        			return ( exponentCall(buffer) );
        		}
        		else if(isFloat(ch)){
        			return new TokenInfo(FLOAT_LITERAL, bufferIncrement(buffer).toString(), line);
        		}
        		else if(isDouble(ch)){
        			return new TokenInfo(DOUBLE_LITERAL, bufferIncrement(buffer).toString(), line);
        		}
        		else
        		{
        			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
        		}
        	}
            else if( isExponent(ch) ){
            	return ( exponentCall(buffer) );
            }
            else{
            	if( isLong(ch) ){
            		return new TokenInfo(LONG_LITERAL, bufferIncrement(buffer).toString(), line);
            	}
            	if (isDouble(ch)){
            		return new TokenInfo(DOUBLE_LITERAL, bufferIncrement(buffer).toString(), line);
            	}
            	if(isFloat(ch)){
            		return new TokenInfo(FLOAT_LITERAL, bufferIncrement(buffer).toString(), line);
            	}		
            	return new TokenInfo(INT_LITERAL, buffer.toString(), line); 
            }
            
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            buffer = new StringBuffer();
            if(isDigit(ch)){
            	bufferIncrement(buffer);
            	while(isDigit(ch)){
            		bufferIncrement(buffer);
            	}
            	if(ch == '.'){
            		bufferIncrement(buffer);
            		while(isDigit(ch)){
            			bufferIncrement(buffer);
            		}
            		if( isExponent(ch) ){
            			return ( exponentCall(buffer) );
            		}
            		else if(isFloat(ch)){
            			return new TokenInfo(FLOAT_LITERAL, bufferIncrement(buffer).toString(), line);
            		}
            		else if(isDouble(ch)){
            			return new TokenInfo(DOUBLE_LITERAL, bufferIncrement(buffer).toString(), line);
            		}
            		else
            		{
            			return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
            		}
            	}
            	else if(isExponent(ch)){
            		return ( exponentCall(buffer) );
            	}
            	else{
                	if( isLong(ch) ){
                		return new TokenInfo(LONG_LITERAL, bufferIncrement(buffer).toString(), line);
                	}
                	if (isDouble(ch)){
                		return new TokenInfo(DOUBLE_LITERAL, bufferIncrement(buffer).toString(), line);
                	}
                	if(isFloat(ch)){
                		return new TokenInfo(FLOAT_LITERAL, bufferIncrement(buffer).toString(), line);
                	}		
                	return new TokenInfo(INT_LITERAL, buffer.toString(), line); 
                }
            }
           
        default:
            if (isIdentifierStart(ch)) {
                buffer = new StringBuffer();
                while (isIdentifierPart(ch)) {
                    buffer.append(ch);
                    nextCh();
                }
                String identifier = buffer.toString();
                if (reserved.containsKey(identifier)) {
                	return new TokenInfo(reserved.get(identifier), line);
                } else {
                    return new TokenInfo(IDENTIFIER, identifier, line);
                }
            } else {
                reportScannerError("Unidentified input token: '%c'", ch);
                nextCh();
                return getNextToken();
            }
        }
    }

    /**
     * Advance ch to the next character from input, and update the line number.
     */

    private void nextCh() {
        line = input.line();
        try {
            ch = input.nextChar();
        } catch (Exception e) {
            reportScannerError("Unable to read characters from input");
        }
    }

    /**
     * Report a lexcial error and record the fact that an error has occured.
     * This fact can be ascertained from the Scanner by sending it an
     * errorHasOccurred() message.
     * 
     * @param message
     *            message identifying the error.
     * @param args
     *            related values.
     */

    private void reportScannerError(String message, Object... args) {
        isInError = true;
        System.err.printf("%s:%d: ", fileName, line);
        System.err.printf(message.replace("%", "%%"), args);
        System.err.println();
    }

    /**
     * Return true if the specified character is a digit (0-9); false otherwise.
     * 
     * @param c
     *            character.
     * @return true or false.
     */

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }
    
    /**
     * Return true if the specified character is a HexDigit (0-9)|(A-F)|(a-f); false otherwise.
     * 
     * @param c
     *            character.
     * @return true or false.
     */
    private boolean isHexDigit(char c) {
        return (c >= '0' && c <= '9' || c >= 'A' && c<= 'F' || c>='a' && c<='f');
    }
    
    /**
     * Return true if the specified character is a OctalDigit (0-7); false otherwise.
     * 
     * @param c
     *            character.
     * @return true or false.
     */
    private boolean isOctalDigit(char c) {
        return (c >= '0' && c <= '7');
    }
    
    /**
     * Return true if the specified character is a BinaryDigit (0-1); false otherwise.
     * 
     * @param c
     *            character.
     * @return true or false.
     */
    private boolean isBinaryDigit(char c) {
        return (c == '0' || c == '1');
    }
    
    /**
     * Return true if the specified character is + or -; false otherwise.
     * 
     * @param c
     *            character.
     * @return true or false.
     */
    private boolean isPlusOrMinus(char c) {
        return (c == '+' || c == '-');
    }
     
    /**
     * Return true if the specified character is a x or X; false otherwise.
     * 
     * @param c
     *            character.
     * @return true or false.
     */ 
   private boolean isHexa(char c){
	   return(c == 'x' || c == 'X');
   }
   
   /**
    * Return true if the specified character is d or D; false otherwise.
    * 
    * @param c
    *            character.
    * @return true or false.
    */
   private boolean isDouble(char c){
	   return(c == 'd' || c == 'D');
   }
   
   /**
    * Return true if the specified character is l or L; false otherwise.
    * 
    * @param c
    *            character.
    * @return true or false.
    */
   private boolean isLong(char c){
	   return(c == 'l' || c == 'L');
   }
   
   /**
    * Return true if the specified character is f or F; false otherwise.
    * 
    * @param c
    *            character.
    * @return true or false.
    */
   private boolean isFloat(char c){
	   return(c == 'f' || c == 'F');
   }
   
   /**
    * Return true if the specified character is b or B; false otherwise.
    * 
    * @param c
    *            character.
    * @return true or false.
    */
   private boolean isBinary(char c){
	   return(c == 'b' || c == 'B');
   }
   
   
   /**
    * Return true if the specified character is e or E; false otherwise.
    * 
    * @param c
    *            character.
    * @return true or false.
    */
   private boolean isExponent(char c){
	   return(c == 'e' || c == 'E');
   }
   
   /**
    * Return true if the specified character is p or P; false otherwise.
    * 
    * @param c
    *            character.
    * @return true or false.
    */
   private boolean isBinaryExponent(char c){
	   return(c == 'p' || c == 'P');
   }
   
   /**
    * appends current character to buffer and gets next character.
    * @param buffer
    * @return StringBuffer
    */
   private StringBuffer bufferIncrement(StringBuffer buffer){
	  buffer.append(ch);
	  nextCh();
	  return buffer;
   }
   
   /**
    * Deals with binaryExponent
    * @param buffer
    * @return TokenInfo
    */
   private TokenInfo binaryExponentCall(StringBuffer buffer){
	   bufferIncrement(buffer);
		if( isPlusOrMinus(ch) ){
			bufferIncrement(buffer);
			if(isDigit(ch)){
			while( isDigit(ch) ){
				bufferIncrement(buffer);
			}
			if(isFloat(ch)){
				return new TokenInfo(FLOAT_LITERAL, bufferIncrement(buffer).toString(), line);
			}
			else if(isDouble(ch)){
				return new TokenInfo(DOUBLE_LITERAL, bufferIncrement(buffer).toString(), line);
			}
			else{
				return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
			}
				
		}	
		else {
			reportScannerError("Wrong format literal -> " + bufferIncrement(buffer).toString());
			return getNextToken();
		}
		}
		else if(isDigit(ch)){
			while( isDigit(ch) ){
				bufferIncrement(buffer);
			}
			if(isFloat(ch)){
				return new TokenInfo(FLOAT_LITERAL, bufferIncrement(buffer).toString(), line);
			}
			else if(isDouble(ch)){
				return new TokenInfo(DOUBLE_LITERAL, bufferIncrement(buffer).toString(), line);
			}
			else{
				return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
			}
		}
		else {
			reportScannerError("Wrong format literal -> " + bufferIncrement(buffer).toString());
			return getNextToken();
		}
   }
   
   /**
    * Deals with exponent
    * @param buffer
    * @return TokenInfo
    */
   private TokenInfo exponentCall(StringBuffer buffer){
	   bufferIncrement(buffer);
		if( isPlusOrMinus(ch) ){
			bufferIncrement(buffer);
			if(isDigit(ch)){
			while( isDigit(ch) ){
				bufferIncrement(buffer);
			}
			if(isFloat(ch)){
				return new TokenInfo(FLOAT_LITERAL, bufferIncrement(buffer).toString(), line);
			}
			else if(isDouble(ch)){
				return new TokenInfo(DOUBLE_LITERAL, bufferIncrement(buffer).toString(), line);
			}
			else{
				return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
			}
				
		} else {
			reportScannerError("Wrong format literal -> " + bufferIncrement(buffer).toString());
			return getNextToken();
		}
			} else if(isDigit(ch)){
			while( isDigit(ch) ){
				bufferIncrement(buffer);
			}
			if(isFloat(ch)){
				return new TokenInfo(FLOAT_LITERAL, bufferIncrement(buffer).toString(), line);
			}
			else if(isDouble(ch)){
				return new TokenInfo(DOUBLE_LITERAL, bufferIncrement(buffer).toString(), line);
			}
			else{
				return new TokenInfo(DOUBLE_LITERAL, buffer.toString(), line);
			}
				
		}
		else {
			reportScannerError("Wrong format literal -> " + bufferIncrement(buffer).toString());
			return getNextToken();
		}
   }
   
   /**
    * Invoked once an error has occurred for CHAR_LITERAL
    */
   private void charLiteralRecovery(){
	   while(ch != ';' && ch != '\n' && ch != EOFCH){
			if(ch == '\''){
				nextCh();
				break;
			}
			nextCh();
		}
   }
   
   /**
    * Invoked once an error has occurred for STRING_LITERAL
    */
   private void stringLiteralRecovery(){
	   while(ch != '\n' && ch != EOFCH){
			if(ch == '"'){
				nextCh();
				break;
			}
			nextCh();
		}
   }

    /**
     * Return true if the specified character is a whitespace; false otherwise.
     * 
     * @param c
     *            character.
     * @return true or false.
     */

    private boolean isWhitespace(char c) {
        switch (c) {
        case ' ':
        case '\t':
        case '\n': // CharReader maps all new lines to '\n'
        case '\f':
            return true;
        }
        return false;
    }

    /**
     * Return true if the specified character can start an identifier name;
     * false otherwise.
     * 
     * @param c
     *            character.
     * @return true or false.
     */

    private boolean isIdentifierStart(char c) {
        return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c == '$');
    }

    /**
     * Return true if the specified character can be part of an identifier name;
     * false otherwise.
     * 
     * @param c
     *            character.
     * @return true or false.
     */

    private boolean isIdentifierPart(char c) {
        return (isIdentifierStart(c) || isDigit(c));
    }

    /**
     * Has an error occurred up to now in lexical analysis?
     * 
     * @return true or false.
     */

    public boolean errorHasOccurred() {
        return isInError;
    }

    /**
     * The name of the source file.
     * 
     * @return name of the source file.
     */

    public String fileName() {
        return fileName;
    }

}

/**
 * A buffered character reader. Abstracts out differences between platforms,
 * mapping all new lines to '\n'. Also, keeps track of line numbers where the
 * first line is numbered 1.
 */

class CharReader {

    /** A representation of the end of file as a character. */
    public final static char EOFCH = (char) -1;

    /** The underlying reader records line numbers. */
    private LineNumberReader lineNumberReader;

    /** Name of the file that is being read. */
    private String fileName;

    /**
     * Construct a CharReader from a file name.
     * 
     * @param fileName
     *            the name of the input file.
     * @exception FileNotFoundException
     *                if the file is not found.
     */

    public CharReader(String fileName) throws FileNotFoundException {
        lineNumberReader = new LineNumberReader(new FileReader(fileName));
        this.fileName = fileName;
    }

    /**
     * Scan the next character.
     * 
     * @return the character scanned.
     * @exception IOException
     *                if an I/O error occurs.
     */

    public char nextChar() throws IOException {
        return (char) lineNumberReader.read();
    }

    /**
     * The current line number in the source file, starting at 1.
     * 
     * @return the current line number.
     */

    public int line() {
        // LineNumberReader counts lines from 0.
        return lineNumberReader.getLineNumber() + 1;
    }

    /**
     * Return the file name.
     * 
     * @return the file name.
     */

    public String fileName() {
        return fileName;
    }

    /**
     * Close the file.
     * 
     * @exception IOException
     *                if an I/O error occurs.
     */

    public void close() throws IOException {
        lineNumberReader.close();
    }

}

/*
 * Copyright 2014 Laurens Holst
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.grauw.glass;

import br.com.objectware.assembler.Assembler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Glass compiler. Original code by Grauw (www.grauw.nl)
 * Updated version by Objectware Br (www.objectware.net)
 * 
 * @author Laurens Holst
 */
public class GlassAssemblerZ80 implements Assembler {
	
	private Source source;
	
    private File sourcePath = null;
    private File targetPath = null;
    private File symbolPath = null;
    
    private final List<File> includePaths = new ArrayList<>();
    
    public GlassAssemblerZ80() {
    }
    
    @Override
    public int parseArguments(List<String> arguments) {
        
		if(arguments.isEmpty()) {
            throw new InvalidParameterException("Missing parameters");
        }   
        
		for (int i = 0; i < arguments.size(); i++) {
            
            String argument = arguments.get(i);
            
			if (argument.equals("-I") && (i + 1) < arguments.size()) {
				this.includePaths.add(new File(arguments.get(++i)));
			} else if (this.sourcePath == null) {
				this.sourcePath = new File(argument);
			} else if (this.targetPath == null) {
				this.targetPath = new File(argument);
			} else if (this.symbolPath == null) {
				this.symbolPath = new File(argument);
			} else {
				throw new InvalidParameterException("Too many arguments.");
			}   
		}   
        
        return 0;
    }
    
    @Override
    public int assemble() {
        
        this.source = new SourceParser(this.includePaths).parse(this.sourcePath);
		this.writeObject(this.targetPath);
        
        // write symbols file
		if(this.symbolPath != null) {
			this.writeSymbols(this.symbolPath);
        }
        
        return 0;
    }
	
	public void writeObject(File objectPath) {
		try (OutputStream output = objectPath != null ? new FileOutputStream(objectPath) : new NullOutputStream()) {
            this.source.assemble(output);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void writeSymbols(File symbolPath) {
		try (PrintStream symbolOutput = new PrintStream(symbolPath)) {
            Scope scope = this.source.getScope();
			symbolOutput.print(scope.serializeSymbols());
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
    
    @Override
    public String getVersion() {
        return "Glass compiler version 0.4 - http://www.grauw.nl/";
    }
    
	public static class NullOutputStream extends OutputStream {
        @Override public void write(int b) throws IOException {}
		@Override public void write(byte[] b) throws IOException {}
		@Override public void write(byte[] b, int off, int len) throws IOException {}
	}
	
}

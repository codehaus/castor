/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 1999 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id$
 */


package org.exolab.javasource;

import java.util.Enumeration;
import java.util.Vector;

import java.io.*;

/**
 * A class that "WILL EVENTUALLY" represent a Java Doc Comment.
 * @author <a href="mailto:kvisco@exoffice.com">Keith Visco</a>
 * @version $Revision$ $Date$
**/
public class JDocComment {

        
       
    /**
     * An ordered list of descriptors
    **/
    Vector _descriptors = null;


    /**
     * The main comment for this JDocComment
    **/
    StringBuffer _comment = null;
    
    /**
     * Creates a new JavaDoc Comment
    **/
    public JDocComment() {
        super();
        _descriptors = new Vector();
        _comment     = new StringBuffer();
    } //--  JDocComment

    /**
     * Adds the given exception to this JDocComment
     * @param name the name of the exception
     * @param desc the description of the exception
    **/
    public void addDescriptor(JDocDescriptor jdesc) {
        
        if (jdesc == null) return;
        //-- on the fly sorting of descriptors
        if (_descriptors.size() == 0) {
            _descriptors.addElement(jdesc);
            return;
        }
            
        for (int i = 0; i < _descriptors.size(); i++) {
            JDocDescriptor jdd 
                = (JDocDescriptor)_descriptors.elementAt(i);
            
            short compare = jdesc.compareTo(jdd);
            
            switch (compare) {
                case  0: // equal
                    _descriptors.insertElementAt(jdesc, i+1);
                    return;
                case -1: //-- less than
                    _descriptors.insertElementAt(jdesc, i);
                    return;
                case  1:
                    //-- keep looking
                    break;
            }
        }
        
        //-- if we make it here we need to add
        _descriptors.addElement(jdesc);
        
    } //-- addException
    
    /**
     * Appends the comment String to this JDocComment
     * @param comment the comment to append
    **/
    public void appendComment(String comment) {
        _comment.append(comment);
    } //-- appendComment
    
    /**
     * Returns an enumeration of the parameters of this JDocComment
     * @return an enumeration of the parameters of this JDocComment
    **/
    public Enumeration getDescriptors() {
        return _descriptors.elements();
    } //-- getDescriptors
    
    /**
     * prints this JavaDoc comment using the given JSourceWriter
    **/
    public void print(JSourceWriter jsw) {
        
        jsw.writeln("/**");
        //-- print main comment
        LineFormatter formatter = new LineFormatter(_comment.toString());
        while(formatter.hasMoreLines())
            jsw.writeln(formatter.nextLine());
        
        for (int i = 0; i < _descriptors.size(); i++) {
            jsw.write(" * ");
            jsw.writeln(_descriptors.elementAt(i));
        }
        jsw.writeln("**/");
        jsw.flush();
    } //-- print
    
    /**
     * Sets the comment String of this JDocComment
     * @param comment the comment String of this JDocComment
    **/
    public void setComment(String comment) {
        _comment.setLength(0);
        _comment.append(comment);
    } //-- setComment
    
    /**
     * Returns the String representation of this Java Doc Comment
     * @return the String representation of this Java Doc Comment
    **/
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("/**\n");
        sb.append(" * ");
        
        sb.append("**/\n");

        return sb.toString();
    } //-- toString

    
    /**
     * Formats a given String for use within a Java Doc comment
    **/
    class LineFormatter {
        
        static final int MAX_LENGTH = 60;
        
        String comment = null;
        
        int offset = 0;
        int length = 0;
        
        LineFormatter(String comment) {
            this.comment = comment;
            if (comment != null)
                this.length = comment.length();
        }
        
        boolean hasMoreLines() {
            if (comment == null) return false;
            return (offset < length);
        } //-- isFinished
        
        String nextLine() {
            if (comment == null) return null;
            
            StringBuffer sb = new StringBuffer();
            sb.append(" * ");
            
            int len = comment.length();
            
            int diff = length - offset;
            if (diff <= MAX_LENGTH) {
                sb.append(comment.substring(offset));
                offset = length;
            }
            else {
                int cc = offset+MAX_LENGTH;
                
                boolean done = false;
                while (cc > offset) {
                    if (isBreakChar(comment.charAt(cc))) break;
                    else --cc;
                }
                if (offset == cc) {
                    // no place to break
                    //-- we must look ahead
                    cc = offset+MAX_LENGTH+1;
                    while(cc < length) {
                        if (isBreakChar(comment.charAt(cc))) break;
                        else ++cc;
                    }
                }
                sb.append(comment.substring(offset, cc));
                offset = cc+1;
            }
            return sb.toString();
        } //-- getNextLine
        
        
        private boolean isBreakChar(char ch) {
            switch(ch) {
                case '\n':
                case '\r':
                case '\t':
                case  ' ':
                    return true;
                default:
                    break;
            }
            return false;
        }
    } //-- LineFormatter
    
} //-- JDocComment


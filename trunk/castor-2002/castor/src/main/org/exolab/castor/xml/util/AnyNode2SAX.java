package org.exolab.castor.xml.util;

import org.xml.sax.DocumentHandler;

import org.exolab.castor.xml.EventProducer;
import org.exolab.castor.types.AnyNode;
import org.exolab.castor.util.Stack;

import org.xml.sax.helpers.AttributeListImpl;
import org.xml.sax.SAXException;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class AnyNode2SAX implements EventProducer {

   /**
    * The AnyNode we are firing events for
    */
    private AnyNode _node;

   /**
    * The Document Handler
    */
    private DocumentHandler _handler;

   /**
    * The stack to store the elements
    */
    private Stack _elements;

    /**
     * The namespace declaration String
     */
    private static final String XMLNS_PREFIX  = "xmlns";

    public AnyNode2SAX() {
        _elements = new Stack();
    }
    /**
     * Creates a AnyNode2SAX for the given node
     */
    public AnyNode2SAX(AnyNode node) {
        this();
        _node = node;
    }

    /**
     * Set the Document Handler
     * @param handler the document handler to set
     */
    public void setDocumentHandler(DocumentHandler handler) {
        if (handler == null)
           throw new IllegalArgumentException("AnyNode2SAX#setDocumentHandler 'null' value for handler");
        _handler = handler;
    }

    public static void fireEvents(AnyNode node, DocumentHandler handler)
        throws SAXException
    {
        AnyNode2SAX eventProducer = new AnyNode2SAX(node);
        eventProducer.setDocumentHandler(handler);
        eventProducer.start();
    }
    public void start() throws org.xml.sax.SAXException {
        if ( (_node == null) || (_handler == null) )
           return;
        else processAnyNode(_node, _handler);
    }

    private void processAnyNode(AnyNode node, DocumentHandler handler)
         throws SAXException
    {

        if ( (node == null) || (handler == null) ) {
            throw new IllegalArgumentException();
        }

        //-- add object to stack so we don't potentially get into
        //-- an endlessloop
        if (_elements.search(node) >= 0) return;
        else _elements.push(node);

        if (node.getNodeType() == AnyNode.ELEMENT) {
            //the first sibling node of the current one
            AnyNode siblingNode = node.getNextSibling();

            String name = node.getLocalName();

            //-- retrieve the attributes and handle them
            AttributeListImpl atts = new AttributeListImpl();
            AnyNode tempNode = node.getFirstAttribute();
            String xmlName = null;
            String value = null;
            while (tempNode != null) {
                xmlName = tempNode.getLocalName();
                value = tempNode.getStringValue();
                atts.addAttribute(xmlName, null, value);
                tempNode = tempNode.getNextSibling();
            }//attributes



             //-- retrieve the namespaces declaration and handle them
             tempNode = node.getFirstNamespace();
             String prefix = null;
             while (tempNode != null) {
                 prefix = tempNode.getNamespacePrefix();
                 xmlName = XMLNS_PREFIX;
                 if ( (prefix != null) && (prefix.length() != 0) )
                     xmlName += ":"+prefix;
                  value = tempNode.getNamespaceURI();
                  atts.addAttribute(xmlName, null, value);
                  tempNode = tempNode.getNextSibling();
             }//namespaceNode

             //-- namespace management
             String nsPrefix = node.getNamespacePrefix();
             String nsURI = node.getNamespaceURI();

             String qName = null;
             if (nsPrefix != null) {
                 int len = nsPrefix.length();
                 if (len > 0) {
                      StringBuffer sb = new StringBuffer(len+name.length()+1);
                      sb.append(nsPrefix);
                      sb.append(':');
                      sb.append(name);
                      qName = sb.toString();
                 } else qName = name;
             } else qName = name;

             try {
                 handler.startElement(qName, atts);
             } catch (SAXException sx) {
                  throw new SAXException(sx);
             }

             //-- handle child&daughter elements
             tempNode = node.getFirstChild();
             while (tempNode != null) {
                 processAnyNode(tempNode, handler);
                 tempNode = tempNode.getFirstChild();
             }

             //-- finish element
             try {
               handler.endElement(qName);
             } catch(org.xml.sax.SAXException sx) {
                  throw new SAXException(sx);
             }
             if (siblingNode != null)
                  processAnyNode(siblingNode, handler);


       }//ELEMENTS
        else if (node.getNodeType() == AnyNode.TEXT) {
             AnyNode tempNode = node;
             while (tempNode != null) {
                 processTextNode(tempNode, handler);
                 tempNode =tempNode.getNextSibling();
             }
             tempNode = null;
        }

    }

    private void processTextNode(AnyNode node, DocumentHandler handler)
        throws SAXException
    {
        if (node.getNodeType() != AnyNode.TEXT) {
           String err = "This node can not be handle by processTextNode.";
            throw new IllegalArgumentException(err);
        }
        String value = node.getStringValue();
        if ( (value != null) && (value.length() >0) ) {
            char[] chars = value.toCharArray();
            try {
                handler.characters(chars, 0, chars.length);
            } catch(org.xml.sax.SAXException sx) {
                 throw new SAXException(sx);
            }
         }
    }//processTextNode
}